package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffShieldDash;
import blockfighter.server.entities.player.Player;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldDash extends Skill {

    public SkillShieldDash(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_DASH;
        this.maxCooldown = 13000;
    }

    @Override
    public void updateSkillUse(Player player) {
        final int duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (!player.isStunned() && !player.isKnockback()) {
            player.setXSpeed((player.getFacing() == Globals.RIGHT) ? 6 : -6);
        }
        if (player.isSkillMaxed(Skill.SHIELD_DASH) && !player.isInvulnerable()) {
            player.setInvulnerable(true);
        }

        if (duration == 0) {
            Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_DASH, player.getKey(), player.getFacing());
            player.setYSpeed(-4);
        }
        
        if (player.getSkillCounter() == 0 && duration >= 500) {
            player.incrementSkillCounter();
            player.queueBuff(new BuffShieldDash(this.logic, 5000, 0.01 + 0.003 * player.getSkillLevel(Skill.SHIELD_DASH), player));
            Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_DASHBUFF, player.getKey());
        }
        player.updateSkillEnd(duration >= 500 || player.isStunned() || player.isKnockback());
    }
}
