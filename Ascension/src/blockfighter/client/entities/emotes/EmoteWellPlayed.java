package blockfighter.client.entities.emotes;

import blockfighter.client.Core;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class EmoteWellPlayed extends Emote {

    private static final String WELL_PLAYED = "Well Played!";
    private int characterCount = 0;
    private long lastUpdateTime = 0;

    public EmoteWellPlayed(Player owner) {
        super(owner);
        this.frame = 0;
    }

    @Override
    public void update() {
        super.update();
        if (Core.getLogicModule().getTime() - this.lastUpdateTime >= Globals.msToNs(25)) {
            if (++characterCount > WELL_PLAYED.length()) {
                characterCount = WELL_PLAYED.length();
            }
            this.lastUpdateTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        final Point p = this.owner.getPos();
        if (p != null) {
            this.x = p.x;
            this.y = p.y;
        }
        final String output = WELL_PLAYED.substring(0, this.characterCount);
        g.setFont(Globals.ARIAL_18PT);
        int outputWidth = g.getFontMetrics().stringWidth(WELL_PLAYED);
        float drawX = this.x - outputWidth / 2;
        float drawY = this.y - 140;
        for (int i = 0; i < 2; i++) {
            g.setColor(Color.BLACK);
            g.drawString(output, drawX - 1 + i * 2 * 1, drawY);
            g.drawString(output, drawX, drawY - 1 + i * 2 * 1);
        }
        g.setColor(Color.WHITE);
        g.drawString(output, drawX, drawY);
    }
}
