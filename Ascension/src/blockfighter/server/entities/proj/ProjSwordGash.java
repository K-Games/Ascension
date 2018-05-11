package blockfighter.server.entities.proj;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.sword.SkillSwordGash;
import blockfighter.shared.Globals;
import java.awt.geom.Rectangle2D;

public class ProjSwordGash extends Projectile {

    private boolean healed = false;

    public ProjSwordGash(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 50);
        this.screenshake = true;
        this.hitbox = new Rectangle2D.Double[1];
        double rectX = (o.getFacing() == Globals.RIGHT) ? this.x : this.x - 200;
        this.hitbox[0] = new Rectangle2D.Double(rectX, this.y - 150, 200, 150);
    }

    @Override
    public int calculateDamage(final boolean isCrit) {
        final Player owner = getOwner();
        double baseValue = owner.getSkill(Globals.SWORD_GASH).getBaseValue();
        double multValue = owner.getSkill(Globals.SWORD_GASH).getMultValue();
        double damage = owner.rollDamage() * (baseValue + multValue * owner.getSkillLevel(Globals.SWORD_GASH));
        damage = (isCrit) ? owner.criticalDamage(damage) : damage;
        return (int) damage;
    }

    @Override
    public void applyDamage(Player target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit, true));
        if (!this.healed && owner.isSkillMaxed(Globals.SWORD_GASH)) {
            final double heal = owner.getStats()[Globals.STAT_MAXHP] * owner.getSkill(Globals.SWORD_GASH).getCustomValue(SkillSwordGash.CUSTOM_DATA_HEADERS[0]);
            owner.queueHeal((int) heal);
            this.healed = true;
        }
    }

}
