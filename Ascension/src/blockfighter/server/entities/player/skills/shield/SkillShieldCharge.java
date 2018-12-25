package blockfighter.server.entities.player.skills.shield;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.proj.ProjShieldCharge;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;

public class SkillShieldCharge extends Skill {

    public static final byte SKILL_CODE = Globals.SHIELD_CHARGE;

    public SkillShieldCharge(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        player.setXSpeed((player.getFacing() == Globals.RIGHT) ? 35 : -35);
        if (player.getSkillCounter() == 0) {
            final ProjShieldCharge proj = new ProjShieldCharge(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            PacketSender.sendScreenShake(player, 3, 3, 200);
            PacketSender.sendParticle(this.logic, Globals.Particles.SHIELD_CHARGE.getParticleCode(), player.getKey(), player.getFacing());
            player.incrementSkillCounter();
        }
        if (player.updateSkillEnd(duration, getSkillData().getSkillDuration(), false, false)) {
            player.setXSpeed(0);
        }
    }

    @Override
    public void updatePlayerAnimState(Player player) {
        final long frameDuration = Globals.nsToMs(this.logic.getTime() - player.getLastFrameTime());
        player.setAnimState(Globals.PLAYER_ANIM_STATE_ATTACK);
        if (frameDuration >= ((player.getFrame() == 1) ? 4 : 20) && player.getFrame() < 4) {
            player.setFrame((byte) (player.getFrame() + 1));
            player.setLastFrameTime(this.logic.getTime());
        }
    }
}
