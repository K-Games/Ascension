package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.buff.BuffBowVolley;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import java.awt.geom.Rectangle2D;

/**
 * This is the base projectile class. Create projectile classes off this.
 *
 * @author Ken Kwan
 */
public class ProjBowVolley extends Projectile {

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
    public void processQueue() {
        while (!playerQueue.isEmpty()) {
            Player p = playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (.75 + owner.getSkillLevel(Skill.BOW_VOLLEY) * .03));
                boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                    if (!buffed) {
                        buffed = true;
                        if (owner.isSkillMaxed(Skill.BOW_VOLLEY)) {
                            owner.queueBuff(new BuffBowVolley(4000, 0.01, owner));
                            byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
                            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
                            bytes[1] = Globals.PARTICLE_BOW_VOLLEYBUFF;
                            bytes[2] = owner.getKey();
                            sender.sendAll(bytes, logic.getRoom());
                        }
                    }
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(50, (owner.getFacing() == Globals.RIGHT) ? 1 : -1, -0.1, owner, p));
            }
        }

        while (!bossQueue.isEmpty()) {
            Boss b = bossQueue.poll();
            Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (.75 + owner.getSkillLevel(Skill.BOW_VOLLEY) * .03));
                boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                    if (!buffed) {
                        buffed = true;
                        if (owner.isSkillMaxed(Skill.BOW_VOLLEY)) {
                            owner.queueBuff(new BuffBowVolley(4000, 0.01, owner));
                            byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
                            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
                            bytes[1] = Globals.PARTICLE_BOW_VOLLEYBUFF;
                            bytes[2] = owner.getKey();
                            sender.sendAll(bytes, logic.getRoom());
                        }
                    }
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, hitbox[0], b.getHitbox()));
            }
        }
        queuedEffect = false;
    }

}
