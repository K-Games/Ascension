package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffShieldReflect;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjShieldReflect;
import java.util.Map;

/**
 *
 * @author Ken Kwan
 */
public class SkillShieldReflect extends Skill {

    /**
     * Constructor for Shield Skill Reflect.
     */
    public SkillShieldReflect(final LogicModule l) {
        super(l);
        this.skillCode = SHIELD_REFLECT;
        this.maxCooldown = 15000;
        this.reqWeapon = Globals.ITEM_SHIELD;
    }

    @Override
    public void updateSkillUse(Player player) {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        if (duration == 0) {
            player.queueBuff(new BuffShieldReflect(this.logic, 3000, .4 + 0.02 * player.getSkillLevel(Skill.SHIELD_REFLECT), player, player));
            Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_REFLECTCAST, player.getKey());
            Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_REFLECTBUFF, player.getKey());
            if (player.isSkillMaxed(Skill.SHIELD_REFLECT)) {
                for (final Map.Entry<Byte, Player> pEntry : this.logic.getPlayers().entrySet()) {
                    final Player p = pEntry.getValue();
                    if (p != player && !p.isDead()) {
                        p.queueBuff(new BuffShieldReflect(this.logic, 3000, 0.4, player, p));
                        if (!this.logic.getMap().isPvP()) {
                            Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_REFLECTCAST, p.getKey());
                        }
                    }
                }
            }
        }
        player.updateSkillEnd(duration, 250, false, false);
    }

    public void updateSkillReflectHit(final double dmgTaken, final double mult, final Player player) {
        final ProjShieldReflect proj = new ProjShieldReflect(this.logic, player, player.getX(), player.getY(),
                dmgTaken * mult);
        this.logic.queueAddProj(proj);
        Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_REFLECTHIT, player.getX(), player.getY());
    }
}
