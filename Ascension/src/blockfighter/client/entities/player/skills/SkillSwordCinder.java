package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;

public class SkillSwordCinder extends Skill {

    public SkillSwordCinder() {
        this.icon = Globals.SKILL_ICON[SWORD_CINDER];
        this.skillCode = SWORD_CINDER;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_SWORD;
        this.skillName = "Firebrand";
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Deal a powerful hit that afflicts enemies with Burn."
        };
        this.skillCurLevelDesc = new String[]{
            "Deals " + (450 + this.level * 20) + "% damage.",
            "Burning enemies take " + this.level + "% increased damage."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + (450 + (this.level + 1) * 20) + "% damage.",
            "Burning enemies take " + (this.level + 1) + "% increased damage."
        };
        this.maxBonusDesc = new String[]{
            "Burn also deals 375% damage per second for 4 seconds.",
            "Firebrand has 100% Critical Hit Chance."
        };
    }
}
