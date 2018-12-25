package blockfighter.server.entities.player.skills.utility;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffUtilityDash;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;

public class SkillUtilityDash extends Skill {

    public static final byte SKILL_CODE = Globals.UTILITY_DASH;
    public static final byte PLAYER_STATE = Player.PLAYER_STATE_UTILITY_DASH;

    public SkillUtilityDash(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (!player.isStunned() && !player.isKnockback()) {
            player.setXSpeed((player.getFacing() == Globals.RIGHT) ? 8.5 : -8.5);
        }
        if (player.isSkillMaxed(Globals.UTILITY_DASH) && !player.isInvulnerable()) {
            player.setInvulnerable(true);
        }

        if (player.getSkillCounter() == 0) {
            PacketSender.sendParticle(this.logic, Globals.Particles.UTILITY_DASH_EMITTER.getParticleCode(), player.getKey(), player.getFacing());
            player.setYSpeed(-5.5);
            player.incrementSkillCounter();
        }

        if (player.getSkillCounter() == 1 && duration >= getSkillData().getSkillDuration()) {
            player.incrementSkillCounter();
            player.queueBuff(new BuffUtilityDash(this.logic, getCustomValue(0).intValue(), getSkillData().getBaseValue() + getSkillData().getMultValue() * player.getSkillLevel(Globals.UTILITY_DASH), player));
            PacketSender.sendParticle(this.logic, Globals.Particles.UTILITY_DASH_BUFF_EMITTER.getParticleCode(), player.getKey());
        }
        if (player.updateSkillEnd(duration, getSkillData().getSkillDuration(), true, true)) {
            player.setInvulnerable(false);
        }
    }

    @Override
    public void updatePlayerAnimState(Player player) {
        final long frameDuration = Globals.nsToMs(this.logic.getTime() - player.getLastFrameTime());
        player.setAnimState(Globals.PLAYER_ANIM_STATE_ROLL);
        if (frameDuration >= 40 && player.getFrame() < 9) {
            player.setFrame((byte) (player.getFrame() + 1));
            player.setLastFrameTime(this.logic.getTime());
        }
    }
}
