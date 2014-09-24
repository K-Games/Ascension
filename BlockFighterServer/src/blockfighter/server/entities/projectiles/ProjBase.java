/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockfighter.server.entities.Projectiles;

import blockfighter.server.Globals;
import blockfighter.server.entities.Player;
import blockfighter.server.entities.Projectile;
import blockfighter.server.LogicModule;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This is the base projectile class. Create projectile classes off this.
 *
 * @author Ken
 */
public class ProjBase extends Thread implements Projectile {

    private final LogicModule logic;
    private double x, y, xSpeed, ySpeed;
    private Player owner;
    private ArrayList<Player> pHit = new ArrayList<>();
    private double duration;
    private Rectangle2D.Double hitbox;
    private LinkedList<Player> queue = new LinkedList<>();

    /**
     * Create a basic projectile.
     * <p>
     * Does nothing.
     * </p>
     * @param l Reference to Logic module
     * @param o
     * @param x Spawning x
     * @param y Spawning y
     * @param duration
     */
    public ProjBase(LogicModule l, Player o, double x, double y, double duration) {
        owner = o;
        logic = l;
        if (owner.getFacing() == Globals.LEFT) {
            xSpeed = -6;
        } else {
            xSpeed = 6;
        }
        ySpeed = -8;
        this.x = x;
        this.y = y;
        if (owner.getFacing() == Globals.LEFT) {
            hitbox = new Rectangle2D.Double(x - 35, y - 96, 35, 96);
        } else {
            hitbox = new Rectangle2D.Double(x, y - 96, 35, 96);
        }
        this.duration = duration;
    }

    @Override
    public void update() {
        duration -= Globals.LOGIC_UPDATE;
        for (Player p : logic.getPlayers()) {
            if (p != owner && p != null && !pHit.contains(p) && p.intersectHitbox(hitbox)) {
                queue.add(p);
                pHit.add(p);
                logic.queueKnockPlayer(this);
            }
        }
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void run() {
        update();
    }

    @Override
    public boolean isExpired() {
        return duration <= 0;
    }

    /**
     * Process any knockbacks to be applied to players hit by this projectile.
     */
    public void processQueue() {
        while (!queue.isEmpty()) {
            Player p = queue.pop();
            p.setKnockback(500000000, xSpeed, ySpeed);
        }
    }
}
