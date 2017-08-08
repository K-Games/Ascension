package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffShieldReflect;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

public class SkillShieldReflect extends Skill {

    public static final String[] CUSTOM_DATA_HEADERS;
    private static final HashMap<String, Double> CUSTOM_VALUES;

    private static final byte SKILL_CODE = Globals.SHIELD_REFLECT;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final int REQ_LEVEL;
    private static final byte REQ_EQUIP_SLOT = Globals.EQUIP_OFFHAND;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_SHIELD_REFLECT;
    private static final int SKILL_DURATION = 250;

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

    public SkillShieldReflect(final LogicModule l) {
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
        final long duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        if (player.getSkillCounter() == 0) {
            player.incrementSkillCounter();
            double buffDuration = getCustomValue(CUSTOM_DATA_HEADERS[1]);
            if (!player.isSkillMaxed(Globals.SHIELD_REFLECT)) {
                player.queueBuff(new BuffShieldReflect(this.logic, (int) buffDuration, getBaseValue() + getMultValue() * player.getSkillLevel(Globals.SHIELD_REFLECT), player, player, 0));
            } else {
                player.queueBuff(new BuffShieldReflect(this.logic, (int) buffDuration, getBaseValue() + getMultValue() * player.getSkillLevel(Globals.SHIELD_REFLECT), player, player, getCustomValue(CUSTOM_DATA_HEADERS[0])));
            }
            PacketSender.sendParticle(this.logic, Globals.Particles.SHIELD_REFLECT_CAST.getParticleCode(), player.getKey());
            PacketSender.sendParticle(this.logic, Globals.Particles.SHIELD_REFLECT_EMITTER.getParticleCode(), player.getKey());
        }
        player.updateSkillEnd(duration, getSkillDuration(), false, false);
    }

    public void updateSkillReflectHit(final double dmgTaken, final double mult, final Player player) {
        double radius = 300;
        if (this.logic.getRoomData().getMap().isPvP()) {
            ArrayList<Player> playersInRange = this.logic.getRoomData().getPlayersInRange(player, radius);
            if (!playersInRange.isEmpty()) {
                for (Player p : playersInRange) {
                    final Damage dmgEntity = new Damage((int) (dmgTaken * mult), true, player, p, false, new Point2D.Double(p.getX(), p.getY()));
                    dmgEntity.setCanReflect(false);
                    p.queueDamage(dmgEntity);
                }
            }
        } else {
            ArrayList<Mob> mobsInRange = this.logic.getRoomData().getMobsInRange(player, radius);
            if (!mobsInRange.isEmpty()) {
                for (Mob mob : mobsInRange) {
                    final Damage dmgEntity = new Damage((int) (dmgTaken * mult), true, player, mob, false, new Point2D.Double(mob.getX(), mob.getY()));
                    dmgEntity.setCanReflect(false);
                    mob.queueDamage(dmgEntity);
                }
            }
        }
        PacketSender.sendParticle(this.logic, Globals.Particles.SHIELD_REFLECT_HIT.getParticleCode(), player.getX(), player.getY());
    }

    @Override
    public int getReqLevel() {
        return REQ_LEVEL;
    }
}
