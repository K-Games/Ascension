package blockfighter.client.entities.damage;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.util.Random;

/**
 *
 * @author Ken Kwan
 */
public class Damage extends Thread {

    private final static byte DAMAGE_TYPE_PLAYER = 0x00,
            DAMAGE_TYPE_PLAYERCRIT = 0x01,
            DAMAGE_TYPE_BOSS = 0x02;
    private byte type;
    private Point p;
    private int damage, speedX, speedY;
    protected DecimalFormat df = new DecimalFormat("###,###,##0");
    private long duration = 1250;

    public Damage(int dmg, byte t, Point loc) {
        damage = dmg;
        type = t;
        p = loc;
        speedY = -15;
        speedX = new Random().nextInt(10) - 5;
    }

    @Override
    public void run() {
        duration -= Globals.DMG_UPDATE / 1000000;
        if (duration < 0) {
            duration = 0;
        }
        p.y += speedY;
        speedY++;
        p.x += speedX;
    }

    public boolean isExpired() {
        return duration <= 0;
    }

    public void draw(Graphics2D g) {
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g.setFont(Globals.ARIAL_30PT);
        switch (type) {
            case DAMAGE_TYPE_PLAYER:
                g.setColor(new Color(255, 20, 0, (int) (((duration / 1250D)) * 255)));
                break;
            case DAMAGE_TYPE_PLAYERCRIT:
                g.setColor(new Color(255, 0, 120, (int) (((duration / 1250D)) * 255)));
                break;
            case DAMAGE_TYPE_BOSS:
                g.setColor(new Color(75, 0, 255, (int) (((duration / 1250D)) * 255)));
                break;
        }
        g.drawString(df.format(damage), p.x, p.y);
    }
}
