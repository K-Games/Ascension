package blockfighter.server.entities.proj;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.buff.BuffStun;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.SkillShieldCharge;
import blockfighter.shared.Globals;
import java.awt.geom.Rectangle2D;

public class ProjShieldCharge extends Projectile {

    public ProjShieldCharge(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 200);
        this.screenshake = true;
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 150, this.y - 170, 250, 170);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 250 + 150, this.y - 170, 250, 170);
        }
    }

    @Override
    public int calculateDamage(final boolean isCrit) {
        final Player owner = getOwner();
        double baseValue = owner.getSkill(Globals.SHIELD_CHARGE).getBaseValue();
        double multValue = owner.getSkill(Globals.SHIELD_CHARGE).getMultValue();
        double damage = owner.rollDamage() * (baseValue + multValue * owner.getSkillLevel(Globals.SHIELD_CHARGE));
        damage = (isCrit) ? owner.criticalDamage(damage) : damage;
        return (int) damage;
    }

    @Override
    public void applyDamage(Player target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit));
        if (owner.isSkillMaxed(Globals.SHIELD_CHARGE)) {
            double stunDuration = owner.getSkill(Globals.SHIELD_CHARGE).getCustomValue(SkillShieldCharge.CUSTOMHEADER_STUN);
            target.queueBuff(new BuffStun(this.logic, (int) stunDuration + 200));
        } else {
            target.queueBuff(new BuffStun(this.logic, 200));
        }
    }

    @Override
    public void applyDamage(Mob target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit));
        if (owner.isSkillMaxed(Globals.SHIELD_CHARGE)) {
            double stunDuration = owner.getSkill(Globals.SHIELD_CHARGE).getCustomValue(SkillShieldCharge.CUSTOMHEADER_STUN);
            target.queueBuff(new BuffStun(this.logic, (int) stunDuration + 200));
        } else {
            target.queueBuff(new BuffStun(this.logic, 200));
        }
    }

    @Override
    public void update() {
        this.y = getOwner().getY() - 180;
        this.hitbox[0].y = getOwner().getY() - 170;
        if (getOwner().getFacing() == Globals.RIGHT) {
            this.x = getOwner().getX() - 150;
            this.hitbox[0].x = getOwner().getX() - 150;
            this.pHit.values().forEach((player) -> {
                player.setPos(getOwner().getX() + 60, getOwner().getY());
                player.setXSpeed(0);
            });
        } else {
            this.x = getOwner().getX() - 250 + 150;
            this.hitbox[0].x = getOwner().getX() - 250 + 150;
            this.pHit.values().forEach((player) -> {
                player.setPos(getOwner().getX() - 60, getOwner().getY());
                player.setXSpeed(0);
            });
        }
        super.update();
    }

}
