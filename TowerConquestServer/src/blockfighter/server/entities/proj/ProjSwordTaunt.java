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
    public ProjSwordTaunt(final LogicModule l, final int k, final Player o, final double x, final double y) {
        super(l, k, o, x, y, 200);
        this.hitbox = new Rectangle2D.Double[1];
        if (super.getOwner().getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(x - 20, y - 155, 250, 160);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(x - 250 + 20, y - 155, 250, 160);

        }
    }

    @Override
    public void processQueue() {
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (6 + 0.2 * owner.getSkillLevel(Skill.SWORD_TAUNT)));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, this.hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(300, (owner.getFacing() == Globals.RIGHT) ? 1 : -1, -4, owner, p));
            }
        }
        while (!this.bossQueue.isEmpty()) {
            final Boss b = this.bossQueue.poll();
            final Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (6 + 0.2 * owner.getSkillLevel(Skill.SWORD_TAUNT)));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                b.addAggro(owner, damage * 14);
                b.queueDamage(new Damage(damage, true, owner, b, crit, this.hitbox[0], b.getHitbox()));
            }
        }
        this.queuedEffect = false;
    }

}
