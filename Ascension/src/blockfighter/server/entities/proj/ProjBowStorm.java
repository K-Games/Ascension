package blockfighter.server.entities.proj;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.SkillBowStorm;
import blockfighter.shared.Globals;
import java.awt.geom.Rectangle2D;

public class ProjBowStorm extends Projectile {

    private long lastDamageTime;
    private static final long DAMAGE_INSTANCE_DELAY = 100;

    public ProjBowStorm(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, o.getSkill(Globals.BOW_STORM).getCustomValue(SkillBowStorm.CUSTOMHEADER_DURATION).intValue());
        lastDamageTime = this.logic.getTime();
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x + 80, this.y - 450, 700, 450);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 700 - 80, this.y - 450, 700, 450);

        }
    }

    @Override
    public int calculateDamage(final boolean isCrit) {
        final Player owner = getOwner();
        double dmgInstances = owner.getSkill(Globals.BOW_STORM).getCustomValue(SkillBowStorm.CUSTOMHEADER_DURATION) / DAMAGE_INSTANCE_DELAY;
        double baseValue = owner.getSkill(Globals.BOW_STORM).getBaseValue() / dmgInstances;
        double multValue = owner.getSkill(Globals.BOW_STORM).getMultValue() / dmgInstances;

        double damage = owner.rollDamage() * (baseValue + multValue * owner.getSkillLevel(Globals.BOW_STORM));
        if (isCrit) {
            damage = owner.isSkillMaxed(Globals.BOW_STORM) ? owner.criticalDamage(damage, owner.getSkill(Globals.BOW_STORM).getCustomValue(SkillBowStorm.CUSTOMHEADER_MAXLVLBONUSCRITDMG)) : owner.criticalDamage(damage);
        }
        return (int) damage;
    }

    @Override
    public void applyDamage(Player target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit));
        target.queueBuff(new BuffKnockback(this.logic, 50, (Globals.rng(2) == 0) ? 3 : -3, 0, owner, target));
    }

    @Override
    public void applyDamage(Mob target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit));
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(this.logic.getTime() - lastDamageTime) >= DAMAGE_INSTANCE_DELAY) {
            lastDamageTime = this.logic.getTime();
            this.pHit.clear();
            this.bHit.clear();
        }
    }
}
