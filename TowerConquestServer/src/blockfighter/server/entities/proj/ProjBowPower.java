package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import java.awt.geom.Rectangle2D;

/**
 * This is the base projectile class. Create projectile classes off this.
 *
 * @author Ken Kwan
 */
public class ProjBowPower extends Projectile {

    /**
     * Projectile of Bow Skill Power Shot.
     *
     * @param l Room/Logic Module
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     */
    public ProjBowPower(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 300);
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x + 30, this.y - 145, 700, 150);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 700 - 30, this.y - 145, 700, 150);
        }
    }

    @Override
    public void processQueue() {
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (5 + owner.getSkillLevel(Skill.BOW_POWER)));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    if (owner.isSkillMaxed(Skill.BOW_POWER)) {
                        damage = (int) owner.criticalDamage(damage, 3);
                    } else {
                        damage = (int) owner.criticalDamage(damage);
                    }
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, this.hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(this.logic, 500, (owner.getFacing() == Globals.RIGHT) ? 20 : -20, -25, owner, p));
            }
        }

        while (!this.mobQueue.isEmpty()) {
            final Mob b = this.mobQueue.poll();
            final Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (5 + owner.getSkillLevel(Skill.BOW_POWER)));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    if (owner.isSkillMaxed(Skill.BOW_POWER)) {
                        damage = (int) owner.criticalDamage(damage, 3);
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
