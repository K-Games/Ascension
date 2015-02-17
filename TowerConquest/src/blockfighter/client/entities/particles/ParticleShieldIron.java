package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.entities.player.Player;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ParticleShieldIron extends Particle {

    private Player owner;

    public ParticleShieldIron(int k, Player p) {
        super(k, 0, 0);
        frame = 0;
        frameDuration = 25;
        duration = 2300;
        owner = p;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (frameDuration <= 0) {
            frameDuration = 25;
            if (frame < PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_IRON].length - 1) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_IRON] == null) {
            return;
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_IRON].length) {
            return;
        }
        Point p = owner.getPos();
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SHIELD_IRON][frame];
        x = p.x - sprite.getWidth() / 2 ;
        y = p.y - sprite.getHeight() + 65;
        int drawSrcX = x;
        int drawSrcY = y;
        int drawDscY = drawSrcY + sprite.getHeight();
        int drawDscX = x + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
