package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.SaveData;
import blockfighter.client.entities.Player;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.particles.ParticleKnock;
import blockfighter.client.entities.skills.Skill;
import blockfighter.client.net.PacketSender;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ken Kwan
 */
public class ScreenIngame extends Screen {

    private Rectangle2D.Double[] hotkeySlots = new Rectangle2D.Double[12];
    //Ingame Data
    private ConcurrentLinkedQueue<byte[]> dataQueue = new ConcurrentLinkedQueue<>();

    private ConcurrentHashMap<Byte, Player> players;
    private ConcurrentHashMap<Integer, Particle> particles = new ConcurrentHashMap<>(500);
    private byte myKey = -1;

    private long pingTime = 0;
    private int ping = 0;
    private byte pID = 0;
    private PacketSender sender = null;

    private long lastBytesTime;
    private double lastUpdateTime = 0;
    private double lastRequestTime = 50;
    private double lastQueueTime = 0;
    private double lastPingTime = 0;
    private double lastSendKeyTime = 0;

    private long last100 = 0;
    private boolean[] keyDownMove = {false, false, false, false};

    private LogicModule logic;
    private SaveData c;

    public ScreenIngame(LogicModule l, byte i, byte numPlayer, PacketSender s) {
        logic = l;
        myKey = i;
        c = logic.getSelectedChar();
        players = new ConcurrentHashMap<>(numPlayer);
        sender = s;
        for (int j = 0; j < hotkeySlots.length; j++) {
            hotkeySlots[j] = new Rectangle2D.Double(Globals.WINDOW_WIDTH / 2 - Globals.HUD[0].getWidth() / 2 + 10 + (j * 66), 656, 60, 60);
        }
    }

