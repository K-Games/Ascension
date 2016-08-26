package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import java.awt.geom.Rectangle2D;

public class ProjShieldMagnetize extends Projectile {

    public ProjShieldMagnetize(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 150);
        this.screenshake = true;
        this.hitbox = new Rectangle2D.Double[1];
        this.hitbox[0] = new Rectangle2D.Double(this.x - 100, this.y - 200, 200, 200);
    }

    @Override
    public int calculateDamage(final boolean isCrit) {
        final Player owner = getOwner();
        double damage = owner.rollDamage() * (1.5 + 0.15 * owner.getSkillLevel(Skill.SHIELD_MAGNETIZE))
                + (owner.getStats()[Globals.STAT_DEFENSE] * (15 + owner.getSkillLevel(Skill.SHIELD_MAGNETIZE)));
        damage *= (owner.isSkillMaxed(Skill.SHIELD_MAGNETIZE)) ? 3 : 1;
        damage = (isCrit) ? owner.criticalDamage(damage) : damage;
        return (int) damage;
    }

    @Override
    public void applyDamage(Player target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit, this.hitbox[0], target.getHitbox()));
    }

    @Override
    public void applyDamage(Mob target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit, this.hitbox[0], target.getHitbox()));
    }
}
