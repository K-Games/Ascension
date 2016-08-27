package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;

public class SkillPassiveBarrier extends Skill {

    public SkillPassiveBarrier() {
        this.isPassive = true;
        this.skillCode = PASSIVE_BARRIER;
        this.maxCooldown = 30000;
        this.skillName = "Barrier";
        this.icon = Globals.SKILL_ICON[PASSIVE_BARRIER];
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "After taking damage over 50% of HP, gain a barrier",
            "that can absorb up to a percentage of HP in damage."
        };
        this.skillCurLevelDesc = new String[]{
            "Absorbs up to " + this.df.format(10 + this.level * 0.5) + "% of Max HP."
        };
        this.skillNextLevelDesc = new String[]{
            "Absorbs up to " + this.df.format(10 + (this.level + 1) * 0.5) + "% of Max HP."
        };
    }
}
