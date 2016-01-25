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
public class ProjSwordTaunt extends Projectile {

    /**
     * Projectile of Sword Skill Taunt.
     *
     * @param l Room/Logic Module
     * @param k Projectile Key
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     */
    public ProjSwordTaunt(LogicModule l, int k, Player o, double x, double y) {
        super(l, k);
        setOwner(o);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        if (getOwner().getFacing() == Globals.RIGHT) {
            hitbox[0] = new Rectangle2D.Double(x - 20, y - 155, 250, 160);
        } else {
            hitbox[0] = new Rectangle2D.Double(x - 250 + 20, y - 155, 250, 160);

        }
        duration = 200;
    }

    @Override
    public void processQueue() {
        while (!playerQueue.isEmpty()) {
            Player p = playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (6 + 0.2 * owner.getSkillLevel(Skill.SWORD_TAUNT)));
                boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(300, (owner.getFacing() == Globals.RIGHT) ? 1 : -1, -4, owner, p));
            }
        }
        while (!bossQueue.isEmpty()) {
            Boss b = bossQueue.poll();
            Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (6 + 0.2 * owner.getSkillLevel(Skill.SWORD_TAUNT)));
                boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                b.addAggro(owner, damage * 14);
                b.queueDamage(new Damage(damage, true, owner, b, crit, hitbox[0], b.getHitbox()));
            }
        }
        queuedEffect = false;
    }

}
