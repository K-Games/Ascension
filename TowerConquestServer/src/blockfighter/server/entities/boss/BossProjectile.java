package blockfighter.server.entities.boss;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.Projectile;
import java.util.Map;

/**
 *
 * @author Ken Kwan
 */
public abstract class BossProjectile extends Projectile {

    public BossProjectile(LogicModule l, int k) {
        super(l, k);
    }

    public BossProjectile(LogicModule l, int k, Boss o, double x, double y, long duration) {
        super(l, k, o, x, y, duration);
    }

    @Override
    public void update() {
        duration -= Globals.LOGIC_UPDATE / 1000000;
        if (hitbox[0] == null) {
            return;
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
}
