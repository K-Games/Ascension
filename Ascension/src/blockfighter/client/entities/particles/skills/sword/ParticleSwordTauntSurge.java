package blockfighter.client.entities.particles.skills.sword;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.client.entities.player.skills.Skill;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ParticleSwordTauntSurge extends Particle {

    public ParticleSwordTauntSurge(final Player p) {
        super(p);
        this.frame = 0;
        this.frameDuration = 50;
        Skill skill = Globals.SkillClassMap.SWORD_TAUNT_SURGE.getClientSkillInstance();
        this.duration = skill.getCustomValue(skill.getCustomDataHeaders()[0]).intValue();
        final Point point = this.owner.getPos();
        if (point != null) {
            this.x = point.x;
            this.y = point.y;
        }
        this.particleData = Globals.Particles.SWORD_TAUNT_AURA;
    }

    @Override
    public void update() {
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (this.particleData.getSprites() != null && this.frame < this.particleData.getSprites().length) {
                this.frame++;
            } else {
                this.frame = 0;
            }
            this.lastFrameTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        final Point p = this.owner.getPos();
        if (p != null) {
            this.x = p.x;
            this.y = p.y;
        }

        if (this.spriteFrameExists()) {
            final BufferedImage sprite = Globals.Particles.SWORD_TAUNT_AURA.getSprites()[this.frame];
            draw(g, -sprite.getWidth() / 2, 10, false);
        }
    }
}
