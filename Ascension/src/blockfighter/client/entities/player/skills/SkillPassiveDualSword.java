package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;

public class SkillPassiveDualSword extends Skill {

    public SkillPassiveDualSword() {
        this.isPassive = true;
        this.skillCode = PASSIVE_DUALSWORD;
        this.skillName = "Dual Wield Mastery";
        this.icon = Globals.SKILL_ICON[PASSIVE_DUALSWORD];
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "When equipped with 2 Swords you gain additional",
            "Critical Hit Chance and take reduced damage."
        };
        this.skillCurLevelDesc = new String[]{
            "Additional " + this.df.format(4 + this.level * 0.2) + "% Critical Hit Chance.",
            "Take " + this.df.format(this.level) + "% reduced damage."
        };
        this.skillNextLevelDesc = new String[]{
            "Additional " + this.df.format(4 + (this.level + 1) * 0.2) + "% Critical Hit Chance.",
            "Take " + this.df.format(this.level + 1) + "% reduced damage."
        };
    }
}
