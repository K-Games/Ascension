package blockfighter.server.entities.player.skills;

import blockfighter.shared.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBowStorm;
import blockfighter.server.net.PacketSender;

public class SkillBowStorm extends Skill {

    public SkillBowStorm(final LogicModule l) {
        super(l);
        this.skillCode = BOW_STORM;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_BOW;
        this.endDuration = 200;
        this.playerState = Player.PLAYER_STATE_BOW_STORM;
        this.reqEquipSlot = Globals.ITEM_WEAPON;
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (Globals.hasPastDuration(duration, 100) && player.getSkillCounter() == 0) {
            player.incrementSkillCounter();
            final ProjBowStorm proj = new ProjBowStorm(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_BOW_STORM, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    player.getFacing());
        }
        player.updateSkillEnd(duration, this.endDuration, false, false);
    }

}
