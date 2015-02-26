package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ken Kwan
 */
public class ScreenSpriteTest extends ScreenMenu {

    private byte standFrame = 0, jumpFrame = 0, walkFrame = 0, buffFrame = 0,
            att1Frame = 0, att2Frame = 0, att3Frame = 0, att4Frame = 0, att5Frame = 0;

    private double nextFrameTime = 0;
    private int itemCode = 100000;
    private ItemEquip e = new ItemEquip(itemCode);

    @Override
    public void update() {
        double now = System.nanoTime(); //Get time now
        if (now - lastUpdateTime >= Globals.LOGIC_UPDATE) {

            nextFrameTime -= Globals.LOGIC_UPDATE;
            if (nextFrameTime <= 0) {
                standFrame++;
                if (standFrame == Globals.CHAR_SPRITE[Globals.PLAYER_STATE_STAND].length) {
                    standFrame = 0;
                }

                walkFrame++;
                if (walkFrame == Globals.CHAR_SPRITE[Globals.PLAYER_STATE_WALK].length) {
                    walkFrame = 0;
                }

                buffFrame++;
                if (buffFrame == Globals.CHAR_SPRITE[Globals.PLAYER_STATE_BUFF].length) {
                    buffFrame = 0;
                }

                att1Frame++;
                if (att1Frame == Globals.CHAR_SPRITE[Globals.PLAYER_STATE_ATTACK1].length) {
                    att1Frame = 0;
                }

                att2Frame++;
                if (att2Frame == Globals.CHAR_SPRITE[Globals.PLAYER_STATE_ATTACK2].length) {
                    att2Frame = 0;
                }

                att3Frame++;
                if (att3Frame == Globals.CHAR_SPRITE[Globals.PLAYER_STATE_ATTACKOFF1].length) {
                    att3Frame = 0;
                }

                att4Frame++;
                if (att4Frame == Globals.CHAR_SPRITE[Globals.PLAYER_STATE_ATTACKOFF2].length) {
                    att4Frame = 0;
                }

                att5Frame++;
                if (att5Frame == Globals.CHAR_SPRITE[Globals.PLAYER_STATE_ATTACKBOW].length) {
                    att5Frame = 0;
                }
                nextFrameTime = 100000000;
            }
            lastUpdateTime = now;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        drawSlots(g);
    }

    private void drawSlots(Graphics2D g) {
        BufferedImage character = Globals.CHAR_SPRITE[Globals.PLAYER_STATE_STAND][standFrame];
        int x = 50 + character.getWidth() / 2, y = 100 + character.getHeight();
        e.drawIngame(g, x, y, Globals.PLAYER_STATE_STAND, standFrame, Globals.RIGHT, true);
        g.drawImage(character, 50, 100, null);
        e.drawIngame(g, x, y, Globals.PLAYER_STATE_STAND, standFrame, Globals.RIGHT);

        character = Globals.CHAR_SPRITE[Globals.PLAYER_STATE_WALK][walkFrame];
        x = 250 + character.getWidth() / 2;
        y = 100 + character.getHeight();
        e.drawIngame(g, x, y, Globals.PLAYER_STATE_WALK, walkFrame, Globals.RIGHT, true);
        g.drawImage(character, 250, 100, null);
        e.drawIngame(g, x, y, Globals.PLAYER_STATE_WALK, walkFrame, Globals.RIGHT);

        character = Globals.CHAR_SPRITE[Globals.PLAYER_STATE_JUMP][jumpFrame];
        x = 450 + character.getWidth() / 2;
        y = 100 + character.getHeight();
        e.drawIngame(g, x, y, Globals.PLAYER_STATE_JUMP, jumpFrame, Globals.RIGHT, true);
        g.drawImage(character, 450, 100, null);
        e.drawIngame(g, x, y, Globals.PLAYER_STATE_JUMP, jumpFrame, Globals.RIGHT);

        character = Globals.CHAR_SPRITE[Globals.PLAYER_STATE_BUFF][buffFrame];
        x = 650 + character.getWidth() / 2;
        y = 100 + character.getHeight();
        e.drawIngame(g, x, y, Globals.PLAYER_STATE_BUFF, buffFrame, Globals.RIGHT, true);
        g.drawImage(character, 650, 100, null);
        e.drawIngame(g, x, y, Globals.PLAYER_STATE_BUFF, buffFrame, Globals.RIGHT);

        character = Globals.CHAR_SPRITE[Globals.PLAYER_STATE_ATTACK1][att1Frame];
        x = 50 + character.getWidth() / 2;
        y = 400 + character.getHeight();
        e.drawIngame(g, x, y, Globals.PLAYER_STATE_ATTACK1, att1Frame, Globals.RIGHT, true);
        g.drawImage(character, 50 + 10, 400, null);
        e.drawIngame(g, x, y, Globals.PLAYER_STATE_ATTACK1, att1Frame, Globals.RIGHT);

        character = Globals.CHAR_SPRITE[Globals.PLAYER_STATE_ATTACK2][att2Frame];
        x = 250 + character.getWidth() / 2;
        y = 400 + character.getHeight();
        e.drawIngame(g, x, y, Globals.PLAYER_STATE_ATTACK2, att2Frame, Globals.RIGHT, true);
        g.drawImage(character, 250 + 25, 400, null);
        e.drawIngame(g, x, y, Globals.PLAYER_STATE_ATTACK2, att2Frame, Globals.RIGHT);

        character = Globals.CHAR_SPRITE[Globals.PLAYER_STATE_ATTACKOFF1][att3Frame];
        x = 450 + character.getWidth() / 2;
        y = 400 + character.getHeight();
        e.drawIngame(g, x, y, Globals.PLAYER_STATE_ATTACKOFF1, att3Frame, Globals.RIGHT, true);
        g.drawImage(character, 450 + 40, 400, null);
        e.drawIngame(g, x, y, Globals.PLAYER_STATE_ATTACKOFF1, att3Frame, Globals.RIGHT);

        character = Globals.CHAR_SPRITE[Globals.PLAYER_STATE_ATTACKOFF2][att4Frame];
        x = 650 + character.getWidth() / 2;
        y = 400 + character.getHeight();
        e.drawIngame(g, x, y, Globals.PLAYER_STATE_ATTACKOFF2, att4Frame, Globals.RIGHT, true);
        g.drawImage(character, 650 + 40, 400, null);
        e.drawIngame(g, x, y, Globals.PLAYER_STATE_ATTACKOFF2, att4Frame, Globals.RIGHT);
        
        character = Globals.CHAR_SPRITE[Globals.PLAYER_STATE_ATTACKBOW][att5Frame];
        x = 950 + character.getWidth() / 2;
        y = 400 + character.getHeight();
        e.drawIngame(g, x, y, Globals.PLAYER_STATE_ATTACKBOW, att5Frame, Globals.RIGHT, true);
        g.drawImage(character, 950, 400, null);
        e.drawIngame(g, x, y, Globals.PLAYER_STATE_ATTACKBOW, att5Frame, Globals.RIGHT);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void unload() {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

}
