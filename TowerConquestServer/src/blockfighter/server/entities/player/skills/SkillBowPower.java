package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBowPower;

/**
 *
 * @author Ken Kwan
 */
public class SkillBowPower extends Skill {

    /**
     * Constructor for Bow Skill Power Shot.
     * @param l
     */
    public SkillBowPower(final LogicModule l) {
        super(l);
        this.skillCode = BOW_POWER;
        this.maxCooldown = 16000;
        this.reqWeapon = Globals.ITEM_BOW;
    }

    @Override
    public void updateSkillUse(Player player) {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        if (duration == 0) {
            Player.sendSFX(this.logic.getRoom(), Globals.SFX_POWER2, player.getX(), player.getY());
        }
        if (duration <= 400 && Player.hasPastDuration(duration, player.getSkillCounter() * 50) && player.getSkillCounter() < 6) {
            Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_BOW_POWERCHARGE, player.getX() + ((player.getFacing() == Globals.RIGHT) ? 75 : -75),
                    player.getY() - 215, player.getFacing());
                        player.incrementSkillCounter();
        } else if (Player.hasPastDuration(duration, 800) && player.getSkillCounter() < 7) {
                        player.incrementSkillCounter();
            final ProjBowPower proj = new ProjBowPower(this.logic, this.logic.getNextProjKey(), player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_BOW_POWER, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    player.getFacing());
            Player.sendSFX(this.logic.getRoom(), Globals.SFX_POWER, player.getX(), player.getY());
        }
        player.updateSkillEnd(duration >= 1400 || (!player.isSkillMaxed(Skill.BOW_POWER) && duration < 800 && (player.isStunned() || player.isKnockback())));
    }

}
