package blockfighter.client.net;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.Main;
import blockfighter.client.screen.ScreenIngame;
import blockfighter.client.screen.ScreenLoading;
import blockfighter.client.screen.ScreenServerList;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Ken Kwan
 */
public class PacketReceiver extends Thread {

    private static LogicModule logic;
    private DatagramSocket socket = null;
    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(5);
    private boolean isConnected = true, settingStatus = true;

    public PacketReceiver(final DatagramSocket s) {
        this.socket = s;
    }

    public static void init() {
        logic = Main.getLogicModule();
    }

    @Override
    public void run() {
        try {
            while (true) {
                final byte[] request = new byte[Globals.PACKET_MAX_SIZE];
                final DatagramPacket p = new DatagramPacket(request, request.length);
                this.socket.receive(p);
                THREAD_POOL.execute(new PacketHandler(p));
            }
        } catch (final SocketTimeoutException e) {
            if (this.settingStatus && logic.getScreen() instanceof ScreenServerList) {
                ((ScreenServerList) logic.getScreen()).setStatus(ScreenServerList.STATUS_FAILEDCONNECT);
            }
        } catch (final SocketException e) {
            if (this.settingStatus && logic.getScreen() instanceof ScreenServerList) {
                ((ScreenServerList) logic.getScreen()).setStatus(ScreenServerList.STATUS_SOCKETCLOSED);
            }
        } catch (final IOException ex) {
        }
        System.out.println("Receiver End");
        if (logic.getScreen() instanceof ScreenIngame || logic.getScreen() instanceof ScreenLoading) {
            logic.returnMenu();
        }
        this.isConnected = false;
    }

    public void shutdown() {
        this.socket.close();
    }

    public void shutdown(boolean settingStatus) {
        this.settingStatus = settingStatus;
        this.socket.close();
    }

    public boolean isConnected() {
        return this.isConnected;
    }
}
