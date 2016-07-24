package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;

public class SkillPassiveShadowAttack extends Skill {

    public SkillPassiveShadowAttack(final LogicModule l) {
        super(l);
        this.skillCode = PASSIVE_SHADOWATTACK;
        this.maxCooldown = 200;
        this.isPassive = true;
    }

    public void updateSkillUse(final Player player, final Damage dmg) {
        if (Globals.rng(100) + 1 <= 20 + player.getSkillLevel(Skill.PASSIVE_SHADOWATTACK)) {
            player.getSkill(Skill.PASSIVE_SHADOWATTACK).setCooldown();
            player.sendCooldown(Skill.PASSIVE_SHADOWATTACK);
            PacketSender.sendParticle(this.logic.getRoom(), Globals.PARTICLE_PASSIVE_SHADOWATTACK, dmg.getDmgPoint().x, dmg.getDmgPoint().y);
            if (dmg.getTarget() != null) {
                final Damage shadow = new Damage((int) (dmg.getDamage() * 0.5D), false, dmg.getOwner(), dmg.getTarget(), false,
                        dmg.getDmgPoint());
                shadow.setHidden(true);
                dmg.getTarget().queueDamage(shadow);
            } else if (dmg.getMobTarget() != null) {
                final Damage shadow = new Damage((int) (dmg.getDamage() * 0.5D), false, dmg.getOwner(), dmg.getMobTarget(), false,
                        dmg.getDmgPoint());
                shadow.setHidden(true);
                dmg.getMobTarget().queueDamage(shadow);
            }
        }
    }
}
