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

    //Shared Data
    private PacketReceiver receiver = null;

    private SaveData selectedChar;
    private byte selectedRoom = 0;
    private Screen screen = new ScreenSelectChar();
    //private Screen screen = new ScreenSpriteTest();
    private SoundModule soundModule;
    private boolean initBgm = false, loggedIn = false;

    public LogicModule(SoundModule s) {
        soundModule = s;
    }

    @Override
    public void run() {
        if (soundModule.isLoaded() && !initBgm) {
            //soundModule.playBGM("theme.ogg");
            initBgm = true;
        }
        try {
            screen.update();
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    public void receiveLogin() {
        byte attemps = 0;
        loggedIn = false;
        while (!loggedIn && attemps < 5) {
            PacketSender.sendPlayerCreate(selectedRoom, selectedChar);
            attemps++;
            synchronized (this) {
                try {
                    this.wait(900);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public void receiveCreate(byte mapID, byte key, byte size) {
        synchronized (this) {
            loggedIn = true;
            this.notify();
        }

        ScreenLoading loading = new ScreenLoading();
        setScreen(loading);
        try {
            loading.load(mapID);
            synchronized (loading) {
                try {
                    loading.wait();
                } catch (InterruptedException e) {
                }
            }
            setScreen(new ScreenIngame(key, size, loading.getLoadedMap()));
            sendGetAll(key);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            Particle.unloadParticles();
            disconnect();
            sendDisconnect(key);
            returnMenu();
        }
    }

    public void sendGetPing(byte k, byte pID) {
        PacketSender.sendGetPing(selectedRoom, k, pID);
    }

    public void sendGetAll(byte k) {
        PacketSender.sendGetAll(selectedRoom, k);
    }

    public void sendSetBossType(byte k) {
        PacketSender.sendSetBossType(selectedRoom, k);
    }

    public void sendGetBossStat(byte k, byte s) {
        PacketSender.sendGetBossStat(selectedRoom, k, s);
    }

    public void sendGetName(byte k) {
        PacketSender.sendGetName(selectedRoom, k);
    }

    public void sendGetStat(byte k, byte s) {
        PacketSender.sendGetStat(selectedRoom, k, s);
    }

    public void sendGetEquip(byte k) {
        PacketSender.sendGetEquip(selectedRoom, k);
    }

    public void sendDisconnect(byte k) {
        PacketSender.sendDisconnect(selectedRoom, k);
    }

    public void sendUseSkill(byte k, byte sc) {
        PacketSender.sendUseSkill(selectedRoom, k, sc);
    }

    public void sendMoveKey(byte k, byte dir, boolean b) {
        PacketSender.sendMove(selectedRoom, k, dir, b);
    }

    public void sendLogin(final String server, byte r) {
        selectedRoom = r;
        Thread send = new Thread() {
            @Override
            public void run() {
                if (receiver != null && receiver.isConnected()) {
                    return;
                }
                try {
                    DatagramSocket socket = new DatagramSocket();
                    Globals.SERVER_ADDRESS = server;
                    if (screen instanceof ScreenServerList) {
                        ((ScreenServerList) screen).setStatus(ScreenServerList.STATUS_CONNECTING);
                    }
                    socket.connect(InetAddress.getByName(Globals.SERVER_ADDRESS), Globals.SERVER_PORT);
                    socket.setSoTimeout(5000);
                    PacketSender.setSocket(socket);
                    receiver = new PacketReceiver(socket);
                    receiver.setName("Reciever");
                    receiver.setDaemon(true);
                    receiver.start();
                    System.out.println("Connecting to " + server);
                    PacketSender.sendPlayerLogin(selectedRoom, selectedChar);
                } catch (SocketException e) {
                    if (screen instanceof ScreenServerList) {
                        ((ScreenServerList) screen).setStatus(ScreenServerList.STATUS_SOCKETCLOSED);
                    }
                } catch (UnknownHostException ex) {
                    if (screen instanceof ScreenServerList) {
                        ((ScreenServerList) screen).setStatus(ScreenServerList.STATUS_UNKNOWNHOST);
                    }
                }
            }
        };
        send.setName("HostResolver");
        send.setDaemon(true);
        send.start();
    }

    public Screen getScreen() {
        return screen;
    }

    public void setPing(byte data) {
        if (screen instanceof ScreenIngame) {
            ((ScreenIngame) screen).setPing(data);
        }
    }

    public void queueData(byte[] data) {
        if (screen instanceof ScreenIngame) {
            ((ScreenIngame) screen).queueData(data);
        }
    }

    public void disconnect() {
        if (selectedChar != null) {
            SaveData.saveData(selectedChar.getSaveNum(), selectedChar);
        }
        if (screen instanceof ScreenIngame) {
            ((ScreenIngame) screen).disconnect();
        }
    }

    public void setSelectedChar(SaveData s) {
        selectedChar = s;
    }

    public SaveData getSelectedChar() {
        return selectedChar;
    }

    public void setSelectedRoom(byte r) {
        selectedRoom = r;
    }

    public byte getSelectedRoom() {
        return selectedRoom;
    }

    public void setScreen(Screen s) {
        screen.unload();
        screen = s;
    }

    public void returnMenu() {
        if (receiver != null) {
            receiver.shutdown();
            receiver = null;
        }
        setScreen(new ScreenServerList());
        //soundModule.playBGM("theme.ogg");
    }

    public void playSound(String soundFile) {
        soundModule.playSound(soundFile);
    }

    public void playBGM(String bgmFile) {
        soundModule.playBGM(bgmFile);
    }

}
