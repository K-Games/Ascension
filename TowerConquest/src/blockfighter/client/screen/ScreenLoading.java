package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.maps.GameMap;
import blockfighter.client.maps.GameMapLvl1;
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

    public ScreenLoading(LogicModule l) {
        super(l);
    }

    public void load(byte mapID) {
        Particle.loadParticles();
        switch (mapID) {
            case 0:
                map = new GameMapLvl1();
                break;
        }
    }

    public GameMap getLoadedMap() {
        return map;
    }

    @Override
    public void draw(Graphics2D g) {
        BufferedImage bg = Globals.MENU_BG[0];
        g.drawImage(bg, 0, 0, null);

        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, "Loading...", 520, 640, 2);
        g.setColor(Color.WHITE);
        g.drawString("Loading...", 520, 640);

        super.draw(g);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void unload() {
        //Particle.unloadParticles();
    }

}
