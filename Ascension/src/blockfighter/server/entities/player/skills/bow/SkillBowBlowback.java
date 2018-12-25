package blockfighter.server.entities.player.skills.bow;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.proj.ProjBowFrost;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;

public class SkillBowBlowback extends Skill {

    public static final byte SKILL_CODE = Globals.BOW_BLOWBACK;

    public SkillBowBlowback(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        final int numHits = (int) (player.isSkillMaxed(Globals.BOW_FROST) ? getCustomValue(2) + 1 : 1);
        if (Globals.hasPastDuration(duration, 160 + player.getSkillCounter() * 90) && player.getSkillCounter() < numHits) {
            player.incrementSkillCounter();
            final ProjBowFrost proj = new ProjBowFrost(this.logic, player, player.getX(), player.getY(), false);
            this.logic.queueAddProj(proj);
            if (player.getSkillCounter() == 1) {
                PacketSender.sendParticle(this.logic, Globals.Particles.BOW_FROSTARROW_EMITTER.getParticleCode(), player.getX(), player.getY(),
                        player.getFacing());
            }
        }

        player.updateSkillEnd(duration, getSkillData().getSkillDuration(), false, false);
    }

}
