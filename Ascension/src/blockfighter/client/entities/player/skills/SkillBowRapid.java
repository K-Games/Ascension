package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;

public class SkillBowRapid extends Skill {

    public SkillBowRapid() {
        this.icon = Globals.SKILL_ICON[BOW_RAPID];
        this.skillCode = BOW_RAPID;
        this.maxCooldown = 700;
        this.reqWeapon = Globals.ITEM_BOW;
        this.skillName = "Rapid Fire";
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Fire 3 shots over 0.5 seconds."
        };
        this.skillCurLevelDesc = new String[]{
            "Deals " + (80 + this.level * 2) + "% damage per hit."
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + (80 + (this.level + 1) * 2) + "% damage per hit."
        };
        this.maxBonusDesc = new String[]{
            "Each shot has 50% Chance to deal 2x damage."
        };
    }

}
