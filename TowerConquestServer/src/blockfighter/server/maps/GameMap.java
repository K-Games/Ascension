package blockfighter.server.maps;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.Mob;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Server map information and helper methods.
 *
 * @author Ken Kwan
 */
public abstract class GameMap {

    Rectangle2D.Double[] platforms;
    Point2D.Double[] spawnPoints;
    double[] boundary = new double[2];
    byte mapID = -1;
    boolean isPvP = false;
    LogicModule logic;

    /**
     * Default Game Map Constructor Sets map boundaries at default. Left boundary is 0, right boundary is 1280.
     */
    public GameMap() {
        this.boundary[Globals.MAP_LEFT] = 0.0;
        this.boundary[Globals.MAP_RIGHT] = 1280.0;
    }

    /**
     * Check if a location is out of bounds.
     *
     * @param x X in double to be checked
     * @return True if out of bounds
     */
    public boolean isOutOfBounds(final double x) {
        return x < this.boundary[Globals.MAP_LEFT] || x > this.boundary[Globals.MAP_RIGHT];
    }

    /**
     * Check if the current y is falling
     * <p>
     * Takes increment into account (Y+FallSpeed) Every platform is checked against the input location with a rectangle sized 90x1
     * </p>
     *
     * @param x x coordinate of location
     * @param y y of location(entity's bottom)
     * @param fallspeed The distance to be increased in Y in double
     * @return True if there is no intersection with any platform.
     */
    public boolean isFalling(final double x, final double y, final double fallspeed) {
        if (fallspeed <= 0) {
            return false;
        }
        for (final Rectangle2D.Double platform : this.platforms) {
            if (platform.intersects(x - 25, y, 50, 1)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return the nearest valid Y
     *
     * @param x x coordinate of location
     * @param y y of location(player's bottom)
     * @param fallspeed The distance to be increased in Y in double
     * @return A Y value of the nearest platform below the x,y. 0 if none found.
     */
    public double getValidY(final double x, final double y, final double fallspeed) {
        for (final Rectangle2D.Double platform : this.platforms) {
            if (platform.intersects(x - 25, y, 50, 1)) {
                return platform.y;
            }
        }
        return 0;
    }

    public double getValidX(final double x) {
        if (x < boundary[Globals.MAP_LEFT]) {
            return boundary[Globals.MAP_LEFT];
        }
        if (x > boundary[Globals.MAP_RIGHT]) {
            return boundary[Globals.MAP_RIGHT];
        }
        return x;
    }

    /**
     * Get the map id of this map
     *
     * @return Byte - Map ID
     */
    public byte getMapID() {
        return this.mapID;
    }

    public double[] getBoundary() {
        return this.boundary;
    }

    public boolean isPvP() {
        return this.isPvP;
    }

    public abstract Mob[] getMobs(LogicModule l);

    public Point2D.Double getRandomSpawnPoint() {
        if (this.spawnPoints == null) {
            return new Point2D.Double(this.boundary[Globals.MAP_LEFT] + 50, 0);
        }
        return this.spawnPoints[Globals.rng(this.spawnPoints.length)];
    }
}
