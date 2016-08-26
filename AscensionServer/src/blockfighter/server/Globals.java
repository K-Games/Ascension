package blockfighter.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class Globals {

    public final static boolean LOGGING = true,
            DEBUG_MODE = true;
    private final static String LOG_DIR = "logs/",
            ERRLOG_FILE = "ErrorLog.log",
            DATALOG_FILE = "DataLog.log";
    private static JTextArea dataConsole, errConsole;

    public final static byte LOG_TYPE_ERR = 0x00,
            LOG_TYPE_DATA = 0x01;

    public final static byte GAME_MAJOR_VERSION = 0,
            GAME_MINOR_VERSION = 20,
            GAME_UPDATE_NUMBER = 2;
    private final static String GAME_DEV_STATE = "ALPHA";

    public final static String GAME_RELEASE_VERSION = GAME_DEV_STATE + " " + GAME_MAJOR_VERSION + "." + GAME_MINOR_VERSION + "."
            + GAME_UPDATE_NUMBER;

    public final static String WINDOW_TITLE = "Ascension Server " + GAME_RELEASE_VERSION;

    private static final Random RNG = new Random();

    public static ExecutorService LOG_THREAD = Executors.newSingleThreadExecutor(
            new BasicThreadFactory.Builder()
            .namingPattern("Logger-%d")
            .daemon(true)
            .priority(Thread.MIN_PRIORITY)
            .build());

    public static int SERVER_TCP_PORT = 25565;
    public static int SERVER_UDP_PORT = 25566;

    public static byte SERVER_MAX_PLAYERS = 10;
    public static boolean SERVER_BATCH_PACKETSEND = false;
    public static HashMap<Byte, Byte> SERVER_ROOMNUM_TO_ROOMINDEX = new HashMap<>();
    public static int SERVER_MAX_IDLE = 120000;
    public static byte SERVER_LOGIC_THREADS = 3,
            SERVER_PACKETSENDER_THREADS = 5;

    public final static byte MAX_NAME_LENGTH = 15;

    public final static long PROCESS_QUEUE_TICKS_PER_SEC = 100;
    public final static long PROCESS_QUEUE = 1000000000 / PROCESS_QUEUE_TICKS_PER_SEC;

    public final static long LOGIC_TICKS_PER_SEC = 100;
    public final static long LOGIC_UPDATE = 1000000000 / LOGIC_TICKS_PER_SEC;
    public final static long LOGIC_BUCKET_CELLSIZE = 300;

    public final static long SENDALL_TICKS_PER_SEC = 80;
    public final static long SENDALL_UPDATE = 1000000000 / SENDALL_TICKS_PER_SEC;

    public final static long REFRESH_ALL_UPDATE = 100;

    public final static byte RIGHT = 0, LEFT = 1, DOWN = 2, UP = 3;
    public final static byte MAP_LEFT = 0, MAP_RIGHT = 1, MAP_TOP = 2, MAP_BOTTOM = 3;

    public final static double GRAVITY = 0.35, MAX_FALLSPEED = 12.5;
    public static double EXP_MULTIPLIER = 0.05;

    public final static int NUM_PLAYER_ANIM_STATE = 9;
    public final static byte PLAYER_ANIM_STATE_STAND = 0x00,
            PLAYER_ANIM_STATE_WALK = 0x01,
            PLAYER_ANIM_STATE_JUMP = 0x02,
            PLAYER_ANIM_STATE_ATTACK = 0x03,
            PLAYER_ANIM_STATE_ATTACKBOW = 0x04,
            PLAYER_ANIM_STATE_BUFF = 0x05,
            PLAYER_ANIM_STATE_DEAD = 0x06,
            PLAYER_ANIM_STATE_INVIS = 0x07,
            PLAYER_ANIM_STATE_ROLL = 0x08;

    public final static int NUM_PARTICLE_EFFECTS = 50;
    public final static byte PARTICLE_SWORD_SLASH1 = 0x00,
            PARTICLE_SWORD_SLASH2 = 0x01,
            PARTICLE_SWORD_SLASH3 = 0x02,
            PARTICLE_SWORD_GASH1 = 0x03,
            PARTICLE_SWORD_VORPAL = 0x04,
            PARTICLE_SWORD_MULTI = 0x05,
            PARTICLE_SWORD_CINDER = 0x06,
            PARTICLE_BURN = 0x07,
            PARTICLE_SWORD_TAUNT = 0x08,
            PARTICLE_SWORD_TAUNTAURA1 = 0x0A,
            PARTICLE_SWORD_TAUNTAURA2 = 0x0B,
            PARTICLE_BOW_ARC = 0x0C,
            PARTICLE_BOW_RAPID = 0x0D,
            PARTICLE_BOW_POWER = 0x0E,
            PARTICLE_BOW_POWERCHARGE = 0x0F,
            PARTICLE_BOW_POWERPARTICLE = 0x10,
            PARTICLE_BOW_VOLLEYBOW = 0x11,
            PARTICLE_BOW_VOLLEYARROW = 0x12,
            PARTICLE_BOW_STORM = 0x13,
            PARTICLE_BOW_FROSTARROW = 0x14,
            PARTICLE_SHIELD_DASH = 0x15,
            PARTICLE_SHIELD_FORTIFY = 0x16,
            PARTICLE_SHIELD_CHARGE = 0x17,
            PARTICLE_SHIELD_CHARGEPARTICLE = 0x18,
            PARTICLE_SHIELD_REFLECTCAST = 0x19,
            PARTICLE_SHIELD_REFLECTHIT = 0x1A,
            PARTICLE_SHIELD_REFLECTBUFF = 0x1B,
            PARTICLE_SHIELD_ROAR = 0x1C,
            PARTICLE_SHIELD_ROARHIT = 0x1D,
            PARTICLE_SHIELD_FORTIFYBUFF = 0x1E,
            PARTICLE_SHIELD_MAGNETIZE = 0x1F,
            PARTICLE_SWORD_TAUNTBUFF = 0x20,
            PARTICLE_SWORD_SLASHBUFF = 0x21,
            PARTICLE_SHIELD_DASHBUFF = 0x22,
            PARTICLE_BOW_VOLLEYBUFF = 0x23,
            PARTICLE_PASSIVE_RESIST = 0x24,
            PARTICLE_PASSIVE_BARRIER = 0x25,
            PARTICLE_PASSIVE_SHADOWATTACK = 0x26,
            PARTICLE_BLOOD = 0x27,
            PARTICLE_BOW_RAPID2 = 0x28,
            PARTICLE_SWORD_PHANTOM = 0x29,
            PARTICLE_SWORD_PHANTOM2 = 0x2A,
            PARTICLE_SWORD_GASH2 = 0x2B,
            PARTICLE_SWORD_GASH3 = 0x2C,
            PARTICLE_SWORD_GASH4 = 0x2D,
            PARTICLE_BLOOD_HIT = 0x2E,
            PARTICLE_PASSIVE_STATIC = 0x2F,
            PARTICLE_SHIELD_MAGNETIZESTART = 0x30,
            PARTICLE_SHIELD_MAGNETIZEBURST = 0x31;

    public final static byte NUM_SFX = 9,
            SFX_SLASH = 0x00,
            SFX_VOLLEY = 0x01,
            SFX_RAPID = 0x02,
            SFX_POWER = 0x03,
            SFX_FORTIFY = 0x04,
            SFX_IRON = 0x05,
            SFX_ARC = 0x06,
            SFX_POWER2 = 0x07,
            SFX_GASH = 0x08;

    public final static byte NUM_ITEM_TABS = 10,
            ITEM_WEAPON = 0, // ITEM_WEAPON is the equipment slot
            ITEM_SWORD = 0, // ITEM_SWORD is the item type.
            ITEM_HEAD = 1,
            ITEM_CHEST = 2,
            ITEM_PANTS = 3,
            ITEM_SHOULDER = 4,
            ITEM_GLOVE = 5,
            ITEM_SHOE = 6,
            ITEM_BELT = 7,
            ITEM_RING = 8,
            ITEM_AMULET = 9,
            ITEM_OFFHAND = 10, // OFFHAND = slot
            ITEM_SHIELD = 10, // SHIELD = item type
            ITEM_BOW = 11,
            ITEM_ARROW = 12; // 10,11,12 Only used for equipment slot index. Its the same as weapons.

    public final static byte NUM_EQUIP_SLOTS = 11;

    public final static byte NUM_STATS = 17,
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
            STAT_DAMAGEREDUCT = 15,
            STAT_MAXEXP = 16;

    public final static double HP_BASE = 3000,
            HP_MULT = 200,
            REDUCT_CONST = 150,
            ARMOR_MULT = 6,
            REGEN_MULT = 1.5,
            CRITCHC_BASE = 0,
            CRITCHC_FACT = 10,
            CRITCHC_MULT = 0.01,
            CRITCHC_CONST = 2500,
            CRITDMG_BASE = 0.5,
            CRITDMG_FACT = 5.5,
            CRITDMG_MULT = 0.01,
            MINDMG_MULT = 15,
            MAXDMG_MULT = 17,
            MINDMG_BASE = 20,
            MAXDMG_BASE = 40;

    // Packet globals
    public final static int PACKET_MAX_SIZE = 150;
    public final static int PACKET_BYTE = 1;
    public final static int PACKET_INT = 4;
    public final static int PACKET_LONG = 8;
    public final static int PACKET_CHAR = 1;
    public static int PACKET_MAX_PER_CON = 1000;

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
            DATA_PLAYER_CREATE = 0x18,
            DATA_SCREEN_SHAKE = 0x19,
            DATA_PLAYER_EMOTE = 0x1A;

    public static final byte LOGIN_SUCCESS = 0x00,
            LOGIN_FAIL_UID_IN_ROOM = 0x01,
            LOGIN_FAIL_FULL_ROOM = 0x02,
            LOGIN_FAIL_OUTSIDE_LEVEL_RANGE = 0x03;

    public static final byte NUM_EMOTES = 1,
            EMOTE_ALERT = 0x00,
            EMOTE_QUESTION = 0x01,
            EMOTE_SWEAT = 0x02,
            EMOTE_SLEEP = 0x03,
            EMOTE_ANGRY = 0x04,
            EMOTE_5 = 0x05,
            EMOTE_6 = 0x06,
            EMOTE_7 = 0x07,
            EMOTE_8 = 0x08,
            EMOTE_9 = 0x09;

    public static final byte NUMBER_TYPE_PLAYER = 0,
            NUMBER_TYPE_PLAYERCRIT = 1,
            NUMBER_TYPE_MOB = 2;

    static {
        createLogDirectory();
    }

    public final static void setServerProp() {
        for (byte i = 0; i < 10; i++) {
            SERVER_ROOMNUM_TO_ROOMINDEX.put(i, i);
        }
        InputStream inputStream = null;
        try {
            final Properties prop = new Properties();

            inputStream = new FileInputStream("config.properties");
            prop.load(inputStream);
            if (prop.getProperty("tcpport") != null) {
                SERVER_TCP_PORT = Integer.parseInt(prop.getProperty("tcpport"));
            }
            if (prop.getProperty("udpport") != null) {
                SERVER_UDP_PORT = Integer.parseInt(prop.getProperty("udpport"));
            }
            if (prop.getProperty("maxplayers") != null) {
                SERVER_MAX_PLAYERS = Byte.parseByte(prop.getProperty("maxplayers"));
            }
            if (prop.getProperty("expmult") != null) {
                EXP_MULTIPLIER = Double.parseDouble(prop.getProperty("expmult"));
            }
            if (prop.getProperty("rooms") != null) {
                SERVER_ROOMNUM_TO_ROOMINDEX.clear();
                String[] rooms = prop.getProperty("rooms").split(",");
                Object[] roomNums = Arrays.asList(rooms).stream().map(Byte::parseByte).toArray();
                for (byte i = 0; i < roomNums.length; i++) {
                    SERVER_ROOMNUM_TO_ROOMINDEX.put((Byte) roomNums[i], i);
                }
            }
            if (prop.getProperty("maxpackets") != null) {
                PACKET_MAX_PER_CON = Integer.parseInt(prop.getProperty("maxpackets"));
            }
            if (prop.getProperty("logicthreads") != null) {
                SERVER_LOGIC_THREADS = Byte.parseByte(prop.getProperty("logicthreads"));
            }
            if (prop.getProperty("packetsenderthreads") != null) {
                SERVER_PACKETSENDER_THREADS = Byte.parseByte(prop.getProperty("packetsenderthreads"));
            }
        } catch (final FileNotFoundException e) {
            log(Globals.class, "Config", "config.properties not found in root directory. Using default server values.", Globals.LOG_TYPE_DATA, true);
        } catch (final IOException ex) {
            Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException ex) {
                    Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            log(Globals.class, "Config", "Server TCP Port: " + SERVER_TCP_PORT, Globals.LOG_TYPE_DATA, true);
            log(Globals.class, "Config", "Server UDP Port: " + SERVER_UDP_PORT, Globals.LOG_TYPE_DATA, true);
            log(Globals.class, "Config", "Max Players per Room: " + SERVER_MAX_PLAYERS, Globals.LOG_TYPE_DATA, true);
            log(Globals.class, "Config", "Rooms: " + SERVER_ROOMNUM_TO_ROOMINDEX, Globals.LOG_TYPE_DATA, true);
            log(Globals.class, "Config", "EXP Multiplier: " + EXP_MULTIPLIER, Globals.LOG_TYPE_DATA, true);
            log(Globals.class, "Config", "Logic Module Threads: " + SERVER_LOGIC_THREADS, Globals.LOG_TYPE_DATA, true);
            log(Globals.class, "Config", "Max Packet Sender Threads: " + SERVER_PACKETSENDER_THREADS, Globals.LOG_TYPE_DATA, true);
            log(Globals.class, "Config", "Max Packets Per Connection: " + PACKET_MAX_PER_CON, Globals.LOG_TYPE_DATA, true);
        }
    }

    public final static void setGUILog(final JTextArea data, final JTextArea err) {
        dataConsole = data;
        errConsole = err;
    }

    public final static void createLogDirectory() {

        try {
            Files.createDirectories(Paths.get(LOG_DIR + "/" + ERRLOG_FILE).getParent());
            if (!new File(LOG_DIR + "/" + ERRLOG_FILE).exists()) {
                Files.createFile(Paths.get(LOG_DIR + "/" + ERRLOG_FILE));
            }

            Files.createDirectories(Paths.get(LOG_DIR + "/" + DATALOG_FILE).getParent());
            if (!new File(LOG_DIR + "/" + DATALOG_FILE).exists()) {
                Files.createFile(Paths.get(LOG_DIR + "/" + DATALOG_FILE));
            }
        } catch (final IOException e) {
            Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, "Couldn't create log file.", e);
        }
    }

    public final static void log(final Class originClass, final String info, final byte logType, final boolean outputToConsole) {
        log(originClass.getSimpleName(), info, logType, outputToConsole);
    }

    public final static void log(final Class originClass, final String customClassDesc, final String info, final byte logType, final boolean outputToConsole) {
        log(originClass.getSimpleName() + " " + customClassDesc, info, logType, outputToConsole);
    }

    public final static void log(final String className, final String info, final byte logType, final boolean outputToConsole) {

        final Runnable logging = () -> {
            String logFile;
            switch (logType) {
                case LOG_TYPE_ERR:
                    logFile = ERRLOG_FILE;
                    break;
                case LOG_TYPE_DATA:
                    logFile = DATALOG_FILE;
                    break;
                default:
                    logFile = "Other.log";
            }

            String logT = "?";
            switch (logType) {
                case LOG_TYPE_ERR:
                    logT = "ERROR";
                    break;
                case LOG_TYPE_DATA:
                    logT = "DATA";
                    break;
            }

            String message = "[" + String.format("%1$td/%1$tm/%1$tY %1$tT %1$tZ", System.currentTimeMillis()) + "] " + logT + ":" + className + ":" + info;
            if (outputToConsole) {
                System.out.println(message);
            }

            if (LOGGING) {
                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(LOG_DIR, logFile), true)))) {
                    out.println(message);
                } catch (final IOException e) {
                    System.err.println(e);
                }
            }
            switch (logType) {
                case LOG_TYPE_ERR:
                    errConsole.append("\n" + message);
                    errConsole.setCaretPosition(errConsole.getDocument().getLength());
                    break;
                case LOG_TYPE_DATA:
                    dataConsole.append("\n" + message);
                    dataConsole.setCaretPosition(dataConsole.getDocument().getLength());
                    break;
            }
        };

        LOG_THREAD.execute(logging);
    }

    public static final void logError(final String errorMessage, final Exception e, final boolean outputToConsole) {
        final Runnable logging = () -> {
            final String logFile = ERRLOG_FILE;
            String message = "[" + String.format("%1$td/%1$tm/%1$tY %1$tT %1$tZ", System.currentTimeMillis()) + "] ERROR:" + errorMessage + ":" + e.getStackTrace()[1];

            if (outputToConsole) {
                System.out.println(message);
            }
            if (LOGGING) {
                try (final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(LOG_DIR, logFile), true)))) {
                    out.println(message);
                    for (final StackTraceElement s : e.getStackTrace()) {
                        out.println(s.toString());
                        if (outputToConsole) {
                            System.out.println(s.toString());
                        }
                    }
                } catch (final IOException e1) {
                    System.err.println(e1);
                }
            }
            errConsole.append("\n" + message);
            errConsole.setCaretPosition(errConsole.getDocument().getLength());
        };
        LOG_THREAD.execute(logging);
    }

    public static final String getStatName(final byte statID) {
        switch (statID) {
            case STAT_POWER:
                return "Power";
            case STAT_DEFENSE:
                return "Defense";
            case STAT_SPIRIT:
                return "Spirit";
            case STAT_MINHP:
                return "Current HP";
            case STAT_MAXHP:
                return "Max HP";
            case STAT_MINDMG:
                return "Minimum Damage";
            case STAT_MAXDMG:
                return "Maximum Damage";
            case STAT_CRITCHANCE:
                return "Critical Hit Chance";
            case STAT_CRITDMG:
                return "Critical Hit Damage";
            case STAT_REGEN:
                return "Regen(HP/Sec)";
            case STAT_ARMOR:
                return "Armor";
            case STAT_LEVEL:
                return "Level";
            case STAT_POINTS:
                return "Stat Points";
            case STAT_EXP:
                return "Experience";
            case STAT_SKILLPOINTS:
                return "Skill Points";
            case STAT_DAMAGEREDUCT:
                return "Damage Reduction";
            case STAT_MAXEXP:
                return "Required EXP";
        }
        return "INVALID STAT";
    }

    public static final double calcEXPtoNxtLvl(final double level) {
        return Math.pow(level, 3.75) + 100;
    }

    public static final double calcArmor(final double defense) {
        return defense * ARMOR_MULT;
    }

    public static final double calcRegen(final double spirit) {
        return spirit * REGEN_MULT;
    }

    public static final double calcMaxHP(final double defense) {
        return defense * HP_MULT + HP_BASE;
    }

    public static final double calcMinDmg(final double power) {
        return power * MINDMG_MULT + MINDMG_BASE;
    }

    public static final double calcMaxDmg(final double power) {
        return power * MAXDMG_MULT + MAXDMG_BASE;
    }

    public static final double calcCritChance(final double spirit) {
        return spirit / (spirit + CRITCHC_CONST) + CRITCHC_BASE;
    }

    public static final double calcCritDmg(final double spirit) {
        return spirit / CRITDMG_FACT * CRITDMG_MULT + CRITDMG_BASE;
    }

    public static final double calcReduction(final double armor) {
        return 1 - (armor / (armor + REDUCT_CONST));
    }

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

    public static final long nsToMs(final long time) {
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

    public static boolean hasPastDuration(final long currentDuration, final long durationToPast) {
        if (durationToPast <= 0) {
            return true;
        }
        return currentDuration >= durationToPast;
    }

    public static final InputStream loadResourceAsStream(String path) {
        try {
            return FileUtils.openInputStream(new File("resources/" + path));
        } catch (IOException ex) {
            logError("Failed to load resource: " + path, ex, true);
        }
        return null;
    }
}
