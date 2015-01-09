/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockfighter.server.entities.buff;

/**
 *
 * @author Ken
 */
public class BuffStun extends BuffBase {

    /**
     * Construct stun debuff
     *
     * @param d duration in ms
     */
    public BuffStun(long d) {
        super(d);
    }

}
