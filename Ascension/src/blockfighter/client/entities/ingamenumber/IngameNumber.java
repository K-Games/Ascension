package blockfighter.client.entities.ingamenumber;

import blockfighter.client.Core;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

public class IngameNumber implements Callable<IngameNumber> {

    private static final Color DAMAGE_RED = new Color(255, 15, 0);
    private static final Color DAMAGE_ORANGE = new Color(255, 200, 0);
    private static final Color DAMAGE_BLUE = new Color(105, 185, 255);

    private final int key;
    private final byte type;
    private double x, y;

    private double speedX;
    private double speedY;
    private final int number;
    private long startTime = 0;
    private final int duration = 700;

    private static final ConcurrentLinkedQueue<Integer> AVAILABLE_KEYS = new ConcurrentLinkedQueue<>();
    private static int keyCount = 0;

    public static void returnKey(final int key) {
        AVAILABLE_KEYS.add(key);
    }

    private static int getNextAvailableKey() {
        Integer nextKey = AVAILABLE_KEYS.poll();
        while (nextKey == null) {
            AVAILABLE_KEYS.add(keyCount);
            keyCount++;
            nextKey = AVAILABLE_KEYS.poll();
        }
        return nextKey;
    }

    public IngameNumber(final int num, final byte t, final Point loc) {
        this.key = getNextAvailableKey();
        this.startTime = Core.getLogicModule().getTime();
        this.number = num;
        this.type = t;
        this.x = loc.x;
        this.y = loc.y - 18;
        this.speedY = -12 + Globals.rng(40) / 10D;
        this.speedX = (Globals.rng(10) - 5) / 2D;
    }

    public int getKey() {
        return this.key;
    }

    @Override
    public IngameNumber call() {
        this.speedY += 0.6;
        this.y += this.speedY;
        this.x += this.speedX;
        return this;
    }

    public boolean isExpired() {
        return Globals.nsToMs(Core.getLogicModule().getTime() - this.startTime) >= this.duration;
    }

    public void draw(final Graphics2D g) {
        g.setFont((this.type == Globals.NUMBER_TYPE_PLAYERCRIT || this.type == Globals.NUMBER_TYPE_MOBCRIT) ? Globals.ARIAL_21PTBOLD : Globals.ARIAL_19PTBOLD);

        String output = Integer.toString(this.number);
        output = (this.type == Globals.NUMBER_TYPE_PLAYERCRIT || this.type == Globals.NUMBER_TYPE_MOBCRIT) ? output + "!" : output;

        int outputWidth = g.getFontMetrics().stringWidth(output);
        for (int i = 0; i < 2; i++) {
            g.setColor(Color.BLACK);
            g.drawString(output, (float) this.x - outputWidth / 2 - 1 + i * 2 * 1, (float) this.y);
            g.drawString(output, (float) this.x - outputWidth / 2, (float) this.y - 1 + i * 2 * 1);
        }
        switch (this.type) {
            case Globals.NUMBER_TYPE_PLAYER:
                g.setColor(DAMAGE_RED);
                break;
            case Globals.NUMBER_TYPE_PLAYERCRIT:
                g.setColor(DAMAGE_ORANGE);
                break;
            case Globals.NUMBER_TYPE_MOB:
            case Globals.NUMBER_TYPE_MOBCRIT:
                g.setColor(DAMAGE_BLUE);
                break;
        }
        g.drawString(output, (float) this.x - outputWidth / 2, (float) this.y);
    }
}
