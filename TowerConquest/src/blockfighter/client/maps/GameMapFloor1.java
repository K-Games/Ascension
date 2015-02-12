package blockfighter.client.maps;

import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Ken Kwan
 */
public class GameMapFloor1 extends GameMap {

    BufferedImage bg;

    public GameMapFloor1() {
        setMapID(0);
    }

    @Override
    public void draw(Graphics2D g) {
        g.drawImage(bg, -640, -1500, bg.getWidth() * 8, bg.getHeight() * 8, null);
        g.setColor(Color.BLACK);
        g.drawRect(0, 620, 5000, 30);
        g.drawRect(200, 400, 300, 30);
        g.drawRect(600, 180, 300, 30);
        super.draw(g);

    }

    @Override
    public void loadAssets() throws Exception {
        bg = ImageIO.read(Globals.class.getResource("sprites/maps/" + getMapID() + "/bg.png"));
    }

}
