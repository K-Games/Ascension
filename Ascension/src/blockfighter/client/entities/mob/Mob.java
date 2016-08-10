package blockfighter.client.entities.mob;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.Main;
import blockfighter.client.entities.mob.boss.Lightning.BossLightning;
import blockfighter.client.net.GameClient;
import blockfighter.client.net.PacketSender;
import java.awt.Graphics2D;
import java.awt.Point;

public abstract class Mob extends Thread {

    public final static int NUM_STATS = 3;
    public final static byte STAT_LEVEL = 0,
            STAT_MAXHP = 1,
            STAT_MINHP = 2;
    public final static byte ANIM_STAND = 0x00,
            ANIM_WALK = 0x01,
            ANIM_JUMP = 0x02,
            ANIM_DYING = 0x03,
            ANIM_DEAD = 0x04;

    public final static byte MOB_BOSS_LIGHTNING = 0x00,
            MOB_BOSS_SHADOWFIEND = 0x01;

    protected int x, y;
    protected byte facing, animState, frame;
    protected int key;
    protected double[] stats;
    protected static LogicModule logic;

    public Mob(final int x, final int y, final int k) {
        this.x = x;
        this.y = y;
        this.key = k;
        this.facing = Globals.RIGHT;
        this.animState = ANIM_STAND;
        this.frame = 0;
        setDaemon(true);
    }

    public static void init() {
        logic = Main.getLogicModule();
    }

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
        this.animState = s;
    }

    public void setFrame(final byte f) {
        this.frame = f;
    }

    public void setStat(final byte statType, final double stat) {
        this.stats[statType] = stat;
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
            PacketSender.sendGetMobStat(logic.getSelectedRoom(), this.key, STAT_MAXHP);
        }
    }

    public abstract void update();

    public static Mob spawnMob(final byte type, final int key, final int x, final int y, final GameClient cl) {
        Mob b = null;
        switch (type) {
            case MOB_BOSS_LIGHTNING:
                b = new BossLightning(x, y, key);
                BossLightning.load();
                break;
        }
        return b;
    }

    public abstract void unload();
}
