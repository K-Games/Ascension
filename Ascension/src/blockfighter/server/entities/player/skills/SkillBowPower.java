package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBowPower;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillBowPower extends Skill {

    public static final String CUSTOMHEADER_MAXLEVELBONUSCRITDMG = "[maxlevelbonuscritdamage]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        CUSTOMHEADER_MAXLEVELBONUSCRITDMG
    };

    private static final HashMap<String, Double> CUSTOM_VALUES = new HashMap<>(1);

    private static final byte SKILL_CODE = Globals.BOW_POWER;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;
    private static final double BASE_VALUE, MULT_VALUE;

    private static final byte REQ_EQUIP_SLOT = Globals.ITEM_WEAPON;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_BOW_POWER;
    private static final int SKILL_DURATION = 1400;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);

        CUSTOM_VALUES.put(CUSTOMHEADER_MAXLEVELBONUSCRITDMG, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_MAXLEVELBONUSCRITDMG));
    }

    public SkillBowPower(final LogicModule l) {
        super(l);
    }

    @Override
    public Double getCustomValue(String customHeader) {
        return CUSTOM_VALUES.get(customHeader);
    }

    @Override
    public byte castPlayerState() {
        return PLAYER_STATE;
    }

    @Override
    public double getBaseValue() {
        return BASE_VALUE;
    }

    @Override
    public long getMaxCooldown() {
        return MAX_COOLDOWN;
    }

    @Override
    public double getMultValue() {
        return MULT_VALUE;
    }

    @Override
    public byte getReqEquipSlot() {
        return REQ_EQUIP_SLOT;
    }

    @Override
    public byte getReqWeapon() {
        return REQ_WEAPON;
    }

    @Override
    public byte getSkillCode() {
        return SKILL_CODE;
    }

    @Override
    public int getSkillDuration() {
        return SKILL_DURATION;
    }

    @Override
    public boolean isPassive() {
        return IS_PASSIVE;
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (player.getSkillCounter() == 0) {
            if (player.isSkillMaxed(Globals.BOW_POWER)) {
                player.setHyperStance(true);
            }
            PacketSender.sendSFX(this.logic, Globals.SFXs.POWER2.getSfxCode(), player.getX(), player.getY());
        }
        if (duration <= 400 && Globals.hasPastDuration(duration, player.getSkillCounter() * 20) && player.getSkillCounter() < 20) {
            PacketSender.sendParticle(this.logic, Globals.Particles.BOW_POWER_CHARGE.getParticleCode(), player.getKey());
            player.incrementSkillCounter();
        }
        if (Globals.hasPastDuration(duration, 800) && player.getSkillCounter() < 21) {
            player.incrementSkillCounter();
            final ProjBowPower proj = new ProjBowPower(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic, Globals.Particles.BOW_POWER.getParticleCode(), player.getX(), player.getY(),
                    player.getFacing());
            PacketSender.sendSFX(this.logic, Globals.SFXs.POWER.getSfxCode(), player.getX(), player.getY());
        }
        if (player.updateSkillEnd(duration >= getSkillDuration() || (!player.isSkillMaxed(Globals.BOW_POWER) && duration < 800 && (player.isStunned() || player.isKnockback())))) {
            player.setHyperStance(false);
        }
    }

}
