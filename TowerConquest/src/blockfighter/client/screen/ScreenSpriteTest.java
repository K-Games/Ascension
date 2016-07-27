package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ScreenSpriteTest extends ScreenMenu {

    private byte standFrame = 0;

    private final byte jumpFrame = 0;

    private byte walkFrame = 0;

    private byte buffFrame = 0;

    private byte att1Frame = 0;

    private byte att5Frame = 0;

    private byte rollFrame = 0;

    private long nextFrameTime = 0;
    private final int itemCode = 100000;
    private final ItemEquip e = new ItemEquip(this.itemCode);

    @Override
    public void update() {
        final long now = logic.getTime(); // Get time now
        if (now - this.lastUpdateTime >= Globals.LOGIC_UPDATE) {

            this.nextFrameTime -= Globals.LOGIC_UPDATE;
            if (this.nextFrameTime <= 0) {
                this.standFrame++;
                if (this.standFrame == Globals.CHAR_SPRITE[Globals.PLAYER_ANIM_STATE_STAND].length) {
                    this.standFrame = 0;
                }

                this.walkFrame++;
                if (this.walkFrame == Globals.CHAR_SPRITE[Globals.PLAYER_ANIM_STATE_WALK].length) {
                    this.walkFrame = 0;
                }

                this.buffFrame++;
                if (this.buffFrame == Globals.CHAR_SPRITE[Globals.PLAYER_ANIM_STATE_BUFF].length) {
                    this.buffFrame = 0;
                }

                this.att1Frame++;
                if (this.att1Frame == Globals.CHAR_SPRITE[Globals.PLAYER_ANIM_STATE_ATTACK].length) {
                    this.att1Frame = 0;
                }

                this.att5Frame++;
                if (this.att5Frame == Globals.CHAR_SPRITE[Globals.PLAYER_ANIM_STATE_ATTACKBOW].length) {
                    this.att5Frame = 0;
                }

                this.rollFrame++;
                if (this.rollFrame == Globals.CHAR_SPRITE[Globals.PLAYER_ANIM_STATE_ROLL].length) {
                    this.rollFrame = 0;
                }
                this.nextFrameTime = 150000000;
            }
            this.lastUpdateTime = now;
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        final Graphics2D g2d = g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        drawSlots(g);
    }

    private void drawSlots(final Graphics2D g) {
        BufferedImage character = Globals.CHAR_SPRITE[Globals.PLAYER_ANIM_STATE_STAND][this.standFrame];
        int x = 50 + character.getWidth() / 2, y = 100 + character.getHeight();
        this.e.drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_STAND, this.standFrame, Globals.RIGHT, true);
        g.drawImage(character, 50, 100, null);
        this.e.drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_STAND, this.standFrame, Globals.RIGHT);

        character = Globals.CHAR_SPRITE[Globals.PLAYER_ANIM_STATE_WALK][this.walkFrame];
        x = 250 + character.getWidth() / 2;
        y = 100 + character.getHeight();
        this.e.drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_WALK, this.walkFrame, Globals.RIGHT, true);
        g.drawImage(character, 250, 100, null);
        this.e.drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_WALK, this.walkFrame, Globals.RIGHT);

        character = Globals.CHAR_SPRITE[Globals.PLAYER_ANIM_STATE_JUMP][this.jumpFrame];
        x = 450 + character.getWidth() / 2;
        y = 100 + character.getHeight();
        this.e.drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_JUMP, this.jumpFrame, Globals.RIGHT, true);
        g.drawImage(character, 450, 100, null);
        this.e.drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_JUMP, this.jumpFrame, Globals.RIGHT);

        character = Globals.CHAR_SPRITE[Globals.PLAYER_ANIM_STATE_BUFF][this.buffFrame];
        x = 650 + character.getWidth() / 2;
        y = 100 + character.getHeight();
        this.e.drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_BUFF, this.buffFrame, Globals.RIGHT, true);
        g.drawImage(character, 650, 100, null);
        this.e.drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_BUFF, this.buffFrame, Globals.RIGHT);

        character = Globals.CHAR_SPRITE[Globals.PLAYER_ANIM_STATE_ATTACK][this.att1Frame];
        x = 50 + character.getWidth() / 2;
        y = 400 + character.getHeight();
        this.e.drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_ATTACK, this.att1Frame, Globals.RIGHT, true);
        g.drawImage(character, 50 + 10, 400, null);
        this.e.drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_ATTACK, this.att1Frame, Globals.RIGHT);

        character = Globals.CHAR_SPRITE[Globals.PLAYER_ANIM_STATE_ATTACKBOW][this.att5Frame];
        x = 950 + character.getWidth() / 2;
        y = 400 + character.getHeight();
        this.e.drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_ATTACKBOW, this.att5Frame, Globals.RIGHT, true);
        g.drawImage(character, 950, 400, null);
        this.e.drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_ATTACKBOW, this.att5Frame, Globals.RIGHT);

        character = Globals.CHAR_SPRITE[Globals.PLAYER_ANIM_STATE_ROLL][this.rollFrame];
        x = 720 + character.getWidth() / 2;
        y = 400 + character.getHeight();
        this.e.drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_ROLL, this.rollFrame, Globals.RIGHT, true);
        g.drawImage(character, 720, 400, null);
        this.e.drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_ROLL, this.rollFrame, Globals.RIGHT);
    }

    @SuppressWarnings("hiding")
    @Override
    public void keyTyped(final KeyEvent e) {

    }

    @SuppressWarnings("hiding")
    @Override
    public void keyPressed(final KeyEvent e) {

    }

    @SuppressWarnings("hiding")
    @Override
    public void keyReleased(final KeyEvent e) {
    }

    @SuppressWarnings("hiding")
    @Override
    public void mouseClicked(final MouseEvent e) {

    }

    @SuppressWarnings("hiding")
    @Override
    public void mousePressed(final MouseEvent e) {

    }

    @SuppressWarnings("hiding")
    @Override
    public void mouseReleased(final MouseEvent e) {

    }

    @Override
    public void unload() {
    }

    @SuppressWarnings("hiding")
    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @SuppressWarnings("hiding")
    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @SuppressWarnings("hiding")
    @Override
    public void mouseDragged(final MouseEvent e) {
    }

    @SuppressWarnings("hiding")
    @Override
    public void mouseMoved(final MouseEvent e) {
    }

}
