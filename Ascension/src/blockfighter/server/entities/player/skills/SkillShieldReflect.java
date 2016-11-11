package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffShieldReflect;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SkillShieldReflect extends Skill {

    private static final String MAXLVLREFLECT_HEADER = "[maxlevelreflect]",
            BUFFDURATION_HEADER = "[buffduration]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        MAXLVLREFLECT_HEADER,
        BUFFDURATION_HEADER
    };

    private static final double MAX_LEVEL_REFLECT,
            BUFF_DURATION;

    private static final byte SKILL_CODE = Globals.SHIELD_REFLECT;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final double MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final byte REQ_EQUIP_SLOT = Globals.ITEM_OFFHAND;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_SHIELD_REFLECT;
    private static final int SKILL_DURATION = 250;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
        MAX_LEVEL_REFLECT = Globals.loadDoubleValue(data, dataHeaders, MAXLVLREFLECT_HEADER);
        BUFF_DURATION = Globals.loadDoubleValue(data, dataHeaders, BUFFDURATION_HEADER);
    }

    public SkillShieldReflect(final LogicModule l) {
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
        final long duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        if (player.getSkillCounter() == 0) {
            player.incrementSkillCounter();
            player.queueBuff(new BuffShieldReflect(this.logic, (int) BUFF_DURATION, getBaseValue() + getMultValue() * player.getSkillLevel(Globals.SHIELD_REFLECT), player, player));
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_REFLECTCAST, player.getKey());
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_REFLECTBUFF, player.getKey());
            if (player.isSkillMaxed(Globals.SHIELD_REFLECT)) {
                for (final Map.Entry<Byte, Player> pEntry : this.logic.getRoom().getPlayers().entrySet()) {
                    final Player p = pEntry.getValue();
                    if (p != player && !p.isDead()) {
                        p.queueBuff(new BuffShieldReflect(this.logic, (int) BUFF_DURATION, MAX_LEVEL_REFLECT, player, p));
                        if (!this.logic.getRoom().getMap().isPvP()) {
                            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_REFLECTCAST, p.getKey());
                        }
                    }
                }
            }
        }
        player.updateSkillEnd(duration, getSkillDuration(), false, false);
    }

    public void updateSkillReflectHit(final double dmgTaken, final double mult, final Player player) {
        double radius = 300;
        if (this.logic.getRoom().getMap().isPvP()) {
            ArrayList<Player> playersInRange = this.logic.getRoom().getPlayersInRange(player, radius);
            if (!playersInRange.isEmpty()) {
                for (Player p : playersInRange) {
                    final Damage dmgEntity = new Damage((int) (dmgTaken * mult), true, player, p, false, p.getHitbox(), p.getHitbox());
                    dmgEntity.setCanReflect(false);
                    p.queueDamage(dmgEntity);
                }
            }
        } else {
            ArrayList<Mob> mobsInRange = this.logic.getRoom().getMobsInRange(player, radius);
            if (!mobsInRange.isEmpty()) {
                for (Mob mob : mobsInRange) {
                    final Damage dmgEntity = new Damage((int) (dmgTaken * mult), true, player, mob, false, mob.getHitbox(), mob.getHitbox());
                    dmgEntity.setCanReflect(false);
                    mob.queueDamage(dmgEntity);
                }
            }
        }
        PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_REFLECTHIT, player.getX(), player.getY());
    }
}
