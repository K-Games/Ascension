package blockfighter.server.entities.buff;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.shared.Globals;

public class BuffTauntCripple extends Buff implements BuffSpeedDecrease {

    public BuffTauntCripple(final LogicModule l, final int d, final Player o, final Player t) {
        super(l, d, o, t);
        super.setDebuff(true);
    }

    @Override
    public double getXSpeedDecrease() {
        return getOwner().getSkill(Globals.SWORD_TAUNT_CRIPPLE).getSkillData().getBaseValue();
    }

    @Override
    public double getYSpeedDecrease() {
        return getOwner().getSkill(Globals.SWORD_TAUNT_CRIPPLE).getSkillData().getBaseValue();
    }

}
