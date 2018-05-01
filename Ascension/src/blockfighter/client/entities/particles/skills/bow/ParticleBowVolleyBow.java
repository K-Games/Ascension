package blockfighter.client.entities.particles.skills.bow;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.Point;

public class ParticleBowVolleyBow extends Particle {

    public ParticleBowVolleyBow(final byte f, final Player p) {
        super(f, p);
        this.frame = 0;
        this.duration = 250;
        this.frameDuration = 25;
        this.particleData = Globals.Particles.BOW_VOLLEY_BOW;
    }

    @Override
    public void draw(final Graphics2D g) {
        final Point p = this.owner.getPos();
        if (p != null) {
            this.x = p.x + ((this.facing == Globals.RIGHT) ? -15 : 15);
            this.y = p.y - 75;
        }
        draw(g, -69, 68);
    }
}
