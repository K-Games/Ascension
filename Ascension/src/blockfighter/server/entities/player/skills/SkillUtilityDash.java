package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffUtilityDash;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillUtilityDash extends Skill {

    private static final byte SKILL_CODE = Globals.UTILITY_DASH;

    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final byte REQ_EQUIP_SLOT = Globals.ITEM_OFFHAND;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_UTILITY_DASH;
    private static final int SKILL_DURATION = 400;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, null);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
    }

    public SkillUtilityDash(final LogicModule l) {
        super(l);
    }

    @Override
    public Double getCustomValue(String customHeader) {
        return null;
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
        if (!player.isStunned() && !player.isKnockback()) {
            player.setXSpeed((player.getFacing() == Globals.RIGHT) ? 8.5 : -8.5);
        }
        if (player.isSkillMaxed(Globals.UTILITY_DASH) && !player.isInvulnerable()) {
            player.setInvulnerable(true);
        }

        if (player.getSkillCounter() == 0) {
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_DASH, player.getKey(), player.getFacing());
            player.setYSpeed(-5.5);
            player.incrementSkillCounter();
        }

        if (player.getSkillCounter() == 1 && duration >= getSkillDuration()) {
            player.incrementSkillCounter();
            player.queueBuff(new BuffUtilityDash(this.logic, 5000, getBaseValue() + getMultValue() * player.getSkillLevel(Globals.UTILITY_DASH), player));
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_DASHBUFF, player.getKey());
        }
        player.updateSkillEnd(duration, getSkillDuration(), true, true);
    }
}
