package blockfighter.server.entities.buff;

import blockfighter.server.entities.player.Player;

public class BuffPassiveBarrier extends Buff {

	private double barrierAmount;

	public BuffPassiveBarrier(final double amt, final Player o) {
		super(0);
		this.barrierAmount = amt;
		setOwner(o);
	}

	@Override
	public void update() {
	}

	public double reduceDmg(final double dmg) {
		double finalDmg;
		if (this.barrierAmount >= dmg) {
			this.barrierAmount -= dmg;
			finalDmg = 0;
		} else {
			finalDmg = dmg - this.barrierAmount;
			this.barrierAmount = 0;
		}
		return finalDmg;
	}

	@Override
	public boolean isExpired() {
		return this.barrierAmount <= 0;
	}
}
