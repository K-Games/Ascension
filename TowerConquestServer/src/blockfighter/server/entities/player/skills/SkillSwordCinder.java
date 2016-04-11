package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjSwordCinder;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordCinder extends Skill {

    /**
     * Constructor for Sword Skill Cinder.
     * @param l
     */
    public SkillSwordCinder(final LogicModule l) {
        super(l);
        this.skillCode = SWORD_CINDER;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_SWORD;
    }
    
    @Override
    public void updateCasting(Player player) {
        final int duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (Player.hasPastDuration(duration, 50) && player.getSkillCounter() < 1) {
            player.incrementSkillCounter();
            final ProjSwordCinder proj = new ProjSwordCinder(this.logic, this.logic.getNextProjKey(), player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_CINDER, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    player.getFacing());
        }
        player.updateSkillEnd(duration, 250, true, false);
    }
}
