package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;

public class SkillPassiveKeenEye extends Skill {

    public SkillPassiveKeenEye() {
        this.isPassive = true;
        this.skillCode = PASSIVE_KEENEYE;
        this.skillName = "Keen Eye";
        this.icon = Globals.SKILL_ICON[PASSIVE_KEENEYE];
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Increases Critical Hit Chance."
        };
        this.skillCurLevelDesc = new String[]{
            "Additional " + this.df.format(1 + this.level * 0.3) + "% Critical Hit Chance"
        };
        this.skillNextLevelDesc = new String[]{
            "Additional " + this.df.format(1 + (this.level + 1) * 0.3) + "% Critical Hit Chance"
        };
    }
}
