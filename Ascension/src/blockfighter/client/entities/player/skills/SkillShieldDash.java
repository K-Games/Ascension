package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;

public class SkillShieldDash extends Skill {

    public SkillShieldDash() {
        this.icon = Globals.SKILL_ICON[SHIELD_DASH];
        this.skillCode = SHIELD_DASH;
        this.maxCooldown = 13000;
        // reqWeapon = Globals.ITEM_SHIELD;
        this.skillName = "Dash";
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Dash a short distance over 0.4 seconds.",
            "Increases damage dealt for 5 seconds."
        };
        this.skillCurLevelDesc = new String[]{
            "Increases damage dealt by " + this.df.format(1 + this.level * 0.3) + "%."
        };
        this.skillNextLevelDesc = new String[]{
            "Increases damage dealt by " + this.df.format(1 + (this.level + 1) * 0.3) + "%."
        };
        this.maxBonusDesc = new String[]{
            "Invulnerable during dash."
        };
    }
}
