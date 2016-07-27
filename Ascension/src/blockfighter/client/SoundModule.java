package blockfighter.client;

import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemJPCT;

public class SoundModule implements Runnable {

    private SoundSystemJPCT soundModule;
    private float originVol = 0.2f;
    private byte currentBGM = -1;

    @Override
    public void run() {
        this.soundModule = new SoundSystemJPCT();
        this.soundModule.setMasterVolume(0.5f);
        SoundSystemConfig.setSoundFilesPackage("resources/sounds/");
    }

    public void setListenerPos(final int x, final int y) {
        //soundModule.setListenerPosition(new SimpleVector(x * .04, y * .04, 0));
    }

    public void shutdown() {
        this.soundModule.cleanup();
    }

    public void playSound(final byte soundID) {
        if (isLoaded()) {
            this.soundModule.quickPlay(Globals.SOUND_SFX[soundID], false);
        }
    }

    public void playSound(final byte soundID, final int x, final int y) {
        if (isLoaded()) {
            this.soundModule.quickPlay(Globals.SOUND_SFX[soundID], false);
        }
    }

    public void playBGM(final byte bgmID) {
        if (isLoaded() && bgmID > -1 && currentBGM != bgmID) {
            if (currentBGM != -1) {
                this.soundModule.fadeOut(Globals.SOUND_BGM[currentBGM], null, 1000);
            }
            this.soundModule.backgroundMusic(Globals.SOUND_BGM[bgmID], Globals.SOUND_BGM[bgmID]);
            currentBGM = bgmID;
            this.soundModule.setVolume("bgm", 1f);
            System.out.println("Playing " + Globals.SOUND_BGM[bgmID]);
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
