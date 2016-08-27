package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;

public class SkillSwordGash extends Skill {

    public SkillSwordGash() {
        this.icon = Globals.SKILL_ICON[SWORD_GASH];
        this.skillCode = SWORD_GASH;
        this.maxCooldown = 500;
        this.reqWeapon = Globals.ITEM_SWORD;
        this.skillName = "Blade Flurry";
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Swing your blade 4 times."
        };
        this.skillCurLevelDesc = new String[]{
            "Deals " + (3 * this.level + 75) + "% damage per hit."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + (3 * (this.level + 1) + 75) + "% damage per hit."
        };
        this.maxBonusDesc = new String[]{
            "Restore 0.25% HP per hit."
        };
    }
}
