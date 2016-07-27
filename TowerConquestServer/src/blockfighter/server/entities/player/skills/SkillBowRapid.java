package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBowRapid;
import blockfighter.server.net.PacketSender;

public class SkillBowRapid extends Skill {

    public SkillBowRapid(final LogicModule l) {
        super(l);
        this.skillCode = BOW_RAPID;
        this.maxCooldown = 700;
        this.reqWeapon = Globals.ITEM_BOW;
        this.endDuration = 550;
        this.playerState = Player.PLAYER_STATE_BOW_RAPID;
        this.reqEquipSlot = Globals.ITEM_WEAPON;
    }

    @Override
    public void updateSkillUse(Player player) {
        final int duration = Globals.nsToMs(this.room.getTime() - player.getSkillCastTime());
        final int numHits = 3;

        if (Globals.hasPastDuration(duration, 150 + player.getSkillCounter() * 150) && player.getSkillCounter() < numHits) {
            if (player.getSkillCounter() != 0) {
                player.setFrame((byte) 2);
            }
            player.incrementSkillCounter();
            double projY = player.getY();
            if (player.getSkillCounter() == 1) {
                projY = player.getY() - 20;
            } else if (player.getSkillCounter() == 3) {
                projY = player.getY() + 20;
            }
            final ProjBowRapid proj = new ProjBowRapid(this.room, player, player.getX(), projY);
            this.room.queueAddProj(proj);
            PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_BOW_RAPID, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    player.getFacing());
            PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_BOW_RAPID2, (player.getFacing() == Globals.LEFT) ? player.getX() - 20 : player.getX() - 40, proj.getHitbox()[0].getY() - 40,
                    player.getFacing());
            PacketSender.sendSFX(this.room.getRoom(), Globals.SFX_RAPID, player.getX(), player.getY());
        }
        player.updateSkillEnd(duration, this.endDuration, true, false);
    }

}
