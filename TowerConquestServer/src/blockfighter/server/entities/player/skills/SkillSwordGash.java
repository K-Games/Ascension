package blockfighter.server.entities.player.skills;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjSwordGash;

/**
 *
 * @author Ken Kwan
 */
public class SkillSwordGash extends Skill {

    /**
     * Constructor for Sword Skill Gash.
     *
     * @param l
     */
    public SkillSwordGash(final LogicModule l) {
        super(l);
        this.skillCode = SWORD_GASH;
        this.maxCooldown = 500;
        this.reqWeapon = Globals.ITEM_SWORD;
    }

    @Override
    public void updateSkillUse(Player player) {
        final int duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        final byte numHits = 4;
        if (Globals.hasPastDuration(duration, (80 * player.getSkillCounter())) && player.getSkillCounter() < numHits) {
            player.incrementSkillCounter();
            final ProjSwordGash proj = new ProjSwordGash(this.logic, player, player.getX(), player.getY(), (byte) player.getSkillCounter());
            this.logic.queueAddProj(proj);
            Player.sendSFX(this.logic.getRoom(), Globals.SFX_SLASH, player.getX(), player.getY());
            switch (player.getSkillCounter()) {
                case 1:
                    Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_GASH1, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                            player.getFacing());
                    break;
                case 2:
                    Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_GASH2, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                            player.getFacing());
                    break;
                case 3:
                    Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_GASH3, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                            player.getFacing());
                    break;
                case 4:
                    Player.sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_GASH4, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                            player.getFacing());
                    break;
            }
        }
        player.updateSkillEnd(duration, 450, true, false);
    }
}
