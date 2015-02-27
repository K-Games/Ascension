package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.Map;

/**
 * This is the base projectile class. Create projectile classes off this.
 *
 * @author Ken Kwan
 */
public class ProjShieldReflect extends Projectile {

    private double dmg;
    private final LinkedList<Player> queue = new LinkedList<>();

    public ProjShieldReflect(LogicModule l, int k, Player o, double x, double y, double damage) {
        super(l, k);
        setOwner(o);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        hitbox[0] = new Rectangle2D.Double(x - 325, y - 450, 650, 650);
        duration = 400;
        dmg = damage;
    }

    @Override
    public void update() {
        duration -= Globals.nsToMs(Globals.LOGIC_UPDATE);
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
            if (p != null && !p.isDead()) {
                Damage dmgEntity = new Damage((int) dmg, true, getOwner(), p, false, hitbox[0], p.getHitbox());
                dmgEntity.setCanReflect(false);
                p.queueDamage(dmgEntity);
            }
        }
        queuedEffect = false;
    }

}
