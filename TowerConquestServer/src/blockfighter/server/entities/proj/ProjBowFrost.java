package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.buff.BuffStun;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.Map;

/**
 * This is the base projectile class. Create projectile classes off this.
 *
 * @author Ken Kwan
 */
public class ProjBowFrost extends ProjBase {

    private final LinkedList<Player> queue = new LinkedList<>();
    private double speedX = 0;

    /**
     * Projectile of Bow Skill Frost Bind.
     *
     * @param l Room/Logic Module
     * @param k Projectile Key
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     */
    public ProjBowFrost(LogicModule l, int k, Player o, double x, double y) {
        super(l, k);
        setOwner(o);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        if (getOwner().getFacing() == Globals.RIGHT) {
            hitbox[0] = new Rectangle2D.Double(x + 80, y - 190, 300, 148);
            speedX = 20;
        } else {
            hitbox[0] = new Rectangle2D.Double(x - 300 - 80, y - 190, 300, 148);
            speedX = -20;
        }
        duration = 500;
    }

    @Override
    public void update() {
        duration -= Globals.nsToMs(Globals.LOGIC_UPDATE);
        x += speedX;
        hitbox[0].x += speedX;
        for (Map.Entry<Byte, Player> pEntry : logic.getPlayers().entrySet()) {
            Player p = pEntry.getValue();
            if (p != getOwner() && !pHit.contains(p) && p.intersectHitbox(hitbox[0])) {
                queue.add(p);
                pHit.add(p);
                queueEffect(this);
            }
        }
    }

    @Override
    public void processQueue() {
        while (!queue.isEmpty()) {
            Player p = queue.poll();
            if (p != null) {
                int damage = (int) (getOwner().rollDamage() + (.3 * getOwner().getSkillLevel(Skill.BOW_STORM)));
                p.queueDamage(damage);
                p.queueBuff(new BuffKnockback(200, (getOwner().getFacing() == Globals.RIGHT) ? 10 : -10, -10, getOwner(), p));
                p.queueBuff(new BuffStun(2000));
            }
        }
        queuedEffect = false;
    }

}
