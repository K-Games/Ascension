package blockfighter.client.entities.particles;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import blockfighter.client.Globals;

public class ParticleBowPowerParticle extends Particle {

	public ParticleBowPowerParticle(final int k, final int x, final int y, final byte f) {
		super(k, x, y, f);
		if (this.facing == Globals.RIGHT) {
			this.x += Globals.rng(450);
		} else {
			this.x -= Globals.rng(450) - 25;
		}
		this.y += Globals.rng(80) + 25;
		this.frame = 0;
		this.frameDuration = 25;
		this.duration = 250;
	}

	@Override
	public void update() {
		super.update();
		this.frameDuration -= Globals.LOGIC_UPDATE / 1000000;
		this.x += (this.facing == Globals.RIGHT) ? 20 : -20;
		if (this.frameDuration <= 0) {
			this.frameDuration = 25;
			if (this.frame < PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERPARTICLE].length - 1) {
				this.frame++;
			}
		}
	}

	@Override
	public void draw(final Graphics2D g) {
		if (PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERPARTICLE] == null) {
			return;
		}
		if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERPARTICLE].length) {
			return;
		}
		final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_BOW_POWERPARTICLE][this.frame];
		g.drawImage(sprite, this.x, this.y, null);
	}
}
