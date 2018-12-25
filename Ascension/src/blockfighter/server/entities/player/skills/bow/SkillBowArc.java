package blockfighter.server.entities.player.skills.bow;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.proj.ProjBowArc;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;

public class SkillBowArc extends Skill {

    public static final byte SKILL_CODE = Globals.BOW_ARC;

    public SkillBowArc(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        final int numHits = 3;
        if (player.getSkillCounter() < numHits && Globals.hasPastDuration(duration, 100 + player.getSkillCounter() * 50)) {
            player.incrementSkillCounter();
            final ProjBowArc proj = new ProjBowArc(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            if (player.getSkillCounter() == 1) {
                PacketSender.sendParticle(this.logic, Globals.Particles.BOW_ARC.getParticleCode(), player.getX(), player.getY(), player.getFacing());
                PacketSender.sendSFX(this.logic, Globals.SFXs.SARC.getSfxCode(), player.getX(), player.getY());
            }
        }
        player.updateSkillEnd(duration, getSkillData().getSkillDuration(), false, false);
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
