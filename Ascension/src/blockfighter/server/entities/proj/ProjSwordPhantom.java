package blockfighter.server.entities.proj;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.geom.Rectangle2D;

public class ProjSwordPhantom extends Projectile {

    public ProjSwordPhantom(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 200);
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x, this.y - 175, 200, 155);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 200, this.y - 175, 200, 155);
        }
    }

    @Override
    public int calculateDamage(final boolean isCrit) {
        final Player owner = getOwner();
        double baseValue = owner.getSkill(Globals.SWORD_PHANTOM).getBaseValue();
        double multValue = owner.getSkill(Globals.SWORD_PHANTOM).getMultValue();
        double damage = owner.rollDamage() * (baseValue + multValue * owner.getSkillLevel(Globals.SWORD_PHANTOM));
        damage = (isCrit) ? owner.criticalDamage(damage) : damage;
        return (int) damage;
    }

    @Override
    public void update() {
        if (Globals.nsToMs(logic.getTime() - this.projStartTime) >= 100) {
            super.update();
        }
    }
}
