package blockfighter.server.entities.player.skills;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveBarrier extends Skill {

    public SkillPassiveBarrier() {
        skillCode = PASSIVE_BARRIER;
        maxCooldown = 30000;
    }

}
