package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class ParticleBowVolleyArrow extends Particle {

    int[][] lightningPointsX, lightningPointsY;
    float drawWidth = 4;
    Color[] color = {Color.white, new Color(88, 184, 232), new Color(184, 224, 240)};
    int colourIndex;

    public ParticleBowVolleyArrow(final int x, final int y, final byte f) {
        super(x, y, f);
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 300;
        this.lightningPointsX = new int[3][30];
        this.lightningPointsY = new int[3][30];
        this.colourIndex = Globals.rng(color.length);
    }

    @Override
    public void update() {
        super.update();
        if (Globals.nsToMs(logic.getTime() - this.lastFrameTime) >= this.frameDuration) {
            this.drawWidth -= 4.0f / (this.duration / Globals.nsToMs((long) Globals.LOGIC_UPDATE));
            for (byte j = 0; j < this.lightningPointsX.length; j++) {
                double nextX = 0;
                for (byte i = 0; i < this.lightningPointsX[j].length; i++) {
                    if (i != this.lightningPointsX[j].length - 1) {
                        this.lightningPointsX[j][i] = (int) (this.x + nextX);
                        this.lightningPointsY[j][i] = (int) (this.y + Globals.rng(30) - 15);
                        if (nextX < 465) {
                            nextX += Globals.rng(25) + 25;
                        } else {
                            this.lightningPointsY[j][i] = this.y;
                        }

                        if (nextX >= 465) {
                            nextX = 465;
                        }
                    } else {
                        this.lightningPointsX[j][i] = (int) (this.x + 465);
                    }

                    if (i == 0 || i == this.lightningPointsX[j].length - 1) {
                        this.lightningPointsY[j][i] = this.y;
                    }
                }
            }
//            if (PARTICLE_SPRITE != null && this.frame < PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYARROW].length - 1) {
//                this.frame++;
//            }
            this.lastFrameTime = logic.getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        for (byte j = 0; j < this.lightningPointsX.length; j++) {
            g.setColor(this.color[this.colourIndex]);
            g.setStroke(new BasicStroke(this.drawWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawPolyline(this.lightningPointsX[j], this.lightningPointsY[j], this.lightningPointsX[j].length);

        }
        g.drawLine(this.x, this.y, this.x + 465, y);
        g.setStroke(new BasicStroke());
//        if (PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYARROW] == null) {
//            return;
//        }
//        if (this.frame >= PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYARROW].length) {
//            return;
//        }
//        final BufferedImage sprite = PARTICLE_SPRITE[Globals.PARTICLE_BOW_VOLLEYARROW][this.frame];
//        final int drawSrcX = this.x + ((this.facing == Globals.RIGHT) ? 0 : sprite.getWidth());
//        final int drawSrcY = this.y;
//        final int drawDscY = drawSrcY + sprite.getHeight();
//        final int drawDscX = this.x + ((this.facing == Globals.RIGHT) ? sprite.getWidth() : 0);
//        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, drawDscY, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
