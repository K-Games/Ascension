package blockfighter.server.entities.proj;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.SkillSwordVorpal;
import blockfighter.shared.Globals;
import java.awt.geom.Rectangle2D;

public class ProjSwordVorpal extends Projectile {

    public ProjSwordVorpal(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 200);
        this.screenshake = true;
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 60, this.y - 100, 350, 90);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 350 + 60, this.y - 100, 350, 90);
        }
    }

    @Override
    public int calculateDamage(boolean isCrit) {
        final Player owner = getOwner();
        double baseValue = owner.getSkill(Globals.SWORD_VORPAL).getBaseValue();
        double multValue = owner.getSkill(Globals.SWORD_VORPAL).getMultValue();
        double damage = owner.rollDamage() * (baseValue + multValue * owner.getSkillLevel(Globals.SWORD_VORPAL));

        double baseCritDmg = owner.getSkill(Globals.SWORD_VORPAL).getCustomValue(SkillSwordVorpal.CUSTOMHEADER_BASEBONUSCRITDMG);
        double multCritDmg = owner.getSkill(Globals.SWORD_VORPAL).getCustomValue(SkillSwordVorpal.CUSTOMHEADER_MULTBONUSCRITDMG);
        damage = (isCrit) ? owner.criticalDamage(damage, baseCritDmg + multCritDmg * owner.getSkillLevel(Globals.SWORD_VORPAL)) : damage;
        return (int) damage;
    }

    @Override
    public void applyDamage(Player target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit(owner.isSkillMaxed(Globals.SWORD_VORPAL) ? owner.getSkill(Globals.SWORD_VORPAL).getCustomValue(SkillSwordVorpal.CUSTOMHEADER_BONUSCRITCHC) : 0);
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit));
        target.queueBuff(new BuffKnockback(this.logic, 200, (owner.getFacing() == Globals.RIGHT) ? 3 : -3, 0.1, owner, target));
    }

    @Override
    public void applyDamage(Mob target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit(owner.isSkillMaxed(Globals.SWORD_VORPAL) ? 0.3 : 0);
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit));
    }

}
