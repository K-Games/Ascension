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
    public static String SERVER_ADDRESS = "222.153.137.224";

    public final static int WINDOW_WIDTH = 1280;
    public final static int WINDOW_HEIGHT = 720;

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

    public final static int NUM_PLAYER_STATE = 5;
    public final static byte PLAYER_STATE_STAND = 0x00,
            PLAYER_STATE_WALK = 0x01,
            PLAYER_STATE_JUMP = 0x02,
            PLAYER_STATE_STUN = 0x03,
            PLAYER_STATE_KNOCKBACK = 0x04;

    public final static BufferedImage[][] CHAR_SPRITE = new BufferedImage[NUM_PLAYER_STATE][];
    public final static BufferedImage[][] PARTICLE_SPRITE = new BufferedImage[NUM_PARTICLE_EFFECTS][];

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

    public static void loadCharSprites() {
        try {
            CHAR_SPRITE[PLAYER_STATE_STAND] = new BufferedImage[1];
            CHAR_SPRITE[PLAYER_STATE_STAND][0] = ImageIO.read(Globals.class.getResource("sprites/character/stand/0.png"));

            CHAR_SPRITE[PLAYER_STATE_WALK] = new BufferedImage[2];
            CHAR_SPRITE[PLAYER_STATE_WALK][0] = ImageIO.read(Globals.class.getResource("sprites/character/walk/0.png"));
            CHAR_SPRITE[PLAYER_STATE_WALK][1] = ImageIO.read(Globals.class.getResource("sprites/character/walk/1.png"));

            CHAR_SPRITE[PLAYER_STATE_JUMP] = new BufferedImage[1];
            CHAR_SPRITE[PLAYER_STATE_JUMP][0] = ImageIO.read(Globals.class.getResource("sprites/character/jump/0.png"));
            
            PARTICLE_SPRITE[PARTICLE_KNOCK] = new BufferedImage[5];
            PARTICLE_SPRITE[PARTICLE_KNOCK][0] = ImageIO.read(Globals.class.getResource("sprites/particle/knock/0.png"));
            PARTICLE_SPRITE[PARTICLE_KNOCK][1] = ImageIO.read(Globals.class.getResource("sprites/particle/knock/1.png"));
            PARTICLE_SPRITE[PARTICLE_KNOCK][2] = ImageIO.read(Globals.class.getResource("sprites/particle/knock/2.png"));
            PARTICLE_SPRITE[PARTICLE_KNOCK][3] = ImageIO.read(Globals.class.getResource("sprites/particle/knock/3.png"));
            PARTICLE_SPRITE[PARTICLE_KNOCK][4] = ImageIO.read(Globals.class.getResource("sprites/particle/knock/4.png"));
            
        } catch (IOException ex) {
            Logger.getLogger(Globals.class.getName()).log(Level.SEVERE, null, ex);
        }
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
