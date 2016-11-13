package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjShieldRoar;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillShieldRoar extends Skill {

    private static final String BASEDEF_HEADER = "[basedefense]",
            MULTDEF_HEADER = "[multdefense]",
            MULTBASEDEF_HEADER = "[multbasedefense]",
            STUN_HEADER = "[stunduration]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        BASEDEF_HEADER,
        MULTDEF_HEADER,
        STUN_HEADER,
        MULTBASEDEF_HEADER
    };

    private static final double STUN_DURATION,
            BASE_DEFENSE,
            MULT_DEFENSE,
            MULT_BASE_DEFENSE;

    private static final byte SKILL_CODE = Globals.SHIELD_ROAR;

    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final byte REQ_EQUIP_SLOT = Globals.ITEM_OFFHAND;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_SHIELD_ROAR;
    private static final int SKILL_DURATION = 500;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
        STUN_DURATION = Globals.loadDoubleValue(data, dataHeaders, STUN_HEADER);
        BASE_DEFENSE = Globals.loadDoubleValue(data, dataHeaders, BASEDEF_HEADER);
        MULT_DEFENSE = Globals.loadDoubleValue(data, dataHeaders, MULTDEF_HEADER);
        MULT_BASE_DEFENSE = Globals.loadDoubleValue(data, dataHeaders, MULTBASEDEF_HEADER);
    }

    public SkillShieldRoar(final LogicModule l) {
        super(l);
    }

    public double getBaseDefense() {
        return BASE_DEFENSE;
    }

    public double getMultDefense() {
        return MULT_DEFENSE;
    }

    public double getMultBaseDefense() {
        return MULT_BASE_DEFENSE;
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
    public long getMaxCooldown() {
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
        final long duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        //Send roar particle
        if (player.getSkillCounter() == 0) {
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_ROAR, player.getKey(), player.getFacing());
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
}
