package blockfighter.client.entities.player.skills;

import blockfighter.shared.Globals;

public class SkillPassiveResistance extends Skill {

    public SkillPassiveResistance() {
        this.isPassive = true;
        this.skillName = "Resistance";
        this.skillCode = PASSIVE_RESIST;
        this.maxCooldown = 35000;
        this.icon = Globals.SKILL_ICON[PASSIVE_RESIST];
    }

    @Override
    public double getMaxCooldown() {
        return this.maxCooldown - (1000 * this.level);
    }

    @Override
    public void setCooldown() {
        super.setCooldown();
        reduceCooldown(1000 * this.level);
    }

    @Override
    public void updateDesc() {
        this.description = new String[]{
            "When taking damage over 25% of your HP within 2 seconds,",
            "block all damage for the next 2 seconds."
        };
        this.skillCurLevelDesc = new String[]{
            "Reduce cooldown by " + this.level + ((this.level > 1) ? " seconds." : " second.")
        };
        this.skillNextLevelDesc = new String[]{
            "Reduce cooldown by " + (this.level + 1) + (((this.level + 1) > 1) ? " seconds." : " second.")
        };
    }
}
