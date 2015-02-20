package blockfighter.client.entities.player;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ken Kwan
 */
public class Player extends Thread {

    private int x, y;
    private byte key, facing, state, frame;
    private double[] stats = new double[Globals.NUM_STATS];
    private String name;
    private ItemEquip[] equipment = new ItemEquip[Globals.NUM_EQUIP_SLOTS];
    private long lastUpdateTime = 5000;
    private static LogicModule logic;

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

    public void setEquip(byte slot, int itemCode) {
        equipment[slot] = new ItemEquip(itemCode);
    }

    public void setStat(byte statType, double stat) {
        stats[statType] = stat;
        lastUpdateTime = 5000;
    }

    public void setPlayerName(String n) {
        name = n;
        lastUpdateTime = 5000;
    }

    public static void setLogic(LogicModule l) {
        logic = l;
    }

    public Player(int x, int y, byte k) {
        this.x = x;
        this.y = y;
        key = k;
        facing = Globals.RIGHT;
        state = Globals.PLAYER_STATE_STAND;
        name = "";
        frame = 0;
    }

    public void draw(Graphics2D g) {
        byte s = state, f = frame;
        if (f >= Globals.CHAR_SPRITE[s].length) {
            return;
        }
        BufferedImage sprite = Globals.CHAR_SPRITE[s][f];
        int drawSrcX = x - ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth() / 2;
        int drawSrcY = y - sprite.getHeight();
        int drawDscY = drawSrcY + sprite.getHeight();
        int drawDscX = drawSrcX + ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth();
        switch (s) {
            case Globals.PLAYER_STATE_ATTACK1:
                drawSrcX += ((facing == Globals.RIGHT) ? 1 : -1) * 10;
                drawDscX += ((facing == Globals.RIGHT) ? 1 : -1) * 10;
                break;
            case Globals.PLAYER_STATE_ATTACK2:
                drawSrcX += ((facing == Globals.RIGHT) ? 1 : -1) * 25;
                drawDscX += ((facing == Globals.RIGHT) ? 1 : -1) * 25;
                break;
            case Globals.PLAYER_STATE_ATTACKOFF1:
                drawSrcX += ((facing == Globals.RIGHT) ? 1 : -1) * 40;
                drawDscX += ((facing == Globals.RIGHT) ? 1 : -1) * 40;
                break;
            case Globals.PLAYER_STATE_ATTACKOFF2:
                drawSrcX += ((facing == Globals.RIGHT) ? 1 : -1) * 40;
                drawDscX += ((facing == Globals.RIGHT) ? 1 : -1) * 40;
                break;
        }
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
        if (equipment[Globals.ITEM_OFFHAND] != null) {
            equipment[Globals.ITEM_OFFHAND].drawIngame(g, x, y, s, f, facing, true);
        }
        if (equipment[Globals.ITEM_CHEST] != null) {
            equipment[Globals.ITEM_CHEST].drawIngame(g, x, y, s, f, facing);
        }
        if (equipment[Globals.ITEM_SHOULDER] != null) {
            equipment[Globals.ITEM_SHOULDER].drawIngame(g, x, y, s, f, facing);
        }

        if (equipment[Globals.ITEM_PANTS] != null) {
            equipment[Globals.ITEM_PANTS].drawIngame(g, x, y, s, f, facing);
        }
        if (equipment[Globals.ITEM_SHOE] != null) {
            equipment[Globals.ITEM_SHOE].drawIngame(g, x, y, s, f, facing);
        }
        if (equipment[Globals.ITEM_WEAPON] != null) {
            equipment[Globals.ITEM_WEAPON].drawIngame(g, x, y, s, f, facing);
        }
        if (equipment[Globals.ITEM_GLOVE] != null) {
            equipment[Globals.ITEM_GLOVE].drawIngame(g, x, y, s, f, facing);
        }

        g.setFont(Globals.ARIAL_18PT);
        int width = g.getFontMetrics().stringWidth(name);
        g.setColor(Color.BLACK);
        g.drawString(name, x - width / 2 - 1, y + 20);
        g.drawString(name, x - width / 2 + 1, y + 20);
        g.drawString(name, x - width / 2, y + 19);
        g.drawString(name, x - width / 2, y + 21);
        g.setColor(Color.WHITE);
        g.drawString(name, x - width / 2, y + 20);
    }

    @Override
    public void run() {
        lastUpdateTime -= Globals.LOGIC_UPDATE / 1000000;
        if (name.length() <= 0) {
            logic.sendGetName(key);
        }
        for (ItemEquip e : equipment) {
            if (e == null) {
                logic.sendGetEquip(key);
                break;
            }
        }
        if (stats[Globals.STAT_MAXHP] <= 0) {
            logic.sendGetStat(key, Globals.STAT_MAXHP);
        }
    }

    public boolean isDisconnected() {
        return lastUpdateTime <= 0;
    }
}
