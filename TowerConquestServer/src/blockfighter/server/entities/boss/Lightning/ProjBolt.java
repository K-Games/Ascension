package blockfighter.server.entities.boss.Lightning;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.boss.BossProjectile;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;
import java.awt.geom.Rectangle2D;

/**
 * This is the base projectile class. Create projectile classes off this.
 *
 * @author Ken Kwan
 */
public class ProjBolt extends BossProjectile {

    /**
     * Projectile of Sword Skill Defensive Impact.
     *
     * @param l Room/Logic Module
     * @param k Projectile Key
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     */
    public ProjBolt(LogicModule l, int k, Boss o, double x, double y) {
        super(l, k);
        setBossOwner(o);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        hitbox[0] = new Rectangle2D.Double(x - 150, y - 1100, 300, 1200);
        duration = 200;
    }

    @Override
    public void processQueue() {
        while (!playerQueue.isEmpty()) {
            Player p = playerQueue.poll();
            if (p != null && !p.isDead()) {
                int damage = (int) (250 *Math.pow(getBossOwner().getStats()[Boss.STAT_LEVEL],1.7));
                p.queueDamage(new Damage(damage, false, getBossOwner(), p, hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(300, (getBossOwner().getFacing() == Globals.RIGHT) ? 5 : -5, -8, getBossOwner(), p));
            }
        }
        queuedEffect = false;
    }

}
