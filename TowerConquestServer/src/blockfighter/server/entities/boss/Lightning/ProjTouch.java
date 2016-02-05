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

    private long lastTouchDamage = 0;

    /**
     * Projectile of on boss contact damage
     *
     * @param l Room/Logic Module
     * @param k Projectile Key
     * @param o Owning player
     */
    public ProjTouch(final LogicModule l, final int k, final Boss o) {
        super(l, k, o);
        this.hitbox = new Rectangle2D.Double[1];
        this.hitbox[0] = o.getHitbox();
    }

    @Override
    public void update() {
        if (this.hitbox[0] == null) {
            return;
        }

        int sinceLastDamage = Globals.nsToMs(this.logic.getTime() - this.lastTouchDamage);
        if (sinceLastDamage >= 500) {
            this.pHit.clear();
            this.lastTouchDamage = this.logic.getTime();
        }

        for (final Map.Entry<Byte, Player> pEntry : this.logic.getPlayers().entrySet()) {
            final Player p = pEntry.getValue();
            if (p != getOwner() && !this.pHit.contains(p) && !p.isInvulnerable() && p.intersectHitbox(this.hitbox[0])) {
                this.playerQueue.add(p);
                this.pHit.add(p);
                queueEffect(this);
            }
        }
    }

    @Override
    public void processQueue() {
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll();
            if (p != null && !p.isDead()) {
                final int damage = (int) (70 * Math.pow(getBossOwner().getStats()[Boss.STAT_LEVEL], 1.7));
                p.queueDamage(new Damage(damage, false, getBossOwner(), p, this.hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(this.logic, 100, (p.getFacing() == Globals.RIGHT) ? -5 : 5, -6, getBossOwner(), p));
            }
        }
        this.queuedEffect = false;
    }

    @Override
    public boolean isExpired() {
        return getBossOwner().isDead();
    }
}
