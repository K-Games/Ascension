package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjSwordGash;
import blockfighter.server.net.PacketSender;

public class SkillSwordGash extends Skill {

    public SkillSwordGash(final LogicModule l) {
        super(l);
        this.skillCode = SWORD_GASH;
        this.maxCooldown = 500;
        this.reqWeapon = Globals.ITEM_SWORD;
        this.endDuration = 450;
        this.playerState = Player.PLAYER_STATE_SWORD_GASH;
        this.reqEquipSlot = Globals.ITEM_WEAPON;
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        final byte numHits = 4;
        if (Globals.hasPastDuration(duration, (100 * player.getSkillCounter())) && player.getSkillCounter() < numHits) {
            player.incrementSkillCounter();
            final ProjSwordGash proj = new ProjSwordGash(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            //PacketSender.sendSFX(this.logic.getRoom().getRoomNumber(), Globals.SFX_GASH, player.getX(), player.getY());
            switch (player.getSkillCounter()) {
                case 1:
                case 2:
                    PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SWORD_GASH1, player.getX(), player.getY(),
                            player.getFacing());
                    break;
                case 3:
                case 4:
                    PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SWORD_GASH2, player.getX(), player.getY(),
                            player.getFacing());
                    break;
            }
        }
        player.updateSkillEnd(duration, this.endDuration, true, false);
    }
}
