package blockfighter.server.entities.buff;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import java.awt.Point;

public class BuffBurn extends Buff implements BuffDmgTakenAmp {

    private final double dmgAmp, dmgPerSec;
    private long lastDmgTime;

    public BuffBurn(final LogicModule l, final int d, final double amp, final double dmg, final Player o, final Player t) {
        super(l, d, o, t);
        this.dmgAmp = amp;
        this.lastDmgTime = l.getTime();
        this.dmgPerSec = dmg * 3.75;
    }

    public BuffBurn(final LogicModule l, final int d, final double amp, final double dmg, final Player o, final Mob t) {
        super(l, d, o, t);
        this.dmgAmp = amp;
        this.lastDmgTime = l.getTime();
        this.dmgPerSec = dmg * 3.75;
    }

    @Override
    public double getDmgTakenAmp() {
        return this.dmgAmp;
    }

    @Override
    public void update() {
        super.update();
        int sinceLastDamage = Globals.nsToMs(this.room.getTime() - this.lastDmgTime);
        if (this.dmgPerSec > 0 && sinceLastDamage >= 500) {
            this.lastDmgTime = this.room.getTime();
            if (getTarget() != null) {
                final Point dmgPoint = new Point((int) (getTarget().getHitbox().x),
                        (int) (getTarget().getHitbox().y + getTarget().getHitbox().height / 2));
                getTarget().queueDamage(new Damage((int) (this.dmgPerSec / 2), false, getOwner(), getTarget(), false, dmgPoint));
            }
            if (getMobTarget() != null) {
                final Point dmgPoint = new Point((int) (getMobTarget().getHitbox().x),
                        (int) (getMobTarget().getHitbox().y + getMobTarget().getHitbox().height / 2));
                getMobTarget().queueDamage(new Damage((int) (this.dmgPerSec / 2), false, getOwner(), getMobTarget(), false, dmgPoint));
            }
        }
    }
}
