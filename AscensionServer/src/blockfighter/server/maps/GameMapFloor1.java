package blockfighter.server.maps;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.boss.ShadowFiend.BossShadowFiend;
import java.awt.geom.Rectangle2D;

public class GameMapFloor1 extends GameMap {

    public GameMapFloor1() {
        this.mapID = 1;
        this.isPvP = false;
        this.platforms = new GameMapPlatform[8];
        this.platforms[0] = new GameMapPlatform(new Rectangle2D.Double(-50, 600, 3750, 30));
        this.platforms[1] = new GameMapPlatform(new Rectangle2D.Double(200, 350, 300, 30));
        this.platforms[2] = new GameMapPlatform(new Rectangle2D.Double(700, 100, 300, 30));
        this.platforms[3] = new GameMapPlatform(new Rectangle2D.Double(1200, -150, 300, 30));
        this.platforms[4] = new GameMapPlatform(new Rectangle2D.Double(1700, -150, 300, 30));
        this.platforms[5] = new GameMapPlatform(new Rectangle2D.Double(2200, -150, 300, 30));
        this.platforms[6] = new GameMapPlatform(new Rectangle2D.Double(2700, 100, 300, 30));
        this.platforms[7] = new GameMapPlatform(new Rectangle2D.Double(3200, 350, 300, 30));
        this.boundary[Globals.MAP_LEFT] = 0.0;
        this.boundary[Globals.MAP_RIGHT] = 3700.0;
    }

    @Override
    public void spawnMapMobs(final LogicModule l) {
        for (int i = 0; i < 1; i++) {
            l.queueAddMob(new BossShadowFiend(l, this, 1500, 100));
        }
    }
}
