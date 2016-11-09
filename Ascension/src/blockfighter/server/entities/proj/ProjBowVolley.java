package blockfighter.server.entities.proj;

import blockfighter.shared.Globals;
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
        this.screenshake = true;
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x + 40, this.y - 75 + Globals.rng(10) * 5 - 25, 445, 15);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 445 - 40, this.y - 75 + Globals.rng(10) * 5 - 25, 445, 15);
        }
    }

    @Override
    public int calculateDamage(final boolean isCrit) {
        final Player owner = getOwner();
        double damage = owner.rollDamage() * (.85 + owner.getSkillLevel(Skill.BOW_VOLLEY) * .03);
        damage = (isCrit) ? owner.criticalDamage(damage) : damage;
        return (int) damage;
    }

    @Override
    public void applyDamage(Player target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        if (isCrit) {
            if (!this.buffed) {
                this.buffed = true;
                if (owner.isSkillMaxed(Skill.BOW_VOLLEY)) {
                    owner.queueBuff(new BuffBowVolley(this.logic, 4000, 0.01, owner));
                    final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
                    bytes[0] = Globals.DATA_PARTICLE_EFFECT;
                    bytes[1] = Globals.PARTICLE_BOW_VOLLEYBUFF;
                    bytes[2] = owner.getKey();
                    PacketSender.sendAll(bytes, this.logic.getRoom().getRoomNumber());
                }
            }
        }
        target.queueDamage(new Damage(damage, true, owner, target, isCrit, this.hitbox[0], target.getHitbox()));
        target.queueBuff(new BuffKnockback(this.logic, 50, (owner.getFacing() == Globals.RIGHT) ? 1 : -1, -0.1, owner, target));
    }

    @Override
    public void applyDamage(Mob target) {
        final Player owner = getOwner();
        final boolean isCrit = owner.rollCrit();
        final int damage = calculateDamage(isCrit);
        if (isCrit) {
            if (!this.buffed) {
                this.buffed = true;
                if (owner.isSkillMaxed(Skill.BOW_VOLLEY)) {
                    owner.queueBuff(new BuffBowVolley(this.logic, 4000, 0.01, owner));
                    final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
                    bytes[0] = Globals.DATA_PARTICLE_EFFECT;
                    bytes[1] = Globals.PARTICLE_BOW_VOLLEYBUFF;
                    bytes[2] = owner.getKey();
                    PacketSender.sendAll(bytes, this.logic.getRoom().getRoomNumber());
                }
            }
        }
        target.queueDamage(new Damage(damage, true, owner, target, isCrit, this.hitbox[0], target.getHitbox()));
    }

}
