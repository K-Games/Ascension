package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;

public class SkillSwordTaunt extends Skill {

    public SkillSwordTaunt() {
        this.icon = Globals.SKILL_ICON[SWORD_TAUNT];
        this.skillCode = SWORD_TAUNT;
        this.maxCooldown = 25000;
        this.reqWeapon = Globals.ITEM_SWORD;
        this.skillName = "Aggression";
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Deal an immense deadly blow."
        };
        this.skillCurLevelDesc = new String[]{
            "Deals " + (20 * this.level + 800) + "% damage."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + (20 * (this.level + 1) + 800) + "% damage."
        };
        this.maxBonusDesc = new String[]{
            "Take 20% less damage for 5 seconds.",
            "Deal 20% increased damage for 5 seconds."
        };
    }
}
