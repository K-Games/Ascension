package blockfighter.client.entities;

import blockfighter.client.Globals;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ken
 */
public class Player extends Thread {

    private int x, y, dstX, dstY;
    private byte facing, state, frame;
    private double[] stats = new double[Globals.NUM_STATS];
    private String name;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
        //dstX = x;
        //dstY = y;
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

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        facing = Globals.RIGHT;
        state = Globals.PLAYER_STATE_STAND;
        frame = 0;
    }

    public void draw(Graphics g) {
        byte s = state, f = frame;
        if (f >= Globals.CHAR_SPRITE[s].length) {
            f = 0;
        }
        BufferedImage sprite = Globals.CHAR_SPRITE[s][f];
        int drawSrcX = x - ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth() / 2;
        int drawSrcY = y - sprite.getHeight();
        int drawDscX = x + ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth() / 2;
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, y, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }

    public void setStats(byte statType, double stat) {
        stats[statType] = stat;
        updateStats();
    }

    public double getStat(byte statType) {
        return stats[statType];
    }

    private void updateStats() {
        stats[Globals.STAT_ARMOR] = Globals.calcArmor(stats[Globals.STAT_DEFENSE]);
        stats[Globals.STAT_REGEN] = Globals.calcRegen(stats[Globals.STAT_SPIRIT]);
        stats[Globals.STAT_MAXHP] = Globals.calcMaxHP(stats[Globals.STAT_DEFENSE]);
        stats[Globals.STAT_MINHP] = stats[Globals.STAT_MAXHP];
        stats[Globals.STAT_MINDMG] = Globals.calcMinDmg(stats[Globals.STAT_POWER]);
        stats[Globals.STAT_MAXDMG] = Globals.calcMaxDmg(stats[Globals.STAT_POWER]);
        stats[Globals.STAT_CRITCHANCE] = Globals.calcCritChance(stats[Globals.STAT_SPIRIT]);
        stats[Globals.STAT_CRITDMG] = Globals.calcCritDmg(stats[Globals.STAT_POWER]);
    }

    public void setPlayerName(String n) {
        name = n;
    }

    public String getPlayerName() {
        return name;
    }

    @Override
    public void run() {
        if (x != dstX) {
            x += (dstX - x) / (100000000F / Globals.LOGIC_UPDATE);
        }
        if (y != dstY) {
            y += (dstY - y) / (100000000F / Globals.LOGIC_UPDATE);
        }
    }
}
