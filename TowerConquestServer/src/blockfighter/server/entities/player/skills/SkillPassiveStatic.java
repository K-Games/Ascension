package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Ken Kwan
 */
public class SkillPassiveStatic extends Skill {

    public SkillPassiveStatic(final LogicModule l) {
        super(l);
        this.skillCode = PASSIVE_STATIC;
        this.isPassive = true;
    }

    @Override
    public void updateSkillUse(final Player player) {
        if (Globals.rng(100) + 1 <= 20) {
            if (this.logic.getMap().isPvP()) {
                ArrayList<Player> playersInRange = new ArrayList<>(Globals.SERVER_MAX_PLAYERS);
                for (final Map.Entry<Byte, Player> pEntry : this.logic.getPlayers().entrySet()) {
                    final Player p = pEntry.getValue();
                    if (p != player && !p.isDead() && !p.isInvulnerable()) {
                        double distance = Math.sqrt(Math.pow((player.getX() - p.getX()), 2) + Math.pow((player.getY() - p.getY()), 2));
                        if (distance <= 250) {
                            playersInRange.add(p);
                        }
                    }
                }
                if (!playersInRange.isEmpty()) {
                    Player target = playersInRange.get(Globals.rng(playersInRange.size()));
                    int damage = (int) (player.getStats()[Globals.STAT_ARMOR] * (0.5 + 0.15 * player.getSkillLevel(Skill.PASSIVE_STATIC)));
                    final boolean crit = player.rollCrit();
                    if (crit) {
                        damage = (int) player.criticalDamage(damage);
                    }
                    target.queueDamage(new Damage(damage, false, player, target, crit, target.getHitbox(), target.getHitbox()));
                    PacketSender.sendParticle(this.logic.getRoom(), Globals.PARTICLE_PASSIVE_STATIC, player.getKey(), target.getKey());
                }
            } else {
                ArrayList<Mob> enemyInRange = new ArrayList<>(this.logic.getMobs().size());
                for (final Map.Entry<Byte, Mob> bEntry : this.logic.getMobs().entrySet()) {
                    final Mob b = bEntry.getValue();
                    double distance = Math.sqrt(Math.pow((player.getX() - b.getX()), 2) + Math.pow((player.getY() - b.getY()), 2));
                    if (distance <= 100) {
                        enemyInRange.add(b);
                    }
                }
                if (!enemyInRange.isEmpty()) {
                    Mob target = enemyInRange.get(Globals.rng(enemyInRange.size()));
                    int damage = (int) (player.getStats()[Globals.STAT_ARMOR] * (0.5 + 0.1 * player.getSkillLevel(Skill.PASSIVE_STATIC)));
                    final boolean crit = player.rollCrit();
                    if (crit) {
                        damage = (int) player.criticalDamage(damage);
                    }
                    target.queueDamage(new Damage(damage, false, player, target, crit, target.getHitbox(), target.getHitbox()));
                }
            }
        }
    }
}
