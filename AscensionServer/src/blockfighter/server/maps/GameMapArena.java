package blockfighter.server.maps;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class GameMapArena extends GameMap {

    public GameMapArena() {
        this.mapID = 0;
        this.isPvP = true;
        this.platforms = new GameMapPlatform[15];
        this.platforms[0] = new GameMapPlatform(new Rectangle2D.Double(-50, 600, 3450, 30));

        this.platforms[1] = new GameMapPlatform(new Rectangle2D.Double(650, 350, 300, 30));
        this.platforms[2] = new GameMapPlatform(new Rectangle2D.Double(1550, 350, 300, 30));
        this.platforms[3] = new GameMapPlatform(new Rectangle2D.Double(2450, 350, 300, 30));

        this.platforms[4] = new GameMapPlatform(new Rectangle2D.Double(200, 100, 300, 30));
        this.platforms[5] = new GameMapPlatform(new Rectangle2D.Double(1100, 100, 300, 30));
        this.platforms[6] = new GameMapPlatform(new Rectangle2D.Double(2000, 100, 300, 30));
        this.platforms[7] = new GameMapPlatform(new Rectangle2D.Double(2900, 100, 300, 30));

        this.platforms[8] = new GameMapPlatform(new Rectangle2D.Double(650, -150, 300, 30));
        this.platforms[9] = new GameMapPlatform(new Rectangle2D.Double(1550, -150, 300, 30));
        this.platforms[10] = new GameMapPlatform(new Rectangle2D.Double(2450, -150, 300, 30));

        this.platforms[11] = new GameMapPlatform(new Rectangle2D.Double(200, -400, 300, 30));
        this.platforms[12] = new GameMapPlatform(new Rectangle2D.Double(1100, -400, 300, 30));
        this.platforms[13] = new GameMapPlatform(new Rectangle2D.Double(2000, -400, 300, 30));
        this.platforms[14] = new GameMapPlatform(new Rectangle2D.Double(2900, -400, 300, 30));

        this.spawnPoints = new Point2D.Double[18];
        for (int i = 0; i < 4; i++) {
            this.spawnPoints[i + 14] = new Point2D.Double(this.platforms[0].getRect().width / 6D * (i + 1), this.platforms[0].getRect().getY() - 150);
        }
        for (int i = 1; i < 15; i++) {
            this.spawnPoints[i - 1] = new Point2D.Double(this.platforms[i].getRect().getCenterX(), this.platforms[i].getRect().getY() - 150);
        }
        this.boundary[Globals.MAP_LEFT] = 0.0;
        this.boundary[Globals.MAP_RIGHT] = 3400.0;
        this.boundary[Globals.MAP_TOP] = -1300;
        this.boundary[Globals.MAP_BOTTOM] = 700;
        setupMap();
    }

    @Override
    public void spawnMapMobs(final LogicModule l) {
    }
}
