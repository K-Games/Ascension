package blockfighter.server.entities.player.skills;

import blockfighter.shared.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjShieldRoar;
import blockfighter.server.net.PacketSender;

public class SkillShieldRoar extends Skill {

    public SkillShieldRoar(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_ROAR;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_SHIELD;
        this.endDuration = 500;
        this.playerState = Player.PLAYER_STATE_SHIELD_ROAR;
        this.reqEquipSlot = Globals.ITEM_OFFHAND;
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        //Send roar particle
        if (player.getSkillCounter() == 0) {
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_ROAR, player.getKey(), player.getFacing());
            player.incrementSkillCounter();
        }
        //Spawn projectile.
        if (Globals.hasPastDuration(duration, 200) && player.getSkillCounter() == 1) {
            PacketSender.sendScreenShake(player, 8, 8, 200);
            player.incrementSkillCounter();
            final ProjShieldRoar proj = new ProjShieldRoar(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
        }
        player.updateSkillEnd(duration, this.endDuration, false, false);
    }
}
