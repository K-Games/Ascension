package blockfighter.server.entities.player.skills.sword;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.skills.SkillPassive;
import blockfighter.shared.Globals;

public class SkillSwordVorpalGhost extends SkillPassive {

    public static final byte SKILL_CODE = Globals.SWORD_VORPAL_GHOST;

    public SkillSwordVorpalGhost(final LogicModule l) {
        super(l);
    }

}
