package blockfighter.client;

import blockfighter.client.entities.damage.Damage;
import blockfighter.client.entities.player.skills.Skill;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Ken Kwan
 */
public class Globals {

    public final static int SERVER_PORT = 25565;
    private final static boolean isPublic = false;
    public static String SERVER_ADDRESS = (isPublic) ? "towerconquest.servegame.com" : "192.168.1.2";
    public final static String GAME_VERSION = "ALPHA 0";
    public final static String WINDOW_TITLE = "Tower Conquest " + GAME_VERSION;
    public final static int WINDOW_WIDTH = 1280;
    public final static int WINDOW_HEIGHT = 720;

    //Render globals
    public final static Font ARIAL_30PT = new Font("Arial", Font.PLAIN, 30);
    public final static Font ARIAL_12PT = new Font("Arial", Font.PLAIN, 12);
    public final static Font ARIAL_15PT = new Font("Arial", Font.BOLD, 15);
    public final static Font ARIAL_15PT_ITALIC = new Font("Arial", Font.ITALIC, 15);
    public final static Font ARIAL_24PT = new Font("Arial", Font.PLAIN, 24);
    public final static Font ARIAL_18PT = new Font("Arial", Font.PLAIN, 18);

    public final static byte MAX_NAME_LENGTH = 15;

    public final static double RENDER_FPS = 63D;
    public final static double RENDER_UPDATE = 1000000000D / RENDER_FPS;

    public final static double LOGIC_TICKS_PER_SEC = 40D;
    public final static double LOGIC_UPDATE = 1000000000D / LOGIC_TICKS_PER_SEC;

    public final static double DMG_TICKS_PER_SEC = 60D;
    public final static double DMG_UPDATE = 1000000000D / DMG_TICKS_PER_SEC;

    public final static double SEND_KEYDOWN_PER_SEC = 15D;
    public final static double SEND_KEYDOWN_UPDATE = 1000000000D / SEND_KEYDOWN_PER_SEC;

    public final static double REQUESTALL_TICKS_PER_SEC = 1D;
    public final static double REQUESTALL_UPDATE = 5000000000D / REQUESTALL_TICKS_PER_SEC;

    public final static double PINGS_PER_SEC = 1D;
    public final static double PING_UPDATE = 1000000000D / PINGS_PER_SEC;

    public final static double PROCESS_QUEUES_PER_SEC = 100D;
    public final static double QUEUES_UPDATE = 1000000000D / PROCESS_QUEUES_PER_SEC;

    public final static byte RIGHT = 0, LEFT = 1, DOWN = 2, UP = 3;

    public final static int NUM_PARTICLE_EFFECTS = 39;
    public final static byte PARTICLE_SWORD_SLASH1 = 0x00,
            PARTICLE_SWORD_SLASH2 = 0x01,
            PARTICLE_SWORD_SLASH3 = 0x02,
            PARTICLE_SWORD_DRIVE = 0x03,
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
            PARTICLE_PASSIVE_SHADOWATTACK = 0x26;

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
            ITEM_WEAPON = 0, //ITEM_WEAPON is the equipment slot
            ITEM_SWORD = 0, //ITEM_SWORD is the item type.
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
            ITEM_SHIELD = 10,
            ITEM_BOW = 11,
            ITEM_QUIVER = 12; //Only used for equipment slot index. Its the same as weapons.

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

    public final static double HP_BASE = 3000,//PvE = 100
            HP_MULT = 200,//PvE = 30
            REDUCT_CONST = 150,
            ARMOR_MULT = 6,
            REGEN_MULT = 1.5,
            CRITCHC_BASE = 0.1,
            CRITCHC_FACT = 10,
            CRITCHC_MULT = 0.01,
            CRITCHC_CONST = 400,
            CRITDMG_BASE = 0.5,
            CRITDMG_FACT = 5.5,
            CRITDMG_MULT = 0.01,
            MINDMG_MULT = 11,
            MAXDMG_MULT = 21,
            MINDMG_BASE = 20,
            MAXDMG_BASE = 40,
            STAT_PER_LEVEL = 7;

    public final static int NUM_PLAYER_STATE = 10;
    public final static byte PLAYER_STATE_STAND = 0x00,
            PLAYER_STATE_WALK = 0x01,
            PLAYER_STATE_JUMP = 0x02,
            PLAYER_STATE_ATTACK1 = 0x03,
            PLAYER_STATE_ATTACK2 = 0x04,
            PLAYER_STATE_ATTACKOFF1 = 0x05,
            PLAYER_STATE_ATTACKOFF2 = 0x06,
            PLAYER_STATE_ATTACKBOW = 0x07,
            PLAYER_STATE_BUFF = 0x08,
            PLAYER_STATE_DEAD = 0x09;

