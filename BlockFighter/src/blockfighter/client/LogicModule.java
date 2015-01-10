package blockfighter.client;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.Player;
import blockfighter.client.entities.particles.ParticleKnock;
import blockfighter.client.net.ConnectionThread;
import blockfighter.client.net.PacketSender;
import java.awt.HeadlessException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author ckwa290
 */
public class LogicModule extends Thread {

    private boolean isRunning = false;
    //Concurrent Queuing
    private ConcurrentLinkedQueue<Byte> playersAddQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> playersMoveQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> playersFacingQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> playersStateQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> particlesQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> particlesRemoveQueue = new ConcurrentLinkedQueue<>();

    private Player[] players = null;
    private ConcurrentHashMap<Integer, Particle> particles = new ConcurrentHashMap<>(150);
    private byte myIndex = -1;

    private PacketSender sender = null;
    private long pingTime = 0;
    private int ping = -1;
    private byte pID = 0;

    private long lastAttackTime;
    private double lastUpdateTime = System.nanoTime();
    private double lastRequestTime = lastUpdateTime;
    private double lastQueueTime = lastUpdateTime;
    private double lastPingTime = lastUpdateTime;
    private long last100 = System.currentTimeMillis();

    ExecutorService threadPool = Executors.newCachedThreadPool();

    private byte screen = Globals.SCREEN_CHAR_SELECT;

    boolean[] keyDownMove = {false, false, false, false};

    public LogicModule() {
        isRunning = true;
    }

    @Override
    public void run() {

        while (isRunning) {
            switch (screen) {
                case Globals.SCREEN_CHAR_SELECT:
                    runSelectMenu();
                    break;
                case Globals.SCREEN_INGAME:
                    runIngame();
                    break;
            }
        }
    }

    private void runSelectMenu() {

    }

    private void runIngame() {
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
            sender.sendMove(myIndex, Globals.UP, keyDownMove[Globals.UP]);
            sender.sendMove(myIndex, Globals.DOWN, keyDownMove[Globals.DOWN]);
            sender.sendMove(myIndex, Globals.LEFT, keyDownMove[Globals.LEFT]);
            sender.sendMove(myIndex, Globals.RIGHT, keyDownMove[Globals.RIGHT]);

            Iterator<Integer> partItr = particles.keySet().iterator();
            while (partItr.hasNext()) {
                Integer key = partItr.next();
                if (particles.get(key) != null) {
                    threadPool.execute(particles.get(key));
                }
            }

            partItr = particles.keySet().iterator();
            while (partItr.hasNext()) {
                Integer key = partItr.next();
                if (particles.get(key) != null) {
                    try {
                        particles.get(key).join();
                    } catch (InterruptedException ex) {
                    }
                }
            }
            removeParticles();

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

    private void removeParticles() {
        LinkedList<Integer> remove = new LinkedList<>();
        Iterator<Integer> partItr = particles.keySet().iterator();

        while (partItr.hasNext()) {
            Integer key = partItr.next();
            Particle p = particles.get(key);
            if (p.isExpired()) {
                remove.add(key);
            }
        }

        while (!remove.isEmpty()) {
            particles.remove(remove.pop());
        }
    }

    private void processQueues() {
        while (!playersAddQueue.isEmpty()) {
            if (players == null) {
                break;
            }
            players[playersAddQueue.remove()] = new Player(0, 0);
        }

        while (!playersMoveQueue.isEmpty()) {
            if (players == null) {
                break;
            }
            byte[] data = playersMoveQueue.remove();
            byte index = data[1];
            int x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
            int y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
            if (players[index] != null) {
                players[index].setPos(x, y);
            } else {
                players[index] = new Player(x, y);
            }
        }

        while (!playersFacingQueue.isEmpty()) {
            if (players == null) {
                break;
            }
            byte[] data = playersFacingQueue.remove();
            byte index = data[1];
            byte facing = data[2];
            if (players[index] != null) {
                players[index].setFacing(facing);
            }
        }

        while (!playersStateQueue.isEmpty()) {
            if (players == null) {
                break;
            }
            byte[] data = playersStateQueue.remove();
            byte index = data[1];
            if (players[index] != null) {
                byte state = data[2];
                byte frame = data[3];
                players[index].setState(state);
                players[index].setFrame(frame);
            }
        }

        while (!particlesQueue.isEmpty()) {
            byte[] data = particlesQueue.remove();
            int index = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
            byte particleID = data[5];
            int x = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
            int y = Globals.bytesToInt(Arrays.copyOfRange(data, 10, 14));
            particles.put(index, new ParticleKnock(this, index, x, y, 500));
        }

        while (!particlesRemoveQueue.isEmpty()) {
            byte[] data = particlesRemoveQueue.remove();
            int key = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
            particles.remove(key);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setKeyDown(int direction, boolean move) {
        keyDownMove[direction] = move;
    }

    public void receiveLogin(byte index) {
        myIndex = index;
        sender.sendGetAll();
        screen = Globals.SCREEN_INGAME;
    }

    public void attack() {
        lastAttackTime = 500;
    }

    public boolean canAttack() {
        return lastAttackTime <= 0;
    }

    public byte getMyIndex() {
        return myIndex;
    }

    public int getPing() {
        return ping;
    }

    public void setPlayersSize(byte size) {
        players = new Player[size];
    }

    public Player[] getPlayers() {
        return players;
    }

    public ConcurrentHashMap<Integer, Particle> getParticles() {
        return particles;
    }

    public void queueAddPlayer(byte index) {
        playersAddQueue.add(index);
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

    public void sendAction() {
        sender.sendAction(myIndex);
    }

    public void sendLogin() {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.connect(InetAddress.getByName(Globals.SERVER_ADDRESS), Globals.SERVER_PORT);
            sender = new PacketSender(socket);
            ConnectionThread responseThread = new ConnectionThread(this, socket);
            responseThread.start();
        } catch (SocketException | UnknownHostException | HeadlessException e) {
            e.printStackTrace();
        }
        sender.sendLogin();
    }

    public byte getScreen() {
        return screen;
    }
}
