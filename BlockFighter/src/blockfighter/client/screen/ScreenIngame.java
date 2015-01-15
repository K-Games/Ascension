package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.entities.Player;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.particles.ParticleKnock;
import blockfighter.client.net.PacketSender;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author Ken
 */
public class ScreenIngame extends Screen {

    //Ingame Data
    private ConcurrentLinkedQueue<Byte> playersAddQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> playersMoveQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> playersFacingQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> playersStateQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> particlesQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> particlesRemoveQueue = new ConcurrentLinkedQueue<>();

    private ConcurrentHashMap<Byte, Player> players;
    private ConcurrentHashMap<Integer, Particle> particles = new ConcurrentHashMap<>(500);
    private byte myKey = -1;

    private long pingTime = 0;
    private int ping = 0;
    private byte pID = 0;
    private PacketSender sender = null;

    private long lastAttackTime;
    private double lastUpdateTime = System.nanoTime();
    private double lastRequestTime = lastUpdateTime;
    private double lastQueueTime = lastUpdateTime;
    private double lastPingTime = lastUpdateTime;

    private long last100 = System.currentTimeMillis();
    private boolean[] keyDownMove = {false, false, false, false};

    private LogicModule logic;

    public ScreenIngame(LogicModule l, byte i, byte numPlayer, PacketSender s) {
        logic = l;
        myKey = i;
        players = new ConcurrentHashMap<>(numPlayer);
        sender = s;
    }

    @Override
    public void update() {
        double now = System.nanoTime(); //Get time now
        long nowMs = System.currentTimeMillis();

        if (now - lastQueueTime >= Globals.QUEUES_UPDATE) {
            processQueues();
            lastQueueTime = now;
        }

        if (lastAttackTime > 0 && nowMs - last100 >= 100) {
            lastAttackTime -= 100;
            last100 = nowMs;
        }

        if (now - lastUpdateTime >= Globals.LOGIC_UPDATE) {
            sender.sendMove(myKey, Globals.UP, keyDownMove[Globals.UP]);
            sender.sendMove(myKey, Globals.DOWN, keyDownMove[Globals.DOWN]);
            sender.sendMove(myKey, Globals.LEFT, keyDownMove[Globals.LEFT]);
            sender.sendMove(myKey, Globals.RIGHT, keyDownMove[Globals.RIGHT]);

            updateParticles(particles);
            //updatePlayers();
            lastUpdateTime = now;
        }

        if (now - lastRequestTime >= Globals.REQUESTALL_UPDATE) {
            sender.sendGetAll();
            lastRequestTime = now;
        }

        if (now - lastPingTime >= Globals.PING_UPDATE) {
            pID = (byte) (Math.random() * 256);
            pingTime = System.currentTimeMillis();
            sender.sendGetPing(pID);
            lastPingTime = now;
        }

        //Yield until something is happening
        while (now - lastQueueTime < Globals.QUEUES_UPDATE
                && now - lastPingTime < Globals.PING_UPDATE
                && now - lastRequestTime < Globals.REQUESTALL_UPDATE
                && now - lastUpdateTime < Globals.LOGIC_UPDATE) {
            Thread.yield();
            now = System.nanoTime();
        }
    }

    private void updatePlayers() {
        for (Map.Entry<Byte, Player> pEntry : players.entrySet()) {
            threadPool.execute(pEntry.getValue());
        }
        for (Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
            try {
                pEntry.getValue().join();
            } catch (InterruptedException ex) {
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform resetForm = g2d.getTransform();

        if (players != null && myKey != -1 && players.get(myKey) != null) {
            ((Graphics2D) g).translate(640.0 - players.get(myKey).getX(), 550.0 - players.get(myKey).getY());
        }

        if (players != null) {
            for (Map.Entry<Byte, Player> pEntry : players.entrySet()) {
                pEntry.getValue().draw(g);
            }
        }

        for (Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
            pEntry.getValue().draw(g);
        }

        g.drawRect(0, 620, 5000, 30);
        g.drawRect(200, 400, 300, 30);
        g.drawRect(600, 180, 300, 30);

        ((Graphics2D) g).setTransform(resetForm);

        //BufferedImage hud = Globals.HUD[0];
        //g.drawImage(hud, Globals.WINDOW_WIDTH / 2 - hud.getWidth() / 2, Globals.WINDOW_HEIGHT - hud.getHeight(), null);
        g.drawString("Ping: " + ping, 1200, 40);
    }

    @Override
    public ConcurrentHashMap<Integer, Particle> getParticles() {
        return particles;
    }

    private void processQueues() {
        while (players != null && !playersAddQueue.isEmpty()) {
            players.put(playersAddQueue.remove(), new Player(0, 0));
        }

        while (players != null && !playersMoveQueue.isEmpty()) {
            byte[] data = playersMoveQueue.remove();
            byte key = data[1];
            int x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
            int y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
            if (players.containsKey(key)) {
                players.get(key).setPos(x, y);
            } else {
                players.put(key, new Player(x, y));
            }
        }

        while (players != null && !playersFacingQueue.isEmpty()) {
            byte[] data = playersFacingQueue.remove();
            byte key = data[1];
            byte facing = data[2];
            if (players.containsKey(key)) {
                players.get(key).setFacing(facing);
            }
        }

        while (players != null && !playersStateQueue.isEmpty()) {
            byte[] data = playersStateQueue.remove();
            byte key = data[1];
            if (players.containsKey(key)) {
                byte state = data[2];
                byte frame = data[3];
                players.get(key).setState(state);
                players.get(key).setFrame(frame);
            }
        }

        while (!particlesQueue.isEmpty()) {
            byte[] data = particlesQueue.remove();
            int key = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
            byte particleID = data[5];
            int x = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
            int y = Globals.bytesToInt(Arrays.copyOfRange(data, 10, 14));
            particles.put(key, new ParticleKnock(logic, key, x, y, 500));
        }

        while (!particlesRemoveQueue.isEmpty()) {
            byte[] data = particlesRemoveQueue.remove();
            int key = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
            particles.remove(key);
        }
    }

    public void queueAddPlayer(byte key) {
        playersAddQueue.add(key);
    }

    public void queueSetPlayerPos(byte[] data) {
        playersMoveQueue.add(data);
    }

    public void queueSetPlayerFacing(byte[] data) {
        playersFacingQueue.add(data);
    }

    public void queueSetPlayerState(byte[] data) {
        playersStateQueue.add(data);
    }

    public void queueParticleEffect(byte[] data) {
        particlesQueue.add(data);
    }

    public void queueParticleRemove(byte[] data) {
        particlesRemoveQueue.add(data);
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
        lastAttackTime = 500;
    }

    public boolean canAttack() {
        return lastAttackTime <= 0;
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
