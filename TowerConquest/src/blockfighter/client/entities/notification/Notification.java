package blockfighter.client.entities.notification;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.Main;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class Notification extends Thread {

    private static LogicModule logic;
    private final byte type;
    private final int number;
    private long startTime = 0;
    private long lastUpdateTime = 0;
    private final int duration = 5000;
    private Color colour, border;

    public Notification(final int num, final byte t) {
        this.startTime = logic.getTime();
        this.number = num;
        this.type = t;
        setDaemon(true);
    }

    public static void init() {
        logic = Main.getLogicModule();
    }

    @Override
    public void run() {
        float transparency = 1f - (logic.getTime() - this.startTime) / this.duration;
        this.colour = new Color(Color.YELLOW.getRed() / 255F, Color.YELLOW.getBlue() / 255F, Color.YELLOW.getGreen() / 255F, transparency);
        this.border = new Color(0, 0, 0, transparency);

        this.lastUpdateTime = logic.getTime();
    }

    public boolean isExpired() {
        return Globals.nsToMs(logic.getTime() - this.startTime) >= this.duration;
    }

    public void draw(final Graphics2D g, final int x, final int y) {
        g.setFont(Globals.ARIALBLACK_18P);

        String output = "";
        switch (this.type) {
            case Globals.NOTIFICATION_EXP:
                output = "Gained " + Integer.toString(this.number) + "EXP";
                break;
            case Globals.NOTIFICATION_ITEMEQUIP:
                output = "Received " + ItemEquip.getItemName(this.number);
                break;
            case Globals.NOTIFICATION_ITEMUPGRADE:
                output = "Received Tome of Enhancement";
                break;
        }

        for (int i = 0; i < 2; i++) {
            g.setColor(this.border);
            g.drawString(output, (float) x - 1 + i * 2 * 1, (float) y);
            g.drawString(output, (float) x, (float) y - 1 + i * 2 * 1);
        }

        g.setColor(this.colour);
        g.drawString(output, (float) x, (float) y);
    }
}
