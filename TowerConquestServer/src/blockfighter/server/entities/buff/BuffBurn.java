package blockfighter.server.entities.buff;

import java.awt.Point;

import blockfighter.server.Globals;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;

/**
 *
 * @author Ken Kwan
 */
public class BuffBurn extends Buff implements BuffDmgTakenAmp {

	private final double dmgAmp, dmgPerSec;
	private long nextDmgTime = 500;

	public BuffBurn(final long d, final double amp, final double dmg, final Player o, final Player t) {
		super(d);
		setOwner(o);
		setTarget(t);
		this.dmgAmp = amp;
		this.dmgPerSec = dmg * 3.75;
	}

	public BuffBurn(final long d, final double amp, final double dmg, final Player o, final Boss t) {
		super(d);
		setOwner(o);
		setTarget(t);
		this.dmgAmp = amp;
		this.dmgPerSec = dmg * 3.75;
	}

	@Override
	public double getDmgTakenAmp() {
		return this.dmgAmp;
	}

	@Override
	public void update() {
		super.update();
		this.nextDmgTime -= Globals.LOGIC_UPDATE / 1000000;
		if (this.dmgPerSec > 0 && this.nextDmgTime <= 0) {
			this.nextDmgTime = 500;
			if (getTarget() != null) {
				final Point dmgPoint = new Point((int) (getTarget().getHitbox().x),
						(int) (getTarget().getHitbox().y + getTarget().getHitbox().height / 2));
				getTarget().queueDamage(new Damage((int) (this.dmgPerSec / 2), false, getOwner(), getTarget(), false, dmgPoint));
			}
			if (getBossTarget() != null) {
				final Point dmgPoint = new Point((int) (getBossTarget().getHitbox().x),
						(int) (getBossTarget().getHitbox().y + getBossTarget().getHitbox().height / 2));
				getBossTarget().queueDamage(new Damage((int) (this.dmgPerSec / 2), false, getOwner(), getBossTarget(), false, dmgPoint));
			}
		}
	}
}
