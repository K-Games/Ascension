package blockfighter.client.entities;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import java.awt.Graphics;

/**
 *
 * @author ckwa290
 */
public class Particle extends Thread {

    private int x, y;
    private double size = 10.0;

    protected final int key;
    /**
     * Reference to Logic Module.
     */
    protected final LogicModule logic;

    /**
     * The duration of this projectile in ns.
     */
    protected double duration;

    public void update() {
        duration -= Globals.LOGIC_UPDATE;
        size -= 0.5;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public void run() {
        update();
    }

    public boolean isExpired() {
        return duration <= 0;
    }

    public Particle(LogicModule l, int k, int x, int y, double d) {
        logic = l;
        key = k;
        this.x = x;
        this.y = y;
        duration = d;
    }

    public void draw(Graphics g) {
        g.drawRect((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);
    }
}
