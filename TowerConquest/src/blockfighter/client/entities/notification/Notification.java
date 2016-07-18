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
        if (!isExpired()) {
            float transparency = 1f - Globals.nsToMs(logic.getTime() - this.startTime) * 1f / this.duration;
            this.colour = new Color(255, 255, 255, (int) (transparency * 255));
        }
    }

    public boolean isExpired() {
        return Globals.nsToMs(logic.getTime() - this.startTime) >= this.duration;
    }

    public void draw(final Graphics2D g, final int x, final int y) {
        g.setFont(Globals.ARIAL_15PT);

        String output = "";
        switch (this.type) {
            case Globals.NOTIFICATION_EXP:
                output = "Gained " + Integer.toString(this.number) + " EXP";
                break;
            case Globals.NOTIFICATION_ITEMEQUIP:
                output = "Received " + ItemEquip.getItemName(this.number);
                break;
            case Globals.NOTIFICATION_ITEMUPGRADE:
                output = "Received Tome of Enhancement";
                break;
        }
        g.setColor(this.colour);
        g.drawString(output, (float) x, (float) y);
    }
}
