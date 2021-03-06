package blockfighter.client.entities.emotes;

import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class EmoteAngry extends Emote {

    double deltaY = 0, baseSpeed = -2, ySpeed = baseSpeed;

    public EmoteAngry(Player owner) {
        super(owner);
        this.frame = 0;
    }

    @Override
    public void update() {
        super.update();
        this.ySpeed += 0.6;
        this.deltaY += this.ySpeed;
        if (this.deltaY >= 0) {
            this.deltaY = 0;
            this.ySpeed = this.baseSpeed;
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Emotes.ANGRY.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Emotes.ANGRY.getSprite().length) {
            return;
        }
        final Point p = this.owner.getPos();
        if (p != null) {
            this.x = p.x + 14;
            this.y = (int) (p.y - 115 + deltaY);
        }
        final BufferedImage sprite = Globals.Emotes.ANGRY.getSprite()[this.frame];
        final int drawSrcX = this.x;
        final int drawSrcY = this.y;
        final int drawDscY = drawSrcY + sprite.getHeight();
        final int drawDscX = drawSrcX + sprite.getWidth();
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
