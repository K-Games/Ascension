/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockfighter.server.entities.buff;

import blockfighter.server.Globals;
import blockfighter.server.entities.Player;

/**
 * Abstract class for all buffs/debuffs
 *
 * @author Ken
 */
public class BuffBase implements Buff {

    protected Player owner;
    protected long duration;

    /**
     * Constructor for buffs
     * @param d duration in ms
     */
    public BuffBase(long d) {
        duration = d;
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
    public void update() {
        duration -= Globals.nsToMs(Globals.LOGIC_UPDATE);
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public boolean isExpired() {
        return duration <= 0;
    }

}
