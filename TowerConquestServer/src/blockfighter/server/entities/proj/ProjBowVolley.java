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
    public ProjBowVolley(final LogicModule l, final int k, final Player o, final double x, final double y) {
        super(l, k);
        setOwner(o);
        this.x = x;
        this.y = y;
        this.hitbox = new Rectangle2D.Double[1];
        if (getOwner().getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(x + 130, y - 98, 465, 15);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(x - 490 - 130, y - 98, 465, 15);
        }
        this.duration = 400;
    }

    @Override
    public void processQueue() {
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (.75 + owner.getSkillLevel(Skill.BOW_VOLLEY) * .03));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                    if (!this.buffed) {
                        this.buffed = true;
                        if (owner.isSkillMaxed(Skill.BOW_VOLLEY)) {
                            owner.queueBuff(new BuffBowVolley(4000, 0.01, owner));
                            final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
                            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
                            bytes[1] = Globals.PARTICLE_BOW_VOLLEYBUFF;
                            bytes[2] = owner.getKey();
                            sender.sendAll(bytes, this.logic.getRoom());
                        }
                    }
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, this.hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(50, (owner.getFacing() == Globals.RIGHT) ? 1 : -1, -0.1, owner, p));
            }
        }

        while (!this.bossQueue.isEmpty()) {
            final Boss b = this.bossQueue.poll();
            final Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (.75 + owner.getSkillLevel(Skill.BOW_VOLLEY) * .03));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                    if (!this.buffed) {
                        this.buffed = true;
                        if (owner.isSkillMaxed(Skill.BOW_VOLLEY)) {
                            owner.queueBuff(new BuffBowVolley(4000, 0.01, owner));
                            final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
                            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
                            bytes[1] = Globals.PARTICLE_BOW_VOLLEYBUFF;
                            bytes[2] = owner.getKey();
                            sender.sendAll(bytes, this.logic.getRoom());
                        }
                    }
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, this.hitbox[0], b.getHitbox()));
            }
        }
        this.queuedEffect = false;
    }

}
