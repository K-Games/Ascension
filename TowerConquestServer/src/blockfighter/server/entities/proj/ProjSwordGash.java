package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import java.awt.geom.Rectangle2D;

/**
 * This is the base projectile class. Create projectile classes off this.
 *
 * @author Ken Kwan
 */
public class ProjSwordGash extends Projectile {

    private boolean healed = false;

    /**
     * Projectile of Sword Skill Gash.
     *
     * @param l Room/Logic Module
     * @param k Projectile Key
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     * @param hit
     */
    public ProjSwordGash(final LogicModule l, final int k, final Player o, final double x, final double y, final byte hit) {
        super(l, k, o, x, y, 50);
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            switch (hit) {
                case 1:
                case 4:
                    this.hitbox[0] = new Rectangle2D.Double(this.x - 50, this.y - 75, 250, 76);
                    break;
                case 2:
                case 3:
                    this.hitbox[0] = new Rectangle2D.Double(this.x - 50, this.y - 153, 246, 153);
                    break;
            }
        } else {
            switch (hit) {
                case 1:
                case 4:
                    this.hitbox[0] = new Rectangle2D.Double(this.x - 250 + 50, this.y - 75, 250, 76);
                    break;
                case 2:
                case 3:
                    this.hitbox[0] = new Rectangle2D.Double(this.x - 252 + 50, this.y - 153, 246, 153);
                    break;
            }
        }
    }

    @Override
    public void processQueue() {
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (.75 + 0.03 * owner.getSkillLevel(Skill.SWORD_GASH)));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, this.hitbox[0], p.getHitbox()));
                //p.queueBuff(new BuffKnockback(this.logic, 10, (owner.getFacing() == Globals.RIGHT) ? 3 : -3, -0.5, owner, p));

                if (!this.healed && owner.isSkillMaxed(Skill.SWORD_GASH)) {
                    final double heal = owner.getStats()[Globals.STAT_MAXHP] * 0.0025;
                    owner.queueHeal((int) heal);
                    this.healed = true;
                }
            }
        }
        while (!this.mobQueue.isEmpty()) {
            final Mob b = this.mobQueue.poll();
            final Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (.75 + 0.03 * owner.getSkillLevel(Skill.SWORD_GASH)));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, this.hitbox[0], b.getHitbox()));

                if (!this.healed && owner.isSkillMaxed(Skill.SWORD_GASH)) {
                    final double heal = owner.getStats()[Globals.STAT_MAXHP] * 0.0025;
                    owner.queueHeal((int) heal);
                    this.healed = true;
                }
            }
        }
        this.queuedEffect = false;
    }

}
