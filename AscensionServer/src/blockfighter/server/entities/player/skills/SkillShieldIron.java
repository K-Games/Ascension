package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffShieldIron;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;
import java.util.Map;

public class SkillShieldIron extends Skill {

    public SkillShieldIron(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_IRON;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_SHIELD;
        this.endDuration = 2100;
        this.playerState = Player.PLAYER_STATE_SHIELD_IRON;
        this.reqEquipSlot = Globals.ITEM_OFFHAND;
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.room.getTime() - this.skillCastTime);
        if (player.getSkillCounter() == 0) {
            PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_SHIELD_IRON, player.getKey());
            player.incrementSkillCounter();
            //Player.sendSFX(this.logic.getRoom(), Globals.SFX_IRON, player.getX(), player.getY());
        }
        if (Globals.hasPastDuration(duration, 100) && player.getSkillCounter() == 1) {
            player.incrementSkillCounter();
            player.setRemovingDebuff(true);
            player.queueBuff(new BuffShieldIron(this.room, 2000, 0.55 + 0.01 * player.getSkillLevel(Skill.SHIELD_IRON)));
            if (player.isSkillMaxed(Skill.SHIELD_IRON) && !this.room.getMap().isPvP()) {
                for (final Map.Entry<Byte, Player> pEntry : this.room.getPlayers().entrySet()) {
                    final Player p = pEntry.getValue();
                    if (p != player && !p.isDead()) {
                        p.queueBuff(new BuffShieldIron(this.room, 2000, 0.4));
                        PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_SHIELD_IRONALLY, p.getKey());
                    }
                }
            }
        }
        if (player.updateSkillEnd(duration, this.endDuration, false, false)) {
            player.setRemovingDebuff(false);
        }
    }
}
