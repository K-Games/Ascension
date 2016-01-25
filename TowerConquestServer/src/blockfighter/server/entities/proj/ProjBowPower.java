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
public class ProjBowPower extends Projectile {

    /**
     * Projectile of Bow Skill Power Shot.
     *
     * @param l Room/Logic Module
     * @param k Projectile Key
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     */
    public ProjBowPower(LogicModule l, int k, Player o, double x, double y) {
        super(l, k);
        setOwner(o);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        if (getOwner().getFacing() == Globals.RIGHT) {
            hitbox[0] = new Rectangle2D.Double(x + 80, y - 155, 700, 150);
        } else {
            hitbox[0] = new Rectangle2D.Double(x - 700 - 80, y - 155, 700, 150);

        }
        duration = 300;
    }

    @Override
    public void processQueue() {
        while (!playerQueue.isEmpty()) {
            Player p = playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (5 + owner.getSkillLevel(Skill.BOW_POWER)));
                boolean crit = owner.rollCrit();
                if (crit) {
                    if (owner.isSkillMaxed(Skill.BOW_POWER)) {
                        damage = (int) owner.criticalDamage(damage, 3);
                    } else {
                        damage = (int) owner.criticalDamage(damage);
                    }
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(500, (owner.getFacing() == Globals.RIGHT) ? 20 : -20, -25, owner, p));
            }
        }

        while (!bossQueue.isEmpty()) {
            Boss b = bossQueue.poll();
            Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (5 + owner.getSkillLevel(Skill.BOW_POWER)));
                boolean crit = owner.rollCrit();
                if (crit) {
                    if (owner.isSkillMaxed(Skill.BOW_POWER)) {
                        damage = (int) owner.criticalDamage(damage, 3);
                    } else {
                        damage = (int) owner.criticalDamage(damage);
                    }
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, hitbox[0], b.getHitbox()));
            }
        }
        queuedEffect = false;
    }

}
