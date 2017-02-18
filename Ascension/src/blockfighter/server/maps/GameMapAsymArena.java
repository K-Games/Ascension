package blockfighter.server.maps;

import blockfighter.server.LogicModule;
import blockfighter.shared.Globals;
import blockfighter.shared.Globals.GameMaps;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class GameMapAsymArena extends GameMap {

    public GameMapAsymArena() {
        super(GameMaps.ASYM_ARENA);
        this.isPvP = true;

        this.platforms = new GameMapPlatform[16];
        this.spawnPoints = new Point2D.Double[13];

        //base
        this.platforms[0] = new GameMapPlatform(new Rectangle2D.Double(-50, 2400, 4050, 15));

        //ladder base
        this.platforms[1] = new GameMapPlatform(new Rectangle2D.Double(-50, 1504, 768, 896));
        this.platforms[1].setIsSolid(true);
        this.spawnPoints[0] = new Point2D.Double(this.platforms[1].getRect().getCenterX(), this.platforms[1].getRect().getY() - 70);

        // ladder
        this.platforms[2] = new GameMapPlatform(new Rectangle2D.Double(-42, 1250, 192, 15));
        this.platforms[3] = new GameMapPlatform(new Rectangle2D.Double(-42, 1000, 192, 15));

        //fireplace air
        this.platforms[4] = new GameMapPlatform(new Rectangle2D.Double(900, 1700, 512, 15));
        this.spawnPoints[1] = new Point2D.Double(900, this.platforms[0].getRect().getY() - 70);

        // mid base
        this.platforms[5] = new GameMapPlatform(new Rectangle2D.Double(1600, 1888, 448, 512));
        this.platforms[5].setIsSolid(true);
        this.spawnPoints[2] = new Point2D.Double(this.platforms[5].getRect().getCenterX(), this.platforms[5].getRect().getY() - 70);

        // mid base left ext
        this.platforms[6] = new GameMapPlatform(new Rectangle2D.Double(1536, 2176, 64, 15));

        // candles air
        this.platforms[7] = new GameMapPlatform(new Rectangle2D.Double(2250, 2100, 704, 15));
        this.spawnPoints[3] = new Point2D.Double(2600, this.platforms[0].getRect().getY() - 70);
        this.spawnPoints[12] = new Point2D.Double(2800, this.platforms[0].getRect().getY() - 70);

        //shelves base
        this.platforms[8] = new GameMapPlatform(new Rectangle2D.Double(3200, 1120, 640, 1280));
        this.platforms[8].setIsSolid(true);
        this.spawnPoints[4] = new Point2D.Double(this.platforms[8].getRect().getCenterX() - 150, this.platforms[8].getRect().getY() - 70);
        this.spawnPoints[5] = new Point2D.Double(this.platforms[8].getRect().getCenterX() + 150, this.platforms[8].getRect().getY() - 70);

        // shelves mid ext
        this.platforms[9] = new GameMapPlatform(new Rectangle2D.Double(2816, 1820, 384, 15));
        // shelves high ext
        this.platforms[10] = new GameMapPlatform(new Rectangle2D.Double(2624, 1270, 576, 15));

        // mid
        this.platforms[11] = new GameMapPlatform(new Rectangle2D.Double(1604, 1550, 1216, 15));

        // mid high
        this.platforms[12] = new GameMapPlatform(new Rectangle2D.Double(1526, 1000, 1024, 15));

        // pillars
        this.platforms[13] = new GameMapPlatform(new Rectangle2D.Double(-36, 750, 1536, 15));
        this.spawnPoints[6] = new Point2D.Double(this.platforms[13].getRect().getMinX() + 350, this.platforms[13].getRect().getY() - 70);
        this.spawnPoints[7] = new Point2D.Double(this.platforms[13].getRect().getMinX() + 700, this.platforms[13].getRect().getY() - 70);
        this.spawnPoints[8] = new Point2D.Double(this.platforms[13].getRect().getMinX() + 1050, this.platforms[13].getRect().getY() - 70);

        // top mid
        this.platforms[14] = new GameMapPlatform(new Rectangle2D.Double(1574, 500, 576, 15));

        // top high
        this.platforms[15] = new GameMapPlatform(new Rectangle2D.Double(2314, 250, 1536, 15));
        this.spawnPoints[9] = new Point2D.Double(this.platforms[15].getRect().getMaxX() - 350, this.platforms[15].getRect().getY() - 70);
        this.spawnPoints[10] = new Point2D.Double(this.platforms[15].getRect().getMaxX() - 700, this.platforms[15].getRect().getY() - 70);
        this.spawnPoints[11] = new Point2D.Double(this.platforms[15].getRect().getMaxX() - 1050, this.platforms[15].getRect().getY() - 70);

        this.boundary[Globals.MAP_LEFT] = 0;
        this.boundary[Globals.MAP_RIGHT] = 3800D;
        this.boundary[Globals.MAP_TOP] = -500;
        this.boundary[Globals.MAP_BOTTOM] = 2500D;
        setupMap();
    }

    @Override
    public void spawnMapMobs(final LogicModule l) {
    }
}
