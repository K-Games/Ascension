package blockfighter.server.entities.player.skills;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveRevive extends Skill {

    public SkillPassiveRevive() {
        skillCode = PASSIVE_REVIVE;
        maxCooldown = 120000;
    }

}
