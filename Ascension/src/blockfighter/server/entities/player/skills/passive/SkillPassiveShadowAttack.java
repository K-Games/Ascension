package blockfighter.server.entities.player.skills.passive;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.damage.DamageBuilder;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.SkillPassive;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.awt.geom.Point2D;

public class SkillPassiveShadowAttack extends SkillPassive {

    public static final byte SKILL_CODE = Globals.PASSIVE_SHADOWATTACK;

    public SkillPassiveShadowAttack(final LogicModule l) {
        super(l);
    }

    public void updateSkillUse(final Player player, final Damage dmg) {
        if (Globals.rng(100) + 1 <= 20 + getSkillData().getBaseValue() + getSkillData().getMultValue() * player.getSkillLevel(Globals.PASSIVE_SHADOWATTACK)) {
            player.getSkill(Globals.PASSIVE_SHADOWATTACK).setCooldown();
            player.sendCooldown(Globals.PASSIVE_SHADOWATTACK);
            PacketSender.sendParticle(this.logic, Globals.Particles.PASSIVE_SHADOWATTACK.getParticleCode(), dmg.getTarget().getX(), dmg.getTarget().getY());
            Point2D.Double newPos = new Point2D.Double(dmg.getDmgPoint().x, dmg.getDmgPoint().y + 20);
            if (dmg.getTarget() != null) {
                final Damage shadow = new DamageBuilder()
                        .setDamage((int) (dmg.getDamage() * 0.5D))
                        .setCanProc(false)
                        .setOwner(dmg.getOwner())
                        .setTarget(dmg.getTarget())
                        .setIsCrit(false)
                        .setShowParticle(false)
                        .setDmgPoint(newPos)
                        .build();
                dmg.getTarget().queueDamage(shadow);
            }
        }
    }

}
