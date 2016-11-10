package blockfighter.server.entities.player.skills;

import blockfighter.shared.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjSwordPhantom;
import blockfighter.server.net.PacketSender;
import java.util.ArrayList;

public class SkillSwordPhantom extends Skill {

    public SkillSwordPhantom(final LogicModule l) {
        super(l);
        this.skillCode = SWORD_PHANTOM;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_SWORD;
        this.playerState = Player.PLAYER_STATE_SWORD_PHANTOM;
        this.reqEquipSlot = Globals.ITEM_WEAPON;
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        final int numHits = getLevel() / 2 + 5;
        final int radius = 350;
        boolean endPhantom = false;
        player.setInvulnerable(true);
        player.setYSpeed(0);

        //Send initial phase effect
        if (player.getSkillCounter() == 0) {
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SWORD_PHANTOM, player.getX(), player.getY(), player.getFacing());
            player.incrementSkillCounter();
        }

        if (Globals.hasPastDuration(duration, 100 + 100 * (player.getSkillCounter() - 1)) && (player.getSkillCounter() - 1) < numHits) {
            if (this.logic.getRoom().getMap().isPvP()) {
                Player target;
                ArrayList<Player> playersInRange = this.logic.getRoom().getPlayersInRange(player, radius);

                if (!playersInRange.isEmpty()) {
                    target = playersInRange.get(Globals.rng(playersInRange.size()));
                    double teleX = (Globals.rng(2) == 0) ? target.getHitbox().x + target.getHitbox().width + 100 + Globals.rng(50) : target.getHitbox().x - 100 - Globals.rng(50);
                    player.setPos(teleX, target.getY() - 10 * Globals.rng(5));
                    if (target.getX() < player.getX()) {
                        player.setFacing(Globals.LEFT);
                    } else if (target.getX() > player.getX()) {
                        player.setFacing(Globals.RIGHT);
                    }
                } else {
                    endPhantom = true;
                }
            } else {
                Mob target;
                ArrayList<Mob> mobsInRange = this.logic.getRoom().getMobsInRange(player, radius);

                if (!mobsInRange.isEmpty()) {
                    target = mobsInRange.get(Globals.rng(mobsInRange.size()));
                    double teleX = (Globals.rng(2) == 0) ? target.getHitbox().x + target.getHitbox().width + 100 + Globals.rng(50) : target.getHitbox().x - 100 - Globals.rng(50);
                    player.setPos(teleX, target.getY() - 10 * Globals.rng(5));
                    if (target.getX() < player.getX()) {
                        player.setFacing(Globals.LEFT);
                    } else if (target.getX() > player.getX()) {
                        player.setFacing(Globals.RIGHT);
                    }
                } else {
                    endPhantom = true;
                }
            }
            if (!endPhantom) {
                final ProjSwordPhantom proj = new ProjSwordPhantom(this.logic, player, player.getX(), player.getY());
                this.logic.queueAddProj(proj);
                PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SWORD_PHANTOM, player.getX(), player.getY(), player.getFacing());
                PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SWORD_PHANTOM2, player.getX(), player.getY(), player.getFacing());
                player.incrementSkillCounter();
            }
        }
        player.updateSkillEnd(endPhantom || (player.getSkillCounter() - 1) >= numHits);
    }
}