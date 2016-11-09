package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;

public class SkillShieldRoar extends Skill {

    public SkillShieldRoar() {
        this.icon = Globals.SKILL_ICON[SHIELD_ROAR];
        this.skillCode = SHIELD_ROAR;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_SHIELD;
        this.skillName = "Hellion Roar";
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Send enemies flying with a ferocious roar."
        };
        this.skillCurLevelDesc = new String[]{
            "Deals " + (this.level * 15 + 150) + "% + Defense multiplied by " + df.format(16 * (1.5 + 0.15 * this.level)) + " damage."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + ((this.level + 1) * 15 + 150) + "% + Defense multiplied by " + df.format(16 * (1.5 + 0.15 * (this.level + 1))) + " damage."
        };
        this.maxBonusDesc = new String[]{
            "Enemies are stunned for 2 seconds."
        };
    }
}
