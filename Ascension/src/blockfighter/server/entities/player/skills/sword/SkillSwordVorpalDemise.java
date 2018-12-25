package blockfighter.server.entities.player.skills.sword;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.skills.SkillPassive;
import blockfighter.shared.Globals;

public class SkillSwordVorpalDemise extends SkillPassive {

    public static final byte SKILL_CODE = Globals.SWORD_VORPAL_DEMISE;

    public SkillSwordVorpalDemise(final LogicModule l) {
        super(l);
    }

}
