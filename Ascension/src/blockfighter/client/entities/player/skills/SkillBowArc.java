package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;

public class SkillBowArc extends Skill {

    public SkillBowArc() {
        this.icon = Globals.SKILL_ICON[BOW_ARC];
        this.skillCode = BOW_ARC;
        this.maxCooldown = 500;
        this.reqWeapon = Globals.ITEM_BOW;
        this.skillName = "Arc Shot";
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Fire 3 shots in an arc."
        };
        this.skillCurLevelDesc = new String[]{
            "Deal " + (80 + this.level * 2) + "% damage per hit."
        };
        this.skillNextLevelDesc = new String[]{
            "Deal " + (80 + (this.level + 1) * 2) + "% damage per hit."
        };
        this.maxBonusDesc = new String[]{
            " Restore 5% damage to HP. Maximum of 10% HP."
        };
    }
}
