package blockfighter.server.entities.player.skills.sword;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.proj.ProjSwordVorpal;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;

public class SkillSwordVorpal extends Skill {

    public static final byte SKILL_CODE = Globals.SWORD_VORPAL;

    private double projX, projY, destX;
    private boolean dashFinished = false;

    public SkillSwordVorpal(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());

        int skillTime = 50, numHits = player.isSkillMaxed(Globals.SWORD_VORPAL) ? 5 : 3;
        final int dashDistance = 300, dashDuration = 120;
        if (Globals.hasPastDuration(duration, 100) && player.getSkillCounter() == 0) {
            this.destX = player.getX() + ((player.getFacing() == Globals.RIGHT) ? 1 : -1) * dashDistance;
            this.dashFinished = false;
            this.projX = player.getX();
            this.projY = player.getY();
            player.incrementSkillCounter();
        }

        if (!dashFinished && Globals.hasPastDuration(duration, 100 + dashDuration)) {
            this.dashFinished = true;
            player.setHyperStance(false);
            player.setXSpeed(0);
            player.setPos(this.destX, player.getY());
        } else if (!dashFinished && player.getSkillCounter() > 0) {
            player.setXSpeed(((player.getFacing() == Globals.RIGHT) ? 1 : -1) * dashDistance / (dashDuration / Globals.nsToMs(Globals.SERVER_LOGIC_UPDATE)));
            player.setHyperStance(true);
        }

        if (player.getSkillCounter() > 0 && Globals.hasPastDuration(duration, 100 + skillTime * (player.getSkillCounter() - 1)) && player.getSkillCounter() - 1 < numHits) {
            double randomY = this.projY + (Globals.rng(5) * 5 - 10);
            final ProjSwordVorpal proj = new ProjSwordVorpal(this.logic, player, this.projX, randomY);
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_VORPAL.getParticleCode(), this.projX, randomY, player.getFacing());
            player.incrementSkillCounter();
        }

        player.updateSkillEnd(duration, getSkillData().getSkillDuration(), false, false);
    }

    @Override
    public void updatePlayerAnimState(Player player) {
        final long frameDuration = Globals.nsToMs(this.logic.getTime() - player.getLastFrameTime());
        player.setAnimState(Globals.PLAYER_ANIM_STATE_ATTACK);
        if (frameDuration >= 100 && player.getFrame() == 0 || frameDuration >= 40 && player.getFrame() < 5 && player.getFrame() > 0) {
            player.setFrame((byte) (player.getFrame() + 1));
            player.setLastFrameTime(this.logic.getTime());
        }
    }
}
