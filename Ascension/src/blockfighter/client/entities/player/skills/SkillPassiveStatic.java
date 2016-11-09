package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;

public class SkillPassiveStatic extends Skill {

    public SkillPassiveStatic() {
        this.isPassive = true;
        this.skillName = "Static Charge";
        this.skillCode = PASSIVE_STATIC;
        this.icon = Globals.SKILL_ICON[PASSIVE_STATIC];
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Shock a nearby enemy when you deal damage."
        };
        this.skillCurLevelDesc = new String[]{
            "20% Chance to shock a nearby enemy for " + (50 + (this.level * 15)) + "% of Armor as damage."
        };
        this.skillNextLevelDesc = new String[]{
            "20% Chance to shock a nearby enemy for " + (50 + ((this.level + 1) * 15)) + "% of Armor as damage."
        };
    }
}
