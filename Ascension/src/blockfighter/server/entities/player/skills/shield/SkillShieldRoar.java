package blockfighter.server.entities.player.skills.shield;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.proj.ProjShieldRoar;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;

public class SkillShieldRoar extends Skill {

    public static final byte SKILL_CODE = Globals.SHIELD_ROAR;

    public SkillShieldRoar(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        //Send roar particle
        if (player.getSkillCounter() == 0) {
            PacketSender.sendParticle(this.logic, Globals.Particles.SHIELD_ROAR.getParticleCode(), player.getKey(), player.getFacing());
            player.incrementSkillCounter();
        }
        //Spawn projectile.
        if (Globals.hasPastDuration(duration, 200) && player.getSkillCounter() == 1) {
            PacketSender.sendScreenShake(player, 8, 8, 200);
            player.incrementSkillCounter();
            final ProjShieldRoar proj = new ProjShieldRoar(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
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
