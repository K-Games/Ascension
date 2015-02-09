package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.screen.ScreenIngame;
import java.awt.Graphics2D;

public class ParticleBowStormArea extends Particle {

    public ParticleBowStormArea( int k, int x, int y, byte f) {
        super( k, x, y, f);
        frame = 0;
        frameDuration = 25;
        duration = 5000;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (duration > 200) {
            ParticleBowStormArrow b = new ParticleBowStormArrow(((ScreenIngame) logic.getScreen()).getNextParticleKey(), x, y, facing);
            ((ScreenIngame) logic.getScreen()).addParticle(b);

        }

    }

    @Override
    public void draw(Graphics2D g) {
    }
}
