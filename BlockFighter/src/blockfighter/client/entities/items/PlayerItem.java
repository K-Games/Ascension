package blockfighter.client.entities.items;

import java.awt.Graphics2D;

/**
 *
 * @author Ken Kwan
 */
public interface PlayerItem {

    public void draw(Graphics2D g, int x, int y);

    public int getItemCode();

    public String getItemName();
}
