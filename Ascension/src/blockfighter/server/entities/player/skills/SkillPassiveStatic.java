package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.util.ArrayList;
import java.util.HashMap;

public class SkillPassiveStatic extends SkillPassive {

    private static final byte SKILL_CODE = Globals.PASSIVE_STATIC;
    private static final boolean IS_PASSIVE;
    private static final byte REQ_WEAPON;
    private static final long MAX_COOLDOWN;

    private static final double BASE_VALUE, MULT_VALUE;

    static {
        String[] data = Globals.loadSkillData(SKILL_CODE);
        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, null);

        REQ_WEAPON = Globals.loadReqWeapon(data, dataHeaders);
        MAX_COOLDOWN = (long) Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
        BASE_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
        MULT_VALUE = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
        IS_PASSIVE = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
    }

    public SkillPassiveStatic(final LogicModule l) {
        super(l);
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
    public byte getReqWeapon() {
        return REQ_WEAPON;
    }

    @Override
    public byte getSkillCode() {
        return SKILL_CODE;
    }

    @Override
    public boolean isPassive() {
        return IS_PASSIVE;
    }

    @Override
    public void updateSkillUse(final Player player) {
        double radius = 250;
        if (Globals.rng(100) + 1 <= 20) {
            double baseValue = getBaseValue();
            double multValue = getMultValue();
            int damage = (int) (player.getStats()[Globals.STAT_ARMOR] * (baseValue + multValue * player.getSkillLevel(Globals.PASSIVE_STATIC)));

            if (this.logic.getRoom().getMap().isPvP()) {
                ArrayList<Player> playersInRange = this.logic.getRoom().getPlayersInRange(player, radius);
                if (!playersInRange.isEmpty()) {
                    Player target = playersInRange.get(Globals.rng(playersInRange.size()));
                    final boolean crit = player.rollCrit();
                    if (crit) {
                        damage = (int) player.criticalDamage(damage);
                    }
                    target.queueDamage(new Damage(damage, false, player, target, crit, target.getHitbox(), target.getHitbox()));
                    PacketSender.sendParticle(this.logic.getRoom().getRoomNumber(), Globals.PARTICLE_PASSIVE_STATIC, player.getKey(), target.getKey());
                }
            } else {
                ArrayList<Mob> mobsInRange = this.logic.getRoom().getMobsInRange(player, radius);
                if (!mobsInRange.isEmpty()) {
                    Mob target = mobsInRange.get(Globals.rng(mobsInRange.size()));
                    final boolean crit = player.rollCrit();
                    if (crit) {
                        damage = (int) player.criticalDamage(damage);
                    }
                    target.queueDamage(new Damage(damage, false, player, target, crit, target.getHitbox(), target.getHitbox()));
                }
            }
        }
    }
}
