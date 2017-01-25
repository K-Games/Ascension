package blockfighter.client;

import blockfighter.client.entities.player.skills.Skill;
import blockfighter.client.net.GameClient;
import blockfighter.client.screen.Screen;
import blockfighter.client.screen.ScreenIngame;
import blockfighter.client.screen.ScreenServerList;
import blockfighter.shared.Globals;

public class LogicModule implements Runnable {

    // Shared Data
    private GameClient client;
    private long currentTime = 0;
    private SaveData selectedChar;
    private byte selectedRoom = 0;
    private byte myPlayerKey = -1;
    private Screen screen;

    private long connectStartTime = 0;
    private boolean connecting = false;

    @Override
    public void run() {
        try {
            this.currentTime = System.nanoTime();
            this.screen.update();

            if (connecting && this.currentTime - connectStartTime >= Globals.msToNs(6000)) {
                connecting = false;
                shutdownClient();
            }

        } catch (final Exception ex) {
            Globals.logError(ex.toString(), ex);
        }
    }

    public long getTime() {
        return this.currentTime;
    }

    public Screen getScreen() {
        return this.screen;
    }

    public void connect(final String server) {
        boolean skillReady = false;
        boolean equipReady = false;
        for (Skill s : this.selectedChar.getHotkeys()) {
            if (s != null) {
                skillReady = true;
            }
        }

        if (this.selectedChar.getEquip()[Globals.ITEM_WEAPON] != null) {
            equipReady = true;
        }

        if (skillReady && equipReady) {
            client = new GameClient(this, server);
            AscensionClient.SHARED_THREADPOOL.execute(client);
        } else if (!skillReady && !equipReady) {
            if (getScreen() instanceof ScreenServerList) {
                ((ScreenServerList) getScreen()).setStatus(ScreenServerList.STATUS_NOSKILL_NOEQUIP);
            }
        } else if (!skillReady) {
            if (getScreen() instanceof ScreenServerList) {
                ((ScreenServerList) getScreen()).setStatus(ScreenServerList.STATUS_NOSKILL);
            }
        } else if (!equipReady) {
            if (getScreen() instanceof ScreenServerList) {
                ((ScreenServerList) getScreen()).setStatus(ScreenServerList.STATUS_NOEQUIP);
            }
        }
    }

    public void disconnect() {
        if (this.selectedChar != null) {
            SaveData.saveData(this.selectedChar.getSaveNum(), this.selectedChar);
        }
        if (getScreen() instanceof ScreenIngame) {
            ((ScreenIngame) getScreen()).disconnect();
        }
    }

    public void setMyPlayerKey(final byte key) {
        this.myPlayerKey = key;
    }

    public byte getMyPlayerKey() {
        return this.myPlayerKey;
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
        Core.getSoundModule().playBGM(s.getBgmCode());
        if (this.screen != null) {
            this.screen.unload();
        }
        this.screen = s;
    }

    public void returnMenu() {
        shutdownClient();
        setScreen(new ScreenServerList(true));
    }

    private void shutdownClient() {
        shutdownClient(ScreenServerList.STATUS_NORMAL_SHUTDOWN);
    }

    public void shutdownClient(final byte status) {
        client.shutdownClient(status);
        setMyPlayerKey((byte) -1);
    }

    public void startLoginAttemptTimeout() {
        this.connectStartTime = this.currentTime;
        this.connecting = true;
    }

    public void stopCharacterLoginAttemptTimeout() {
        this.connecting = false;
    }

}
