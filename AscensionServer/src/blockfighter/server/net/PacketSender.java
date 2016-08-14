package blockfighter.server.net;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import com.esotericsoftware.kryonet.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class PacketSender implements Runnable {

    private static LogicModule[] logic;

    public static void sendParticle(final byte roomNumber, final byte particleID, final double x, final double y, final byte facing) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_PARTICLE_EFFECT;
        bytes[1] = particleID;

        final byte[] posXInt = Globals.intToBytes((int) x);
        System.arraycopy(posXInt, 0, bytes, 2, posXInt.length);

        final byte[] posYInt = Globals.intToBytes((int) y);
        System.arraycopy(posYInt, 0, bytes, 6, posYInt.length);

        bytes[10] = facing;
        sendAll(bytes, roomNumber);
    }

    public static void sendParticle(final byte roomNumber, final byte particleID, final double x, final double y) {
        sendParticle(roomNumber, particleID, x, y, Globals.RIGHT);
    }

    public static void sendParticle(final byte roomNumber, final byte particleID, final byte key) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PARTICLE_EFFECT;
        bytes[1] = particleID;
        bytes[2] = key;
        sendAll(bytes, roomNumber);
    }

    public static void sendParticle(final byte roomNumber, final byte particleID, final byte key, final byte facing) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PARTICLE_EFFECT;
        bytes[1] = particleID;
        bytes[2] = facing;
        bytes[3] = key;
        sendAll(bytes, roomNumber);
    }

    public static void sendScreenShake(final Player player) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 1];
        bytes[0] = Globals.DATA_SCREEN_SHAKE;
        sendPlayer(bytes, player);
    }

    public static void sendSFX(final byte roomNumber, final byte sfxID, final double soundX, final double soundY) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_SOUND_EFFECT;
        bytes[1] = sfxID;
        final byte[] posXInt = Globals.intToBytes((int) soundX);
        System.arraycopy(posXInt, 0, bytes, 2, posXInt.length);
        final byte[] posYInt = Globals.intToBytes((int) soundY);
        System.arraycopy(posYInt, 0, bytes, 6, posYInt.length);
        sendAll(bytes, roomNumber);
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
            final GamePacket packet = OUT_PACKET_QUEUE.poll();
            if (packet != null) {
                SENDER_THREAD_POOL.execute(() -> {
                    if (packet.getPlayer() != null) {
                        if (packet.getPlayer().getConnection().getTcpWriteBufferSize() < Globals.PACKET_MAX_SIZE * Globals.PACKET_MAX_PER_CON * 0.75) {
                            packet.getPlayer().getConnection().sendTCP(packet.getData());
                        }
                    } else if (packet.getConnection() != null) {
                        if (packet.getConnection().getTcpWriteBufferSize() < Globals.PACKET_MAX_SIZE * Globals.PACKET_MAX_PER_CON * 0.75) {
                            packet.getConnection().sendTCP(packet.getData());
                        }
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
        logic = l;
    }

    public static void sendConnection(final byte[] data, final Connection c) {
        if (Globals.SERVER_BATCH_PACKETSEND) {
            OUT_PACKET_QUEUE.add(new GamePacket(data, c));
        } else if (c.getTcpWriteBufferSize() < Globals.PACKET_MAX_SIZE * Globals.PACKET_MAX_PER_CON * 0.75) {
            c.sendTCP(data);
        }
    }

    public static void sendPlayer(final byte[] data, final Player player) {
        if (player.isConnected()) {
            try {
                if (Globals.SERVER_BATCH_PACKETSEND) {
                    OUT_PACKET_QUEUE.add(new GamePacket(data, player));
                } else if (player.getConnection().getTcpWriteBufferSize() < Globals.PACKET_MAX_SIZE * Globals.PACKET_MAX_PER_CON * 0.75) {
                    player.getConnection().sendTCP(data);
                }
            } catch (Exception e) {
                Globals.logError(e.getStackTrace()[0].toString(), e, true);
            }
        }
    }

    public static void sendAll(final byte[] data, final byte roomNumber) {
        for (final Map.Entry<Byte, Player> pEntry : logic[Globals.SERVER_ROOMNUM_TO_ROOMINDEX.get(roomNumber)].getRoom().getPlayers().entrySet()) {
            sendPlayer(data, pEntry.getValue());
        }
    }

    public static void sendAllPlayerData(final byte roomNumber) {
        for (final Map.Entry<Byte, Player> pEntry : logic[Globals.SERVER_ROOMNUM_TO_ROOMINDEX.get(roomNumber)].getRoom().getPlayers().entrySet()) {
            final Player player = pEntry.getValue();
            player.sendData();
        }
    }
}
