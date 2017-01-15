package performancetest;

import blockfighter.shared.AscensionSerialization;
import blockfighter.shared.Globals;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;

public class TestGameClient {

    private Client client;
    private TestPacketReceiver receiver;
    private final TestLogicModule logic;
    private boolean loggedIn = false;

    private final String server;
    private final int tcpPort, udpPort;

    public TestGameClient(final TestLogicModule lm, final String server, final int tcpport, final int udpport) {
        this.logic = lm;
        this.server = server;
        this.tcpPort = tcpport;
        this.udpPort = udpport;
    }

    public void run() {
        if (this.receiver != null && this.receiver.isConnected()) {
            return;
        }
        this.client = new Client(Globals.PACKET_MAX_SIZE * 5, Globals.PACKET_MAX_SIZE, new AscensionSerialization());
        this.client.start();

        this.receiver = new TestPacketReceiver(this);
        this.client.addListener(new Listener.ThreadedListener(this.receiver));

        System.out.println("Connecting to " + server + ":" + tcpPort + " with " + logic.getSelectedChar().getPlayerName());
        try {
            if (Globals.UDP_MODE) {
                client.connect(5000, server, tcpPort, udpPort);
            } else {
                client.connect(5000, server, tcpPort);
            }
            TestPacketSender.sendPlayerLogin(logic.getSelectedChar(), this.client);
        } catch (IOException ex) {
            client.close();
        }

    }

    public void shutdownClient() {
        client.close();
        if (this.receiver != null) {
            this.receiver = null;
        }
    }

    public void receiveLogin(final byte[] data) {
        this.loggedIn = false;

        byte loginResponse = data[1];
        switch (loginResponse) {
            case Globals.LOGIN_SUCCESS:
                logic.setSelectedRoom(data[5]);
                break;
            default:
                shutdownClient();
                return;
        }

        System.out.println("Attempting to login with " + this.logic.getSelectedChar().getPlayerName() + ".");
        TestPacketSender.sendPlayerCreate(logic.getSelectedRoom(), logic.getSelectedChar(), this.client);
        synchronized (this) {
            try {
                this.wait(3000);
            } catch (final InterruptedException e) {
                return;
            }
        }

        if (!this.loggedIn) {
            System.out.println("Failed to login with " + this.logic.getSelectedChar().getPlayerName());
            shutdownClient();
        }
    }

    public void receiveCreate(final byte mapID, final byte key, final byte size) {
        synchronized (this) {
            this.loggedIn = true;
            notify();
        }
        logic.setKey(key);
    }

    public void sendDisconnect(final byte k) {
        TestPacketSender.sendDisconnect(logic.getSelectedRoom(), k, this.client);
    }

    public void sendMoveKey(final byte k, final byte dir, final boolean b) {
        TestPacketSender.sendMove(logic.getSelectedRoom(), k, dir, b, this.client);
    }

    public void sendGetPing() {
        this.client.updateReturnTripTime();
    }

    public int getPing() {
        return this.client.getReturnTripTime();
    }
}
