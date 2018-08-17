package blockfighter.server.entities.player.skills.sword;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffSwordTaunt;
import blockfighter.server.entities.buff.BuffTauntSurge;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.proj.ProjSwordTaunt;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.HashMap;

public class SkillSwordTaunt extends Skill {

    private ProjSwordTaunt proj;

    public static final String[] CUSTOM_DATA_HEADERS;
    public static final HashMap<String, Double> CUSTOM_VALUES;

    public static final byte SKILL_CODE = Globals.SWORD_TAUNT;
    public static final boolean IS_PASSIVE;
    public static final byte REQ_WEAPON;
    public static final long MAX_COOLDOWN;

    public static final double BASE_VALUE, MULT_VALUE;
    public static final int REQ_LEVEL;
    public static final byte REQ_EQUIP_SLOT = Globals.EQUIP_WEAPON;
    public static final byte PLAYER_STATE = Player.PLAYER_STATE_SWORD_TAUNT;
    public static final int SKILL_DURATION = 350;

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

    public SkillSwordTaunt(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (player.getSkillCounter() == 0) {
            player.incrementSkillCounter();
            if (player.isSkillMaxed(Globals.SWORD_TAUNT)) {
                player.setHyperStance(true);
                double buffDuration = getCustomValue(CUSTOM_DATA_HEADERS[0]);
                player.queueBuff(new BuffSwordTaunt(this.logic, (int) buffDuration, getCustomValue(CUSTOM_DATA_HEADERS[2]), getCustomValue(CUSTOM_DATA_HEADERS[1]), player));
                PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_TAUNT_BUFF_EMITTER.getParticleCode(), player.getKey());
            }
            proj = new ProjSwordTaunt(this.logic, player, player.getX(), player.getY());
            PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_TAUNT_AURA.getParticleCode(), player.getKey());
            PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_TAUNT.getParticleCode(), player.getX(), player.getY(), player.getFacing());
        }
        if (Globals.hasPastDuration(duration, 100) && player.getSkillCounter() == 1) {
            player.incrementSkillCounter();
            this.logic.queueAddProj(proj);

        }
        if (player.updateSkillEnd(duration, getSkillDuration(), false, false)) {
            if (player.hasSkill(Globals.SWORD_TAUNT_SURGE)) {
                player.queueBuff(new BuffTauntSurge(this.logic, player.getSkill(Globals.SWORD_TAUNT_SURGE).getCustomValue(SkillSwordTauntSurge.CUSTOM_DATA_HEADERS[0]).intValue(), player));
                PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_TAUNT_SURGE.getParticleCode(), player.getKey());
            }
            player.setHyperStance(false);
        }
    }

    @Override
    public void updatePlayerAnimState(Player player) {
        final long frameDuration = Globals.nsToMs(this.logic.getTime() - player.getLastFrameTime());
        player.setAnimState(Globals.PLAYER_ANIM_STATE_ATTACK);
        if (frameDuration >= ((player.getFrame() == 4) ? 150 : 30) && player.getFrame() < 5) {
            player.setFrame((byte) (player.getFrame() + 1));
            player.setLastFrameTime(this.logic.getTime());
        }
    }
}
