package blockfighter.server.entities.buff;

import blockfighter.server.entities.player.Player;

public class BuffSwordSlash extends Buff implements BuffDmgReduct {

	private final double dmgReduct, dmgTakenMult;

	public BuffSwordSlash(final long d, final double reduct, final Player o) {
		super(d);
		this.dmgReduct = reduct;
		this.dmgTakenMult = 1D - this.dmgReduct;
		setOwner(o);
	}

	@Override
	public double getDmgReduction() {
		return this.dmgReduct;
	}

	@Override
	public double getDmgTakenMult() {
		return this.dmgTakenMult;
	}

}
