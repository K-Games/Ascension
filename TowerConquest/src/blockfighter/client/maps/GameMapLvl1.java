package blockfighter.client.maps;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public class GameMapLvl1 extends GameMap {

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.drawRect(0, 620, 5000, 30);
        g.drawRect(200, 400, 300, 30);
        g.drawRect(600, 180, 300, 30);
        super.draw(g);
    }

}
