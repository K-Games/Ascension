package blockfighter.client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author ckwa290
 */
public class Globals {

    public final static int SERVER_PORT = 25565;
    public static String SERVER_ADDRESS = "127.0.0.1";

    public final static int WINDOW_WIDTH = 1280;
    public final static int WINDOW_HEIGHT = 720;

    public final static byte SCREEN_CHAR_SELECT = 0x00,
            SCREEN_CHAR_STATS = 0x01,
            SCREEN_CHAR_EQUIPS = 0x02,
            SCREEN_CHAR_UPGRADE = 0x03,
            SCREEN_CHAR_SKILLS = 0x04,
            SCREEN_CHAR_OPTIONS = 0x05,
            SCREEN_CHAR_SERVERS = 0x06,
            SCREEN_INGAME = 0x07;

    //Render globals
    public final static double RENDER_FPS = 60.0;
    public final static double RENDER_UPDATE = 1000000000 / RENDER_FPS;

    public final static double LOGIC_TICKS_PER_SEC = 40.0;
    public final static double LOGIC_UPDATE = 1000000000 / LOGIC_TICKS_PER_SEC;

    public final static double REQUESTALL_TICKS_PER_SEC = 2;
    public final static double REQUESTALL_UPDATE = 1000000000 / REQUESTALL_TICKS_PER_SEC;

    public final static double PINGS_PER_SEC = 1;
    public final static double PING_UPDATE = 1000000000 / PINGS_PER_SEC;

    public final static double PROCESS_QUEUES_PER_SEC = 100;
    public final static double QUEUES_UPDATE = 1000000000 / PROCESS_QUEUES_PER_SEC;

    public final static byte RIGHT = 0, LEFT = 1, DOWN = 2, UP = 3;

    public final static int NUM_PARTICLE_EFFECTS = 1;
    public final static byte PARTICLE_KNOCK = 0x00;

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
            CRITCHC_MULT = 0.01,
            CRITCHC_CONST = 750 / 0.85 - 750,
            CRITDMG_BASE = 0.5,
            CRITDMG_FACT = 5.5,
            CRITDMG_MULT = 0.01,
            MINDMG_MULT = 11,
            MAXDMG_MULT = 21,
            MINDMG_BASE = 20,
            MAXDMG_BASE = 40;

    public final static int NUM_PLAYER_STATE = 5;
    public final static byte PLAYER_STATE_STAND = 0x00,
            PLAYER_STATE_WALK = 0x01,
            PLAYER_STATE_JUMP = 0x02,
            PLAYER_STATE_STUN = 0x03,
            PLAYER_STATE_KNOCKBACK = 0x04;

    public final static BufferedImage[][] CHAR_SPRITE = new BufferedImage[NUM_PLAYER_STATE][];
    public final static BufferedImage[][] PARTICLE_SPRITE = new BufferedImage[NUM_PARTICLE_EFFECTS][];
    public final static BufferedImage[] HUD = new BufferedImage[1];
    
    public final static BufferedImage[] MENU_BG = new BufferedImage[1];
    public final static BufferedImage[] MENU_SMOKE = new BufferedImage[1];
    public final static BufferedImage[] MENU_BUTTON = new BufferedImage[2];
    
    public final static byte BUTTON_OKAY = 0,
            BUTTON_SELECTCHAR = 1;

    //Packet globals
    public final static int PACKET_MAX_SIZE = 128;
    public final static int PACKET_BYTE = 1;
    public final static int PACKET_INT = 4;
    public final static int PACKET_CHAR = 1;

    //Datatypes
    public final static byte DATA_PING = 0x00,
            DATA_LOGIN = 0x01,
            DATA_GET_ALL_PLAYER = 0x02,
            DATA_SET_PLAYER_MOVE = 0x03,
            DATA_SET_PLAYER_POS = 0x04,
            DATA_SET_PLAYER_FACING = 0x05,
            DATA_SET_PLAYER_STATE = 0x06,
            DATA_PLAYER_ACTION = 0x07,
            DATA_PARTICLE_EFFECT = 0x08,
            DATA_PARTICLE_REMOVE = 0x09;

    public final static byte NUM_PLAYER_ACTION = 1,
            PLAYER_ACTION_KNOCK = 0x00;

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
        double chc = spirit * CRITCHC_MULT + CRITCHC_BASE;
        if (chc > 0.85) {
            chc = spirit / (spirit + CRITCHC_CONST);
        }
        return chc;
    }

    public static final double calcCritDmg(double spirit) {
        return spirit / CRITDMG_FACT * CRITDMG_MULT + CRITDMG_BASE;
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
        return (input[0] & 0xff | (input[1]& 0xff) << 8 | (input[2]& 0xff) << 16 | (input[3]& 0xff) << 24);
    }

    public static void loadGFX() {
        try {
            CHAR_SPRITE[PLAYER_STATE_STAND] = new BufferedImage[9];
            for (int i = 0; i < CHAR_SPRITE[PLAYER_STATE_STAND].length; i++) {
                CHAR_SPRITE[PLAYER_STATE_STAND][i] = ImageIO.read(Globals.class.getResource("sprites/character/stand/" + i + ".png"));
            }

            CHAR_SPRITE[PLAYER_STATE_WALK] = new BufferedImage[19];
            for (int i = 0; i < CHAR_SPRITE[PLAYER_STATE_WALK].length; i++) {
                CHAR_SPRITE[PLAYER_STATE_WALK][i] = ImageIO.read(Globals.class.getResource("sprites/character/walk/" + i + ".png"));
            }

            CHAR_SPRITE[PLAYER_STATE_JUMP] = new BufferedImage[1];
            CHAR_SPRITE[PLAYER_STATE_JUMP][0] = ImageIO.read(Globals.class.getResource("sprites/character/jump/0.png"));

            PARTICLE_SPRITE[PARTICLE_KNOCK] = new BufferedImage[5];
            PARTICLE_SPRITE[PARTICLE_KNOCK][0] = ImageIO.read(Globals.class.getResource("sprites/particle/knock/0.png"));
            PARTICLE_SPRITE[PARTICLE_KNOCK][1] = ImageIO.read(Globals.class.getResource("sprites/particle/knock/1.png"));
            PARTICLE_SPRITE[PARTICLE_KNOCK][2] = ImageIO.read(Globals.class.getResource("sprites/particle/knock/2.png"));
            PARTICLE_SPRITE[PARTICLE_KNOCK][3] = ImageIO.read(Globals.class.getResource("sprites/particle/knock/3.png"));
            PARTICLE_SPRITE[PARTICLE_KNOCK][4] = ImageIO.read(Globals.class.getResource("sprites/particle/knock/4.png"));

            HUD[0] = ImageIO.read(Globals.class.getResource("sprites/ui/ingame/ui.png"));
            MENU_BG[0] = ImageIO.read(Globals.class.getResource("sprites/ui/menu/bg.png"));

            MENU_BUTTON[BUTTON_OKAY] = ImageIO.read(Globals.class.getResource("sprites/ui/menu/button1.png"));
            MENU_BUTTON[BUTTON_SELECTCHAR] = ImageIO.read(Globals.class.getResource("sprites/ui/menu/button2.png"));

            MENU_SMOKE[0] = ImageIO.read(Globals.class.getResource("sprites/ui/menu/smoke.png"));
        } catch (IOException ex) {
            Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static final long nsToMs(double time) {
        return (long) (time / 1000000);
    }

    public static final void savePlayer() {

    }
}
