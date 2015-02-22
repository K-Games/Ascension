package blockfighter.server.entities.player.skills;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveResistance extends Skill {

    public SkillPassiveResistance() {
        skillCode = PASSIVE_RESISTANCE;
        maxCooldown = 35000;
    }

    @Override
    public void setCooldown() {
        super.setCooldown();
        reduceCooldown(1000 * level);
    }
}
