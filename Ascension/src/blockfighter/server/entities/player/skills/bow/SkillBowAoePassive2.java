package blockfighter.server.entities.player.skills.bow;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.skills.SkillPassive;
import blockfighter.shared.Globals;

public class SkillBowAoePassive2 extends SkillPassive {

    public static final byte SKILL_CODE = Globals.BOW_AOE_PASSIVE2;

    public SkillBowAoePassive2(final LogicModule l) {
        super(l);
    }
}
