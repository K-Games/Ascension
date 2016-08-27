package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;

public class SkillShieldReflect extends Skill {

    public SkillShieldReflect() {
        this.icon = Globals.SKILL_ICON[SHIELD_REFLECT];
        this.skillCode = SHIELD_REFLECT;
        this.maxCooldown = 15000;
        this.reqWeapon = Globals.ITEM_SHIELD;
        this.skillName = "Reflect Damage";
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "For 3 seconds, when you take damage, you explode",
            "dealing a portion of damage taken."
        };
        this.skillCurLevelDesc = new String[]{
            "Deals " + (40 + this.level * 2) + "% of damage taken."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + (40 + (this.level + 1) * 2) + "% of damage taken."
        };
        this.maxBonusDesc = new String[]{
            "You reflect 40% of damage taken by other players."
        };
    }
}
