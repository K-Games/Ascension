package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import static blockfighter.client.Globals.NUM_PARTICLE_EFFECTS;
import blockfighter.client.LogicModule;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
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

    protected final static BufferedImage[][] PARTICLE_SPRITE = new BufferedImage[NUM_PARTICLE_EFFECTS][];
    protected final int key;
    private static boolean LOADED = false;
    protected final static Random rng = new Random();
    /**
     * Reference to Logic Module.
     */
    protected final LogicModule logic;

    /**
     * The duration of this particle in ms.
     */
    protected long duration;

    public static void unloadParticles() {
        for (int i = 0; i < PARTICLE_SPRITE.length; i++) {
            for (int j = 0; j<PARTICLE_SPRITE[i].length;j++){
                PARTICLE_SPRITE[i][j] = null;
            }
            PARTICLE_SPRITE[i] = null;
        }
        LOADED = false;
    }

    public static void loadParticles() {
        if (LOADED) {
            return;
        }
        LOADED = true;
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH1] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH1].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH1][i] = ImageIO.read(Globals.class.getResource("sprites/particle/slash1/" + i + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH2] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH2].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH2][i] = ImageIO.read(Globals.class.getResource("sprites/particle/slash2/" + i + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH3] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH3].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_SLASH3][i] = ImageIO.read(Globals.class.getResource("sprites/particle/slash3/" + i + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_DRIVE] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_DRIVE].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_DRIVE][i] = ImageIO.read(Globals.class.getResource("sprites/particle/drive/" + i + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_VORPAL] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_VORPAL].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_VORPAL][i] = ImageIO.read(Globals.class.getResource("sprites/particle/vorpal/" + i + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_MULTI] = new BufferedImage[24];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_MULTI].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_MULTI][i] = ImageIO.read(Globals.class.getResource("sprites/particle/multi/" + i + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_CINDER] = new BufferedImage[8];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_CINDER].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_CINDER][i] = ImageIO.read(Globals.class.getResource("sprites/particle/cinder/" + i + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_BURN] = new BufferedImage[20];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_BURN].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_BURN][i] = ImageIO.read(Globals.class.getResource("sprites/particle/burn/" + i + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNT] = new BufferedImage[16];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNT].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNT][i] = ImageIO.read(Globals.class.getResource("sprites/particle/taunt/" + i + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA1] = new BufferedImage[20];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA1].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA1][i] = ImageIO.read(Globals.class.getResource("sprites/particle/tauntaura/" + i + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(Particle.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA2] = new BufferedImage[10];
        for (int i = 0; i < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA2].length; i++) {
            try {
                PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA2][i] = ImageIO.read(Globals.class.getResource("sprites/particle/tauntaura2/" + i + ".png"));
            } catch (IOException ex) {
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

    public Particle(LogicModule l, int k, int x, int y) {
        logic = l;
        key = k;
        this.x = x;
        this.y = y;
        duration = 200;
    }

    public Particle(LogicModule l, int k, int x, int y, byte f) {
        logic = l;
        key = k;
        this.x = x;
        this.y = y;
        facing = f;
        duration = 200;
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.blue);
        g.fillRect((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);
    }

    public void setExpire() {
        duration = 0;
    }
}
