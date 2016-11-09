package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;

public class SkillPassiveTough extends Skill {

    public SkillPassiveTough() {
        this.isPassive = true;
        this.skillName = "Tough Skin";
        this.skillCode = PASSIVE_TOUGH;
        this.icon = Globals.SKILL_ICON[PASSIVE_TOUGH];
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Boost your natural resilience to damage."
        };
        this.skillCurLevelDesc = new String[]{
            "Take " + this.df.format(6 + this.level * 0.3) + "% reduced damage."
        };
        this.skillNextLevelDesc = new String[]{
            "Take " + this.df.format(6 + (this.level + 1) * 0.3) + "% reduced damage."
        };
    }
}
