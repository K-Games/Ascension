package blockfighter.client.entities.damage;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;

/**
 *
 * @author Ken Kwan
 */
public class Damage extends Thread {

    public final static byte DAMAGE_TYPE_PLAYER = 0x00,
            DAMAGE_TYPE_PLAYERCRIT = 0x01,
            DAMAGE_TYPE_BOSS = 0x02;
    private byte type;
    private double x, y, speedX, speedY;
    private int damage;
    private long duration = 700;

    public Damage(int dmg, byte t, Point loc) {
        damage = dmg;
        type = t;
        x = loc.x;
        y = loc.y;
        speedY = -13;
        speedX = (new Random().nextInt(3) - 1) *3;
    }

    @Override
    public void run() {
        duration -= Globals.DMG_UPDATE / 1000000;
        if (duration < 0) {
            duration = 0;
        }
        y += speedY;
        speedY += .5;
        x += speedX;
    }

    public boolean isExpired() {
        return duration <= 0;
    }

    public void draw(Graphics2D g) {
        char[] dArray = Integer.toString(damage).toCharArray();
        for (int i = 0; i < dArray.length; i++) {
            g.drawImage(Globals.DAMAGE_FONT[type][dArray[i] - 48], (int) (x + i * 18), (int) y, null);
        }
    }
}
