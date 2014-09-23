/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BlockFighter.Server.Entities.Projectiles;

import BlockFighter.Server.Entities.Player;
import BlockFighter.Server.Entities.Projectile;
import BlockFighter.Server.LogicModule;

/**
 * This is the base projectile class. Create projectile classes off this.
 * @author Ken
 */
public class ProjBase implements Projectile{
    private LogicModule logic;
    private double x,y, xSpeed, ySpeed;
    private Player owner;
    private Player[] pHit;
    
    /**
     * Create a basic projectile. Does nothing.
     * @param l Reference to Logic module
     * @param x Spawning x
     * @param y Spawning y
     */
    public ProjBase(LogicModule l, double x, double y) {
        logic = l;
        this.x = x;
        this.y = y;
    }
    
    @Override
    public void update() {
        
    }

    @Override
    public double getX() { return x; }

    @Override
    public double getY() { return y; }

    @Override
    public void setOwner(Player owner) { this.owner = owner; }

    @Override
    public Player getOwner() { return owner;}
    
}
