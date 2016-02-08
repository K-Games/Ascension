package blockfighter.client;

import com.threed.jpct.SimpleVector;
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

    public void setListenerPos(final int x, final int y) {
        soundModule.setListenerPosition(new SimpleVector(x * .2, y * .2, 0));
    }

    public void shutdown() {
        this.soundModule.cleanup();
    }

    public void playSound(final byte soundID, final int x, final int y) {
        if (isLoaded()) {
            this.soundModule.quickPlay(Globals.SOUND_SFX[soundID], false, new SimpleVector(x * .2, y * .2, 0));
        }
    }

    public void playBGM(final byte bgmID) {
        if (isLoaded() && bgmID > -1) {
            this.soundModule.stop("bgm");
            this.soundModule.backgroundMusic("bgm", Globals.SOUND_BGM[bgmID]);
            this.soundModule.setVolume("bgm", .35f);
            System.out.println("Play " + Globals.SOUND_BGM[bgmID]);
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
