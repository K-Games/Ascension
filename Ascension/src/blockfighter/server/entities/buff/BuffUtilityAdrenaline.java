package blockfighter.server.entities.buff;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.SkillUtilityAdrenaline;
import blockfighter.shared.Globals;

public class BuffUtilityAdrenaline extends Buff implements BuffDmgReduct, BuffXSpeedIncrease {

    private double xSpeedBonus;
    private final double dmgReduct, dmgTakenMult;
    private final int maxDuration;
    private long lastHPHeal = 0;

    public BuffUtilityAdrenaline(final LogicModule l, final int d, final double reduct, final Player o) {
        super(l, d, o);
        this.maxDuration = d;
        this.dmgReduct = reduct;
        this.dmgTakenMult = 1D - this.dmgReduct;
        double moveSpeedBase = o.getSkill(Globals.UTILITY_ADRENALINE).getCustomValue(SkillUtilityAdrenaline.CUSTOM_DATA_HEADERS[2]);
        double moveSpeedMult = o.getSkill(Globals.UTILITY_ADRENALINE).getCustomValue(SkillUtilityAdrenaline.CUSTOM_DATA_HEADERS[3]) * o.getSkillLevel(Globals.UTILITY_ADRENALINE);
        this.xSpeedBonus = Globals.WALK_SPEED * (moveSpeedBase + moveSpeedMult);
    }

    @Override
    public void update() {
        super.update();
        if (getOwner().isSkillMaxed(Globals.UTILITY_ADRENALINE)) {
            if (Globals.nsToMs(room.getTime() - lastHPHeal) >= Globals.nsToMs(Globals.SERVER_LOGIC_UPDATE)) {
                double healAmount = getOwner().getSkill(Globals.UTILITY_ADRENALINE).getCustomValue(SkillUtilityAdrenaline.CUSTOM_DATA_HEADERS[1]);
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

    @Override
    public double getXSpeedIncrease() {
        return this.xSpeedBonus;
    }

}
