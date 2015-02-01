package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ken Kwan
 */
public class ParticleMenuUpgrade extends Particle {

    private int color, deltaX, deltaY, oX, oY;
    private boolean set = false;

    public ParticleMenuUpgrade(LogicModule l, int k, int x, int y, int c, int dX, int dY) {
        super(l, k, x, y);
        duration = 1000;
        color = c;
        deltaX = dX;
        deltaY = dY;
        oX = x;
        oY = y;
    }

    @Override
    public void update() {
        super.update();
        if (duration <= 250) {
            deltaX = 1140 + 30 - 7;
            deltaY = 450 + 30 - 7;
            if (!set) {
                oX = x;
                oY = y;
            }
            x += (int) ((deltaX - oX) / 5);
            y += (int) ((deltaY - oY) / 5);
        } else {
            x += deltaX;
            y += deltaY;
            deltaX++;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        BufferedImage sprite = Globals.MENU_UPGRADEPARTICLE[color];
        g.drawImage(sprite, x, y, null);

    }
}
