package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;

public class SkillBowFrost extends Skill {

    public SkillBowFrost() {
        this.icon = Globals.SKILL_ICON[BOW_FROST];
        this.skillCode = BOW_FROST;
        this.maxCooldown = 22000;
        this.reqWeapon = Globals.ITEM_BOW;
        this.skillName = "Frost Bind";
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Shoot a frost arrow freezing targets hit for 1.5 second."
        };
        this.skillCurLevelDesc = new String[]{
            "Deals " + (100 + 20 * this.level) + "% damage."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + (100 + 20 * (this.level + 1)) + "% damage."
        };
        this.maxBonusDesc = new String[]{
            "Freeze now lasts for 2.5 seconds.",
            "Additional 2 shots that deals 250% damage."
        };
    }
}
