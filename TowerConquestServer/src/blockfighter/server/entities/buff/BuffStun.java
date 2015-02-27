package blockfighter.server.entities.buff;

/**
 *
 * @author Ken Kwan
 */
public class BuffStun extends Buff {

    /**
     * Construct stun debuff
     *
     * @param d duration in ms
     */
    public BuffStun(long d) {
        super(d);
        setDebuff(true);
    }

}
