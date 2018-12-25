package blockfighter.server.entities.player.skills.bow;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.proj.ProjBowStorm;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;

public class SkillBowStorm extends Skill {

    public static final byte SKILL_CODE = Globals.BOW_STORM;

    public SkillBowStorm(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (Globals.hasPastDuration(duration, 100) && player.getSkillCounter() == 0) {
            player.incrementSkillCounter();
            final ProjBowStorm proj = new ProjBowStorm(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic, Globals.Particles.BOW_STORM_EMITTER.getParticleCode(), player.getX(), player.getY(), player.getFacing());
        }
        player.updateSkillEnd(duration, this.getSkillData().getSkillDuration(), false, false);
    }

    @Override
    public void updatePlayerAnimState(Player player) {
        final long frameDuration = Globals.nsToMs(this.logic.getTime() - player.getLastFrameTime());
        player.setAnimState(Globals.PLAYER_ANIM_STATE_ATTACKBOW);
        if (player.getFrame() < 7 && frameDuration >= 30) {
            player.setFrame((byte) (player.getFrame() + 1));
            player.setLastFrameTime(this.logic.getTime());
        }
    }
}
