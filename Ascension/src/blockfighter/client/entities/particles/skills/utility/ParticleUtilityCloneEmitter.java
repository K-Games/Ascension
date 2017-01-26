package blockfighter.client.entities.particles.skills.utility;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;

public class ParticleUtilityCloneEmitter extends Particle {

    private long lastParticleTime = 0;

    public ParticleUtilityCloneEmitter(final Player p) {
        super(p);
        this.frame = 0;
        this.duration = 100000;
    }

    @Override
    public void update() {
        super.update();
        if (!isExpired() && Globals.nsToMs(Core.getLogicModule().getTime() - lastParticleTime) >= 75) {
            final Point p = this.owner.getPos();
            if (p != null) {
                this.x = p.x;
                this.y = p.y;
                final ParticleUtilityCloneParticle b = new ParticleUtilityCloneParticle(this.x, this.y, this.owner, this.owner.getFacing(), this.owner.getAnimState(), this.owner.getFrame());
                //Core.getLogicModule().getScreen().addParticle(b);
            }
            lastParticleTime = Core.getLogicModule().getTime();
        }
    }

    @Override
    public boolean isExpired() {
        return super.isExpired() || this.owner.isDead();
    }

    @Override
    public void draw(final Graphics2D g) {
        Composite reset = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        int offset = (owner.getFacing() == Globals.RIGHT) ? -35 : 35;
        owner.drawSprite(g, owner.getX() + offset, owner.getY(), owner.getFacing(), owner.getAnimState(), owner.getFrame());
        g.setComposite(reset);
    }
}
