package blockfighter.server.entities.player.skills;

import blockfighter.shared.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBowVolley;
import blockfighter.server.net.PacketSender;

public class SkillBowVolley extends Skill {

    public SkillBowVolley(final LogicModule l) {
        super(l);
        this.skillCode = BOW_VOLLEY;
        this.maxCooldown = 17000;
        this.reqWeapon = Globals.ITEM_BOW;
        this.endDuration = 1900;
        this.playerState = Player.PLAYER_STATE_BOW_VOLLEY;
        this.reqEquipSlot = Globals.ITEM_WEAPON;
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        final int numHits = 20;
        if (Globals.hasPastDuration(duration, player.getSkillCounter() * 100) && player.getSkillCounter() < numHits) {
            final ProjBowVolley proj = new ProjBowVolley(this.logic, player, player.getX(),
                    player.getY());
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_BOW_VOLLEYARROW, player.getX(), proj.getHitbox()[0].getY(),
                    player.getFacing());
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_BOW_VOLLEYBOW, player.getKey(), player.getFacing());
            player.incrementSkillCounter();
            PacketSender.sendSFX(this.logic.getRoom().getRoomNumber(), Globals.SFX_VOLLEY, player.getX(), player.getY());
        }
        player.updateSkillEnd(duration, this.endDuration, true, true);
    }
}
