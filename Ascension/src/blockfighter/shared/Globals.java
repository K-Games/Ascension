package blockfighter.shared;

import blockfighter.client.entities.player.skills.Skill;
import java.awt.Font;
import java.awt.image.BufferedImage;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JTextArea;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class Globals {

    public static boolean SKIP_TITLE = false;
    public static int SERVER_TCP_PORT = 25565;
    public static int SERVER_UDP_PORT = 35565;
    public static String SERVER_ADDRESS;
    public static boolean UDP_MODE = true;

    public static final String DEV_PASSPHRASE = "amFwAkjuy0K/lSvUUyZvdiIFdn/Dzu/OAxStgUEdLKk=";
    public static final String COLON_SPACE_TEXT = ": ";

    public static boolean TEST_MAX_LEVEL = false,
            DEBUG_MODE = false;

    public final static byte GAME_MAJOR_VERSION = 0,
            GAME_MINOR_VERSION = 21,
            GAME_UPDATE_NUMBER = 0;

    private final static String GAME_DEV_STATE = "ALPHA";

    public final static String GAME_RELEASE_VERSION = GAME_DEV_STATE + " " + GAME_MAJOR_VERSION + "." + GAME_MINOR_VERSION + "."
            + GAME_UPDATE_NUMBER;

    public final static String WINDOW_TITLE = "Ascenion " + GAME_RELEASE_VERSION;
    public final static boolean WINDOW_SCALE_ENABLED = false;
    public final static double WINDOW_SCALE = 1.5D;
    public final static int WINDOW_WIDTH = 1280;
    public final static int WINDOW_HEIGHT = 720;

    // Render globals
    public final static Font ARIAL_30PT = new Font("Arial", Font.PLAIN, 30);
    public final static Font ARIAL_12PT = new Font("Arial", Font.PLAIN, 12);
    public final static Font ARIAL_15PT = new Font("Arial", Font.BOLD, 15);
    public final static Font ARIAL_15PTITALIC = new Font("Arial", Font.ITALIC, 15);
    public final static Font ARIAL_24PT = new Font("Arial", Font.PLAIN, 24);
    public final static Font ARIAL_18PT = new Font("Arial", Font.PLAIN, 18);
    public final static Font ARIAL_18PTBOLD = new Font("Arial", Font.BOLD, 18);

    public final static byte MAX_NAME_LENGTH = 15;

    private final static Random RNG = new Random();

    // Render 60 fps in microseconds
    public final static int RENDER_FPS = 60;
    public final static long RENDER_UPDATE = 1000000 / RENDER_FPS;

    public final static double CLIENT_LOGIC_TICKS_PER_SEC = 40D;
    public final static double CLIENT_LOGIC_UPDATE = 1000000000D / CLIENT_LOGIC_TICKS_PER_SEC;

    // public final static double DMG_TICKS_PER_SEC = 60D;
    public final static int INGAME_NUMBER_UPDATE = 20;

    // public final static double SEND_KEYDOWN_PER_SEC = 10D;
    public final static double SEND_KEYDOWN_UPDATE = 100000000D;

    // public final static double REQUESTALL_TICKS_PER_SEC = 1D;
    public final static double REQUESTALL_UPDATE = 10000000000D;

    public final static double PINGS_PER_SEC = 2D;
    public final static double PING_UPDATE = 1000000000D / PINGS_PER_SEC;

    public final static double PROCESS_QUEUES_PER_SEC = 100D;
    public final static double QUEUES_UPDATE = 1000000000D / PROCESS_QUEUES_PER_SEC;

    public final static byte RIGHT = 0, LEFT = 1, DOWN = 2, UP = 3;

    public final static int NUM_SOUND_EFFECTS = 0;

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
            //PARTICLE_SWORD_GASH3 = 0x2C,
            //PARTICLE_SWORD_GASH4 = 0x2D,
            PARTICLE_BLOOD_HIT = 0x2E,
            PARTICLE_PASSIVE_STATIC = 0x2F,
            PARTICLE_SHIELD_MAGNETIZESTART = 0x30,
            PARTICLE_SHIELD_MAGNETIZEBURST = 0x31;

    public final static int NUM_KEYBINDS = 26,
            KEYBIND_SKILL1 = 0,
            KEYBIND_SKILL2 = 1,
            KEYBIND_SKILL3 = 2,
            KEYBIND_SKILL4 = 3,
            KEYBIND_SKILL5 = 4,
            KEYBIND_SKILL6 = 5,
            KEYBIND_SKILL7 = 6,
            KEYBIND_SKILL8 = 7,
            KEYBIND_SKILL9 = 8,
            KEYBIND_SKILL10 = 9,
            KEYBIND_SKILL11 = 10,
            KEYBIND_SKILL12 = 11,
            KEYBIND_LEFT = 12,
            KEYBIND_RIGHT = 13,
            KEYBIND_JUMP = 14,
            KEYBIND_DOWN = 15,
            KEYBIND_EMOTE1 = 16,
            KEYBIND_EMOTE2 = 17,
            KEYBIND_EMOTE3 = 18,
            KEYBIND_EMOTE4 = 19,
            KEYBIND_EMOTE5 = 20,
            KEYBIND_EMOTE6 = 21,
            KEYBIND_EMOTE7 = 22,
            KEYBIND_EMOTE8 = 23,
            KEYBIND_EMOTE9 = 24,
            KEYBIND_EMOTE10 = 25;

    public final static byte NUM_ITEM_TABS = 10,
            ITEM_WEAPON = 0, // ITEM_WEAPON is the equipment slot/tab
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
            ITEM_OFFHAND = 10,
            ITEM_SHIELD = 10, // 10,11,12 only used for item type identification.
            ITEM_BOW = 11, // They all will be placed in weapons tab
            ITEM_ARROW = 12;

    public final static byte NUM_ITEM_TYPES = 13;
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

    public final static double HP_BASE = 3000, // PvE = 100
            HP_MULT = 170, // PvE = 30
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
            MAXDMG_BASE = 40,
            STAT_PER_LEVEL = 7,
            SP_PER_LEVEL = 3;

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

    // Packet globals
    public final static int PACKET_MAX_SIZE = 500;
    public final static int PACKET_BYTE = 1;
    public final static int PACKET_INT = 4;
    public final static int PACKET_LONG = 8;
    public final static int PACKET_CHAR = 1;

    public final static byte HUB_DATA_PING = 0x00,
            HUB_DATA_GET_SERVERINFOS = 0x01,
            HUB_DATA_GET_SERVERSTATS = 0x02;

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
            LOGIN_FAIL_NO_ROOMS = 0x03;

    public static final byte NUM_EMOTES = 10,
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

    public final static byte NUM_SKILLS = 30,
            SWORD_VORPAL = 0x00,
            SWORD_PHANTOM = 0x01,
            SWORD_CINDER = 0x02,
            SWORD_GASH = 0x03,
            SWORD_SLASH = 0x04,
            SWORD_TAUNT = 0x05,
            BOW_ARC = 0x06,
            BOW_POWER = 0x07,
            BOW_RAPID = 0x08,
            BOW_FROST = 0x09,
            BOW_STORM = 0x0A,
            BOW_VOLLEY = 0x0B,
            UTILITY_FORTIFY = 0x0C,
            SHIELD_ROAR = 0x0D,
            SHIELD_CHARGE = 0x0E,
            SHIELD_REFLECT = 0x0F,
            SHIELD_MAGNETIZE = 0x10,
            UTILITY_DASH = 0x11,
            PASSIVE_DUALSWORD = 0x12,
            PASSIVE_KEENEYE = 0x13,
            PASSIVE_VITALHIT = 0x14,
            PASSIVE_SHIELDMASTERY = 0x15,
            PASSIVE_BARRIER = 0x16,
            PASSIVE_RESIST = 0x17,
            PASSIVE_BOWMASTERY = 0x18,
            PASSIVE_WILLPOWER = 0x19,
            PASSIVE_HARMONY = 0x1A,
            PASSIVE_TOUGH = 0x1B,
            PASSIVE_SHADOWATTACK = 0x1C,
            PASSIVE_STATIC = 0x1D;

    public final static BufferedImage[][] CHAR_SPRITE = new BufferedImage[NUM_PLAYER_ANIM_STATE][];
    public final static BufferedImage[] HUD = new BufferedImage[4];
    public static BufferedImage TITLE;

    public final static BufferedImage[] MENU_BG = new BufferedImage[3];
    public final static BufferedImage[] MENU_SMOKE = new BufferedImage[1];
    public final static BufferedImage[] MENU_UPGRADEPARTICLE = new BufferedImage[4];
    public final static BufferedImage[] MENU_BUTTON = new BufferedImage[17];
    public final static BufferedImage[] MENU_WINDOW = new BufferedImage[2];
    public final static BufferedImage[] MENU_TABPOINTER = new BufferedImage[2];
    public final static BufferedImage[] MENU_ITEMDELETE = new BufferedImage[1];

    // Use Cooper Std Black size 25
    public final static BufferedImage[][] DAMAGE_FONT = new BufferedImage[4][10];
    public final static BufferedImage[] EXP_WORD = new BufferedImage[1];

    public final static BufferedImage[] SKILL_ICON = new BufferedImage[NUM_SKILLS];

    public final static byte NUM_BGM = 5,
            BGM_MENU = 0x00,
            BGM_ARENA1 = 0x01,
            BGM_ARENA2 = 0x02,
            BGM_ARENA3 = 0x03,
            BGM_TITLE = 0x04;

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

    public final static String[] SOUND_BGM = new String[NUM_BGM];
    public final static String[] SOUND_SFX = new String[NUM_SFX];

    public final static int[] PLAYER_NUM_ANIM_FRAMES = new int[NUM_PLAYER_ANIM_STATE];

    public final static byte BUTTON_BIGRECT = 0,
            BUTTON_SELECTCHAR = 1,
            BUTTON_ADDSTAT = 2,
            BUTTON_SLOT = 3,
            BUTTON_MENUS = 4,
            BUTTON_WEAPONTAB = 5,
            BUTTON_HEADTAB = 6,
            BUTTON_CHESTTAB = 7,
            BUTTON_PANTSTAB = 8,
            BUTTON_SHOULDERTAB = 9,
            BUTTON_GLOVETAB = 10,
            BUTTON_SHOETAB = 11,
            BUTTON_BELTTAB = 12,
            BUTTON_RINGTAB = 13,
            BUTTON_AMULETTAB = 14,
            BUTTON_SMALLRECT = 15,
            BUTTON_RIGHTCLICK = 16;

    public final static byte WINDOW_CREATECHAR = 0,
            WINDOW_DESTROYCONFIRM = 1;

    public static final byte NUMBER_TYPE_PLAYER = 0,
            NUMBER_TYPE_PLAYERCRIT = 1,
            NUMBER_TYPE_MOB = 2;

    public static final byte NOTIFICATION_EXP = 0,
            NOTIFICATION_ITEM = 1,
            NOTIFICATION_KILL = 2;

    public static boolean LOGGING = false;
    private final static String LOG_DIR = "logs/",
            ERRLOG_FILE = "ErrorLog-" + String.format("%1$td%1$tm%1$tY-%1$tH%1$tM%1$tS", System.currentTimeMillis()) + ".log",
            DATALOG_FILE = "DataLog-" + String.format("%1$td%1$tm%1$tY-%1$tH%1$tM%1$tS", System.currentTimeMillis()) + ".log";
    private static JTextArea dataConsole, errConsole;

    public final static byte LOG_TYPE_ERR = 0x00,
            LOG_TYPE_DATA = 0x01;

    public static ExecutorService LOG_THREAD = Executors.newSingleThreadExecutor(
            new BasicThreadFactory.Builder()
                    .namingPattern("Logger-%d")
                    .daemon(true)
                    .priority(Thread.MIN_PRIORITY)
                    .build());

    public static byte SERVER_MAX_ROOM_PLAYERS = 10;
    public static int SERVER_PLAYER_MAX_IDLE = 120000;
    public static int SERVER_ROOM_MAX_ILDE = 300000;
    public static byte SERVER_LOGIC_THREADS = 3;
    public static byte SERVER_PACKETSENDER_THREADS = 5;
    public static int SERVER_MAX_ROOMS = 10;
    public static boolean SERVER_HUB_CONNECT = false;

    public static int HUB_SERVER_TCP_PORT = 25566;
    public static String HUB_SERVER_ADDRESS = "asc-hub.servegame.com";

    public final static long PROCESS_QUEUE_TICKS_PER_SEC = 100;
    public final static long PROCESS_QUEUE = 1000000000 / PROCESS_QUEUE_TICKS_PER_SEC;

    public final static long SERVER_LOGIC_TICKS_PER_SEC = 100;
    public final static long SERVER_LOGIC_UPDATE = 1000000000 / SERVER_LOGIC_TICKS_PER_SEC;
    public final static long SERVER_LOGIC_BUCKET_CELLSIZE = 300;

    public final static long SENDALL_TICKS_PER_SEC = 80;
    public final static long SENDALL_UPDATE = 1000000000 / SENDALL_TICKS_PER_SEC;

    public final static long REFRESH_ALL_UPDATE = 100;

    public final static byte MAP_LEFT = 0, MAP_RIGHT = 1, MAP_TOP = 2, MAP_BOTTOM = 3;

    public final static double GRAVITY = 0.35, MAX_FALLSPEED = 12.5, WALK_SPEED = 3.8;
    public static double EXP_MULTIPLIER = 0.05;

    public static int PACKET_MAX_PER_CON = 1000;

    public final static HashSet<Integer> ITEM_CODES = new HashSet<>();
    public static final HashSet<Integer> ITEM_UPGRADE_CODES = new HashSet<>();

    public static final String SKILL_BASEVALUE_HEADER = "[basevalue]",
            SKILL_DESC_HEADER = "[desc]",
            SKILL_MAXCOOLDOWN_HEADER = "[maxcooldown]",
            SKILL_MULTVALUE_HEADER = "[multvalue]",
            SKILL_NAME_HEADER = "[name]",
            SKILL_PASSIVE_HEADER = "[passive]",
            SKILL_REQWEAPON_HEADER = "[reqweapon]";

    public static final String[] DATA_HEADERS = {
        SKILL_NAME_HEADER,
        SKILL_DESC_HEADER,
        SKILL_REQWEAPON_HEADER,
        SKILL_MAXCOOLDOWN_HEADER,
        SKILL_BASEVALUE_HEADER,
        SKILL_MULTVALUE_HEADER,
        SKILL_PASSIVE_HEADER};

    public static HashMap<String, Integer> getDataHeaders(final String[] data, final String[] customDataHeaders) {
        HashMap<String, Integer> dataHeader = new HashMap<>();
        for (int i = 0; i < data.length; i++) {
            for (String header : DATA_HEADERS) {
                if (data[i].equalsIgnoreCase(header)) {
                    dataHeader.put(header, i);
                }
            }
            if (customDataHeaders != null) {
                for (String header : customDataHeaders) {
                    if (data[i].equalsIgnoreCase(header)) {
                        dataHeader.put(header, i);
                    }
                }
            }
        }
        return dataHeader;
    }

    public static boolean loadBooleanValue(final String[] data, final HashMap<String, Integer> dataHeaders, String header) {
        try {
            return Boolean.parseBoolean(data[dataHeaders.get(header) + 1]);
        } catch (Exception e) {
            Globals.logError(e.toString(), e, true);
        }
        return false;
    }

    public static double loadDoubleValue(final String[] data, final HashMap<String, Integer> dataHeaders, String header) {
        try {
            return Double.parseDouble(data[dataHeaders.get(header) + 1]);
        } catch (Exception e) {
            Globals.logError(e.toString(), e, true);
        }
        return 0;
    }

    public static byte loadReqWeapon(final String[] data, final HashMap<String, Integer> dataHeaders) {
        try {
            byte weaponData = Byte.parseByte(data[dataHeaders.get(SKILL_REQWEAPON_HEADER) + 1]);
            return (weaponData >= Globals.NUM_ITEM_TYPES) ? -1 : weaponData;
        } catch (Exception e) {
            Globals.logError(e.toString(), e, true);
        }
        return -1;
    }

    public static String[] loadSkillData(final byte skillCode) {
        Globals.log(Skill.class, "Loading Skill " + String.format("0x%02X", skillCode) + " Data...", Globals.LOG_TYPE_DATA, true);
        try {
            InputStream skillDataFile = Globals.loadResourceAsStream("skilldata/" + String.format("0x%02X", skillCode) + ".txt");
            List<String> fileLines = IOUtils.readLines(skillDataFile, "UTF-8");
            return fileLines.toArray(new String[fileLines.size()]);
        } catch (IOException | NullPointerException e) {
            Globals.logError("Could not load Skill " + String.format("0x%02X", skillCode) + " Data." + e.toString(), e, true);
            System.exit(101);
            return null;
        }
    }

    public static String[] loadSkillDesc(final String[] data, final HashMap<String, Integer> dataHeaders) {
        try {
            int numOfLines = Integer.parseInt(data[dataHeaders.get(SKILL_DESC_HEADER) + 1]);
            String[] description = new String[numOfLines];
            for (int line = 0; line < numOfLines; line++) {
                description[line] = data[dataHeaders.get(SKILL_DESC_HEADER) + 2 + line];
            }
            return description;
        } catch (Exception e) {
            Globals.logError(e.toString(), e, true);
        }
        return new String[0];
    }

    public static String loadSkillName(final String[] data, final HashMap<String, Integer> dataHeaders) {
        try {
            return data[dataHeaders.get(SKILL_NAME_HEADER) + 1];
        } catch (Exception e) {
            Globals.logError(e.toString(), e, true);
        }
        return "NO_NAME";
    }

    public static void loadServer() {
    }

    public static void loadClient() {
        PLAYER_NUM_ANIM_FRAMES[PLAYER_ANIM_STATE_ATTACK] = 6;
        PLAYER_NUM_ANIM_FRAMES[PLAYER_ANIM_STATE_ATTACKBOW] = 8;
        PLAYER_NUM_ANIM_FRAMES[PLAYER_ANIM_STATE_STAND] = 4;
        PLAYER_NUM_ANIM_FRAMES[PLAYER_ANIM_STATE_WALK] = 8;
        PLAYER_NUM_ANIM_FRAMES[PLAYER_ANIM_STATE_BUFF] = 5;
        PLAYER_NUM_ANIM_FRAMES[PLAYER_ANIM_STATE_DEAD] = 10;
        PLAYER_NUM_ANIM_FRAMES[PLAYER_ANIM_STATE_ROLL] = 10;
        PLAYER_NUM_ANIM_FRAMES[PLAYER_ANIM_STATE_JUMP] = 3;
        loadSound();
        loadGFX();
    }

    private static void loadSound() {
        SOUND_BGM[BGM_MENU] = "hero.ogg";
        SOUND_BGM[BGM_ARENA1] = "bgm/0.ogg";
        SOUND_BGM[BGM_ARENA2] = "bgm/1.ogg";
        SOUND_BGM[BGM_ARENA3] = "bgm/2.ogg";
        SOUND_BGM[BGM_TITLE] = "Through the Forest in Midwinter.ogg";

        SOUND_SFX[SFX_SLASH] = "sfx/sword/slash/0.wav";
        SOUND_SFX[SFX_GASH] = "sfx/sword/gash/0.wav";
        SOUND_SFX[SFX_VOLLEY] = "sfx/bow/volley/0.wav";
        SOUND_SFX[SFX_RAPID] = "sfx/bow/rapid/0.wav";
        SOUND_SFX[SFX_ARC] = "sfx/bow/arc/0.wav";
        SOUND_SFX[SFX_POWER] = "sfx/bow/power/0.wav";
        SOUND_SFX[SFX_POWER2] = "sfx/bow/power/1.wav";
        SOUND_SFX[SFX_FORTIFY] = "sfx/shield/fortify/0.wav";
        SOUND_SFX[SFX_IRON] = "sfx/shield/iron/0.wav";
    }

    private static final String STAT_NAME_MAXEXP = "Required EXP";
    private static final String STAT_NAME_DMGREDUCT = "Damage Reduction";
    private static final String STAT_NAME_SKILLPOINTS = "Skill Points";
    private static final String STAT_NAME_EXP = "Experience";
    private static final String STAT_NAME_POINTS = "Stat Points";
    private static final String STAT_NAME_LEVEL = "Level";
    private static final String STAT_NAME_ARMOR = "Armor";
    private static final String STAT_NAME_REGEN = "Regen(HP/Sec)";
    private static final String STAT_NAME_CRITDMG = "Critical Hit Damage";
    private static final String STAT_NAME_CRITCHC = "Critical Hit Chance";
    private static final String STAT_NAME_MAXDMG = "Maximum Damage";
    private static final String STAT_NAME_MINDMG = "Minimum Damage";
    private static final String STAT_NAME_MAXHP = "Max HP";
    private static final String STAT_NAME_MINHP = "Current HP";
    private static final String STAT_NAME_SPIRIT = "Spirit";
    private static final String STAT_NAME_DEFENSE = "Defense";
    private static final String STAT_NAME_POWER = "Power";

    public static final String getStatName(final byte statID) {
        switch (statID) {
            case STAT_POWER:
                return STAT_NAME_POWER;
            case STAT_DEFENSE:
                return STAT_NAME_DEFENSE;
            case STAT_SPIRIT:
                return STAT_NAME_SPIRIT;
            case STAT_MINHP:
                return STAT_NAME_MINHP;
            case STAT_MAXHP:
                return STAT_NAME_MAXHP;
            case STAT_MINDMG:
                return STAT_NAME_MINDMG;
            case STAT_MAXDMG:
                return STAT_NAME_MAXDMG;
            case STAT_CRITCHANCE:
                return STAT_NAME_CRITCHC;
            case STAT_CRITDMG:
                return STAT_NAME_CRITDMG;
            case STAT_REGEN:
                return STAT_NAME_REGEN;
            case STAT_ARMOR:
                return STAT_NAME_ARMOR;
            case STAT_LEVEL:
                return STAT_NAME_LEVEL;
            case STAT_POINTS:
                return STAT_NAME_POINTS;
            case STAT_EXP:
                return STAT_NAME_EXP;
            case STAT_SKILLPOINTS:
                return STAT_NAME_SKILLPOINTS;
            case STAT_DAMAGEREDUCT:
                return STAT_NAME_DMGREDUCT;
            case STAT_MAXEXP:
                return STAT_NAME_MAXEXP;
        }
        return "INVALID STAT";
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

    public static final double calcEHP(final double reduct, final double maxHP) {
        return maxHP / reduct;
    }

    public static final int calcEXPtoNxtLvl(final double level) {
        return (int) (Math.round(Math.pow(level, 3.75) + 100));
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

    private static void loadGFX() {
        for (int state = 0; state < CHAR_SPRITE.length; state++) {
            if (PLAYER_NUM_ANIM_FRAMES[state] > 0) {
                CHAR_SPRITE[state] = new BufferedImage[PLAYER_NUM_ANIM_FRAMES[state]];
                for (int frames = 0; frames < CHAR_SPRITE[state].length; frames++) {
                    String folder = "";
                    switch (state) {
                        case PLAYER_ANIM_STATE_ATTACK:
                            folder = "attack/mainhand";
                            break;
                        case PLAYER_ANIM_STATE_ATTACKBOW:
                            folder = "attack/bow";
                            break;
                        case PLAYER_ANIM_STATE_STAND:
                            folder = "stand";
                            break;
                        case PLAYER_ANIM_STATE_WALK:
                            folder = "walk";
                            break;
                        case PLAYER_ANIM_STATE_BUFF:
                            folder = "buff";
                            break;
                        case PLAYER_ANIM_STATE_DEAD:
                            folder = "dead";
                            break;
                        case PLAYER_ANIM_STATE_JUMP:
                            folder = "jump";
                            break;
                        case PLAYER_ANIM_STATE_ROLL:
                            folder = "roll";
                            break;
                    }
                    CHAR_SPRITE[state][frames] = Globals.loadTextureResource("sprites/character/" + folder + "/" + frames + ".png");
                }
            }
        }
        HUD[0] = Globals.loadTextureResource("sprites/ui/ingame/hud.png");
        HUD[1] = Globals.loadTextureResource("sprites/ui/ingame/hp.png");
        HUD[2] = Globals.loadTextureResource("sprites/ui/ingame/exphud.png");
        HUD[3] = Globals.loadTextureResource("sprites/ui/ingame/exp.png");

        for (byte i = 0; i < MENU_BG.length; i++) {
            MENU_BG[i] = Globals.loadTextureResource("sprites/ui/menu/bg" + (i + 1) + ".png");
        }

        TITLE = Globals.loadTextureResource("sprites/ui/menu/title.png");

        for (byte i = 0; i < MENU_BUTTON.length; i++) {
            MENU_BUTTON[i] = Globals.loadTextureResource("sprites/ui/menu/button" + (i + 1) + ".png");
        }

        for (byte i = 0; i < MENU_WINDOW.length; i++) {
            MENU_WINDOW[i] = Globals.loadTextureResource("sprites/ui/menu/window" + (i + 1) + ".png");
        }

        for (byte i = 0; i < MENU_UPGRADEPARTICLE.length; i++) {
            MENU_UPGRADEPARTICLE[i] = Globals.loadTextureResource("sprites/ui/menu/particle" + (i + 1) + ".png");
        }

        MENU_TABPOINTER[0] = Globals.loadTextureResource("sprites/ui/menu/pointer.png");
        MENU_TABPOINTER[1] = Globals.loadTextureResource("sprites/ui/menu/pointer2.png");
        MENU_ITEMDELETE[0] = Globals.loadTextureResource("sprites/ui/menu/delete.png");
        MENU_SMOKE[0] = Globals.loadTextureResource("sprites/ui/menu/smoke.png");
        for (byte i = 0; i < 30; i++) {
            SKILL_ICON[i] = Globals.loadTextureResource("sprites/skillicon/" + i + ".png");
        }

//        for (byte i = 0; i < 10; i++) {
//            DAMAGE_FONT[NUMBER_TYPE_MOB][i] = Globals.loadTextureResource("sprites/number/boss/" + i + ".png");
//            DAMAGE_FONT[NUMBER_TYPE_PLAYER][i] = Globals.loadTextureResource("sprites/number/player/" + i + ".png");
//            DAMAGE_FONT[NUMBER_TYPE_PLAYERCRIT][i] = Globals.loadTextureResource("sprites/number/playercrit/" + i + ".png");
//            DAMAGE_FONT[NUMBER_TYPE_EXP][i] = Globals.loadTextureResource("sprites/number/exp/" + i + ".png");
//        }
        EXP_WORD[0] = Globals.loadTextureResource("sprites/number/exp/exp.png");
    }

    public static final long nsToMs(final long time) {
        return TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS);
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

    public static final BufferedImage loadTextureResource(String path) {
        try {
            InputStream resource = loadResourceAsStream(path);
            if (resource != null) {
                return ImageIO.read(loadResourceAsStream(path));
            }
        } catch (IOException ex) {
            System.err.println("Failed to load texture: " + path);
        }
        return null;
    }

    public static final InputStream loadResourceAsStream(String path) {
        try {
            return FileUtils.openInputStream(new File("resources/" + path));
        } catch (IOException ex) {
            //System.err.println("Failed to load resource: " + path);
        }
        return null;
    }

    public final static void setServerProp() {
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
                SERVER_MAX_ROOM_PLAYERS = Byte.parseByte(prop.getProperty("maxplayers"));
            }
            if (prop.getProperty("expmult") != null) {
                EXP_MULTIPLIER = Double.parseDouble(prop.getProperty("expmult"));
            }
            if (prop.getProperty("maxrooms") != null) {
                SERVER_MAX_ROOMS = Integer.parseInt(prop.getProperty("maxrooms"));
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
            if (prop.getProperty("udpmode") != null) {
                UDP_MODE = Boolean.parseBoolean(prop.getProperty("udpmode"));
            }
            if (prop.getProperty("hubconnect") != null) {
                SERVER_HUB_CONNECT = Boolean.parseBoolean(prop.getProperty("hubconnect"));
            }
            if (prop.getProperty("hubaddress") != null) {
                HUB_SERVER_ADDRESS = prop.getProperty("hubaddress");
            }
            if (prop.getProperty("hubport") != null) {
                HUB_SERVER_TCP_PORT = Integer.parseInt(prop.getProperty("hubport"));
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
            log(Globals.class, "Config", "Max Players per Room: " + SERVER_MAX_ROOM_PLAYERS, Globals.LOG_TYPE_DATA, true);
            log(Globals.class, "Config", "Max Rooms: " + SERVER_MAX_ROOMS, Globals.LOG_TYPE_DATA, true);
            log(Globals.class, "Config", "EXP Multiplier: " + EXP_MULTIPLIER, Globals.LOG_TYPE_DATA, true);
            log(Globals.class, "Config", "Logic Module Threads: " + SERVER_LOGIC_THREADS, Globals.LOG_TYPE_DATA, true);
            log(Globals.class, "Config", "Max Packet Sender Threads: " + SERVER_PACKETSENDER_THREADS, Globals.LOG_TYPE_DATA, true);
            log(Globals.class, "Config", "Max Packets Per Connection: " + PACKET_MAX_PER_CON, Globals.LOG_TYPE_DATA, true);
            log(Globals.class, "Config", "UDP Mode: " + UDP_MODE, Globals.LOG_TYPE_DATA, true);
            log(Globals.class, "Config", "Hub Connect: " + SERVER_HUB_CONNECT, Globals.LOG_TYPE_DATA, true);
            log(Globals.class, "Config", "Hub Address: " + HUB_SERVER_ADDRESS, Globals.LOG_TYPE_DATA, true);
            log(Globals.class, "Config", "Hub Port: " + HUB_SERVER_TCP_PORT, Globals.LOG_TYPE_DATA, true);
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
                    if (errConsole != null) {
                        errConsole.append("\n" + message);
                        errConsole.setCaretPosition(errConsole.getDocument().getLength());
                    }
                    break;
                case LOG_TYPE_DATA:
                    if (dataConsole != null) {
                        dataConsole.append("\n" + message);
                        dataConsole.setCaretPosition(dataConsole.getDocument().getLength());
                    }
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
            if (errConsole != null) {
                errConsole.append("\n" + message);
                errConsole.setCaretPosition(errConsole.getDocument().getLength());
            }
        };
        LOG_THREAD.execute(logging);
    }

    public static boolean hasPastDuration(final long currentDuration, final long durationToPast) {
        if (durationToPast <= 0) {
            return true;
        }
        return currentDuration >= durationToPast;
    }

    public static void loadItemCodes() {
        ITEM_UPGRADE_CODES.add(100);

        try {
            InputStream itemFile = Globals.loadResourceAsStream("itemdata/equip/itemcodes.txt");
            LineIterator it = IOUtils.lineIterator(itemFile, "UTF-8");
            try {
                while (it.hasNext()) {
                    String line = it.nextLine();
                    try {
                        int itemcode = Integer.parseInt(line);
                        ITEM_CODES.add(itemcode);
                    } catch (NumberFormatException e) {
                    }
                }
            } finally {
                LineIterator.closeQuietly(it);
            }
        } catch (IOException e) {
            Globals.logError("Could not load item codes from data", e, true);
            System.exit(102);
        }
    }
}
