package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;

public class SkillPassiveShieldMastery extends Skill {

    public SkillPassiveShieldMastery() {
        this.isPassive = true;
        this.skillName = "Defender Mastery";
        this.skillCode = PASSIVE_SHIELDMASTERY;
        this.icon = Globals.SKILL_ICON[PASSIVE_SHIELDMASTERY];
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "When equipped with a Sword and Shield you deal",
            "additional damage and take reduced damage."
        };
        this.skillCurLevelDesc = new String[]{
            "Deal additional " + this.df.format(9 + this.level * 0.2) + "% damage.",
            "Take " + this.df.format(5 + this.level * 0.5) + "% reduced damage."
        };
        this.skillNextLevelDesc = new String[]{
            "Deal additional " + this.df.format(9 + (this.level + 1) * 0.2) + "% damage.",
            "Take " + this.df.format(5 + (this.level + 1) * 0.5) + "% reduced damage."
        };
    }
}
