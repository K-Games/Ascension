package blockfighter.server.maps;

import blockfighter.server.LogicModule;
import blockfighter.shared.Globals;
import blockfighter.shared.Globals.GameMaps;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class GameMapDebug extends GameMap {

    public GameMapDebug() {
        super(GameMaps.DEBUG);
        this.isPvP = true;

        this.platforms = new GameMapPlatform[1];
        this.spawnPoints = new Point2D.Double[1];

        //base
        this.platforms[0] = new GameMapPlatform(new Rectangle2D.Double(-50, 800, 1100, 15));

        this.spawnPoints[0] = new Point2D.Double(this.platforms[0].getRect().getCenterX(), this.platforms[0].getRect().getY() - 70);

        this.boundary[Globals.MAP_LEFT] = 0;
        this.boundary[Globals.MAP_RIGHT] = 1050;
        this.boundary[Globals.MAP_TOP] = -500;
        this.boundary[Globals.MAP_BOTTOM] = 1000D;
        setupMap();
    }

    @Override
    public void spawnMapMobs(final LogicModule l) {
    }
}
