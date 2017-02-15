package blockfighter.server.maps;

import blockfighter.shared.Globals;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class GameMapPlatform {

    private final Rectangle2D.Double rect;

    private Area polygonArea;
    private final boolean isRect;
    private boolean isSolid;
    private final double x1, y1, x2, y2;

    public GameMapPlatform(final Rectangle2D.Double rect) {
        this.isRect = true;
        this.isSolid = false;
        this.rect = rect;
        this.x1 = 0;
        this.y1 = 0;
        this.x2 = 0;
        this.y2 = 0;
    }

    public GameMapPlatform(final double x1, final double y1, final double x2, final double y2) {
        this.isRect = false;
        this.isSolid = false;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        double angleRadians = Math.atan2(y2 - y1, x2 - x1);
        double width = Point.distance(x1, y1, x2, y2);
        double height = 30;
        Rectangle2D.Double platformRect = new Rectangle2D.Double(x1, y1, width, height);
        this.polygonArea = new Area(platformRect);
        AffineTransform rotation = new AffineTransform();

        rotation.rotate(angleRadians, x1, y1);
        this.polygonArea.transform(rotation);

        this.rect = new Rectangle2D.Double(
                this.polygonArea.getBounds2D().getX(),
                this.polygonArea.getBounds2D().getY(),
                this.polygonArea.getBounds2D().getWidth(),
                this.polygonArea.getBounds2D().getHeight()
        );
    }

    public boolean isSolid() {
        return this.isSolid;
    }

    public void setIsSolid(final boolean solid) {
        this.isSolid = solid;
    }

    public Rectangle2D.Double getRect() {
        return this.rect;
    }

    public boolean intersects(Rectangle2D.Double rect) {
        if (this.isRect) {
            return this.rect.intersects(rect);
        } else {
            return this.polygonArea.intersects(rect);
        }
    }

    public double getValidX(final double x, final GameMap map) {
        double result;
        if (Math.abs(x - this.rect.x) <= Math.abs(x - (this.rect.x + this.rect.width))) {
            result = this.rect.getMinX() - 25;
        } else {
            result = this.rect.getMaxX() + 25;
        }
        if (result < map.getBoundary()[Globals.MAP_LEFT]) {
            result = this.rect.getMaxX() + 25;
        }
        if (result > map.getBoundary()[Globals.MAP_RIGHT]) {
            result = this.rect.getMinX() - 25;
        }
        return result;
    }

    public double getY(double x) {
        if (this.isRect) {
            return this.rect.y;
        } else {
            double minX = Math.min(x1, x2), maxX = Math.max(x1, x2);
            if (x < minX) {
                x = minX;
            } else if (x > maxX) {
                x = maxX;
            }
            double m = (this.y2 - this.y1) / (this.x2 - this.x1);
            double c = this.y1 - m * this.x1;
            return m * x + c;
        }
    }
}
