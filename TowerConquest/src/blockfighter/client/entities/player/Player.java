package blockfighter.client.entities.player;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.Main;
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
    private final byte key;
    private byte facing;
    private byte animState;
    private byte frame;
    private final double[] stats = new double[Globals.NUM_STATS];
    private String name;
    private final ItemEquip[] equips = new ItemEquip[Globals.NUM_EQUIP_SLOTS];
    private long lastUpdateTime;
    private static LogicModule logic;
    private boolean disconnect = false;

    public Player(final int x, final int y, final byte k) {
        this.x = x;
        this.y = y;
        this.key = k;
        this.facing = Globals.RIGHT;
        this.animState = Globals.PLAYER_ANIM_STATE_INVIS;
        this.name = "";
        this.frame = 0;
        this.lastUpdateTime = logic.getTime();
        setDaemon(true);
    }

    public Player(final int x, final int y, final byte k, final byte f) {
        this(x, y, k);
        this.facing = f;
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

    public String getPlayerName() {
        return this.name;
    }

    public byte getFacing() {
        return this.facing;
    }

    public void disconnect() {
        this.disconnect = true;
    }

    public void setPos(final int x, final int y) {
        this.x = x;
        this.y = y;
        this.lastUpdateTime = logic.getTime();
    }

    public void setFacing(final byte dir) {
        this.facing = dir;
        this.lastUpdateTime = logic.getTime();
    }

    public void setState(final byte s) {
        this.animState = s;
        this.lastUpdateTime = logic.getTime();
    }

    public void setFrame(final byte f) {
        this.frame = f;
        this.lastUpdateTime = logic.getTime();
    }

    public void setEquip(final byte slot, final int itemCode) {
        this.equips[slot] = new ItemEquip(itemCode);
    }

    public void setStat(final byte statType, final double stat) {
        this.stats[statType] = stat;
        this.lastUpdateTime = logic.getTime();
    }

    public void setPlayerName(final String n) {
        this.name = n;
        this.lastUpdateTime = logic.getTime();
    }

    public void draw(final Graphics2D g) {
        final byte s = this.animState, f = this.frame;
        if (s > Globals.NUM_PLAYER_ANIM_STATE || s < 0 || s == Globals.PLAYER_ANIM_STATE_INVIS || f >= Globals.CHAR_SPRITE[s].length) {
            return;
        }
        final BufferedImage sprite = Globals.CHAR_SPRITE[s][f];
        final int drawSrcX = this.x - ((this.facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth() / 2;
        final int drawSrcY = this.y - sprite.getHeight();
        final int drawDscY = drawSrcY + sprite.getHeight();
        /*
		 * switch (s) { case Globals.PLAYER_ANIM_STATE_ATTACK: drawSrcX += ((facing == Globals.RIGHT) ? 1 : -1) * 10; break; case
		 * Globals.PLAYER_STATE_ATTACK2: drawSrcX += ((facing == Globals.RIGHT) ? 1 : -1) * 25; break; case Globals.PLAYER_STATE_ATTACKOFF1:
		 * drawSrcX += ((facing == Globals.RIGHT) ? 1 : -1) * 40; break; case Globals.PLAYER_STATE_ATTACKOFF2: drawSrcX += ((facing ==
		 * Globals.RIGHT) ? 1 : -1) * 40; break; }
         */
        final int drawDscX = drawSrcX + ((this.facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth();
        if (this.equips[Globals.ITEM_OFFHAND] != null) {
            this.equips[Globals.ITEM_OFFHAND].drawIngame(g, this.x, this.y, s, f, this.facing, true);
        }
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);

        if (this.equips[Globals.ITEM_CHEST] != null) {
            this.equips[Globals.ITEM_CHEST].drawIngame(g, this.x, this.y, s, f, this.facing);
        }
        if (this.equips[Globals.ITEM_SHOULDER] != null) {
            this.equips[Globals.ITEM_SHOULDER].drawIngame(g, this.x, this.y, s, f, this.facing);
        }

        if (this.equips[Globals.ITEM_PANTS] != null) {
            this.equips[Globals.ITEM_PANTS].drawIngame(g, this.x, this.y, s, f, this.facing);
        }
        if (this.equips[Globals.ITEM_SHOE] != null) {
            this.equips[Globals.ITEM_SHOE].drawIngame(g, this.x, this.y, s, f, this.facing);
        }
        if (this.equips[Globals.ITEM_WEAPON] != null) {
            this.equips[Globals.ITEM_WEAPON].drawIngame(g, this.x, this.y, s, f, this.facing);
        }
        if (this.equips[Globals.ITEM_GLOVE] != null) {
            this.equips[Globals.ITEM_GLOVE].drawIngame(g, this.x, this.y, s, f, this.facing);
        }

        g.setFont(Globals.ARIAL_18PT);
        final int width = g.getFontMetrics().stringWidth(this.name);
        g.setColor(Color.BLACK);
        g.drawString(this.name, this.x - width / 2 - 1, this.y + 20);
        g.drawString(this.name, this.x - width / 2 + 1, this.y + 20);
        g.drawString(this.name, this.x - width / 2, this.y + 19);
        g.drawString(this.name, this.x - width / 2, this.y + 21);
        g.setColor(Color.WHITE);
        g.drawString(this.name, this.x - width / 2, this.y + 20);

        if (this.getStat(Globals.STAT_MAXHP) > 0) {
            int hpBarWidth = 80, hpBarHeight = 7;
            g.setColor(Color.GRAY);
            g.fillRect(this.x - (hpBarWidth + 2) / 2, y - 110, hpBarWidth, hpBarHeight);
            g.setColor(Color.RED);
            g.fillRect(this.x - hpBarWidth / 2, y - 110, (int) (hpBarWidth * this.getStat(Globals.STAT_MINHP) / this.getStat(Globals.STAT_MAXHP)), hpBarHeight);
            g.setColor(Color.BLACK);
            g.drawRect(this.x - (hpBarWidth + 2) / 2, y - 110, hpBarWidth, hpBarHeight);
        }

    }

    @Override
    public void run() {
        if (this.name.length() <= 0) {
            logic.sendGetName(this.key);
        }
        for (final ItemEquip e : this.equips) {
            if (e == null) {
                logic.sendGetEquip(this.key);
                break;
            }
        }
        if (this.stats[Globals.STAT_MAXHP] <= 0) {
            logic.sendGetStat(this.key, Globals.STAT_MAXHP);
        }
    }

    public boolean isDisconnected() {
        return this.disconnect || Globals.nsToMs(logic.getTime() - this.lastUpdateTime) >= 5000;
    }

    public boolean isDead() {
        return this.animState == Globals.PLAYER_ANIM_STATE_DEAD;
    }
}
