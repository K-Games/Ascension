package blockfighter.client.entities.notification;

import blockfighter.client.Core;
import blockfighter.client.entities.items.Item;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.concurrent.Callable;

public class Notification implements Callable<Notification> {

    private final byte type;
    private long startTime = 0;
    private final int duration = 5000;
    private Color colour = new Color(255, 255, 255, 255),
            bgColour = new Color(0, 0, 0, 255),
            ownerColour, victimColour;
    private final Polygon bg = new Polygon(xPoints, yPoints, 4);
    private final Polygon ownerColourBg = new Polygon(ownerXPoints, yPoints, 4);
    private final Polygon victimColourBg = new Polygon(victimXPoints, yPoints, 4);
    private final Player owner, victim;

    private final String output;

    public static final int BG_HEIGHT = 25;
    public static final int MAX_NUM_NOTIFICATIONS = 18;
    private static final String[] KILL_TEXT = {"destroyed", "killed", "pwned", "annhilated", "wrecked"};
    private static int[] xPoints = {0, 0, 0, 0};
    private static int[] yPoints = {0, 0, BG_HEIGHT, BG_HEIGHT};
    private static int[] ownerXPoints = {0, 0, 0, 0};
    private static int[] victimXPoints = {0, 0, 0, 0};

    public Notification(final int EXP) {
        this.startTime = Core.getLogicModule().getTime();
        this.type = Globals.NOTIFICATION_EXP;
        this.output = "Gained " + Globals.NUMBER_FORMAT.format(EXP) + " EXP";
        this.owner = null;
        this.victim = null;
    }

    public Notification(final Player owner, final Player victim) {
        this.startTime = Core.getLogicModule().getTime();
        this.type = Globals.NOTIFICATION_KILL;
        this.output = owner.getPlayerName() + " " + KILL_TEXT[Globals.rng(KILL_TEXT.length)] + " " + victim.getPlayerName() + "!";
        this.owner = owner;
        this.victim = victim;
        this.ownerColour = this.owner.getPlayerColor();
        this.victimColour = this.victim.getPlayerColor();
    }

    public Notification(final Item i) {
        this.startTime = Core.getLogicModule().getTime();
        this.type = Globals.NOTIFICATION_ITEM;
        this.output = "Received " + i.getItemName();
        this.owner = null;
        this.victim = null;
    }

    @Override
    public Notification call() {
        if (!isExpired()) {
            float transparency = 1f - Globals.nsToMs(Core.getLogicModule().getTime() - this.startTime) * 1f / this.duration;
            this.colour = new Color(255, 255, 255, (int) (transparency * 255));
            this.bgColour = new Color(0, 0, 0, (int) (transparency * 255));
            if (this.owner != null) {
                Color baseColour = this.owner.getPlayerColor();
                this.ownerColour = new Color(baseColour.getRed(), baseColour.getGreen(), baseColour.getBlue(), (int) (transparency * 255));
            }

            if (this.victim != null) {
                Color baseColour = this.victim.getPlayerColor();
                this.victimColour = new Color(baseColour.getRed(), baseColour.getGreen(), baseColour.getBlue(), (int) (transparency * 255));
            }
        }
        return this;
    }

    public boolean isExpired() {
        return Globals.nsToMs(Core.getLogicModule().getTime() - this.startTime) >= this.duration;
    }

    public void draw(final Graphics2D g, final int x, final int y) {
        if (this.type == Globals.NOTIFICATION_KILL) {
            drawKillNotification(g, x, y);
        } else {
            g.setFont(Globals.ARIAL_15PT);

            this.bg.xpoints[1] = 15 + 10 + g.getFontMetrics().stringWidth(output);
            this.bg.xpoints[2] = 10 + g.getFontMetrics().stringWidth(output);
            this.bg.translate(x, y);

            g.setColor(this.bgColour);
            g.fillPolygon(this.bg);
            this.bg.translate(-x, -y);

            g.setColor(this.colour);
            g.drawString(this.output, (float) x + 5, (float) y + g.getFontMetrics(Globals.ARIAL_15PT).getHeight());
        }
    }

    public void drawKillNotification(final Graphics2D g, final int x, final int y) {
        g.setFont(Globals.ARIAL_15PT);

        this.bg.xpoints[0] = 0;
        this.bg.xpoints[1] = 15 + 10 + g.getFontMetrics().stringWidth(output);
        this.bg.xpoints[2] = 10 + g.getFontMetrics().stringWidth(output);
        this.bg.xpoints[3] = 0;
        this.bg.translate(x, y);
        g.setColor(this.bgColour);
        g.fillPolygon(this.bg);
        this.bg.translate(-x, -y);

        this.ownerColourBg.xpoints[0] = bg.xpoints[1];
        this.ownerColourBg.xpoints[1] = this.ownerColourBg.xpoints[0] + 10;
        this.ownerColourBg.xpoints[2] = this.ownerColourBg.xpoints[1] - 15;
        this.ownerColourBg.xpoints[3] = this.bg.xpoints[2];
        this.ownerColourBg.translate(x, y);
        g.setColor(this.ownerColour);
        g.fillPolygon(this.ownerColourBg);
        this.ownerColourBg.translate(-x, -y);

//        this.victimColourBg.xpoints[0] = this.bg.xpoints[1];
//        this.victimColourBg.xpoints[1] = this.victimColourBg.xpoints[0] + 10;
//        this.victimColourBg.xpoints[2] = this.victimColourBg.xpoints[1] - 10;
//        this.victimColourBg.xpoints[3] = this.bg.xpoints[2];
//        this.victimColourBg.translate(x, y);
//        g.setColor(this.victimColour);
//        g.fillPolygon(this.victimColourBg);
//        this.victimColourBg.translate(-x, -y);
        g.setColor(this.colour);
        g.drawString(this.output, (float) x + 5, (float) y + g.getFontMetrics(Globals.ARIAL_15PT).getHeight());
    }
}
