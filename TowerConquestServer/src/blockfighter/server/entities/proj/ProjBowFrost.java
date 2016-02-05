package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.buff.BuffStun;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import java.awt.geom.Rectangle2D;

/**
 * This is the base projectile class. Create projectile classes off this.
 *
 * @author Ken Kwan
 */
public class ProjBowFrost extends Projectile {

    private double speedX = 0;
    private final boolean isSecondary;

    /**
     * Projectile of Bow Skill Frost Bind.
     *
     * @param l Room/Logic Module
     * @param k Projectile Key
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     * @param isSec Is a secondary(non-freezing) shot.
     */
    public ProjBowFrost(final LogicModule l, final int k, final Player o, final double x, final double y, final boolean isSec) {
        super(l, k, o, x, y, 500);
        this.isSecondary = isSec;
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x + 80, this.y - 160, 300, 148);
            this.speedX = 20;
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 300 - 80, this.y - 160, 300, 148);
            this.speedX = -20;
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
                int damage;
                if (!this.isSecondary) {
                    damage = (int) (owner.rollDamage() * (1 + .2 * owner.getSkillLevel(Skill.BOW_FROST)));
                } else {
                    damage = (int) (owner.rollDamage() * 2.5);
                }
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, this.hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(this.logic, 200, (owner.getFacing() == Globals.RIGHT) ? 3 : -3, -4, owner, p));
                p.queueBuff(new BuffStun(this.logic, owner.isSkillMaxed(Skill.BOW_FROST) ? 2500 : 1500));
            }
        }
        while (!this.bossQueue.isEmpty()) {
            final Boss b = this.bossQueue.poll();
            final Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage;
                if (!this.isSecondary) {
                    damage = (int) (owner.rollDamage() * (1 + .2 * owner.getSkillLevel(Skill.BOW_FROST)));
                } else {
                    damage = (int) (owner.rollDamage() * 2.5);
                }
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, this.hitbox[0], b.getHitbox()));
                if (!this.isSecondary) {
                    b.queueBuff(new BuffStun(this.logic, owner.isSkillMaxed(Skill.BOW_FROST) ? 2500 : 1500));
                }
            }
        }
        this.queuedEffect = false;
    }

}
