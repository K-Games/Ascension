package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveResistance extends Skill {

    public SkillPassiveResistance(final LogicModule l) {
        super(l);
        this.skillCode = PASSIVE_RESIST;
        this.maxCooldown = 35000;
        this.isPassive = true;
    }

    @Override
    public void setCooldown() {
        super.setCooldown();
        reduceCooldown(1000 * this.level);
    }
}
