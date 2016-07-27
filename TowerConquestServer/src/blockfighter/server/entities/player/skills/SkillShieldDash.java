package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffShieldDash;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;

public class SkillShieldDash extends Skill {

    public SkillShieldDash(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_DASH;
        this.maxCooldown = 13000;
        this.endDuration = 400;
        this.playerState = Player.PLAYER_STATE_SHIELD_DASH;
        this.reqEquipSlot = Globals.ITEM_OFFHAND;
    }

    @Override
    public void updateSkillUse(Player player) {
        final int duration = Globals.nsToMs(this.room.getTime() - player.getSkillCastTime());
        if (!player.isStunned() && !player.isKnockback()) {
            player.setXSpeed((player.getFacing() == Globals.RIGHT) ? 8.5 : -8.5);
        }
        if (player.isSkillMaxed(Skill.SHIELD_DASH) && !player.isInvulnerable()) {
            player.setInvulnerable(true);
        }

        if (player.getSkillCounter() == 0) {
            PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_SHIELD_DASH, player.getKey(), player.getFacing());
            player.setYSpeed(-5.5);
            player.incrementSkillCounter();
        }

        if (player.getSkillCounter() == 1 && duration >= this.endDuration) {
            player.incrementSkillCounter();
            player.queueBuff(new BuffShieldDash(this.room, 5000, 0.01 + 0.003 * player.getSkillLevel(Skill.SHIELD_DASH), player));
            PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_SHIELD_DASHBUFF, player.getKey());
        }
        player.updateSkillEnd(duration, this.endDuration, true, true);
    }
}
