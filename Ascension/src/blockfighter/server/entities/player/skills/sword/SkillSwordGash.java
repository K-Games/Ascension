package blockfighter.server.entities.player.skills.sword;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.proj.ProjSwordGash;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;

public class SkillSwordGash extends Skill {

    public static final byte SKILL_CODE = Globals.SWORD_GASH;

    public SkillSwordGash(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        final byte numHits = 4;
        if (Globals.hasPastDuration(duration, (100 * player.getSkillCounter())) && player.getSkillCounter() < numHits) {
            player.incrementSkillCounter();
            final ProjSwordGash proj = new ProjSwordGash(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            //PacketSender.sendSFX(this.logic, Globals.SFX_GASH, player.getX(), player.getY());
            switch (player.getSkillCounter()) {
                case 1:
                case 2:
                    PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_GASH1.getParticleCode(), player.getX(), player.getY(),
                            player.getFacing());
                    break;
                case 3:
                case 4:
                    PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_GASH2.getParticleCode(), player.getX(), player.getY(),
                            player.getFacing());
                    break;
            }
        }
        player.updateSkillEnd(duration, getSkillData().getSkillDuration(), true, false);
    }

    @Override
    public void updatePlayerAnimState(Player player) {
        final long frameDuration = Globals.nsToMs(this.logic.getTime() - player.getLastFrameTime());
        player.setAnimState(Globals.PLAYER_ANIM_STATE_ATTACK);
        if (frameDuration >= ((player.getFrame() == 4) ? 150 : 20) && player.getFrame() < 5) {
            player.setFrame((byte) (player.getFrame() + 1));
            player.setLastFrameTime(this.logic.getTime());
        }
    }
}
