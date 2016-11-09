package blockfighter.client.entities.notification;

import blockfighter.shared.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.AscensionClient;
import blockfighter.client.entities.items.Item;
import java.awt.Color;
import java.awt.Graphics2D;

public class Notification extends Thread {

    private static LogicModule logic;
    private final byte type;
    private final int exp;
    private long startTime = 0;
    private final int duration = 5000;
    private Color colour, border;
    private Item item;

    public Notification(final int EXP) {
        this.startTime = logic.getTime();
        this.exp = EXP;
        this.type = Globals.NOTIFICATION_EXP;
        setDaemon(true);
    }

    public Notification(final Item i) {
        this.startTime = logic.getTime();
        this.exp = 0;
        this.item = i;
        this.type = Globals.NOTIFICATION_ITEM;
        setDaemon(true);
    }

    public static void init() {
        logic = AscensionClient.getLogicModule();
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
                output = "Gained " + Integer.toString(this.exp) + " EXP";
                break;
            case Globals.NOTIFICATION_ITEM:
                output = "Received " + item.getItemName();
                break;
        }
        g.setColor(this.colour);
        g.drawString(output, (float) x, (float) y);
    }
}
