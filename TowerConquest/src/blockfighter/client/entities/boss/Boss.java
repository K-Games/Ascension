package blockfighter.client.entities.boss;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.entities.boss.Lightning.BossLightning;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;

/**
 *
 * @author Ken Kwan
 */
public abstract class Boss extends Thread {

    public final static int NUM_STATS = 3;
    public final static byte STAT_LEVEL = 0,
            STAT_MAXHP = 1,
            STAT_MINHP = 2;
    public final static byte STATE_STAND = 0x00,
            STATE_WALK = 0x01,
            STATE_JUMP = 0x02;

    public final static byte BOSS_LIGHTNING = 0x00;

    protected int x, y;
    protected byte key, facing, state, frame;
    protected double[] stats;
    protected static LogicModule logic;
    protected final static Random rng = new Random();

    public Point getPos() {
        return new Point(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getStat(byte statType) {
        return stats[statType];
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setFacing(byte dir) {
        facing = dir;
    }

    public void setState(byte s) {
        state = s;
    }

    public void setFrame(byte f) {
        frame = f;
    }

    public void setStat(byte statType, double stat) {
        stats[statType] = stat;
    }

    public static void setLogic(LogicModule l) {
        logic = l;
    }

    public Boss(int x, int y, byte k) {
        this.x = x;
        this.y = y;
        key = k;
        facing = Globals.RIGHT;
        state = STATE_STAND;
        frame = 0;
        setDaemon(true);
    }

    public abstract void addParticle(byte[] data);

    public void draw(Graphics2D g) {
        g.drawRect(x - 25, y - 50, 50, 50);
        /*        if (f >= Globals.CHAR_SPRITE[s].length) {
         return;
         }
         BufferedImage sprite = Globals.CHAR_SPRITE[s][f];
         int drawSrcX = x - ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth() / 2;
         int drawSrcY = y - sprite.getHeight();
         int drawDscY = drawSrcY + sprite.getHeight();
         int drawDscX = drawSrcX + ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth();
         g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);*/
    }

    @Override
    public void run() {
        update();
        if (stats[STAT_MAXHP] <= 0) {
            //Get boss stat
            logic.sendGetBossStat(key, STAT_MAXHP);
        }
    }

    public abstract void update();

    public static Boss spawnBoss(byte type, byte key, int x, int y) {
        Boss b = null;
        switch (type) {
            case BOSS_LIGHTNING:
                b = new BossLightning(x, y, key);
                BossLightning.load();
                break;
        }
        return b;
    }

    public abstract void unload();
}
