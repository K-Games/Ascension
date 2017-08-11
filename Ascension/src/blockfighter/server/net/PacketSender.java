package blockfighter.server.net;

import blockfighter.server.Core;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.shared.Globals;
import com.esotericsoftware.kryonet.Connection;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PacketSender implements Runnable {

    private static final ConcurrentHashMap<Connection, ConcurrentLinkedQueue<byte[]>> CONN_PACKET_BATCH = new ConcurrentHashMap<>();

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
        sendScreenShake(player, 2, 2, 150);
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
        Core.SHARED_THREADPOOL.execute(() -> {
            try {
                synchronized (c) {
                    CONN_PACKET_BATCH.putIfAbsent(c, new ConcurrentLinkedQueue<>());
                    CONN_PACKET_BATCH.get(c).add(data);
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
        if ((Boolean) Globals.ServerConfig.UDP_MODE.getValue()) {
            CONN_PACKET_BATCH.entrySet().forEach((entry) -> {
                Connection c = entry.getKey();
                ConcurrentLinkedQueue<byte[]> batch = entry.getValue();
                synchronized (c) {
                    CONN_PACKET_BATCH.remove(c);
                }
                Core.SHARED_THREADPOOL.execute(() -> {
                    while (!batch.isEmpty()) {
                        sendData(c, splitBatchData(batch));
                    }
                });
            });
        }

    }

    private static byte[][] splitBatchData(final Queue<byte[]> batch) {
        ArrayDeque<byte[]> packetBatch = new ArrayDeque<>(batch.size());
        int packetSize = 0;
        while (!batch.isEmpty() && packetSize < Globals.PACKET_MAX_SIZE * 0.85) {
            byte[] singlePacket = batch.poll();
            packetSize += singlePacket.length;
            packetBatch.add(singlePacket);
        }
        return packetBatch.toArray(new byte[0][0]);
    }

    private static void sendData(final Connection c, final byte[][] data) {
        try {
            if ((Boolean) Globals.ServerConfig.UDP_MODE.getValue()) {
                c.sendUDP(data);
            } else {
                if (c.getTcpWriteBufferSize() < Globals.PACKET_MAX_SIZE * (Byte) Globals.ServerConfig.MAX_PACKETS_PER_CON.getValue() * 0.75) {
                    c.sendTCP(data);
                }
            }
        } catch (Exception e) {
            Globals.logError(e.toString(), e);
        }
    }

    public static void clearDisconnectedConnectionBatch() {
        for (final Map.Entry<Connection, ConcurrentLinkedQueue<byte[]>> entry : CONN_PACKET_BATCH.entrySet()) {
            Connection c = entry.getKey();
            if (!c.isConnected() && CONN_PACKET_BATCH.containsKey(c)) {
                CONN_PACKET_BATCH.remove(c);
            }
        }
    }

}
