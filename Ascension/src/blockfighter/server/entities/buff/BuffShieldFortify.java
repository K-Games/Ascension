package blockfighter.server.entities.buff;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.SkillShieldFortify;
import blockfighter.shared.Globals;

public class BuffShieldFortify extends Buff implements BuffDmgReduct {

    private final double dmgReduct, dmgTakenMult;
    private final int maxDuration;
    private long lastHPHeal = 0;

    public BuffShieldFortify(final LogicModule l, final int d, final double reduct, final Player o) {
        super(l, d, o);
        this.maxDuration = d;
        this.dmgReduct = reduct;
        this.dmgTakenMult = 1D - this.dmgReduct;
    }

    @Override
    public void update() {
        super.update();
        if (getOwner().isSkillMaxed(Globals.SHIELD_FORTIFY)) {
            if (Globals.nsToMs(room.getTime() - lastHPHeal) >= Globals.nsToMs(Globals.SERVER_LOGIC_UPDATE)) {
                double healAmount = ((SkillShieldFortify) getOwner().getSkill(Globals.SHIELD_FORTIFY)).getHealAmount();
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
