package blockfighter.server.entities.proj;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.buff.BuffTauntCripple;
import blockfighter.server.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.geom.Rectangle2D;

public class ProjSwordTaunt extends Projectile {

    public ProjSwordTaunt(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 150);
        this.screenshake = true;
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x, this.y - 240, 210, 240);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 210, this.y - 240, 210, 240);
        }
    }

    @Override
    public int calculateDamage(final boolean isCrit) {
        final Player owner = getOwner();
        double baseValue = owner.getSkill(Globals.SWORD_TAUNT).getSkillData().getBaseValue();
        double multValue = owner.getSkill(Globals.SWORD_TAUNT).getSkillData().getMultValue();
        double damage = owner.rollDamage() * (baseValue + multValue * owner.getSkillLevel(Globals.SWORD_TAUNT));
        damage = (isCrit) ? owner.criticalDamage(damage) : damage;
        return (int) damage;
    }

    @Override
    public void applyDamage(Player target) {
        super.applyDamage(target);
        final Player owner = getOwner();
        if (!owner.hasSkill(Globals.SWORD_TAUNT_CRIPPLE)) {
            target.queueBuff(new BuffKnockback(this.logic, 300, (owner.getFacing() == Globals.RIGHT) ? 3 : -3, 6, owner, target));
        } else {
            target.queueBuff(new BuffKnockback(this.logic, 300, (owner.getFacing() == Globals.RIGHT) ? 1.5 : -1.5, 6, owner, target));
            target.queueBuff(new BuffTauntCripple(logic, owner.getSkill(Globals.SWORD_TAUNT_CRIPPLE).getCustomValue(0).intValue(), owner, owner));
        }
    }

}
