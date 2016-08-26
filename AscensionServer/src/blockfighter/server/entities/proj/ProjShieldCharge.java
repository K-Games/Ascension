package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.buff.BuffStun;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import java.awt.geom.Rectangle2D;

public class ProjShieldCharge extends Projectile {

    public ProjShieldCharge(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 750);
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
        double damage = owner.rollDamage() * (1.5 + .2 * owner.getSkillLevel(Skill.SHIELD_CHARGE));
        damage = (isCrit) ? owner.criticalDamage(damage) : damage;
        return (int) damage;
    }

    @Override
    public void applyDamage(Player target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit, this.hitbox[0], target.getHitbox()));
        target.queueBuff(new BuffKnockback(this.logic, 300, (owner.getFacing() == Globals.RIGHT) ? 4 : -4, -5, owner, target));
        if (owner.isSkillMaxed(Skill.SHIELD_CHARGE)) {
            target.queueBuff(new BuffStun(this.logic, 1000));
        }
    }

    @Override
    public void applyDamage(Mob target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit, this.hitbox[0], target.getHitbox()));
        if (owner.isSkillMaxed(Skill.SHIELD_CHARGE)) {
            target.queueBuff(new BuffStun(this.logic, 1000));
        }
    }

    @Override
    public void update() {
        this.y = getOwner().getY() - 180;
        this.hitbox[0].y = getOwner().getY() - 170;
        if (getOwner().getFacing() == Globals.RIGHT) {
            this.x = getOwner().getX() - 150;
            this.hitbox[0].x = getOwner().getX() - 150;
        } else {
            this.x = getOwner().getX() - 250 + 150;
            this.hitbox[0].x = getOwner().getX() - 250 + 150;
        }
        super.update();
    }

}
