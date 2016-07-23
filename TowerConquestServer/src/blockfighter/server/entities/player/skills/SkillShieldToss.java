package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjShieldToss;
import blockfighter.server.net.PacketSender;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldToss extends Skill {

    /**
     * Constructor for Shield Skill Shield Throw.
     *
     * @param l
     */
    public SkillShieldToss(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_TOSS;
        this.maxCooldown = 13000;
        this.reqWeapon = Globals.ITEM_SHIELD;
    }

    @Override
    public void updateSkillUse(Player player) {
        final int duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        final int numHits = player.isSkillMaxed(Skill.SHIELD_TOSS) ? 3 : 1;

        if (Globals.hasPastDuration(duration, 100 + player.getSkillCounter() * 200) && player.getSkillCounter() < numHits) {
            player.incrementSkillCounter();
            final ProjShieldToss proj = new ProjShieldToss(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_TOSS, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    player.getFacing());
        }
        player.updateSkillEnd(duration, 700, true, false);
    }
}
