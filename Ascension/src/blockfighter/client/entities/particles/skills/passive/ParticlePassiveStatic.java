package blockfighter.client.entities.particles.skills.passive;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class ParticlePassiveStatic extends Particle {

    int[][] lightningPointsX, lightningPointsY;
    float drawWidth = 3;
    Color[] color = {Color.white, new Color(88, 184, 232), new Color(184, 224, 240)};
    int colourIndex;
    Player target;

    public ParticlePassiveStatic(final Player owner, final Player target) {
        super(owner);
        this.target = target;
        this.frame = 0;
        this.frameDuration = 25;
        this.duration = 300;
        this.lightningPointsX = new int[3][20];
        this.lightningPointsY = new int[3][20];
        this.particleData = Globals.Particles.PASSIVE_STATIC;
    }

    @Override
    public void update() {
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastFrameTime) >= this.frameDuration) {
            int distanceX = (this.target.getX() - this.owner.getX()) / this.lightningPointsX[0].length;
            int distanceY = ((this.target.getY() - 75) - (this.owner.getY() - 75)) / this.lightningPointsY[0].length;

            this.colourIndex = Globals.rng(color.length);
            this.drawWidth = 0.2f + 2.8f * (1f - Globals.nsToMs(Core.getLogicModule().getTime() - this.particleStartTime) / (float) this.duration);
            for (byte j = 0; j < this.lightningPointsX.length; j++) {
                for (byte i = 0; i < this.lightningPointsX[j].length; i++) {
                    this.lightningPointsX[j][i] = (int) (this.owner.getX() + i * distanceX + Globals.rng(20) - 10);
                    this.lightningPointsY[j][i] = (int) (this.owner.getY() - 75 + i * distanceY + Globals.rng(20) - 10);
                }
            }
            this.lastFrameTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        for (byte j = 0; j < this.lightningPointsX.length; j++) {
            g.setColor(this.color[this.colourIndex]);
            g.setStroke(new BasicStroke(this.drawWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawPolyline(this.lightningPointsX[j], this.lightningPointsY[j], this.lightningPointsX[j].length);
        }
        g.setStroke(new BasicStroke());
    }
}
