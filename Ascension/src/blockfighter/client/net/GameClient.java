package blockfighter.client.net;

import blockfighter.client.LogicModule;
import blockfighter.client.screen.ScreenServerList;
import blockfighter.shared.Globals;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameClient extends Thread {

    private static Client client;
    private PacketReceiver receiver;
    private final LogicModule logic;

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
        client.setTimeout(3000);
        client.setKeepAliveTCP(500);
        PacketHandler.setGameClient(this);
        client.start();

        Kryo kyro = client.getKryo();
        kyro.register(byte[].class);
        this.receiver = new PacketReceiver();
        client.addListener(new Listener.ThreadedListener(this.receiver));

        Globals.log(GameClient.class, "Connecting to " + Globals.SERVER_ADDRESS, Globals.LOG_TYPE_DATA, true);
        try {
            client.connect(3000, Globals.SERVER_ADDRESS, Globals.SERVER_TCP_PORT);
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
