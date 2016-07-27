package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffShieldReflect;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;
import java.util.ArrayList;
import java.util.Map;

public class SkillShieldReflect extends Skill {

    public SkillShieldReflect(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_REFLECT;
        this.maxCooldown = 15000;
        this.reqWeapon = Globals.ITEM_SHIELD;
        this.endDuration = 250;
        this.playerState = Player.PLAYER_STATE_SHIELD_REFLECT;
        this.reqEquipSlot = Globals.ITEM_OFFHAND;
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.room.getTime() - this.skillCastTime);
        if (player.getSkillCounter() == 0) {
            player.incrementSkillCounter();
            player.queueBuff(new BuffShieldReflect(this.room, 3000, .4 + 0.02 * player.getSkillLevel(Skill.SHIELD_REFLECT), player, player));
            PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_SHIELD_REFLECTCAST, player.getKey());
            PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_SHIELD_REFLECTBUFF, player.getKey());
            if (player.isSkillMaxed(Skill.SHIELD_REFLECT)) {
                for (final Map.Entry<Byte, Player> pEntry : this.room.getPlayers().entrySet()) {
                    final Player p = pEntry.getValue();
                    if (p != player && !p.isDead()) {
                        p.queueBuff(new BuffShieldReflect(this.room, 3000, 0.4, player, p));
                        if (!this.room.getMap().isPvP()) {
                            PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_SHIELD_REFLECTCAST, p.getKey());
                        }
                    }
                }
            }
        }
        player.updateSkillEnd(duration, this.endDuration, false, false);
    }

    public void updateSkillReflectHit(final double dmgTaken, final double mult, final Player player) {
        double radius = 300;
        if (this.room.getMap().isPvP()) {
            ArrayList<Player> playersInRange = this.room.getPlayersInRange(player, radius);
            if (!playersInRange.isEmpty()) {
                for (Player p : playersInRange) {
                    final Damage dmgEntity = new Damage((int) (dmgTaken * mult), true, player, p, false, p.getHitbox(), p.getHitbox());
                    dmgEntity.setCanReflect(false);
                    p.queueDamage(dmgEntity);
                }
            }
        } else {
            ArrayList<Mob> mobsInRange = this.room.getMobsInRange(player, radius);
            if (!mobsInRange.isEmpty()) {
                for (Mob mob : mobsInRange) {
                    final Damage dmgEntity = new Damage((int) (dmgTaken * mult), true, player, mob, false, mob.getHitbox(), mob.getHitbox());
                    dmgEntity.setCanReflect(false);
                    mob.queueDamage(dmgEntity);
                }
            }
        }
        PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_SHIELD_REFLECTHIT, player.getX(), player.getY());
    }
}
