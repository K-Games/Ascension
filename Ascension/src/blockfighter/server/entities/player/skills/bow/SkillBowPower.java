package blockfighter.server.entities.player.skills.bow;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.proj.ProjBowPower;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;

public class SkillBowPower extends Skill {

    public static final byte SKILL_CODE = Globals.BOW_POWER;

    public SkillBowPower(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (player.getSkillCounter() == 0) {
            if (player.isSkillMaxed(Globals.BOW_POWER)) {
                player.setHyperStance(true);
            }
            PacketSender.sendSFX(this.logic, Globals.SFXs.POWER2.getSfxCode(), player.getX(), player.getY());
        }
        if (duration <= 400 && Globals.hasPastDuration(duration, player.getSkillCounter() * 20) && player.getSkillCounter() < 20) {
            PacketSender.sendParticle(this.logic, Globals.Particles.BOW_POWER_CHARGE.getParticleCode(), player.getKey());
            player.incrementSkillCounter();
        }
        if (Globals.hasPastDuration(duration, 800) && player.getSkillCounter() < 21) {
            player.incrementSkillCounter();
            PacketSender.sendScreenShake(player, 10, 10, 350);
            final ProjBowPower proj = new ProjBowPower(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            PacketSender.sendParticle(this.logic, Globals.Particles.BOW_POWER.getParticleCode(), player.getX(), player.getY(),
                    player.getFacing());
            PacketSender.sendSFX(this.logic, Globals.SFXs.POWER.getSfxCode(), player.getX(), player.getY());
        }
        if (player.updateSkillEnd(duration >= getSkillData().getSkillDuration() || (!player.isSkillMaxed(Globals.BOW_POWER) && duration < 800 && (player.isStunned() || player.isKnockback())))) {
            player.setHyperStance(false);
        }
    }

    @Override
    public void updatePlayerAnimState(Player player) {
        final long frameDuration = Globals.nsToMs(this.logic.getTime() - player.getLastFrameTime());
        player.setAnimState(Globals.PLAYER_ANIM_STATE_ATTACKBOW);
        if (frameDuration >= ((player.getFrame() < 3) ? 30 : 70)) {
            if (player.getSkillCounter() < 20 && player.getFrame() != 3) {

                player.setFrame((byte) (player.getFrame() + 1));
            } else if (player.getSkillCounter() == 21 && player.getFrame() < 7) {

                player.setFrame((byte) (player.getFrame() + 1));
            }
            player.setLastFrameTime(this.logic.getTime());
        }
    }
}
