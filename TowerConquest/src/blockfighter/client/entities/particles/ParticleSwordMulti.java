package blockfighter.client.entities.particles;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import blockfighter.client.Globals;

public class ParticleSwordMulti extends Particle {

	public ParticleSwordMulti(final int k, final int x, final int y, final byte f) {
		super(k, x, y, f);
		this.frame = 0;
		this.frameDuration = 25;
		this.duration = 600;
	}

	@Override
	public void update() {
		super.update();
		this.frameDuration -= Globals.LOGIC_UPDATE / 1000000;
		if (this.frameDuration <= 0) {
			this.frameDuration = 25;
			if (this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_MULTI].length) {
				this.frame++;
			}
		}
	}

	@Override
	public void draw(final Graphics2D g) {

		if (PARTICLE_SPRITE[Globals.PARTICLE_SWORD_MULTI] == null) {
			return;
		}
		if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SWORD_MULTI].length) {
			return;
		}
		final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SWORD_MULTI][this.frame];
		final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? -100 : sprite.getWidth() - 40);
		final int drawSrcY = this.y - 60;
		final int drawDscY = drawSrcY + sprite.getHeight();
		final int drawDscX = this.x + ((this.facing == Globals.RIGHT) ? sprite.getWidth() - 100 : -40);
		g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
	}
}
