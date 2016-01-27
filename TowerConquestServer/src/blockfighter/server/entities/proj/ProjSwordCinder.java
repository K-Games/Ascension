package blockfighter.server.entities.proj;

import java.awt.geom.Rectangle2D;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.buff.BuffBurn;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;

/**
 * This is the base projectile class. Create projectile classes off this.
 *
 * @author Ken Kwan
 */
public class ProjSwordCinder extends Projectile {

	/**
	 * Projectile of Sword Skill Cinder.
	 *
	 * @param l Room/Logic Module
	 * @param k Projectile Key
	 * @param o Owning player
	 * @param x Spawn x-coordinate
	 * @param y Spawn y-coordinate
	 */
	public ProjSwordCinder(final LogicModule l, final int k, final Player o, final double x, final double y) {
		super(l, k);
		setOwner(o);
		this.x = x;
		this.y = y;
		this.hitbox = new Rectangle2D.Double[1];
		if (getOwner().getFacing() == Globals.RIGHT) {
			this.hitbox[0] = new Rectangle2D.Double(x - 30, y - 200, 190, 250);
		} else {
			this.hitbox[0] = new Rectangle2D.Double(x - 190 + 30, y - 200, 190, 250);

		}
		this.duration = 300;
	}

	@Override
	public void processQueue() {
		while (!this.playerQueue.isEmpty()) {
			final Player p = this.playerQueue.poll(), owner = getOwner();
			if (p != null && !p.isDead()) {
				int damage = (int) (owner.rollDamage() * (4.5 + owner.getSkillLevel(Skill.SWORD_CINDER) * .2));
				final boolean crit = owner.rollCrit((owner.isSkillMaxed(Skill.SWORD_CINDER)) ? 1 : 0);
				if (crit) {
					damage = (int) owner.criticalDamage(damage);
				}
				p.queueDamage(new Damage(damage, true, owner, p, crit, this.hitbox[0], p.getHitbox()));
				p.queueBuff(new BuffKnockback(300, (owner.getFacing() == Globals.RIGHT) ? 1 : -1, -4, owner, p));
				p.queueBuff(new BuffBurn(4000, owner.getSkillLevel(Skill.SWORD_CINDER) * 0.01,
						owner.isSkillMaxed(Skill.SWORD_CINDER) ? owner.rollDamage() : 0, owner, p));
				final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
				bytes[0] = Globals.DATA_PARTICLE_EFFECT;
				bytes[1] = Globals.PARTICLE_BURN;
				bytes[2] = p.getKey();
				sender.sendAll(bytes, this.logic.getRoom());
			}
		}
		while (!this.bossQueue.isEmpty()) {
			final Boss b = this.bossQueue.poll();
			final Player owner = getOwner();
			if (b != null && !b.isDead()) {
				int damage = (int) (owner.rollDamage() * (4.5 + owner.getSkillLevel(Skill.SWORD_CINDER) * .2));
				final boolean crit = owner.rollCrit((owner.isSkillMaxed(Skill.SWORD_CINDER)) ? 1 : 0);
				if (crit) {
					damage = (int) owner.criticalDamage(damage);
				}
				b.queueDamage(new Damage(damage, true, owner, b, crit, this.hitbox[0], b.getHitbox()));
				b.queueBuff(new BuffBurn(4000, owner.getSkillLevel(Skill.SWORD_CINDER) * 0.01,
						owner.isSkillMaxed(Skill.SWORD_CINDER) ? owner.rollDamage() : 0, owner, b));
				// Monster buff display
			}
		}
		this.queuedEffect = false;
	}

}
