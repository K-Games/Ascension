package blockfighter.server.entities.player.skills.passive;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.SkillPassive;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.awt.geom.Point2D;
import java.util.HashMap;

public class SkillPassiveShadowAttack extends SkillPassive {

    public static final byte SKILL_CODE = Globals.PASSIVE_SHADOWATTACK;
    public static final boolean IS_PASSIVE;
    public static final byte REQ_WEAPON;
    public static final long MAX_COOLDOWN;

    public static final double BASE_VALUE, MULT_VALUE;
    public static final int REQ_LEVEL;

    static {
        String[] data = Globals.loadSkillRawData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data);

        REQ_WEAPON = Globals.loadSkillReqWeapon(data, dataHeaders);
        REQ_LEVEL = Globals.loadSkillReqLevel(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
    }

    public SkillPassiveShadowAttack(final LogicModule l) {
        super(l);
    }

    public void updateSkillUse(final Player player, final Damage dmg) {
        if (Globals.rng(100) + 1 <= 20 + getBaseValue() + getMultValue() * player.getSkillLevel(Globals.PASSIVE_SHADOWATTACK)) {
            player.getSkill(Globals.PASSIVE_SHADOWATTACK).setCooldown();
            player.sendCooldown(Globals.PASSIVE_SHADOWATTACK);
            PacketSender.sendParticle(this.logic, Globals.Particles.PASSIVE_SHADOWATTACK.getParticleCode(), dmg.getDmgPoint().x, dmg.getDmgPoint().y);
            Point2D.Double newPos = new Point2D.Double(dmg.getDmgPoint().x, dmg.getDmgPoint().y + 20);
            if (dmg.getTarget() != null) {
                final Damage shadow = new Damage((int) (dmg.getDamage() * 0.5D), false, dmg.getOwner(), dmg.getTarget(), false, newPos, true);
                shadow.setHidden(true);
                shadow.setCanReflect(false);
                dmg.getTarget().queueDamage(shadow);
            } else if (dmg.getMobTarget() != null) {
                final Damage shadow = new Damage((int) (dmg.getDamage() * 0.5D), false, dmg.getOwner(), dmg.getMobTarget(), false, newPos, true);
                shadow.setHidden(true);
                shadow.setCanReflect(false);
                dmg.getMobTarget().queueDamage(shadow);
            }
        }
    }

}
