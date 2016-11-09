package blockfighter.client.net;

import blockfighter.shared.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.entities.emotes.Emote;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.screen.ScreenIngame;
import blockfighter.client.screen.ScreenLoading;
import blockfighter.client.screen.ScreenServerList;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameClient extends Thread {

    private static Client client;
    private PacketReceiver receiver;
    private final LogicModule logic;
    private boolean loggedIn = false;
    private final ConcurrentLinkedQueue<byte[]> dataQueue = new ConcurrentLinkedQueue<>();

    public GameClient(final LogicModule lm, final String server) {
        this.logic = lm;
        Globals.SERVER_ADDRESS = server;
    }

    @Override
    public void run() {
        if (this.receiver != null && this.receiver.isConnected()) {
            return;
        }
        if (logic.getScreen() instanceof ScreenServerList) {
            ((ScreenServerList) logic.getScreen()).setStatus(ScreenServerList.STATUS_CONNECTING);
        }

        client = new Client(Globals.PACKET_MAX_SIZE * 800, Globals.PACKET_MAX_SIZE);
        client.setTimeout(5000);
        client.setKeepAliveTCP(500);
        PacketHandler.setGameClient(this);
        client.start();

        Kryo kyro = client.getKryo();
        kyro.register(byte[].class);
        this.receiver = new PacketReceiver();
        client.addListener(new Listener.ThreadedListener(this.receiver));

        System.out.println("Connecting to " + Globals.SERVER_ADDRESS);
        try {
            client.connect(5000, Globals.SERVER_ADDRESS, Globals.SERVER_TCP_PORT);
            PacketSender.sendPlayerLogin(logic.getSelectedRoom(), logic.getSelectedChar());
        } catch (IOException ex) {
            client.close();
            if (logic.getScreen() instanceof ScreenServerList) {
                ((ScreenServerList) logic.getScreen()).setStatus(ScreenServerList.STATUS_FAILEDCONNECT);
            }
        }

    }

    public void shutdownClient(final byte status) {
        client.close();
        this.receiver = null;
        if (logic.getScreen() instanceof ScreenServerList) {
            ((ScreenServerList) logic.getScreen()).setStatus(status);
        }
    }

    public void receiveLogin(final byte[] data) {
        byte attempts = 0;
        this.loggedIn = false;

        byte loginResponse = data[1];
        switch (loginResponse) {
            case Globals.LOGIN_SUCCESS:
                try {
                    if (data[2] != Globals.GAME_MAJOR_VERSION || data[3] != Globals.GAME_MINOR_VERSION || data[4] != Globals.GAME_UPDATE_NUMBER) {
                        shutdownClient(ScreenServerList.STATUS_WRONGVERSION);
                        return;
                    }
                } catch (Exception e) {
                    shutdownClient(ScreenServerList.STATUS_WRONGVERSION);
                    return;
                }
                break;
            case Globals.LOGIN_FAIL_UID_IN_ROOM:
                shutdownClient(ScreenServerList.STATUS_UIDINROOM);
                return;
            case Globals.LOGIN_FAIL_FULL_ROOM:
                shutdownClient(ScreenServerList.STATUS_FULLROOM);
                return;
            case Globals.LOGIN_FAIL_OUTSIDE_LEVEL_RANGE:
                shutdownClient(ScreenServerList.STATUS_OUTSIDELEVEL);
                return;
            default:
                shutdownClient((byte) -1);
                return;
        }

        while (!this.loggedIn && attempts < 5) {
            System.out.println("Attempting to login with character. " + (attempts + 1) + "/5");
            PacketSender.sendPlayerCreate(logic.getSelectedRoom(), logic.getSelectedChar());
            attempts++;
            synchronized (this) {
                try {
                    this.wait(900);
                } catch (final InterruptedException e) {
                    break;
                }
            }
        }
        if (attempts >= 5) {
            shutdownClient(ScreenServerList.STATUS_FAILEDCONNECT);
        }
    }

    public void receiveCreate(final byte mapID, final byte key, final byte size) {
        synchronized (this) {
            this.loggedIn = true;
            notify();
        }
        logic.setMyPlayerKey(key);
        final ScreenLoading loading = new ScreenLoading();
        logic.setScreen(loading);
        try {
            loading.load(mapID);
            synchronized (loading) {
                try {
                    loading.wait();
                } catch (final InterruptedException e) {
                }
            }
            System.out.println("Finished Loading");
            ScreenIngame ingameScreen = new ScreenIngame(size, loading.getLoadedMap(), this);
            logic.setScreen(ingameScreen);
            PacketSender.sendGetAll(logic.getSelectedRoom(), key);
        } catch (final Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            Particle.unloadParticles();
            Emote.unloadEmotes();
            logic.disconnect();
            PacketSender.sendDisconnect(logic.getSelectedRoom(), key);
            logic.returnMenu();
        }
    }

    public static Client getClient() {
        return client;
    }

    public static int getPing() {
        return client.getReturnTripTime();
    }

    public void queueData(final byte[] data) {
        this.dataQueue.add(data);
    }

    public ConcurrentLinkedQueue<byte[]> getDataQueue() {
        return this.dataQueue;
    }
}
