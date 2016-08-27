package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;

public class SkillSwordSlash extends Skill {

    public SkillSwordSlash() {
        this.icon = Globals.SKILL_ICON[SWORD_SLASH];
        this.skillCode = SWORD_SLASH;
        this.maxCooldown = 400;
        this.reqWeapon = Globals.ITEM_SWORD;
        this.skillName = "Rend";
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Slash 3 times."
        };

        this.skillCurLevelDesc = new String[]{
            "Deals " + (4 * this.level + 100) + "% damage per hit."
        };

        this.skillNextLevelDesc = new String[]{
            "Deals " + (4 * (this.level + 1) + 100) + "% damage per hit."
        };

        this.maxBonusDesc = new String[]{
            "Take 10% less damage for 2 seconds."
        };
    }
}
