package blockfighter.client.maps;

import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class GameMapArena extends GameMap {

    BufferedImage[] platforms = new BufferedImage[3];
    byte bgm;

    public GameMapArena() {
        super.setMapID(0);
        this.mapHeight = 1600;
        this.mapWidth = 3400;
        this.mapYOrigin = -1000;
    }

    @Override
    public void draw(final Graphics2D g) {
        for (int i = 0; i < Math.ceil(1D * this.mapWidth / this.platforms[2].getWidth()); i++) {
            if ((i + 1) * this.platforms[2].getWidth() <= this.mapWidth) {
                g.drawImage(this.platforms[2], i * this.platforms[2].getWidth(), 600, null);
            } else {
                g.drawImage(this.platforms[2], i * this.platforms[2].getWidth(), 600, this.platforms[2].getWidth() + (this.mapWidth - ((i + 1) * this.platforms[2].getWidth())), 30, null);
            }
        }

        g.drawImage(this.platforms[0], 650, 350, null);
        g.drawImage(this.platforms[1], 1550, 350, null);
        g.drawImage(this.platforms[1], 2450, 350, null);

        g.drawImage(this.platforms[0], 200, 100, null);
        g.drawImage(this.platforms[0], 1100, 100, null);
        g.drawImage(this.platforms[1], 2000, 100, null);
        g.drawImage(this.platforms[0], 2900, 100, null);

        g.drawImage(this.platforms[0], 650, -150, null);
        g.drawImage(this.platforms[0], 1550, -150, null);
        g.drawImage(this.platforms[1], 2450, -150, null);

        g.drawImage(this.platforms[0], 1100, -400, null);
        g.drawImage(this.platforms[0], 2000, -400, null);
        super.draw(g);

    }

    @Override
    public void loadAssets() throws Exception {
        Globals.log(GameMapArena.class, "Loading Map Arena Assets...", Globals.LOG_TYPE_DATA);
        this.bg = Globals.loadTextureResource("sprites/maps/" + getMapID() + "/bg.png");
        if (this.bg == null) {
            throw new NullPointerException("Failed to load map " + getMapID() + " bg.");
        }
        for (int i = 0; i < this.platforms.length; i++) {
            this.platforms[i] = Globals.loadTextureResource("sprites/maps/" + getMapID() + "/plat" + i + ".png");
            if (this.platforms[i] == null) {
                throw new NullPointerException("Failed to load platform texture. Map " + getMapID() + " Plat " + i + ".");
            }
        }
        int random = Globals.rng(3);
        switch (random) {
            case 0:
                this.bgm = Globals.BGM_ARENA1;
                break;
            case 1:
                this.bgm = Globals.BGM_ARENA2;
                break;
            case 2:
                this.bgm = Globals.BGM_ARENA3;
                break;
        }
    }

    @Override
    public byte getBGM() {
        return bgm;
    }

    @Override
    public void prerender(Graphics2D g) {
        g.drawImage(this.bg, 0, 0, null);
        for (BufferedImage sprite : this.platforms) {
            g.drawImage(sprite, 650, 350, null);
        }
    }
}
