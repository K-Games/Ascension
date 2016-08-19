package blockfighter.server.maps;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class GameMapPlatform {

    private final Rectangle2D.Double rect;
    private Polygon triangle;
    private final boolean isRamp;
    private final double x, y, width, height, peakX;

    public GameMapPlatform(final Rectangle2D.Double rect) {
        this.isRamp = false;
        this.rect = rect;
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
        this.peakX = 0;
    }

    public GameMapPlatform(final double x, final double y, final double width, final double height, final double peakX) {
        this.isRamp = true;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.peakX = peakX;
        this.triangle = new Polygon();
        this.triangle.addPoint((int) x, (int) y);
        this.triangle.addPoint((int) (x + peakX), (int) (y - height));
        this.triangle.addPoint((int) (x + width), (int) y);
        this.triangle.addPoint((int) (x + width), (int) y + 15);
        this.triangle.addPoint((int) x, (int) y + 15);
        this.rect = new Rectangle2D.Double(this.triangle.getBounds2D().getX(), this.triangle.getBounds2D().getY(), this.triangle.getBounds2D().getWidth(), this.triangle.getBounds2D().getHeight());
    }

    public Rectangle2D.Double getRect() {
        return this.rect;
    }

    public boolean intersects(Rectangle2D.Double rect) {
        if (!this.isRamp) {
            return this.rect.intersects(rect);
        } else {
            return this.triangle.intersects(rect);
        }
    }

    public double getY(final double x) {
        if (!this.isRamp) {
            return this.rect.y;
        } else {
            Line2D.Double line = (x < this.x + peakX)
                    ? new Line2D.Double(this.triangle.xpoints[0], this.triangle.ypoints[0], this.triangle.xpoints[1], this.triangle.ypoints[1])
                    : new Line2D.Double(this.triangle.xpoints[1], this.triangle.ypoints[1], this.triangle.xpoints[2], this.triangle.ypoints[2]);
            double factor = (line.y2 - line.y1) / (line.x2 - line.x1);
            return factor * x + (line.y1 - factor * line.x1);
        }
    }
}
