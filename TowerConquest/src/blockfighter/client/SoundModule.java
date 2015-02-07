package blockfighter.client;

import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemJPCT;

/**
 *
 * @author Ken Kwan
 */
public class SoundModule {
    private SoundSystemJPCT soundModule;
    
    public SoundModule(){
        soundModule = new SoundSystemJPCT();
        SoundSystemConfig.setSoundFilesPackage("blockfighter/client/sounds/");
    }
    
    public void shutdown(){
        soundModule.cleanup();
    }
    
    public void playSound(String soundFile){
        soundModule.quickPlay(soundFile, false);
    }
    
    public void playBGM(String bgmFile){
        soundModule.stop("bgm");
        soundModule.backgroundMusic("bgm", bgmFile);
    }
}
