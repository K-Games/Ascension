package blockfighter.server.entities.proj;

import java.awt.geom.Rectangle2D;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;

/**
 * This is the base projectile class. Create projectile classes off this.
 *
 * @author Ken Kwan
 */
public class ProjBowStorm extends Projectile {

	/**
	 * Projectile of Bow Skill Arrow Storm.
	 *
	 * @param l Room/Logic Module
	 * @param k Projectile Key
	 * @param o Owning player
	 * @param x Spawn x-coordinate
	 * @param y Spawn y-coordinate
	 */
	public ProjBowStorm(final LogicModule l, final int k, final Player o, final double x, final double y) {
		super(l, k);
		setOwner(o);
		this.x = x;
		this.y = y;
		this.hitbox = new Rectangle2D.Double[1];
		if (getOwner().getFacing() == Globals.RIGHT) {
			this.hitbox[0] = new Rectangle2D.Double(x + 80, y - 450, 700, 450);
		} else {
			this.hitbox[0] = new Rectangle2D.Double(x - 700 - 80, y - 450, 700, 450);

		}
		this.duration = 5000;
	}

	@Override
	public void update() {
		super.update();
		if (this.duration % 200 == 0 && this.duration < 5000) {
			this.pHit.clear();
			this.bHit.clear();
		}
	}

	@Override
	public void processQueue() {
		while (!this.playerQueue.isEmpty()) {
			final Player p = this.playerQueue.poll(), owner = getOwner();
			if (p != null && !p.isDead()) {
				int damage = (int) (owner.rollDamage() * 0.6 + (.06 * owner.getSkillLevel(Skill.BOW_STORM)));
				final boolean crit = owner.rollCrit();
				if (crit) {
					if (owner.isSkillMaxed(Skill.BOW_STORM)) {
						damage = (int) owner.criticalDamage(damage, 5);
					} else {
						damage = (int) owner.criticalDamage(damage);
					}
				}
				p.queueDamage(new Damage(damage, true, owner, p, crit, this.hitbox[0], p.getHitbox()));
			}
		}

		while (!this.bossQueue.isEmpty()) {
			final Boss b = this.bossQueue.poll();
			final Player owner = getOwner();
			if (b != null && !b.isDead()) {
				int damage = (int) (owner.rollDamage() * 0.6 + (.06 * owner.getSkillLevel(Skill.BOW_STORM)));
				final boolean crit = owner.rollCrit();
				if (crit) {
					if (owner.isSkillMaxed(Skill.BOW_STORM)) {
						damage = (int) owner.criticalDamage(damage, 5);
					} else {
						damage = (int) owner.criticalDamage(damage);
					}
				}
				b.queueDamage(new Damage(damage, true, owner, b, crit, this.hitbox[0], b.getHitbox()));
			}
		}
		this.queuedEffect = false;
	}

}
