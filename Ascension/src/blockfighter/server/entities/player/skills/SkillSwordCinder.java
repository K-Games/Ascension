package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjSwordCinder;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillSwordCinder extends Skill {

    private ProjSwordCinder proj;

    public static final String CUSTOMHEADER_BUFFDURATION = "[buffduration]",
            CUSTOMHEADER_DMGAMP = "[damageamp]",
            CUSTOMHEADER_BURNDMG = "[burndamage]",
            CUSTOMHEADER_BONUSCRITCHC = "[bonuscritchc]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        CUSTOMHEADER_BUFFDURATION,
        CUSTOMHEADER_DMGAMP,
        CUSTOMHEADER_BURNDMG,
        CUSTOMHEADER_BONUSCRITCHC
    };

    private static final HashMap<String, Double> CUSTOM_VALUES = new HashMap<>(4);

    private static final byte SKILL_CODE = Globals.SWORD_CINDER;

    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final byte REQ_EQUIP_SLOT = Globals.ITEM_WEAPON;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_SWORD_CINDER;
    private static final int SKILL_DURATION = 250;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
        CUSTOM_VALUES.put(CUSTOMHEADER_BUFFDURATION, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_BUFFDURATION));
        CUSTOM_VALUES.put(CUSTOMHEADER_DMGAMP, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_DMGAMP));
        CUSTOM_VALUES.put(CUSTOMHEADER_BURNDMG, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_BURNDMG));
        CUSTOM_VALUES.put(CUSTOMHEADER_BONUSCRITCHC, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_BONUSCRITCHC));
    }

    public SkillSwordCinder(final LogicModule l) {
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
            player.incrementSkillCounter();
            proj = new ProjSwordCinder(this.logic, player, player.getX(), player.getY());
            PacketSender.sendParticle(this.logic, Globals.PARTICLE_SWORD_CINDER, player.getX(), player.getY(), player.getFacing());
        }
        if (Globals.hasPastDuration(duration, 100) && player.getSkillCounter() == 1) {
            player.incrementSkillCounter();
            this.logic.queueAddProj(proj);
        }
        player.updateSkillEnd(duration, getSkillDuration(), true, false);
    }
}
