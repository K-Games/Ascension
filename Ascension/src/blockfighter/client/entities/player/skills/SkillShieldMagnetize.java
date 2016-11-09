package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;

public class SkillShieldMagnetize extends Skill {

    public SkillShieldMagnetize() {
        this.icon = Globals.SKILL_ICON[SHIELD_MAGNETIZE];
        this.skillCode = SHIELD_MAGNETIZE;
        this.maxCooldown = 15000;
        this.reqWeapon = Globals.ITEM_SHIELD;
        this.skillName = "Magnetize";
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Pull enemies within 400 range towards you."
        };
        this.skillCurLevelDesc = new String[]{
            "Deals " + (150 + 15 * this.level) + "% damage + Defense multiplied by " + this.df.format(15 + this.level) + "."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + (150 + 15 * (this.level + 1)) + "% damage + Defense multiplied by " + this.df.format(15 + (this.level + 1)) + "."
        };
        this.maxBonusDesc = new String[]{
            "Deals 3x damage."
        };
    }
}
