package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBowFrost;
import blockfighter.server.net.PacketSender;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowFrost extends Skill {

    /**
     * Constructor for Bow Skill Frost Bind.
     *
     * @param l
     */
    public SkillBowFrost(final LogicModule l) {
        super(l);
        this.skillCode = BOW_FROST;
        this.maxCooldown = 22000;
        this.reqWeapon = Globals.ITEM_BOW;
    }

    @Override
    public void updateSkillUse(Player player) {
        final int duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        final int numHits = player.isSkillMaxed(Skill.BOW_FROST) ? 3 : 1;
        if (Globals.hasPastDuration(duration, 160 + player.getSkillCounter() * 90) && player.getSkillCounter() < numHits) {
            player.incrementSkillCounter();
            final ProjBowFrost proj = new ProjBowFrost(this.logic, player, player.getX(), player.getY(), false);
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic.getRoom(), Globals.PARTICLE_BOW_FROSTARROW, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    player.getFacing());
        }

        player.updateSkillEnd(duration, 380, false, false);
    }

}