    @Override
    public void update() {
        double now = System.nanoTime(); //Get time now
        long nowMs = System.currentTimeMillis();

        if (now - lastQueueTime >= Globals.QUEUES_UPDATE) {
            processQueues();
            lastQueueTime = now;
        }

        if (lastBytesTime > 0 && nowMs - last100 >= 100) {
            lastBytesTime -= 100;
            last100 = nowMs;
        }
        if (now - lastSendKeyTime >= Globals.SEND_KEYDOWN_UPDATE) {
            sender.sendMove(logic.getSelectedRoom(), myKey, Globals.UP, keyDownMove[Globals.UP]);
            sender.sendMove(logic.getSelectedRoom(), myKey, Globals.DOWN, keyDownMove[Globals.DOWN]);
            sender.sendMove(logic.getSelectedRoom(), myKey, Globals.LEFT, keyDownMove[Globals.LEFT]);
            sender.sendMove(logic.getSelectedRoom(), myKey, Globals.RIGHT, keyDownMove[Globals.RIGHT]);
            lastSendKeyTime = now;
        }

        if (now - lastUpdateTime >= Globals.LOGIC_UPDATE) {
            updateParticles(particles);
            updatePlayers();
            lastUpdateTime = now;
        }

        if (now - lastRequestTime >= Globals.REQUESTALL_UPDATE) {
            sender.sendGetAll(logic.getSelectedRoom(), myKey);
            lastRequestTime = now;
        }

        if (now - lastPingTime >= Globals.PING_UPDATE) {
            pID = (byte) (Math.random() * 256);
            pingTime = System.currentTimeMillis();
            sender.sendGetPing(pID, logic.getSelectedRoom(), myKey);
            lastPingTime = now;
        }

        try {
            Thread.sleep(0, 1);
        } catch (InterruptedException ex) {
            Logger.getLogger(ScreenInventory.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void updatePlayers() {
        for (Map.Entry<Byte, Player> pEntry : players.entrySet()) {
            threadPool.execute(pEntry.getValue());
        }
        LinkedList<Byte> remove = new LinkedList<>();
        for (Map.Entry<Byte, Player> pEntry : players.entrySet()) {
            try {
                pEntry.getValue().join();
                if (pEntry.getValue().isDisconnected()) {
                    remove.add(pEntry.getKey());
                }
            } catch (InterruptedException ex) {
            }
        }
        while (!remove.isEmpty()) {
            players.remove(remove.poll());
        }
    }

    @Override
    public void draw(Graphics2D g) {
        AffineTransform resetForm = g.getTransform();

        if (players != null && myKey != -1 && players.get(myKey) != null) {
            g.translate(640.0 - players.get(myKey).getX(), 500.0 - players.get(myKey).getY());
        }

        if (players != null) {
            for (Map.Entry<Byte, Player> pEntry : players.entrySet()) {
                pEntry.getValue().draw(g);
            }
        }

        for (Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
            pEntry.getValue().draw(g);
        }
        g.setColor(Color.BLACK);
        g.drawRect(0, 620, 5000, 30);
        g.drawRect(200, 400, 300, 30);
        g.drawRect(600, 180, 300, 30);

        g.setTransform(resetForm);

        BufferedImage hud = Globals.HUD[0];
        g.drawImage(hud, Globals.WINDOW_WIDTH / 2 - hud.getWidth() / 2, Globals.WINDOW_HEIGHT - hud.getHeight(), null);
        Skill[] hotkey = c.getHotkeys();
        for (int j = 0; j < hotkeySlots.length; j++) {
            if (hotkey[j] != null) {
                hotkey[j].draw(g, (int) hotkeySlots[j].x, (int) hotkeySlots[j].y);
                g.setColor(new Color(100,100,100,45));
                int cdHeight = (int) ((hotkey[j].getCooldown() / hotkey[j].getMaxCooldown())*hotkeySlots[j].height);
                g.fillRect((int) hotkeySlots[j].x, (int) (hotkeySlots[j].y + hotkeySlots[j].height - cdHeight), (int) hotkeySlots[j].width, cdHeight);
            }
            g.setColor(Color.WHITE);
        }
        g.setFont(Globals.ARIAL_12PT);
        g.drawString("Ping: " + ping, 1200, 40);
    }

    @Override
    public ConcurrentHashMap<Integer, Particle> getParticles() {
        return particles;
    }

    private void processQueues() {
        while (players != null && !dataQueue.isEmpty()) {
            byte[] data = dataQueue.remove();
            byte dataType = data[0];
            switch (dataType) {
                case Globals.DATA_PLAYER_SET_POS:
                    dataPlayerSetPos(data);
                    break;
                case Globals.DATA_PLAYER_SET_FACING:
                    dataPlayerSetFacing(data);
                    break;
                case Globals.DATA_PLAYER_SET_STATE:
                    dataPlayerSetState(data);
                    break;
                case Globals.DATA_PARTICLE_EFFECT:
                    dataParticleEffect(data);
                    break;
                case Globals.DATA_PARTICLE_REMOVE:
                    dataParticleRemove(data);
                    break;
                case Globals.DATA_PLAYER_DISCONNECT:
                    dataPlayerDisconnect(data);
                    break;
                case Globals.DATA_PLAYER_GET_NAME:
                    dataPlayerGetName(data);
                    break;
                case Globals.DATA_PLAYER_GET_STAT:
                    dataPlayerGetLevel(data);
                    break;
                case Globals.DATA_PLAYER_GET_EQUIP:
                    dataPlayerGetEquip(data);
                    break;
            }
        }
    }

    private void dataPlayerGetEquip(byte[] data) {
        byte key = data[1];
        if (players.containsKey(key)) {
            for (byte i = 0; i < Globals.NUM_EQUIP_SLOTS; i++) {
                byte[] temp = new byte[4];
                System.arraycopy(data, i * 4 + 2, temp, 0, temp.length);
                players.get(key).setEquip(i, Globals.bytesToInt(temp));
            }
        }
    }

    private void dataPlayerGetLevel(byte[] data) {
        byte key = data[1];

        if (players.containsKey(key)) {
            byte stat = data[2];
            int amount = Globals.bytesToInt(Arrays.copyOfRange(data, 3, 7));
            players.get(key).setStat(stat, amount);
        }
    }

    private void dataPlayerSetPos(byte[] data) {
        byte key = data[1];
        int x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
        int y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
        if (players.containsKey(key)) {
            players.get(key).setPos(x, y);
        } else {
            players.put(key, new Player(x, y, logic, key));
        }
    }

    private void dataPlayerSetFacing(byte[] data) {
        byte key = data[1];
        byte facing = data[2];
        if (players.containsKey(key)) {
            players.get(key).setFacing(facing);
        }
    }

    private void dataPlayerSetState(byte[] data) {
        byte key = data[1];
        if (players.containsKey(key)) {
            byte state = data[2];
            byte frame = data[3];
            players.get(key).setState(state);
            players.get(key).setFrame(frame);
        }
    }

    private void dataPlayerDisconnect(byte[] data) {
        byte key = data[1];
        if (players.containsKey(key)) {
            players.remove(key);
        }
    }

    private void dataParticleEffect(byte[] data) {
        int key = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
        byte particleID = data[5];
        int x = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
        int y = Globals.bytesToInt(Arrays.copyOfRange(data, 10, 14));
        if (particles.containsKey(key)) {
            int i = 0;
            while (particles.containsKey(i)) {
                i++;
            }
            Particle temp = particles.remove(key);
            if (temp != null) {
                particles.put(i, temp);
            }
        }
        particles.put(key, new ParticleKnock(logic, key, x, y, 500));
    }

    private void dataParticleRemove(byte[] data) {
        int key = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
        if (particles.containsKey(key)) {
            particles.remove(key);
        }
    }

    private void dataPlayerGetName(byte[] data) {
        byte key = data[1];
        if (players.containsKey(key)) {
            byte[] temp = new byte[Globals.MAX_NAME_LENGTH];
            System.arraycopy(data, 2, temp, 0, temp.length);

            players.get(key).setPlayerName(new String(temp, StandardCharsets.UTF_8).trim());
        }
    }

    public void queueData(byte[] data) {
        dataQueue.add(data);
    }

    public void disconnect() {
        logic.sendDisconnect(myKey);
    }

    public void setPing(byte rID) {
        if (rID != pID) {
            return;
        }
        ping = (int) (System.currentTimeMillis() - pingTime);
        if (ping >= 1000) {
            ping = 9999;
        }
    }

    public void attack() {
        lastBytesTime = 10;
    }

    public boolean canAttack() {
        return true;
    }

    public void setKeyDown(int direction, boolean move) {
        keyDownMove[direction] = move;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                setKeyDown(Globals.UP, true);
                break;
            case KeyEvent.VK_DOWN:
                setKeyDown(Globals.DOWN, true);
                break;
            case KeyEvent.VK_LEFT:
                setKeyDown(Globals.LEFT, true);
                break;
            case KeyEvent.VK_RIGHT:
                setKeyDown(Globals.RIGHT, true);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                setKeyDown(Globals.UP, false);
                break;
            case KeyEvent.VK_DOWN:
                setKeyDown(Globals.DOWN, false);
                break;
            case KeyEvent.VK_LEFT:
                setKeyDown(Globals.LEFT, false);
                break;
            case KeyEvent.VK_RIGHT:
                setKeyDown(Globals.RIGHT, false);
                break;
            case KeyEvent.VK_A:
                if (canAttack()) {
                    logic.sendAction(myKey);
                    attack();
                }
                break;
            case KeyEvent.VK_ESCAPE:
                logic.sendDisconnect(myKey);
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

}
