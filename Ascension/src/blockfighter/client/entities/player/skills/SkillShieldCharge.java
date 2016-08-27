package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;

public class SkillShieldCharge extends Skill {

    public SkillShieldCharge() {
        this.icon = Globals.SKILL_ICON[SHIELD_CHARGE];
        this.skillCode = SHIELD_CHARGE;
        this.maxCooldown = 17000;
        this.reqWeapon = Globals.ITEM_SHIELD;
        this.skillName = "Overwhelm";
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Charge forward with your shield.",
            "Any enemies hit while charging will take damage."
        };
        this.skillCurLevelDesc = new String[]{
            "Deals " + (150 + this.level * 20) + "% damage."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + (150 + (this.level + 1) * 20) + "% damage."
        };
        this.maxBonusDesc = new String[]{
            "Stun enemies hit for 1 second."
        };
    }
}
