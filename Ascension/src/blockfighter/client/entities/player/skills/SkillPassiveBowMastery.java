package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;

public class SkillPassiveBowMastery extends Skill {

    public SkillPassiveBowMastery() {
        this.isPassive = true;
        this.skillCode = PASSIVE_BOWMASTERY;
        this.skillName = "Bow Mastery";
        this.icon = Globals.SKILL_ICON[PASSIVE_BOWMASTERY];
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "When equipped with an Bow and Arrow Enchantment",
            "you gain additional Critical Hit Damage."
        };
        this.skillCurLevelDesc = new String[]{
            "Additional " + (30 + this.level * 4) + "% Critical Hit Damage."
        };
        this.skillNextLevelDesc = new String[]{
            "Additional " + (30 + (this.level + 1) * 4) + "% Critical Hit Damage."
        };
    }
}
