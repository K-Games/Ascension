package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;
import java.util.ArrayList;

public class SkillShieldMagnetize extends Skill {

    ArrayList<Player> playersCaught;
    ArrayList<Mob> mobsCaught;

    public SkillShieldMagnetize(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_MAGNETIZE;
        this.maxCooldown = 15000;
        this.reqWeapon = Globals.ITEM_SHIELD;
        this.endDuration = 450;
        this.playerState = Player.PLAYER_STATE_SHIELD_MAGNETIZE;
        this.reqEquipSlot = Globals.ITEM_OFFHAND;
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.room.getTime() - player.getSkillCastTime());
        final int radius = 400;
        if (player.getSkillCounter() == 0) {
            PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_SHIELD_MAGNETIZESTART, player.getKey());
            player.incrementSkillCounter();
        }
        if (Globals.hasPastDuration(duration, 200) && player.getSkillCounter() == 1) {
            PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_SHIELD_MAGNETIZEBURST, player.getKey());
            if (this.room.getMap().isPvP()) {
                this.playersCaught = this.room.getPlayersInRange(player, radius);
                if (!this.playersCaught.isEmpty()) {
                    for (Player p : this.playersCaught) {
                        PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_SHIELD_MAGNETIZE, player.getKey(), p.getKey());
                        int damage = (int) (player.rollDamage() * (1.5 + 0.15 * player.getSkillLevel(Skill.SHIELD_MAGNETIZE))
                                + (player.getStats()[Globals.STAT_DEFENSE] * (15 + player.getSkillLevel(Skill.SHIELD_MAGNETIZE))));
                        if (player.isSkillMaxed(Skill.SHIELD_MAGNETIZE)) {
                            damage *= 3;
                        }
                        final boolean crit = player.rollCrit();
                        if (crit) {
                            damage = (int) player.criticalDamage(damage);
                        }
                        p.queueDamage(new Damage(damage, true, player, p, crit, p.getHitbox(), p.getHitbox()));
                    }
                }
            } else {
                this.mobsCaught = this.room.getMobsInRange(player, radius);
                if (!this.mobsCaught.isEmpty()) {
                    for (Mob mob : this.mobsCaught) {
                        PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_SHIELD_MAGNETIZE, player.getKey(), mob.getKey());
                        int damage = (int) (player.rollDamage() * (1.5 + 0.15 * player.getSkillLevel(Skill.SHIELD_MAGNETIZE))
                                + (player.getStats()[Globals.STAT_DEFENSE] * (15 + player.getSkillLevel(Skill.SHIELD_MAGNETIZE))));
                        if (player.isSkillMaxed(Skill.SHIELD_MAGNETIZE)) {
                            damage *= 3;
                        }
                        final boolean crit = player.rollCrit();
                        if (crit) {
                            damage = (int) player.criticalDamage(damage);
                        }
                        mob.queueDamage(new Damage(damage, true, player, mob, crit, mob.getHitbox(), mob.getHitbox()));
                    }
                }
            }
            player.incrementSkillCounter();
        }

        if (player.getSkillCounter() == 2) {
            if (this.room.getMap().isPvP()) {
                if (!this.playersCaught.isEmpty()) {
                    for (Player p : this.playersCaught) {
                        int numOfTicks = (int) ((endDuration - duration) / Globals.nsToMs(Globals.LOGIC_UPDATE));
                        if (numOfTicks > 0) {
                            double distanceX = (player.getX() - p.getX()) / numOfTicks;
                            double distanceY = (player.getY() - p.getY()) / numOfTicks;
                            p.setXSpeed(distanceX);
                            p.setYSpeed(distanceY);
                        } else {
                            p.setXSpeed(0);
                            p.setYSpeed(0.01);
                        }
                    }
                }
            } else {
                ArrayList<Mob> mobsInRange = this.room.getMobsInRange(player, radius);
                if (!this.mobsCaught.isEmpty()) {
                    for (Mob mob : this.mobsCaught) {
                        final Damage dmgEntity = new Damage(100, true, player, mob, false, mob.getHitbox(), mob.getHitbox());
                        dmgEntity.setCanReflect(false);
                        mob.queueDamage(dmgEntity);
                    }
                }
            }
        }
        player.updateSkillEnd(duration, this.endDuration, false, false);
    }
}
