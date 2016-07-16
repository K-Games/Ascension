package blockfighter.client;

import blockfighter.client.net.GameClient;
import blockfighter.client.screen.Screen;
import blockfighter.client.screen.ScreenIngame;
import blockfighter.client.screen.ScreenSelectChar;
import blockfighter.client.screen.ScreenServerList;

/**
 *
 * @author Ken Kwan
 */
public class LogicModule implements Runnable {

    // Shared Data
    private GameClient client;
    private long currentTime = 0;
    private SaveData selectedChar;
    private byte selectedRoom = 0;
    private Screen screen = new ScreenSelectChar();
    // private Screen screen = new ScreenSpriteTest();
    private final SoundModule soundModule;
    private boolean initBgm = false;

    public LogicModule(final SoundModule s) {
        this.soundModule = s;
    }

    @Override
    public void run() {
        if (this.soundModule.isLoaded() && !this.initBgm) {
            this.soundModule.playBGM(Globals.BGM_MENU);
            this.initBgm = true;
        }
        try {
            this.currentTime = System.nanoTime();
            this.screen.update();
        } catch (final Exception ex) {
            ex.printStackTrace();
            System.err.println(this.getClass().getCanonicalName() + ": " + ex.getLocalizedMessage() + "@" + ex.getStackTrace()[0]);
        }
    }

    public long getTime() {
        return this.currentTime;
    }

    public Screen getScreen() {
        return this.screen;
    }

    public void connect(final String server, final byte r) {
        this.selectedRoom = r;
        client = new GameClient(this, server);
        client.setName("GameClient");
        client.setDaemon(true);
        client.start();
    }

    public void disconnect() {
        if (this.selectedChar != null) {
            SaveData.saveData(this.selectedChar.getSaveNum(), this.selectedChar);
        }
        if (getScreen() instanceof ScreenIngame) {
            ((ScreenIngame) getScreen()).disconnect();
        }
    }

    public void setSelectedChar(final SaveData s) {
        this.selectedChar = s;
    }

    public SaveData getSelectedChar() {
        return this.selectedChar;
    }

    public void setSelectedRoom(final byte r) {
        this.selectedRoom = r;
    }

    public byte getSelectedRoom() {
        return this.selectedRoom;
    }

    public void setScreen(final Screen s) {
        this.screen.unload();
        this.screen = s;
    }

    public void returnMenu() {
        shutdownClient();
        setScreen(new ScreenServerList());
        this.soundModule.playBGM(Globals.BGM_MENU);
    }

    public void playSound(final byte sfxID, final int x, final int y) {
        this.soundModule.playSound(sfxID, x, y);
    }

    public void playBGM(final byte bgmID) {
        this.soundModule.playBGM(bgmID);
    }

    public void disableSound() {
        this.soundModule.mute();
    }

    public void enableSound() {
        this.soundModule.unmute();
    }

    public void setSoundLisenterPos(final int x, final int y) {
        this.soundModule.setListenerPos(x, y);
    }

    private void shutdownClient() {
        client.shutdownClient();
    }
}
