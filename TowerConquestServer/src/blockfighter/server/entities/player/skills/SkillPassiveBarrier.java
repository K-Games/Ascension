package blockfighter.server.entities.player.skills;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveBarrier extends Skill {

    public SkillPassiveBarrier() {
        this.skillCode = PASSIVE_BARRIER;
        this.maxCooldown = 30000;
    }

}
