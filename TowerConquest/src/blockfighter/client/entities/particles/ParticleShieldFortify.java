package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.entities.player.Player;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ParticleShieldFortify extends Particle {

    private Player owner;

    public ParticleShieldFortify(int k, int x, int y, byte f, Player p) {
        super(k, x, y, f);
        frame = 0;
        frameDuration = 25;
        duration = 500;
        owner = p;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (frameDuration <= 0) {
            frameDuration = 25;
            if (frame < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_FORTIFY].length - 1) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_FORTIFY] == null) {
            return;
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_FORTIFY].length) {
            return;
        }
        Point p = owner.getPos();
        x = p.x - 238;
        y = p.y - 410;
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_FORTIFY][frame];
        int drawSrcX = x;
        int drawSrcY = y;
        int drawDscY = drawSrcY + sprite.getHeight();
        int drawDscX = x + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
