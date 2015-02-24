package blockfighter.client;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.net.PacketReceiver;
import blockfighter.client.net.PacketSender;
import blockfighter.client.screen.Screen;
import blockfighter.client.screen.ScreenIngame;
import blockfighter.client.screen.ScreenLoading;
import blockfighter.client.screen.ScreenSelectChar;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ken Kwan
 */
public class LogicModule extends Thread {

    //Shared Data
    private PacketSender sender = null;
    private PacketReceiver receiver = null;

    private SaveData selectedChar;
    private byte selectedRoom = 0;
    private Screen screen = new ScreenSelectChar();
    private Screen lastMenu;
    private SoundModule soundModule;

    public LogicModule(SoundModule s) {
        soundModule = s;
    }

    @Override
    public void run() {
        //soundModule.playBGM("theme.ogg");
        screen.update();
    }

    public void receiveLogin(byte mapID, byte key, byte size) {
        ScreenLoading loading = new ScreenLoading();
        setScreen(loading);
        try {
            loading.load(mapID);
            setScreen(new ScreenIngame(key, size, sender, loading.getLoadedMap()));
            sender.sendGetAll(selectedRoom, key);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            Particle.unloadParticles();
            disconnect();
            sender.sendDisconnect(selectedRoom, key);
            receiver.shutdown();
            returnMenu();
        }
    }

    public void sendGetName(byte k) {
        sender.sendGetName(selectedRoom, k);
    }

    public void sendGetStat(byte k, byte s) {
        sender.sendGetStat(selectedRoom, k, s);
    }

    public void sendGetEquip(byte k) {
        sender.sendGetEquip(selectedRoom, k);
    }

    public void sendDisconnect(byte k) {
        sender.sendDisconnect(selectedRoom, k);
    }

    public void sendUseSkill(byte k, byte sc) {
        sender.sendUseSkill(selectedRoom, k, sc);
    }

    public void sendLogin() {
        if (receiver != null && receiver.isConnected()) {
            return;
        }
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.connect(InetAddress.getByName(Globals.SERVER_ADDRESS), Globals.SERVER_PORT);
            socket.setSoTimeout(5000);
            sender = new PacketSender(socket);

            receiver = new PacketReceiver(socket);
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

    public void queueData(byte[] data) {
        if (screen instanceof ScreenIngame) {
            ((ScreenIngame) screen).queueData(data);
        }
    }

    public void disconnect() {
        if (selectedChar != null) {
            SaveData.saveData(selectedChar.getSaveNum(), selectedChar);
        }
        if (screen instanceof ScreenIngame) {
            ((ScreenIngame) screen).disconnect();
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
        screen.unload();
        screen = s;
        if (!(s instanceof ScreenIngame) && !(s instanceof ScreenLoading)) {
            lastMenu = screen;
        }
    }

    public void returnMenu() {
        screen = lastMenu;
        //soundModule.playBGM("theme.ogg");
    }

    public void playSound(String soundFile) {
        soundModule.playSound(soundFile);
    }

    public void playBGM(String bgmFile) {
        soundModule.playBGM(bgmFile);
    }

}
