package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffShieldFortify;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;

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
        this.endDuration = 350;
        this.playerState = Player.PLAYER_STATE_SHIELD_FORTIFY;
    }

    @Override
    public void updateSkillUse(Player player) {
        final int duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (duration == 0) {
            PacketSender.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_FORTIFY, player.getKey());
            Player.sendSFX(this.logic.getRoom(), Globals.SFX_FORTIFY, player.getX(), player.getY());
        }

        if (Globals.hasPastDuration(duration, this.endDuration) && player.getSkillCounter() < 1) {
            player.incrementSkillCounter();
            player.queueBuff(new BuffShieldFortify(this.logic, 5000, 0.01 + 0.005 * player.getSkillLevel(Skill.SHIELD_FORTIFY), player));
            PacketSender.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_FORTIFYBUFF, player.getKey());
        }

        player.updateSkillEnd(duration, this.endDuration, false, false);
    }

}
