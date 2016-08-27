package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;

public class SkillPassiveWillpower extends Skill {

    public SkillPassiveWillpower() {
        this.isPassive = true;
        this.skillCode = PASSIVE_WILLPOWER;
        this.skillName = "Power Of Will";
        this.icon = Globals.SKILL_ICON[PASSIVE_WILLPOWER];
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Increase damage dealt based on your remaining HP.",
            "More remaining HP, grants more damage increase."
        };
        this.skillCurLevelDesc = new String[]{
            "Increase damage dealt up to " + this.df.format(5 + this.level * 0.5) + "%."
        };
        this.skillNextLevelDesc = new String[]{
            "Increase damage dealt up to " + this.df.format(5 + (this.level + 1) * 0.5) + "%."
        };
    }
}
