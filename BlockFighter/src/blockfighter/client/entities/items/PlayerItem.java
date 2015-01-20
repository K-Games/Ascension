package blockfighter.client.entities.items;

import java.awt.Graphics;

/**
 *
 * @author Ken
 */
public interface PlayerItem {

    public void draw(Graphics g, int x, int y);

    public int getItemCode();

    public String getItemName();
}
