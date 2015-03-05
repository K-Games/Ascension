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
public class ProjBowArc extends Projectile {

    /**
     * Projectile of Bow Skill Arcshot.
     *
     * @param l Room/Logic Module
     * @param k Projectile Key
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     */
    public ProjBowArc(LogicModule l, int k, Player o, double x, double y) {
        super(l, k);
        setOwner(o);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        if (getOwner().getFacing() == Globals.RIGHT) {
            hitbox[0] = new Rectangle2D.Double(x + 80, y - 284, 490, 350);
        } else {
            hitbox[0] = new Rectangle2D.Double(x - 490 - 80, y - 284, 490, 350);

        }
        duration = 300;
    }

    @Override
    public void processQueue() {
        while (!playerQueue.isEmpty()) {
            Player p = playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (.8 + .02 * owner.getSkillLevel(Skill.BOW_ARC)));
                boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, hitbox[0], p.getHitbox()));
                if (owner.isSkillMaxed(Skill.BOW_ARC)) {
                    double heal = damage * 0.05;
                    if (heal > owner.getStats()[Globals.STAT_MAXHP] * 1 / 30D) {
                        heal = owner.getStats()[Globals.STAT_MAXHP] * 1 / 30D;
                    }
                    owner.queueHeal((int) heal);
                }
                p.queueBuff(new BuffKnockback(50, (owner.getFacing() == Globals.RIGHT) ? 1 : -1, -0.1, owner, p));
            }
        }

        while (!bossQueue.isEmpty()) {
            Boss b = bossQueue.poll();
            Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (.8 + .02 * owner.getSkillLevel(Skill.BOW_ARC)));
                boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, hitbox[0], b.getHitbox()));
                if (owner.isSkillMaxed(Skill.BOW_ARC)) {
                    double heal = damage * 0.05;
                    if (heal > owner.getStats()[Globals.STAT_MAXHP] * 1 / 30D) {
                        heal = owner.getStats()[Globals.STAT_MAXHP] * 1 / 30D;
                    }
                    owner.queueHeal((int) heal);
                }
            }
        }
        queuedEffect = false;
    }

}
