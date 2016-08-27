package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;

public class SkillBowVolley extends Skill {

    public SkillBowVolley() {
        this.icon = Globals.SKILL_ICON[BOW_VOLLEY];
        this.skillCode = BOW_VOLLEY;
        this.maxCooldown = 17000;
        this.reqWeapon = Globals.ITEM_BOW;
        this.skillName = "Vortex Bolts";
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Fire 20 bolts over 2 seconds.",
            "Can be interrupted by stuns and knockback."
        };
        this.skillCurLevelDesc = new String[]{
            "Deals " + (85 + 3 * this.level) + "% damage per hit"
        };
        this.skillNextLevelDesc = new String[]{
            "Deals " + (85 + 3 * (this.level + 1)) + "% damage per hit"
        };
        this.maxBonusDesc = new String[]{
            "Each Critical Hit increases damage by 1% for 4 seconds."
        };
    }
}
