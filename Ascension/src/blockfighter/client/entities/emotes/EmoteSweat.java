package blockfighter.client.entities.emotes;

import blockfighter.client.Core;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class EmoteSweat extends Emote {

    public EmoteSweat(Player owner) {
        super(owner);
        this.frame = 0;
        this.frameDuration = 75;
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastFrameTime) >= this.frameDuration) {
            if (Globals.Emotes.SWEAT.getSprite() != null && this.frame < Globals.Emotes.SWEAT.getSprite().length - 1) {
                this.frame++;
            }
            this.lastFrameTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        if (Globals.Emotes.SWEAT.getSprite() == null) {
            return;
        }
        if (this.frame >= Globals.Emotes.SWEAT.getSprite().length) {
            return;
        }
        final Point p = this.owner.getPos();
        if (p != null) {
            this.x = p.x - 22;
            this.y = p.y - 118;
        }
        final BufferedImage sprite = Globals.Emotes.SWEAT.getSprite()[this.frame];
        final int drawSrcX = this.x;
        final int drawSrcY = this.y;
        g.drawImage(sprite, drawSrcX, drawSrcY, sprite.getWidth(), sprite.getHeight(), null);
    }
}
