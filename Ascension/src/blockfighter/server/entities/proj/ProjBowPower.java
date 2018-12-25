package blockfighter.server.entities.proj;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.shared.Globals;
import java.awt.geom.Rectangle2D;

public class ProjBowPower extends Projectile {

    public ProjBowPower(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 150);
        this.screenshake = false;
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
        final Skill skill = owner.getSkill(Globals.BOW_POWER);
        double baseValue = skill.getSkillData().getBaseValue();
        double multValue = skill.getSkillData().getMultValue();
        double damage = owner.rollDamage() * (baseValue + multValue * owner.getSkillLevel(Globals.BOW_POWER));
        if (isCrit) {
            double bonusCritDmg = (skill.isMaxed()) ? skill.getCustomValue(0) : 0;
            damage = owner.criticalDamage(damage, bonusCritDmg);
        }
        return (int) damage;
    }

    @Override
    public void applyDamage(Player target) {
        super.applyDamage(target);
        final Player owner = getOwner();
        target.queueBuff(new BuffKnockback(this.logic, 500, (owner.getFacing() == Globals.RIGHT) ? 20 : -20, -25, owner, target));
    }

}
