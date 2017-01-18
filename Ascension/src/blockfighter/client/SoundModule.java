package blockfighter.client;

import blockfighter.shared.Globals;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemJPCT;

public class SoundModule implements Runnable {

    private static final ScheduledExecutorService SOUND_MODULE_SCHEDULER = Executors.newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder()
            .namingPattern("Sound-Module-%d")
            .daemon(true)
            .priority(Thread.NORM_PRIORITY)
            .build());

    private SoundSystemJPCT soundModule;
    private float originVol = 0.2f;
    private byte currentBGM = -1;
    private final ConcurrentLinkedQueue<Byte> bgmQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Byte> sfxQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void run() {
        this.soundModule = new SoundSystemJPCT();
        this.soundModule.setMasterVolume(0.5f);
        SoundSystemConfig.setSoundFilesPackage("resources/sounds/");
        SOUND_MODULE_SCHEDULER.scheduleAtFixedRate(() -> {
            if (isLoaded()) {
                while (!this.sfxQueue.isEmpty()) {
                    this.soundModule.quickPlay(Globals.SFXs.get(sfxQueue.poll()).getResourcePath(), false);
                }

                while (!this.bgmQueue.isEmpty()) {
                    byte bgmID = this.bgmQueue.poll();
                    if (bgmID > -1 && currentBGM != bgmID) {
                        if (currentBGM != -1) {
                            this.soundModule.fadeOut(Globals.BGMs.get(currentBGM).getResourcePath(), null, 1000);
                        }
                        this.soundModule.backgroundMusic(Globals.BGMs.get(bgmID).getResourcePath(), Globals.BGMs.get(bgmID).getResourcePath());
                        currentBGM = bgmID;
                        this.soundModule.setVolume(Globals.BGMs.get(bgmID).getResourcePath(), 1f);
                        Globals.log(SoundModule.class, "Playing " + Globals.BGMs.get(bgmID), Globals.LOG_TYPE_DATA);
                    }
                }
            }
        }, 0, 1, TimeUnit.MILLISECONDS);
    }

    public void setListenerPos(final int x, final int y) {
        //soundModule.setListenerPosition(new SimpleVector(x * .04, y * .04, 0));
    }

    public void shutdown() {
        this.soundModule.cleanup();
    }

    public void playSound(final byte soundID) {
        this.sfxQueue.add(soundID);
    }

    public void playSound(final byte soundID, final int x, final int y) {
        this.sfxQueue.add(soundID);
    }

    public void playBGM(final byte bgmID) {
        this.bgmQueue.add(bgmID);
    }

    public boolean isLoaded() {
        return this.soundModule != null;
    }

    public void mute() {
        if (!isLoaded()) {
            return;
        }
        if (this.soundModule.getMasterVolume() > 0) {
            originVol = this.soundModule.getMasterVolume();
            this.soundModule.setMasterVolume(0f);
        }
    }

    public void unmute() {
        if (!isLoaded()) {
            return;
        }
        if (this.soundModule.getMasterVolume() <= 0) {
            this.soundModule.setMasterVolume(originVol);
        }
    }
}
