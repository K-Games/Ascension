package blockfighter.client.entities.particles;

import blockfighter.client.Globals;
import static blockfighter.client.entities.particles.Particle.logic;
import blockfighter.client.entities.player.Player;
import blockfighter.client.screen.ScreenIngame;
import java.awt.Point;

public class ParticleSwordTauntBuffEmitter extends Particle {

    private long lastParticleTime = 0;

    public ParticleSwordTauntBuffEmitter(final Player p) {
        super(0, 0, p);
        this.frame = 0;
        this.duration = 10000;
    }

    @Override
    public void update() {
        super.update();
        if (!isExpired() && Globals.nsToMs(logic.getTime() - lastParticleTime) >= 50) {
            final Point p = this.owner.getPos();
            if (p != null) {
                this.x = p.x;
                this.y = p.y;
            }
            for (int i = 0; i < 2; i++) {
                if (logic.getScreen() instanceof ScreenIngame) {
                    final ParticleSwordTauntAuraParticle b = new ParticleSwordTauntAuraParticle(this.x, this.y, this.facing);
                    logic.getScreen().addParticle(b);
                }
            }
            lastParticleTime = logic.getTime();
        }
    }

    @Override
    public boolean isExpired() {
        return super.isExpired() || this.owner.isDead();
    }
}
