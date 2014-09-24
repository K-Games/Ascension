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

/**
 * This is the base projectile class. Create projectile classes off this.
 * @author Ken
 */
public class ProjBase extends Thread implements Projectile{
    private final LogicModule logic;
    private double x,y, xSpeed, ySpeed;
    private Player owner;
    private ArrayList<Player> pHit = new ArrayList<>();
    private double duration;
    private Rectangle2D.Double hitbox;
    
    /**
     * Create a basic projectile. Does nothing.
     * @param l Reference to Logic module
     * @param x Spawning x
     * @param y Spawning y
     */
    public ProjBase(LogicModule l, Player o, double x, double y, double duration) {
        owner = o;
        logic = l;
        if (owner.getFacing() == Globals.LEFT) xSpeed = 8; else xSpeed = -8;
        ySpeed = -8;
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double(x,y,30,-30);
        this.duration = duration;
    }
    
    @Override
    public void update() {
        duration -= Globals.LOGIC_UPDATE;
        for (Player p:logic.getPlayers()) {
            if (!pHit.contains(p) && p.intersectHitbox(hitbox)) {
                p.setKnockback(500000000, xSpeed, ySpeed);
                pHit.add(p);
            }
        }
    }

    @Override
    public double getX() { return x; }

    @Override
    public double getY() { return y; }

    @Override
    public void setOwner(Player owner) { this.owner = owner; }

    @Override
    public Player getOwner() { return owner;}

    @Override
    public void run() {
        update();
    }

    @Override
    public boolean isExpired() { return duration <= 0; }
    
}
