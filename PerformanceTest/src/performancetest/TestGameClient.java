package performancetest;

import blockfighter.shared.AscensionSerialization;
import blockfighter.shared.Globals;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;

public class TestGameClient implements Runnable {

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

    @Override
    public void run() {
        if (this.receiver != null && this.receiver.isConnected()) {
            return;
        }
        this.client = new Client(Globals.PACKET_MAX_SIZE * 5, Globals.PACKET_MAX_SIZE, new AscensionSerialization());
        //this.client.start();
        this.client.setTimeout(5000);
        this.client.setKeepAliveTCP(3000);

        this.receiver = new TestPacketReceiver(this);
        this.client.addListener(new Listener.ThreadedListener(this.receiver));

        System.out.println("Connecting to " + server + ":" + tcpPort + " with " + logic.getSelectedChar().getPlayerName());
        try {
            if ((Boolean) Globals.ServerConfig.UDP_MODE.getValue()) {
                client.connect(3000, server, tcpPort, udpPort);
            } else {
                client.connect(3000, server, tcpPort);
            }
            TestPacketSender.sendPlayerLogin(logic.getSelectedChar(), this.client);
        } catch (IOException ex) {
            shutdownClient();
        }
    }

    public void update() throws Exception {
        if (this.client != null) {
            this.client.update(250);
        }
    }

    public void shutdownClient() {
        System.out.println("Disconnected with " + logic.getSelectedChar().getPlayerName());
        client.close();
        this.client = null;
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
        TestPacketSender.sendPlayerCreate(logic.getSelectedRoom(), logic.getSelectedChar(), this.client);
    }

    public void receiveCreate(final byte mapID, final byte key, final byte size) {
        System.out.println("Logged in with " + this.logic.getSelectedChar().getPlayerName() + ".");
        this.loggedIn = true;
        logic.setKey(key);
    }

    public void sendDisconnect(final byte k) {
        TestPacketSender.sendDisconnect(logic.getSelectedRoom(), k, this.client);
    }

    public void sendMoveKey(final byte k, final byte dir, final boolean b) {
        TestPacketSender.sendMove(logic.getSelectedRoom(), k, dir, b, this.client);
    }

    public void sendUseSkill(final byte k, final byte skillCode) {
        TestPacketSender.sendUseSkill(logic.getSelectedRoom(), k, skillCode, this.client);
    }

    public void sendGetPing() {
        this.client.updateReturnTripTime();
    }

    public int getPing() {
        return this.client.getReturnTripTime();
    }
}
