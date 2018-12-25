package blockfighter.server.entities.player.skills.passive;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.damage.DamageBuilder;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.SkillPassive;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class SkillPassiveStatic extends SkillPassive {

    public static final byte SKILL_CODE = Globals.PASSIVE_STATIC;

    public SkillPassiveStatic(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(final Player player) {
        double radius = 250;
        if (Globals.rng(100) + 1 <= 20) {
            double baseValue = getSkillData().getBaseValue();
            double multValue = getSkillData().getMultValue();
            int damage = (int) (player.getStats()[Globals.STAT_ARMOUR] * (baseValue + multValue * player.getSkillLevel(Globals.PASSIVE_STATIC)));

            if (this.logic.getRoomData().getMap().isPvP()) {
                ArrayList<Player> playersInRange = this.logic.getRoomData().getPlayersInRange(player, radius);
                if (!playersInRange.isEmpty()) {
                    Player target = playersInRange.get(Globals.rng(playersInRange.size()));
                    final boolean crit = player.rollCrit();
                    if (crit) {
                        damage = (int) player.criticalDamage(damage);
                    }
                    Point2D.Double newPos = new Point2D.Double(target.getHitbox().x + target.getHitbox().width / 2, target.getHitbox().y + target.getHitbox().height / 2);
                    target.queueDamage(new DamageBuilder()
                            .setDamage(damage)
                            .setCanProc(false)
                            .setOwner(player)
                            .setTarget(target)
                            .setIsCrit(crit)
                            .setDmgPoint(newPos)
                            .build());
                    PacketSender.sendParticle(this.logic, Globals.Particles.PASSIVE_STATIC.getParticleCode(), player.getKey(), target.getKey());
                }
            }
        }
    }

}
