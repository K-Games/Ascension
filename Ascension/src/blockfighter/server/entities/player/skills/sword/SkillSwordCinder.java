package blockfighter.server.entities.player.skills.sword;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.proj.ProjSwordCinder;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;

public class SkillSwordCinder extends Skill {

    private ProjSwordCinder proj;

    public static final byte SKILL_CODE = Globals.SWORD_CINDER;

    public SkillSwordCinder(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (player.getSkillCounter() == 0) {
            player.incrementSkillCounter();
            proj = new ProjSwordCinder(this.logic, player, player.getX(), player.getY());
            PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_CINDER.getParticleCode(), player.getX(), player.getY(), player.getFacing());
        }
        if (Globals.hasPastDuration(duration, 100) && player.getSkillCounter() == 1) {
            player.incrementSkillCounter();
            this.logic.queueAddProj(proj);
        }
        player.updateSkillEnd(duration, getSkillData().getSkillDuration(), true, false);
    }

    @Override
    public void updatePlayerAnimState(Player player) {
        final long frameDuration = Globals.nsToMs(this.logic.getTime() - player.getLastFrameTime());
        player.setAnimState(Globals.PLAYER_ANIM_STATE_ATTACK);
        if (frameDuration >= ((player.getFrame() == 4) ? 40 : 30) && player.getFrame() < 5) {
            player.setFrame((byte) (player.getFrame() + 1));
            player.setLastFrameTime(this.logic.getTime());
        }
    }
}
