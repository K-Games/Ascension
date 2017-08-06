package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjShieldRoar;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillShieldRoar extends Skill {

    public static final String CUSTOMHEADER_BASEDEF = "[basedefense]",
            CUSTOMHEADER_MULTDEF = "[multdefense]",
            CUSTOMHEADER_MULTBASEDEF = "[multbasedefense]",
            CUSTOMHEADER_STUN = "[stunduration]";

    public static final String[] CUSTOM_DATA_HEADERS = {
        CUSTOMHEADER_BASEDEF,
        CUSTOMHEADER_MULTDEF,
        CUSTOMHEADER_STUN,
        CUSTOMHEADER_MULTBASEDEF
    };

    private static final HashMap<String, Double> CUSTOM_VALUES = new HashMap<>(4);

    private static final byte SKILL_CODE = Globals.SHIELD_ROAR;

    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final int REQ_LEVEL;
    private static final byte REQ_EQUIP_SLOT = Globals.EQUIP_OFFHAND;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_SHIELD_ROAR;
    private static final int SKILL_DURATION = 500;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        REQ_LEVEL = Globals.loadSkillReqLevel(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
        CUSTOM_VALUES.put(CUSTOMHEADER_BASEDEF, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_BASEDEF));
        CUSTOM_VALUES.put(CUSTOMHEADER_MULTDEF, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_MULTDEF));
        CUSTOM_VALUES.put(CUSTOMHEADER_STUN, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_STUN));
        CUSTOM_VALUES.put(CUSTOMHEADER_MULTBASEDEF, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_MULTBASEDEF));
    }

    public SkillShieldRoar(final LogicModule l) {
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
        final long duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        //Send roar particle
        if (player.getSkillCounter() == 0) {
            PacketSender.sendParticle(this.logic, Globals.Particles.SHIELD_ROAR.getParticleCode(), player.getKey(), player.getFacing());
            player.incrementSkillCounter();
        }
        //Spawn projectile.
        if (Globals.hasPastDuration(duration, 200) && player.getSkillCounter() == 1) {
            PacketSender.sendScreenShake(player, 8, 8, 200);
            player.incrementSkillCounter();
            final ProjShieldRoar proj = new ProjShieldRoar(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
        }
        player.updateSkillEnd(duration, getSkillDuration(), false, false);
    }

    @Override
    public int getReqLevel() {
        return REQ_LEVEL;
    }
}
