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
    public ProjBowFrost(LogicModule l, int k, Player o, double x, double y, boolean isSec) {
        super(l, k);
        setOwner(o);
        this.x = x;
        this.y = y;
        isSecondary = isSec;
        hitbox = new Rectangle2D.Double[1];
        if (getOwner().getFacing() == Globals.RIGHT) {
            hitbox[0] = new Rectangle2D.Double(x + 80, y - 160, 300, 148);
            speedX = 20;
        } else {
            hitbox[0] = new Rectangle2D.Double(x - 300 - 80, y - 160, 300, 148);
            speedX = -20;
        }
        duration = 500;
    }

    @Override
    public void update() {
        x += speedX;
        hitbox[0].x += speedX;
        super.update();
    }

    @Override
    public void processQueue() {
        while (!playerQueue.isEmpty()) {
            Player p = playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage;
                if (!isSecondary) {
                    damage = (int) (owner.rollDamage() * (1 + .2 * owner.getSkillLevel(Skill.BOW_FROST)));
                } else {
                    damage = (int) (owner.rollDamage() * 2.5);
                }
                boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(200, (owner.getFacing() == Globals.RIGHT) ? 3 : -3, -4, owner, p));
                p.queueBuff(new BuffStun(owner.isSkillMaxed(Skill.BOW_FROST) ? 2500 : 1500));
            }
        }
        while (!bossQueue.isEmpty()) {
            Boss b = bossQueue.poll();
            Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage;
                if (!isSecondary) {
                    damage = (int) (owner.rollDamage() * (1 + .2 * owner.getSkillLevel(Skill.BOW_FROST)));
                } else {
                    damage = (int) (owner.rollDamage() * 2.5);
                }
                boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, hitbox[0], b.getHitbox()));
                if (!isSecondary) {
                    b.queueBuff(new BuffStun(owner.isSkillMaxed(Skill.BOW_FROST) ? 2500 : 1500));
                }
            }
        }
        queuedEffect = false;
    }

}
