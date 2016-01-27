package blockfighter.client.entities.particles;

import static blockfighter.client.entities.particles.Particle.logic;
import blockfighter.client.entities.player.Player;
import blockfighter.client.screen.ScreenIngame;
import java.awt.Point;

public class ParticleBloodEmitter extends Particle {

    private Player owner;

    public ParticleBloodEmitter(int k, Player p) {
        super(k, 0, 0);
        frame = 0;
        duration = 500;
        owner = p;
    }

    @Override
    public void update() {
        super.update();
        if (duration > 0 && duration % 15 == 0) {
            Point p = owner.getPos();
            if (p != null) {
                x = p.x;
                y = p.y;
            }
            for (int i = 0; i <15; i++) {
                ParticleBlood b = new ParticleBlood(((ScreenIngame) logic.getScreen()).getNextParticleKey(), x, y, owner.getFacing());
                ((ScreenIngame) logic.getScreen()).addParticle(b);
            }
        }
    }
}
