package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjShieldCharge;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillShieldCharge extends Skill {

    private static final String STUN_HEADER = "[stunduration]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        STUN_HEADER
    };

    private static final double STUN_DURATION;

    private static final byte SKILL_CODE = Globals.SHIELD_CHARGE;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final double MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;

    private static final byte REQ_EQUIP_SLOT = Globals.ITEM_OFFHAND;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_SHIELD_CHARGE;
    private static final int SKILL_DURATION = 750;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
        STUN_DURATION = Globals.loadDoubleValue(data, dataHeaders, STUN_HEADER);
    }

    public SkillShieldCharge(final LogicModule l) {
        super(l);
    }

    public double getStunDuration() {
        return STUN_DURATION;
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
        return REQ_EQUIP_SLOT;
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
        player.setXSpeed((player.getFacing() == Globals.RIGHT) ? 18 : -18);
        if (player.getSkillCounter() == 0) {
            final ProjShieldCharge proj = new ProjShieldCharge(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_CHARGE, player.getKey(), player.getFacing());
            player.incrementSkillCounter();
        }
        player.updateSkillEnd(duration, getSkillDuration(), false, false);
    }
}
