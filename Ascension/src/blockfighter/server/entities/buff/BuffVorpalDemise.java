package blockfighter.server.entities.buff;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.damage.DamageBuilder;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.sword.SkillSwordVorpalDemise;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;

public class BuffVorpalDemise extends Buff implements BuffSpeedDecrease {

    private boolean damageDealt = false;

    public BuffVorpalDemise(final LogicModule l, final int d, final Player o, final Player t) {
        super(l, d, o, t);
        super.setDebuff(true);
    }

    @Override
    public double getXSpeedDecrease() {
        return getOwner().getSkill(Globals.SWORD_VORPAL_DEMISE).getCustomValue(SkillSwordVorpalDemise.CUSTOM_DATA_HEADERS[1]);
    }

    @Override
    public double getYSpeedDecrease() {
        return getOwner().getSkill(Globals.SWORD_VORPAL_DEMISE).getCustomValue(SkillSwordVorpalDemise.CUSTOM_DATA_HEADERS[1]);
    }

    @Override
    public void update() {
        super.update();
        long timeSinceStart = Globals.nsToMs(this.logic.getTime() - this.buffStartTime);
        if (!damageDealt && timeSinceStart >= getOwner().getSkill(Globals.SWORD_VORPAL_DEMISE).getCustomValue(SkillSwordVorpalDemise.CUSTOM_DATA_HEADERS[0])) {
            if (getTarget() != null) {
                double damageRoll = getOwner().rollDamage() * getOwner().getSkill(Globals.SWORD_VORPAL_DEMISE).getBaseValue();
                boolean isCrit = getOwner().rollCrit();
                damageRoll = (isCrit) ? getOwner().criticalDamage(damageRoll) : damageRoll;
                Damage dmg = new DamageBuilder()
                        .setDamage((int) damageRoll)
                        .setCanProc(false)
                        .setShowParticle(false)
                        .setIsCrit(isCrit)
                        .setOwner(getOwner())
                        .setTarget(getTarget())
                        .build();
                getTarget().queueDamage(dmg);
                PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_VORPAL_DEMISE.getParticleCode(), getTarget().getKey());
            }
            damageDealt = true;
        }
    }
}
