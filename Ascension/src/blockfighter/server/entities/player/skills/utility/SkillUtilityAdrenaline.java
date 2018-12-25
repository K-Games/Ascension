package blockfighter.server.entities.player.skills.utility;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffUtilityAdrenaline;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;

public class SkillUtilityAdrenaline extends Skill {

    public static final byte SKILL_CODE = Globals.UTILITY_ADRENALINE;

    public SkillUtilityAdrenaline(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (player.getSkillCounter() == 0) {
            player.incrementSkillCounter();
            PacketSender.sendParticle(this.logic, Globals.Particles.UTILITY_ADRENALINE.getParticleCode(), player.getKey());
            PacketSender.sendSFX(this.logic, Globals.SFXs.FORTIFY.getSfxCode(), player.getX(), player.getY());
        }

        if (Globals.hasPastDuration(duration, getSkillData().getSkillDuration()) && player.getSkillCounter() == 1) {
            player.incrementSkillCounter();
            double buffDuration = getCustomValue(0);
            player.queueBuff(new BuffUtilityAdrenaline(this.logic, (int) buffDuration, getSkillData().getBaseValue() + getSkillData().getMultValue() * player.getSkillLevel(Globals.UTILITY_ADRENALINE), player));
            PacketSender.sendParticle(this.logic, Globals.Particles.UTILITY_ADRENALINE_CLONE_EMITTER.getParticleCode(), player.getKey());
        }

        player.updateSkillEnd(duration, getSkillData().getSkillDuration(), false, false);
    }

    @Override
    public void updatePlayerAnimState(Player player) {
        final long frameDuration = Globals.nsToMs(this.logic.getTime() - player.getLastFrameTime());
        player.setAnimState(Globals.PLAYER_ANIM_STATE_BUFF);
        if (frameDuration >= 30 && player.getFrame() < 4) {
            player.setFrame((byte) (player.getFrame() + 1));
            player.setLastFrameTime(this.logic.getTime());
        }
    }
}
