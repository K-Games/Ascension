package blockfighter.client.maps;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import blockfighter.client.Globals;

/**
 * PvP map
 *
 * @author Ken Kwan
 */
public class GameMapArena extends GameMap {

	BufferedImage[] platforms = new BufferedImage[3];

	public GameMapArena() {
		setMapID(0);
	}

	@Override
	public void draw(final Graphics2D g) {
		g.drawImage(this.platforms[2], 0, 600, 3700, 30, null);
		g.drawImage(this.platforms[0], 200, 350, null);
		g.drawImage(this.platforms[0], 700, 100, null);
		g.drawImage(this.platforms[1], 1200, 350, null);
		g.drawImage(this.platforms[1], 1700, 100, null);
		g.drawImage(this.platforms[0], 2200, 350, null);
		g.drawImage(this.platforms[1], 2700, 100, null);
		g.drawImage(this.platforms[0], 3200, 350, null);
		super.draw(g);

	}

	@Override
	public void loadAssets() throws Exception {
		this.bg = ImageIO.read(Globals.class.getResourceAsStream("sprites/maps/" + getMapID() + "/bg.png"));
		for (int i = 0; i < this.platforms.length; i++) {
			this.platforms[i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/maps/" + getMapID() + "/plat" + i + ".png"));
		}
	}

}
