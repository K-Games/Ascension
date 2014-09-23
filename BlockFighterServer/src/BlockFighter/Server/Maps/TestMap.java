package BlockFighter.Server.Maps;

import BlockFighter.Server.Globals;
import java.awt.geom.Rectangle2D;

/**
 * Server map information and helper methods.
 * @author Ken
 */
public class TestMap {
    Rectangle2D.Double[] platforms = new Rectangle2D.Double[3];
    double[] boundary = new double[2];
    
    /**
     * Load server map
     */
    public TestMap(){
        platforms[0] = new Rectangle2D.Double(0,620,1280,30);
        platforms[1] = new Rectangle2D.Double(200,400,300,30);
        platforms[2] = new Rectangle2D.Double(600,180,300,30);
        
        boundary[Globals.MAP_LEFT] = 0.0;
        boundary[Globals.MAP_RIGHT] = 1280.0;
    }
    
    /**
     * Check if a location is out of bounds.
     * @param x X in double to be checked
     * @param y Y in double to be checked
     * @return True if out of bounds
     */
    public boolean isOutOfBounds(double x, double y){
        return x < boundary[Globals.MAP_LEFT] || x > boundary[Globals.MAP_RIGHT];
    }
    
    /**
     * Check if the current y is falling on this increment(Y+FallSpeed is checked)
     * A new Rectangle2D.Double is created based on x,y with a width of 30 and height of 1
     * to check for intersections with any of the platforms.
     * @param x X in double to be checked
     * @param y Y in double to be checked
     * @param fallspeed The distance to be increased in Y in double
     * @return True if there is no intersection with any platform.
     */
    public boolean isFalling(double x, double y, double fallspeed) {
        if (fallspeed <= 0) return false;
        for (Rectangle2D.Double platform : platforms) {
            if (platform.y >= y && platform.intersects(new Rectangle2D.Double(x-15, y+fallspeed,30,1))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Return the nearest valid Y using the current Y and distance falling(Y+Fallspeed)
     * A new Rectangle2D.Double is created based on x,y with a width of 30 and height of 1
     * to look for the nearest intersection with a platform.
     * @param x X in double
     * @param y Y in double
     * @param fallspeed The distance to be increased in Y in double
     * @return A Y value of the nearest platform below the x,y. 0 if none found.
     */
    public double getValidY(double x, double y, double fallspeed) {
        for (Rectangle2D.Double platform : platforms) {
            if (platform.y >= y && platform.intersects(new Rectangle2D.Double(x-15, y+fallspeed,30,1))) {
                return platform.y;
            }
        }
        return 0;
    }
}
