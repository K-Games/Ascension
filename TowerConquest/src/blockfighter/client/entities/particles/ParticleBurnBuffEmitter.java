package blockfighter.client.entities.particles;

import blockfighter.client.entities.player.Player;
import blockfighter.client.screen.ScreenIngame;
import java.awt.Point;

public class ParticleBurnBuffEmitter extends Particle {

    private Player owner;

    public ParticleBurnBuffEmitter(int k, Player p) {
        super(k, 0, 0);
        frame = 0;
        duration = 4000;
        owner = p;
    }

    @Override
    public void update() {
        super.update();
        if (duration > 0 && duration % 50 == 0) {
            Point p = owner.getPos();
            if (p != null) {
                x = p.x;
                y = p.y;
            }
            for (int i = 0; i < 3; i++) {
                ParticleBurnBuffParticle b = new ParticleBurnBuffParticle(((ScreenIngame) logic.getScreen()).getNextParticleKey(), x, y, facing);
                ((ScreenIngame) logic.getScreen()).addParticle(b);
            }
        }
    }
}
