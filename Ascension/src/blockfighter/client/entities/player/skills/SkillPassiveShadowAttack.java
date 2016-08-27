package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;

public class SkillPassiveShadowAttack extends Skill {

    public SkillPassiveShadowAttack() {
        this.skillName = "Piercing Shadows";
        this.skillCode = PASSIVE_SHADOWATTACK;
        this.maxCooldown = 200;
        this.icon = Globals.SKILL_ICON[PASSIVE_SHADOWATTACK];
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "When you deal damage there is a chance a shadow blade",
            "pierces that same target for 50% of the damage dealt."
        };
        this.skillCurLevelDesc = new String[]{
            (20 + this.level) + "% chance to summon a shadow blade."
        };
        this.skillNextLevelDesc = new String[]{
            (20 + (this.level + 1)) + "% chance to summon a shadow blade."
        };
    }
}
