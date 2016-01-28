package blockfighter.client.entities.particles;

import blockfighter.client.entities.player.Player;
import blockfighter.client.screen.ScreenIngame;
import java.awt.Point;

public class ParticleBurnBuffEmitter extends Particle {

    private final Player owner;

    public ParticleBurnBuffEmitter(final int k, final Player p) {
        super(k, 0, 0);
        this.frame = 0;
        this.duration = 4000;
        this.owner = p;
    }

    @Override
    public void update() {
        super.update();
        if (this.duration > 0 && this.duration % 50 == 0) {
            final Point p = this.owner.getPos();
            if (p != null) {
                this.x = p.x;
                this.y = p.y;
            }
            for (int i = 0; i < 3; i++) {
                final ParticleBurnBuffParticle b = new ParticleBurnBuffParticle(((ScreenIngame) logic.getScreen()).getNextParticleKey(),
                        this.x, this.y,
                        this.facing);
                ((ScreenIngame) logic.getScreen()).addParticle(b);
            }
        }
    }
}
