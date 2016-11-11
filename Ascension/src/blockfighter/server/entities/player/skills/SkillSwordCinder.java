package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjSwordCinder;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;

public class SkillSwordCinder extends Skill {

    private ProjSwordCinder proj;

    public SkillSwordCinder(final LogicModule l) {
        super(l);
        this.skillCode = SWORD_CINDER;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_SWORD;
        this.endDuration = 250;
        this.playerState = Player.PLAYER_STATE_SWORD_CINDER;
        this.reqEquipSlot = Globals.ITEM_WEAPON;
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (player.getSkillCounter() == 0) {
            player.incrementSkillCounter();
            proj = new ProjSwordCinder(this.logic, player, player.getX(), player.getY());
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SWORD_CINDER, player.getX(), player.getY(), player.getFacing());
        }
        if (Globals.hasPastDuration(duration, 100) && player.getSkillCounter() == 1) {
            player.incrementSkillCounter();
            this.logic.queueAddProj(proj);
        }
        player.updateSkillEnd(duration, this.endDuration, true, false);
    }
}
