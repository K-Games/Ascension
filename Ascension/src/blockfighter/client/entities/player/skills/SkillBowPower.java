package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;

public class SkillBowPower extends Skill {

    public SkillBowPower() {
        this.icon = Globals.SKILL_ICON[BOW_POWER];
        this.skillCode = BOW_POWER;
        this.maxCooldown = 16000;
        this.reqWeapon = Globals.ITEM_BOW;
        this.skillName = "Cannon Fire";
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Charge a powerful shot for 0.8 seconds.",
            "Can be interrupted by stuns and knockback."
        };
        this.skillCurLevelDesc = new String[]{
            "Deals " + (500 + 100 * this.level) + "% damage."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + (500 + 100 * (this.level + 1)) + "% damage."
        };
        this.maxBonusDesc = new String[]{
            "Can no longer be interrupted.",
            "Critical Hits deal +300% Critical Hit damage."
        };
    }
}
