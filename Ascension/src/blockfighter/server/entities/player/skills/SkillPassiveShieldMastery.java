package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.items.Items;
import blockfighter.server.entities.player.Player;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillPassiveShieldMastery extends SkillPassive {

    private static final String BASEDMGREDUCT_HEADER = "[basedmgreduct]",
            MULTDMGREDUCT_HEADER = "[multdmgreduct]";
    private static final String[] CUSTOM_DATA_HEADERS = {
        BASEDMGREDUCT_HEADER,
        MULTDMGREDUCT_HEADER
    };
    private static final double BASE_DMG_REDUCT, MULT_DMG_REDUCT;

    private static final byte SKILL_CODE = Globals.PASSIVE_SHIELDMASTERY;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final double MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);

        BASE_DMG_REDUCT = Globals.loadDoubleValue(data, dataHeaders, BASEDMGREDUCT_HEADER);
        MULT_DMG_REDUCT = Globals.loadDoubleValue(data, dataHeaders, MULTDMGREDUCT_HEADER);
    }

    public SkillPassiveShieldMastery(final LogicModule l) {
        super(l);
    }

    public double getBaseDmgReduct() {
        return BASE_DMG_REDUCT;
    }

    public double getMultDmgReduct() {
        return MULT_DMG_REDUCT;
    }

    @Override
    public double getBaseValue() {
        return BASE_VALUE;
    }

    @Override
    public double getMaxCooldown() {
        return MAX_COOLDOWN;
    }

    @Override
    public double getMultValue() {
        return MULT_VALUE;
    }

    @Override
    public Byte getReqWeapon() {
        return REQ_WEAPON;
    }

    @Override
    public byte getSkillCode() {
        return SKILL_CODE;
    }

    @Override
    public boolean isPassive() {
        return IS_PASSIVE;
    }

    @Override
    public boolean canCast(final Player player) {
        return Items.getItemType(player.getEquips()[Globals.ITEM_WEAPON]) == Globals.ITEM_SWORD
                && Items.getItemType(player.getEquips()[Globals.ITEM_OFFHAND]) == Globals.ITEM_SHIELD;
    }
}
