package blockfighter.server.maps;

import blockfighter.server.Globals;
import java.awt.geom.Rectangle2D;

/**
 * Server map information and helper methods.
 *
 * @author Ken Kwan
 */
public class GameMapFloor1 extends GameMap {

    /**
     * Load server map
     */
    public GameMapFloor1() {
        mapID = 0;
        platforms = new Rectangle2D.Double[10];
        platforms[0] = new Rectangle2D.Double(-50, 600, 3750, 30);

        platforms[1] = new Rectangle2D.Double(200, 350, 300, 30);

        platforms[2] = new Rectangle2D.Double(700, 100, 300, 30);

        platforms[3] = new Rectangle2D.Double(1200, -150, 300, 30);
        platforms[4] = new Rectangle2D.Double(1200, 350, 300, 30);

        platforms[5] = new Rectangle2D.Double(1700, 100, 300, 30);

        platforms[6] = new Rectangle2D.Double(2200, -150, 300, 30);
        platforms[7] = new Rectangle2D.Double(2200, 350, 300, 30);

        platforms[8] = new Rectangle2D.Double(2700, 100, 300, 30);

        platforms[9] = new Rectangle2D.Double(3200, 350, 300, 30);
        boundary[Globals.MAP_LEFT] = 0.0;
        boundary[Globals.MAP_RIGHT] = 3700.0;
    }

}
