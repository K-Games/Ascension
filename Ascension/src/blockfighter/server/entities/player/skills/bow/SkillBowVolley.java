package blockfighter.server.entities.player.skills.bow;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.proj.ProjBowVolley;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;

public class SkillBowVolley extends Skill {

    public static final byte SKILL_CODE = Globals.BOW_VOLLEY;

    public SkillBowVolley(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        final int numHits = 20;
        if (Globals.hasPastDuration(duration, player.getSkillCounter() * 100) && player.getSkillCounter() < numHits) {
            final ProjBowVolley proj = new ProjBowVolley(this.logic, player, player.getX(),
                    player.getY());
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic, Globals.Particles.BOW_VOLLEY_ARROW.getParticleCode(), player.getX(), proj.getHitbox()[0].getY(),
                    player.getFacing());
            PacketSender.sendParticle(this.logic, Globals.Particles.BOW_VOLLEY_BOW.getParticleCode(), player.getKey(), player.getFacing());
            player.incrementSkillCounter();
            PacketSender.sendSFX(this.logic, Globals.SFXs.VOLLEY.getSfxCode(), player.getX(), player.getY());
        }
        player.updateSkillEnd(duration, getSkillData().getSkillDuration(), true, true);
    }

    @Override
    public void updatePlayerAnimState(Player player) {
        final long frameDuration = Globals.nsToMs(this.logic.getTime() - player.getLastFrameTime());
        player.setAnimState(Globals.PLAYER_ANIM_STATE_ATTACKBOW);
        if (player.getFrame() < 3 && frameDuration >= 30) {
            player.setFrame((byte) (player.getFrame() + 1));
            player.setLastFrameTime(this.logic.getTime());
        }
    }
}
