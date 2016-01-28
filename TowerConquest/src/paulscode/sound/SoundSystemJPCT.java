package paulscode.sound;

import com.threed.jpct.Camera;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import paulscode.sound.codecs.CodecJOgg;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryJOAL;
import paulscode.sound.libraries.LibraryJavaSound;

/**
 * The SoundSystemJPCT class is a user-friendly extention of the core SoundSystem class. It is designed to be easily used with the jPCT API (for more information, visit http://www.jpct.net). In
 * addition to the things which SoundSystem does, SoundSystemJPCT provides methods for binding sources to Object3D's and binding the listener to a Camera. A whole bunch of methods have been added to
 * make source creation much easier than using the core SoundSystem class alone, by automatically using default settings any time a parameter is not specified. All SimpleVector arguments are assumed
 * to be in the jPCT coordinate system, and are automatically converted into the SoundSystem coordinate system (y = -y, z = -z), so the user doesn't have to worry about keeping track of two different
 * coordinate systems. Pluggins for LibraryLWJGLOpenAL, LibraryJavaSound, CodecWav, and CodecJOgg are automatically linked, so the user doesn't have to worry about pluggins either. The SoundSystemJPCT
 * can be constructed by defining which sound library to use or by using the the built in compatibility checking. See {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information
 * about changing default settings.
 * <br><br>
 * <b><i> SoundSystem License:</b></i><br><b><br>
 * You are free to use this library for any purpose, commercial or otherwise. You may modify this library or source code, and distribute it any way you like, provided the following conditions are met:
 * <br>
 * 1) You may not falsely claim to be the author of this library or any unmodified portion of it.
 * <br>
 * 2) You may not copyright this library or a modified version of it and then sue me for copyright infringement.
 * <br>
 * 3) If you modify the source code, you must clearly document the changes made before redistributing the modified source code, so other users know it is not the original code.
 * <br>
 * 4) You are not required to give me credit for this library in any derived work, but if you do, you must also mention my website: http://www.paulscode.com
 * <br>
 * 5) I the author will not be responsible for any damages (physical, financial, or otherwise) caused by the use if this library or any part of it.
 * <br>
 * 6) I the author do not guarantee, warrant, or make any representations, either expressed or implied, regarding the use of this library or any part of it.
 * <br><br>
 * Author: Paul Lamb
 * <br>
 * http://www.paulscode.com
 * </b>
 */
public class SoundSystemJPCT extends SoundSystem {

    /**
     * Camera which the listener will follow.
     */
    private Camera boundCamera;

    /**
     * Map linking Sources with the Object3D's which they follow.
     */
    private HashMap<String, Object3D> boundObjects = null;

    /**
     * Constructor: Create the sound system. If library priorities have not been defined, SoundSystemJPCT attempts to load the LWJGL binding of OpenAL first, and if that fails it trys JavaSound. If
     * both fail, it switches to silent mode. See {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for information about linking with sound libraries and codecs.
     */
    public SoundSystemJPCT() {
        super();
    }

    /**
     * Constructor: Create the sound system using the specified library.
     *
     * @param libraryClass Type of library to use (must extend class 'Library'). See {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for information about chosing a sound library.
     */
    public SoundSystemJPCT(Class libraryClass) throws SoundSystemException {
        super(libraryClass);
    }

    /**
     * Links with the default libraries (LibraryLWJGLOpenAL and LibraryJavaSound) and default codecs (CodecWav and CodecJOgg).
     */
    @Override
    protected void linkDefaultLibrariesAndCodecs() {
        try {
            SoundSystemConfig.addLibrary(LibraryJOAL.class);
        } catch (SoundSystemException sse) {
            errorMessage("Problem adding library for OpenAL",
                    0);
            logger.printStackTrace(sse, 1);
        }
        try {
            SoundSystemConfig.addLibrary(LibraryJavaSound.class);
        } catch (SoundSystemException sse) {
            errorMessage("Problem adding library for JavaSound",
                    0);
            logger.printStackTrace(sse, 1);
        }
        try {
            SoundSystemConfig.setCodec("wav", CodecWav.class);
        } catch (SoundSystemException sse) {
            errorMessage("Problem adding codec for .wav files",
                    0);
            logger.printStackTrace(sse, 1);
        }
        try {
            SoundSystemConfig.setCodec("ogg", CodecJOgg.class);
        } catch (SoundSystemException sse) {
            errorMessage("Problem adding codec for .ogg files",
                    0);
            logger.printStackTrace(sse, 1);
        }
    }

    /**
     * Loads the message logger, initializes the specified sound library, and starts the command thread. Also instantiates the random number generator and the command queue, and sets className to
     * "SoundSystemJPCT".
     *
     * @param libraryClass Library to initialize. See {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for information about chosing a sound library.
     */
    @Override
    protected void init(Class libraryClass) throws SoundSystemException {
        className = "SoundSystemJPCT";

        super.init(libraryClass);
    }

    /**
     * Ends the command thread, shuts down the sound system, and removes references to all instantiated objects.
     */
    @Override
    public void cleanup() {
        super.cleanup();

        try {
            if (boundObjects != null) {
                boundObjects.clear();
            }
        } catch (Exception e) {
        }

        boundObjects = null;
        boundCamera = null;
    }

