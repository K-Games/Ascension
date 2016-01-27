package blockfighter.server.maps;

import java.awt.geom.Rectangle2D;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.boss.Lightning.BossLightning;

public class GameMapFloor1 extends GameMap {

	public GameMapFloor1() {
		this.mapID = 1;
		this.isPvP = false;
		this.platforms = new Rectangle2D.Double[8];
		this.platforms[0] = new Rectangle2D.Double(-50, 600, 3750, 30);
		this.platforms[1] = new Rectangle2D.Double(200, 350, 300, 30);
		this.platforms[2] = new Rectangle2D.Double(700, 100, 300, 30);
		this.platforms[3] = new Rectangle2D.Double(1200, -150, 300, 30);
		this.platforms[4] = new Rectangle2D.Double(1700, -150, 300, 30);
		this.platforms[5] = new Rectangle2D.Double(2200, -150, 300, 30);
		this.platforms[6] = new Rectangle2D.Double(2700, 100, 300, 30);
		this.platforms[7] = new Rectangle2D.Double(3200, 350, 300, 30);
		this.boundary[Globals.MAP_LEFT] = 0.0;
		this.boundary[Globals.MAP_RIGHT] = 3700.0;
	}

	@Override
	public Boss[] getBosses(final LogicModule l) {
		final Boss[] bosses = new Boss[1];
		bosses[0] = new BossLightning(l, (byte) 0, this, 1500, 500);
		return bosses;
	}
}
