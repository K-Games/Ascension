package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBowFrost;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillBowFrost extends Skill {

    public static final String CUSTOMHEADER_BASESTUN = "[basestun]",
            CUSTOMHEADER_MAXLEVELSTUN = "[maxlevelstun]",
            CUSTOMHEADER_MAXLEVELBONUSPROJ = "[maxlevelbonusproj]",
            CUSTOMHEADER_MAXLEVELBONUSDAMAGE = "[maxlevelbonusdamage]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        CUSTOMHEADER_BASESTUN,
        CUSTOMHEADER_MAXLEVELSTUN,
        CUSTOMHEADER_MAXLEVELBONUSPROJ,
        CUSTOMHEADER_MAXLEVELBONUSDAMAGE
    };

    private static final HashMap<String, Double> CUSTOM_VALUES = new HashMap<>(4);

    private static final byte SKILL_CODE = Globals.BOW_FROST;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;
    private static final double BASE_VALUE, MULT_VALUE;
    private static final int REQ_LEVEL;

    private static final byte REQ_EQUIP_SLOT = Globals.EQUIP_WEAPON;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_BOW_FROST;
    private static final int SKILL_DURATION = 380;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        REQ_LEVEL = Globals.loadSkillReqLevel(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);

        CUSTOM_VALUES.put(CUSTOMHEADER_BASESTUN, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_BASESTUN));
        CUSTOM_VALUES.put(CUSTOMHEADER_MAXLEVELSTUN, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_MAXLEVELSTUN));
        CUSTOM_VALUES.put(CUSTOMHEADER_MAXLEVELBONUSDAMAGE, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_MAXLEVELBONUSDAMAGE));
        CUSTOM_VALUES.put(CUSTOMHEADER_MAXLEVELBONUSPROJ, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_MAXLEVELBONUSPROJ));
    }

    public SkillBowFrost(final LogicModule l) {
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
        final int numHits = (int) (player.isSkillMaxed(Globals.BOW_FROST) ? CUSTOM_VALUES.get(CUSTOMHEADER_MAXLEVELBONUSPROJ) + 1 : 1);
        if (Globals.hasPastDuration(duration, 160 + player.getSkillCounter() * 90) && player.getSkillCounter() < numHits) {
            player.incrementSkillCounter();
            final ProjBowFrost proj = new ProjBowFrost(this.logic, player, player.getX(), player.getY(), false);
            this.logic.queueAddProj(proj);
            if (player.getSkillCounter() == 1) {
                PacketSender.sendParticle(this.logic, Globals.Particles.BOW_FROSTARROW_EMITTER.getParticleCode(), player.getX(), player.getY(),
                        player.getFacing());
            }
        }

        player.updateSkillEnd(duration, getSkillDuration(), false, false);
    }

    @Override
    public int getReqLevel() {
        return REQ_LEVEL;
    }
}
