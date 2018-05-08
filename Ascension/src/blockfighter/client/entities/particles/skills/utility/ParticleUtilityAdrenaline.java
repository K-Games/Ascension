package blockfighter.client.entities.particles.skills.utility;

import blockfighter.client.Core;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;

public class ParticleUtilityAdrenaline extends Particle {

    public ParticleUtilityAdrenaline(final Player p) {
        super(p);
        this.frame = 0;
        this.frameDuration = 50;
        this.duration = 300;
        this.particleData = Globals.Particles.UTILITY_ADRENALINE;
    }

    @Override
    public void update() {
        if (Globals.nsToMs(Core.getLogicModule().getTime() - this.lastFrameTime) >= this.frameDuration) {
            this.x = owner.getX();
            this.y = owner.getY();
            double factor = 1D * Globals.nsToMs(Core.getLogicModule().getTime() - this.particleStartTime) / this.duration;
            double delta = 250;
            delta -= (factor * delta);
            ParticleUtilityAdrenalineCloneParticle b = new ParticleUtilityAdrenalineCloneParticle((int) (this.x + delta), this.y, this.owner, this.owner.getFacing(), this.owner.getAnimState(), this.owner.getFrame());
            Core.getLogicModule().getScreen().addParticle(b);
            b = new ParticleUtilityAdrenalineCloneParticle((int) (this.x - delta), this.y, this.owner, this.owner.getFacing(), this.owner.getAnimState(), this.owner.getFrame());
            Core.getLogicModule().getScreen().addParticle(b);
            b = new ParticleUtilityAdrenalineCloneParticle(this.x, (int) (this.y + delta), this.owner, this.owner.getFacing(), this.owner.getAnimState(), this.owner.getFrame());
            Core.getLogicModule().getScreen().addParticle(b);
            b = new ParticleUtilityAdrenalineCloneParticle(this.x, (int) (this.y - delta), this.owner, this.owner.getFacing(), this.owner.getAnimState(), this.owner.getFrame());
            Core.getLogicModule().getScreen().addParticle(b);

            this.lastFrameTime = Core.getLogicModule().getTime();
        }
    }
}
