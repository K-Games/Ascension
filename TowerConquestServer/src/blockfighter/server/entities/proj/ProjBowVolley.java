package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffBowVolley;
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
public class ProjBowVolley extends ProjBase {

    private final LinkedList<Player> queue = new LinkedList<>();
    private boolean buffed = false;

    /**
     * Projectile of Bow Skill Volley.
     *
     * @param l Room/Logic Module
     * @param k Projectile Key
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     */
    public ProjBowVolley(LogicModule l, int k, Player o, double x, double y) {
        super(l, k);
        setOwner(o);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        if (getOwner().getFacing() == Globals.RIGHT) {
            hitbox[0] = new Rectangle2D.Double(x + 130, y - 128, 465, 15);
        } else {
            hitbox[0] = new Rectangle2D.Double(x - 490 - 130, y - 128, 465, 15);
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
                int damage = (int) (getOwner().rollDamage() * (.25 + getOwner().getSkillLevel(Skill.BOW_VOLLEY) * .02));
                boolean crit = getOwner().rollCrit();
                if (crit) {
                    damage = (int) getOwner().criticalDamage(damage);
                    if (!buffed) {
                        buffed = true;
                        if (getOwner().isSkillMaxed(Skill.BOW_VOLLEY)) {
                            getOwner().queueBuff(new BuffBowVolley(4000, 0.01, getOwner()));
                            byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
                            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
                            bytes[1] = Globals.PARTICLE_BOW_VOLLEYBUFF;
                            bytes[2] = getOwner().getKey();
                            sender.sendAll(bytes, logic.getRoom());
                        }
                        
                    }
                }
                p.queueDamage(new Damage(damage, true, getOwner(), p, crit, hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(50, (getOwner().getFacing() == Globals.RIGHT) ? 1 : -1, 0, getOwner(), p));
            }
        }
        queuedEffect = false;
    }

}
