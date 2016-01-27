package blockfighter.client.entities.particles;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import blockfighter.client.Globals;
import blockfighter.client.screen.ScreenIngame;

public class ParticleSwordCinder extends Particle {

	public ParticleSwordCinder(final int k, final int x, final int y, final byte f) {
		super(k, x, y, f);
		this.frame = 0;
		this.frameDuration = 50;
		this.duration = 400;
	}

	@Override
	public void update() {
		super.update();
		this.frameDuration -= Globals.LOGIC_UPDATE / 1000000;
		if (this.duration > 100) {
			for (int i = 0; i < 2; i++) {
				final ParticleSwordCinderParticle b = new ParticleSwordCinderParticle(
						((ScreenIngame) logic.getScreen()).getNextParticleKey(), this.x,
						this.y, this.facing);
				((ScreenIngame) logic.getScreen()).addParticle(b);
			}
		}
		if (this.frameDuration <= 0) {
			this.frameDuration = 50;
			if (this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_CINDER].length - 1) {
				this.frame++;
			}
		}
	}

	@Override
	public void draw(final Graphics2D g) {
		if (PARTICLE_SPRITE[Globals.PARTICLE_SWORD_CINDER] == null) {
			return;
		}
		if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SWORD_CINDER].length) {
			return;
		}
		final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SWORD_CINDER][this.frame];
		final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? -60 : sprite.getWidth() - 45);
		final int drawSrcY = this.y - 30;
		final int drawDscY = drawSrcY + sprite.getHeight();
		final int drawDscX = this.x + ((this.facing == Globals.RIGHT) ? sprite.getWidth() - 60 : -45);
		g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
	}
}
