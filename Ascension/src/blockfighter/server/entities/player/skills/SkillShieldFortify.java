package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffShieldFortify;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillShieldFortify extends Skill {

    private static final String BUFFDURATION_HEADER = "[buffduration]",
            HEAL_HEADER = "[heal]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        BUFFDURATION_HEADER,
        HEAL_HEADER
    };

    private static final double BUFF_DURATION,
            HEAL_AMOUNT;

    private static final byte SKILL_CODE = Globals.SHIELD_FORTIFY;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final double MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final byte REQ_EQUIP_SLOT = Globals.ITEM_OFFHAND;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_SHIELD_FORTIFY;
    private static final int SKILL_DURATION = 350;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
        BUFF_DURATION = Globals.loadDoubleValue(data, dataHeaders, BUFFDURATION_HEADER);
        HEAL_AMOUNT = Globals.loadDoubleValue(data, dataHeaders, HEAL_HEADER);
    }

    public SkillShieldFortify(final LogicModule l) {
        super(l);
    }

    public double getHealAmount() {
        return HEAL_AMOUNT;
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
    public double getMaxCooldown() {
        return MAX_COOLDOWN;
    }

    @Override
    public double getMultValue() {
        return MULT_VALUE;
    }

    @Override
    public Byte getReqEquipSlot() {
        return null;
    }

    @Override
    public Byte getReqWeapon() {
        return null;
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
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_FORTIFY, player.getKey());
            PacketSender.sendSFX(this.logic.getRoom().getRoomNumber(), Globals.SFX_FORTIFY, player.getX(), player.getY());
        }

        if (Globals.hasPastDuration(duration, getSkillDuration()) && player.getSkillCounter() == 1) {
            player.incrementSkillCounter();
            player.queueBuff(new BuffShieldFortify(this.logic, (int) BUFF_DURATION, BASE_VALUE + MULT_VALUE * player.getSkillLevel(Globals.SHIELD_FORTIFY), player));
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_FORTIFYBUFF, player.getKey());
        }

        player.updateSkillEnd(duration, getSkillDuration(), false, false);
    }

}
