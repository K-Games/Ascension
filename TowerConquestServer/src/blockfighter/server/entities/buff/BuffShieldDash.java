package blockfighter.server.entities.buff;

import blockfighter.server.entities.player.Player;

/**
 *
 * @author Ken Kwan
 */
public class BuffShieldDash extends Buff implements BuffDmgIncrease {

	private final double dmgIncrease;

	public BuffShieldDash(final long d, final double inc, final Player o) {
		super(d);
		setOwner(o);
		this.dmgIncrease = inc;
	}

	@Override
	public double getDmgIncrease() {
		return this.dmgIncrease;
	}

}
