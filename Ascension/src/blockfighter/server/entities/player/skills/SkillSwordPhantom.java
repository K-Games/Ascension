package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjSwordPhantom;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.ArrayList;
import java.util.HashMap;

public class SkillSwordPhantom extends Skill {

    private static final byte SKILL_CODE = Globals.SWORD_PHANTOM;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;
    private static final int REQ_LEVEL;
    private static final byte REQ_EQUIP_SLOT = Globals.EQUIP_WEAPON;
    private static final byte PLAYER_STATE = Player.PLAYER_STATE_SWORD_PHANTOM;
    private static final int SKILL_DURATION = 450;

    static {
        String[] data = Globals.loadSkillRawData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data);

        REQ_WEAPON = Globals.loadSkillReqWeapon(data, dataHeaders);
        REQ_LEVEL = Globals.loadSkillReqLevel(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
    }

    public SkillSwordPhantom(final LogicModule l) {
        super(l);
    }

    @Override
    public Double getCustomValue(String customHeader) {
        return null;
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
        final int numHits = 5;
        final int radius = 350;
        boolean endPhantom = false;
        player.setInvulnerable(true);
        player.setYSpeed(0);

        //Send initial phase effect
        if (player.getSkillCounter() == 0) {
            PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_PHANTOM.getParticleCode(), player.getX(), player.getY(), player.getFacing());
            player.incrementSkillCounter();
        }

        if (Globals.hasPastDuration(duration, 100 + 100 * (player.getSkillCounter() - 1)) && (player.getSkillCounter() - 1) < numHits) {
            if (this.logic.getRoomData().getMap().isPvP()) {
                Player target;
                ArrayList<Player> playersInRange = this.logic.getRoomData().getPlayersInRange(player, radius);

                if (!playersInRange.isEmpty()) {
                    target = playersInRange.get(Globals.rng(playersInRange.size()));

                    double teleX = ((player.getFacing() == Globals.RIGHT)) ? target.getHitbox().x + target.getHitbox().width + 70 + Globals.rng(50) : target.getHitbox().x - 70 - Globals.rng(50);
                    player.setPos(teleX, target.getY() - 10 * Globals.rng(5));
                    if (target.getX() < player.getX()) {
                        player.setFacing(Globals.LEFT);
                    } else if (target.getX() > player.getX()) {
                        player.setFacing(Globals.RIGHT);
                    }
                } else {
                    endPhantom = true;
                }
            } else {
                Mob target;
                ArrayList<Mob> mobsInRange = this.logic.getRoomData().getMobsInRange(player, radius);

                if (!mobsInRange.isEmpty()) {
                    target = mobsInRange.get(Globals.rng(mobsInRange.size()));
                    double teleX = (Globals.rng(2) == 0) ? target.getHitbox().x + target.getHitbox().width + 100 + Globals.rng(50) : target.getHitbox().x - 100 - Globals.rng(50);
                    player.setPos(teleX, target.getY() - 10 * Globals.rng(5));
                    if (target.getX() < player.getX()) {
                        player.setFacing(Globals.LEFT);
                    } else if (target.getX() > player.getX()) {
                        player.setFacing(Globals.RIGHT);
                    }
                } else {
                    endPhantom = true;
                }
            }
            if (!endPhantom) {
                final ProjSwordPhantom proj = new ProjSwordPhantom(this.logic, player, player.getX(), player.getY());
                this.logic.queueAddProj(proj);
                PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_PHANTOM.getParticleCode(), player.getX(), player.getY(), player.getFacing());
                PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_PHANTOM2.getParticleCode(), player.getKey());
                player.incrementSkillCounter();
            }
        }

        if (player.updateSkillEnd(endPhantom || (player.getSkillCounter() - 1) >= numHits)) {
            player.setYSpeed(0.1);
            player.setInvulnerable(false);
        }
    }

    @Override
    public int getReqLevel() {
        return REQ_LEVEL;
    }
}
