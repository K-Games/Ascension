package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBowArc;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillBowArc extends Skill {

    private static final String LIFESTEAL_HEADER = "[lifesteal]",
            MAXLIFESTEAL_HEADER = "[maxlifesteal]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        LIFESTEAL_HEADER,
        MAXLIFESTEAL_HEADER
    };

    private static final double LIFESTEAL, MAX_LIFESTEAL;

    private static final byte SKILL_CODE = Globals.BOW_ARC;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final double MAX_COOLDOWN;
    private static final double BASE_VALUE, MULT_VALUE;

    private static final byte REQ_EQUIP_SLOT = Globals.ITEM_WEAPON;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_BOW_ARC;
    private static final int SKILL_DURATION = 300;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);

        LIFESTEAL = Globals.loadDoubleValue(data, dataHeaders, LIFESTEAL_HEADER);
        MAX_LIFESTEAL = Globals.loadDoubleValue(data, dataHeaders, MAXLIFESTEAL_HEADER);
    }

    public double getLifesteal() {
        return LIFESTEAL;
    }

    public double getMaxLifesteal() {
        return MAX_LIFESTEAL;
    }

    public SkillBowArc(final LogicModule l) {
        super(l);
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
        final int numHits = 3;
        if (player.getSkillCounter() < numHits && Globals.hasPastDuration(duration, 100 + player.getSkillCounter() * 50)) {
            player.incrementSkillCounter();
            final ProjBowArc proj = new ProjBowArc(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            if (player.getSkillCounter() == 1) {
                PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_BOW_ARC, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                        player.getFacing());
                PacketSender.sendSFX(this.logic.getRoom().getRoomNumber(), Globals.SFX_ARC, player.getX(), player.getY());
            }
        }
        player.updateSkillEnd(duration, getSkillDuration(), false, false);
    }
}
