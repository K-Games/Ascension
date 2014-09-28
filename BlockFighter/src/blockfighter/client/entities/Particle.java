package blockfighter.client.entities;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author ckwa290
 */
public abstract class Particle extends Thread {

    protected int x, y;
    protected double size = 10.0;
    protected long frameDuration;
    protected int frame = 0;
    protected byte facing = Globals.RIGHT;
    
    protected final int key;
    /**
     * Reference to Logic Module.
     */
    protected final LogicModule logic;

    /**
     * The duration of this projectile in ms.
     */
    protected long duration;

    public void update() {
        duration -= Globals.LOGIC_UPDATE/1000000;
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

    public Particle(LogicModule l, int k, int x, int y, long d) {
        logic = l;
        key = k;
        this.x = x;
        this.y = y;
        duration = d;
    }

    public void draw(Graphics g) {
        g.setColor(Color.blue);
        g.fillRect((int) (x - size / 2), (int) (y - size / 2), (int) size, (int) size);
    }
    
    public void setFacing(byte f) {
        facing = f;
    }
}
