package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjShieldCharge;
import blockfighter.server.net.PacketSender;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldCharge extends Skill {

    /**
     * Constructor for Shield Skill Charge.
     *
     * @param l
     */
    public SkillShieldCharge(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_CHARGE;
        this.maxCooldown = 17000;
        this.reqWeapon = Globals.ITEM_SHIELD;
    }

    @Override
    public void updateSkillUse(Player player) {
        final int duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        player.setXSpeed((player.getFacing() == Globals.RIGHT) ? 18 : -18);
        if (duration == 0) {
            final ProjShieldCharge proj = new ProjShieldCharge(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_CHARGE, player.getKey(), player.getFacing());
        }
        player.updateSkillEnd(duration, 750, false, false);
    }
}
