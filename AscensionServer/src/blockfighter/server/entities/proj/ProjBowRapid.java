package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import java.awt.geom.Rectangle2D;

public class ProjBowRapid extends Projectile {

    public ProjBowRapid(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 100);
        this.screenshake = true;
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x + 40, this.y - 80, 449, 20);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 449 - 40, this.y - 80, 449, 20);
        }
    }

    @Override
    public void applyEffect() {
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (.8 + 0.02 * owner.getSkillLevel(Skill.BOW_RAPID)));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                if (owner.isSkillMaxed(Skill.BOW_RAPID) && Globals.rng(50) + 1 <= 50) {
                    damage *= 2;
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, this.hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(this.room, 50, (owner.getFacing() == Globals.RIGHT) ? 3 : -3, -2, owner, p));
            }
        }

        while (!this.mobQueue.isEmpty()) {
            final Mob b = this.mobQueue.poll();
            final Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (.8 + 0.02 * owner.getSkillLevel(Skill.BOW_RAPID)));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                if (owner.isSkillMaxed(Skill.BOW_RAPID) && Globals.rng(50) + 1 <= 50) {
                    damage *= 2;
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, this.hitbox[0], b.getHitbox()));
            }
        }
        this.queuedEffect = false;
    }

}
