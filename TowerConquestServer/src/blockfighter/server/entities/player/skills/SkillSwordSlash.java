package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffSwordSlash;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjSwordSlash;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordSlash extends Skill {

    /**
     * Constructor for Sword Skill Defensive Impact.
     *
     * @param l
     */
    public SkillSwordSlash(final LogicModule l) {
        super(l);
        this.skillCode = SWORD_SLASH;
        this.maxCooldown = 400;
        this.reqWeapon = Globals.ITEM_SWORD;
    }

    @Override
    public void updateSkillUse(Player player) {
        final int numHits = 3;
        final int duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (player.isSkillMaxed(Skill.SWORD_SLASH) && duration == 0) {
            player.queueBuff(new BuffSwordSlash(this.logic, 2000, .1, player));
            Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_SLASHBUFF, player.getKey());
        }
        if (Player.hasPastDuration(duration, (30 + 110 * player.getSkillCounter())) && player.getSkillCounter() < numHits) {
            player.incrementSkillCounter();
            final ProjSwordSlash proj = new ProjSwordSlash(this.logic, player, player.getX(), player.getY(),
                    player.getSkillCounter());
            this.logic.queueAddProj(proj);
            switch (player.getSkillCounter()) {
                case 1:
                    Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_SLASH1, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                            player.getFacing());
                    Player.sendSFX(this.logic.getRoom(), Globals.SFX_SLASH, player.getX(), player.getY());
                    break;
                case 2:
                    Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_SLASH2, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                            player.getFacing());
                    Player.sendSFX(this.logic.getRoom(), Globals.SFX_SLASH, player.getX(), player.getY());
                    break;
                case 3:
                    Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_SLASH3, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                            player.getFacing());
                    Player.sendSFX(this.logic.getRoom(), Globals.SFX_SLASH, player.getX(), player.getY());
                    break;
                default:
                    break;
            }
        }

        player.updateSkillEnd(duration, 350, true, false);
    }
}
