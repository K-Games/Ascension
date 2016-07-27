package blockfighter.server.net;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class PacketSender implements Runnable {

    private static LogicModule[] rooms;
    private static Server server;

    public static void sendParticle(final byte room, final byte particleID, final double x, final double y, final byte facing) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_PARTICLE_EFFECT;
        bytes[1] = particleID;

        final byte[] posXInt = Globals.intToBytes((int) x);
        System.arraycopy(posXInt, 0, bytes, 2, posXInt.length);

        final byte[] posYInt = Globals.intToBytes((int) y);
        System.arraycopy(posYInt, 0, bytes, 6, posYInt.length);

        bytes[10] = facing;
        sendAll(bytes, room);
    }

    public static void sendParticle(final byte room, final byte particleID, final double x, final double y) {
        sendParticle(room, particleID, x, y, Globals.RIGHT);
    }

    public static void sendParticle(final byte room, final byte particleID, final byte key) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PARTICLE_EFFECT;
        bytes[1] = particleID;
        bytes[2] = key;
        sendAll(bytes, room);
    }

    public static void sendParticle(final byte room, final byte particleID, final byte key, final byte facing) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PARTICLE_EFFECT;
        bytes[1] = particleID;
        bytes[2] = facing;
        bytes[3] = key;
        sendAll(bytes, room);
    }

    public static void sendSFX(final byte room, final byte sfxID, final double soundX, final double soundY) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_SOUND_EFFECT;
        bytes[1] = sfxID;
        final byte[] posXInt = Globals.intToBytes((int) soundX);
        System.arraycopy(posXInt, 0, bytes, 2, posXInt.length);
        final byte[] posYInt = Globals.intToBytes((int) soundY);
        System.arraycopy(posYInt, 0, bytes, 6, posYInt.length);
        sendAll(bytes, room);
    }
    private int dataSent = 0;
    private static final ConcurrentLinkedQueue<GamePacket> OUT_PACKET_QUEUE = new ConcurrentLinkedQueue<>();

    private static ExecutorService SENDER_THREAD_POOL;

    public static void init() {
        SENDER_THREAD_POOL = new ThreadPoolExecutor(1, Globals.SERVER_PACKETSENDER_THREADS,
                10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new BasicThreadFactory.Builder()
                .namingPattern("PacketSender-%d")
                .daemon(true)
                .priority(Thread.MAX_PRIORITY)
                .build());
    }

    @Override
    public void run() {
        while (!OUT_PACKET_QUEUE.isEmpty()) {
            final GamePacket p = OUT_PACKET_QUEUE.poll();
            if (p != null) {
                SENDER_THREAD_POOL.execute(() -> {
                    if (p.getPlayer() != null) {
                        server.sendToTCP(p.getPlayer().getConnection().getID(), p.getData());
                    } else if (p.getConnection() != null) {
                        server.sendToTCP(p.getConnection().getID(), p.getData());
                    }
                });
            }
        }
    }

    public void resetByte() {
        this.dataSent = 0;
    }

    public int getBytes() {
        return this.dataSent;
    }

    public static void setLogic(final LogicModule[] l) {
        rooms = l;
    }

    public static void setServer(final Server s) {
        server = s;
    }

    public static void sendConnection(final byte[] data, final Connection c) {
        if (Globals.SERVER_BATCH_PACKETSEND) {
            OUT_PACKET_QUEUE.add(new GamePacket(data, c));
        } else {
            server.sendToTCP(c.getID(), data);
        }
    }

    public static void sendPlayer(final byte[] data, final Player p) {
        if (p.isConnected()) {
            try {
                if (Globals.SERVER_BATCH_PACKETSEND) {
                    OUT_PACKET_QUEUE.add(new GamePacket(data, p));
                } else {
                    server.sendToTCP(p.getConnection().getID(), data);
                }
            } catch (Exception e) {
            }
        }
    }

    public static void sendAll(final byte[] data, final byte room) {
        for (final Map.Entry<Byte, Player> pEntry : rooms[Globals.SERVER_ROOMNUM_TO_ROOMINDEX.get(room)].getPlayers().entrySet()) {
            sendPlayer(data, pEntry.getValue());
        }
//        server.sendToAllTCP(data);
    }

    public static void broadcastAllPlayersUpdate(final byte room) {
        for (final Map.Entry<Byte, Player> pEntry : rooms[room].getPlayers().entrySet()) {
            final Player player = pEntry.getValue();
            player.sendData();
        }
    }
}
