package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.buff.BuffStun;
import blockfighter.server.entities.damage.Damage;
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
public class ProjShieldCharge extends ProjBase {

    private final LinkedList<Player> queue = new LinkedList<>();

    /**
     * Projectile of Sword Skill Drive.
     *
     * @param l Room/Logic Module
     * @param k Projectile Key
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     */
    public ProjShieldCharge(LogicModule l, int k, Player o, double x, double y) {
        super(l, k);
        setOwner(o);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        if (getOwner().getFacing() == Globals.RIGHT) {
            hitbox[0] = new Rectangle2D.Double(x - 200, y - 176, 428, 176);
        } else {
            hitbox[0] = new Rectangle2D.Double(x - 428 + 200, y - 176, 428, 176);

        }
        duration = 750;
    }

    @Override
    public void update() {
        duration -= Globals.nsToMs(Globals.LOGIC_UPDATE);
        if (getOwner().getFacing() == Globals.RIGHT) {
            hitbox[0].x = getOwner().getX() - 200;
        } else {
            hitbox[0].x = getOwner().getX() - 428 + 200;
        }

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
                int damage = (int) (getOwner().rollDamage() * (3 + .2 * getOwner().getSkillLevel(Skill.SHIELD_CHARGE)));
                boolean crit = getOwner().rollCrit();
                if (crit) {
                    damage = (int) getOwner().criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, getOwner(), p, crit, hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(300, (getOwner().getFacing() == Globals.RIGHT) ? 4 : -4, -5, getOwner(), p));
                if (getOwner().isSkillMaxed(Skill.SHIELD_CHARGE)) {
                    p.queueBuff(new BuffStun(2000));
                }
            }
        }
        queuedEffect = false;
    }

}
