package blockfighter.server.maps;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class GameMapTest {

    GameMap testMap;
    final double[] testBoundary = {0.0, 3400.0, -800, 700};

    @Before
    public void setup() {
        this.testMap = new GameMap() {
            public GameMap setup() {
                this.mapID = 60;
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
                this.boundary[Globals.MAP_LEFT] = testBoundary[Globals.MAP_LEFT];
                this.boundary[Globals.MAP_RIGHT] = testBoundary[Globals.MAP_RIGHT];
                this.boundary[Globals.MAP_TOP] = testBoundary[Globals.MAP_TOP];
                this.boundary[Globals.MAP_BOTTOM] = testBoundary[Globals.MAP_BOTTOM];
                setupMap();
                return this;
            }

            @Override
            public void spawnMapMobs(LogicModule l) {
            }
        }.setup();
    }

    @Test
    public void testIsOutOfBounds() {
        double x = -1000.0;
        assertTrue(this.testMap.isOutOfBounds(x));

        x = 6000.0;
        assertTrue(this.testMap.isOutOfBounds(x));

        for (int i = 0; i < this.testMap.getBoundary()[Globals.MAP_RIGHT]; i++) {
            assertFalse(this.testMap.isOutOfBounds(i));
        }
    }

    @Test
    public void testAreaIntersectsPlatform() {
        Rectangle2D.Double[] testPlatforms = new Rectangle2D.Double[15];
        double width = 50, height = 1;
        testPlatforms[0] = new Rectangle2D.Double(-50, 600, width, height);

        testPlatforms[1] = new Rectangle2D.Double(650, 350, width, height);
        testPlatforms[2] = new Rectangle2D.Double(1550, 350, width, height);
        testPlatforms[3] = new Rectangle2D.Double(2450, 350, width, height);

        testPlatforms[4] = new Rectangle2D.Double(200, 100, width, height);
        testPlatforms[5] = new Rectangle2D.Double(1100, 100, width, height);
        testPlatforms[6] = new Rectangle2D.Double(2000, 100, width, height);
        testPlatforms[7] = new Rectangle2D.Double(2900, 100, width, height);

        testPlatforms[8] = new Rectangle2D.Double(650, -150, width, height);
        testPlatforms[9] = new Rectangle2D.Double(1550, -150, width, height);
        testPlatforms[10] = new Rectangle2D.Double(2450, -150, width, height);

        testPlatforms[11] = new Rectangle2D.Double(200, -400, width, height);
        testPlatforms[12] = new Rectangle2D.Double(1100, -400, width, height);
        testPlatforms[13] = new Rectangle2D.Double(2000, -400, width, height);
        testPlatforms[14] = new Rectangle2D.Double(2900, -400, width, height);
        for (Rectangle2D.Double rect : testPlatforms) {
            double originX = rect.x;
            double originY = rect.y;
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 30; y++) {
                    rect.x = originX + x;
                    rect.y = originY + y;
                    assertTrue(this.testMap.areaIntersectsPlatform(rect));
                }
            }
        }

        assertFalse(this.testMap.areaIntersectsPlatform(new Rectangle2D.Double(2800, 100, width, height)));
    }

    @Test
    public void testIsFalling() {
        assertFalse(this.testMap.isFalling(0, 0, 0));
        assertFalse(this.testMap.isFalling(2900, -400, 10));
        assertFalse(this.testMap.isFalling(0, 0, -10));
        assertTrue(this.testMap.isFalling(0, 0, 10));
    }

    @Test
    public void testIsWithinDistanceToGround() {
        assertTrue(this.testMap.isWithinDistanceToGround(2900, -400, 50));
        assertFalse(this.testMap.isWithinDistanceToGround(2900, -450, 50));
    }

    @Test
    public void testGetValidY() {
        assertEquals(-400, this.testMap.getValidY(2900, -400), 0.0);
        assertEquals(0, this.testMap.getValidY(2900, -450), 0.0);
        assertEquals(-400, this.testMap.getValidY(2900, -380), 0.0);
    }

    @Test
    public void testGetValidX() {
        assertEquals(this.testMap.getBoundary()[Globals.MAP_LEFT], this.testMap.getValidX(this.testMap.getBoundary()[Globals.MAP_LEFT] - 100), 0.0);
        assertEquals(this.testMap.getBoundary()[Globals.MAP_RIGHT], this.testMap.getValidX(this.testMap.getBoundary()[Globals.MAP_RIGHT] + 100), 0.0);
        assertEquals(3000, this.testMap.getValidX(3000), 0.0);
    }

    @Test
    public void testGetMapID() {
        assertEquals(60, this.testMap.getMapID());
    }

    @Test
    public void testGetBoundary() {
        double[] result = this.testMap.getBoundary();
        for (int i = 0; i < this.testBoundary.length; i++) {
            assertTrue(this.testBoundary[i] == result[i]);
        }
    }

    @Test
    public void testIsPvP() {
        assertTrue(this.testMap.isPvP());
    }

    @Test
    public void testGetRandomSpawnPoint() {
        assertNotNull(this.testMap.getRandomSpawnPoint());
    }

    @Test
    public void testGetBoundaryRectangle() {
        Rectangle2D.Double expResult = new Rectangle2D.Double(
                this.testBoundary[Globals.MAP_LEFT],
                this.testBoundary[Globals.MAP_TOP],
                this.testBoundary[Globals.MAP_RIGHT] - this.testBoundary[Globals.MAP_LEFT],
                this.testBoundary[Globals.MAP_BOTTOM] - this.testBoundary[Globals.MAP_TOP]);
        Rectangle2D.Double result = this.testMap.getBoundaryRectangle();
        assertTrue(result.equals(expResult));
    }

    @Test
    public void testGetMapWidth() {
        double expResult = this.testBoundary[Globals.MAP_RIGHT] - this.testBoundary[Globals.MAP_LEFT];
        double result = this.testMap.getMapWidth();
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetMapHeight() {
        double expResult = this.testBoundary[Globals.MAP_BOTTOM] - this.testBoundary[Globals.MAP_TOP];
        double result = this.testMap.getMapHeight();
        assertEquals(expResult, result, 0.0);
    }

}
