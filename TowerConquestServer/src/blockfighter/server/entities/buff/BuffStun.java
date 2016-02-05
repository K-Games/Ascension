package blockfighter.server.entities.buff;

import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class BuffStun extends Buff {

    /**
     * Construct stun debuff
     *
     * @param l
     * @param d duration in ms
     */
    public BuffStun(final LogicModule l, final int d) {
        super(l, d);
        super.setDebuff(true);
    }

}
