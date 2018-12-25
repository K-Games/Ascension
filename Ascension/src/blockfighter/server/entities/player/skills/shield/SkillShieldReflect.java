package blockfighter.server.entities.player.skills.shield;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.BuffShieldReflect;
import blockfighter.server.entities.damage.DamageBuilder;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class SkillShieldReflect extends Skill {

    public static final byte SKILL_CODE = Globals.SHIELD_REFLECT;

    public SkillShieldReflect(final LogicModule l) {
        super(l);
    }

    @Override
    public void updateSkillUse(Player player) {
        final long duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        if (player.getSkillCounter() == 0) {
            player.incrementSkillCounter();
            double buffDuration = getCustomValue(1);
            if (!player.isSkillMaxed(Globals.SHIELD_REFLECT)) {
                player.queueBuff(new BuffShieldReflect(this.logic, (int) buffDuration, getSkillData().getBaseValue() + getSkillData().getMultValue() * player.getSkillLevel(Globals.SHIELD_REFLECT), player, player, 0));
            } else {
                player.queueBuff(new BuffShieldReflect(this.logic, (int) buffDuration, getSkillData().getBaseValue() + getSkillData().getMultValue() * player.getSkillLevel(Globals.SHIELD_REFLECT), player, player,
                        getCustomValue(0)));
            }
            PacketSender.sendParticle(this.logic, Globals.Particles.SHIELD_REFLECT_CAST.getParticleCode(), player.getKey());
            PacketSender.sendParticle(this.logic, Globals.Particles.SHIELD_REFLECT_EMITTER.getParticleCode(), player.getKey());
        }
        player.updateSkillEnd(duration, getSkillData().getSkillDuration(), false, false);
    }

    public void updateSkillReflectHit(final double dmgTaken, final double mult, final Player player) {
        double radius = 300;
        if (this.logic.getRoomData().getMap().isPvP()) {
            ArrayList<Player> playersInRange = this.logic.getRoomData().getPlayersInRange(player, radius);
            if (!playersInRange.isEmpty()) {
                playersInRange.forEach((p) -> {
                    p.queueDamage(new DamageBuilder()
                            .setDamage((int) (dmgTaken * mult))
                            .setCanProc(false)
                            .setShowParticle(false)
                            .setCanReflect(false)
                            .setOwner(player)
                            .setTarget(p)
                            .setIsCrit(false)
                            .setDmgPoint(new Point2D.Double(p.getX(), p.getY()))
                            .build());
                });
            }
        }
        PacketSender.sendParticle(this.logic, Globals.Particles.SHIELD_REFLECT_HIT.getParticleCode(), player.getX(), player.getY());
    }

    @Override
    public void updatePlayerAnimState(Player player) {
        final long frameDuration = Globals.nsToMs(this.logic.getTime() - player.getLastFrameTime());
        player.setAnimState(Globals.PLAYER_ANIM_STATE_BUFF);
        if (frameDuration >= 20 && player.getFrame() < 4) {
            player.setFrame((byte) (player.getFrame() + 1));
            player.setLastFrameTime(this.logic.getTime());
        }
    }
}
