package blockfighter.server.entities.proj;

import blockfighter.shared.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.buff.BuffStun;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.net.PacketSender;
import java.awt.geom.Rectangle2D;

public class ProjShieldRoar extends Projectile {

    public ProjShieldRoar(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 100);
        this.screenshake = true;
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x, this.y - 300, 550, 300);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 550, this.y - 300, 550, 300);
        }
    }

    @Override
    public void applyDamage(Player target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit, this.hitbox[0], target.getHitbox()));
        target.queueBuff(new BuffKnockback(this.logic, 200, (1 - Math.abs(target.getX() - owner.getX()) / 600D) * ((owner.getFacing() == Globals.RIGHT) ? 30 : -30), 0, owner, target));
        PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_ROARHIT, target.getKey());
        if (owner.isSkillMaxed(Skill.SHIELD_ROAR)) {
            target.queueBuff(new BuffStun(this.logic, 2200));
        }
    }

    @Override
    public void applyDamage(Mob target) {
    }

    @Override
    public int calculateDamage(boolean isCrit) {
        Player owner = getOwner();
        double damage = owner.rollDamage() * (1.5 + 0.15 * owner.getSkillLevel(Skill.SHIELD_ROAR))
                + (owner.getStats()[Globals.STAT_DEFENSE] * (16 * (1.5 + 0.15 * owner.getSkillLevel(Skill.SHIELD_ROAR))));
        damage = (isCrit) ? owner.criticalDamage(damage) : damage;
        return (int) damage;
    }

}
