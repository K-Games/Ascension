package blockfighter.server.entities.player.skills;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveRevive extends Skill {

	public SkillPassiveRevive() {
		this.skillCode = PASSIVE_REVIVE;
		this.maxCooldown = 120000;
	}

}
