package blockfighter.server.entities.buff;

import blockfighter.server.Globals;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;

public class BuffShieldFortify extends BuffBase implements BuffDmgReduct {

    private final double dmgReduct, dmgTakenMult;
    private final long maxDuration;
    public BuffShieldFortify(long d, double reduct, Player o) {
        super(d);
        maxDuration = d;
        dmgReduct = reduct;
        dmgTakenMult = 1D - dmgReduct;
        setOwner(o);
    }

    @Override
    public void update() {
        super.update();
        if (getOwner().isSkillMaxed(Skill.SHIELD_FORTIFY)) {
            int amount = (int) (getOwner().getStats()[Globals.STAT_MAXHP] * 0.075 / (maxDuration * 1000000 / Globals.LOGIC_UPDATE));
            getOwner().queueHeal(amount);
        }
    }

    @Override
    public double getDmgReduction() {
        return dmgReduct;
    }

    @Override
    public double getDmgTakenMult() {
        return dmgTakenMult;
    }

}
