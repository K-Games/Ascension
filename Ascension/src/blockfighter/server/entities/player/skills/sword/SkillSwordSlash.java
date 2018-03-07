package blockfighter.server.entities.player.skills.sword;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffSwordSlash;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.proj.ProjSwordSlash;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillSwordSlash extends Skill {

    public static final String[] CUSTOM_DATA_HEADERS;
    private static final HashMap<String, Double> CUSTOM_VALUES;

    private static final byte SKILL_CODE = Globals.SWORD_SLASH;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final int REQ_LEVEL;
    private static final byte REQ_EQUIP_SLOT = Globals.EQUIP_WEAPON;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_SWORD_SLASH;
    private static final int SKILL_DURATION = 350;

    static {
        String[] data = Globals.loadSkillRawData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data);

        CUSTOM_DATA_HEADERS = Globals.getSkillCustomHeaders(data, dataHeaders);
        CUSTOM_VALUES = new HashMap<>(CUSTOM_DATA_HEADERS.length);

        REQ_WEAPON = Globals.loadSkillReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
        REQ_LEVEL = Globals.loadSkillReqLevel(data, dataHeaders);

        for (String customHeader : CUSTOM_DATA_HEADERS) {
            CUSTOM_VALUES.put(customHeader, Globals.loadDoubleValue(data, dataHeaders, customHeader));
        }
    }

    public SkillSwordSlash(final LogicModule l) {
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
        final int numHits = 3;
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (player.getSkillCounter() == 0) {
            player.incrementSkillCounter();
            if (player.isSkillMaxed(Globals.SWORD_SLASH)) {
                double buffDuration = getCustomValue(CUSTOM_DATA_HEADERS[0]);
                player.queueBuff(new BuffSwordSlash(this.logic, (int) buffDuration, getCustomValue(CUSTOM_DATA_HEADERS[1]), player));
                PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_SLASH_BUFF_EMITTER.getParticleCode(), player.getKey());
            }
        }
        if (Globals.hasPastDuration(duration, (30 + 110 * (player.getSkillCounter() - 1))) && (player.getSkillCounter() - 1) < numHits) {
            player.setFrame((byte) 0);
            player.incrementSkillCounter();
            final ProjSwordSlash proj = new ProjSwordSlash(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            switch (player.getSkillCounter() - 1) {
                case 1:
                    PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_SLASH1.getParticleCode(), player.getX(), player.getY(), player.getFacing());
                    PacketSender.sendSFX(this.logic, Globals.SFXs.SLASH.getSfxCode(), player.getX(), player.getY());
                    break;
                case 2:
                    PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_SLASH2.getParticleCode(), player.getX(), player.getY(), player.getFacing());
                    PacketSender.sendSFX(this.logic, Globals.SFXs.SLASH.getSfxCode(), player.getX(), player.getY());
                    break;
                case 3:
                    PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_SLASH3.getParticleCode(), player.getX(), player.getY(), player.getFacing());
                    PacketSender.sendSFX(this.logic, Globals.SFXs.SLASH.getSfxCode(), player.getX(), player.getY());
                    break;
                default:
                    break;
            }
        }

        player.updateSkillEnd(duration, getSkillDuration(), true, false);
    }

    @Override
    public int getReqLevel() {
        return REQ_LEVEL;
    }
}