package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjShieldCharge;
import blockfighter.server.net.PacketSender;

public class SkillShieldCharge extends Skill {

    public SkillShieldCharge(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_CHARGE;
        this.maxCooldown = 17000;
        this.reqWeapon = Globals.ITEM_SHIELD;
        this.endDuration = 750;
        this.playerState = Player.PLAYER_STATE_SHIELD_CHARGE;
        this.reqEquipSlot = Globals.ITEM_OFFHAND;
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.room.getTime() - player.getSkillCastTime());
        player.setXSpeed((player.getFacing() == Globals.RIGHT) ? 18 : -18);
        if (player.getSkillCounter() == 0) {
            final ProjShieldCharge proj = new ProjShieldCharge(this.room, player, player.getX(), player.getY());
            this.room.queueAddProj(proj);
            PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_SHIELD_CHARGE, player.getKey(), player.getFacing());
            player.incrementSkillCounter();
        }
        player.updateSkillEnd(duration, this.endDuration, false, false);
    }
}
