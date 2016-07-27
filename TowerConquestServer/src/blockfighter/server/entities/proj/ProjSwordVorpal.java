package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import java.awt.geom.Rectangle2D;

public class ProjSwordVorpal extends Projectile {

    public ProjSwordVorpal(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 200);
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 60, this.y - 130, 350, 113);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 350 + 60, this.y - 130, 350, 113);
        }
    }

    @Override
    public void processQueue() {
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (1.45 + 0.06 * owner.getSkillLevel(Skill.SWORD_VORPAL)));
                final boolean crit = owner.rollCrit(owner.isSkillMaxed(Skill.SWORD_VORPAL) ? 0.3 : 0);
                if (crit) {
                    damage = (int) owner.criticalDamage(damage, 0.4 + 0.03 * owner.getSkillLevel(Skill.SWORD_VORPAL));
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, this.hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(this.room, 200, (owner.getFacing() == Globals.RIGHT) ? 1 : -1, -3, owner, p));
            }
        }
        while (!this.mobQueue.isEmpty()) {
            final Mob b = this.mobQueue.poll();
            final Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (1.45 + 0.06 * owner.getSkillLevel(Skill.SWORD_VORPAL)));
                final boolean crit = owner.rollCrit(owner.isSkillMaxed(Skill.SWORD_VORPAL) ? 0.3 : 0);
                if (crit) {
                    damage = (int) owner.criticalDamage(damage, 0.4 + 0.03 * owner.getSkillLevel(Skill.SWORD_VORPAL));
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, this.hitbox[0], b.getHitbox()));
            }
        }
        this.queuedEffect = false;
    }

}
