package towerconquestperformancetest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;

public class TestGameClient {

    private Client client;
    private TestPacketReceiver receiver;
    private final TestLogicModule logic;
    private boolean loggedIn = false;

    private final String server;
    private final int port;

    public TestGameClient(final TestLogicModule lm, final String server, final int port) {
        this.logic = lm;
        this.server = server;
        this.port = port;
    }

    public void run() {
        if (this.receiver != null && this.receiver.isConnected()) {
            return;
        }
        this.client = new Client(Globals.PACKET_MAX_SIZE * 35, Globals.PACKET_MAX_SIZE);
        this.client.start();

        Kryo kyro = this.client.getKryo();
        kyro.register(byte[].class);
        this.receiver = new TestPacketReceiver(this);
        this.client.addListener(new Listener.ThreadedListener(this.receiver));

        System.out.println("Connecting to " + server + ":" + port + " with " + logic.getSelectedChar().getPlayerName());
        try {
            client.connect(5000, server, port);
            TestPacketSender.sendPlayerLogin(logic.getSelectedRoom(), logic.getSelectedChar(), this.client);
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
        byte attempts = 0;
        this.loggedIn = false;

        byte loginResponse = data[1];
        switch (loginResponse) {
            case Globals.LOGIN_SUCCESS:
                if (data[2] != Globals.GAME_MAJOR_VERSION || data[3] != Globals.GAME_MINOR_VERSION) {
                    shutdownClient();
                }
                break;
            default:
                shutdownClient();
                return;
        }

        while (!this.loggedIn && attempts < 5) {
            System.out.println("Attempting to login with " + this.logic.getSelectedChar().getPlayerName() + "." + (attempts + 1) + "/5");
            TestPacketSender.sendPlayerCreate(logic.getSelectedRoom(), logic.getSelectedChar(), this.client);
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
