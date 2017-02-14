package blockfighter.client.entities.player;

import blockfighter.client.Core;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.net.PacketSender;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.concurrent.Callable;

public class Player implements Callable<Player> {

    private static final Color NAME_BG = new Color(0, 0, 0, 150);
    public static Color[] PLAYER_COLOURS = new Color[25];

    static {
        int index = 0;
        for (float h = 0; h < 1; h += 0.04f) {
            PLAYER_COLOURS[index] = new Color(Color.HSBtoRGB(h, 1f, 1f));
            index++;
        }

    }

    private int x, y, queueX, queueY;
    private final byte key;
    private byte facing;
    private byte animState;
    private byte frame;
    private final double[] stats = new double[Globals.NUM_STATS];
    private String name;
    private final ItemEquip[] equips = new ItemEquip[Globals.NUM_EQUIP_SLOTS];
    private long lastUpdateTime;
    private boolean disconnect = false;
    private int score = -1;
    private int ping = 0;
    private Color playerColour;

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

    public int getScore() {
        return this.score;
    }

    public int getPing() {
        return this.ping;
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
        this.queueX = x;
        this.queueY = y;
        this.lastUpdateTime = Core.getLogicModule().getTime();
    }

    public void updatePos() {
        this.x = this.queueX;
        this.y = this.queueY;
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

    public void setPing(final int ping) {
        this.ping = ping;
    }

    public void setScore(final int score) {
        this.score = score;
    }

    public void draw(final Graphics2D g) {
        if (this.animState > Globals.NUM_PLAYER_ANIM_STATE
                || this.animState < 0
                || this.animState == Globals.PLAYER_ANIM_STATE_INVIS
                || this.frame >= Globals.CHAR_SPRITE[animState].length) {
            return;
        }
        drawSprite(g, this.x, this.y, this.facing, this.animState, this.frame);

        g.setFont(Globals.ARIAL_18PT);
        final int width = g.getFontMetrics().stringWidth(this.name);

        g.setColor(getPlayerColor());
        g.fillRoundRect(this.x - width / 2 - 15, this.y + 4, 5, 20, 4, 4);
        g.setColor(Color.BLACK);
        g.drawRoundRect(this.x - width / 2 - 15, this.y + 4, 5, 20, 4, 4);

        g.setColor(NAME_BG);
        g.fillRect(this.x - width / 2 - 5, this.y + 4, width + 10, 20);

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
        if (animState > Globals.NUM_PLAYER_ANIM_STATE
                || animState < 0
                || animState == Globals.PLAYER_ANIM_STATE_INVIS
                || frame >= Globals.CHAR_SPRITE[animState].length) {
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
        if (this.stats[Globals.STAT_LEVEL] <= 0) {
            PacketSender.sendGetStat(Core.getLogicModule().getSelectedRoom(), this.key, Globals.STAT_LEVEL);
        }
        return this;
    }

    public boolean isDisconnected() {
        return this.disconnect || Globals.nsToMs(Core.getLogicModule().getTime() - this.lastUpdateTime) >= 5000;
    }

    public boolean isDead() {
        return this.animState == Globals.PLAYER_ANIM_STATE_DEAD;
    }

    public Color getPlayerColor() {
        if (this.name.length() <= 0 || this.stats[Globals.STAT_MAXHP] <= 0) {
            return Color.WHITE;
        }
        if (this.playerColour == null) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                String colorString = this.name + this.stats[Globals.STAT_MAXHP];
                md.update(colorString.getBytes("UTF-8"));
                byte[] digest = md.digest();
                this.playerColour = PLAYER_COLOURS[Math.abs(Base64.getEncoder().encodeToString(digest).hashCode() % PLAYER_COLOURS.length)];
            } catch (Exception ex) {
                return Color.WHITE;
            }
        }
        return this.playerColour;
    }
}
