package blockfighter.server.maps;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class GameMap {

    Rectangle2D.Double[] platforms;
    Point2D.Double[] spawnPoints;
    double[] boundary = new double[4];
    byte mapID = -1;
    boolean isPvP = false;
    LogicModule logic;

    public GameMap() {
        this.boundary[Globals.MAP_LEFT] = 0.0;
        this.boundary[Globals.MAP_RIGHT] = 1280.0;
        this.boundary[Globals.MAP_TOP] = -500;
        this.boundary[Globals.MAP_BOTTOM] = 600;
    }

    public boolean isOutOfBounds(final double x) {
        return x < this.boundary[Globals.MAP_LEFT] || x > this.boundary[Globals.MAP_RIGHT];
    }

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

    public boolean isWithinDistanceToGround(final double x, final double y, final double distance) {
        for (final Rectangle2D.Double platform : this.platforms) {
            if (platform.intersects(x - 25, y, 50, distance)) {
                return true;
            }
        }
        return false;
    }

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

    public byte getMapID() {
        return this.mapID;
    }

    public double[] getBoundary() {
        return this.boundary;
    }

    public boolean isPvP() {
        return this.isPvP;
    }

    public abstract void spawnMapMobs(LogicModule l);

    public Point2D.Double getRandomSpawnPoint() {
        if (this.spawnPoints == null) {
            return new Point2D.Double(this.boundary[Globals.MAP_LEFT] + 50, 0);
        }
        return this.spawnPoints[Globals.rng(this.spawnPoints.length)];
    }

    public Rectangle2D.Double getBoundaryRectangle() {
        return new Rectangle2D.Double(this.boundary[Globals.MAP_LEFT], this.boundary[Globals.MAP_TOP], getMapWidth(), getMapHeight());
    }

    public double getMapWidth() {
        return this.boundary[Globals.MAP_RIGHT] - this.boundary[Globals.MAP_LEFT];
    }

    public double getMapHeight() {
        return this.boundary[Globals.MAP_BOTTOM] - this.boundary[Globals.MAP_TOP];
    }
}
