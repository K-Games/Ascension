package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.screen.ScreenIngame;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ParticleSwordDrive extends Particle {

    private byte player;

    public ParticleSwordDrive(LogicModule l, int k, int x, int y, byte f, byte p) {
        super(l, k, x, y, f);
        frame = 0;
        frameDuration = 25;
        duration = 1000;
        player = p;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (frameDuration <= 0) {
            frameDuration = 25;
            frame++;
            if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_SWORD_DRIVE].length) {
                frame = 0;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SWORD_DRIVE] == null) {
            loadParticles();
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_SWORD_DRIVE].length) {
            return;
        }
        Point p = ((ScreenIngame) logic.getScreen()).getPlayerPos(player);
        if (facing == Globals.RIGHT) {
            x = p.x - 310;
        } else {
            x = p.x - 560 + 310;
        }
        y = p.y - 167;
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SWORD_DRIVE][frame];
        int drawSrcX = x + ((facing == Globals.RIGHT) ? 0 : sprite.getWidth());
        int drawSrcY = y - 70;
        int drawDscY = drawSrcY + sprite.getHeight();
        int drawDscX = x + ((facing == Globals.RIGHT) ? sprite.getWidth() : 0);
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
        /*        g.setColor(Color.BLACK);
         g.drawRect(x, y, 560, 150);*/
    }
}
