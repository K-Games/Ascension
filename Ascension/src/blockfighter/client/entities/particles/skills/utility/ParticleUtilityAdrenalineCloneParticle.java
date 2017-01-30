package blockfighter.client.entities.particles.skills.utility;

import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;

public class ParticleUtilityAdrenalineCloneParticle extends Particle {

    byte cloneAnimState, cloneFacing, cloneFrame;

    public ParticleUtilityAdrenalineCloneParticle(final int x, final int y, final Player owner, final byte facing, final byte animState, final byte frame) {
        super(x, y, owner);
        this.duration = 200;
        this.cloneFacing = facing;
        this.cloneAnimState = animState;
        this.cloneFrame = frame;
    }

    @Override
    public void draw(final Graphics2D g) {
        Composite reset = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        owner.drawSprite(g, this.x, this.y, this.cloneFacing, this.cloneAnimState, this.cloneFrame);
        g.setComposite(reset);
    }
}