    //Packet globals
    public final static int PACKET_MAX_SIZE = 512;
    public final static int PACKET_BYTE = 1;
    public final static int PACKET_INT = 4;
    public final static int PACKET_CHAR = 1;

    //Datatypes
    public final static byte DATA_PING = 0x00,
            DATA_LOGIN = 0x01,
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
            DATA_DAMAGE = 0x0F,
            DATA_PLAYER_GIVEEXP = 0x10;

    public final static BufferedImage[][] CHAR_SPRITE = new BufferedImage[NUM_PLAYER_STATE][];
    public final static BufferedImage[] HUD = new BufferedImage[2];

    public final static BufferedImage[] MENU_BG = new BufferedImage[5];
    public final static BufferedImage[] MENU_SMOKE = new BufferedImage[1];
    public final static BufferedImage[] MENU_UPGRADEPARTICLE = new BufferedImage[4];
    public final static BufferedImage[] MENU_BUTTON = new BufferedImage[16];
    public final static BufferedImage[] MENU_WINDOW = new BufferedImage[2];
    public final static BufferedImage[] MENU_TABPOINTER = new BufferedImage[1];
    public final static BufferedImage[] MENU_ITEMDELETE = new BufferedImage[1];

    //Use Cooper Std Black size 25
    public final static BufferedImage[][] DAMAGE_FONT = new BufferedImage[4][10];
    public final static BufferedImage[] EXP_WORD = new BufferedImage[1];

