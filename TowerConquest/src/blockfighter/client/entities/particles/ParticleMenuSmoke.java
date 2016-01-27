package blockfighter.client.entities.particles;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import blockfighter.client.Globals;

/**
 *
 * @author Ken Kwan
 */
public class ParticleMenuSmoke extends Particle {

	public ParticleMenuSmoke(final int k, final int x, final int y) {
		super(k, x, y);
	}

	@Override
	public void update() {
		this.x -= 2;
		if (this.x <= -1280) {
			this.x = 1280;
		}
	}

	@Override
	public void draw(final Graphics2D g) {
		final BufferedImage sprite = Globals.MENU_SMOKE[0];
		if (this.key == 0) {
			g.drawImage(sprite, this.x, this.y, 1280, 720, null);
		} else {
			g.drawImage(sprite, this.x + 1280, this.y, -1280, 720, null);
		}
	}

	@Override
	public boolean isExpired() {
		return false;
	}
}
