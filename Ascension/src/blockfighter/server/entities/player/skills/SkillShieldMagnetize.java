package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjShieldMagnetize;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.ArrayList;

public class SkillShieldMagnetize extends Skill {

    ArrayList<Player> playersCaught;
    ArrayList<Mob> mobsCaught;

    public SkillShieldMagnetize(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_MAGNETIZE;
        this.maxCooldown = 15000;
        this.reqWeapon = Globals.ITEM_SHIELD;
        this.endDuration = 600;
        this.playerState = Player.PLAYER_STATE_SHIELD_MAGNETIZE;
        this.reqEquipSlot = Globals.ITEM_OFFHAND;
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        final int radius = 400;
        if (player.getSkillCounter() == 0) {
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_MAGNETIZESTART, player.getKey());
            player.incrementSkillCounter();
        }
        if (Globals.hasPastDuration(duration, 200) && player.getSkillCounter() == 1) {
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_MAGNETIZEBURST, player.getKey());
            if (this.logic.getRoom().getMap().isPvP()) {
                this.playersCaught = this.logic.getRoom().getPlayersInRange(player, radius);
                if (!this.playersCaught.isEmpty()) {
                    for (Player p : this.playersCaught) {
                        PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_MAGNETIZE, player.getKey(), p.getKey());
                    }
                }
            } else {
                this.mobsCaught = this.logic.getRoom().getMobsInRange(player, radius);
                if (!this.mobsCaught.isEmpty()) {
                    for (Mob mob : this.mobsCaught) {
                        PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_MAGNETIZE, player.getKey(), mob.getKey());
                    }
                }
            }
            player.incrementSkillCounter();
        }

        if (Globals.hasPastDuration(duration, 200) && player.getSkillCounter() == 2) {
            if (this.logic.getRoom().getMap().isPvP()) {
                if (!this.playersCaught.isEmpty()) {
                    int numOfTicks = (int) ((500 - duration) / Globals.nsToMs(Globals.SERVER_LOGIC_UPDATE));
                    for (Player p : this.playersCaught) {
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
            }
        }

        if (Globals.hasPastDuration(duration, 500) && player.getSkillCounter() == 2) {
            final ProjShieldMagnetize proj = new ProjShieldMagnetize(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            player.incrementSkillCounter();
        }

        player.updateSkillEnd(duration, this.endDuration, false, false);
    }
}
