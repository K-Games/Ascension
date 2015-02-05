package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.net.PacketSender;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.Map;

/**
 * This is the base projectile class. Create projectile classes off this.
 *
 * @author Ken Kwan
 */
public class ProjBowPower extends ProjBase {

    private final LinkedList<Player> queue = new LinkedList<>();

    public ProjBowPower(PacketSender b, LogicModule l, int k, Player o, double x, double y) {
        super(b, l, k);
        owner = o;
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        if (owner.getFacing() == Globals.RIGHT) {
            hitbox[0] = new Rectangle2D.Double(x + 80, y - 185, 700, 150);
        } else {
            hitbox[0] = new Rectangle2D.Double(x - 700 - 80, y - 185, 700, 150);

        }
        duration = 600;
    }

    @Override
    public void update() {
        duration -= Globals.nsToMs(Globals.LOGIC_UPDATE);
        for (Map.Entry<Byte, Player> pEntry : logic.getPlayers().entrySet()) {
            Player p = pEntry.getValue();
            if (p != owner && !pHit.contains(p) && p.intersectHitbox(hitbox[0])) {
                queue.add(p);
                pHit.add(p);
            }
        }
        if (!isQueued()) {
            logic.queueProjEffect(this);
            queuedEffect = true;
        }
    }

    @Override
    public void processQueue() {
        while (!queue.isEmpty()) {
            Player p = queue.poll();
            if (p != null) {
                int damage = (int) (owner.rollDamage() * (5+owner.getSkillLevel(Skill.BOW_POWER))) ;
                p.queueDamage(damage);
                p.addBuff(new BuffKnockback(500, (owner.getFacing() == Globals.RIGHT) ? 20 : -20, -25, p));
            }
        }
        queuedEffect = false;
    }

}
