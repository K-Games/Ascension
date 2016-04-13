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
public class ProjShieldToss extends Projectile {

    private double speedX = 0;

    /**
     * Projectile of Bow Skill Frost Bind.
     *
     * @param l Room/Logic Module
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     */
    public ProjShieldToss(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 500);
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x, this.y - 190, 190, 150);
            this.speedX = 12;
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 254, this.y - 190, 190, 150);
            this.speedX = -12;
        }
    }

    @Override
    public void update() {
        this.x += this.speedX;
        this.hitbox[0].x += this.speedX;
        super.update();
    }

    @Override
    public void processQueue() {
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (1.5 + 0.15 * owner.getSkillLevel(Skill.SHIELD_TOSS))
                        + (owner.getStats()[Globals.STAT_DEFENSE] * (15 + owner.getSkillLevel(Skill.SHIELD_TOSS))));

                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, this.hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(this.logic, 100, (owner.getFacing() == Globals.RIGHT) ? 3 : -3, -4, owner, p));
            }
        }
        while (!this.mobQueue.isEmpty()) {
            final Mob b = this.mobQueue.poll();
            final Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (1.5 + 0.15 * owner.getSkillLevel(Skill.SHIELD_TOSS))
                        + (owner.getStats()[Globals.STAT_DEFENSE] * (15 + owner.getSkillLevel(Skill.SHIELD_TOSS))));

                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, this.hitbox[0], b.getHitbox()));
            }
        }
        this.queuedEffect = false;
    }

}
