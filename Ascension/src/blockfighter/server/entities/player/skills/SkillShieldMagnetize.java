package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjShieldMagnetize;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.ArrayList;
import java.util.HashMap;

public class SkillShieldMagnetize extends Skill {

    ArrayList<Player> playersCaught;
    ArrayList<Mob> mobsCaught;

    private static final String BASEDEF_HEADER = "[basedefense]",
            MULTDEF_HEADER = "[multdefense]",
            MAXLEVELMULT_HEADER = "[maxlevelmult]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        BASEDEF_HEADER,
        MULTDEF_HEADER,
        MAXLEVELMULT_HEADER
    };

    private static final double BASE_DEFENSE,
            MULT_DEFENSE,
            MAX_LEVEL_MULT;

    private static final byte SKILL_CODE = Globals.SHIELD_MAGNETIZE;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final byte REQ_EQUIP_SLOT = Globals.ITEM_OFFHAND;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_SHIELD_MAGNETIZE;
    private static final int SKILL_DURATION = 600;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, CUSTOM_DATA_HEADERS);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
        BASE_DEFENSE = Globals.loadDoubleValue(data, dataHeaders, BASEDEF_HEADER);
        MULT_DEFENSE = Globals.loadDoubleValue(data, dataHeaders, MULTDEF_HEADER);
        MAX_LEVEL_MULT = Globals.loadDoubleValue(data, dataHeaders, MAXLEVELMULT_HEADER);
    }

    public SkillShieldMagnetize(final LogicModule l) {
        super(l);
    }

    public double getBaseDefense() {
        return BASE_DEFENSE;
    }

    public double getMultDefense() {
        return MULT_DEFENSE;
    }

    public double getMaxLevelMult() {
        return MAX_LEVEL_MULT;
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
        final int radius = 400;
        if (player.getSkillCounter() == 0) {
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_MAGNETIZESTART, player.getKey());
            player.incrementSkillCounter();
        }
        if (Globals.hasPastDuration(duration, 200) && player.getSkillCounter() == 1) {
            PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_MAGNETIZEBURST, player.getKey());
            if (this.logic.getRoom().getMap().isPvP()) {
                this.playersCaught = this.logic.getRoom().getPlayersInRange(player, radius);
                if (!this.playersCaught.isEmpty()) {
                    for (Player p : this.playersCaught) {
                        PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_MAGNETIZE, player.getKey(), p.getKey());
                    }
                }
            } else {
                this.mobsCaught = this.logic.getRoom().getMobsInRange(player, radius);
                if (!this.mobsCaught.isEmpty()) {
                    for (Mob mob : this.mobsCaught) {
                        PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_SHIELD_MAGNETIZE, player.getKey(), mob.getKey());
                    }
                }
            }
            player.incrementSkillCounter();
        }

        if (Globals.hasPastDuration(duration, 200) && player.getSkillCounter() == 2) {
            if (this.logic.getRoom().getMap().isPvP()) {
                if (!this.playersCaught.isEmpty()) {
                    int numOfTicks = (int) ((500 - duration) / Globals.nsToMs(Globals.SERVER_LOGIC_UPDATE));
                    for (Player p : this.playersCaught) {
                        if (numOfTicks > 0) {
                            double distanceX = (player.getX() - p.getX()) / numOfTicks;
                            double distanceY = (player.getY() - p.getY()) / numOfTicks;
                            p.setXSpeed(distanceX);
                            p.setYSpeed(distanceY);
                        } else {
                            p.setXSpeed(0);
                            p.setYSpeed(0.01);
                        }
                    }
                }
            }
        }

        if (Globals.hasPastDuration(duration, 500) && player.getSkillCounter() == 2) {
            final ProjShieldMagnetize proj = new ProjShieldMagnetize(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            player.incrementSkillCounter();
        }

        player.updateSkillEnd(duration, getSkillDuration(), false, false);
    }
}
