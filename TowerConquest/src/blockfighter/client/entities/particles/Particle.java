package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import static blockfighter.client.Globals.NUM_PARTICLE_EFFECTS;
import blockfighter.client.LogicModule;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Ken Kwan
 */
public abstract class Particle extends Thread {

    protected int x, y;
    protected double size = 10.0;
    protected long frameDuration;
    protected int frame = 0;
    protected byte facing = Globals.RIGHT;

    protected static BufferedImage[][] PARTICLE_SPRITE;
    protected final int key;
    private static boolean LOADED = false;
    protected final static Random rng = new Random();
    /**
     * Reference to Logic Module.
     */
    protected static LogicModule logic;

    /**
     * The duration of this particle in ms.
     */
    protected long duration;

    public static void setLogic(LogicModule l) {
        logic = l;
    }

    public static void unloadParticles() {
        for (int i = 0; PARTICLE_SPRITE != null && i < PARTICLE_SPRITE.length; i++) {
            for (int j = 0; PARTICLE_SPRITE[i] != null && j < PARTICLE_SPRITE[i].length; j++) {
                PARTICLE_SPRITE[i][j] = null;
            }
            PARTICLE_SPRITE[i] = null;
        }
        PARTICLE_SPRITE = null;
        LOADED = false;
        System.gc();
    }

    public static void loadParticles() {
        if (LOADED) {
            return;
        }
        unloadParticles();
        LOADED = true;
        PARTICLE_SPRITE = new BufferedImage[NUM_PARTICLE_EFFECTS][];
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH1] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH1].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH1][i] = ImageIO.read(Globals.class.getResource("sprites/particle/slash1/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH2] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH2].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH2][i] = ImageIO.read(Globals.class.getResource("sprites/particle/slash2/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH3] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH3].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH3][i] = ImageIO.read(Globals.class.getResource("sprites/particle/slash3/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_DRIVE] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_DRIVE].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_DRIVE][i] = ImageIO.read(Globals.class.getResource("sprites/particle/drive/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_VORPAL] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_VORPAL].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_VORPAL][i] = ImageIO.read(Globals.class.getResource("sprites/particle/vorpal/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_MULTI] = new BufferedImage[24];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_MULTI].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_MULTI][i] = ImageIO.read(Globals.class.getResource("sprites/particle/multi/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_CINDER] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_CINDER].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_CINDER][i] = ImageIO.read(Globals.class.getResource("sprites/particle/cinder/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BURN] = new BufferedImage[20];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BURN].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_BURN][i] = ImageIO.read(Globals.class.getResource("sprites/particle/burn/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNT] = new BufferedImage[16];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNT].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNT][i] = ImageIO.read(Globals.class.getResource("sprites/particle/taunt/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA1] = new BufferedImage[20];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA1].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA1][i] = ImageIO.read(Globals.class.getResource("sprites/particle/tauntaura/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA2] = new BufferedImage[10];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA2].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA2][i] = ImageIO.read(Globals.class.getResource("sprites/particle/tauntaura2/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_ARC] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_ARC].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_BOW_ARC][i] = ImageIO.read(Globals.class.getResource("sprites/particle/arc/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_RAPID] = new BufferedImage[6];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_RAPID].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_BOW_RAPID][i] = ImageIO.read(Globals.class.getResource("sprites/particle/rapid/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWER] = new BufferedImage[24];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWER].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWER][i] = ImageIO.read(Globals.class.getResource("sprites/particle/power/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERPARTICLE] = new BufferedImage[10];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERPARTICLE].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERPARTICLE][i] = ImageIO.read(Globals.class.getResource("sprites/particle/power2/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERCHARGE] = new BufferedImage[24];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERCHARGE].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERCHARGE][i] = ImageIO.read(Globals.class.getResource("sprites/particle/powercharge/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBOW] = new BufferedImage[1];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBOW].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYBOW][i] = ImageIO.read(Globals.class.getResource("sprites/particle/volleybow/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYARROW] = new BufferedImage[6];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYARROW].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYARROW][i] = ImageIO.read(Globals.class.getResource("sprites/particle/volleyarrow/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_STORMARROW] = new BufferedImage[20];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_STORMARROW].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_BOW_STORMARROW][i] = ImageIO.read(Globals.class.getResource("sprites/particle/stormarrow/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BOW_FROSTARROW] = new BufferedImage[12];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BOW_FROSTARROW].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_BOW_FROSTARROW][i] = ImageIO.read(Globals.class.getResource("sprites/particle/frostarrow/" + i + ".png"));
            } catch (Exception ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void update() {
        duration -= Globals.LOGIC_UPDATE / 1000000;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getKey() {
        return key;
    }

    @Override
    public void run() {
        update();
    }

    public boolean isExpired() {
        return duration <= 0;
    }

    public Particle(int k, int x, int y) {
        key = k;
        this.x = x;
        this.y = y;
        duration = 200;
    }

    public Particle(int k, int x, int y, byte f) {
        key = k;
        this.x = x;
        this.y = y;
        facing = f;
        duration = 200;
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawRect(x, y, 560, 150);
    }

    public void setExpire() {
        duration = 0;
    }
}