    /**
     * Re-aligns the listener to the camera and keeps sources following the Object3D's that they are bound to. This method should be called on the same thread that manipulates the Camera and
     * Object3D's, to avoid synchronization-related errors. A good place to call tick() would be within the main game loop.
     */
    public void tick() {
        // If listener is bound to a camera, match its position and orientation:
        if (boundCamera != null) {
            SimpleVector position = convertCoordinates(
                    boundCamera.getPosition());

            SimpleVector direction = convertCoordinates(
                    boundCamera.getDirection());

            SimpleVector up = convertCoordinates(boundCamera.getUpVector());

            ListenerData listener = soundLibrary.getListenerData();

            // Check if the listener has moved:
            if (listener.position.x != position.x
                    || listener.position.y != position.y
                    || listener.position.z != position.z) {
                setListenerPosition(position.x, position.y, position.z);
            }

            // Check if listener's orientation changed:
            if (listener.lookAt.x != direction.x
                    || listener.lookAt.y != direction.y
                    || listener.lookAt.z != direction.z
                    || listener.up.x != up.x
                    || listener.up.y != up.y
                    || listener.up.z != up.z) {
                setListenerOrientation(direction.x, direction.y, direction.z,
                        up.x, up.y, up.z);
            }
        }

        if (boundObjects != null && soundLibrary != null) {
            Set<String> keys = boundObjects.keySet();
            Iterator<String> iter = keys.iterator();
            String sourcename;
            Source source;
            Object3D object;
            SimpleVector center;

            // loop through and cleanup all the sources:
            while (iter.hasNext()) {
                sourcename = iter.next();
                source = soundLibrary.getSource(sourcename);
                if (source == null) {
                    iter.remove();
                } else {
                    object = boundObjects.get(sourcename);
                    if (object == null) {
                        iter.remove();
                    } else {
                        center = convertCoordinates(
                                object.getTransformedCenter());
                        synchronized (SoundSystemConfig.THREAD_SYNC) {
                            if (source.position.x != center.x
                                    || source.position.y != center.y
                                    || source.position.z != center.z) {
                                setPosition(sourcename, center.x, center.y,
                                        center.z);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Binds the listener to the specified camera, so that when the camera's position or orientation changes, the listener automatically follows it. NOTE: When using this method, it is necessary to
     * call the tick() method within a loop running on the same thread which changes the specified Camera.
     *
     * @param c Camera to follow.
     */
    public void bindListener(Camera c) {
        synchronized (SoundSystemConfig.THREAD_SYNC) {
            boundCamera = c;

            if (boundCamera != null) {
                SimpleVector position = convertCoordinates(
                        boundCamera.getPosition());

                SimpleVector direction = convertCoordinates(
                        boundCamera.getDirection());

                SimpleVector up = convertCoordinates(boundCamera.getUpVector());

                ListenerData listener = soundLibrary.getListenerData();

                // Check if the listener has moved:
                if (listener.position.x != position.x
                        || listener.position.y != position.y
                        || listener.position.z != position.z) {
                    setListenerPosition(position.x, position.y, position.z);
                }

                // Check if listener's orientation changed:
                if (listener.lookAt.x != direction.x
                        || listener.lookAt.y != direction.y
                        || listener.lookAt.z != direction.z
                        || listener.up.x != up.x
                        || listener.up.y != up.y
                        || listener.up.z != up.z) {
                    setListenerOrientation(direction.x, direction.y, direction.z,
                            up.x, up.y, up.z);
                }
            }
        }
    }

    /**
     * Binds a source to an Object3D so that when the Object3D's position changes, the source automatically follows it. NOTE: When using this method, it is necessary to call the tick() method within a
     * loop running on the same thread which changes the specified Object3D.
     *
     * @param sourcename The source's identifier.
     * @param obj The object to follow.
     */
    public void bindSource(String sourcename, Object3D obj) {
        synchronized (SoundSystemConfig.THREAD_SYNC) {
            if (boundObjects == null) {
                boundObjects = new HashMap<>();
            }

            if (boundObjects != null && sourcename != null && obj != null) {
                boundObjects.put(sourcename, obj);
            }
        }
    }

    /**
     * Releases the specified source from its bound Object3D.
     *
     * @param sourcename The source's identifier.
     */
    public void releaseSource(String sourcename) {
        synchronized (SoundSystemConfig.THREAD_SYNC) {
            boundObjects.remove(sourcename);
        }
    }

    /**
     * Releases all sources bound to the specified Object3D. This method should be called before deleting an Object3D with sources attached to it.
     *
     * @param obj Object3D to remove sources from.
     */
    public void releaseAllSources(Object3D obj) {
        synchronized (SoundSystemConfig.THREAD_SYNC) {
            Set<String> keys = boundObjects.keySet();
            Iterator<String> iter = keys.iterator();
            String sourcename;
            Object3D object;
            while (iter.hasNext()) {
                sourcename = iter.next();
                object = boundObjects.get(sourcename);
                if (object == null || object == obj) {
                    iter.remove();
                }
            }
        }
    }

    /**
     * Creates a new permanant, streaming, looping priority source with zero attenuation.
     *
     * @param filename The name of the sound file to play at this source.
     * @return The new source's name.
     */
    public String backgroundMusic(String filename) {
        // return a name for the looping background music:
        return backgroundMusic(filename, true);
    }

    /**
     * Creates a new permanant, streaming, priority source with zero attenuation.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should the music loop, or play only once.
     * @return The new source's name.
     */
    public String backgroundMusic(String filename, boolean toLoop) {
        //generate a random name for this source:
        String sourcename = "Background_"
                + randomNumberGenerator.nextInt()
                + "_" + randomNumberGenerator.nextInt();

        backgroundMusic(sourcename, filename, toLoop);
        // return a name for the background music:
        return sourcename;
    }

    /**
     * Creates a new permanant, streaming, looping priority source with zero attenuation.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     */
    public void backgroundMusic(String sourcename, String filename) {
        backgroundMusic(sourcename, filename, true);
    }

    /**
     * Creates a new permanant, streaming, looping priority source with zero attenuation.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @return The new source's name.
     */
    public String backgroundMusic(URL url, String identifier) {
        // return a name for the looping background music:
        return backgroundMusic(url, identifier, true);
    }

    /**
     * Creates a new permanant, streaming, priority source with zero attenuation.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should the music loop, or play only once.
     * @return The new source's name.
     */
    public String backgroundMusic(URL url, String identifier, boolean toLoop) {
        //generate a random name for this source:
        String sourcename = "Background_"
                + randomNumberGenerator.nextInt()
                + "_" + randomNumberGenerator.nextInt();

        backgroundMusic(sourcename, url, identifier, toLoop);
        // return a name for the background music:
        return sourcename;
    }

    /**
     * Creates a new permanant, streaming, looping priority source with zero attenuation.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     */
    public void backgroundMusic(String sourcename, URL url, String identifier) {
        backgroundMusic(sourcename, url, identifier, true);
    }

    /**
     * Creates a new non-streaming, non-priority source at the origin. Default values are used for attenuation.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     */
    public void newSource(String sourcename, String filename,
            boolean toLoop) {
        newSource(sourcename, filename, toLoop, new SimpleVector(0, 0, 0),
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a new non-streaming source at the origin. Default values are used for attenuation.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     */
    public void newSource(boolean priority, String sourcename, String filename,
            boolean toLoop) {
        newSource(priority, sourcename, filename, toLoop,
                new SimpleVector(0, 0, 0),
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a new non-streaming, non-priority source at the specified position. Default values are used for attenuation.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     */
    public void newSource(String sourcename, String filename,
            boolean toLoop, SimpleVector jPCTposition) {
        newSource(sourcename, filename, toLoop, jPCTposition,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a new non-streaming source at the specified position. Default values are used for attenuation.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     */
    public void newSource(boolean priority, String sourcename, String filename,
            boolean toLoop, SimpleVector jPCTposition) {
        newSource(priority, sourcename, filename, toLoop, jPCTposition,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a new non-streaming, non-priority source at the origin. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. See
     * {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information about Attenuation, fade distance, and rolloff factor.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     */
    public void newSource(String sourcename, String filename, boolean toLoop,
            int attmodel) {
        newSource(sourcename, filename, toLoop, new SimpleVector(0, 0, 0),
                attmodel);
    }

    /**
     * Creates a new non-streaming source at the origin. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. See
     * {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information about Attenuation, fade distance, and rolloff factor.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     */
    public void newSource(boolean priority, String sourcename, String filename,
            boolean toLoop, int attmodel) {
        newSource(priority, sourcename, filename, toLoop,
                new SimpleVector(0, 0, 0), attmodel);
    }

    /**
     * Creates a new non-streaming, non-priority source at the origin. See {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information about Attenuation, fade distance, and
     * rolloff factor.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @param distORroll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     */
    public void newSource(String sourcename, String filename, boolean toLoop,
            int attmodel, float distORroll) {
        newSource(sourcename, filename, toLoop, new SimpleVector(0, 0, 0),
                attmodel, distORroll);
    }

    /**
     * Creates a new non-streaming source at the origin. See {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information about Attenuation, fade distance, and rolloff factor.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @param distORroll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     */
    public void newSource(boolean priority, String sourcename, String filename,
            boolean toLoop, int attmodel, float distORroll) {
        newSource(priority, sourcename, filename, toLoop, 0, 0, 0, attmodel,
                distORroll);
    }

    /**
     * Creates a new non-streaming, non-priority source at the specified position. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. See
     * {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information about Attenuation, fade distance, and rolloff factor.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     */
    public void newSource(String sourcename, String filename, boolean toLoop,
            SimpleVector jPCTposition, int attmodel) {
        newSource(false, sourcename, filename, toLoop, jPCTposition,
                attmodel);
    }

    /**
     * Creates a new non-streaming source at the specified position. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. See
     * {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information about Attenuation, fade distance, and rolloff factor.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     */
    public void newSource(boolean priority, String sourcename, String filename,
            boolean toLoop, SimpleVector jPCTposition,
            int attmodel) {
        switch (attmodel) {
            case SoundSystemConfig.ATTENUATION_ROLLOFF:
                newSource(priority, sourcename, filename, toLoop, jPCTposition,
                        attmodel, SoundSystemConfig.getDefaultRolloff());
                break;
            case SoundSystemConfig.ATTENUATION_LINEAR:
                newSource(priority, sourcename, filename, toLoop, jPCTposition,
                        attmodel,
                        SoundSystemConfig.getDefaultFadeDistance());
                break;
            default:
                newSource(priority, sourcename, filename, toLoop, jPCTposition,
                        attmodel, 0);
                break;
        }
    }

    /**
     * Creates a new non-streaming, non-priority source at the specified position. See {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information about Attenuation, fade
     * distance, and rolloff factor.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     */
    public void newSource(String sourcename, String filename, boolean toLoop,
            SimpleVector jPCTposition, int attmodel,
            float distOrRoll) {
        newSource(false, sourcename, filename, toLoop, jPCTposition, attmodel,
                distOrRoll);
    }

    /**
     * Creates a new non-streaming source at the specified position. See {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information about Attenuation, fade distance, and rolloff
     * factor.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     */
    public void newSource(boolean priority, String sourcename, String filename,
            boolean toLoop, SimpleVector jPCTposition,
            int attmodel, float distOrRoll) {
        SimpleVector position = convertCoordinates(jPCTposition);

        CommandQueue(new CommandObject(CommandObject.NEW_SOURCE, priority,
                false, toLoop, sourcename,
                new FilenameURL(filename),
                position.x, position.y, position.z, attmodel,
                distOrRoll));
        commandThread.interrupt();
    }

    /**
     * Creates a new non-streaming, non-priority source at the origin. Default values are used for attenuation.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     */
    public void newSource(String sourcename, URL url, String identifier,
            boolean toLoop) {
        newSource(sourcename, url, identifier, toLoop, new SimpleVector(0, 0, 0),
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a new non-streaming source at the origin. Default values are used for attenuation.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     */
    public void newSource(boolean priority, String sourcename, URL url,
            String identifier, boolean toLoop) {
        newSource(priority, sourcename, url, identifier, toLoop,
                new SimpleVector(0, 0, 0),
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a new non-streaming, non-priority source at the specified position. Default values are used for attenuation.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     */
    public void newSource(String sourcename, URL url, String identifier,
            boolean toLoop, SimpleVector jPCTposition) {
        newSource(sourcename, url, identifier, toLoop, jPCTposition,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a new non-streaming source at the specified position. Default values are used for attenuation.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     */
    public void newSource(boolean priority, String sourcename, URL url,
            String identifier, boolean toLoop,
            SimpleVector jPCTposition) {
        newSource(priority, sourcename, url, identifier, toLoop, jPCTposition,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a new non-streaming, non-priority source at the origin. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. See
     * {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information about Attenuation, fade distance, and rolloff factor.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     */
    public void newSource(String sourcename, URL url, String identifier,
            boolean toLoop, int attmodel) {
        newSource(sourcename, url, identifier, toLoop, new SimpleVector(0, 0, 0),
                attmodel);
    }

    /**
     * Creates a new non-streaming source at the origin. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. See
     * {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information about Attenuation, fade distance, and rolloff factor.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     */
    public void newSource(boolean priority, String sourcename, URL url,
            String identifier, boolean toLoop, int attmodel) {
        newSource(priority, sourcename, url, identifier, toLoop,
                new SimpleVector(0, 0, 0), attmodel);
    }

    /**
     * Creates a new non-streaming, non-priority source at the origin. See {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information about Attenuation, fade distance, and
     * rolloff factor.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @param distORroll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     */
    public void newSource(String sourcename, URL url, String identifier,
            boolean toLoop, int attmodel, float distORroll) {
        newSource(sourcename, url, identifier, toLoop, new SimpleVector(0, 0, 0),
                attmodel, distORroll);
    }

    /**
     * Creates a new non-streaming source at the origin. See {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information about Attenuation, fade distance, and rolloff factor.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @param distORroll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     */
    public void newSource(boolean priority, String sourcename, URL url,
            String identifier, boolean toLoop, int attmodel,
            float distORroll) {
        newSource(priority, sourcename, url, identifier, toLoop, 0, 0, 0, attmodel,
                distORroll);
    }

    /**
     * Creates a new non-streaming, non-priority source at the specified position. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. See
     * {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information about Attenuation, fade distance, and rolloff factor.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     */
    public void newSource(String sourcename, URL url, String identifier,
            boolean toLoop, SimpleVector jPCTposition,
            int attmodel) {
        newSource(false, sourcename, url, identifier, toLoop, jPCTposition,
                attmodel);
    }

    /**
     * Creates a new non-streaming source at the specified position. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. See
     * {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information about Attenuation, fade distance, and rolloff factor.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     */
    public void newSource(boolean priority, String sourcename, URL url,
            String identifier, boolean toLoop,
            SimpleVector jPCTposition, int attmodel) {
        switch (attmodel) {
            case SoundSystemConfig.ATTENUATION_ROLLOFF:
                newSource(priority, sourcename, url, identifier, toLoop, jPCTposition,
                        attmodel, SoundSystemConfig.getDefaultRolloff());
                break;
            case SoundSystemConfig.ATTENUATION_LINEAR:
                newSource(priority, sourcename, url, identifier, toLoop, jPCTposition,
                        attmodel,
                        SoundSystemConfig.getDefaultFadeDistance());
                break;
            default:
                newSource(priority, sourcename, url, identifier, toLoop, jPCTposition,
                        attmodel, 0);
                break;
        }
    }

    /**
     * Creates a new non-streaming, non-priority source at the specified position. See {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information about Attenuation, fade
     * distance, and rolloff factor.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     */
    public void newSource(String sourcename, URL url, String identifier,
            boolean toLoop, SimpleVector jPCTposition,
            int attmodel, float distOrRoll) {
        newSource(false, sourcename, url, identifier, toLoop, jPCTposition, attmodel,
                distOrRoll);
    }

    /**
     * Creates a new non-streaming source at the specified position. See {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information about Attenuation, fade distance, and rolloff
     * factor.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     */
    public void newSource(boolean priority, String sourcename, URL url,
            String identifier, boolean toLoop,
            SimpleVector jPCTposition, int attmodel,
            float distOrRoll) {
        SimpleVector position = convertCoordinates(jPCTposition);

        CommandQueue(new CommandObject(CommandObject.NEW_SOURCE, priority,
                false, toLoop, sourcename,
                new FilenameURL(url, identifier),
                position.x, position.y, position.z, attmodel,
                distOrRoll));
        commandThread.interrupt();
    }

    /**
     * Creates a new streaming non-priority source at the origin. Default values are used for attenuation.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     */
    public void newStreamingSource(String sourcename, String filename,
            boolean toLoop) {
        newStreamingSource(sourcename, filename, toLoop,
                new SimpleVector(0, 0, 0),
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a new streaming source. Default values are used for attenuation.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     */
    public void newStreamingSource(boolean priority, String sourcename,
            String filename, boolean toLoop) {
        newStreamingSource(priority, sourcename, filename, toLoop,
                new SimpleVector(0, 0, 0),
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a new streaming non-priority source at the specified position. Default values are used for attenuation.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     */
    public void newStreamingSource(String sourcename, String filename,
            boolean toLoop, SimpleVector jPCTposition) {
        newStreamingSource(sourcename, filename, toLoop, jPCTposition,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a new streaming source at the specified position. Default values are used for attenuation.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     */
    public void newStreamingSource(boolean priority, String sourcename,
            String filename, boolean toLoop,
            SimpleVector jPCTposition) {
        newStreamingSource(priority, sourcename, filename, toLoop,
                jPCTposition,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a new streaming non-priority source at the origin. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     */
    public void newStreamingSource(String sourcename, String filename,
            boolean toLoop, int attmodel) {
        newStreamingSource(sourcename, filename, toLoop,
                new SimpleVector(0, 0, 0), attmodel);
    }

    /**
     * Creates a new streaming source at the origin. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     */
    public void newStreamingSource(boolean priority, String sourcename,
            String filename, boolean toLoop,
            int attmodel) {
        newStreamingSource(priority, sourcename, filename, toLoop,
                new SimpleVector(0, 0, 0), attmodel);
    }

    /**
     * Creates a new streaming non-priority source at the origin.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @param distORroll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     */
    public void newStreamingSource(String sourcename, String filename,
            boolean toLoop, int attmodel,
            float distORroll) {
        newStreamingSource(sourcename, filename, toLoop,
                new SimpleVector(0, 0, 0), attmodel, distORroll);
    }

    /**
     * Creates a new streaming source at the origin.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @param distORroll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     */
    public void newStreamingSource(boolean priority, String sourcename,
            String filename, boolean toLoop,
            int attmodel, float distORroll) {
        newStreamingSource(priority, sourcename, filename, toLoop, 0, 0, 0,
                attmodel, distORroll);
    }

    /**
     * Creates a new streaming non-priority source at the specified position. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     */
    public void newStreamingSource(String sourcename, String filename,
            boolean toLoop, SimpleVector jPCTposition,
            int attmodel) {
        newStreamingSource(false, sourcename, filename, toLoop, jPCTposition,
                attmodel);
    }

    /**
     * Creates a new streaming source at the specified position. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     */
    public void newStreamingSource(boolean priority, String sourcename,
            String filename, boolean toLoop,
            SimpleVector jPCTposition, int attmodel) {
        switch (attmodel) {
            case SoundSystemConfig.ATTENUATION_ROLLOFF:
                newStreamingSource(priority, sourcename, filename, toLoop,
                        jPCTposition, attmodel,
                        SoundSystemConfig.getDefaultRolloff());
                break;
            case SoundSystemConfig.ATTENUATION_LINEAR:
                newStreamingSource(priority, sourcename, filename, toLoop,
                        jPCTposition, attmodel,
                        SoundSystemConfig.getDefaultFadeDistance());
                break;
            default:
                newStreamingSource(priority, sourcename, filename, toLoop,
                        jPCTposition, attmodel, 0);
                break;
        }
    }

    /**
     * Creates a new streaming non-priority source at the specified position.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     */
    public void newStreamingSource(String sourcename, String filename,
            boolean toLoop, SimpleVector jPCTposition,
            int attmodel, float distOrRoll) {
        newStreamingSource(false, sourcename, filename, toLoop, jPCTposition,
                attmodel, distOrRoll);
    }

    /**
     * Creates a new streaming source at the specified position.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     */
    public void newStreamingSource(boolean priority, String sourcename,
            String filename, boolean toLoop,
            SimpleVector jPCTposition, int attmodel,
            float distOrRoll) {
        SimpleVector position = convertCoordinates(jPCTposition);

        CommandQueue(new CommandObject(CommandObject.NEW_SOURCE, priority,
                true, toLoop, sourcename,
                new FilenameURL(filename), position.x,
                position.y, position.z, attmodel, distOrRoll));
        commandThread.interrupt();
    }

    /**
     * Creates a new streaming non-priority source at the origin. Default values are used for attenuation.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     */
    public void newStreamingSource(String sourcename, URL url,
            String identifier, boolean toLoop) {
        newStreamingSource(sourcename, url, identifier, toLoop,
                new SimpleVector(0, 0, 0),
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a new streaming source. Default values are used for attenuation.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     */
    public void newStreamingSource(boolean priority, String sourcename,
            URL url, String identifier, boolean toLoop) {
        newStreamingSource(priority, sourcename, url, identifier, toLoop,
                new SimpleVector(0, 0, 0),
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a new streaming non-priority source at the specified position. Default values are used for attenuation.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     */
    public void newStreamingSource(String sourcename, URL url,
            String identifier, boolean toLoop,
            SimpleVector jPCTposition) {
        newStreamingSource(sourcename, url, identifier, toLoop, jPCTposition,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a new streaming source at the specified position. Default values are used for attenuation.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     */
    public void newStreamingSource(boolean priority, String sourcename,
            URL url, String identifier, boolean toLoop,
            SimpleVector jPCTposition) {
        newStreamingSource(priority, sourcename, url, identifier, toLoop,
                jPCTposition,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a new streaming non-priority source at the origin. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     */
    public void newStreamingSource(String sourcename, URL url,
            String identifier, boolean toLoop,
            int attmodel) {
        newStreamingSource(sourcename, url, identifier, toLoop,
                new SimpleVector(0, 0, 0), attmodel);
    }

    /**
     * Creates a new streaming source at the origin. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     */
    public void newStreamingSource(boolean priority, String sourcename,
            URL url, String identifier, boolean toLoop,
            int attmodel) {
        newStreamingSource(priority, sourcename, url, identifier, toLoop,
                new SimpleVector(0, 0, 0), attmodel);
    }

    /**
     * Creates a new streaming non-priority source at the origin.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @param distORroll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     */
    public void newStreamingSource(String sourcename, URL url,
            String identifier, boolean toLoop,
            int attmodel, float distORroll) {
        newStreamingSource(sourcename, url, identifier, toLoop,
                new SimpleVector(0, 0, 0), attmodel, distORroll);
    }

    /**
     * Creates a new streaming source at the origin.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @param distORroll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     */
    public void newStreamingSource(boolean priority, String sourcename,
            URL url, String identifier, boolean toLoop,
            int attmodel, float distORroll) {
        newStreamingSource(priority, sourcename, url, identifier, toLoop,
                0, 0, 0, attmodel, distORroll);
    }

    /**
     * Creates a new streaming non-priority source at the specified position. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     */
    public void newStreamingSource(String sourcename, URL url,
            String identifier, boolean toLoop,
            SimpleVector jPCTposition, int attmodel) {
        newStreamingSource(false, sourcename, url, identifier, toLoop,
                jPCTposition, attmodel);
    }

    /**
     * Creates a new streaming source at the specified position. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     */
    public void newStreamingSource(boolean priority, String sourcename,
            URL url, String identifier, boolean toLoop,
            SimpleVector jPCTposition, int attmodel) {
        switch (attmodel) {
            case SoundSystemConfig.ATTENUATION_ROLLOFF:
                newStreamingSource(priority, sourcename, url, identifier,
                        toLoop, jPCTposition, attmodel,
                        SoundSystemConfig.getDefaultRolloff());
                break;
            case SoundSystemConfig.ATTENUATION_LINEAR:
                newStreamingSource(priority, sourcename, url, identifier,
                        toLoop, jPCTposition, attmodel,
                        SoundSystemConfig.getDefaultFadeDistance());
                break;
            default:
                newStreamingSource(priority, sourcename, url, identifier,
                        toLoop, jPCTposition, attmodel, 0);
                break;
        }
    }

    /**
     * Creates a new streaming non-priority source at the specified position.
     *
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     */
    public void newStreamingSource(String sourcename, URL url,
            String identifier, boolean toLoop,
            SimpleVector jPCTposition, int attmodel,
            float distOrRoll) {
        newStreamingSource(false, sourcename, url, identifier, toLoop,
                jPCTposition, attmodel, distOrRoll);
    }

    /**
     * Creates a new streaming source at the specified position.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param sourcename A unique identifier for this source. Two sources may not use the same sourcename.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     */
    public void newStreamingSource(boolean priority, String sourcename,
            URL url, String identifier, boolean toLoop,
            SimpleVector jPCTposition, int attmodel,
            float distOrRoll) {
        SimpleVector position = convertCoordinates(jPCTposition);

        CommandQueue(new CommandObject(CommandObject.NEW_SOURCE, priority,
                true, toLoop, sourcename,
                new FilenameURL(url, identifier), position.x,
                position.y, position.z, attmodel, distOrRoll));
        commandThread.interrupt();
    }

    /**
     * Creates a temporary, non-priority source at the origin and plays it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly generated name
     * for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @return The new sorce's name.
     */
    public String quickPlay(String filename, boolean toLoop) {
        return quickPlay(filename, toLoop, new SimpleVector(0, 0, 0),
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary source at the origin and plays it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly generated name for the new
     * source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @return The new sorce's name.
     */
    public String quickPlay(boolean priority, String filename, boolean toLoop) {
        return quickPlay(priority, filename, toLoop,
                new SimpleVector(0, 0, 0),
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary non-priority source at the specified position and plays it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly
     * generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @return The new sorce's name.
     */
    public String quickPlay(String filename, boolean toLoop,
            SimpleVector jPCTposition) {
        return quickPlay(filename, toLoop, jPCTposition,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary source and plays it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE:
     * to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @return The new sorce's name.
     */
    public String quickPlay(boolean priority, String filename, boolean toLoop,
            SimpleVector jPCTposition) {
        return quickPlay(priority, filename, toLoop, jPCTposition,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary non-priority source at the origin and plays it. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. After the
     * source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the
     * return value for sourcename.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickPlay(String filename, boolean toLoop, int attmodel) {
        return quickPlay(filename, toLoop, new SimpleVector(0, 0, 0),
                attmodel);
    }

    /**
     * Creates a temporary source and plays it. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. After the source finishes playing, it
     * is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickPlay(boolean priority, String filename, boolean toLoop,
            int attmodel) {
        return quickPlay(priority, filename, toLoop, new SimpleVector(0, 0, 0),
                attmodel);
    }

    /**
     * Creates a temporary non-priority source at the origin and plays it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a
     * source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @param distORroll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickPlay(String filename, boolean toLoop, int attmodel,
            float distORroll) {
        return quickPlay(filename, toLoop, new SimpleVector(0, 0, 0), attmodel,
                distORroll);
    }

    /**
     * Creates a temporary source at the origin and plays it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by
     * this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @param distORroll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickPlay(boolean priority, String filename, boolean toLoop,
            int attmodel, float distORroll) {
        return quickPlay(priority, filename, toLoop, 0, 0, 0, attmodel,
                distORroll);
    }

    /**
     * Creates a temporary non-priority source and plays it. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. After the source finishes
     * playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for
     * sourcename.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickPlay(String filename, boolean toLoop,
            SimpleVector jPCTposition, int attmodel) {
        return quickPlay(false, filename, toLoop, jPCTposition, attmodel);
    }

    /**
     * Creates a temporary source and plays it. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. After the source finishes playing, it
     * is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickPlay(boolean priority, String filename, boolean toLoop,
            SimpleVector jPCTposition, int attmodel) {
        switch (attmodel) {
            case SoundSystemConfig.ATTENUATION_ROLLOFF:
                return quickPlay(priority, filename, toLoop, jPCTposition,
                        attmodel,
                        SoundSystemConfig.getDefaultRolloff());
            case SoundSystemConfig.ATTENUATION_LINEAR:
                return quickPlay(priority, filename, toLoop, jPCTposition,
                        attmodel,
                        SoundSystemConfig.getDefaultFadeDistance());
            default:
                return quickPlay(priority, filename, toLoop, jPCTposition,
                        attmodel, 0);
        }
    }

    /**
     * Creates a temporary non-priority source and plays it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by
     * this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickPlay(String filename, boolean toLoop,
            SimpleVector jPCTposition, int attmodel,
            float distOrRoll) {
        return quickPlay(false, filename, toLoop, jPCTposition, attmodel,
                distOrRoll);
    }

    /**
     * Creates a temporary source and plays it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method
     * permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickPlay(boolean priority, String filename, boolean toLoop,
            SimpleVector jPCTposition, int attmodel,
            float distOrRoll) {
        //generate a random name for this source:
        String sourcename = "Source_"
                + randomNumberGenerator.nextInt()
                + "_" + randomNumberGenerator.nextInt();

        SimpleVector position = convertCoordinates(jPCTposition);

        // Queue a command to quick play this new source:
        CommandQueue(new CommandObject(CommandObject.QUICK_PLAY, priority,
                false, toLoop, sourcename,
                new FilenameURL(filename), position.x,
                position.y, position.z, attmodel, distOrRoll,
                true));
        CommandQueue(new CommandObject(CommandObject.PLAY, sourcename));
        // Wake the command thread to process commands:
        commandThread.interrupt();

        // return the new source name.
        return sourcename;
    }

    /**
     * Creates a temporary non-priority source, binds it to the specified Object3D, and plays. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a
     * randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @return The new sorce's name.
     */
    public String quickPlay(String filename, boolean toLoop, Object3D object) {
        return quickPlay(filename, toLoop, object,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary source, binds it to the specified Object3D, and plays. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly
     * generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @return The new sorce's name.
     */
    public String quickPlay(boolean priority, String filename, boolean toLoop,
            Object3D object) {
        return quickPlay(priority, filename, toLoop, object,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary non-priority source, binds it to the specified Object3D, and plays. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter
     * 'attmodel'. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive()
     * method using the return value for sourcename.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickPlay(String filename, boolean toLoop, Object3D object,
            int attmodel) {
        return quickPlay(false, filename, toLoop, object, attmodel);
    }

    /**
     * Creates a temporary source, binds it to the specified Object3D, and plays. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'.
     * After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method
     * using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickPlay(boolean priority, String filename, boolean toLoop,
            Object3D object, int attmodel) {
        switch (attmodel) {
            case SoundSystemConfig.ATTENUATION_ROLLOFF:
                return quickPlay(priority, filename, toLoop, object, attmodel,
                        SoundSystemConfig.getDefaultRolloff());
            case SoundSystemConfig.ATTENUATION_LINEAR:
                return quickPlay(priority, filename, toLoop, object, attmodel,
                        SoundSystemConfig.getDefaultFadeDistance());
            default:
                return quickPlay(priority, filename, toLoop, object, attmodel,
                        0);
        }
    }

    /**
     * Creates a temporary non-priority source, binds it to the specified Object3D, and plays. After the source finishes playing, it is removed. Returns a randomly generated name for the new source.
     * NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickPlay(String filename, boolean toLoop, Object3D object,
            int attmodel, float distOrRoll) {
        return quickPlay(false, filename, toLoop, object, attmodel,
                distOrRoll);
    }

    /**
     * Creates a temporary source, binds it to the specified Object3D, and plays it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to
     * make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickPlay(boolean priority, String filename, boolean toLoop,
            Object3D object, int attmodel, float distOrRoll) {
        //generate a random name for this source:
        String sourcename = "Source_"
                + randomNumberGenerator.nextInt()
                + "_" + randomNumberGenerator.nextInt();

        SimpleVector jPCTposition;
        if (object != null) {
            jPCTposition = object.getTransformedCenter();
        } else {
            jPCTposition = new SimpleVector(0, 0, 0);
        }

        SimpleVector position = convertCoordinates(jPCTposition);

        // Queue a command to quick play this new source:
        CommandQueue(new CommandObject(CommandObject.QUICK_PLAY, priority,
                false, toLoop, sourcename,
                new FilenameURL(filename), position.x,
                position.y, position.z, attmodel, distOrRoll,
                true));
        CommandQueue(new CommandObject(CommandObject.PLAY, sourcename));
        // Wake the command thread to process commands:
        commandThread.interrupt();

        // Create the boundObjects map if it doesn't exist:
        if (boundObjects == null) {
            boundObjects = new HashMap<>();
        }

        // Add the sourcename and Object3D to the map:
        if (boundObjects != null && sourcename != null && object != null) {
            boundObjects.put(sourcename, object);
        }

        // return the new source name.
        return sourcename;
    }

    /**
     * Creates a temporary, non-priority source at the origin and plays it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly generated name
     * for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @return The new sorce's name.
     */
    public String quickPlay(URL url, String identifier, boolean toLoop) {
        return quickPlay(url, identifier, toLoop, new SimpleVector(0, 0, 0),
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary source at the origin and plays it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly generated name for the new
     * source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @return The new sorce's name.
     */
    public String quickPlay(boolean priority, URL url, String identifier,
            boolean toLoop) {
        return quickPlay(priority, url, identifier, toLoop,
                new SimpleVector(0, 0, 0),
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary non-priority source at the specified position and plays it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly
     * generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @return The new sorce's name.
     */
    public String quickPlay(URL url, String identifier, boolean toLoop,
            SimpleVector jPCTposition) {
        return quickPlay(url, identifier, toLoop, jPCTposition,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary source and plays it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE:
     * to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @return The new sorce's name.
     */
    public String quickPlay(boolean priority, URL url, String identifier,
            boolean toLoop, SimpleVector jPCTposition) {
        return quickPlay(priority, url, identifier, toLoop, jPCTposition,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary non-priority source at the origin and plays it. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. After the
     * source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the
     * return value for sourcename.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickPlay(URL url, String identifier, boolean toLoop,
            int attmodel) {
        return quickPlay(url, identifier, toLoop, new SimpleVector(0, 0, 0),
                attmodel);
    }

    /**
     * Creates a temporary source and plays it. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. After the source finishes playing, it
     * is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickPlay(boolean priority, URL url, String identifier,
            boolean toLoop, int attmodel) {
        return quickPlay(priority, url, identifier, toLoop,
                new SimpleVector(0, 0, 0), attmodel);
    }

    /**
     * Creates a temporary non-priority source at the origin and plays it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a
     * source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @param distORroll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickPlay(URL url, String identifier, boolean toLoop,
            int attmodel, float distORroll) {
        return quickPlay(url, identifier, toLoop, new SimpleVector(0, 0, 0),
                attmodel, distORroll);
    }

    /**
     * Creates a temporary source at the origin and plays it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by
     * this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @param distORroll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickPlay(boolean priority, URL url, String identifier,
            boolean toLoop, int attmodel, float distORroll) {
        return quickPlay(priority, url, identifier, toLoop, 0, 0, 0, attmodel,
                distORroll);
    }

    /**
     * Creates a temporary non-priority source and plays it. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. After the source finishes
     * playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for
     * sourcename.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickPlay(URL url, String identifier, boolean toLoop,
            SimpleVector jPCTposition, int attmodel) {
        return quickPlay(false, url, identifier, toLoop, jPCTposition,
                attmodel);
    }

    /**
     * Creates a temporary source and plays it. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. After the source finishes playing, it
     * is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickPlay(boolean priority, URL url, String identifier,
            boolean toLoop, SimpleVector jPCTposition,
            int attmodel) {
        switch (attmodel) {
            case SoundSystemConfig.ATTENUATION_ROLLOFF:
                return quickPlay(priority, url, identifier, toLoop,
                        jPCTposition, attmodel,
                        SoundSystemConfig.getDefaultRolloff());
            case SoundSystemConfig.ATTENUATION_LINEAR:
                return quickPlay(priority, url, identifier, toLoop,
                        jPCTposition, attmodel,
                        SoundSystemConfig.getDefaultFadeDistance());
            default:
                return quickPlay(priority, url, identifier, toLoop,
                        jPCTposition, attmodel, 0);
        }
    }

    /**
     * Creates a temporary non-priority source and plays it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by
     * this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickPlay(URL url, String identifier, boolean toLoop,
            SimpleVector jPCTposition, int attmodel,
            float distOrRoll) {
        return quickPlay(false, url, identifier, toLoop, jPCTposition,
                attmodel, distOrRoll);
    }

    /**
     * Creates a temporary source and plays it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method
     * permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickPlay(boolean priority, URL url, String identifier,
            boolean toLoop, SimpleVector jPCTposition,
            int attmodel, float distOrRoll) {
        //generate a random name for this source:
        String sourcename = "Source_"
                + randomNumberGenerator.nextInt()
                + "_" + randomNumberGenerator.nextInt();

        SimpleVector position = convertCoordinates(jPCTposition);

        // Queue a command to quick play this new source:
        CommandQueue(new CommandObject(CommandObject.QUICK_PLAY, priority,
                false, toLoop, sourcename,
                new FilenameURL(url, identifier), position.x,
                position.y, position.z, attmodel, distOrRoll,
                true));
        CommandQueue(new CommandObject(CommandObject.PLAY, sourcename));
        // Wake the command thread to process commands:
        commandThread.interrupt();

        // return the new source name.
        return sourcename;
    }

    /**
     * Creates a temporary non-priority source, binds it to the specified Object3D, and plays. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a
     * randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @return The new sorce's name.
     */
    public String quickPlay(URL url, String identifier, boolean toLoop,
            Object3D object) {
        return quickPlay(url, identifier, toLoop, object,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary source, binds it to the specified Object3D, and plays. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly
     * generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @return The new sorce's name.
     */
    public String quickPlay(boolean priority, URL url, String identifier,
            boolean toLoop, Object3D object) {
        return quickPlay(priority, url, identifier, toLoop, object,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary non-priority source, binds it to the specified Object3D, and plays. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter
     * 'attmodel'. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive()
     * method using the return value for sourcename.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickPlay(URL url, String identifier, boolean toLoop,
            Object3D object, int attmodel) {
        return quickPlay(false, url, identifier, toLoop, object, attmodel);
    }

    /**
     * Creates a temporary source, binds it to the specified Object3D, and plays. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'.
     * After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method
     * using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickPlay(boolean priority, URL url, String identifier,
            boolean toLoop, Object3D object, int attmodel) {
        switch (attmodel) {
            case SoundSystemConfig.ATTENUATION_ROLLOFF:
                return quickPlay(priority, url, identifier, toLoop, object,
                        attmodel,
                        SoundSystemConfig.getDefaultRolloff());
            case SoundSystemConfig.ATTENUATION_LINEAR:
                return quickPlay(priority, url, identifier, toLoop, object,
                        attmodel,
                        SoundSystemConfig.getDefaultFadeDistance());
            default:
                return quickPlay(priority, url, identifier, toLoop, object,
                        attmodel, 0);
        }
    }

    /**
     * Creates a temporary non-priority source, binds it to the specified Object3D, and plays. After the source finishes playing, it is removed. Returns a randomly generated name for the new source.
     * NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickPlay(URL url, String identifier, boolean toLoop,
            Object3D object, int attmodel, float distOrRoll) {
        return quickPlay(false, url, identifier, toLoop, object, attmodel,
                distOrRoll);
    }

    /**
     * Creates a temporary source, binds it to the specified Object3D, and plays it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to
     * make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickPlay(boolean priority, URL url, String identifier,
            boolean toLoop, Object3D object, int attmodel,
            float distOrRoll) {
        //generate a random name for this source:
        String sourcename = "Source_"
                + randomNumberGenerator.nextInt()
                + "_" + randomNumberGenerator.nextInt();

        SimpleVector jPCTposition;
        if (object != null) {
            jPCTposition = object.getTransformedCenter();
        } else {
            jPCTposition = new SimpleVector(0, 0, 0);
        }

        SimpleVector position = convertCoordinates(jPCTposition);

        // Queue a command to quick play this new source:
        CommandQueue(new CommandObject(CommandObject.QUICK_PLAY, priority,
                false, toLoop, sourcename,
                new FilenameURL(url, identifier), position.x,
                position.y, position.z, attmodel, distOrRoll,
                true));
        CommandQueue(new CommandObject(CommandObject.PLAY, sourcename));
        // Wake the command thread to process commands:
        commandThread.interrupt();

        // Create the boundObjects map if it doesn't exist:
        if (boundObjects == null) {
            boundObjects = new HashMap<>();
        }

        // Add the sourcename and Object3D to the map:
        if (boundObjects != null && sourcename != null && object != null) {
            boundObjects.put(sourcename, object);
        }

        // return the new source name.
        return sourcename;
    }

    /**
     * Creates a temporary, non-priority source at the origin and streams it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly generated
     * name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @return The new sorce's name.
     */
    public String quickStream(String filename, boolean toLoop) {
        return quickStream(filename, toLoop, new SimpleVector(0, 0, 0),
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary source at the origin and streams it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly generated name for the new
     * source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @return The new sorce's name.
     */
    public String quickStream(boolean priority, String filename,
            boolean toLoop) {
        return quickStream(priority, filename, toLoop,
                new SimpleVector(0, 0, 0),
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary non-priority source and streams it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly generated name for the new
     * source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @return The new sorce's name.
     */
    public String quickStream(String filename, boolean toLoop,
            SimpleVector jPCTposition) {
        return quickStream(filename, toLoop, jPCTposition,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary source and streams it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE:
     * to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @return The new sorce's name.
     */
    public String quickStream(boolean priority, String filename,
            boolean toLoop, SimpleVector jPCTposition) {
        return quickStream(priority, filename, toLoop, jPCTposition,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary non-priority source at the origin and streams it. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. After the
     * source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the
     * return value for sourcename.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickStream(String filename, boolean toLoop, int attmodel) {
        return quickStream(filename, toLoop, new SimpleVector(0, 0, 0),
                attmodel);
    }

    /**
     * Creates a temporary source and streams it. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. After the source finishes playing,
     * it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickStream(boolean priority, String filename,
            boolean toLoop, int attmodel) {
        return quickStream(priority, filename, toLoop,
                new SimpleVector(0, 0, 0), attmodel);
    }

    /**
     * Creates a temporary non-priority source at the origin and streams it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a
     * source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @param distORroll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickStream(String filename, boolean toLoop, int attmodel,
            float distORroll) {
        return quickStream(filename, toLoop, new SimpleVector(0, 0, 0),
                attmodel, distORroll);
    }

    /**
     * Creates a temporary source at the origin and streams it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created
     * by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @param distORroll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickStream(boolean priority, String filename,
            boolean toLoop, int attmodel, float distORroll) {
        return quickStream(priority, filename, toLoop, 0, 0, 0, attmodel,
                distORroll);
    }

    /**
     * Creates a temporary non-priority source and streams it. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. After the source
     * finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return
     * value for sourcename.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickStream(String filename, boolean toLoop,
            SimpleVector jPCTposition, int attmodel) {
        return quickStream(false, filename, toLoop, jPCTposition, attmodel);
    }

    /**
     * Creates a temporary source and streams it. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. After the source finishes playing,
     * it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickStream(boolean priority, String filename,
            boolean toLoop, SimpleVector jPCTposition,
            int attmodel) {
        switch (attmodel) {
            case SoundSystemConfig.ATTENUATION_ROLLOFF:
                return quickStream(priority, filename, toLoop, jPCTposition,
                        attmodel,
                        SoundSystemConfig.getDefaultRolloff());
            case SoundSystemConfig.ATTENUATION_LINEAR:
                return quickStream(priority, filename, toLoop, jPCTposition,
                        attmodel,
                        SoundSystemConfig.getDefaultFadeDistance());
            default:
                return quickStream(priority, filename, toLoop, jPCTposition,
                        attmodel, 0);
        }
    }

    /**
     * Creates a temporary non-priority source and streams it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by
     * this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickStream(String filename, boolean toLoop,
            SimpleVector jPCTposition, int attmodel,
            float distOrRoll) {
        return quickStream(false, filename, toLoop, jPCTposition, attmodel,
                distOrRoll);
    }

    /**
     * Creates a temporary source and streams it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method
     * permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickStream(boolean priority, String filename,
            boolean toLoop, SimpleVector jPCTposition,
            int attmodel, float distOrRoll) {
        //generate a random name for this source:
        String sourcename = "Source_"
                + randomNumberGenerator.nextInt()
                + "_" + randomNumberGenerator.nextInt();

        SimpleVector position = convertCoordinates(jPCTposition);

        // Queue a command to quick stream this new source:
        CommandQueue(new CommandObject(CommandObject.QUICK_PLAY, priority,
                true, toLoop, sourcename,
                new FilenameURL(filename), position.x,
                position.y, position.z, attmodel, distOrRoll,
                true));
        CommandQueue(new CommandObject(CommandObject.PLAY, sourcename));
        // Wake the command thread to process commands:
        commandThread.interrupt();

        // return the new source name.
        return sourcename;
    }

    /**
     * Creates a temporary non-priority source, binds it to the specified Object3D, and streams it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a
     * randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @return The new sorce's name.
     */
    public String quickStream(String filename, boolean toLoop,
            Object3D object) {
        return quickStream(filename, toLoop, object,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary source, binds it to the specified Object3D, and streams it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly
     * generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @return The new sorce's name.
     */
    public String quickStream(boolean priority, String filename,
            boolean toLoop, Object3D object) {
        return quickStream(priority, filename, toLoop, object,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary non-priority source, binds it to the specified Object3D, and streams it. Default value is used for either fade-distance or rolloff factor, depending on the value of
     * parameter 'attmodel'. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the
     * setActive() method using the return value for sourcename.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickStream(String filename, boolean toLoop, Object3D object,
            int attmodel) {
        return quickStream(false, filename, toLoop, object, attmodel);
    }

    /**
     * Creates a temporary source, binds it to the specified Object3D, and streams it. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'.
     * After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method
     * using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickStream(boolean priority, String filename,
            boolean toLoop, Object3D object, int attmodel) {
        switch (attmodel) {
            case SoundSystemConfig.ATTENUATION_ROLLOFF:
                return quickStream(priority, filename, toLoop, object,
                        attmodel,
                        SoundSystemConfig.getDefaultRolloff());
            case SoundSystemConfig.ATTENUATION_LINEAR:
                return quickStream(priority, filename, toLoop, object,
                        attmodel,
                        SoundSystemConfig.getDefaultFadeDistance());
            default:
                return quickStream(priority, filename, toLoop, object,
                        attmodel, 0);
        }
    }

    /**
     * Creates a temporary non-priority source, binds it to the specified Object3D, and streams it. After the source finishes playing, it is removed. Returns a randomly generated name for the new
     * source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickStream(String filename, boolean toLoop, Object3D object,
            int attmodel, float distOrRoll) {
        return quickStream(false, filename, toLoop, object, attmodel,
                distOrRoll);
    }

    /**
     * Creates a temporary source, binds it to the specified Object3D, and streams it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to
     * make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param filename The name of the sound file to play at this source.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickStream(boolean priority, String filename,
            boolean toLoop, Object3D object,
            int attmodel, float distOrRoll) {
        //generate a random name for this source:
        String sourcename = "Source_"
                + randomNumberGenerator.nextInt()
                + "_" + randomNumberGenerator.nextInt();

        SimpleVector jPCTposition;
        if (object != null) {
            jPCTposition = object.getTransformedCenter();
        } else {
            jPCTposition = new SimpleVector(0, 0, 0);
        }

        SimpleVector position = convertCoordinates(jPCTposition);

        // Queue a command to quick stream this new source:
        CommandQueue(new CommandObject(CommandObject.QUICK_PLAY, priority,
                true, toLoop, sourcename,
                new FilenameURL(filename), position.x,
                position.y, position.z, attmodel, distOrRoll,
                true));
        CommandQueue(new CommandObject(CommandObject.PLAY, sourcename));
        // Wake the command thread to process commands:
        commandThread.interrupt();

        // Create the boundObjects map if it doesn't exist:
        if (boundObjects == null) {
            boundObjects = new HashMap<>();
        }

        // Add the sourcename and Object3D to the map:
        if (boundObjects != null && sourcename != null && object != null) {
            boundObjects.put(sourcename, object);
        }

        // return the new source name.
        return sourcename;
    }

    /**
     * Creates a temporary, non-priority source at the origin and streams it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly generated
     * name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @return The new sorce's name.
     */
    public String quickStream(URL url, String identifier, boolean toLoop) {
        return quickStream(url, identifier, toLoop,
                new SimpleVector(0, 0, 0),
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary source at the origin and streams it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly generated name for the new
     * source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @return The new sorce's name.
     */
    public String quickStream(boolean priority, URL url, String identifier,
            boolean toLoop) {
        return quickStream(priority, url, identifier, toLoop,
                new SimpleVector(0, 0, 0),
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary non-priority source and streams it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly generated name for the new
     * source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @return The new sorce's name.
     */
    public String quickStream(URL url, String identifier, boolean toLoop,
            SimpleVector jPCTposition) {
        return quickStream(url, identifier, toLoop, jPCTposition,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary source and streams it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE:
     * to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @return The new sorce's name.
     */
    public String quickStream(boolean priority, URL url, String identifier,
            boolean toLoop, SimpleVector jPCTposition) {
        return quickStream(priority, url, identifier, toLoop, jPCTposition,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary non-priority source at the origin and streams it. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. After the
     * source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the
     * return value for sourcename.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickStream(URL url, String identifier, boolean toLoop,
            int attmodel) {
        return quickStream(url, identifier, toLoop,
                new SimpleVector(0, 0, 0), attmodel);
    }

    /**
     * Creates a temporary source and streams it. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. After the source finishes playing,
     * it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickStream(boolean priority, URL url, String identifier,
            boolean toLoop, int attmodel) {
        return quickStream(priority, url, identifier, toLoop,
                new SimpleVector(0, 0, 0), attmodel);
    }

    /**
     * Creates a temporary non-priority source at the origin and streams it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a
     * source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @param distORroll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickStream(URL url, String identifier, boolean toLoop,
            int attmodel, float distORroll) {
        return quickStream(url, identifier, toLoop,
                new SimpleVector(0, 0, 0), attmodel, distORroll);
    }

    /**
     * Creates a temporary source at the origin and streams it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created
     * by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param attmodel Attenuation model to use.
     * @param distORroll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickStream(boolean priority, URL url, String identifier,
            boolean toLoop, int attmodel, float distORroll) {
        return quickStream(priority, url, identifier, toLoop, 0, 0, 0,
                attmodel, distORroll);
    }

    /**
     * Creates a temporary non-priority source and streams it. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. After the source
     * finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return
     * value for sourcename.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickStream(URL url, String identifier, boolean toLoop,
            SimpleVector jPCTposition, int attmodel) {
        return quickStream(false, url, identifier, toLoop, jPCTposition,
                attmodel);
    }

    /**
     * Creates a temporary source and streams it. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'. After the source finishes playing,
     * it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickStream(boolean priority, URL url, String identifier,
            boolean toLoop, SimpleVector jPCTposition,
            int attmodel) {
        switch (attmodel) {
            case SoundSystemConfig.ATTENUATION_ROLLOFF:
                return quickStream(priority, url, identifier, toLoop,
                        jPCTposition, attmodel,
                        SoundSystemConfig.getDefaultRolloff());
            case SoundSystemConfig.ATTENUATION_LINEAR:
                return quickStream(priority, url, identifier, toLoop,
                        jPCTposition, attmodel,
                        SoundSystemConfig.getDefaultFadeDistance());
            default:
                return quickStream(priority, url, identifier, toLoop,
                        jPCTposition, attmodel, 0);
        }
    }

    /**
     * Creates a temporary non-priority source and streams it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by
     * this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickStream(URL url, String identifier, boolean toLoop,
            SimpleVector jPCTposition, int attmodel,
            float distOrRoll) {
        return quickStream(false, url, identifier, toLoop, jPCTposition,
                attmodel, distOrRoll);
    }

    /**
     * Creates a temporary source and streams it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method
     * permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickStream(boolean priority, URL url, String identifier,
            boolean toLoop, SimpleVector jPCTposition,
            int attmodel, float distOrRoll) {
        //generate a random name for this source:
        String sourcename = "Source_"
                + randomNumberGenerator.nextInt()
                + "_" + randomNumberGenerator.nextInt();

        SimpleVector position = convertCoordinates(jPCTposition);

        // Queue a command to quick stream this new source:
        CommandQueue(new CommandObject(CommandObject.QUICK_PLAY, priority,
                true, toLoop, sourcename,
                new FilenameURL(url, identifier), position.x,
                position.y, position.z, attmodel, distOrRoll,
                true));
        CommandQueue(new CommandObject(CommandObject.PLAY, sourcename));
        // Wake the command thread to process commands:
        commandThread.interrupt();

        // return the new source name.
        return sourcename;
    }

    /**
     * Creates a temporary non-priority source, binds it to the specified Object3D, and streams it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a
     * randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @return The new sorce's name.
     */
    public String quickStream(URL url, String identifier, boolean toLoop,
            Object3D object) {
        return quickStream(url, identifier, toLoop, object,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary source, binds it to the specified Object3D, and streams it. Default values are used for attenuation. After the source finishes playing, it is removed. Returns a randomly
     * generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @return The new sorce's name.
     */
    public String quickStream(boolean priority, URL url, String identifier,
            boolean toLoop, Object3D object) {
        return quickStream(priority, url, identifier, toLoop, object,
                SoundSystemConfig.getDefaultAttenuation());
    }

    /**
     * Creates a temporary non-priority source, binds it to the specified Object3D, and streams it. Default value is used for either fade-distance or rolloff factor, depending on the value of
     * parameter 'attmodel'. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the
     * setActive() method using the return value for sourcename.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickStream(URL url, String identifier, boolean toLoop,
            Object3D object, int attmodel) {
        return quickStream(false, url, identifier, toLoop, object, attmodel);
    }

    /**
     * Creates a temporary source, binds it to the specified Object3D, and streams it. Default value is used for either fade-distance or rolloff factor, depending on the value of parameter 'attmodel'.
     * After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to make a source created by this method permanant, call the setActive() method
     * using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @param attmodel Attenuation model to use.
     * @return The new sorce's name.
     */
    public String quickStream(boolean priority, URL url, String identifier,
            boolean toLoop, Object3D object, int attmodel) {
        switch (attmodel) {
            case SoundSystemConfig.ATTENUATION_ROLLOFF:
                return quickStream(priority, url, identifier, toLoop, object,
                        attmodel,
                        SoundSystemConfig.getDefaultRolloff());
            case SoundSystemConfig.ATTENUATION_LINEAR:
                return quickStream(priority, url, identifier, toLoop, object,
                        attmodel,
                        SoundSystemConfig.getDefaultFadeDistance());
            default:
                return quickStream(priority, url, identifier, toLoop, object,
                        attmodel, 0);
        }
    }

    /**
     * Creates a temporary non-priority source, binds it to the specified Object3D, and streams it. After the source finishes playing, it is removed. Returns a randomly generated name for the new
     * source. NOTE: to make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickStream(URL url, String identifier, boolean toLoop,
            Object3D object, int attmodel, float distOrRoll) {
        return quickStream(false, url, identifier, toLoop, object, attmodel,
                distOrRoll);
    }

    /**
     * Creates a temporary source, binds it to the specified Object3D, and streams it. After the source finishes playing, it is removed. Returns a randomly generated name for the new source. NOTE: to
     * make a source created by this method permanant, call the setActive() method using the return value for sourcename.
     *
     * @param priority Setting this to true will prevent other sounds from overriding this one.
     * @param url URL handle to the sound file to stream at this source.
     * @param identifier Filename/identifier of the file referenced by the URL.
     * @param toLoop Should this source loop, or play only once.
     * @param object Object3D for this source to follow.
     * @param attmodel Attenuation model to use.
     * @param distOrRoll Either the fading distance or rolloff factor, depending on the value of "attmodel".
     * @return The new sorce's name.
     */
    public String quickStream(boolean priority, URL url, String identifier,
            boolean toLoop, Object3D object,
            int attmodel, float distOrRoll) {
        //generate a random name for this source:
        String sourcename = "Source_"
                + randomNumberGenerator.nextInt()
                + "_" + randomNumberGenerator.nextInt();

        SimpleVector jPCTposition;
        if (object != null) {
            jPCTposition = object.getTransformedCenter();
        } else {
            jPCTposition = new SimpleVector(0, 0, 0);
        }

        SimpleVector position = convertCoordinates(jPCTposition);

        // Queue a command to quick stream this new source:
        CommandQueue(new CommandObject(CommandObject.QUICK_PLAY, priority,
                true, toLoop, sourcename,
                new FilenameURL(url, identifier), position.x,
                position.y, position.z, attmodel, distOrRoll,
                true));
        CommandQueue(new CommandObject(CommandObject.PLAY, sourcename));
        // Wake the command thread to process commands:
        commandThread.interrupt();

        // Create the boundObjects map if it doesn't exist:
        if (boundObjects == null) {
            boundObjects = new HashMap<>();
        }

        // Add the sourcename and Object3D to the map:
        if (boundObjects != null && sourcename != null && object != null) {
            boundObjects.put(sourcename, object);
        }

        // return the new source name.
        return sourcename;
    }

    /**
     * Move a source to the specified location.
     *
     * @param sourcename Identifier for the source.
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     */
    public void setPosition(String sourcename, SimpleVector jPCTposition) {
        SimpleVector position = convertCoordinates(jPCTposition);

        CommandQueue(new CommandObject(CommandObject.SET_POSITION,
                sourcename, position.x, position.y,
                position.z));
        commandThread.interrupt();
    }

    /**
     * Moves the listener relative to the current location.
     *
     * @param jPCTrelative SimpleVector containing jPCT coordinates.
     */
    public void moveListener(SimpleVector jPCTrelative) {
        SimpleVector relative = convertCoordinates(jPCTrelative);

        CommandQueue(new CommandObject(CommandObject.MOVE_LISTENER,
                relative.x, relative.y, relative.z));
        commandThread.interrupt();
    }

    /**
     * Moves the listener to the specified location.
     *
     * @param jPCTposition SimpleVector containing jPCT coordinates.
     */
    public void setListenerPosition(SimpleVector jPCTposition) {
        SimpleVector position = convertCoordinates(jPCTposition);

        CommandQueue(new CommandObject(CommandObject.SET_LISTENER_POSITION,
                position.x, position.y, position.z));
        commandThread.interrupt();
    }

    /**
     * Sets the listener's orientation.
     *
     * @param jPCTlook normalized SimpleVector representing the "look" direction (in jPCT coordinates).
     * @param jPCTup normalized SimpleVector representing the "up" direction (in jPCT coordinates).
     */
    public void setListenerOrientation(SimpleVector jPCTlook,
            SimpleVector jPCTup) {
        SimpleVector look = convertCoordinates(jPCTlook);
        SimpleVector up = convertCoordinates(jPCTup);

        CommandQueue(new CommandObject(CommandObject.SET_LISTENER_ORIENTATION,
                look.x, look.y, look.z, up.x, up.y,
                up.z));
        commandThread.interrupt();
    }

    /**
     * Sets the listener's orientation to match the specified camera.
     *
     * @param camera Camera to match.
     */
    public void setListenerOrientation(Camera camera) {
        SimpleVector look = convertCoordinates(camera.getDirection());
        SimpleVector up = convertCoordinates(camera.getUpVector());

        CommandQueue(new CommandObject(CommandObject.SET_LISTENER_ORIENTATION,
                look.x, look.y, look.z, up.x, up.y,
                up.z));
        commandThread.interrupt();
    }

    /**
     * Sets the specified source's velocity, for use in Doppler effect. See {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information about Doppler effect.
     *
     * @param sourcename The source's name.
     * @param velocity Velocity vector in jPCT world-space.
     */
    public void setVelocity(String sourcename, SimpleVector velocity) {
        SimpleVector vel = convertCoordinates(velocity);
        CommandQueue(new CommandObject(CommandObject.SET_VELOCITY,
                sourcename, vel.x, vel.y, vel.z));
        commandThread.interrupt();
    }

    /**
     * Sets the listener's velocity, for use in Doppler effect. See {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more information about Doppler effect.
     *
     * @param velocity Velocity vector in jPCT world-space.
     */
    public void setListenerVelocity(SimpleVector velocity) {
        SimpleVector vel = convertCoordinates(velocity);
        CommandQueue(new CommandObject(CommandObject.SET_LISTENER_VELOCITY,
                vel.x, vel.y, vel.z));
        commandThread.interrupt();
    }

    /**
     * Converts a SimpleVector from jPCT coordinates into SoundSystem coordinates, and vice-versa. (y = -y and z = -z)
     */
    public SimpleVector convertCoordinates(SimpleVector v) {
        return new SimpleVector(v.x, -v.y, -v.z);
    }
}
