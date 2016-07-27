package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBowFrost;
import blockfighter.server.net.PacketSender;

public class SkillBowFrost extends Skill {

    public SkillBowFrost(final LogicModule l) {
        super(l);
        this.skillCode = BOW_FROST;
        this.maxCooldown = 22000;
        this.reqWeapon = Globals.ITEM_BOW;
        this.endDuration = 380;
        this.playerState = Player.PLAYER_STATE_BOW_FROST;
        this.reqEquipSlot = Globals.ITEM_WEAPON;
    }

    @Override
    public void updateSkillUse(Player player) {
        final int duration = Globals.nsToMs(this.room.getTime() - player.getSkillCastTime());
        final int numHits = player.isSkillMaxed(Skill.BOW_FROST) ? 3 : 1;
        if (Globals.hasPastDuration(duration, 160 + player.getSkillCounter() * 90) && player.getSkillCounter() < numHits) {
            player.incrementSkillCounter();
            final ProjBowFrost proj = new ProjBowFrost(this.room, player, player.getX(), player.getY(), false);
            this.room.queueAddProj(proj);
            PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_BOW_FROSTARROW, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    player.getFacing());
        }

        player.updateSkillEnd(duration, this.endDuration, false, false);
    }

}
