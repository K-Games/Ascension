package blockfighter.server.entities.proj;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.buff.BuffVorpalDemise;
import blockfighter.server.entities.damage.DamageBuilder;
import blockfighter.server.entities.player.Player;
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
        double baseValue = owner.getSkill(Globals.SWORD_VORPAL).getSkillData().getBaseValue();
        double multValue = owner.getSkill(Globals.SWORD_VORPAL).getSkillData().getMultValue();
        double damage = owner.rollDamage() * (baseValue + multValue * owner.getSkillLevel(Globals.SWORD_VORPAL));

        double baseCritDmg = owner.getSkill(Globals.SWORD_VORPAL).getCustomValue(1);
        double multCritDmg = owner.getSkill(Globals.SWORD_VORPAL).getCustomValue(2);
        damage = (isCrit) ? owner.criticalDamage(damage, baseCritDmg + multCritDmg * owner.getSkillLevel(Globals.SWORD_VORPAL)) : damage;
        return (int) damage;
    }

    @Override
    public void applyDamage(Player target) {
        final Player owner = getOwner();
        double bonusCritChance = owner.getSkill(Globals.SWORD_VORPAL).getCustomValue(0);
        final boolean isCrit = owner.rollCrit(owner.isSkillMaxed(Globals.SWORD_VORPAL) ? bonusCritChance : 0);
        final int damage = calculateDamage(isCrit);
        if (!owner.hasSkill(Globals.SWORD_VORPAL_GHOST)) {

            target.queueDamage(new DamageBuilder()
                    .setDamage(damage)
                    .setOwner(owner)
                    .setTarget(target)
                    .setIsCrit(isCrit)
                    .build());
        } else {
            final double trueDamage = damage * owner.getSkill(Globals.SWORD_VORPAL_GHOST).getSkillData().getBaseValue();
            final double normalDamage = damage - trueDamage;

            target.queueDamage(new DamageBuilder()
                    .setDamage((int) normalDamage)
                    .setOwner(owner)
                    .setTarget(target)
                    .setIsCrit(isCrit)
                    .build());

            target.queueDamage(new DamageBuilder()
                    .setDamage((int) trueDamage)
                    .setOwner(owner)
                    .setTarget(target)
                    .setIsCrit(isCrit)
                    .setIsTrueDamage(true)
                    .build());
        }
        target.queueBuff(new BuffKnockback(this.logic, 200, (owner.getFacing() == Globals.RIGHT) ? 3 : -3, 0.1, owner, target));
        if (owner.hasSkill(Globals.SWORD_VORPAL_DEMISE)) {
            target.queueBuff(new BuffVorpalDemise(this.logic, (int) (getOwner().getSkill(Globals.SWORD_VORPAL_DEMISE).getCustomValue(0) + 50), owner, target));
        }

    }

}
