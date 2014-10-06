package blockfighter.server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * All the server globals constants and helper methods.
 *
 * @author Ken
 */
public class Globals {

    public final static String ERRLOG_FILE = "ErrorLog.log",
            DATALOG_FILE = "DataLog.log";

    public final static byte LOG_TYPE_ERR = 0x00,
            LOG_TYPE_DATA = 0x01;

    private final static int SERVER_ID = (int) (Math.random() * 50000);

    private final static ExecutorService LOG_THREADS = Executors.newCachedThreadPool();

    public final static void log(final String ex, final String s, final byte logType, final boolean console) {
        Runnable logging = new Runnable() {
            @Override
            public void run() {
                String logFile = ERRLOG_FILE;
                switch (logType) {
                    case LOG_TYPE_ERR:
                        logFile = ERRLOG_FILE;
                        break;
                    case LOG_TYPE_DATA:
                        logFile = DATALOG_FILE;
                        break;
                }

                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)))) {
                    String logT = "?:";
                    switch (logType) {
                        case LOG_TYPE_ERR:
                            logT = "ERROR:";
                            break;
                        case LOG_TYPE_DATA:
                            logT = "DATA:";
                            break;
                    }
                    out.println("[" + SERVER_ID + "]" + ex + "@" + s);
                    if (console) {
                        System.out.println(logT + ex + "@" + s);
                    }
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        };

        LOG_THREADS.execute(logging);
    }

    public final static void log(final String ex, final Exception e, final boolean console) {
        Runnable logging = new Runnable() {
            @Override
            public void run() {
                String logFile = ERRLOG_FILE;

                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)))) {
                    out.println("[" + SERVER_ID + "]" + ex + "@");
                    for (StackTraceElement s : e.getStackTrace()) {
                        out.println("[" + SERVER_ID + "]" + s.toString());
                    }
                    if (console) {
                        System.out.println("ERROR:" + ex + "@" + e.getStackTrace()[1]);
                    }
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        };

        LOG_THREADS.execute(logging);
    }

    public final static int SERVER_PORT = 25565;
    public final static byte MAX_PLAYERS = 10;

    public final static double LOGIC_TICKS_PER_SEC = 100.0;
    public final static double LOGIC_UPDATE = 1000000000 / LOGIC_TICKS_PER_SEC;

    public final static long REFRESH_ALL_UPDATE = 100;

    public final static byte RIGHT = 0, LEFT = 1, DOWN = 2, UP = 3;
    public final static byte MAP_LEFT = 0, MAP_RIGHT = 1;

    public final static double GRAVITY = 0.35, MAX_FALLSPEED = 11.5;

    public final static int NUM_PLAYER_STATE = 5;
    public final static byte PLAYER_STATE_STAND = 0x00,
            PLAYER_STATE_WALK = 0x01,
            PLAYER_STATE_JUMP = 0x02,
            PLAYER_STATE_STUN = 0x03,
            PLAYER_STATE_KNOCKBACK = 0x04;

    public final static byte NUM_PARTICLE_EFFECTS = 1;
    public final static byte PARTICLE_TEMP = 0x00;

    public final static int NUM_STATS = 11;
    public final static int STAT_POWER = 0,
            STAT_DEFENSE = 1,
            STAT_SPIRIT = 2,
            STAT_MINHP = 3,
            STAT_MAXHP = 4,
            STAT_MINDMG = 5,
            STAT_MAXDMG = 6,
            STAT_CRITCHANCE = 7,
            STAT_CRITDMG = 8,
            STAT_REGEN = 9,
            STAT_ARMOR = 10;

    public final static double HP_BASE = 100,
            HP_MULT = 30,
            REDUCT_CONST = 150,
            ARMOR_MULT = 6,
            REGEN_MULT = 1.5,
            CRITCHC_BASE = 0.1,
            CRITCHC_CONST = 200,
            CRITDMG_BASE = 0.5,
            CRITDMG_FACT = 10,
            CRITDMG_MULT = 0.01,
            MINDMG_MULT = 11,
            MAXDMG_MULT = 21,
            MINDMG_BASE = 20,
            MAXDMG_BASE = 40;

    public static final double calcArmor(double defense) {
        return defense * ARMOR_MULT;
    }

    public static final double calcRegen(double spirit) {
        return spirit * REGEN_MULT;
    }

    public static final double calcMaxHP(double defense) {
        return defense * HP_MULT + HP_BASE;
    }

    public static final double calcMinDmg(double power) {
        return power * MINDMG_MULT + MINDMG_BASE;
    }

    public static final double calcMaxDmg(double power) {
        return power * MAXDMG_MULT + MAXDMG_BASE;
    }

    public static final double calcCritChance(double spirit) {
        return spirit / (spirit + CRITCHC_CONST) + CRITCHC_BASE;
    }

    public static final double calcCritDmg(double power) {
        return power / CRITDMG_FACT * CRITDMG_MULT + CRITDMG_BASE;
    }

    //Packet globals
    public final static int PACKET_MAX_SIZE = 128;
    public final static int PACKET_BYTE = 1;
    public final static int PACKET_INT = 4;
    public final static int PACKET_CHAR = 1;

    public static final byte[] intToByte(int input) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (input);
        bytes[1] = (byte) (input >>> 8);
        bytes[2] = (byte) (input >>> 16);
        bytes[3] = (byte) (input >>> 24);
        return bytes;
    }

    public static final int bytesToInt(byte[] input) {
        return (int) (input[0] & 0xff | input[1] << 8 | input[2] << 16 | input[3] << 24);
    }

    //Datatypes
    public final static byte DATA_PING = 0x00,
            DATA_LOGIN = 0x01,
            DATA_GET_ALL_PLAYER = 0x02,
            DATA_SET_PLAYER_MOVE = 0x03,
            DATA_GET_PLAYER_POS = 0x04,
            DATA_SET_PLAYER_FACING = 0x05,
            DATA_SET_PLAYER_STATE = 0x06,
            DATA_PLAYER_KNOCK = 0x07,
            DATA_PARTICLE_EFFECT = 0x08,
            DATA_PARTICLE_REMOVE = 0x09;
}
