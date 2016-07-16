package blockfighter.client.net;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
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

    private Client client;
    private PacketReceiver receiver;
    private LogicModule logic;
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
        if (!Globals.customPort) {
            Globals.SERVER_PORT = 25565 + (int) (logic.getSelectedRoom() / 3);
            System.out.println(Globals.SERVER_PORT);
        }

        this.client = new Client(Globals.PACKET_MAX_SIZE * 200, Globals.PACKET_MAX_SIZE);
        PacketSender.setClient(this.client);
        PacketHandler.setGameClient(this);
        this.client.start();

        Kryo kyro = this.client.getKryo();
        kyro.register(byte[].class);
        this.receiver = new PacketReceiver();
        this.client.addListener(new Listener.ThreadedListener(this.receiver));

        System.out.println("Connecting to " + Globals.SERVER_ADDRESS);
        try {
            client.connect(5000, Globals.SERVER_ADDRESS, Globals.SERVER_PORT);
            PacketSender.sendPlayerLogin(logic.getSelectedRoom(), logic.getSelectedChar());
        } catch (IOException ex) {
            client.close();
            if (logic.getScreen() instanceof ScreenServerList) {
                ((ScreenServerList) logic.getScreen()).setStatus(ScreenServerList.STATUS_UNKNOWNHOST);
            }
        }

    }

    public void shutdownClient() {
        client.close();
        if (this.receiver != null) {
            this.receiver.shutdown();
            this.receiver = null;
        }
    }

    public void receiveLogin(final byte[] data) {
        byte attempts = 0;
        this.loggedIn = false;

        byte loginResponse = data[1];
        switch (loginResponse) {
            case Globals.LOGIN_SUCCESS:
                if (data[2] != Globals.GAME_MAJOR_VERSION || data[3] != Globals.GAME_MINOR_VERSION) {
                    shutdownClient();
                    if (logic.getScreen() instanceof ScreenServerList) {
                        ((ScreenServerList) logic.getScreen()).setStatus(ScreenServerList.STATUS_WRONGVERSION);
                    }
                    return;
                }
                break;
            case Globals.LOGIN_FAIL_UID_IN_ROOM:
                shutdownClient();
                if (logic.getScreen() instanceof ScreenServerList) {
                    ((ScreenServerList) logic.getScreen()).setStatus(ScreenServerList.STATUS_UIDINROOM);
                }
                return;
            case Globals.LOGIN_FAIL_FULL_ROOM:
                shutdownClient();
                if (logic.getScreen() instanceof ScreenServerList) {
                    ((ScreenServerList) logic.getScreen()).setStatus(ScreenServerList.STATUS_FULLROOM);
                }
                return;
            case Globals.LOGIN_FAIL_OUTSIDE_LEVEL_RANGE:
                shutdownClient();
                if (logic.getScreen() instanceof ScreenServerList) {
                    ((ScreenServerList) logic.getScreen()).setStatus(ScreenServerList.STATUS_OUTSIDELEVEL);
                }
                return;
            default:
                shutdownClient();
                if (logic.getScreen() instanceof ScreenServerList) {
                    ((ScreenServerList) logic.getScreen()).setStatus((byte) -1);
                }
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
            shutdownClient();
            if (logic.getScreen() instanceof ScreenServerList) {
                ((ScreenServerList) logic.getScreen()).setStatus(ScreenServerList.STATUS_FAILEDCONNECT);
            }
        }
    }

    public void receiveCreate(final byte mapID, final byte key, final byte size) {
        synchronized (this) {
            this.loggedIn = true;
            notify();
        }

        final ScreenLoading loading = new ScreenLoading(key, this);
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
            ScreenIngame ingameScreen = new ScreenIngame(key, size, loading.getLoadedMap(), this);
            while (!this.dataQueue.isEmpty()) {
                ingameScreen.queueData(this.dataQueue.poll());
            }
            ingameScreen.update();
            logic.setScreen(ingameScreen);
            sendGetAll(key);
        } catch (final Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            Particle.unloadParticles();
            logic.disconnect();
            sendDisconnect(key);
            logic.returnMenu();
        }
    }

    public void sendGetPing(final byte k, final byte pID) {
        PacketSender.sendGetPing(logic.getSelectedRoom(), k, pID);
    }

    public void sendGetAll(final byte k) {
        PacketSender.sendGetAll(logic.getSelectedRoom(), k);
    }

    public void sendSetMobType(final byte k) {
        PacketSender.sendSetMobType(logic.getSelectedRoom(), k);
    }

    public void sendGetMobStat(final byte k, final byte s) {
        PacketSender.sendGetMobStat(logic.getSelectedRoom(), k, s);
    }

    public void sendGetName(final byte k) {
        PacketSender.sendGetName(logic.getSelectedRoom(), k);
    }

    public void sendGetStat(final byte k, final byte s) {
        PacketSender.sendGetStat(logic.getSelectedRoom(), k, s);
    }

    public void sendGetEquip(final byte k) {
        PacketSender.sendGetEquip(logic.getSelectedRoom(), k);
    }

    public void sendDisconnect(final byte k) {
        PacketSender.sendDisconnect(logic.getSelectedRoom(), k);
    }

    public void sendUseSkill(final byte k, final byte sc) {
        PacketSender.sendUseSkill(logic.getSelectedRoom(), k, sc);
    }

    public void sendMoveKey(final byte k, final byte dir, final boolean b) {
        PacketSender.sendMove(logic.getSelectedRoom(), k, dir, b);
    }

    public void setPing(final byte data) {
        if (logic.getScreen() instanceof ScreenIngame) {
            ((ScreenIngame) logic.getScreen()).setPing(data);
        }
    }

    public void queueData(final byte[] data) {
        if (logic.getScreen() instanceof ScreenIngame) {
            ((ScreenIngame) logic.getScreen()).queueData(data);
        } else {
            this.dataQueue.add(data);
        }
    }

    public void disconnect() {
        if (logic.getScreen() instanceof ScreenIngame) {
            ((ScreenIngame) logic.getScreen()).disconnect();
        }
    }
}
