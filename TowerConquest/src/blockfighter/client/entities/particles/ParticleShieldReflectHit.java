package blockfighter.client.entities.particles;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import blockfighter.client.Globals;

public class ParticleShieldReflectHit extends Particle {

	public ParticleShieldReflectHit(final int k, final int x, final int y) {
		super(k, x, y);
		this.frame = 0;
		this.frameDuration = 25;
		this.duration = 400;
	}

	@Override
	public void update() {
		super.update();
		this.frameDuration -= Globals.LOGIC_UPDATE / 1000000;
		if (this.frameDuration <= 0) {
			this.frameDuration = 25;
			if (this.frame < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTHIT].length - 1) {
				this.frame++;
			}
		}
	}

	@Override
	public void draw(final Graphics2D g) {
		if (PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTHIT] == null) {
			return;
		}
		if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTHIT].length) {
			return;
		}
		final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_REFLECTHIT][this.frame];
		final int drawSrcX = this.x - sprite.getWidth() / 2;
		final int drawSrcY = this.y - 450;
		final int drawDscY = drawSrcY + sprite.getHeight();
		final int drawDscX = drawSrcX + sprite.getWidth();
		g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
	}
}
