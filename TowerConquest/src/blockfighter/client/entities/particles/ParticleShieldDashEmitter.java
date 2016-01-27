package blockfighter.client.entities.particles;

import java.awt.Point;

import blockfighter.client.Globals;
import blockfighter.client.entities.player.Player;
import blockfighter.client.screen.ScreenIngame;

public class ParticleShieldDashEmitter extends Particle {

	private final Player owner;

	public ParticleShieldDashEmitter(final int k, final byte f, final Player p) {
		super(k, 0, 0, f);
		this.frame = 0;
		this.duration = 250;
		this.owner = p;
	}

	@Override
	public void update() {
		super.update();
		if (this.duration > 0) {
			final Point p = this.owner.getPos();
			if (p != null) {
				this.x = p.x;
				this.y = p.y;
			}
			final ParticleShieldDash b = new ParticleShieldDash(((ScreenIngame) logic.getScreen()).getNextParticleKey(),
					this.x + ((this.facing == Globals.RIGHT) ? -172 : -200), this.y - 330, this.facing);
			((ScreenIngame) logic.getScreen()).addParticle(b);
		}
	}
}
