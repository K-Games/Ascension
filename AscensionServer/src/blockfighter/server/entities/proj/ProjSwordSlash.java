package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import java.awt.geom.Rectangle2D;

public class ProjSwordSlash extends Projectile {

    public ProjSwordSlash(final LogicModule l, final Player o, final double x, final double y, final int hit) {
        super(l, o, x, y, 200);
        this.screenshake = true;
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            switch (hit) {
                case 1:
                    this.hitbox[0] = new Rectangle2D.Double(this.x - 20, this.y - 130, 250, 80);
                    break;
                case 2:
                    this.hitbox[0] = new Rectangle2D.Double(this.x - 20, this.y - 90, 250, 80);
                    break;
                case 3:
                    this.hitbox[0] = new Rectangle2D.Double(this.x - 20, this.y - 100, 270, 60);
                    break;
            }
        } else {
            switch (hit) {
                case 1:
                    this.hitbox[0] = new Rectangle2D.Double(this.x - 274 + 20, this.y - 130, 250, 80);
                    break;
                case 2:
                    this.hitbox[0] = new Rectangle2D.Double(this.x - 274 + 20, this.y - 90, 250, 80);
                    break;
                case 3:
                    this.hitbox[0] = new Rectangle2D.Double(this.x - 274 + 20, this.y - 100, 270, 60);
                    break;
            }
        }
    }

    @Override
    public int calculateDamage(final boolean isCrit) {
        final Player owner = getOwner();
        double damage = owner.rollDamage() * (1 + 0.04 * owner.getSkillLevel(Skill.SWORD_SLASH));
        damage = (isCrit) ? owner.criticalDamage(damage) : damage;
        return (int) damage;
    }

    @Override
    public void applyDamage(Player target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit, this.hitbox[0], target.getHitbox()));
    }

    @Override
    public void applyDamage(Mob target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit, this.hitbox[0], target.getHitbox()));
    }
}
