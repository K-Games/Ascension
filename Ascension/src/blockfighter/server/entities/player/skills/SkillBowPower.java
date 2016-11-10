package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBowPower;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillBowPower extends Skill {

    private static final String MAXLEVELBONUSCRITDMG_HEADER = "[maxlevelbonuscritdamage]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        MAXLEVELBONUSCRITDMG_HEADER
    };

    private static final double MAX_LEVEL_BONUS_CRIT_DAMAGE;

    private static final byte SKILL_CODE = Globals.BOW_POWER;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final double MAX_COOLDOWN;
    private static final double BASE_VALUE, MULT_VALUE;

    private static final byte REQ_EQUIP_SLOT = Globals.ITEM_WEAPON;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_BOW_POWER;
    private static final int SKILL_DURATION = 1400;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);

        MAX_LEVEL_BONUS_CRIT_DAMAGE = Globals.loadDoubleValue(data, dataHeaders, MAXLEVELBONUSCRITDMG_HEADER);
    }

    public SkillBowPower(final LogicModule l) {
        super(l);
    }

    public double getBonusCritDamage() {
        return (isMaxed()) ? MAX_LEVEL_BONUS_CRIT_DAMAGE : 0;
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
        if (player.getSkillCounter() == 0) {
            PacketSender.sendSFX(this.logic.getRoom().getRoomNumber(), Globals.SFX_POWER2, player.getX(), player.getY());
        }
        if (duration <= 400 && Globals.hasPastDuration(duration, player.getSkillCounter() * 20) && player.getSkillCounter() < 20) {
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_BOW_POWERCHARGE, player.getKey());
            player.incrementSkillCounter();
        }
        if (Globals.hasPastDuration(duration, 800) && player.getSkillCounter() < 21) {
            player.incrementSkillCounter();
            final ProjBowPower proj = new ProjBowPower(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_BOW_POWER, player.getX(), player.getY(),
                    player.getFacing());
            PacketSender.sendSFX(this.logic.getRoom().getRoomNumber(), Globals.SFX_POWER, player.getX(), player.getY());
        }
        player.updateSkillEnd(duration >= getSkillDuration() || (!player.isSkillMaxed(Globals.BOW_POWER) && duration < 800 && (player.isStunned() || player.isKnockback())));
    }

}
