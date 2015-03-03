package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.entities.player.Player;
import blockfighter.client.screen.ScreenIngame;
import java.awt.Point;

public class ParticleShieldDashEmitter extends Particle {

    private Player owner;

    public ParticleShieldDashEmitter(int k, byte f, Player p) {
        super(k, 0, 0, f);
        frame = 0;
        duration = 250;
        owner = p;
    }

    @Override
    public void update() {
        super.update();
        if (duration > 0) {
            Point p = owner.getPos();
            if (p != null) {
                x = p.x;
                y = p.y;
            }
            ParticleShieldDash b = new ParticleShieldDash(((ScreenIngame) logic.getScreen()).getNextParticleKey(), x + ((facing == Globals.RIGHT) ? -172 : -200), y - 330, facing);
            ((ScreenIngame) logic.getScreen()).addParticle(b);
        }
    }
}
