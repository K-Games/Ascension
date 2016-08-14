package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;
import java.util.ArrayList;

public class SkillPassiveStatic extends Skill {

    public SkillPassiveStatic(final LogicModule l) {
        super(l);
        this.skillCode = PASSIVE_STATIC;
        this.isPassive = true;
    }

    @Override
    public void updateSkillUse(final Player player) {
        double radius = 250;
        if (Globals.rng(100) + 1 <= 20) {
            if (this.logic.getRoom().getMap().isPvP()) {
                ArrayList<Player> playersInRange = this.logic.getRoom().getPlayersInRange(player, radius);
                if (!playersInRange.isEmpty()) {
                    Player target = playersInRange.get(Globals.rng(playersInRange.size()));
                    int damage = (int) (player.getStats()[Globals.STAT_ARMOR] * (0.5 + 0.15 * player.getSkillLevel(Skill.PASSIVE_STATIC)));
                    final boolean crit = player.rollCrit();
                    if (crit) {
                        damage = (int) player.criticalDamage(damage);
                    }
                    target.queueDamage(new Damage(damage, false, player, target, crit, target.getHitbox(), target.getHitbox()));
                    PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_PASSIVE_STATIC, player.getKey(), target.getKey());
                }
            } else {
                ArrayList<Mob> mobsInRange = this.logic.getRoom().getMobsInRange(player, radius);
                if (!mobsInRange.isEmpty()) {
                    Mob target = mobsInRange.get(Globals.rng(mobsInRange.size()));
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
