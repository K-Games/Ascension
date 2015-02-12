package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.entities.player.Player;
import blockfighter.client.screen.ScreenIngame;
import java.awt.Graphics2D;
import java.awt.Point;

public class ParticleSwordShieldDashEmitter extends Particle {

    private Player owner;

    public ParticleSwordShieldDashEmitter(int k, int x, int y, byte f, Player p) {
        super(k, x, y, f);
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

    @Override
    public void draw(Graphics2D g) {
    }
}
