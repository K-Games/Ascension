package blockfighter.client.entities.particles;

import java.awt.Point;

import blockfighter.client.entities.player.Player;
import blockfighter.client.screen.ScreenIngame;

public class ParticleShieldReflectEmitter extends Particle {

	private final Player owner;

	public ParticleShieldReflectEmitter(final int k, final Player p) {
		super(k, 0, 0);
		this.frame = 0;
		this.duration = 3000;
		this.owner = p;
	}

	@Override
	public void update() {
		super.update();
		if (this.duration > 0 && this.duration % 50 == 0) {
			final Point p = this.owner.getPos();
			if (p != null) {
				this.x = p.x;
				this.y = p.y;
			}
			for (int i = 0; i < 5; i++) {
				final ParticleShieldReflectBuff b = new ParticleShieldReflectBuff(((ScreenIngame) logic.getScreen()).getNextParticleKey(),
						this.x, this.y,
						this.facing);
				((ScreenIngame) logic.getScreen()).addParticle(b);
			}

		}
	}
}
