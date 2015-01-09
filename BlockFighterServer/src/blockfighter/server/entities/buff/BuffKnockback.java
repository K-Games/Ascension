/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockfighter.server.entities.buff;

import blockfighter.server.entities.Player;

/**
 *
 * @author Ken
 */
public class BuffKnockback extends BuffBase {

    private double xSpeed, ySpeed;
    private boolean applied = false;

    public BuffKnockback(long d, double x, double y, Player o) {
        super(d);
        xSpeed = x;
        ySpeed = y;
        owner = o;
    }

    @Override
    public void update() {
        super.update();
        if (!applied) {
            owner.setXSpeed(xSpeed);
            owner.setYSpeed(ySpeed);
            applied = true;
        }
    }
}
