package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import java.awt.geom.Rectangle2D;

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
    public ProjBowStorm(LogicModule l, int k, Player o, double x, double y) {
        super(l, k);
        setOwner(o);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        if (getOwner().getFacing() == Globals.RIGHT) {
            hitbox[0] = new Rectangle2D.Double(x + 80, y - 450, 700, 450);
        } else {
            hitbox[0] = new Rectangle2D.Double(x - 700 - 80, y - 450, 700, 450);

        }
        duration = 5000;
    }

    @Override
    public void update() {
        super.update();
        if (duration % 200 == 0 && duration < 5000) {
            pHit.clear();
        }
    }

    @Override
    public void processQueue() {
        while (!playerQueue.isEmpty()) {
            Player p = playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * 0.6 + (.06 * owner.getSkillLevel(Skill.BOW_STORM)));
                boolean crit = owner.rollCrit();
                if (crit) {
                    if (owner.isSkillMaxed(Skill.BOW_STORM)) {
                        damage = (int) owner.criticalDamage(damage, 5);
                    } else {
                        damage = (int) owner.criticalDamage(damage);
                    }
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, hitbox[0], p.getHitbox()));
            }
        }

        while (!bossQueue.isEmpty()) {
            Boss b = bossQueue.poll();
            Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * 0.6 + (.06 * owner.getSkillLevel(Skill.BOW_STORM)));
                boolean crit = owner.rollCrit();
                if (crit) {
                    if (owner.isSkillMaxed(Skill.BOW_STORM)) {
                        damage = (int) owner.criticalDamage(damage, 5);
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
