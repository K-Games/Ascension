package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffUtilityAdrenaline;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillUtilityAdrenaline extends Skill {

    public static final String CUSTOMHEADER_BUFFDURATION = "[buffduration]",
            CUSTOMHEADER_HEAL = "[heal]",
            CUSTOMHEADER_MOVESPEED_BASE = "[movespeedbase]",
            CUSTOMHEADER_MOVESPEED_MULT = "[movespeedmult]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        CUSTOMHEADER_BUFFDURATION,
        CUSTOMHEADER_HEAL,
        CUSTOMHEADER_MOVESPEED_BASE,
        CUSTOMHEADER_MOVESPEED_MULT
    };

    private static final HashMap<String, Double> CUSTOM_VALUES = new HashMap<>(2);

    private static final byte SKILL_CODE = Globals.UTILITY_ADRENALINE;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final byte REQ_EQUIP_SLOT = Globals.ITEM_OFFHAND;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_UTILITY_FORTIFY;
    private static final int SKILL_DURATION = 350;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
        CUSTOM_VALUES.put(CUSTOMHEADER_BUFFDURATION, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_BUFFDURATION));
        CUSTOM_VALUES.put(CUSTOMHEADER_HEAL, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_HEAL));
        CUSTOM_VALUES.put(CUSTOMHEADER_MOVESPEED_BASE, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_MOVESPEED_BASE));
        CUSTOM_VALUES.put(CUSTOMHEADER_MOVESPEED_MULT, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_MOVESPEED_MULT));
    }

    public SkillUtilityAdrenaline(final LogicModule l) {
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
        return -1;
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
            player.incrementSkillCounter();
            PacketSender.sendParticle(this.logic, Globals.Particles.UTILITY_ADRENALINE.getParticleCode(), player.getKey());
            PacketSender.sendSFX(this.logic, Globals.SFXs.FORTIFY.getSfxCode(), player.getX(), player.getY());
        }

        if (Globals.hasPastDuration(duration, getSkillDuration()) && player.getSkillCounter() == 1) {
            player.incrementSkillCounter();
            double buffDuration = getCustomValue(CUSTOMHEADER_BUFFDURATION);
            player.queueBuff(new BuffUtilityAdrenaline(this.logic, (int) buffDuration, BASE_VALUE + MULT_VALUE * player.getSkillLevel(Globals.UTILITY_ADRENALINE), player));
            PacketSender.sendParticle(this.logic, Globals.Particles.UTILITY_ADRENALINE_CLONE_EMITTER.getParticleCode(), player.getKey());
        }

        player.updateSkillEnd(duration, getSkillDuration(), false, false);
    }

}
