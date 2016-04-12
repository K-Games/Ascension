package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBowStorm;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowStorm extends Skill {

    /**
     * Constructor for Bow Skill Arrow Storm.
     *
     * @param l
     */
    public SkillBowStorm(final LogicModule l) {
        super(l);
        this.skillCode = BOW_STORM;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_BOW;
    }

    @Override
    public void updateSkillUse(Player player) {
        final int duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (Player.hasPastDuration(duration, 100) && player.getSkillCounter() < 1) {
            player.incrementSkillCounter();
            final ProjBowStorm proj = new ProjBowStorm(this.logic, this.logic.getNextProjKey(), player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_BOW_STORM, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    player.getFacing());
        }
        player.updateSkillEnd(duration, 200, false, false);
    }

}
