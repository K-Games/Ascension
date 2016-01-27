package blockfighter.client.entities.damage;

import java.awt.Graphics2D;
import java.awt.Point;

import blockfighter.client.Globals;

/**
 *
 * @author Ken Kwan
 */
public class Damage extends Thread {

	public final static byte DAMAGE_TYPE_PLAYER = 0x00,
			DAMAGE_TYPE_PLAYERCRIT = 0x01,
			DAMAGE_TYPE_BOSS = 0x02,
			DAMAGE_TYPE_EXP = 0x03;

	private final byte type;
	private double x, y;

	private final double speedX;

	private final double speedY;
	private final int damage;
	private long duration = 700;

	public Damage(final int dmg, final byte t, final Point loc) {
		this.damage = dmg;
		this.type = t;
		this.x = loc.x;
		this.y = loc.y;
		// if (type == DAMAGE_TYPE_EXP) {
		// duration = 1200;
		this.speedY = -5;
		this.speedX = 0;
		// } else {
		// speedY = -13;
		// speedX = (Globals.rng(3) - 1) * 3;
		// }
		setDaemon(true);
	}

	@Override
	public void run() {
		this.duration -= Globals.DMG_UPDATE / 1000000;
		if (this.duration < 0) {
			this.duration = 0;
		}
		this.y += this.speedY;
		if (this.type != DAMAGE_TYPE_EXP) {
			// speedY += .5;
		}
		this.x += this.speedX;
	}

	public boolean isExpired() {
		return this.duration <= 0;
	}

	public void draw(final Graphics2D g) {
		final char[] dArray = Integer.toString(this.damage).toCharArray();
		for (int i = 0; i < dArray.length; i++) {
			g.drawImage(Globals.DAMAGE_FONT[this.type][dArray[i] - 48], (int) (this.x + i * 18), (int) this.y, null);
		}
		if (this.type == DAMAGE_TYPE_EXP) {
			g.drawImage(Globals.EXP_WORD[0], (int) (this.x + 7 + dArray.length * 18), (int) this.y, null);
		}
	}
}
