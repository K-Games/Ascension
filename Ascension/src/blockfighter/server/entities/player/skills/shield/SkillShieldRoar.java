package blockfighter.server.entities.player.skills.shield;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.proj.ProjShieldRoar;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillShieldRoar extends Skill {

    public static final String[] CUSTOM_DATA_HEADERS;
    public static final HashMap<String, Double> CUSTOM_VALUES;

    public static final byte SKILL_CODE = Globals.SHIELD_ROAR;

    public static final boolean IS_PASSIVE;
    public static final byte REQ_WEAPON;
    public static final long MAX_COOLDOWN;

    public static final double BASE_VALUE, MULT_VALUE;
    public static final int REQ_LEVEL;
    public static final byte REQ_EQUIP_SLOT = Globals.EQUIP_OFFHAND;
    public static final byte PLAYER_STATE = Player.PLAYER_STATE_SHIELD_ROAR;
    public static final int SKILL_DURATION = 500;

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

    public SkillShieldRoar(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        //Send roar particle
        if (player.getSkillCounter() == 0) {
            PacketSender.sendParticle(this.logic, Globals.Particles.SHIELD_ROAR.getParticleCode(), player.getKey(), player.getFacing());
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

    @Override
    public void updatePlayerAnimState(Player player) {
        final long frameDuration = Globals.nsToMs(this.logic.getTime() - player.getLastFrameTime());
        player.setAnimState(Globals.PLAYER_ANIM_STATE_BUFF);
        if (frameDuration >= 30 && player.getFrame() < 4) {
            player.setFrame((byte) (player.getFrame() + 1));
            player.setLastFrameTime(this.logic.getTime());
        }
    }
}
