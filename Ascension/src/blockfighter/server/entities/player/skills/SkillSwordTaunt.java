package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffSwordTaunt;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjSwordTaunt;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillSwordTaunt extends Skill {

    private ProjSwordTaunt proj;

    private static final String BUFFDURATION_HEADER = "[buffduration]",
            DMGREDUCT_HEADER = "[damagereduct]",
            DMGINC_HEADER = "[damageinc]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        BUFFDURATION_HEADER,
        DMGREDUCT_HEADER,
        DMGINC_HEADER
    };

    private static final double BUFF_DURATION,
            DAMAGE_REDUCT,
            DAMAGE_INCREASE;

    private static final byte SKILL_CODE = Globals.SWORD_TAUNT;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final byte REQ_EQUIP_SLOT = Globals.ITEM_WEAPON;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_SWORD_TAUNT;
    private static final int SKILL_DURATION = 350;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
        BUFF_DURATION = Globals.loadDoubleValue(data, dataHeaders, BUFFDURATION_HEADER);
        DAMAGE_REDUCT = Globals.loadDoubleValue(data, dataHeaders, DMGREDUCT_HEADER);
        DAMAGE_INCREASE = Globals.loadDoubleValue(data, dataHeaders, DMGINC_HEADER);
    }

    public SkillSwordTaunt(final LogicModule l) {
        super(l);
    }

    public double getBuffDuration() {
        return BUFF_DURATION;
    }

    public double getDamageReduct() {
        return DAMAGE_REDUCT;
    }

    public double getDamageIncrease() {
        return DAMAGE_INCREASE;
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
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (player.getSkillCounter() == 0) {
            player.incrementSkillCounter();
            if (player.isSkillMaxed(Globals.SWORD_TAUNT)) {
                player.queueBuff(new BuffSwordTaunt(this.logic, (int) BUFF_DURATION, DAMAGE_REDUCT, DAMAGE_INCREASE, player));
                PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SWORD_TAUNTBUFF, player.getKey());
            }
            proj = new ProjSwordTaunt(this.logic, player, player.getX(), player.getY());
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SWORD_TAUNTAURA1, player.getKey());
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SWORD_TAUNT, player.getX(), player.getY(), player.getFacing());
        }
        if (Globals.hasPastDuration(duration, 100) && player.getSkillCounter() == 1) {
            player.incrementSkillCounter();
            this.logic.queueAddProj(proj);

        }
        player.updateSkillEnd(duration, getSkillDuration(), false, false);
    }

}
