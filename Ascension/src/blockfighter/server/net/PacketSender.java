package blockfighter.server.net;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.shared.Globals;
import com.esotericsoftware.kryonet.Connection;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class PacketSender implements Runnable {

    private static final ConcurrentHashMap<Connection, ConcurrentLinkedQueue<byte[]>> connPacketBatch = new ConcurrentHashMap<>();
    private static final ExecutorService PACKETSENDER_THREAD_POOL = Executors.newFixedThreadPool(Globals.SERVER_PACKETSENDER_THREADS,
            new BasicThreadFactory.Builder()
                    .namingPattern("Packet-Sender-%d")
                    .daemon(true)
                    .priority(Thread.NORM_PRIORITY)
                    .build());

    public static void sendParticle(final LogicModule room, final byte particleID, final double x, final double y, final byte facing) {
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

    public static void sendParticle(final LogicModule room, final byte particleID, final double x, final double y) {
        sendParticle(room, particleID, x, y, Globals.RIGHT);
    }

    public static void sendParticle(final LogicModule room, final byte particleID, final byte key) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PARTICLE_EFFECT;
        bytes[1] = particleID;
        bytes[2] = key;
        sendAll(bytes, room);
    }

    public static void sendParticle(final LogicModule room, final byte particleID, final byte key, final byte facing) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PARTICLE_EFFECT;
        bytes[1] = particleID;
        bytes[2] = facing;
        bytes[3] = key;
        sendAll(bytes, room);
    }

    public static void sendScreenShake(final Player player) {
        sendScreenShake(player, 2, 2, 50);
    }

    public static void sendScreenShake(final Player player, final int xAmount, final int yAmount, final int duration) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 1 + Globals.PACKET_INT * 3];
        bytes[0] = Globals.DATA_SCREEN_SHAKE;
        final byte[] posXInt = Globals.intToBytes(xAmount);
        System.arraycopy(posXInt, 0, bytes, 1, posXInt.length);
        final byte[] posYInt = Globals.intToBytes(yAmount);
        System.arraycopy(posYInt, 0, bytes, 5, posYInt.length);
        final byte[] durationBytes = Globals.intToBytes(duration);
        System.arraycopy(durationBytes, 0, bytes, 9, durationBytes.length);
        sendPlayer(bytes, player);
    }

    public static void sendSFX(final LogicModule room, final byte sfxID, final double soundX, final double soundY) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_SOUND_EFFECT;
        bytes[1] = sfxID;
        final byte[] posXInt = Globals.intToBytes((int) soundX);
        System.arraycopy(posXInt, 0, bytes, 2, posXInt.length);
        final byte[] posYInt = Globals.intToBytes((int) soundY);
        System.arraycopy(posYInt, 0, bytes, 6, posYInt.length);
        sendAll(bytes, room);
    }

    public static void sendConnection(final byte[] data, final Connection c) {
        PACKETSENDER_THREAD_POOL.execute(() -> {
            try {
                if (!Globals.UDP_MODE) {
                    if (c.getTcpWriteBufferSize() < Globals.PACKET_MAX_SIZE * Globals.PACKET_MAX_PER_CON * 0.75) {
                        c.sendTCP(data);
                    }
                } else {
                    ConcurrentLinkedQueue<byte[]> batch = connPacketBatch.get(c);
                    if (batch == null) {
                        batch = new ConcurrentLinkedQueue<>();
                        connPacketBatch.put(c, batch);
                    }
                    batch.add(data);
                }
            } catch (Exception e) {
                Globals.logError(e.toString(), e);
            }
        });
    }

    public static void sendPlayer(final byte[] data, final Player player) {
        if (player.isConnected()) {
            sendConnection(data, player.getConnection());
        }
    }

    public static void sendAll(final byte[] data, final LogicModule room) {
        for (final Map.Entry<Byte, Player> pEntry : room.getRoomData().getPlayers().entrySet()) {
            sendPlayer(data, pEntry.getValue());
        }
    }

    public static void sendAllPlayerData(final LogicModule room) {
        for (final Map.Entry<Byte, Player> pEntry : room.getRoomData().getPlayers().entrySet()) {
            final Player player = pEntry.getValue();
            player.sendData();
        }
    }

    @Override
    public void run() {
        if (Globals.UDP_MODE) {
            for (final Map.Entry<Connection, ConcurrentLinkedQueue<byte[]>> entry : connPacketBatch.entrySet()) {
                connPacketBatch.remove(entry.getKey());
                PACKETSENDER_THREAD_POOL.execute(() -> {
                    Connection c = entry.getKey();
                    ConcurrentLinkedQueue<byte[]> batch = entry.getValue();
                    while (!batch.isEmpty()) {
                        LinkedList<byte[]> packetBatch = new LinkedList<>();
                        int packetSize = 0;
                        while (!batch.isEmpty() && packetSize < Globals.PACKET_MAX_SIZE * 0.85) {
                            byte[] singlePacket = batch.poll();
                            packetSize += singlePacket.length;
                            packetBatch.add(singlePacket);
                        }
                        byte[][] data = packetBatch.toArray(new byte[0][0]);
                        int size = c.sendUDP(data);
                    }
                });
            }
        }

    }
}
