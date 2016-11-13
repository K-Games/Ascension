package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBowFrost;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillBowFrost extends Skill {

    private static final String BASESTUN_HEADER = "[basestun]",
            MAXLEVELSTUN_HEADER = "[maxlevelstun]",
            MAXLEVELBONUSPROJ_HEADER = "[maxlevelbonusproj]",
            MAXLEVELBONUSDAMAGE_HEADER = "[maxlevelbonusdamage]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        BASESTUN_HEADER,
        MAXLEVELSTUN_HEADER,
        MAXLEVELBONUSPROJ_HEADER,
        MAXLEVELBONUSDAMAGE_HEADER
    };

    private static final double BASE_STUN, MAX_LEVEL_STUN, MAX_LEVEL_BONUS_DAMAGE;
    private static final int MAX_LEVEL_BONUS_PROJ;

    private static final byte SKILL_CODE = Globals.BOW_FROST;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;
    private static final double BASE_VALUE, MULT_VALUE;

    private static final byte REQ_EQUIP_SLOT = Globals.ITEM_WEAPON;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_BOW_FROST;
    private static final int SKILL_DURATION = 380;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);

        BASE_STUN = Globals.loadDoubleValue(data, dataHeaders, BASESTUN_HEADER);
        MAX_LEVEL_STUN = Globals.loadDoubleValue(data, dataHeaders, MAXLEVELSTUN_HEADER);
        MAX_LEVEL_BONUS_DAMAGE = Globals.loadDoubleValue(data, dataHeaders, MAXLEVELBONUSDAMAGE_HEADER);
        MAX_LEVEL_BONUS_PROJ = (int) Globals.loadDoubleValue(data, dataHeaders, MAXLEVELBONUSPROJ_HEADER);
    }

    public SkillBowFrost(final LogicModule l) {
        super(l);
    }

    public double getStunDuration() {
        return (isMaxed()) ? MAX_LEVEL_STUN : BASE_STUN;
    }

    public double getSecondaryDamage() {
        return MAX_LEVEL_BONUS_DAMAGE;
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
        final int numHits = player.isSkillMaxed(Globals.BOW_FROST) ? MAX_LEVEL_BONUS_PROJ + 1 : 1;
        if (Globals.hasPastDuration(duration, 160 + player.getSkillCounter() * 90) && player.getSkillCounter() < numHits) {
            player.incrementSkillCounter();
            final ProjBowFrost proj = new ProjBowFrost(this.logic, player, player.getX(), player.getY(), false);
            this.logic.queueAddProj(proj);
            if (player.getSkillCounter() == 1) {
                PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_BOW_FROSTARROW, player.getX(), player.getY(),
                        player.getFacing());
            }
        }

        player.updateSkillEnd(duration, getSkillDuration(), false, false);
    }

}
