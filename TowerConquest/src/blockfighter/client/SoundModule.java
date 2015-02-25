package blockfighter.client;

import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemJPCT;

/**
 *
 * @author Ken Kwan
 */
public class SoundModule implements Runnable {

    private SoundSystemJPCT soundModule;

    @Override
    public void run() {
        soundModule = new SoundSystemJPCT();
        soundModule.setMasterVolume(0.7f);
        SoundSystemConfig.setSoundFilesPackage("blockfighter/client/sounds/");
    }

    public void shutdown() {
        soundModule.cleanup();
    }

    public void playSound(String soundFile) {
        if (isLoaded()) {
            soundModule.quickPlay(soundFile, false);
        }
    }

    public void playBGM(String bgmFile) {
        if (isLoaded()) {
            soundModule.stop("bgm");
            soundModule.backgroundMusic("bgm", bgmFile);
        }
    }

    public boolean isLoaded() {
        return soundModule != null;
    }
}
