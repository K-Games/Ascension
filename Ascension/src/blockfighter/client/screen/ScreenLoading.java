package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.maps.GameMap;
import blockfighter.client.maps.GameMapArena;
import blockfighter.client.maps.GameMapFloor1;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ken Kwan
 */
public class ScreenLoading extends ScreenMenu {

    private GameMap map;
    private boolean effectsReadyToRender = false, rendered = false;

    public void load(final byte mapID) throws Exception {
        Particle.loadParticles();
        switch (mapID) {
            case 0:
                this.map = new GameMapArena();
                break;
            case 1:
                this.map = new GameMapFloor1();
                break;
        }
        this.map.loadAssets();
        this.effectsReadyToRender = true;
    }

    public GameMap getLoadedMap() {
        return this.map;
    }

    @Override
    public void draw(final Graphics2D g) {
        final BufferedImage bg = Globals.MENU_BG[0];
        if (this.effectsReadyToRender && !this.rendered) {
            System.out.println("Prerendering Particles");
            for (int i = 0; i < Globals.NUM_PARTICLE_EFFECTS; i++) {
                if (Particle.getParticleSprites()[i] != null) {
                    for (final BufferedImage sprite : Particle.getParticleSprites()[i]) {
                        g.drawImage(sprite, 0, 0, null);
                    }
                }
            }
            this.map.prerender(g);
            this.rendered = true;
            synchronized (this) {
                notify();
            }
        }
        g.drawImage(bg, 0, 0, null);

        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, "Loading...", 520, 640, 2);
        g.setColor(Color.WHITE);
        g.drawString("Loading...", 520, 640);

        super.draw(g);
    }

    @Override
    public void keyTyped(final KeyEvent e) {

    }

    @Override
    public void keyPressed(final KeyEvent e) {

    }

    @Override
    public void keyReleased(final KeyEvent e) {

    }

    @Override
    public void mouseClicked(final MouseEvent e) {

    }

    @Override
    public void mousePressed(final MouseEvent e) {

    }

    @Override
    public void mouseReleased(final MouseEvent e) {
    }

    @Override
    public void mouseEntered(final MouseEvent e) {

    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @Override
    public void mouseDragged(final MouseEvent e) {

    }

    @Override
    public void mouseMoved(final MouseEvent e) {

    }

    @Override
    public void unload() {
    }
}
