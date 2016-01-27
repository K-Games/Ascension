package blockfighter.client.entities.particles;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import blockfighter.client.Globals;

public class ParticleShieldChargeParticle extends Particle {

	public ParticleShieldChargeParticle(final int k, final int x, final int y, final byte f) {
		super(k, x, y, f);
		this.frame = 0;
		this.frameDuration = 50;
		this.duration = 400;
		this.y += Globals.rng(11) * 15;
		if (this.facing == Globals.RIGHT) {
			this.x += 428 - 179;
		}
	}

	@Override
	public void update() {
		super.update();
		this.frameDuration -= Globals.LOGIC_UPDATE / 1000000;
		if (this.frameDuration <= 0) {
			this.frameDuration = 50;
			if (this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGEPARTICLE].length) {
				this.frame++;
			}
		}
	}

	@Override
	public void draw(final Graphics2D g) {
		if (PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGEPARTICLE] == null) {
			return;
		}
		if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGEPARTICLE].length) {
			return;
		}
		final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGEPARTICLE][this.frame];
		final int drawSrcX = this.x;
		final int drawSrcY = this.y;
		final int drawDscY = drawSrcY + sprite.getHeight();
		final int drawDscX = this.x + sprite.getWidth();
		g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
	}
}
