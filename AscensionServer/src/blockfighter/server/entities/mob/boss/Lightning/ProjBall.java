package blockfighter.server.entities.mob.boss.Lightning;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.mob.MobProjectile;
import blockfighter.server.entities.player.Player;
import java.awt.geom.Rectangle2D;

public class ProjBall extends MobProjectile {

    public ProjBall(final LogicModule l, final Mob o, final double x, final double y) {
        super(l, o, x, y, 200);
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(x + 200, y - 200, 2000, 200);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(x - 2000 - 200, y - 200, 2000, 200);
        }
    }

    @Override
    public void applyEffect() {
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll();
            if (p != null && !p.isDead()) {
                final int damage = (int) (400 * Math.pow(getMobOwner().getStats()[Mob.STAT_LEVEL], 1.7));
                p.queueDamage(new Damage(damage, false, getMobOwner(), p, this.hitbox[0], p.getHitbox()));

                p.queueBuff(new BuffKnockback(this.room, 300, (getMobOwner().getFacing() == Globals.RIGHT) ? 5 : -5, -8, getMobOwner(), p));
            }
        }
        this.queuedEffect = false;
    }

}
