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
public class ProjSwordDrive extends Projectile {

    private boolean healed = false;

    /**
     * Projectile of Sword Skill Drive.
     *
     * @param l Room/Logic Module
     * @param k Projectile Key
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     */
    public ProjSwordDrive(LogicModule l, int k, Player o, double x, double y) {
        super(l, k);
        setOwner(o);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        if (getOwner().getFacing() == Globals.RIGHT) {
            hitbox[0] = new Rectangle2D.Double(x - 310, y - 167, 560, 150);
        } else {
            hitbox[0] = new Rectangle2D.Double(x - 560 + 310, y - 167, 560, 150);

        }
        duration = 250;
    }

    @Override
    public void processQueue() {
        while (!playerQueue.isEmpty()) {
            Player p = playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (.75 + 0.03 * owner.getSkillLevel(Skill.SWORD_DRIVE)));
                boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(50, (owner.getFacing() == Globals.RIGHT) ? 3 : -3, -0.5, owner, p));

                if (!healed && owner.isSkillMaxed(Skill.SWORD_DRIVE)) {
                    double heal = owner.getStats()[Globals.STAT_MAXHP] * 0.005;
                    owner.queueHeal((int) heal);
                    healed = true;
                }
            }
        }
        while (!bossQueue.isEmpty()) {
            Boss b = bossQueue.poll();
            Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (.75 + 0.03 * owner.getSkillLevel(Skill.SWORD_DRIVE)));
                boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, hitbox[0], b.getHitbox()));

                if (!healed && owner.isSkillMaxed(Skill.SWORD_DRIVE)) {
                    double heal = owner.getStats()[Globals.STAT_MAXHP] * 0.005;
                    owner.queueHeal((int) heal);
                    healed = true;
                }
            }
        }
        queuedEffect = false;
    }

}
