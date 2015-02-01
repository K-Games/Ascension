package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *
 * @author Ken Kwan
 */
public class ParticleMenuUpgradeEnd extends Particle {

    private int color, deltaX, deltaY;

    public ParticleMenuUpgradeEnd(LogicModule l, int k, int x, int y, int c, int dX, int dY) {
        super(l, k, x, y);
        duration = 1000;
        color = c;
        deltaX = dX;
        deltaY = dY;
    }

    @Override
    public void update() {
        super.update();
        x += deltaX;
        y += deltaY;
        if (new Random().nextInt(2) == 0) {
            deltaY++;
        }

    }

    @Override
    public void draw(Graphics2D g) {
        BufferedImage sprite = Globals.MENU_UPGRADEPARTICLE[color];
        g.drawImage(sprite, x, y, null);
    }
}
