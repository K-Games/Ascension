package blockfighter.client.entities.emotes;

import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class EmoteGoodGame extends Emote {

    private float[] charX = new float[2];
    private float[] charY = new float[2];
    private float[] ySpeed = new float[2];

    public EmoteGoodGame(Player owner) {
        super(owner);
        this.frame = 0;
        charX[0] = -23;
        charX[1] = charX[0] + 20;
        charY[0] = -30;
        charY[1] = charY[0];
    }

    @Override
    public void update() {
        super.update();
        charY[0] += ySpeed[0];
        charY[1] += ySpeed[1];

        ySpeed[0] += 0.6;
        ySpeed[1] += 0.5;

        if (charY[0] >= 0 && ySpeed[0] > 0) {
            ySpeed[0] *= -0.55;
        }

        if (charY[1] >= 0 && ySpeed[1] > 0) {
            ySpeed[1] *= -0.55;
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        final Point p = this.owner.getPos();
        if (p != null) {
            this.x = p.x;
            this.y = p.y;
        }
        for (int i = 0; i < 2; i++) {
            String output = "G";
            g.setFont(Globals.ARIAL_24PT);
            float drawX = this.x + charX[i];
            float drawY = this.y - 140 + charY[i];
            for (int j = 0; j < 2; j++) {
                g.setColor(Color.BLACK);
                g.drawString(output, drawX - 1 + j * 2 * 1, drawY);
                g.drawString(output, drawX, drawY - 1 + j * 2 * 1);
            }
            g.setColor(Color.WHITE);
            g.drawString(output, drawX, drawY);
        }
    }
}
