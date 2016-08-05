package blockfighter.server.entities.proj;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffBowVolley;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.net.PacketSender;
import java.awt.geom.Rectangle2D;

public class ProjBowVolley extends Projectile {

    private boolean buffed = false;

    public ProjBowVolley(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 100);
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x + 40, this.y - 75 + Globals.rng(30) - 15, 465, 15);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 465 - 40, this.y - 75 + Globals.rng(30) - 15, 465, 15);
        }
    }

    @Override
    public void applyEffect() {
        PacketSender.sendScreenShake(this.getOwner());
        while (!this.playerQueue.isEmpty()) {
            final Player p = this.playerQueue.poll(), owner = getOwner();
            if (p != null && !p.isDead()) {
                int damage = (int) (owner.rollDamage() * (.85 + owner.getSkillLevel(Skill.BOW_VOLLEY) * .03));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                    if (!this.buffed) {
                        this.buffed = true;
                        if (owner.isSkillMaxed(Skill.BOW_VOLLEY)) {
                            owner.queueBuff(new BuffBowVolley(this.room, 4000, 0.01, owner));
                            final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
                            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
                            bytes[1] = Globals.PARTICLE_BOW_VOLLEYBUFF;
                            bytes[2] = owner.getKey();
                            PacketSender.sendAll(bytes, this.room.getRoom());
                        }
                    }
                }
                p.queueDamage(new Damage(damage, true, owner, p, crit, this.hitbox[0], p.getHitbox()));
                p.queueBuff(new BuffKnockback(this.room, 50, (owner.getFacing() == Globals.RIGHT) ? 1 : -1, -0.1, owner, p));
            }
        }

        while (!this.mobQueue.isEmpty()) {
            final Mob b = this.mobQueue.poll();
            final Player owner = getOwner();
            if (b != null && !b.isDead()) {
                int damage = (int) (owner.rollDamage() * (.85 + owner.getSkillLevel(Skill.BOW_VOLLEY) * .03));
                final boolean crit = owner.rollCrit();
                if (crit) {
                    damage = (int) owner.criticalDamage(damage);
                    if (!this.buffed) {
                        this.buffed = true;
                        if (owner.isSkillMaxed(Skill.BOW_VOLLEY)) {
                            owner.queueBuff(new BuffBowVolley(this.room, 4000, 0.01, owner));
                            final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
                            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
                            bytes[1] = Globals.PARTICLE_BOW_VOLLEYBUFF;
                            bytes[2] = owner.getKey();
                            PacketSender.sendAll(bytes, this.room.getRoom());
                        }
                    }
                }
                b.queueDamage(new Damage(damage, true, owner, b, crit, this.hitbox[0], b.getHitbox()));
            }
        }
        this.queuedEffect = false;
    }

}
