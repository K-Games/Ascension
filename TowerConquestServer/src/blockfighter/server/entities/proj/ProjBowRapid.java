package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffKnockback;
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
public class ProjBowRapid extends ProjBase {

    private final LinkedList<Player> queue = new LinkedList<>();

    /**
     * Projectile of Bow Skill Rapid Fire.
     *
     * @param l Room/Logic Module
     * @param k Projectile Key
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     */
    public ProjBowRapid(LogicModule l, int k, Player o, double x, double y) {
        super(l, k);
        setOwner(o);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        if (getOwner().getFacing() == Globals.RIGHT) {
            hitbox[0] = new Rectangle2D.Double(x + 80, y - 128, 497, 37);
        } else {
            hitbox[0] = new Rectangle2D.Double(x - 490 - 80, y - 128, 497, 37);
        }
        duration = 400;
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
                int damage = (int) (getOwner().rollDamage() * (.75 + getOwner().getSkillLevel(Skill.BOW_RAPID) * .01));
                boolean crit = getOwner().rollCrit();
                if (crit) {
                    damage = (int) getOwner().criticalDamage(damage);
                }
                if (getOwner().isSkillMaxed(Skill.BOW_RAPID) && rng(50) + 1 <= 50) {
                    damage *= 2;
                }
                p.queueDamage(new Damage(damage, true, getOwner(), p, crit, hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(100, (getOwner().getFacing() == Globals.RIGHT) ? 3 : -3, -3, getOwner(), p));
            }
        }
        queuedEffect = false;
    }

}
