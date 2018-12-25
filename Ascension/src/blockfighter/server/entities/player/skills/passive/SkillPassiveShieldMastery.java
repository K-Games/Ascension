package blockfighter.server.entities.player.skills.passive;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.SkillPassive;
import blockfighter.shared.Globals;

public class SkillPassiveShieldMastery extends SkillPassive {

    public static final byte SKILL_CODE = Globals.PASSIVE_SHIELDMASTERY;

    public SkillPassiveShieldMastery(final LogicModule l) {
        super(l);
    }

    @Override
    public boolean canCast(final Player player) {
        return Globals.getEquipType(player.getEquips()[Globals.EQUIP_WEAPON]) == Globals.ITEM_SWORD
                && Globals.getEquipType(player.getEquips()[Globals.EQUIP_OFFHAND]) == Globals.ITEM_SHIELD;
    }

}
