package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.entities.player.Player;
import blockfighter.client.screen.ScreenIngame;
import java.awt.Point;

public class ParticleBloodEmitter extends Particle {

    private final Player owner;
    private long lastParticleTime = 0;

    public ParticleBloodEmitter(final int k, final Player p) {
        super(k, 0, 0);
        this.frame = 0;
        this.duration = 500;
        this.owner = p;
    }

    @Override
    public void update() {
        super.update();
        if (!isExpired() && Globals.nsToMs(logic.getTime() - lastParticleTime) >= 10) {
            final Point p = this.owner.getPos();
            if (p != null) {
                this.x = p.x;
                this.y = p.y;
            }
            for (int i = 0; i < 5; i++) {
                final ParticleBlood b = new ParticleBlood(((ScreenIngame) logic.getScreen()).getNextParticleKey(), this.x, this.y,
                        this.owner.getFacing());
                ((ScreenIngame) logic.getScreen()).addParticle(b);
            }
            lastParticleTime = logic.getTime();
        }
    }
}
