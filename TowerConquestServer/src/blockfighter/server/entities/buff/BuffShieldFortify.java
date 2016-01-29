package blockfighter.server.entities.buff;

import blockfighter.server.Globals;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;

public class BuffShieldFortify extends Buff implements BuffDmgReduct {

    private final double dmgReduct, dmgTakenMult;
    private final long maxDuration;

    public BuffShieldFortify(final long d, final double reduct, final Player o) {
        super(d, o);
        this.maxDuration = d;
        this.dmgReduct = reduct;
        this.dmgTakenMult = 1D - this.dmgReduct;
    }

    @Override
    public void update() {
        super.update();
        if (getOwner().isSkillMaxed(Skill.SHIELD_FORTIFY)) {
            final int amount = (int) (getOwner().getStats()[Globals.STAT_MAXHP] * 0.075
                    / (this.maxDuration * 1000000 / Globals.LOGIC_UPDATE));
            getOwner().queueHeal(amount);
        }
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
