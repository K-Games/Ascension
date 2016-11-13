package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBowVolley;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillBowVolley extends Skill {

    private static final String MAXLVLBUFFDMG_HEADER = "[maxlevelbuffdamage]",
            MAXLVLBUFFDURATION_HEADER = "[maxlevelbuffduration]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        MAXLVLBUFFDMG_HEADER,
        MAXLVLBUFFDURATION_HEADER};

    private static final double MAX_LEVEL_BUFF_DAMAGE, MAX_LEVEL_BUFF_DURATION;

    private static final byte SKILL_CODE = Globals.BOW_VOLLEY;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;

    private static final byte REQ_EQUIP_SLOT = Globals.ITEM_WEAPON;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_BOW_VOLLEY;
    private static final int SKILL_DURATION = 1900;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);

        MAX_LEVEL_BUFF_DAMAGE = Globals.loadDoubleValue(data, dataHeaders, MAXLVLBUFFDMG_HEADER);
        MAX_LEVEL_BUFF_DURATION = Globals.loadDoubleValue(data, dataHeaders, MAXLVLBUFFDURATION_HEADER);
    }

    public SkillBowVolley(final LogicModule l) {
        super(l);
    }

    public double getBuffDamage() {
        return MAX_LEVEL_BUFF_DAMAGE;
    }

    public double getBuffDuration() {
        return MAX_LEVEL_BUFF_DURATION;
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
        final int numHits = 20;
        if (Globals.hasPastDuration(duration, player.getSkillCounter() * 100) && player.getSkillCounter() < numHits) {
            final ProjBowVolley proj = new ProjBowVolley(this.logic, player, player.getX(),
                    player.getY());
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_BOW_VOLLEYARROW, player.getX(), proj.getHitbox()[0].getY(),
                    player.getFacing());
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_BOW_VOLLEYBOW, player.getKey(), player.getFacing());
            player.incrementSkillCounter();
            PacketSender.sendSFX(this.logic.getRoom().getRoomNumber(), Globals.SFX_VOLLEY, player.getX(), player.getY());
        }
        player.updateSkillEnd(duration, getSkillDuration(), true, true);
    }
}
