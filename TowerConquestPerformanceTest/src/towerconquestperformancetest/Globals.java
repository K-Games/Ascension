package towerconquestperformancetest;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Ken Kwan
 */
public class Globals {

    public static int SERVER_PORT = 25565;
    public static String SERVER_ADDRESS;

    public final static boolean TEST_MAX_LEVEL = true,
            DEBUG_MODE = false;

    public final static byte GAME_MAJOR_VERSION = 0,
            GAME_MINOR_VERSION = 17,
            GAME_UPDATE_NUMBER = 2;

    private final static String GAME_DEV_STATE = "ALPHA";

    public final static String GAME_RELEASE_VERSION = GAME_DEV_STATE + " " + GAME_MAJOR_VERSION + "." + GAME_MINOR_VERSION + "."
            + GAME_UPDATE_NUMBER;

    public final static String WINDOW_TITLE = "Tower Conquest " + GAME_RELEASE_VERSION;
    public final static int WINDOW_WIDTH = 1280;
    public final static int WINDOW_HEIGHT = 720;

    // Render globals
    public final static Font ARIAL_30PT = new Font("Arial", Font.PLAIN, 30);
    public final static Font ARIAL_12PT = new Font("Arial", Font.PLAIN, 12);
    public final static Font ARIAL_15PT = new Font("Arial", Font.BOLD, 15);
    public final static Font ARIAL_15PTITALIC = new Font("Arial", Font.ITALIC, 15);
    public final static Font ARIAL_24PT = new Font("Arial", Font.PLAIN, 24);
    public final static Font ARIAL_18PT = new Font("Arial", Font.PLAIN, 18);
    public final static Font ARIAL_18P = new Font("Arial", Font.BOLD, 18);

    public final static byte MAX_NAME_LENGTH = 15;

    private final static Random RNG = new Random();

    // Render 60 fps in microseconds
    public final static int RENDER_FPS = 60;
    public final static long RENDER_UPDATE = 1000000 / RENDER_FPS;

    public final static double LOGIC_TICKS_PER_SEC = 40D;
    public final static double LOGIC_UPDATE = 1000000000D / LOGIC_TICKS_PER_SEC;

    // public final static double DMG_TICKS_PER_SEC = 60D;
    public final static int INGAME_NUMBER_UPDATE = 20;

    // public final static double SEND_KEYDOWN_PER_SEC = 10D;
    public final static long SEND_KEYDOWN_UPDATE = 100000000;

    // public final static double REQUESTALL_TICKS_PER_SEC = 1D;
    public final static double REQUESTALL_UPDATE = 10000000000D;

    public final static double PINGS_PER_SEC = 1D;
    public final static double PING_UPDATE = 1000000000D / PINGS_PER_SEC;

    public final static double PROCESS_QUEUES_PER_SEC = 100D;
    public final static double QUEUES_UPDATE = 1000000000D / PROCESS_QUEUES_PER_SEC;

    public final static byte RIGHT = 0, LEFT = 1, DOWN = 2, UP = 3;

    public final static int NUM_SOUND_EFFECTS = 0;

    public final static int NUM_PARTICLE_EFFECTS = 46;
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
            PARTICLE_SHIELD_IRON = 0x1C,
            PARTICLE_SHIELD_IRONALLY = 0x1D,
            PARTICLE_SHIELD_FORTIFYBUFF = 0x1E,
            PARTICLE_SHIELD_TOSS = 0x1F,
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
            PARTICLE_BLOOD_HIT = 0x2E;

    public final static int NUM_KEYBINDS = 16,
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
            KEYBIND_DOWN = 15;

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

    public final static byte NUM_EQUIP_SLOTS = 11;

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

    public final static double HP_BASE = 3000, // PvE = 100
            HP_MULT = 200, // PvE = 30
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
    public final static int PACKET_MAX_SIZE = 512;
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

    public final static BufferedImage[][] CHAR_SPRITE = new BufferedImage[NUM_PLAYER_ANIM_STATE][];
    public final static BufferedImage[] HUD = new BufferedImage[2];

    public final static BufferedImage[] MENU_BG = new BufferedImage[5];
    public final static BufferedImage[] MENU_SMOKE = new BufferedImage[1];
    public final static BufferedImage[] MENU_UPGRADEPARTICLE = new BufferedImage[4];
    public final static BufferedImage[] MENU_BUTTON = new BufferedImage[16];
    public final static BufferedImage[] MENU_WINDOW = new BufferedImage[2];
    public final static BufferedImage[] MENU_TABPOINTER = new BufferedImage[2];
    public final static BufferedImage[] MENU_ITEMDELETE = new BufferedImage[1];

    // Use Cooper Std Black size 25
    public final static BufferedImage[][] DAMAGE_FONT = new BufferedImage[4][10];
    public final static BufferedImage[] EXP_WORD = new BufferedImage[1];

    public final static byte NUM_BGM = 4,
            BGM_MENU = 0x00,
            BGM_ARENA1 = 0x01,
            BGM_ARENA2 = 0x02,
            BGM_ARENA3 = 0x03;

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

    public final static int[] PLAYER_ANIM_FRAMES = new int[NUM_PLAYER_ANIM_STATE];

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
            BUTTON_SMALLRECT = 15;

    public final static byte WINDOW_CREATECHAR = 0,
            WINDOW_DESTROYCONFIRM = 1;

    public static final byte NUMBER_TYPE_PLAYER = 0,
            NUMBER_TYPE_PLAYERCRIT = 1,
            NUMBER_TYPE_MOB = 2;

    public static final byte NOTIFICATION_EXP = 0,
            NOTIFICATION_ITEMEQUIP = 1,
            NOTIFICATION_ITEMUPGRADE = 2;

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