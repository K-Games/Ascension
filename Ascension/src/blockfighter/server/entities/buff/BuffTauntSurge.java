package blockfighter.server.entities.buff;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.shared.Globals;

public class BuffTauntSurge extends Buff implements BuffXSpeedIncrease {

    public BuffTauntSurge(final LogicModule l, final int d, final Player o) {
        super(l, d, o);
    }

    @Override
    public double getXSpeedIncrease() {
        return getOwner().getSkill(Globals.SWORD_TAUNT_SURGE).getBaseValue();
    }

}
