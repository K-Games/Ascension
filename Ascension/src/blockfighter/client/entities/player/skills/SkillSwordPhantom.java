package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;

public class SkillSwordPhantom extends Skill {

    public SkillSwordPhantom() {
        this.icon = Globals.SKILL_ICON[SWORD_PHANTOM];
        this.skillCode = SWORD_PHANTOM;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_SWORD;
        this.skillName = "Phantom Reaper";
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "Teleport multiple times to a random enemy",
            "within a 350 radius and strike in their direction.",
            "Invulnerable during the skill duration."
        };

        this.skillCurLevelDesc = new String[]{
            "Perform " + (5 + (this.level / 2)) + " attacks for " + (75 + (this.level * 2)) + "% damage."
        };

        this.skillNextLevelDesc = new String[]{
            "Perform " + (5 + ((this.level + 1) / 2)) + " attacks for " + (75 + ((this.level + 1) * 2)) + "% damage."
        };

        this.maxBonusDesc = new String[]{
            "IN DEVELOPMENT"
        };
    }
}
