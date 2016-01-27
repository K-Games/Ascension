package blockfighter.client.entities.particles;

import java.awt.Point;

import blockfighter.client.entities.player.Player;
import blockfighter.client.screen.ScreenIngame;

public class ParticleBloodEmitter extends Particle {

	private final Player owner;

	public ParticleBloodEmitter(final int k, final Player p) {
		super(k, 0, 0);
		this.frame = 0;
		this.duration = 500;
		this.owner = p;
	}

	@Override
	public void update() {
		super.update();
		if (this.duration > 0 && this.duration % 15 == 0) {
			final Point p = this.owner.getPos();
			if (p != null) {
				this.x = p.x;
				this.y = p.y;
			}
			for (int i = 0; i < 15; i++) {
				final ParticleBlood b = new ParticleBlood(((ScreenIngame) logic.getScreen()).getNextParticleKey(), this.x, this.y,
						this.owner.getFacing());
				((ScreenIngame) logic.getScreen()).addParticle(b);
			}
		}
	}
}
