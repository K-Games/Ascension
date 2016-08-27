package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;

public class SkillSwordVorpal extends Skill {

    public SkillSwordVorpal() {
        this.icon = Globals.SKILL_ICON[SWORD_VORPAL];
        this.skillCode = SWORD_VORPAL;
        this.maxCooldown = 14000;
        this.reqWeapon = Globals.ITEM_SWORD;
        this.skillName = "Vorpal Strike";
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Stab rapidly 3 times."
        };
        this.skillCurLevelDesc = new String[]{
            "Deals " + (6 * this.level + 145) + "% damage per hit.",
            "Critical Hits deal additional +" + (3 * this.level + 40) + "% Critical Damage."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + (6 * (this.level + 1) + 145) + "% damage per hit.",
            "Critical Hits deal additional +" + (3 * (this.level + 1) + 40) + "% Critical Damage."
        };
        this.maxBonusDesc = new String[]{
            "This attack has +30% Critical Hit Chance.",
            "Stab rapidly hit 5 times."
        };
    }
}
