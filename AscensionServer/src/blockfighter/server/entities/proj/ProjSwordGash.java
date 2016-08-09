package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import java.awt.geom.Rectangle2D;

public class ProjSwordGash extends Projectile {

    private boolean healed = false;

    public ProjSwordGash(final LogicModule l, final Player o, final double x, final double y, final byte hit) {
        super(l, o, x, y, 50);
        this.screenshake = true;
        this.hitbox = new Rectangle2D.Double[1];

        double rectX;
        switch (hit) {
            case 1:
            case 4:
                rectX = (o.getFacing() == Globals.RIGHT) ? this.x - 50 : this.x - 250 + 50;
                this.hitbox[0] = new Rectangle2D.Double(rectX, this.y - 75, 250, 76);
                break;
            case 2:
            case 3:
                rectX = (o.getFacing() == Globals.RIGHT) ? this.x - 50 : this.x - 252 + 50;
                this.hitbox[0] = new Rectangle2D.Double(rectX, this.y - 153, 246, 153);
                break;
        }
    }

    @Override
    public void applyEffect() {
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (.75 + 0.03 * owner.getSkillLevel(Skill.SWORD_GASH)));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, this.hitbox[0], p.getHitbox()));
                //p.queueBuff(new BuffKnockback(this.logic, 10, (owner.getFacing() == Globals.RIGHT) ? 3 : -3, -0.5, owner, p));

                if (!this.healed && owner.isSkillMaxed(Skill.SWORD_GASH)) {
                    final double heal = owner.getStats()[Globals.STAT_MAXHP] * 0.0025;
                    owner.queueHeal((int) heal);
                    this.healed = true;
                }
            }
        }
        while (!this.mobQueue.isEmpty()) {
            final Mob b = this.mobQueue.poll();
            final Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (.75 + 0.03 * owner.getSkillLevel(Skill.SWORD_GASH)));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, this.hitbox[0], b.getHitbox()));

                if (!this.healed && owner.isSkillMaxed(Skill.SWORD_GASH)) {
                    final double heal = owner.getStats()[Globals.STAT_MAXHP] * 0.0025;
                    owner.queueHeal((int) heal);
                    this.healed = true;
                }
            }
        }
        this.queuedEffect = false;
    }

}
