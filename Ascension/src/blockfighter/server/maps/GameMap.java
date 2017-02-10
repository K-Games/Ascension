package blockfighter.server.maps;

import blockfighter.server.LogicModule;
import blockfighter.shared.Globals;
import blockfighter.shared.Globals.GameMaps;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class GameMap {

    final static long PLATFORM_BUCKET_CELLSIZE = 600;

    private HashMap<Integer, ArrayList<GameMapPlatform>> platformBuckets;

    GameMapPlatform[] platforms;
    Point2D.Double[] spawnPoints;
    double[] boundary = new double[4];
    protected final GameMaps map;
    boolean isPvP = false;
    LogicModule logic;

    public GameMap(GameMaps map) {
        this.map = map;
        this.boundary[Globals.MAP_LEFT] = 0.0;
        this.boundary[Globals.MAP_RIGHT] = 1280.0;
        this.boundary[Globals.MAP_TOP] = -500;
        this.boundary[Globals.MAP_BOTTOM] = 600;
    }

    protected final void setupMap() {
        double numRows = getMapHeight() / Globals.SERVER_LOGIC_BUCKET_CELLSIZE;
        double numCols = getMapWidth() / Globals.SERVER_LOGIC_BUCKET_CELLSIZE;
        int numBuckets = (int) Math.ceil(numRows * numCols);
        this.platformBuckets = new HashMap<>(numBuckets);
        Integer[] bucketIDs = getBucketIDsForRect(getBoundaryRectangle());
        for (int bucketID : bucketIDs) {
            this.platformBuckets.put(bucketID, new ArrayList<>());
        }

        for (GameMapPlatform platform : platforms) {
            bucketIDs = getBucketIDsForRect(platform.getRect());
            for (int bucketID : bucketIDs) {
                this.platformBuckets.get(bucketID).add(platform);
            }
        }
    }

    private Integer[] getBucketIDsForRect(Rectangle2D.Double rect) {
        int maxOccupiedCol = 1 + (int) Math.ceil(rect.width / PLATFORM_BUCKET_CELLSIZE);
        int maxOccupiedRow = 1 + (int) Math.ceil(rect.height / PLATFORM_BUCKET_CELLSIZE);
        ArrayList<Integer> containingBuckets = new ArrayList<>(maxOccupiedRow * maxOccupiedCol);

        double numCol = getMapWidth() / PLATFORM_BUCKET_CELLSIZE;

        double[] rectPointsX = new double[maxOccupiedCol];
        rectPointsX[rectPointsX.length - 1] = rect.getMaxX();
        for (int i = 0; i < rectPointsX.length - 1; i++) {
            rectPointsX[i] = rect.getMinX() + i * PLATFORM_BUCKET_CELLSIZE;
            if (rectPointsX[i] < getBoundary()[Globals.MAP_LEFT]) {
                rectPointsX[i] = getBoundary()[Globals.MAP_LEFT];
            } else if (rectPointsX[i] > getBoundary()[Globals.MAP_RIGHT]) {
                rectPointsX[i] = getBoundary()[Globals.MAP_RIGHT];
            }
        }

        double[] rectPointsY = new double[maxOccupiedRow];
        rectPointsY[rectPointsY.length - 1] = rect.getMaxY();
        for (int i = 0; i < rectPointsY.length - 1; i++) {
            rectPointsY[i] = rect.getMinY() + i * PLATFORM_BUCKET_CELLSIZE;
            if (rectPointsY[i] < getBoundary()[Globals.MAP_TOP]) {
                rectPointsY[i] = getBoundary()[Globals.MAP_TOP];
            } else if (rectPointsY[i] > getBoundary()[Globals.MAP_BOTTOM]) {
                rectPointsY[i] = getBoundary()[Globals.MAP_BOTTOM];
            }
        }

        for (int i = 0; i < rectPointsY.length; i++) {
            for (int j = 0; j < rectPointsX.length; j++) {
                double row = Math.floor(rectPointsY[i] / PLATFORM_BUCKET_CELLSIZE) * numCol;
                double col = Math.floor(rectPointsX[j] / PLATFORM_BUCKET_CELLSIZE);
                int id = (int) (row + col);
                if (!containingBuckets.contains(id)) {
                    containingBuckets.add(id);
                }
            }
        }
        return containingBuckets.toArray(new Integer[containingBuckets.size()]);
    }

    public boolean isOutOfBounds(final double x) {
        return x < this.boundary[Globals.MAP_LEFT] || x > this.boundary[Globals.MAP_RIGHT];
    }

    public boolean areaIntersectsPlatform(final Rectangle2D.Double rect) {
        Integer[] bucketIDs = getBucketIDsForRect(rect);
        for (int bucketID : bucketIDs) {
            if (this.platformBuckets.containsKey(bucketID)) {
                for (GameMapPlatform platform : this.platformBuckets.get(bucketID)) {
                    if (platform.intersects(rect)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isFalling(final double x, final double y, final double fallspeed) {
        if (fallspeed < 0) {
            return false;
        }
        return !areaIntersectsPlatform(new Rectangle2D.Double(x - 25, y, 50, 1));
    }

    public boolean isWithinDistanceToGround(final double x, final double y, final double distance) {
        return areaIntersectsPlatform(new Rectangle2D.Double(x - 25, y, 50, distance));
    }

    public double getValidY(final double x, final double y) {
        Rectangle2D.Double fallingArea = new Rectangle2D.Double(x - 25, y - 1, 50, 0.1);
        Integer[] bucketIDs = getBucketIDsForRect(fallingArea);
        for (int bucketID : bucketIDs) {
            for (GameMapPlatform platform : this.platformBuckets.get(bucketID)) {
                if (platform.intersects(fallingArea)) {
                    return Math.min(platform.getY(fallingArea.getMinX() + 7), platform.getY(fallingArea.getMaxX() - 7));
                }
            }
        }
        return y;
    }

    public double getValidX(final double x, final double y) {
        if (x < boundary[Globals.MAP_LEFT]) {
            return boundary[Globals.MAP_LEFT];
        }
        if (x > boundary[Globals.MAP_RIGHT]) {
            return boundary[Globals.MAP_RIGHT];
        }

        Rectangle2D.Double fallingArea = new Rectangle2D.Double(x - 25, y - 90, 50, 80);
        Integer[] bucketIDs = getBucketIDsForRect(fallingArea);
        for (int bucketID : bucketIDs) {
            for (GameMapPlatform platform : this.platformBuckets.get(bucketID)) {
                if (platform.isSolid() && platform.intersects(fallingArea)) {
                    if (!(x >= platform.getRect().getMinX() && x <= platform.getRect().getMaxX())) {
                        return platform.getValidX(x);
                    }
                }
            }
        }
        return x;
    }

    public GameMaps getGameMap() {
        return this.map;
    }

    public byte getMapCode() {
        return this.map.getMapCode();
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
