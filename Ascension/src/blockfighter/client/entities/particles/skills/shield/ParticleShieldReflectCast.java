package blockfighter.client.entities.particles.skills.shield;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleShieldReflectCast extends Particle {

    public ParticleShieldReflectCast(final Player p) {
        super(p.getX(), p.getY(), p);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 400;
        this.particleData = Globals.Particles.SHIELD_REFLECT_CAST;
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, -260, 180, false);
    }
}
