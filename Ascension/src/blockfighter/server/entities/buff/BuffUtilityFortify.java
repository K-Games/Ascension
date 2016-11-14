package blockfighter.server.entities.buff;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.SkillUtilityFortify;
import blockfighter.shared.Globals;

public class BuffUtilityFortify extends Buff implements BuffDmgReduct {

    private final double dmgReduct, dmgTakenMult;
    private final int maxDuration;
    private long lastHPHeal = 0;

    public BuffUtilityFortify(final LogicModule l, final int d, final double reduct, final Player o) {
        super(l, d, o);
        this.maxDuration = d;
        this.dmgReduct = reduct;
        this.dmgTakenMult = 1D - this.dmgReduct;
    }

    @Override
    public void update() {
        super.update();
        if (getOwner().isSkillMaxed(Globals.UTILITY_FORTIFY)) {
            if (Globals.nsToMs(room.getTime() - lastHPHeal) >= Globals.nsToMs(Globals.SERVER_LOGIC_UPDATE)) {
                double healAmount = getOwner().getSkill(Globals.UTILITY_FORTIFY).getCustomValue(SkillUtilityFortify.CUSTOMHEADER_HEAL);
                final int amount = (int) Math.ceil(getOwner().getStats()[Globals.STAT_MAXHP] * healAmount / (this.maxDuration / Globals.nsToMs(Globals.SERVER_LOGIC_UPDATE)));
                getOwner().queueHeal(amount);
                lastHPHeal = room.getTime();
            }
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
