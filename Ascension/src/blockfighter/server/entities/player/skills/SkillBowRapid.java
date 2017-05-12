package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBowRapid;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillBowRapid extends Skill {

    public static final String CUSTOMHEADER_MAXLVLDMGMULT = "[maxleveldamagemult]",
            CUSTOMHEADER_MAXLVLBONUSCHC = "[maxlevelbonuschance]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        CUSTOMHEADER_MAXLVLDMGMULT,
        CUSTOMHEADER_MAXLVLBONUSCHC};

    private static final HashMap<String, Double> CUSTOM_VALUES = new HashMap<>(2);

    private static final byte SKILL_CODE = Globals.BOW_RAPID;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;

    private static final byte REQ_EQUIP_SLOT = Globals.EQUIP_WEAPON;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_BOW_RAPID;
    private static final int SKILL_DURATION = 550;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);

        CUSTOM_VALUES.put(CUSTOMHEADER_MAXLVLDMGMULT, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_MAXLVLDMGMULT));
        CUSTOM_VALUES.put(CUSTOMHEADER_MAXLVLBONUSCHC, Globals.loadDoubleValue(data, dataHeaders, CUSTOMHEADER_MAXLVLBONUSCHC));
    }

    public SkillBowRapid(final LogicModule l) {
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
        final int numHits = 3;

        if (Globals.hasPastDuration(duration, 150 + player.getSkillCounter() * 150) && player.getSkillCounter() < numHits) {
            if (player.getSkillCounter() != 0) {
                player.setFrame((byte) 2);
            }
            player.incrementSkillCounter();
            double projY = player.getY();
            if (player.getSkillCounter() == 1) {
                projY = player.getY() - 20;
            } else if (player.getSkillCounter() == 3) {
                projY = player.getY() + 20;
            }
            final ProjBowRapid proj = new ProjBowRapid(this.logic, player, player.getX(), projY);
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic, Globals.Particles.BOW_RAPID.getParticleCode(), proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    player.getFacing());
            PacketSender.sendParticle(this.logic, Globals.Particles.BOW_RAPID2.getParticleCode(), (player.getFacing() == Globals.LEFT) ? player.getX() - 20 : player.getX() - 40, proj.getHitbox()[0].getY() - 40,
                    player.getFacing());
            PacketSender.sendSFX(this.logic, Globals.SFXs.RAPID.getSfxCode(), player.getX(), player.getY());
        }
        player.updateSkillEnd(duration, getSkillDuration(), true, false);
    }

}
