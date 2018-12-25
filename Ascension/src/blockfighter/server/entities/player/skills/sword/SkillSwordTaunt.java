package blockfighter.server.entities.player.skills.sword;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffSwordTaunt;
import blockfighter.server.entities.buff.BuffTauntSurge;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.proj.ProjSwordTaunt;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;

public class SkillSwordTaunt extends Skill {

    private ProjSwordTaunt proj;

    public static final byte SKILL_CODE = Globals.SWORD_TAUNT;

    public SkillSwordTaunt(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - player.getSkillCastTime());
        if (player.getSkillCounter() == 0) {
            player.incrementSkillCounter();
            if (player.isSkillMaxed(Globals.SWORD_TAUNT)) {
                player.setHyperStance(true);
                double buffDuration = getCustomValue(0);
                player.queueBuff(new BuffSwordTaunt(this.logic, (int) buffDuration, getCustomValue(2), getCustomValue(1), player));
                PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_TAUNT_BUFF_EMITTER.getParticleCode(), player.getKey());
            }
            proj = new ProjSwordTaunt(this.logic, player, player.getX(), player.getY());
            PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_TAUNT_AURA.getParticleCode(), player.getKey());
            PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_TAUNT.getParticleCode(), player.getX(), player.getY(), player.getFacing());
        }
        if (Globals.hasPastDuration(duration, 100) && player.getSkillCounter() == 1) {
            player.incrementSkillCounter();
            this.logic.queueAddProj(proj);

        }
        if (player.updateSkillEnd(duration, getSkillData().getSkillDuration(), false, false)) {
            if (player.hasSkill(Globals.SWORD_TAUNT_SURGE)) {
                player.queueBuff(new BuffTauntSurge(this.logic, player.getSkill(Globals.SWORD_TAUNT_SURGE).getCustomValue(Globals.SkillClassMap.SWORD_TAUNT_SURGE.getSkillData().getCustomDataHeaders().get(0)).intValue(), player));
                PacketSender.sendParticle(this.logic, Globals.Particles.SWORD_TAUNT_SURGE.getParticleCode(), player.getKey());
            }
            player.setHyperStance(false);
        }
    }

    @Override
    public void updatePlayerAnimState(Player player) {
        final long frameDuration = Globals.nsToMs(this.logic.getTime() - player.getLastFrameTime());
        player.setAnimState(Globals.PLAYER_ANIM_STATE_ATTACK);
        if (frameDuration >= ((player.getFrame() == 4) ? 150 : 30) && player.getFrame() < 5) {
            player.setFrame((byte) (player.getFrame() + 1));
            player.setLastFrameTime(this.logic.getTime());
        }
    }
}
