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
    public ProjSwordSlash(final LogicModule l, final int k, final Player o, final double x, final double y, final int hit) {
        super(l, k, o, x, y, 200);
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            switch (hit) {
                case 1:
                    this.hitbox[0] = new Rectangle2D.Double(this.x-20, this.y - 120, 250, 80);
                    break;
                case 2:
                    this.hitbox[0] = new Rectangle2D.Double(this.x-20, this.y - 70, 250, 80);
                    break;
                case 3:
                    this.hitbox[0] = new Rectangle2D.Double(this.x-20, this.y - 95, 270, 60);
                    break;
            }
        } else {
            switch (hit) {
                case 1:
                    this.hitbox[0] = new Rectangle2D.Double(this.x - 274+20, this.y - 120, 250, 80);
                    break;
                case 2:
                    this.hitbox[0] = new Rectangle2D.Double(this.x - 274+20, this.y - 70, 250, 80);
                    break;
                case 3:
                    this.hitbox[0] = new Rectangle2D.Double(this.x - 274+20, this.y - 95, 270, 60);
                    break;
            }
        }
    }

    @Override
    public void processQueue() {
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (1 + 0.04 * owner.getSkillLevel(Skill.SWORD_SLASH)));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, this.hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(150, (owner.getFacing() == Globals.RIGHT) ? 0.5 : -0.5, -3, owner, p));
            }
        }
        while (!this.bossQueue.isEmpty()) {
            final Boss b = this.bossQueue.poll();
            final Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (1 + 0.04 * owner.getSkillLevel(Skill.SWORD_SLASH)));
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
