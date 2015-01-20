package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ken
 */
public class ParticleUpgrade extends Particle {

    private int color, dstX, dstY, oX, oY;
    private boolean set = false;

    public ParticleUpgrade(LogicModule l, int k, int x, int y, long d, int c, int dstX, int dstY) {
        super(l, k, x, y, d);
        color = c;
        this.dstX = dstX;
        this.dstY = dstY;
        oX = x;
        oY = y;
    }

    @Override
    public void update() {
        super.update();
        if (duration <= 250) {
            dstX = 1140 + 30 - 7;
            dstY = 450 + 30 - 7;
            if (!set) {
                oX = x;
                oY = y;
            }
            x += (int) ((dstX - oX) / 5);
            y += (int) ((dstY - oY) / 5);
        } else if (duration >= 750) {
            x += (int) ((dstX - oX) / 5);
            y += (int) ((dstY - oY) / 5);
        }
    }

    @Override
    public void draw(Graphics g) {
        BufferedImage sprite = Globals.MENU_UPGRADEPARTICLE[color];
        g.drawImage(sprite, x, y, null);

    }
}
