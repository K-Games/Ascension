package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffShieldIron;
import blockfighter.server.entities.player.Player;
import java.util.Map;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldIron extends Skill {

    /**
     * Constructor for Shield Skill Iron Fortress.
     *
     * @param l
     */
    public SkillShieldIron(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_IRON;
        this.maxCooldown = 20000;
        this.reqWeapon = Globals.ITEM_SHIELD;
    }

    @Override
    public void updateSkillUse(Player player) {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        if (duration == 0) {
            Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_IRON, player.getKey());
            Player.sendSFX(this.logic.getRoom(), Globals.SFX_IRON, player.getX(), player.getY());
        }
        if (Player.hasPastDuration(duration, 100) && player.getSkillCounter() < 1) {
            player.incrementSkillCounter();
            player.setRemovingDebuff(true);
            player.queueBuff(new BuffShieldIron(this.logic, 2000, 0.55 + 0.01 * player.getSkillLevel(Skill.SHIELD_IRON)));
            if (player.isSkillMaxed(Skill.SHIELD_IRON) && !this.logic.getMap().isPvP()) {
                for (final Map.Entry<Byte, Player> pEntry : this.logic.getPlayers().entrySet()) {
                    final Player p = pEntry.getValue();
                    if (p != player && !p.isDead()) {
                        p.queueBuff(new BuffShieldIron(this.logic, 2000, 0.4));
                        Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_IRONALLY, p.getKey());
                    }
                }
            }
        }
        if (player.updateSkillEnd(duration >= 2100)) {
            player.setRemovingDebuff(false);
        }
    }
}
