package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBowStorm;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillBowStorm extends Skill {

    public static final String CUSTOMHEADER_DURATION = "[duration]",
            CUSTOMHEADER_MAXLVLBONUSCRITDMG = "[maxlevelbonuscritdamage]";

    public static final String[] CUSTOM_DATA_HEADERS = {
        CUSTOMHEADER_DURATION,
        CUSTOMHEADER_MAXLVLBONUSCRITDMG
    };

    private static final HashMap<String, Double> CUSTOM_VALUES = new HashMap<>(2);

    private static final byte SKILL_CODE = Globals.BOW_STORM;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final int REQ_LEVEL;

    private static final byte REQ_EQUIP_SLOT = Globals.EQUIP_WEAPON;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_BOW_STORM;
    private static final int SKILL_DURATION = 200;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        REQ_LEVEL = Globals.loadSkillReqLevel(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);

        CUSTOM_VALUES.put(CUSTOMHEADER_MAXLVLBONUSCRITDMG, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_MAXLVLBONUSCRITDMG));
        CUSTOM_VALUES.put(CUSTOMHEADER_DURATION, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_DURATION));
    }

    public SkillBowStorm(final LogicModule l) {
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
        if (Globals.hasPastDuration(duration, 100) && player.getSkillCounter() == 0) {
            player.incrementSkillCounter();
            final ProjBowStorm proj = new ProjBowStorm(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic, Globals.Particles.BOW_STORM_EMITTER.getParticleCode(), proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    player.getFacing());
        }
        player.updateSkillEnd(duration, this.getSkillDuration(), false, false);
    }

    @Override
    public int getReqLevel() {
        return REQ_LEVEL;
    }
}
