package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.items.Items;
import blockfighter.server.entities.player.Player;
import blockfighter.shared.Globals;

public class SkillPassiveBowMastery extends Skill {

    public SkillPassiveBowMastery(final LogicModule l) {
        super(l);
        this.skillCode = PASSIVE_BOWMASTERY;
        this.isPassive = true;
    }

    @Override
    public boolean canCast(final Player player) {
        return Items.getItemType(player.getEquips()[Globals.ITEM_WEAPON]) == Globals.ITEM_BOW
                && Items.getItemType(player.getEquips()[Globals.ITEM_OFFHAND]) == Globals.ITEM_ARROW;
    }
}
