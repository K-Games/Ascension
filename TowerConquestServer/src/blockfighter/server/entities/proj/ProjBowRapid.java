package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import java.awt.geom.Rectangle2D;

/**
 * This is the base projectile class. Create projectile classes off this.
 *
 * @author Ken Kwan
 */
public class ProjBowRapid extends Projectile {

    /**
     * Projectile of Bow Skill Rapid Fire.
     *
     * @param l Room/Logic Module
     * @param k Projectile Key
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     */
    public ProjBowRapid(LogicModule l, int k, Player o, double x, double y) {
        super(l, k);
        setOwner(o);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        if (getOwner().getFacing() == Globals.RIGHT) {
            hitbox[0] = new Rectangle2D.Double(x + 80, y - 128, 497, 37);
        } else {
            hitbox[0] = new Rectangle2D.Double(x - 490 - 80, y - 128, 497, 37);
        }
        duration = 300;
    }

    @Override
    public void processQueue() {
        while (!playerQueue.isEmpty()) {
            Player p = playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (.75 + 0.02 * owner.getSkillLevel(Skill.BOW_RAPID)));
                boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                if (owner.isSkillMaxed(Skill.BOW_RAPID) && Globals.rng(50) + 1 <= 50) {
                    damage *= 2;
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(50, (owner.getFacing() == Globals.RIGHT) ? 3 : -3, -2, owner, p));
            }
        }

        while (!bossQueue.isEmpty()) {
            Boss b = bossQueue.poll();
            Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (.75 + 0.02 * owner.getSkillLevel(Skill.BOW_RAPID)));
                boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                if (owner.isSkillMaxed(Skill.BOW_RAPID) && Globals.rng(50) + 1 <= 50) {
                    damage *= 2;
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, hitbox[0], b.getHitbox()));
            }
        }
        queuedEffect = false;
    }

}
