package performancetest;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Ken Kwan
 */
public class Globals {

    public static int SERVER_TCP_PORT = 25565;
    public static int SERVER_UDP_PORT = 35565;
    public static boolean UDP_MODE = true;
    public static int ROOM = 0;
    public static int PLAYERS = 10;

    public final static boolean TEST_MAX_LEVEL = true,
            DEBUG_MODE = false;

    public final static byte MAX_NAME_LENGTH = 15;

    private final static Random RNG = new Random();

    public final static byte RIGHT = 0, LEFT = 1, DOWN = 2, UP = 3;

    public final static byte NUM_STATS = 16,
            STAT_POWER = 0,
            STAT_DEFENSE = 1,
            STAT_SPIRIT = 2,
            STAT_MINHP = 3,
            STAT_MAXHP = 4,
            STAT_MINDMG = 5,
            STAT_MAXDMG = 6,
            STAT_CRITCHANCE = 7,
            STAT_CRITDMG = 8,
            STAT_REGEN = 9,
            STAT_ARMOR = 10,
            STAT_LEVEL = 11,
            STAT_POINTS = 12,
            STAT_EXP = 13,
            STAT_SKILLPOINTS = 14,
            STAT_DAMAGEREDUCT = 15;

    // Packet globals
    public final static int PACKET_MAX_SIZE = 8000;
    public final static int PACKET_BYTE = 1;
    public final static int PACKET_INT = 4;
    public final static int PACKET_LONG = 8;
    public final static int PACKET_CHAR = 1;

    // Datatypes
    public final static byte DATA_PING = 0x00,
            DATA_PLAYER_LOGIN = 0x01,
            DATA_PLAYER_GET_ALL = 0x02,
            DATA_PLAYER_SET_MOVE = 0x03,
            DATA_PLAYER_SET_POS = 0x04,
            DATA_PLAYER_SET_FACING = 0x05,
            DATA_PLAYER_SET_STATE = 0x06,
            DATA_PLAYER_USESKILL = 0x07,
            DATA_PARTICLE_EFFECT = 0x08,
            DATA_SOUND_EFFECT = 0x09,
            DATA_PLAYER_DISCONNECT = 0x0A,
            DATA_PLAYER_GET_NAME = 0x0B,
            DATA_PLAYER_GET_STAT = 0x0C,
            DATA_PLAYER_GET_EQUIP = 0x0D,
            DATA_PLAYER_SET_COOLDOWN = 0x0E,
            DATA_NUMBER = 0x0F,
            DATA_PLAYER_GIVEEXP = 0x10,
            DATA_MOB_SET_POS = 0x11,
            DATA_MOB_SET_FACING = 0x12,
            DATA_MOB_SET_STATE = 0x13,
            DATA_MOB_PARTICLE_EFFECT = 0x14,
            DATA_MOB_SET_TYPE = 0x15,
            DATA_MOB_GET_STAT = 0x16,
            DATA_PLAYER_GIVEDROP = 0x17,
            DATA_PLAYER_CREATE = 0x18;

    public static final byte LOGIN_SUCCESS = 0x00,
            LOGIN_FAIL_UID_IN_ROOM = 0x01,
            LOGIN_FAIL_FULL_ROOM = 0x02,
            LOGIN_FAIL_OUTSIDE_LEVEL_RANGE = 0x03;

    public static byte[] longToBytes(long input) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(input);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer.getLong();
    }

    public static final byte[] intToBytes(final int input) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(input);
        return buffer.array();
    }

    public static final int bytesToInt(final byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer.getInt();
    }

    public static final int nsToMs(final long time) {
        return (int) TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS);
    }

    public static final long msToNs(final long time) {
        return TimeUnit.NANOSECONDS.convert(time, TimeUnit.MILLISECONDS);
    }

    public static final int rng(final int i) {
        if (i > 0) {
            return RNG.nextInt(i);
        }
        return -1;
    }
}
