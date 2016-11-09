package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;

public class SkillPassiveHarmony extends Skill {

    public SkillPassiveHarmony() {
        this.isPassive = true;
        this.skillName = "Vigor";
        this.skillCode = PASSIVE_HARMONY;
        this.icon = Globals.SKILL_ICON[PASSIVE_HARMONY];
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Gain additional damage base on Max HP."
        };
        this.skillCurLevelDesc = new String[]{
            "Gain " + (0.1 + (this.level * 0.1)) + "% of Max HP as bonus damage."
        };
        this.skillNextLevelDesc = new String[]{
            "Gain " + (0.1 + ((this.level + 1) * 0.1)) + "% of Max HP as bonus damage."
        };
    }
}
