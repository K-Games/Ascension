package blockfighter.client;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.net.PacketReceiver;
import blockfighter.client.net.PacketSender;
import blockfighter.client.screen.Screen;
import blockfighter.client.screen.ScreenIngame;
import blockfighter.client.screen.ScreenSelectChar;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author ckwa290
 */
public class LogicModule extends Thread {

    private boolean isRunning = false;

    //Shared Data
    private PacketSender sender = null;
    private PacketReceiver receiver = null;

    private SaveData selectedChar;
    private byte selectedRoom = 0;
    private Screen screen = new ScreenSelectChar(this);
    private Screen lastMenu;

    public LogicModule() {
        isRunning = true;
    }

    @Override
    public void run() {
        while (isRunning) {
            screen.update();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void receiveLogin(byte key, byte size) {
        setScreen(new ScreenIngame(this, key, size, sender));
        ((ScreenIngame) screen).queueAddPlayer(key);
        sender.sendGetAll(selectedRoom);
    }

    public ConcurrentHashMap<Integer, Particle> getParticles() {
        return screen.getParticles();
    }

    public void sendAction(byte k) {
        sender.sendAction(selectedRoom, k);
    }

    public void sendLogin() {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.connect(InetAddress.getByName(Globals.SERVER_ADDRESS), Globals.SERVER_PORT);
            socket.setSoTimeout(5000);
            sender = new PacketSender(socket);
            receiver = new PacketReceiver(this, socket);
            receiver.start();
        } catch (SocketException | UnknownHostException e) {
        }
        sender.sendLogin(selectedRoom, selectedChar);
    }

    public Screen getScreen() {
        return screen;
    }

    public void setPing(byte data) {
        if (screen instanceof ScreenIngame) {
            ((ScreenIngame) screen).setPing(data);
        }
    }

    public void queueSetPlayerPos(byte[] data) {
        if (screen instanceof ScreenIngame) {
            ((ScreenIngame) screen).queueSetPlayerPos(data);
        }
    }

    public void queueSetPlayerFacing(byte[] data) {
        if (screen instanceof ScreenIngame) {
            ((ScreenIngame) screen).queueSetPlayerFacing(data);
        }
    }

    public void queueSetPlayerState(byte[] data) {
        if (screen instanceof ScreenIngame) {
            ((ScreenIngame) screen).queueSetPlayerState(data);
        }
    }

    public void queueParticleEffect(byte[] data) {
        if (screen instanceof ScreenIngame) {
            ((ScreenIngame) screen).queueParticleEffect(data);
        }
    }

    public void queueParticleRemove(byte[] data) {
        if (screen instanceof ScreenIngame) {
            ((ScreenIngame) screen).queueParticleEffect(data);
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
        screen = s;
        if (!(s instanceof ScreenIngame)) {
            lastMenu = screen;
        }
    }

    public void returnMenu() {
        screen = lastMenu;
    }
}
