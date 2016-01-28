package blockfighter.client.entities.boss;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.entities.boss.Lightning.BossLightning;
import java.awt.Graphics2D;
import java.awt.Point;

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

    public Point getPos() {
        return new Point(this.x, this.y);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public double getStat(final byte statType) {
        return this.stats[statType];
    }

    public void setPos(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public void setFacing(final byte dir) {
        this.facing = dir;
    }

    public void setState(final byte s) {
        this.state = s;
    }

    public void setFrame(final byte f) {
        this.frame = f;
    }

    public void setStat(final byte statType, final double stat) {
        this.stats[statType] = stat;
    }

    public static void setLogic(final LogicModule l) {
        logic = l;
    }

    public Boss(final int x, final int y, final byte k) {
        this.x = x;
        this.y = y;
        this.key = k;
        this.facing = Globals.RIGHT;
        this.state = STATE_STAND;
        this.frame = 0;
        setDaemon(true);
    }

    public abstract void addParticle(byte[] data);

    public void draw(final Graphics2D g) {
        g.drawRect(this.x - 25, this.y - 50, 50, 50);
        /*
		 * if (f >= Globals.CHAR_SPRITE[s].length) { return; } BufferedImage sprite = Globals.CHAR_SPRITE[s][f]; int drawSrcX = x - ((facing
		 * == Globals.RIGHT) ? 1 : -1) * sprite.getWidth() / 2; int drawSrcY = y - sprite.getHeight(); int drawDscY = drawSrcY +
		 * sprite.getHeight(); int drawDscX = drawSrcX + ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth(); g.drawImage(sprite,
		 * drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
         */
    }

    @Override
    public void run() {
        update();
        if (this.stats[STAT_MAXHP] <= 0) {
            // Get boss stat
            logic.sendGetBossStat(this.key, STAT_MAXHP);
        }
    }

    public abstract void update();

    public static Boss spawnBoss(final byte type, final byte key, final int x, final int y) {
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
