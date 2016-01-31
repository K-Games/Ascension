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
    private final byte key;
    private byte facing;
    private byte state;
    private byte frame;
    private final double[] stats = new double[Globals.NUM_STATS];
    private String name;
    private final ItemEquip[] equipment = new ItemEquip[Globals.NUM_EQUIP_SLOTS];
    private long lastUpdateTime = 5000;
    private static LogicModule logic;
    private boolean disconnect = false;

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
        this.lastUpdateTime = 5000;
    }

    public void setFacing(final byte dir) {
        this.facing = dir;
        this.lastUpdateTime = 5000;
    }

    public void setState(final byte s) {
        this.state = s;
        this.lastUpdateTime = 5000;
    }

    public void setFrame(final byte f) {
        this.frame = f;
        this.lastUpdateTime = 5000;
    }

    public void setEquip(final byte slot, final int itemCode) {
        this.equipment[slot] = new ItemEquip(itemCode);
    }

    public void setStat(final byte statType, final double stat) {
        this.stats[statType] = stat;
        this.lastUpdateTime = 5000;
    }

    public void setPlayerName(final String n) {
        this.name = n;
        this.lastUpdateTime = 5000;
    }

    public static void setLogic(final LogicModule l) {
        logic = l;
    }

    public Player(final int x, final int y, final byte k) {
        this.x = x;
        this.y = y;
        this.key = k;
        this.facing = Globals.RIGHT;
        this.state = Globals.PLAYER_STATE_STAND;
        this.name = "";
        this.frame = 0;
        setDaemon(true);
    }

    public void draw(final Graphics2D g) {
        final byte s = this.state, f = this.frame;
        if (s > Globals.NUM_PLAYER_STATE || s < 0 || s == Globals.PLAYER_STATE_INVIS || f >= Globals.CHAR_SPRITE[s].length) {
            return;
        }
        final BufferedImage sprite = Globals.CHAR_SPRITE[s][f];
        final int drawSrcX = this.x - ((this.facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth() / 2;
        final int drawSrcY = this.y - sprite.getHeight();
        final int drawDscY = drawSrcY + sprite.getHeight();
        /*
		 * switch (s) { case Globals.PLAYER_STATE_ATTACK: drawSrcX += ((facing == Globals.RIGHT) ? 1 : -1) * 10; break; case
		 * Globals.PLAYER_STATE_ATTACK2: drawSrcX += ((facing == Globals.RIGHT) ? 1 : -1) * 25; break; case Globals.PLAYER_STATE_ATTACKOFF1:
		 * drawSrcX += ((facing == Globals.RIGHT) ? 1 : -1) * 40; break; case Globals.PLAYER_STATE_ATTACKOFF2: drawSrcX += ((facing ==
		 * Globals.RIGHT) ? 1 : -1) * 40; break; }
         */
        final int drawDscX = drawSrcX + ((this.facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth();
        if (this.equipment[Globals.ITEM_OFFHAND] != null) {
            this.equipment[Globals.ITEM_OFFHAND].drawIngame(g, this.x, this.y, s, f, this.facing, true);
        }
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);

        if (this.equipment[Globals.ITEM_CHEST] != null) {
            this.equipment[Globals.ITEM_CHEST].drawIngame(g, this.x, this.y, s, f, this.facing);
        }
        if (this.equipment[Globals.ITEM_SHOULDER] != null) {
            this.equipment[Globals.ITEM_SHOULDER].drawIngame(g, this.x, this.y, s, f, this.facing);
        }

        if (this.equipment[Globals.ITEM_PANTS] != null) {
            this.equipment[Globals.ITEM_PANTS].drawIngame(g, this.x, this.y, s, f, this.facing);
        }
        if (this.equipment[Globals.ITEM_SHOE] != null) {
            this.equipment[Globals.ITEM_SHOE].drawIngame(g, this.x, this.y, s, f, this.facing);
        }
        if (this.equipment[Globals.ITEM_WEAPON] != null) {
            this.equipment[Globals.ITEM_WEAPON].drawIngame(g, this.x, this.y, s, f, this.facing);
        }
        if (this.equipment[Globals.ITEM_GLOVE] != null) {
            this.equipment[Globals.ITEM_GLOVE].drawIngame(g, this.x, this.y, s, f, this.facing);
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
    }

    @Override
    public void run() {
        this.lastUpdateTime -= Globals.LOGIC_UPDATE / 1000000;
        if (this.name.length() <= 0) {
            logic.sendGetName(this.key);
        }
        for (final ItemEquip e : this.equipment) {
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
        return this.disconnect || this.lastUpdateTime <= 0;
    }
    
    public boolean isDead(){
        return this.state == Globals.PLAYER_STATE_DEAD;
    }
}
