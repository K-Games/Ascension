package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.entities.player.Player;
import blockfighter.client.screen.ScreenIngame;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ParticleShieldCharge extends Particle {

    private Player owner;

    public ParticleShieldCharge(int k, int x, int y, byte f, Player p) {
        super(k, x, y, f);
        frame = 0;
        frameDuration = 25;
        duration = 750;
        owner = p;
        Point point = owner.getPos();
        if (point != null) {
            this.x = point.x;
            this.y = point.y;
        }
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (duration > 100 && duration % 50 == 0) {
            ParticleShieldChargeParticle b = new ParticleShieldChargeParticle(((ScreenIngame) logic.getScreen()).getNextParticleKey(), x, y, facing);
            ((ScreenIngame) logic.getScreen()).addParticle(b);
        }
        if (frameDuration <= 0) {
            frameDuration = 25;
            frame++;
            if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGE].length - 1) {
                frame = 0;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGE] == null) {
            return;
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGE].length) {
            return;
        }
        Point p = owner.getPos();
        if (p != null) {
            if (facing == Globals.RIGHT) {
                x = p.x - 200;
            } else {
                x = p.x - 428 + 200;
            }
        }
        y = p.y - 176;
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_CHARGE][frame];
        int drawSrcX = x + ((facing == Globals.RIGHT) ? 0 : sprite.getWidth());
        int drawSrcY = y;
        int drawDscY = drawSrcY + sprite.getHeight();
        int drawDscX = x + ((facing == Globals.RIGHT) ? sprite.getWidth() : 0);
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
