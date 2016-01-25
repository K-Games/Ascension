package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.buff.BuffBurn;
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
public class ProjSwordCinder extends Projectile {

    /**
     * Projectile of Sword Skill Cinder.
     *
     * @param l Room/Logic Module
     * @param k Projectile Key
     * @param o Owning player
     * @param x Spawn x-coordinate
     * @param y Spawn y-coordinate
     */
    public ProjSwordCinder(LogicModule l, int k, Player o, double x, double y) {
        super(l, k);
        setOwner(o);
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double[1];
        if (getOwner().getFacing() == Globals.RIGHT) {
            hitbox[0] = new Rectangle2D.Double(x - 30, y - 200, 190, 250);
        } else {
            hitbox[0] = new Rectangle2D.Double(x - 190 + 30, y - 200, 190, 250);

        }
        duration = 300;
    }

    @Override
    public void processQueue() {
        while (!playerQueue.isEmpty()) {
            Player p = playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (4.5 + owner.getSkillLevel(Skill.SWORD_CINDER) * .2));
                boolean crit = owner.rollCrit((owner.isSkillMaxed(Skill.SWORD_CINDER)) ? 1 : 0);
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(300, (owner.getFacing() == Globals.RIGHT) ? 1 : -1, -4, owner, p));
                p.queueBuff(new BuffBurn(4000, owner.getSkillLevel(Skill.SWORD_CINDER) * 0.01, owner.isSkillMaxed(Skill.SWORD_CINDER) ? owner.rollDamage() : 0, owner, p));
                byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
                bytes[0] = Globals.DATA_PARTICLE_EFFECT;
                bytes[1] = Globals.PARTICLE_BURN;
                bytes[2] = p.getKey();
                sender.sendAll(bytes, logic.getRoom());
            }
        }
        while (!bossQueue.isEmpty()) {
            Boss b = bossQueue.poll();
            Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (4.5 + owner.getSkillLevel(Skill.SWORD_CINDER) * .2));
                boolean crit = owner.rollCrit((owner.isSkillMaxed(Skill.SWORD_CINDER)) ? 1 : 0);
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, hitbox[0], b.getHitbox()));
                b.queueBuff(new BuffBurn(4000, owner.getSkillLevel(Skill.SWORD_CINDER) * 0.01, owner.isSkillMaxed(Skill.SWORD_CINDER) ? owner.rollDamage() : 0, owner, b));
                //Monster buff display
            }
        }
        queuedEffect = false;
    }

}
