package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffSwordTaunt;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjSwordTaunt;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;

public class SkillSwordTaunt extends Skill {

    private ProjSwordTaunt proj;

    public SkillSwordTaunt(final LogicModule l) {
        super(l);
        this.skillCode = SWORD_TAUNT;
        this.maxCooldown = 25000;
        this.reqWeapon = Globals.ITEM_SWORD;
        this.endDuration = 350;
        this.playerState = Player.PLAYER_STATE_SWORD_TAUNT;
        this.reqEquipSlot = Globals.ITEM_WEAPON;
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (player.getSkillCounter() == 0) {
            player.incrementSkillCounter();
            if (player.isSkillMaxed(Skill.SWORD_TAUNT)) {
                player.queueBuff(new BuffSwordTaunt(this.logic, 5000, 0.2, 0.2, player));
                PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SWORD_TAUNTBUFF, player.getKey());
            }
            proj = new ProjSwordTaunt(this.logic, player, player.getX(), player.getY());
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SWORD_TAUNTAURA1, player.getKey());
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SWORD_TAUNT, player.getX(), player.getY(), player.getFacing());
        }
        if (Globals.hasPastDuration(duration, 100) && player.getSkillCounter() == 1) {
            player.incrementSkillCounter();
            this.logic.queueAddProj(proj);

        }
        player.updateSkillEnd(duration, this.endDuration, false, false);
    }

}
