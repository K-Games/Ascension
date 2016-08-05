package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffBurn;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.net.PacketSender;
import java.awt.geom.Rectangle2D;

public class ProjSwordCinder extends Projectile {

    public ProjSwordCinder(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 300);
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 10, this.y - 220, 170, 238);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 190 + 10, this.y - 220, 170, 238);
        }
    }

    @Override
    public void applyEffect() {
        PacketSender.sendScreenShake(this.getOwner());
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (4.5 + owner.getSkillLevel(Skill.SWORD_CINDER) * .2));
                final boolean crit = owner.rollCrit((owner.isSkillMaxed(Skill.SWORD_CINDER)) ? 1 : 0);
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, this.hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(this.room, 300, (owner.getFacing() == Globals.RIGHT) ? 1 : -1, -4, owner, p));
                p.queueBuff(new BuffBurn(this.room, 4000, owner.getSkillLevel(Skill.SWORD_CINDER) * 0.01,
                        owner.isSkillMaxed(Skill.SWORD_CINDER) ? owner.rollDamage() : 0, owner, p));
                final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
                bytes[0] = Globals.DATA_PARTICLE_EFFECT;
                bytes[1] = Globals.PARTICLE_BURN;
                bytes[2] = p.getKey();
                PacketSender.sendAll(bytes, this.room.getRoom());
            }
        }
        while (!this.mobQueue.isEmpty()) {
            final Mob b = this.mobQueue.poll();
            final Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (4.5 + owner.getSkillLevel(Skill.SWORD_CINDER) * .2));
                final boolean crit = owner.rollCrit((owner.isSkillMaxed(Skill.SWORD_CINDER)) ? 1 : 0);
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, this.hitbox[0], b.getHitbox()));
                b.queueBuff(new BuffBurn(this.room, 4000, owner.getSkillLevel(Skill.SWORD_CINDER) * 0.01,
                        owner.isSkillMaxed(Skill.SWORD_CINDER) ? owner.rollDamage() : 0, owner, b));
                // Monster buff display
            }
        }
        this.queuedEffect = false;
    }

}
