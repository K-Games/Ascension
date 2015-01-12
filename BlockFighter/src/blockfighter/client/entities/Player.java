package blockfighter.client.entities;

import blockfighter.client.Globals;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ken
 */
public class Player {

    private int x, y;
    private byte facing, state, frame;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setFacing(byte dir) {
        facing = dir;
    }

    public void setState(byte s) {
        state = s;
    }

    public void setFrame(byte f) {
        frame = f;
    }

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        facing = Globals.RIGHT;
        state = Globals.PLAYER_STATE_STAND;
        frame = 0;
    }

    public void draw(Graphics g) {
        byte s = state, f = frame;
        if (f >= Globals.CHAR_SPRITE[s].length) {
            f = 0;
        }
        BufferedImage sprite = Globals.CHAR_SPRITE[s][f];
        int drawSrcX = x - ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth() / 2;
        int drawSrcY = y - sprite.getHeight();
        int drawDscX = x + ((facing == Globals.RIGHT) ? 1 : -1) * sprite.getWidth() / 2;
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, y, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }

}
