package blockfighter.server.entities.buff;

import blockfighter.server.entities.player.Player;

/**
 *
 * @author Ken Kwan
 */
public class BuffBowVolley extends BuffBase implements BuffDmgIncrease{
    private final double dmgIncrease;

    public BuffBowVolley(long d, double inc, Player o) {
        super(d);
        setOwner(o);
        dmgIncrease = inc;
    }
    
    @Override
    public double getDmgIncrease() {
        return dmgIncrease;
    }

}
