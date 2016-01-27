package blockfighter.server.entities.boss;

import java.util.Map;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.Projectile;

/**
 *
 * @author Ken Kwan
 */
public abstract class BossProjectile extends Projectile {

	public BossProjectile(final LogicModule l, final int k) {
		super(l, k);
	}

	public BossProjectile(final LogicModule l, final int k, final Boss o, final double x, final double y, final long duration) {
		super(l, k, o, x, y, duration);
	}

	@Override
	public void update() {
		this.duration -= Globals.LOGIC_UPDATE / 1000000;
		if (this.hitbox[0] == null) {
			return;
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
}
