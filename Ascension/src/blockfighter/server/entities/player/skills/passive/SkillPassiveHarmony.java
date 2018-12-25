package blockfighter.server.entities.player.skills.passive;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.skills.SkillPassive;
import blockfighter.shared.Globals;

public class SkillPassiveHarmony extends SkillPassive {

    public static final byte SKILL_CODE = Globals.PASSIVE_HARMONY;

    public SkillPassiveHarmony(final LogicModule l) {
        super(l);
    }

}
