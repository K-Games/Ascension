package blockfighter.server.entities.player.skills.bow;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.proj.ProjBowRapid;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;

public class SkillBowRapid extends Skill {

    public static final byte SKILL_CODE = Globals.BOW_RAPID;

    public SkillBowRapid(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        final int numHits = 3;

        if (Globals.hasPastDuration(duration, 150 + player.getSkillCounter() * 150) && player.getSkillCounter() < numHits) {
            if (player.getSkillCounter() != 0) {
                player.setFrame((byte) 2);
            }
            player.incrementSkillCounter();
            double projY = player.getY();
            if (player.getSkillCounter() == 1) {
                projY = player.getY() - 20;
            } else if (player.getSkillCounter() == 3) {
                projY = player.getY() + 20;
            }
            final ProjBowRapid proj = new ProjBowRapid(this.logic, player, player.getX(), projY);
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic, Globals.Particles.BOW_RAPID.getParticleCode(), player.getX(), proj.getHitbox()[0].getY(),
                    player.getFacing());
            PacketSender.sendSFX(this.logic, Globals.SFXs.RAPID.getSfxCode(), player.getX(), player.getY());
        }
        player.updateSkillEnd(duration, getSkillData().getSkillDuration(), true, false);
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
