package blockfighter.server.maps;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.boss.Boss;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class GameMapArena extends GameMap {

    public GameMapArena() {
        this.mapID = 0;
        this.isPvP = true;
        this.platforms = new Rectangle2D.Double[15];
        this.platforms[0] = new Rectangle2D.Double(-50, 600, 3750, 30);

        this.platforms[1] = new Rectangle2D.Double(700, 350, 300, 30);
        this.platforms[2] = new Rectangle2D.Double(1700, 350, 300, 30);
        this.platforms[3] = new Rectangle2D.Double(2700, 350, 300, 30);

        this.platforms[4] = new Rectangle2D.Double(200, 100, 300, 30);
        this.platforms[5] = new Rectangle2D.Double(1200, 100, 300, 30);
        this.platforms[6] = new Rectangle2D.Double(2200, 100, 300, 30);
        this.platforms[7] = new Rectangle2D.Double(3200, 100, 300, 30);

        this.platforms[8] = new Rectangle2D.Double(700, -150, 300, 30);
        this.platforms[9] = new Rectangle2D.Double(1700, -150, 300, 30);
        this.platforms[10] = new Rectangle2D.Double(2700, -150, 300, 30);

        this.platforms[11] = new Rectangle2D.Double(3200, -400, 300, 30);
        this.platforms[12] = new Rectangle2D.Double(2200, -400, 300, 30);
        this.platforms[13] = new Rectangle2D.Double(1200, -400, 300, 30);
        this.platforms[14] = new Rectangle2D.Double(200, -400, 300, 30);

        this.spawnPoints = new Point2D.Double[18];
        this.spawnPoints[0] = new Point2D.Double(350, 450);
        this.spawnPoints[1] = new Point2D.Double(1350, 450);
        this.spawnPoints[2] = new Point2D.Double(2350, 450);
        this.spawnPoints[3] = new Point2D.Double(3350, 450);

        this.spawnPoints[4] = new Point2D.Double(850, 200);
        this.spawnPoints[5] = new Point2D.Double(1850, 200);
        this.spawnPoints[6] = new Point2D.Double(2850, 200);

        this.spawnPoints[7] = new Point2D.Double(350, -50);
        this.spawnPoints[8] = new Point2D.Double(1350, -50);
        this.spawnPoints[9] = new Point2D.Double(2350, -50);
        this.spawnPoints[10] = new Point2D.Double(3350, -50);

        this.spawnPoints[11] = new Point2D.Double(850, -300);
        this.spawnPoints[12] = new Point2D.Double(1850, -300);
        this.spawnPoints[13] = new Point2D.Double(2850, -300);

        this.spawnPoints[14] = new Point2D.Double(350, -550);
        this.spawnPoints[15] = new Point2D.Double(1350, -550);
        this.spawnPoints[16] = new Point2D.Double(2350, -550);
        this.spawnPoints[17] = new Point2D.Double(3350, -550);

        this.boundary[Globals.MAP_LEFT] = 0.0;
        this.boundary[Globals.MAP_RIGHT] = 3700.0;
    }

    @Override
    public Boss[] getBosses(final LogicModule l) {
        return null;
    }
}
