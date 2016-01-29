package blockfighter.client.maps;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * PvP map
 *
 * @author Ken Kwan
 */
public class GameMapArena extends GameMap {

    BufferedImage[] platforms = new BufferedImage[3];
    String bgm;

    public GameMapArena() {
        super.setMapID(0);
    }

    @Override
    public void draw(final Graphics2D g) {
        g.drawImage(this.platforms[2], 0, 600, 3700, 30, null);

        g.drawImage(this.platforms[0], 700, 350, null);
        g.drawImage(this.platforms[1], 1700, 350, null);
        g.drawImage(this.platforms[1], 2700, 350, null);

        g.drawImage(this.platforms[0], 200, 100, null);
        g.drawImage(this.platforms[0], 1200, 100, null);
        g.drawImage(this.platforms[1], 2200, 100, null);
        g.drawImage(this.platforms[0], 3200, 100, null);

        g.drawImage(this.platforms[0], 700, -150, null);
        g.drawImage(this.platforms[0], 1700, -150, null);
        g.drawImage(this.platforms[1], 2700, -150, null);

        g.drawImage(this.platforms[1], 200, -400, null);
        g.drawImage(this.platforms[0], 1200, -400, null);
        g.drawImage(this.platforms[0], 2200, -400, null);
        g.drawImage(this.platforms[1], 3200, -400, null);
        super.draw(g);

    }

    @Override
    public void loadAssets() throws Exception {
        this.bg = ImageIO.read(Globals.class.getResourceAsStream("sprites/maps/" + getMapID() + "/bg.png"));
        for (int i = 0; i < this.platforms.length; i++) {
            this.platforms[i] = ImageIO.read(Globals.class.getResourceAsStream("sprites/maps/" + getMapID() + "/plat" + i + ".png"));
        }
        this.bgm = "bgm/" + Globals.rng(3) + ".ogg";
    }

    @Override
    public String getBGM() {
        return bgm;
    }
}
