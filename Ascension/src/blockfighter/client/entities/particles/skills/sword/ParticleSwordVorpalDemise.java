package blockfighter.client.entities.particles.skills.sword;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ParticleSwordVorpalDemise extends Particle {

    public ParticleSwordVorpalDemise(final Player p) {
        super(p);
        final Point point = this.owner.getPos();
        if (point != null) {
            this.x = point.x + (-25 + Globals.rng(51));
            this.y = point.y + (-50 + Globals.rng(101));
        }
        this.frame = 0;
        this.frameDuration = 100;
        this.duration = 500;
        this.particleData = Globals.Particles.SWORD_VORPAL_DEMISE;
    }

    @Override
    public void draw(final Graphics2D g) {
        if (this.spriteFrameExists()) {
            final BufferedImage sprite = this.particleData.getSprites()[this.frame];
            draw(g, -sprite.getWidth() / 2, 0, false);
        }
    }
}
