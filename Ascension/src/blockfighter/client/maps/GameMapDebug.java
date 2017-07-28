package blockfighter.client.maps;

import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class GameMapDebug extends GameMap {

    private static final Globals.BGMs[] BGMS = {
        Globals.BGMs.BLOOD_STEEL
    };

    Globals.BGMs bgm;

    public GameMapDebug() {
        super(Globals.GameMaps.DEBUG);
        this.mapHeight = 1500;
        this.mapWidth = 1000;
        this.mapYOrigin = -500;
    }

    @Override
    public void draw(final Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawRect(-50, 800, 1100, 15);
        super.draw(g);
    }

    @Override
    public void loadAssets() throws Exception {
        Globals.log(GameMapDebug.class, "Loading Map " + this.gameMap.getMapName() + " Assets...", Globals.LOG_TYPE_DATA);
        this.bgm = BGMS[(byte) Globals.rng(BGMS.length)];
        this.bg = new BufferedImage[0];
    }

    @Override
    public byte getBgmCode() {
        return bgm.getBgmCode();
    }

    @Override
    public void prerender(Graphics2D g) {
    }

    @Override
    public void unloadAssets() {
    }

}
