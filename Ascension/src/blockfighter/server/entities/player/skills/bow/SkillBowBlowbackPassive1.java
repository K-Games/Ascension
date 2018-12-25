package blockfighter.server.entities.player.skills.bow;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.skills.SkillPassive;
import blockfighter.shared.Globals;

public class SkillBowBlowbackPassive1 extends SkillPassive {

    public static final byte SKILL_CODE = Globals.BOW_BLOWBACK_PASSIVE1;

    public SkillBowBlowbackPassive1(final LogicModule l) {
        super(l);
    }

}
