package blockfighter.client.entities.notification;

import blockfighter.client.AscensionClient;
import blockfighter.client.LogicModule;
import blockfighter.client.entities.items.Item;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.concurrent.Callable;

public class Notification implements Callable<Notification> {

    private static LogicModule logic;
    private final byte type;
    private long startTime = 0;
    private final int duration = 5000;
    private Color colour = new Color(255, 255, 255, 255),
            bgColour = new Color(0, 0, 0, 255);
    private final Polygon bg = new Polygon(xPoints, yPoints, 4);
    private final String output;

    public static final int BG_HEIGHT = 25;
    public static final int MAX_NUM_NOTIFICATIONS = 18;
    private static final String[] KILL_TEXT = {"destroyed", "killed", "pwned", "annhilated", "wrecked"};
    private static int[] xPoints = {0, 0, 0, 0};
    private static int[] yPoints = {0, 0, BG_HEIGHT, BG_HEIGHT};

    public Notification(final int EXP) {
        this.startTime = logic.getTime();
        this.type = Globals.NOTIFICATION_EXP;
        this.output = "Gained " + Globals.NUMBER_FORMAT.format(EXP) + " EXP";
    }

    public Notification(final Player killer, final Player victim) {
        this.startTime = logic.getTime();
        this.type = Globals.NOTIFICATION_KILL;
        this.output = killer.getPlayerName() + " " + KILL_TEXT[Globals.rng(KILL_TEXT.length)] + " " + victim.getPlayerName() + "!";
    }

    public Notification(final Item i) {
        this.startTime = logic.getTime();
        this.type = Globals.NOTIFICATION_ITEM;
        this.output = "Received " + i.getItemName();
    }

    public static void init() {
        logic = AscensionClient.getLogicModule();
    }

    @Override
    public Notification call() {
        if (!isExpired()) {
            float transparency = 1f - Globals.nsToMs(logic.getTime() - this.startTime) * 1f / this.duration;
            this.colour = new Color(255, 255, 255, (int) (transparency * 255));
            this.bgColour = new Color(0, 0, 0, (int) (transparency * 255));
        }
        return this;
    }

    public boolean isExpired() {
        return Globals.nsToMs(logic.getTime() - this.startTime) >= this.duration;
    }

    public void draw(final Graphics2D g, final int x, final int y) {
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
