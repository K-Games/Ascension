package blockfighter.client.screen;

import blockfighter.client.entities.emotes.Emote;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.maps.GameMap;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ScreenLoading extends ScreenMenu {

    private GameMap map;
    private boolean particlesReady = false, mapAssetsReady = false;
    private boolean particlesRendered = false, mapAssetsRendered = false;
    private int particleIndex;

    public void load(final byte mapCode) throws Exception {
        this.map = Globals.GameMaps.get(mapCode).newClientGameMap();

        Particle.loadParticles();
        this.particlesReady = true;
        Emote.loadEmotes();

        this.map.loadAssets();
        this.mapAssetsReady = true;
    }

    @Override
    public void update() {
        super.update();
        if (this.particlesRendered && this.mapAssetsRendered) {
            synchronized (this) {
                notify();
            }
        }
    }

    public GameMap getLoadedMap() {
        return this.map;
    }

    @Override
    public void draw(final Graphics2D g) {
        final BufferedImage bg = Globals.MENU_BG[0];
        if (this.particlesReady && !this.particlesRendered) {
            Globals.log(ScreenLoading.class, "Prerendering " + particleIndex + " Particles...", Globals.LOG_TYPE_DATA);
            if (Globals.Particles.values()[this.particleIndex] != null) {
                if (Globals.Particles.values()[this.particleIndex].getSprite() != null) {
                    for (final BufferedImage sprite : Globals.Particles.values()[this.particleIndex].getSprite()) {
                        g.drawImage(sprite, 0, 0, null);
                    }
                }
            }
            this.particleIndex++;
            if (this.particleIndex >= Globals.Particles.values().length) {
                this.particlesRendered = true;
            }
        }

        if (this.mapAssetsReady && !this.mapAssetsRendered) {
            Globals.log(ScreenLoading.class, "Prerendering Map " + this.map.getGameMap().getMapName() + " Assets...", Globals.LOG_TYPE_DATA);
            this.map.prerender(g);
            this.mapAssetsRendered = true;
        }

        g.drawImage(bg, 0, 0, null);
        String loadingString = "Loading...";

        if (this.map != null) {
            loadingString = "Loading " + this.map.getGameMap().getMapName() + "...";
        }
        g.setFont(Globals.ARIAL_18PT);
        int stringWidth = g.getFontMetrics().stringWidth(loadingString);
        drawStringOutline(g, loadingString, Globals.WINDOW_WIDTH / 2 - stringWidth / 2, 640, 2);
        g.setColor(Color.WHITE);
        g.drawString(loadingString, Globals.WINDOW_WIDTH / 2 - stringWidth / 2, 640);
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
