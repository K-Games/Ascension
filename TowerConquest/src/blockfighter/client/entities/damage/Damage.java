package blockfighter.client.entities.damage;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 *
 * @author Ken Kwan
 */
public class Damage extends Thread {

    public final static byte DAMAGE_TYPE_PLAYER = 0x00,
            DAMAGE_TYPE_PLAYERCRIT = 0x01,
            DAMAGE_TYPE_BOSS = 0x02,
            DAMAGE_TYPE_EXP = 0x03;

    private byte type;
    private double x, y, speedX, speedY;
    private int damage;
    private long duration = 700;

    public Damage(int dmg, byte t, Point loc) {
        damage = dmg;
        type = t;
        x = loc.x;
        y = loc.y;
        //if (type == DAMAGE_TYPE_EXP) {
        //duration = 1200;
        speedY = -5;
        speedX = 0;
        // } else {
        //    speedY = -13;
        //    speedX = (Globals.rng(3) - 1) * 3;
        //}
        setDaemon(true);
    }

    @Override
    public void run() {
        duration -= Globals.DMG_UPDATE / 1000000;
        if (duration < 0) {
            duration = 0;
        }
        y += speedY;
        if (type != DAMAGE_TYPE_EXP) {
            //speedY += .5;
        }
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
        if (type == DAMAGE_TYPE_EXP) {
            g.drawImage(Globals.EXP_WORD[0], (int) (x + 7 + dArray.length * 18), (int) y, null);
        }
    }
}
