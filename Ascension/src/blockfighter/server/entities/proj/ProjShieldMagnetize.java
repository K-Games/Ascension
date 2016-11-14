package blockfighter.server.entities.proj;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.SkillShieldMagnetize;
import blockfighter.shared.Globals;
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
        double baseValue = owner.getSkill(Globals.SHIELD_MAGNETIZE).getBaseValue();
        double multValue = owner.getSkill(Globals.SHIELD_MAGNETIZE).getMultValue();
        double baseDef = owner.getSkill(Globals.SHIELD_MAGNETIZE).getCustomValue(SkillShieldMagnetize.CUSTOMHEADER_BASEDEF);
        double multDef = owner.getSkill(Globals.SHIELD_MAGNETIZE).getCustomValue(SkillShieldMagnetize.CUSTOMHEADER_MULTDEF);
        double damage = owner.rollDamage() * (baseValue + multValue * owner.getSkillLevel(Globals.SHIELD_MAGNETIZE))
                + (owner.getStats()[Globals.STAT_DEFENSE] * (baseDef + multDef * owner.getSkillLevel(Globals.SHIELD_MAGNETIZE)));
        damage *= (owner.isSkillMaxed(Globals.SHIELD_MAGNETIZE)) ? owner.getSkill(Globals.SHIELD_MAGNETIZE).getCustomValue(SkillShieldMagnetize.CUSTOMHEADER_MAXLEVELMULT) : 1;
        damage = (isCrit) ? owner.criticalDamage(damage) : damage;
        return (int) damage;
    }

    @Override
    public void applyDamage(Player target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        target.queueBuff(new BuffKnockback(this.logic, 300, (owner.getFacing() == Globals.RIGHT) ? 4 : -4, -5, owner, target));
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
