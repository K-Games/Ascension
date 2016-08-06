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

public class ProjSwordTaunt extends Projectile {

    public ProjSwordTaunt(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 200);
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 20, this.y - 155, 250, 160);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 250 + 20, this.y - 155, 250, 160);

        }
    }

    @Override
    public void applyEffect() {
        PacketSender.sendScreenShake(this.getOwner());
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (8 + 0.2 * owner.getSkillLevel(Skill.SWORD_TAUNT)));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, this.hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(this.room, 300, (owner.getFacing() == Globals.RIGHT) ? 1 : -1, -4, owner, p));
            }
        }
        while (!this.mobQueue.isEmpty()) {
            final Mob b = this.mobQueue.poll();
            final Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (8 + 0.2 * owner.getSkillLevel(Skill.SWORD_TAUNT)));
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