package blockfighter.client;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.net.PacketReceiver;
import blockfighter.client.net.PacketSender;
import blockfighter.client.screen.Screen;
import blockfighter.client.screen.ScreenIngame;
import blockfighter.client.screen.ScreenLoading;
import blockfighter.client.screen.ScreenSelectChar;
import blockfighter.client.screen.ScreenServerList;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ken Kwan
 */
public class LogicModule implements Runnable {

    // Shared Data
    private PacketReceiver receiver = null;
    private long currentTime = 0;
    private SaveData selectedChar;
    private byte selectedRoom = 0;
    private Screen screen = new ScreenSelectChar();
    // private Screen screen = new ScreenSpriteTest();
    private final SoundModule soundModule;
    private boolean initBgm = false, loggedIn = false;
    private final ConcurrentLinkedQueue<byte[]> dataQueue = new ConcurrentLinkedQueue<>();

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
            System.err.println(this.getClass().getCanonicalName() + ": " + ex.getLocalizedMessage() + "@" + ex.getStackTrace()[0]);
        }
    }

    public long getTime() {
        return this.currentTime;
    }

    public void receiveLogin(final byte[] data) {
        byte attempts = 0;
        this.loggedIn = false;
        System.out.println("Server Version: " + data[1] + "." + data[2]);
        if (data[1] != Globals.GAME_MAJOR_VERSION || data[2] != Globals.GAME_MINOR_VERSION) {
            System.out.println("Client Version mismatched. Client Version: " + Globals.GAME_MAJOR_VERSION + "." + Globals.GAME_MINOR_VERSION);
            shutdownSocket();
            if (getScreen() instanceof ScreenServerList) {
                ((ScreenServerList) getScreen()).setStatus(ScreenServerList.STATUS_WRONGVERSION);
            }
            return;
        }
        while (!this.loggedIn && attempts < 5) {
            System.out.println("Attempting to login with character. " + (attempts + 1) + "/5");
            PacketSender.sendPlayerCreate(this.selectedRoom, this.selectedChar);
            attempts++;
            synchronized (this) {
                try {
                    this.wait(900);
                } catch (final InterruptedException e) {
                    break;
                }
            }
        }
    }

    public void receiveCreate(final byte mapID, final byte key, final byte size) {
        synchronized (this) {
            this.loggedIn = true;
            notify();
        }

        final ScreenLoading loading = new ScreenLoading();
        setScreen(loading);
        try {
            loading.load(mapID);
            synchronized (loading) {
                try {
                    loading.wait();
                } catch (final InterruptedException e) {
                }
            }
            System.out.println("Finished Loading");
            ScreenIngame ingameScreen = new ScreenIngame(key, size, loading.getLoadedMap());
            while (!this.dataQueue.isEmpty()) {
                ingameScreen.queueData(this.dataQueue.poll());
            }
            ingameScreen.update();
            setScreen(ingameScreen);
            sendGetAll(key);
        } catch (final Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            Particle.unloadParticles();
            disconnect();
            sendDisconnect(key);
            returnMenu();
        }
    }

    public void sendGetPing(final byte k, final byte pID) {
        PacketSender.sendGetPing(this.selectedRoom, k, pID);
    }

    public void sendGetAll(final byte k) {
        PacketSender.sendGetAll(this.selectedRoom, k);
    }

    public void sendSetMobType(final byte k) {
        PacketSender.sendSetMobType(this.selectedRoom, k);
    }

    public void sendGetMobStat(final byte k, final byte s) {
        PacketSender.sendGetMobStat(this.selectedRoom, k, s);
    }

    public void sendGetName(final byte k) {
        PacketSender.sendGetName(this.selectedRoom, k);
    }

    public void sendGetStat(final byte k, final byte s) {
        PacketSender.sendGetStat(this.selectedRoom, k, s);
    }

    public void sendGetEquip(final byte k) {
        PacketSender.sendGetEquip(this.selectedRoom, k);
    }

    public void sendDisconnect(final byte k) {
        PacketSender.sendDisconnect(this.selectedRoom, k);
    }

    public void sendUseSkill(final byte k, final byte sc) {
        PacketSender.sendUseSkill(this.selectedRoom, k, sc);
    }

    public void sendMoveKey(final byte k, final byte dir, final boolean b) {
        PacketSender.sendMove(this.selectedRoom, k, dir, b);
    }

    public void sendLogin(final String server, final byte r) {
        this.selectedRoom = r;
        this.dataQueue.clear();
        final Thread send = new Thread() {
            @Override
            public void run() {
                if (LogicModule.this.receiver != null && LogicModule.this.receiver.isConnected()) {
                    return;
                }
                try {
                    final DatagramSocket socket = new DatagramSocket();
                    Globals.SERVER_ADDRESS = server;
                    if (LogicModule.this.screen instanceof ScreenServerList) {
                        ((ScreenServerList) LogicModule.this.screen).setStatus(ScreenServerList.STATUS_CONNECTING);
                    }
                    socket.connect(InetAddress.getByName(Globals.SERVER_ADDRESS), Globals.SERVER_PORT);
                    socket.setSoTimeout(5000);
                    PacketSender.setSocket(socket);
                    LogicModule.this.receiver = new PacketReceiver(socket);
                    LogicModule.this.receiver.setName("Reciever");
                    LogicModule.this.receiver.setDaemon(true);
                    LogicModule.this.receiver.start();
                    System.out.println("Connecting to " + server);
                    PacketSender.sendPlayerLogin(LogicModule.this.selectedRoom, LogicModule.this.selectedChar);
                } catch (final SocketException e) {
                    if (LogicModule.this.screen instanceof ScreenServerList) {
                        ((ScreenServerList) LogicModule.this.screen).setStatus(ScreenServerList.STATUS_SOCKETCLOSED);
                    }
                } catch (final UnknownHostException ex) {
                    if (LogicModule.this.screen instanceof ScreenServerList) {
                        ((ScreenServerList) LogicModule.this.screen).setStatus(ScreenServerList.STATUS_UNKNOWNHOST);
                    }
                }
            }
        };
        send.setName("HostResolver");
        send.setDaemon(true);
        send.start();
    }

    public Screen getScreen() {
        return this.screen;
    }

    public void setPing(final byte data) {
        if (this.screen instanceof ScreenIngame) {
            ((ScreenIngame) this.screen).setPing(data);
        }
    }

    public void queueData(final byte[] data) {
        if (this.screen instanceof ScreenIngame) {
            ((ScreenIngame) this.screen).queueData(data);
        } else {
            this.dataQueue.add(data);
        }
    }

    public void disconnect() {
        if (this.selectedChar != null) {
            SaveData.saveData(this.selectedChar.getSaveNum(), this.selectedChar);
        }
        if (this.screen instanceof ScreenIngame) {
            ((ScreenIngame) this.screen).disconnect();
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
        shutdownSocket();
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

    private void shutdownSocket() {
        if (this.receiver != null) {
            this.receiver.shutdown(false);
            this.receiver = null;
        }
    }
}
