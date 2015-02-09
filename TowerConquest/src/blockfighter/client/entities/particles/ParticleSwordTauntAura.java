package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.screen.ScreenIngame;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ParticleSwordTauntAura extends Particle {

    private byte player;

    public ParticleSwordTauntAura(int k, int x, int y, byte f, byte p) {
        super(k, x, y, f);
        frame = 0;
        frameDuration = 25;
        duration = 500;
        player = p;
        Point point = ((ScreenIngame) logic.getScreen()).getPlayerPos(player);
        this.x = point.x;
        this.y = point.y;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (duration > 100) {
            for (int i = 0; i < 2; i++) {
                ParticleSwordTauntAuraParticle b = new ParticleSwordTauntAuraParticle(((ScreenIngame) logic.getScreen()).getNextParticleKey(), x, y, facing);
                ((ScreenIngame) logic.getScreen()).addParticle(b);
            }
        }
        if (frameDuration <= 0) {
            frameDuration = 25;
            if (frame < PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA1].length - 1) {
                frame++;
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA1] == null) {
            loadParticles();
        }
        if (frame >= PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA1].length) {
            return;
        }
        Point p = ((ScreenIngame) logic.getScreen()).getPlayerPos(player);
        if (p != null) {
            x = p.x;
            y = p.y;
        }
        BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_SWORD_TAUNTAURA1][frame];
        int drawSrcX = x - sprite.getWidth() / 2;
        int drawSrcY = y - sprite.getHeight() + 20;
        int drawDscY = drawSrcY + sprite.getHeight();
        int drawDscX = x + sprite.getWidth() / 2;
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
