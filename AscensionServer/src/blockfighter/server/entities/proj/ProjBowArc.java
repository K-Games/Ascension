package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.net.PacketSender;
import java.awt.geom.Rectangle2D;

public class ProjBowArc extends Projectile {

    public ProjBowArc(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 100);
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x + 35, this.y - 125, 445, 108);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 445 - 35, this.y - 125, 445, 108);
        }
    }

    @Override
    public void applyEffect() {
        PacketSender.sendScreenShake(this.getOwner());
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (.8 + .02 * owner.getSkillLevel(Skill.BOW_ARC)));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, this.hitbox[0], p.getHitbox()));
                if (owner.isSkillMaxed(Skill.BOW_ARC)) {
                    double heal = damage * 0.05;
                    if (heal > owner.getStats()[Globals.STAT_MAXHP] * 1 / 30D) {
                        heal = owner.getStats()[Globals.STAT_MAXHP] * 1 / 30D;
                    }
                    owner.queueHeal((int) heal);
                }
                p.queueBuff(new BuffKnockback(this.room, 50, (owner.getFacing() == Globals.RIGHT) ? 1 : -1, -0.1, owner, p));
            }
        }

        while (!this.mobQueue.isEmpty()) {
            final Mob b = this.mobQueue.poll();
            final Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (.8 + .02 * owner.getSkillLevel(Skill.BOW_ARC)));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, this.hitbox[0], b.getHitbox()));
                if (owner.isSkillMaxed(Skill.BOW_ARC)) {
                    double heal = damage * 0.05;
                    if (heal > owner.getStats()[Globals.STAT_MAXHP] * 1 / 30D) {
                        heal = owner.getStats()[Globals.STAT_MAXHP] * 1 / 30D;
                    }
                    owner.queueHeal((int) heal);
                }
            }
        }
        this.queuedEffect = false;
    }

}
