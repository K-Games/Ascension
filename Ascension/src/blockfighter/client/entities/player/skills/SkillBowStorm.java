package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;

public class SkillBowStorm extends Skill {

    public SkillBowStorm() {
        this.icon = Globals.SKILL_ICON[BOW_STORM];
        this.skillCode = BOW_STORM;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_BOW;
        this.skillName = "Bombardment";
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Launch a hail of arrows, damaging the area in front."
        };
        this.skillCurLevelDesc = new String[]{
            "Deals " + (300 + 30 * this.level) + "% damage per second for 5 seconds."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + (300 + 30 * (this.level + 1)) + "% damage per second for 5 seconds."
        };
        this.maxBonusDesc = new String[]{
            "Critical Hits have bonus +500% Critical Hit Damage."
        };
    }

}
