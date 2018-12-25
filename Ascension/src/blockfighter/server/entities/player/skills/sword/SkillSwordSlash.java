package blockfighter.server.entities.player.skills.sword;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffSwordSlash;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.proj.ProjSwordSlash;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;

public class SkillSwordSlash extends Skill {

    public static final byte SKILL_CODE = Globals.SWORD_SLASH;

    public SkillSwordSlash(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(Player player) {
        final int numHits = 3;
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (player.getSkillCounter() == 0) {
            player.incrementSkillCounter();
            if (player.isSkillMaxed(Globals.SWORD_SLASH)) {
                double buffDuration = getCustomValue(0);
                player.queueBuff(new BuffSwordSlash(this.logic, (int) buffDuration, getCustomValue(1), player));
                PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_SLASH_BUFF_EMITTER.getParticleCode(), player.getKey());
            }
        }
        if (Globals.hasPastDuration(duration, (30 + 110 * (player.getSkillCounter() - 1))) && (player.getSkillCounter() - 1) < numHits) {
            player.setFrame((byte) 0);
            player.incrementSkillCounter();
            final ProjSwordSlash proj = new ProjSwordSlash(this.logic, player, player.getX(), player.getY());
            this.logic.queueAddProj(proj);
            switch (player.getSkillCounter() - 1) {
                case 1:
                    PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_SLASH1.getParticleCode(), player.getX(), player.getY(), player.getFacing());
                    PacketSender.sendSFX(this.logic, Globals.SFXs.SLASH.getSfxCode(), player.getX(), player.getY());
                    break;
                case 2:
                    PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_SLASH2.getParticleCode(), player.getX(), player.getY(), player.getFacing());
                    PacketSender.sendSFX(this.logic, Globals.SFXs.SLASH.getSfxCode(), player.getX(), player.getY());
                    break;
                case 3:
                    PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_SLASH3.getParticleCode(), player.getX(), player.getY(), player.getFacing());
                    PacketSender.sendSFX(this.logic, Globals.SFXs.SLASH.getSfxCode(), player.getX(), player.getY());
                    break;
                default:
                    break;
            }
        }

        player.updateSkillEnd(duration, getSkillData().getSkillDuration(), true, false);
    }

    @Override
    public void updatePlayerAnimState(Player player) {
        final byte prevAnimState = player.getAnimState(), prevFrame = player.getFrame();
        final long frameDuration = Globals.nsToMs(this.logic.getTime() - player.getLastFrameTime());
        if (frameDuration >= 20) {
            player.setAnimState(Globals.PLAYER_ANIM_STATE_ATTACK);
            if (player.getFrame() < 5) {
                player.setFrame((byte) (player.getFrame() + 1));
            }
            player.setLastFrameTime(this.logic.getTime());
        }
    }
}
