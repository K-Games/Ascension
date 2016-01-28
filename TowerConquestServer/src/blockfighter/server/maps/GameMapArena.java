package blockfighter.server.maps;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.boss.Boss;
import java.awt.geom.Rectangle2D;

public class GameMapArena extends GameMap {

    public GameMapArena() {
        this.mapID = 0;
        this.isPvP = true;
        this.platforms = new Rectangle2D.Double[8];
        this.platforms[0] = new Rectangle2D.Double(-50, 600, 3750, 30);

        this.platforms[1] = new Rectangle2D.Double(200, 350, 300, 30);

        this.platforms[2] = new Rectangle2D.Double(700, 100, 300, 30);

        this.platforms[3] = new Rectangle2D.Double(1200, 350, 300, 30);

        this.platforms[4] = new Rectangle2D.Double(1700, 100, 300, 30);

        this.platforms[5] = new Rectangle2D.Double(2200, 350, 300, 30);

        this.platforms[6] = new Rectangle2D.Double(2700, 100, 300, 30);

        this.platforms[7] = new Rectangle2D.Double(3200, 350, 300, 30);
        this.boundary[Globals.MAP_LEFT] = 0.0;
        this.boundary[Globals.MAP_RIGHT] = 3700.0;
    }

    @Override
    public Boss[] getBosses(final LogicModule l) {
        return null;
    }
}