    public final static BufferedImage[] SKILL_ICON = new BufferedImage[Skill.NUM_SKILLS];

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
        double chc = spirit / CRITCHC_FACT * CRITCHC_MULT + CRITCHC_BASE;
        if (chc > 0.5) {
            chc = spirit / (spirit + CRITCHC_CONST);
        }
        return chc;
    }

    public static final double calcCritDmg(double spirit) {
        return spirit / CRITDMG_FACT * CRITDMG_MULT + CRITDMG_BASE;
    }

    public static final double calcReduction(double armor) {
        return armor / (armor + REDUCT_CONST);
    }

    public static final double calcEHP(double reduct, double maxHP) {
        return maxHP / reduct;
    }

    public static final int calcEXPtoNxtLvl(double level) {
        return (int) (Math.round(Math.pow(level, 3.75) + 100));
    }

    public static final byte[] intToByte(int input) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (input & 0xff);
        bytes[1] = (byte) ((input >> 8) & 0xff);
        bytes[2] = (byte) ((input >>> 16) & 0xff);
        bytes[3] = (byte) ((input >>> 24) & 0xff);
        return bytes;
    }

    public static final int bytesToInt(byte[] input) {
        return (input[0] & 0xff | (input[1] & 0xff) << 8 | (input[2] & 0xff) << 16 | (input[3] & 0xff) << 24);
    }

    public static void loadGFX() {
        try {
            CHAR_SPRITE[PLAYER_STATE_ATTACK1] = new BufferedImage[5];
            for (int i = 0; i < CHAR_SPRITE[PLAYER_STATE_ATTACK1].length; i++) {
                CHAR_SPRITE[PLAYER_STATE_ATTACK1][i] = ImageIO.read(Globals.class.getResource("sprites/character/attack/mainhand1/" + i + ".png"));
            }
            CHAR_SPRITE[PLAYER_STATE_ATTACK2] = new BufferedImage[5];
            for (int i = 0; i < CHAR_SPRITE[PLAYER_STATE_ATTACK2].length; i++) {
                CHAR_SPRITE[PLAYER_STATE_ATTACK2][i] = ImageIO.read(Globals.class.getResource("sprites/character/attack/mainhand2/" + i + ".png"));
            }
            CHAR_SPRITE[PLAYER_STATE_ATTACKOFF1] = new BufferedImage[5];
            for (int i = 0; i < CHAR_SPRITE[PLAYER_STATE_ATTACKOFF1].length; i++) {
                CHAR_SPRITE[PLAYER_STATE_ATTACKOFF1][i] = ImageIO.read(Globals.class.getResource("sprites/character/attack/offhand1/" + i + ".png"));
            }
            CHAR_SPRITE[PLAYER_STATE_ATTACKOFF2] = new BufferedImage[5];
            for (int i = 0; i < CHAR_SPRITE[PLAYER_STATE_ATTACKOFF2].length; i++) {
                CHAR_SPRITE[PLAYER_STATE_ATTACKOFF2][i] = ImageIO.read(Globals.class.getResource("sprites/character/attack/offhand2/" + i + ".png"));
            }
            CHAR_SPRITE[PLAYER_STATE_ATTACKBOW] = new BufferedImage[5];
            for (int i = 0; i < CHAR_SPRITE[PLAYER_STATE_ATTACKBOW].length; i++) {
                CHAR_SPRITE[PLAYER_STATE_ATTACKBOW][i] = ImageIO.read(Globals.class.getResource("sprites/character/attack/bow/" + i + ".png"));
            }

            CHAR_SPRITE[PLAYER_STATE_STAND] = new BufferedImage[10];
            for (int i = 0; i < CHAR_SPRITE[PLAYER_STATE_STAND].length; i++) {
                CHAR_SPRITE[PLAYER_STATE_STAND][i] = ImageIO.read(Globals.class.getResource("sprites/character/stand/" + i + ".png"));
            }

            CHAR_SPRITE[PLAYER_STATE_WALK] = new BufferedImage[19];
            for (int i = 0; i < CHAR_SPRITE[PLAYER_STATE_WALK].length; i++) {
                CHAR_SPRITE[PLAYER_STATE_WALK][i] = ImageIO.read(Globals.class.getResource("sprites/character/walk/" + i + ".png"));
            }

            CHAR_SPRITE[PLAYER_STATE_BUFF] = new BufferedImage[10];
            for (int i = 0; i < CHAR_SPRITE[PLAYER_STATE_BUFF].length; i++) {
                CHAR_SPRITE[PLAYER_STATE_BUFF][i] = ImageIO.read(Globals.class.getResource("sprites/character/buff/" + i + ".png"));
            }
            CHAR_SPRITE[PLAYER_STATE_DEAD] = new BufferedImage[15];
            for (int i = 0; i < CHAR_SPRITE[PLAYER_STATE_DEAD].length; i++) {
                CHAR_SPRITE[PLAYER_STATE_DEAD][i] = ImageIO.read(Globals.class.getResource("sprites/character/dead/" + i + ".png"));
            }
            CHAR_SPRITE[PLAYER_STATE_JUMP] = new BufferedImage[1];
            CHAR_SPRITE[PLAYER_STATE_JUMP][0] = ImageIO.read(Globals.class.getResource("sprites/character/jump/0.png"));

            HUD[0] = ImageIO.read(Globals.class.getResource("sprites/ui/ingame/ui.png"));
            HUD[1] = ImageIO.read(Globals.class.getResource("sprites/ui/ingame/hp.png"));

            for (byte i = 0; i < MENU_BG.length; i++) {
                MENU_BG[i] = ImageIO.read(Globals.class.getResource("sprites/ui/menu/bg" + (i + 1) + ".png"));
            }

            for (byte i = 0; i < MENU_BUTTON.length; i++) {
                MENU_BUTTON[i] = ImageIO.read(Globals.class.getResource("sprites/ui/menu/button" + (i + 1) + ".png"));
            }

            for (byte i = 0; i < MENU_WINDOW.length; i++) {
                MENU_WINDOW[i] = ImageIO.read(Globals.class.getResource("sprites/ui/menu/window" + (i + 1) + ".png"));
            }

            for (byte i = 0; i < MENU_UPGRADEPARTICLE.length; i++) {
                MENU_UPGRADEPARTICLE[i] = ImageIO.read(Globals.class.getResource("sprites/ui/menu/particle" + (i + 1) + ".png"));
            }

            MENU_TABPOINTER[0] = ImageIO.read(Globals.class.getResource("sprites/ui/menu/pointer.png"));
            MENU_ITEMDELETE[0] = ImageIO.read(Globals.class.getResource("sprites/ui/menu/delete.png"));
            MENU_SMOKE[0] = ImageIO.read(Globals.class.getResource("sprites/ui/menu/smoke.png"));
            for (byte i = 0; i < 29; i++) {
                SKILL_ICON[i] = ImageIO.read(Globals.class.getResource("sprites/skillicon/" + i + ".png"));
            }

            for (byte i = 0; i < 10; i++) {
                DAMAGE_FONT[Damage.DAMAGE_TYPE_BOSS][i] = ImageIO.read(Globals.class.getResource("sprites/damage/boss/" + i + ".png"));
                DAMAGE_FONT[Damage.DAMAGE_TYPE_PLAYER][i] = ImageIO.read(Globals.class.getResource("sprites/damage/player/" + i + ".png"));
                DAMAGE_FONT[Damage.DAMAGE_TYPE_PLAYERCRIT][i] = ImageIO.read(Globals.class.getResource("sprites/damage/playercrit/" + i + ".png"));
                DAMAGE_FONT[Damage.DAMAGE_TYPE_EXP][i] = ImageIO.read(Globals.class.getResource("sprites/damage/exp/" + i + ".png"));
            }
            EXP_WORD[0] = ImageIO.read(Globals.class.getResource("sprites/damage/exp/exp.png"));

        } catch (IOException ex) {
            Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
