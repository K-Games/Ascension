package blockfighter.server.entities.proj;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.SkillBowPower;
import blockfighter.shared.Globals;
import java.awt.geom.Rectangle2D;

public class ProjBowPower extends Projectile {

    public ProjBowPower(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 150);
        this.screenshake = true;
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x + 30, this.y - 145, 650, 150);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 650 - 30, this.y - 145, 650, 150);
        }
    }

    @Override
    public int calculateDamage(final boolean isCrit) {
        final Player owner = getOwner();
        double baseValue = owner.getSkill(Globals.BOW_POWER).getBaseValue();
        double multValue = owner.getSkill(Globals.BOW_POWER).getMultValue();
        double damage = owner.rollDamage() * (baseValue + multValue * owner.getSkillLevel(Globals.BOW_POWER));
        if (isCrit) {
            double bonusCritDmg = (owner.getSkill(Globals.BOW_POWER).isMaxed()) ? owner.getSkill(Globals.BOW_POWER).getCustomValue(SkillBowPower.CUSTOMHEADER_MAXLEVELBONUSCRITDMG) : 0;
            damage = owner.criticalDamage(damage, bonusCritDmg);
        }
        return (int) damage;
    }

    @Override
    public void applyDamage(Player target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit));
        target.queueBuff(new BuffKnockback(this.logic, 500, (owner.getFacing() == Globals.RIGHT) ? 20 : -20, -25, owner, target));
    }

    @Override
    public void applyDamage(Mob target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit));
    }

}
