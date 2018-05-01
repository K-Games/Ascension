package blockfighter.client.entities.particles.skills.sword;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;

public class ParticleSwordPhantom2 extends Particle {

    public ParticleSwordPhantom2(final Player p) {
        super(p);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 400;
        this.particleData = Globals.Particles.SWORD_PHANTOM2;
    }

    @Override
    public void draw(final Graphics2D g) {
        draw(g, -10, 10);
    }
}
