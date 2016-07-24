package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjSwordCinder;
import blockfighter.server.net.PacketSender;

public class SkillSwordCinder extends Skill {

    /**
     * Constructor for Sword Skill Cinder.
     *
     * @param l
     */
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
        final int duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (Globals.hasPastDuration(duration, 50) && player.getSkillCounter() == 0) {
            player.incrementSkillCounter();
            final ProjSwordCinder proj = new ProjSwordCinder(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_CINDER, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    player.getFacing());
        }
        player.updateSkillEnd(duration, this.endDuration, true, false);
    }
}
