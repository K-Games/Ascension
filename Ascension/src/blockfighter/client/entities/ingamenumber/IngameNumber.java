package blockfighter.client.entities.ingamenumber;

import blockfighter.shared.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.AscensionClient;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class IngameNumber extends Thread {

    private static LogicModule logic;
    private final byte type;
    private double x, y;

    private final double speedX;

    private final double speedY;
    private final int number;
    private long startTime = 0;
    private final int duration = 700;

    public IngameNumber(final int num, final byte t, final Point loc) {
        this.startTime = logic.getTime();
        this.number = num;
        this.type = t;
        this.x = loc.x;
        this.y = loc.y - 18;
        this.speedY = -2;
        this.speedX = 0;
        setDaemon(true);
    }

    public static void init() {
        logic = AscensionClient.getLogicModule();
    }

    @Override
    public void run() {
        this.y += this.speedY;
        this.x += this.speedX;
    }

    public boolean isExpired() {
        return Globals.nsToMs(logic.getTime() - this.startTime) >= this.duration;
    }

    public void draw(final Graphics2D g) {
        //final char[] decimalArray = Integer.toString(this.number).toCharArray();
        g.setFont(Globals.ARIAL_18PTBOLD);

        //for (int i = 0; i < decimalArray.length; i++) {
        //    g.drawImage(Globals.DAMAGE_FONT[this.type][decimalArray[i] - 48], (int) (this.x + i * 17), (int) this.y, null);
        //}
        String output = Integer.toString(this.number);

        int outputWidth = g.getFontMetrics().stringWidth(output);
        for (int i = 0; i < 2; i++) {
            g.setColor(Color.BLACK);
            g.drawString(output, (float) this.x - outputWidth / 2 - 1 + i * 2 * 1, (float) this.y);
            g.drawString(output, (float) this.x - outputWidth / 2, (float) this.y - 1 + i * 2 * 1);
        }
        switch (this.type) {
            case Globals.NUMBER_TYPE_PLAYER:
                g.setColor(Color.red);
                break;
            case Globals.NUMBER_TYPE_PLAYERCRIT:
                g.setColor(Color.ORANGE);
                break;
            case Globals.NUMBER_TYPE_MOB:
                g.setColor(Color.MAGENTA);
                break;
        }
        g.drawString(output, (float) this.x - outputWidth / 2, (float) this.y);
    }
}
