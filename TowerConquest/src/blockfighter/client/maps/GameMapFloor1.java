package blockfighter.client.maps;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.imageio.ImageIO;

import blockfighter.client.Globals;
import blockfighter.client.entities.boss.Lightning.BossLightning;

/**
 * PvP map
 *
 * @author Ken Kwan
 */
public class GameMapFloor1 extends GameMap {

	public GameMapFloor1() {
		setMapID(1);
	}

	@Override
	public void draw(final Graphics2D g) {
		g.setColor(Color.BLACK);
		g.drawRect(0, 600, 3700, 30);

		g.drawRect(200, 350, 300, 30);

		g.drawRect(700, 100, 300, 30);

		g.drawRect(1200, -150, 300, 30);

		g.drawRect(1700, -150, 300, 30);

		g.drawRect(2200, -150, 300, 30);

		g.drawRect(2700, 100, 300, 30);

		g.drawRect(3200, 350, 300, 30);

		super.draw(g);

	}

	@Override
	public void loadAssets() throws Exception {
		this.bg = ImageIO.read(Globals.class.getResourceAsStream("sprites/maps/" + getMapID() + "/bg.png"));
		BossLightning.load();
	}

}
