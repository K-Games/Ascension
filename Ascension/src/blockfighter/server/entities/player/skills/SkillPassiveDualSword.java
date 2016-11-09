package blockfighter.server.entities.player.skills;

import blockfighter.shared.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.items.Items;
import blockfighter.server.entities.player.Player;

public class SkillPassiveDualSword extends Skill {

    public SkillPassiveDualSword(final LogicModule l) {
        super(l);
        this.skillCode = PASSIVE_DUALSWORD;
        this.isPassive = true;
    }

    @Override
    public boolean canCast(final Player player) {
        return Items.getItemType(player.getEquips()[Globals.ITEM_WEAPON]) == Globals.ITEM_SWORD
                && Items.getItemType(player.getEquips()[Globals.ITEM_OFFHAND]) == Globals.ITEM_SWORD;
    }
}
