package blockfighter.client.entities;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ken
 */
public class Player extends Thread {

    private int x, y;
    private byte key, facing, state, frame;
    private double[] stats = new double[Globals.NUM_STATS];
    private String name;
    private ItemEquip[] equipment = new ItemEquip[Globals.NUM_EQUIP_SLOTS];
    private long lastUpdateTime = 5000;
    private LogicModule logic;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getStat(byte statType) {
        return stats[statType];
    }

    public String getPlayerName() {
        return name;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
        lastUpdateTime = 5000;
    }

    public void setFacing(byte dir) {
        facing = dir;
        lastUpdateTime = 5000;
    }

    public void setState(byte s) {
        state = s;
        lastUpdateTime = 5000;
    }

    public void setFrame(byte f) {
        frame = f;
        lastUpdateTime = 5000;
    }

    public void setStats(byte statType, double stat) {
        stats[statType] = stat;
        lastUpdateTime = 5000;
    }

    public void setPlayerName(String n) {
        name = n;
        lastUpdateTime = 5000;
    }

    public Player(int x, int y, LogicModule l, byte k) {
        this.x = x;
        this.y = y;
        logic = l;
        key = k;
        facing = Globals.RIGHT;
        state = Globals.PLAYER_STATE_STAND;
        name = "";
        frame = 0;
    }

    public void draw(Graphics2D g) {
        byte s = state, f = frame;
        if (f >= Globals.CHAR_SPRITE[s].length) {
            f = 0;
        }
        BufferedImage sprite = Globals.CHAR_SPRITE[s][f];
        int drawSrcX = x - ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth() / 2;
        int drawSrcY = y - sprite.getHeight();
        int drawDscX = x + ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth() / 2;
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, y, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
        g.setFont(Globals.ARIAL_18PT);
        g.setColor(Color.BLACK);
        g.drawString(name, x - 39, y + 20);
        g.drawString(name, x - 41, y + 20);
        g.drawString(name, x - 40, y + 19);
        g.drawString(name, x - 40, y + 21);
        g.setColor(Color.WHITE);
        g.drawString(name, x - 40, y + 20);
    }

    @Override
    public void run() {
        lastUpdateTime -= Globals.LOGIC_UPDATE / 1000000;
        if (name.length() <= 0) {
            logic.sendGetName(key);
        }
    }

    public boolean isDisconnected() {
        return lastUpdateTime <= 0;
    }
}
