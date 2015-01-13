package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ken
 */
public class ParticleMenuSmoke extends Particle {

    public ParticleMenuSmoke(LogicModule l, int k, int x, int y, long d) {
        super(l, k, x, y, d);
    }

    @Override
    public void update() {
        x -= 2;
        if (x <= -1280) {
            x = 1280;
        }
    }
    
    @Override
    public void draw(Graphics g){
        BufferedImage sprite = Globals.MENU_SMOKE[0];
        if (key == 0){
            g.drawImage(sprite, x, y, 1280, 720,null);
        } else {
            g.drawImage(sprite, x+1280, y, -1280, 720,null);
        }
    }
    
    @Override
    public boolean isExpired(){
        return false;
    }
}
