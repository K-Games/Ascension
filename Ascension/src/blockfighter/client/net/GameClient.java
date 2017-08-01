package blockfighter.client.net;

import blockfighter.client.LogicModule;
import blockfighter.client.screen.ScreenServerList;
import blockfighter.shared.AscensionSerialization;
import blockfighter.shared.Globals;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GameClient implements Runnable {

    private static Client client;
    private final LogicModule logic;

    private final ConcurrentLinkedQueue<byte[]> dataQueue = new ConcurrentLinkedQueue<>();

    public GameClient(final LogicModule lm, final String server) {
        this.logic = lm;
        Globals.SERVER_ADDRESS = server;
    }

    @Override
    public void run() {

        if (client == null) {
            client = new Client(Globals.PACKET_MAX_SIZE * 800, Globals.PACKET_MAX_SIZE, new AscensionSerialization());
            client.setTimeout(3000);
            client.setKeepAliveTCP(500);
            PacketHandler.setGameClient(this);
            client.start();
            client.addListener(new Listener.ThreadedListener(new PacketReceiver()));
        } else {
            shutdownClient(ScreenServerList.STATUS_CONNECTING);
        }

        if (logic.getScreen() instanceof ScreenServerList) {
            ((ScreenServerList) logic.getScreen()).setStatus(ScreenServerList.STATUS_CONNECTING);
            logic.startLoginAttemptTimeout();
        }

        try {
            if (!client.isConnected()) {
                InetAddress address = InetAddress.getByName(Globals.SERVER_ADDRESS);
                Globals.log(GameClient.class, "Connecting to " + address, Globals.LOG_TYPE_DATA);
                if ((Boolean) Globals.ServerConfig.UDP_MODE.getValue()) {
                    client.connect(3000, address, (Integer) Globals.ServerConfig.TCP_PORT.getValue(), (Integer) Globals.ServerConfig.UDP_PORT.getValue());
                } else {
                    client.connect(3000, address, (Integer) Globals.ServerConfig.TCP_PORT.getValue());
                }
                PacketSender.sendPlayerLogin(logic.getSelectedRoom(), logic.getSelectedChar());
            }
        } catch (IOException ex) {
            client.close();
            if (logic.getScreen() instanceof ScreenServerList) {
                ((ScreenServerList) logic.getScreen()).setStatus(ScreenServerList.STATUS_FAILEDCONNECT);
            }
        }

    }

    public void shutdownClient(final byte status) {
        client.close();
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
