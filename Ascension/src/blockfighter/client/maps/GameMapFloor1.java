package blockfighter.client.maps;

import blockfighter.shared.Globals;
import blockfighter.client.entities.mob.boss.Lightning.BossLightning;
import blockfighter.client.entities.mob.boss.Lightning.ParticleAmbient;
import blockfighter.client.entities.mob.boss.Lightning.ParticleBolt;
import java.awt.Color;
import java.awt.Graphics2D;

public class GameMapFloor1 extends GameMap {

    public GameMapFloor1() {
        super.setMapID(1);
        this.mapHeight = 1600;
        this.mapWidth = 3700;
        this.mapYOrigin = -1000;
    }

    @Override
    public void draw(final Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawRect(0, 600, 3700, 30);

        g.drawRect(200, 350, 300, 30);

        g.drawRect(700, 100, 300, 30);

        g.drawRect(1200, -150, 300, 30);

        g.drawRect(1700, -150, 300, 30);

        g.drawRect(2200, -150, 300, 30);

        g.drawRect(2700, 100, 300, 30);

        g.drawRect(3200, 350, 300, 30);

        super.draw(g);

    }

    @Override
    public void loadAssets() throws Exception {
        this.bg = Globals.loadTextureResource("sprites/maps/" + getMapID() + "/bg.png");
        if (this.bg == null) {
            throw new NullPointerException("Failed to load map " + getMapID() + " bg.");
        }
        BossLightning.load();
    }

    @Override
    public void prerender(final Graphics2D g) {
        System.out.println("Prerendering Floor 1 Particles");
        ParticleAmbient.prerender(g);
        ParticleBolt.prerender(g);
        System.out.println("Prerendering Mob Assets");
        BossLightning.prerender(g);
    }

}
