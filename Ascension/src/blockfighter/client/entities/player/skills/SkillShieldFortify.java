package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;

public class SkillShieldFortify extends Skill {

    public SkillShieldFortify() {
        this.icon = Globals.SKILL_ICON[SHIELD_FORTIFY];
        this.skillCode = SHIELD_FORTIFY;
        this.maxCooldown = 24000;
        this.skillName = "Fortify";
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Reduce damage taken for 5 seconds."
        };
        this.skillCurLevelDesc = new String[]{
            "Reduce damage taken by " + this.df.format(0.5 * this.level + 1) + "%."
        };
        this.skillNextLevelDesc = new String[]{
            "Reduce damage taken by " + this.df.format(0.5 * (this.level + 1) + 1) + "%."
        };
        this.maxBonusDesc = new String[]{
            "Restore 7.5% HP over 5 seconds."
        };
    }
}
