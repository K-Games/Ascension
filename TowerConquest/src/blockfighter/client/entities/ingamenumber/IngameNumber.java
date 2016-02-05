package blockfighter.client.entities.ingamenumber;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 *
 * @author Ken Kwan
 */
public class IngameNumber extends Thread {

    private static LogicModule logic;
    private final byte type;
    private double x, y;

    private final double speedX;

    private final double speedY;
    private final int damage;
    private long startTime = 0;
    private int duration = 700;

    public IngameNumber(final int dmg, final byte t, final Point loc) {
        this.startTime = logic.getTime();
        this.damage = dmg;
        this.type = t;
        this.x = loc.x + (Globals.rng(10) * 4 - 20);
        this.y = loc.y;
        // if (type == NUMBER_TYPE_EXP) {
        // duration = 1200;
        this.speedY = -5;
        this.speedX = 0;
        // } else {
        // speedY = -13;
        // speedX = (Globals.rng(3) - 1) * 3;
        // }
        setDaemon(true);
    }

    public static void setLogic(final LogicModule l) {
        logic = l;
    }

    @Override
    public void run() {
        this.duration -= Globals.INGAME_NUMBER_UPDATE / 1000000;
        if (this.duration < 0) {
            this.duration = 0;
        }
        this.y += this.speedY;
        if (this.type != Globals.NUMBER_TYPE_EXP) {
            // speedY += .5;
        }
        this.x += this.speedX;
    }

    public boolean isExpired() {
        return this.duration <= 0;
    }

    public void draw(final Graphics2D g) {
        final char[] decimalArray = Integer.toString(this.damage).toCharArray();
        for (int i = 0; i < decimalArray.length; i++) {
            g.drawImage(Globals.DAMAGE_FONT[this.type][decimalArray[i] - 48], (int) (this.x + i * 17), (int) this.y, null);
        }
        if (this.type == Globals.NUMBER_TYPE_EXP) {
            g.drawImage(Globals.EXP_WORD[0], (int) (this.x + 7 + decimalArray.length * 17), (int) this.y, null);
        }
    }
}
