package blockfighter.server.entities.proj;

import blockfighter.shared.Globals;
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
    public int calculateDamage(final boolean isCrit) {
        final Player owner = getOwner();
        double damage = owner.rollDamage() * (.8 + 0.02 * owner.getSkillLevel(Skill.BOW_RAPID));
        damage = (isCrit) ? owner.criticalDamage(damage) : damage;
        if (owner.isSkillMaxed(Skill.BOW_RAPID) && Globals.rng(50) + 1 <= 50) {
            damage *= 2;
        }
        return (int) damage;
    }

    @Override
    public void applyDamage(Player target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit, this.hitbox[0], target.getHitbox()));
        target.queueBuff(new BuffKnockback(this.logic, 50, (owner.getFacing() == Globals.RIGHT) ? 3 : -3, -2, owner, target));
    }

    @Override
    public void applyDamage(Mob target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit, this.hitbox[0], target.getHitbox()));
    }
}
