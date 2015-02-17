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
public class ProjSwordDrive extends ProjBase {

    private boolean healed = false;

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
    public ProjSwordDrive(LogicModule l, int k, Player o, double x, double y) {
        super(l, k);
        setOwner(o);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        if (getOwner().getFacing() == Globals.RIGHT) {
            hitbox[0] = new Rectangle2D.Double(x - 310, y - 167, 560, 150);
        } else {
            hitbox[0] = new Rectangle2D.Double(x - 560 + 310, y - 167, 560, 150);

        }
        duration = 250;
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
            if (p != null) {
                int damage = (int) (getOwner().rollDamage() * (.75 + 0.03 * getOwner().getSkillLevel(Skill.SWORD_DRIVE)));
                boolean crit = getOwner().rollCrit();
                if (crit) {
                    damage = (int) getOwner().criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, getOwner(), p, crit, hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(300, (getOwner().getFacing() == Globals.RIGHT) ? 4 : -4, -5, getOwner(), p));
                if (!healed && getOwner().isSkillMaxed(Skill.SWORD_DRIVE)) {
                    double heal = getOwner().getStats()[Globals.STAT_MAXHP] * 0.005;
                    getOwner().queueHeal((int) heal);
                    healed = true;
                }
            }
        }
        queuedEffect = false;
    }

}
