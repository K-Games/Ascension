package blockfighter.client;

import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemJPCT;

/**
 *
 * @author Ken Kwan
 */
public class SoundModule implements Runnable {

    private SoundSystemJPCT soundModule;
    private float originVol = 0.2f;

    @Override
    public void run() {
        this.soundModule = new SoundSystemJPCT();
        this.soundModule.setMasterVolume(0.2f);
        SoundSystemConfig.setSoundFilesPackage("blockfighter/client/sounds/");
    }

    public void shutdown() {
        this.soundModule.cleanup();
    }

    public void playSound(final String soundFile) {
        if (isLoaded()) {
            this.soundModule.quickPlay(soundFile, false);
        }
    }

    public void playBGM(final String bgmFile) {
        if (isLoaded() && bgmFile != null) {
            this.soundModule.stop("bgm");
            this.soundModule.backgroundMusic("bgm", bgmFile);
            System.out.println("Play " + bgmFile);
        }
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
