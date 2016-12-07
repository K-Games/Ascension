package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjSwordVorpal;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillSwordVorpal extends Skill {

    public static final String CUSTOMHEADER_BASEBONUSCRITDMG = "[basebonuscritdamage]",
            CUSTOMHEADER_MULTBONUSCRITDMG = "[multbonuscritdamage]",
            CUSTOMHEADER_BONUSCRITCHC = "[bonuscritchc]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        CUSTOMHEADER_BASEBONUSCRITDMG,
        CUSTOMHEADER_MULTBONUSCRITDMG,
        CUSTOMHEADER_BONUSCRITCHC
    };

    private static final HashMap<String, Double> CUSTOM_VALUES = new HashMap<>(3);

    private static final byte SKILL_CODE = Globals.SWORD_VORPAL;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final byte REQ_EQUIP_SLOT = Globals.ITEM_WEAPON;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_SWORD_VORPAL;
    private static final int SKILL_DURATION = 400;

    private double projX, projY, destX;
    private boolean dashed = false;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);

        CUSTOM_VALUES.put(CUSTOMHEADER_BASEBONUSCRITDMG, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_BASEBONUSCRITDMG));
        CUSTOM_VALUES.put(CUSTOMHEADER_MULTBONUSCRITDMG, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_MULTBONUSCRITDMG));
        CUSTOM_VALUES.put(CUSTOMHEADER_BONUSCRITCHC, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_BONUSCRITCHC));
    }

    public SkillSwordVorpal(final LogicModule l) {
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

        int skillTime = 50, numHits = player.isSkillMaxed(Globals.SWORD_VORPAL) ? 5 : 3;
        final int dashDistance = 300, dashDuration = 80;
        if (Globals.hasPastDuration(duration, 100) && player.getSkillCounter() == 0) {
            this.destX = player.getX() + ((player.getFacing() == Globals.RIGHT) ? 1 : -1) * dashDistance;
            this.dashed = false;
            this.projX = player.getX();
            this.projY = player.getY();
            player.incrementSkillCounter();
        }

        if (!dashed && Globals.hasPastDuration(duration, 100 + dashDuration)) {
            this.dashed = true;
            player.setRemovingDebuff(false);
            player.setXSpeed(0);
            player.setPos(this.destX, player.getY());
        } else if (!dashed && player.getSkillCounter() > 0) {
            player.setXSpeed(((player.getFacing() == Globals.RIGHT) ? 1 : -1) * dashDistance / (dashDuration / Globals.nsToMs(Globals.SERVER_LOGIC_UPDATE)));
            player.setRemovingDebuff(true);
        }

        if (player.getSkillCounter() > 0 && Globals.hasPastDuration(duration, 100 + skillTime * (player.getSkillCounter() - 1)) && player.getSkillCounter() - 1 < numHits) {
            double randomY = this.projY + (Globals.rng(5) * 5 - 10);
            final ProjSwordVorpal proj = new ProjSwordVorpal(this.logic, player, this.projX, randomY);
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SWORD_VORPAL, this.projX, randomY, player.getFacing());
            player.incrementSkillCounter();
        }

        player.updateSkillEnd(duration, getSkillDuration(), false, false);
    }
}
