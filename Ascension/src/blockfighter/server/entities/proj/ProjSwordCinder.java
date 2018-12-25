package blockfighter.server.entities.proj;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffBurn;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.damage.DamageBuilder;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.awt.geom.Rectangle2D;

public class ProjSwordCinder extends Projectile {

    public ProjSwordCinder(final LogicModule l, final Player o, final double x, final double y) {
        super(l, o, x, y, 200);
        this.screenshake = true;
        this.hitbox = new Rectangle2D.Double[1];
        if (o.getFacing() == Globals.RIGHT) {
            this.hitbox[0] = new Rectangle2D.Double(this.x, this.y - 180, 280, 180);
        } else {
            this.hitbox[0] = new Rectangle2D.Double(this.x - 280, this.y - 180, 280, 180);
        }
    }

    @Override
    public int calculateDamage(final boolean isCrit) {
        final Player owner = getOwner();
        double baseValue = owner.getSkill(Globals.SWORD_CINDER).getSkillData().getBaseValue();
        double multValue = owner.getSkill(Globals.SWORD_CINDER).getSkillData().getMultValue();
        double damage = owner.rollDamage() * (baseValue + multValue * owner.getSkillLevel(Globals.SWORD_CINDER));
        damage = (isCrit) ? owner.criticalDamage(damage) : damage;
        return (int) damage;
    }

    @Override
    public void applyDamage(Player target) {
        final Player owner = getOwner();
        double bonusCritChc = owner.getSkill(Globals.SWORD_CINDER).getCustomValue(3);
        final boolean isCrit = owner.rollCrit((owner.isSkillMaxed(Globals.SWORD_CINDER)) ? bonusCritChc : 0);
        final int damage = calculateDamage(isCrit);
        target.queueDamage(new DamageBuilder()
                .setDamage(damage)
                .setOwner(owner)
                .setTarget(target)
                .setIsCrit(isCrit)
                .build());
        target.queueBuff(new BuffKnockback(this.logic, 300, (owner.getFacing() == Globals.RIGHT) ? 5 : -5, -8, owner, target));
        double buffDuration = owner.getSkill(Globals.SWORD_CINDER).getCustomValue(0);
        double damageAmp = owner.getSkill(Globals.SWORD_CINDER).getCustomValue(1);
        target.queueBuff(new BuffBurn(this.logic, (int) buffDuration, owner.getSkillLevel(Globals.SWORD_CINDER) * damageAmp,
                owner.isSkillMaxed(Globals.SWORD_CINDER) ? owner.rollDamage() : 0, owner, target));
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PARTICLE_EFFECT;
        bytes[1] = Globals.Particles.BURN_BUFF_EMITTER.getParticleCode();
        bytes[2] = target.getKey();
        PacketSender.sendAll(bytes, this.logic);
    }

}
