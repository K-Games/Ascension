package blockfighter.server.entities.boss.Lightning;

import java.awt.geom.Rectangle2D;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.boss.BossProjectile;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;

/**
 * This is the base projectile class. Create projectile classes off this.
 *
 * @author Ken Kwan
 */
public class ProjAttack extends BossProjectile {

	/**
	 * Projectile of Sword Skill Defensive Impact.
	 *
	 * @param l Room/Logic Module
	 * @param k Projectile Key
	 * @param o Owning player
	 * @param x Spawn x-coordinate
	 * @param y Spawn y-coordinate
	 */
	public ProjAttack(final LogicModule l, final int k, final Boss o, final double x, final double y) {
		super(l, k);
		setBossOwner(o);
		this.x = x;
		this.y = y;
		this.hitbox = new Rectangle2D.Double[1];
		if (getBossOwner().getFacing() == Globals.RIGHT) {
			this.hitbox[0] = new Rectangle2D.Double(x + 200, y - 450, 250, 450);
		} else {
			this.hitbox[0] = new Rectangle2D.Double(x - 250 - 200, y - 450, 250, 450);
		}
		this.duration = 200;
	}

	@Override
	public void processQueue() {
		while (!this.playerQueue.isEmpty()) {
			final Player p = this.playerQueue.poll();
			if (p != null && !p.isDead()) {
				final int damage = (int) (70 * Math.pow(getBossOwner().getStats()[Boss.STAT_LEVEL], 1.7));
				p.queueDamage(new Damage(damage, false, getBossOwner(), p, this.hitbox[0], p.getHitbox()));
			}
		}
		this.queuedEffect = false;
	}

}
