package blockfighter.client.entities.player;

import blockfighter.client.Core;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.net.PacketSender;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

public class Player implements Callable<Player> {

    private int x, y;
    private final byte key;
    private byte facing;
    private byte animState;
    private byte frame;
    private final double[] stats = new double[Globals.NUM_STATS];
    private String name;
    private final ItemEquip[] equips = new ItemEquip[Globals.NUM_EQUIP_SLOTS];
    private long lastUpdateTime;
    private boolean disconnect = false;

    public Player(final int x, final int y, final byte k) {
        this.x = x;
        this.y = y;
        this.key = k;
        this.facing = Globals.RIGHT;
        this.animState = Globals.PLAYER_ANIM_STATE_INVIS;
        this.name = "";
        this.frame = 0;
        this.lastUpdateTime = Core.getLogicModule().getTime();
    }

    public Player(final int x, final int y, final byte k, final byte f) {
        this(x, y, k);
        this.facing = f;
    }

    public byte getKey() {
        return this.key;
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

    public double getStat(final byte statID) {
        return this.stats[statID];
    }

    public String getPlayerName() {
        return this.name;
    }

    public byte getFacing() {
        return this.facing;
    }

    public byte getAnimState() {
        return this.animState;
    }

    public byte getFrame() {
        return this.frame;
    }

    public void disconnect() {
        this.disconnect = true;
    }

    public void setPos(final int x, final int y) {
        this.x = x;
        this.y = y;
        this.lastUpdateTime = Core.getLogicModule().getTime();
    }

    public void setFacing(final byte dir) {
        this.facing = dir;
        this.lastUpdateTime = Core.getLogicModule().getTime();
    }

    public void setState(final byte s) {
        this.animState = s;
        this.lastUpdateTime = Core.getLogicModule().getTime();
    }

    public void setFrame(final byte f) {
        this.frame = f;
        this.lastUpdateTime = Core.getLogicModule().getTime();
    }

    public void setEquip(final byte slot, final int itemCode) {
        this.equips[slot] = new ItemEquip(itemCode);
    }

    public void setStat(final byte statID, final double stat) {
        this.stats[statID] = stat;
        this.lastUpdateTime = Core.getLogicModule().getTime();
    }

    public void setPlayerName(final String n) {
        this.name = n;
        this.lastUpdateTime = Core.getLogicModule().getTime();
    }

    public void draw(final Graphics2D g) {
        drawSprite(g, this.x, this.y, this.facing, this.animState, this.frame);
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
            g.fillRect(this.x - (hpBarWidth + 2) / 2, y - 130, hpBarWidth, hpBarHeight);
            g.setColor(Color.RED);
            g.fillRect(this.x - hpBarWidth / 2, y - 130, (int) (hpBarWidth * this.getStat(Globals.STAT_MINHP) / this.getStat(Globals.STAT_MAXHP)), hpBarHeight);
            g.setColor(Color.BLACK);
            g.drawRect(this.x - (hpBarWidth + 2) / 2, y - 130, hpBarWidth, hpBarHeight);
        }
    }

    public void drawSprite(final Graphics2D g, final int x, final int y, final byte facing, final byte animState, final byte frame) {
        if (animState > Globals.NUM_PLAYER_ANIM_STATE || animState < 0 || animState == Globals.PLAYER_ANIM_STATE_INVIS || frame >= Globals.CHAR_SPRITE[animState].length) {
            return;
        }
        final BufferedImage sprite = Globals.CHAR_SPRITE[animState][frame];
        final int drawSrcX = x - ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth() / 2;
        final int drawSrcY = y - sprite.getHeight();
        final int drawDscY = drawSrcY + sprite.getHeight();
        /*
		 * switch (animState) { case Globals.PLAYER_ANIM_STATE_ATTACK: drawSrcX += ((facing == Globals.RIGHT) ? 1 : -1) * 10; break; case
		 * Globals.PLAYER_STATE_ATTACK2: drawSrcX += ((facing == Globals.RIGHT) ? 1 : -1) * 25; break; case Globals.PLAYER_STATE_ATTACKOFF1:
		 * drawSrcX += ((facing == Globals.RIGHT) ? 1 : -1) * 40; break; case Globals.PLAYER_STATE_ATTACKOFF2: drawSrcX += ((facing ==
		 * Globals.RIGHT) ? 1 : -1) * 40; break; }
         */
        final int drawDscX = drawSrcX + ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth();
        if (this.equips[Globals.ITEM_OFFHAND] != null) {
            this.equips[Globals.ITEM_OFFHAND].drawIngame(g, x, y, animState, frame, facing, true);
        }
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);

        if (this.equips[Globals.ITEM_CHEST] != null) {
            this.equips[Globals.ITEM_CHEST].drawIngame(g, x, y, animState, frame, facing);
        }
        if (this.equips[Globals.ITEM_SHOULDER] != null) {
            this.equips[Globals.ITEM_SHOULDER].drawIngame(g, x, y, animState, frame, facing);
        }

        if (this.equips[Globals.ITEM_PANTS] != null) {
            this.equips[Globals.ITEM_PANTS].drawIngame(g, x, y, animState, frame, facing);
        }
        if (this.equips[Globals.ITEM_SHOE] != null) {
            this.equips[Globals.ITEM_SHOE].drawIngame(g, x, y, animState, frame, facing);
        }
        if (this.equips[Globals.ITEM_WEAPON] != null) {
            this.equips[Globals.ITEM_WEAPON].drawIngame(g, x, y, animState, frame, facing);
        }
        if (this.equips[Globals.ITEM_GLOVE] != null) {
            this.equips[Globals.ITEM_GLOVE].drawIngame(g, x, y, animState, frame, facing);
        }
    }

    @Override
    public Player call() {
        if (this.name.length() <= 0) {
            PacketSender.sendGetName(Core.getLogicModule().getSelectedRoom(), this.key);
        }
        for (final ItemEquip e : this.equips) {
            if (e == null) {
                PacketSender.sendGetEquip(Core.getLogicModule().getSelectedRoom(), this.key);
                break;
            }
        }
        if (this.stats[Globals.STAT_MAXHP] <= 0) {
            PacketSender.sendGetStat(Core.getLogicModule().getSelectedRoom(), this.key, Globals.STAT_MAXHP);
        }
        return this;
    }

    public boolean isDisconnected() {
        return this.disconnect || Globals.nsToMs(Core.getLogicModule().getTime() - this.lastUpdateTime) >= 5000;
    }

    public boolean isDead() {
        return this.animState == Globals.PLAYER_ANIM_STATE_DEAD;
    }
}
