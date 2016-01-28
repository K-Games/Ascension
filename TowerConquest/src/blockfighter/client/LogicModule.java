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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ken Kwan
 */
public class LogicModule implements Runnable {

    // Shared Data
    private PacketReceiver receiver = null;

    private SaveData selectedChar;
    private byte selectedRoom = 0;
    private Screen screen = new ScreenSelectChar();
    // private Screen screen = new ScreenSpriteTest();
    private final SoundModule soundModule;
    private boolean initBgm = false, loggedIn = false;

    public LogicModule(final SoundModule s) {
        this.soundModule = s;
    }

    @Override
    public void run() {
        if (this.soundModule.isLoaded() && !this.initBgm) {
            soundModule.playBGM("theme.ogg");
            this.initBgm = true;
        }
        try {
            this.screen.update();
        } catch (final Exception ex) {
            System.err.println(ex);
        }
    }

    public void receiveLogin() {
        byte attemps = 0;
        this.loggedIn = false;
        while (!this.loggedIn && attemps < 5) {
            PacketSender.sendPlayerCreate(this.selectedRoom, this.selectedChar);
            attemps++;
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
            setScreen(new ScreenIngame(key, size, loading.getLoadedMap()));
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

    public void sendSetBossType(final byte k) {
        PacketSender.sendSetBossType(this.selectedRoom, k);
    }

    public void sendGetBossStat(final byte k, final byte s) {
        PacketSender.sendGetBossStat(this.selectedRoom, k, s);
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
        if (this.receiver != null) {
            this.receiver.shutdown();
            this.receiver = null;
        }
        setScreen(new ScreenServerList());
        // soundModule.playBGM("theme.ogg");
    }

    public void playSound(final String soundFile) {
        this.soundModule.playSound(soundFile);
    }

    public void playBGM(final String bgmFile) {
        this.soundModule.playBGM(bgmFile);
    }

}
