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
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 150, this.y - 170, 250, 176);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 253 + 150, this.y - 170, 250, 176);
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
            this.x = getOwner().getX() - 253 + 150;
            this.hitbox[0].x = getOwner().getX() - 253 + 150;
        }
        super.update();
    }

    @Override
    public void processQueue() {
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (1.5 + .2 * owner.getSkillLevel(Skill.SHIELD_CHARGE)));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, this.hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(this.room, 300, (owner.getFacing() == Globals.RIGHT) ? 4 : -4, -5, owner, p));
                if (owner.isSkillMaxed(Skill.SHIELD_CHARGE)) {
                    p.queueBuff(new BuffStun(this.room, 1000));
                }
            }
        }
        while (!this.mobQueue.isEmpty()) {
            final Mob b = this.mobQueue.poll();
            final Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (1.5 + .2 * owner.getSkillLevel(Skill.SHIELD_CHARGE)));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, this.hitbox[0], b.getHitbox()));
                if (owner.isSkillMaxed(Skill.SHIELD_CHARGE)) {
                    b.queueBuff(new BuffStun(this.room, 1000));
                }
            }
        }
        this.queuedEffect = false;
    }

}
