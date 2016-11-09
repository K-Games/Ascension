package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;

public class SkillPassiveVitalHit extends Skill {

    public SkillPassiveVitalHit() {
        this.isPassive = true;
        this.skillCode = PASSIVE_VITALHIT;
        this.skillName = "Vital Hit";
        this.icon = Globals.SKILL_ICON[PASSIVE_VITALHIT];
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Increases Critical Hit Damage."
        };
        this.skillCurLevelDesc = new String[]{
            "Additional " + (10 + this.level * 2) + "% Critical Hit Damage."
        };
        this.skillNextLevelDesc = new String[]{
            "Additional " + (10 + (this.level + 1) * 2) + "% Critical Hit Damage."
        };
    }
}
