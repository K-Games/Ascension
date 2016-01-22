package blockfighter.server.entities.boss.Lightning;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.boss.BossProjectile;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;
import java.awt.geom.Rectangle2D;
import java.util.Map;

/**
 *
 * @author Ken Kwan
 */
public class ProjTouch extends BossProjectile {

    private long touchDamageTime = 0;

    /**
     * Projectile of on boss contact damage
     *
     * @param l Room/Logic Module
     * @param k Projectile Key
     * @param o Owning player
     */
    public ProjTouch(LogicModule l, int k, Boss o) {
        super(l, k);
        setBossOwner(o);
        hitbox = new Rectangle2D.Double[1];
        hitbox[0] = getBossOwner().getHitbox();
    }

    @Override
    public void update() {
        if (hitbox[0] == null) {
            return;
        }

        touchDamageTime -= Globals.LOGIC_UPDATE / 1000000;
        if (touchDamageTime <= 0) {
            pHit.clear();
            touchDamageTime = 500;
        }

        for (Map.Entry<Byte, Player> pEntry : logic.getPlayers().entrySet()) {
            Player p = pEntry.getValue();
            if (p != getOwner() && !pHit.contains(p) && !p.isInvulnerable() && p.intersectHitbox(hitbox[0])) {
                playerQueue.add(p);
                pHit.add(p);
                queueEffect(this);
            }
        }
    }

    @Override
    public void processQueue() {
        while (!playerQueue.isEmpty()) {
            Player p = playerQueue.poll();
            if (p != null && !p.isDead()) {
                int damage = (int) (70 * Math.pow(getBossOwner().getStats()[Boss.STAT_LEVEL], 1.7));
                p.queueDamage(new Damage(damage, false, getBossOwner(), p, hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(100, (getBossOwner().getFacing() == Globals.RIGHT) ? 5 : -5, -6, getBossOwner(), p));
            }
        }
        queuedEffect = false;
    }

    @Override
    public boolean isExpired() {
        return getBossOwner().isDead();
    }
}
