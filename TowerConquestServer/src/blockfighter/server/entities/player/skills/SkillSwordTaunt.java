package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffSwordTaunt;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjSwordTaunt;
import blockfighter.server.net.PacketSender;

public class SkillSwordTaunt extends Skill {

    /**
     * Constructor for Sword Skill Taunt.
     *
     * @param l
     */
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
        final int duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (duration == 0) {
            if (player.isSkillMaxed(Skill.SWORD_TAUNT)) {
                player.queueBuff(new BuffSwordTaunt(this.logic, 10000, 0.2, 0.2, player));
                PacketSender.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_TAUNTBUFF, player.getKey());
            }
            PacketSender.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_TAUNTAURA1, player.getKey());
        }
        if (Globals.hasPastDuration(duration, 50) && player.getSkillCounter() < 1) {
            player.incrementSkillCounter();
            final ProjSwordTaunt proj = new ProjSwordTaunt(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_TAUNT, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    player.getFacing());
        }
        player.updateSkillEnd(duration, this.endDuration, false, false);
    }

}
