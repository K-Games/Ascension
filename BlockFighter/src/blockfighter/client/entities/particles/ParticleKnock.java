/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.entities.Particle;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class ParticleKnock extends Particle {

    public ParticleKnock(LogicModule l, int k, int x, int y, long d) {
        super(l, k, x, y, d);
        frame = 0;
        frameDuration = 75;
    }

    @Override
    public void update() {
        super.update();
        frameDuration -= Globals.LOGIC_UPDATE / 1000000;
        if (frameDuration <= 0 && frame < Globals.PARTICLE_SPRITE[Globals.PARTICLE_KNOCK].length) {
            frameDuration = 75;
            frame++;
        }

    }

    @Override
    public void draw(Graphics g) {
        if (frame >= Globals.PARTICLE_SPRITE[Globals.PARTICLE_KNOCK].length) {
            return;
        }
        BufferedImage sprite = Globals.PARTICLE_SPRITE[Globals.PARTICLE_KNOCK][frame];
        int drawSrcX = x - ((facing == Globals.RIGHT) ? 1 : -1) * 2 * sprite.getWidth() / 2;
        int drawSrcY = y - 2 * sprite.getHeight();
        int drawDscX = x + ((facing == Globals.RIGHT) ? 1 : -1) * 2 * sprite.getWidth() / 2;
        g.drawImage(sprite, drawSrcX, drawSrcY, drawDscX, y, 0, 0, sprite.getWidth(), sprite.getHeight(), null);
    }
}
