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
        platforms[0] = new Rectangle2D.Double(0, 620, 5000, 30);
        platforms[1] = new Rectangle2D.Double(200, 400, 300, 30);
        platforms[2] = new Rectangle2D.Double(600, 180, 300, 30);

        boundary[Globals.MAP_LEFT] = 0.0;
        boundary[Globals.MAP_RIGHT] = 5000.0;
    }

}
