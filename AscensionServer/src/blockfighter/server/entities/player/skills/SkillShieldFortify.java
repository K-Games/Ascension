package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffShieldFortify;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;

public class SkillShieldFortify extends Skill {

    public SkillShieldFortify(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_FORTIFY;
        this.maxCooldown = 24000;
        this.endDuration = 350;
        this.playerState = Player.PLAYER_STATE_SHIELD_FORTIFY;
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (player.getSkillCounter() == 0) {
            player.incrementSkillCounter();
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_FORTIFY, player.getKey());
            PacketSender.sendSFX(this.logic.getRoom().getRoomNumber(), Globals.SFX_FORTIFY, player.getX(), player.getY());
        }

        if (Globals.hasPastDuration(duration, this.endDuration) && player.getSkillCounter() == 1) {
            player.incrementSkillCounter();
            player.queueBuff(new BuffShieldFortify(this.logic, 5000, 0.01 + 0.005 * player.getSkillLevel(Skill.SHIELD_FORTIFY), player));
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_FORTIFYBUFF, player.getKey());
        }

        player.updateSkillEnd(duration, this.endDuration, false, false);
    }

}
