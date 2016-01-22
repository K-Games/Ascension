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
public class ProjSwordSlash extends Projectile {

    /**
     * Projectile of Sword Skill Defensive Impact.
     *
     * @param l Room/Logic Module
     * @param k Projectile Key
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     * @param hit Hit number to determine hit box.
     */
    public ProjSwordSlash(LogicModule l, int k, Player o, double x, double y, int hit) {
        super(l, k);
        setOwner(o);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        if (getOwner().getFacing() == Globals.RIGHT) {
            switch (hit) {
                case 1:
                    hitbox[0] = new Rectangle2D.Double(x - 90, y - 240, 250, 300);
                    break;
                case 2:
                    hitbox[0] = new Rectangle2D.Double(x - 40, y - 240, 250, 300);
                    break;
                case 3:
                    hitbox[0] = new Rectangle2D.Double(x - 40, y - 90, 320, 60);
                    break;
            }
        } else {
            switch (hit) {
                case 1:
                    hitbox[0] = new Rectangle2D.Double(x - 250 + 90, y - 240, 250, 300);
                    break;
                case 2:
                    hitbox[0] = new Rectangle2D.Double(x - 250 + 40, y - 240, 250, 300);
                    break;
                case 3:
                    hitbox[0] = new Rectangle2D.Double(x - 320 + 40, y - 90, 320, 60);
                    break;
            }
        }
        duration = 200;
    }

    @Override
    public void processQueue() {
        while (!playerQueue.isEmpty()) {
            Player p = playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (1 + 0.04 * owner.getSkillLevel(Skill.SWORD_SLASH)));
                boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(150, (owner.getFacing() == Globals.RIGHT) ? 0.5 : -0.5, -3, owner, p));
            }
        }
        while (!bossQueue.isEmpty()) {
            Boss b = bossQueue.poll();
            Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (1 + 0.04 * owner.getSkillLevel(Skill.SWORD_SLASH)));
                boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, hitbox[0], b.getHitbox()));
            }
        }
        queuedEffect = false;
    }

}
