package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjSwordVorpal;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillSwordVorpal extends Skill {

    private static final String BASEBONUSCRITDMG_HEADER = "[basebonuscritdamage]",
            MULTBONUSCRITDMG_HEADER = "[multbonuscritdamage]",
            BONUSCRITCHC_HEADER = "[bonuscritchc]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        BASEBONUSCRITDMG_HEADER,
        BONUSCRITCHC_HEADER,
        MULTBONUSCRITDMG_HEADER
    };

    private static final double BASE_BONUS_CRIT_DAMAGE, MULT_BONUS_CRIT_DAMAGE, BONUS_CRIT_CHANCE;

    private static final byte SKILL_CODE = Globals.SWORD_VORPAL;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final double MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final byte REQ_EQUIP_SLOT = Globals.ITEM_WEAPON;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_SWORD_VORPAL;
    private static final int SKILL_DURATION = 800;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER) * 100;
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER) * 100;
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);

        BASE_BONUS_CRIT_DAMAGE = Globals.loadDoubleValue(data, dataHeaders, BASEBONUSCRITDMG_HEADER) * 100;
        MULT_BONUS_CRIT_DAMAGE = Globals.loadDoubleValue(data, dataHeaders, MULTBONUSCRITDMG_HEADER) * 100;
        BONUS_CRIT_CHANCE = Globals.loadDoubleValue(data, dataHeaders, BONUSCRITCHC_HEADER) * 100;
    }

    public SkillSwordVorpal(final LogicModule l) {
        super(l);
    }

    public double getBaseBonusCritDmg() {
        return BASE_BONUS_CRIT_DAMAGE;
    }

    public double getMultBonusCritDmg() {
        return MULT_BONUS_CRIT_DAMAGE;
    }

    public double getBonusCritChance() {
        return BONUS_CRIT_CHANCE;
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
        int skillTime = player.isSkillMaxed(Globals.SWORD_VORPAL) ? 150 : 170,
                numHits = player.isSkillMaxed(Globals.SWORD_VORPAL) ? 5 : 3;
        if (Globals.hasPastDuration(duration, skillTime * player.getSkillCounter()) && player.getSkillCounter() < numHits) {
            final ProjSwordVorpal proj = new ProjSwordVorpal(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            player.setFrame((byte) 0);
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SWORD_VORPAL, player.getX(), player.getY(), player.getFacing());
            player.incrementSkillCounter();
        }

        player.updateSkillEnd(duration, getSkillDuration(), true, false);
    }
}
