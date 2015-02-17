package blockfighter.server.entities.buff;

/**
 *
 * @author Ken Kwan
 */
public class BuffStun extends BuffBase {

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
