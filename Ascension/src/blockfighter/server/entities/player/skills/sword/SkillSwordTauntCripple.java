package blockfighter.server.entities.player.skills.sword;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.skills.SkillPassive;
import blockfighter.shared.Globals;

public class SkillSwordTauntCripple extends SkillPassive {

    public static final byte SKILL_CODE = Globals.SWORD_TAUNT_CRIPPLE;

    public SkillSwordTauntCripple(final LogicModule l) {
        super(l);
    }

}
