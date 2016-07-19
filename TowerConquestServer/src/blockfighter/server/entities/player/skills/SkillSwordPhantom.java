package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjSwordPhantom;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordPhantom extends Skill {

    public SkillSwordPhantom(final LogicModule l) {
        super(l);
        this.skillCode = SWORD_PHANTOM;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_SWORD;
    }

    @Override
    public void updateSkillUse(Player player) {
        final int duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        final int numHits = getLevel() / 2 + 5;
        final int radius = 350;
        boolean endPhantom = false;
        player.setInvulnerable(true);
        player.setYSpeed(0);

        //Send initial phase effect
        if (duration == 0) {
            Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_PHANTOM, player.getX(), player.getY(), player.getFacing());
        }

        if (Globals.hasPastDuration(duration, 100 + 100 * player.getSkillCounter()) && player.getSkillCounter() < numHits) {
            if (this.logic.getMap().isPvP()) {
                Player target;
                ArrayList<Player> playersInRange = new ArrayList<>(Globals.SERVER_MAX_PLAYERS);
                for (final Map.Entry<Byte, Player> pEntry : this.logic.getPlayers().entrySet()) {
                    final Player p = pEntry.getValue();
                    if (p != player && !p.isDead() && !p.isInvulnerable()) {
                        double distance = Math.sqrt(Math.pow((player.getX() - p.getX()), 2) + Math.pow((player.getY() - p.getY()), 2));
                        if (distance <= radius) {
                            playersInRange.add(p);
                        }
                    }
                }
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
                ArrayList<Mob> enemyInRange = new ArrayList<>(Globals.SERVER_MAX_PLAYERS);
                for (final Map.Entry<Byte, Mob> bEntry : this.logic.getMobs().entrySet()) {
                    final Mob b = bEntry.getValue();
                    double distance = Math.sqrt(Math.pow((player.getX() - b.getX()), 2) + Math.pow((player.getY() - b.getY()), 2));
                    if (distance <= radius) {
                        enemyInRange.add(b);
                    }
                }
                if (!enemyInRange.isEmpty()) {
                    target = enemyInRange.get(Globals.rng(enemyInRange.size()));
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
                Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_PHANTOM, player.getX(), player.getY(), player.getFacing());
                Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_PHANTOM2, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(), player.getFacing());
                player.incrementSkillCounter();
            }
        }
        player.updateSkillEnd(endPhantom || player.getSkillCounter() >= numHits);
    }
}
