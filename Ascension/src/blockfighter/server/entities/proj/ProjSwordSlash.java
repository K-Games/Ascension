package blockfighter.server.entities.proj;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.geom.Rectangle2D;

public class ProjSwordSlash extends Projectile {

    public ProjSwordSlash(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 150);
        this.screenshake = true;
        this.hitbox = new Rectangle2D.Double[1];
        this.hitbox[0] = new Rectangle2D.Double(this.x - ((o.getFacing() == Globals.RIGHT) ? 0 : 200), this.y - 200, 200, 200);
    }

    @Override
    public int calculateDamage(final boolean isCrit) {
        final Player owner = getOwner();
        double baseValue = owner.getSkill(Globals.SWORD_SLASH).getSkillData().getBaseValue();
        double multValue = owner.getSkill(Globals.SWORD_SLASH).getSkillData().getMultValue();
        double damage = owner.rollDamage() * (baseValue + multValue * owner.getSkillLevel(Globals.SWORD_SLASH));
        damage = (isCrit) ? owner.criticalDamage(damage) : damage;
        return (int) damage;
    }

}
