package blockfighter.server.entities.player.skills;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveResistance extends Skill {

	public SkillPassiveResistance() {
		this.skillCode = PASSIVE_RESIST;
		this.maxCooldown = 35000;
	}

	@Override
	public void setCooldown() {
		super.setCooldown();
		reduceCooldown(1000 * this.level);
	}
}
