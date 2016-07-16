package towerconquestperformancetest;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class TestLogicModule extends Thread {

    private byte selectedRoom = 0;
    private TestPacketReceiver receiver = null;
    private TestSaveData selectedChar;
    private TestPacketSender ps = new TestPacketSender();
    private TestPacketHandler ph;
    private boolean loggedIn = false;
    private byte myKey = -1;

    public TestLogicModule() {
    }

    public void setPH(TestPacketHandler ph) {
        this.ph = ph;
    }

    @Override
    public void run() {
        if (this.loggedIn) {
            sendMoveKey(this.myKey, Globals.UP, true);
        }
    }

    public void sendMoveKey(final byte k, final byte dir, final boolean b) {
        ps.sendMove(this.selectedRoom, k, dir, b);
    }

    public void sendLogin(final String server, final int port, final byte r, final byte num) {
        int lvl = r * 10 + 1;
        this.selectedChar = new TestSaveData("Tester" + r + "-" + num);
        this.selectedChar.newCharacter(lvl);
        sendLogin(server, r, port);
    }

    public void sendLogin(final String server, final byte r, final int port) {
        this.selectedRoom = r;

        final Thread send = new Thread() {
            @Override
            public void run() {
                if (receiver != null && receiver.isConnected()) {
                    return;
                }
                try {
                    final DatagramSocket socket = new DatagramSocket();
                    socket.connect(InetAddress.getByName(server), port);
                    socket.setSoTimeout(5000);
                    ps.setSocket(socket);
                    receiver = new TestPacketReceiver(socket, ph);
                    receiver.setName("Reciever");
                    receiver.setDaemon(true);
                    receiver.start();

                    ps.sendPlayerLogin(TestLogicModule.this.selectedRoom, TestLogicModule.this.selectedChar);
                    System.out.println("Sent Login for " + selectedChar.getPlayerName());
                } catch (final SocketException | UnknownHostException e) {

                }
            }
        };
        send.setName("HostResolver");
        send.setDaemon(true);
        send.start();
    }

    public void receiveLogin(final byte[] data) {
        byte attempts = 0;
        this.loggedIn = false;

        byte loginResponse = data[1];
        switch (loginResponse) {
            case Globals.LOGIN_SUCCESS:
                if (data[2] != Globals.GAME_MAJOR_VERSION || data[3] != Globals.GAME_MINOR_VERSION) {
                    System.out.println("Received Login for " + selectedChar.getPlayerName());
                    shutdownSocket();
                    return;
                }
                break;
            default:
                shutdownSocket();
                return;
        }

        while (!this.loggedIn && attempts < 5) {
            System.out.println("Creating char for " + selectedChar.getPlayerName() + " Room " + this.selectedRoom + " Attempt " + attempts);
            ps.sendPlayerCreate(this.selectedRoom, this.selectedChar);
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
            shutdownSocket();
            System.out.println("Failed login for " + selectedChar.getPlayerName() + " Room " + this.selectedRoom);
        }
    }

    public void receiveCreate(final byte mapID, final byte key, final byte size) {
        System.out.println("Received char for " + selectedChar.getPlayerName() + " Room " + this.selectedRoom);
        synchronized (this) {
            this.loggedIn = true;
            notify();
        }
        this.myKey = key;
    }

    private void shutdownSocket() {
        if (this.receiver != null) {
            this.receiver.shutdown();
            this.receiver = null;
        }
    }

}
