package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import java.awt.geom.Rectangle2D;

public class ProjBowStorm extends Projectile {

    private long lastDamageTime;

    public ProjBowStorm(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 5000);
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
        double damage = owner.rollDamage() * 0.6 + (.06 * owner.getSkillLevel(Skill.BOW_STORM));
        if (isCrit) {
            damage = owner.isSkillMaxed(Skill.BOW_STORM) ? owner.criticalDamage(damage, 5) : owner.criticalDamage(damage);
        }
        return (int) damage;
    }

    @Override
    public void applyDamage(Player target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit, this.hitbox[0], target.getHitbox()));
    }

    @Override
    public void applyDamage(Mob target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        int damage = calculateDamage(isCrit);
        target.queueDamage(new Damage(damage, true, owner, target, isCrit, this.hitbox[0], target.getHitbox()));
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(this.logic.getTime() - lastDamageTime) >= 200) {
            lastDamageTime = this.logic.getTime();
            this.pHit.clear();
            this.bHit.clear();
        }
    }
}
