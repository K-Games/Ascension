package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffShieldFortify;
import blockfighter.server.entities.player.Player;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldFortify extends Skill {

    /**
     * Constructor for Shield Skill Fortify.
     *
     * @param l
     */
    public SkillShieldFortify(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_FORTIFY;
        this.maxCooldown = 24000;
        // reqWeapon = Globals.ITEM_SHIELD;
    }

    @Override
    public void updateSkillUse(Player player) {
        final int duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (duration == 0) {
            Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_FORTIFY, player.getKey());
            Player.sendSFX(this.logic.getRoom(), Globals.SFX_FORTIFY, player.getX(), player.getY());
        }

        if (player.updateSkillEnd(Player.hasPastDuration(duration, 350) && player.getSkillCounter() < 1)) {
            player.incrementSkillCounter();
            player.queueBuff(new BuffShieldFortify(this.logic, 5000, 0.01 + 0.005 * player.getSkillLevel(Skill.SHIELD_FORTIFY), player));
            Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_FORTIFYBUFF, player.getKey());
        }
    }

}
