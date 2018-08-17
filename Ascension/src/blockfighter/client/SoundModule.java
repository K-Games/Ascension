package blockfighter.client;

import blockfighter.shared.Globals;
import java.util.concurrent.ConcurrentLinkedQueue;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemJPCT;

public class SoundModule implements Runnable {

    private final SoundSystemJPCT soundModule;
    private byte currentBGM = -1;
    private final ConcurrentLinkedQueue<Byte> bgmQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Byte> sfxQueue = new ConcurrentLinkedQueue<>();
    private boolean muted = (Boolean) Globals.ClientOptions.SOUND_ENABLE.getValue();

    public SoundModule() {
        this.soundModule = new SoundSystemJPCT();
        updateVolume();
        SoundSystemConfig.setSoundFilesPackage("resources/sounds/");
    }

    @Override
    public void run() {
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
        this.muted = true;
        updateVolume();
    }

    public void unmute() {
        this.muted = false;
        updateVolume();
    }

    public final void updateVolume() {
        if (!isLoaded()) {
            return;
        }

        if (!muted) {
            float volume = (float) Math.pow(((Integer) Globals.ClientOptions.VOLUME_LEVEL.getValue()) / 100f, 2.7);
            this.soundModule.setMasterVolume(volume);
        } else {
            this.soundModule.setMasterVolume(0f);
        }

    }
}
