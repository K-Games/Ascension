package blockfighter.client.maps;

import blockfighter.client.Core;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class GameMapAsymArena extends GameMap {

    private static final String MAP_NAME = "Grand Library";

    private static final int[] RANDOM_THREE_SEQUENCE = new int[15];
    private static int sequenceCounter = 0;

    private static final Globals.BGMs[] BGMS = {
        Globals.BGMs.GRACE_RESIS,
        Globals.BGMs.INEV_BLOOD_ROCK,
        Globals.BGMs.REDEMP_ORCH,
        Globals.BGMs.BLOOD_STEEL,
        Globals.BGMs.INEV_BLOOD_DYN
    };

    private long lastCandleUpdateTime = 0;
    BufferedImage[] platform_fills = new BufferedImage[3];
    BufferedImage[] platform_top = new BufferedImage[3];
    BufferedImage[] platform_bottom = new BufferedImage[3];
    BufferedImage[] platform_left = new BufferedImage[3];
    BufferedImage[] platform_right = new BufferedImage[3];
    BufferedImage[] platform_corners = new BufferedImage[4];
    BufferedImage[] platform_corners_inner = new BufferedImage[4];

    BufferedImage[] candles = new BufferedImage[4];

    BufferedImage[] shelves = new BufferedImage[3];
    BufferedImage fireplace;
    BufferedImage pillar;
    BufferedImage chest;

    int[] candleFrame = new int[2 + 10];
    Globals.BGMs bgm;

    static {
        for (int i = 0; i < RANDOM_THREE_SEQUENCE.length; i++) {
            RANDOM_THREE_SEQUENCE[i] = Globals.rng(3);
        }
    }

    public GameMapAsymArena() {
        this.mapHeight = 3000;
        this.mapWidth = 3800;
        this.mapYOrigin = -500;

        for (int i = 0; i < this.candleFrame.length; i++) {
            this.candleFrame[i] = Globals.rng(candles.length);
        }
    }

    @Override
    public String getMapName() {
        return MAP_NAME;
    }

    private void drawRandomTile(final Graphics2D g, final BufferedImage[] spriteCollection, final int x, final int y, final int[] numberSequence) {
        if (sequenceCounter >= numberSequence.length) {
            sequenceCounter = 0;
        }
        BufferedImage sprite = spriteCollection[numberSequence[sequenceCounter]];
        g.drawImage(sprite, x, y, null);
        if (++sequenceCounter >= numberSequence.length) {
            sequenceCounter = 0;
        }
    }

    private void drawBase(final Graphics2D g) {
        for (int x = -50; x < 4064; x += 64) {
            for (int y = 2400; y < 2610; y += 64) {
                drawRandomTile(g, this.platform_fills, x, y, RANDOM_THREE_SEQUENCE);
            }
        }
        for (int x = -50 + 768; x < 1600; x += 16) {
            drawRandomTile(g, this.platform_top, x, 2400, RANDOM_THREE_SEQUENCE);
        }
        for (int x = 1600 + 448; x < 3200; x += 16) {
            drawRandomTile(g, this.platform_top, x, 2400, RANDOM_THREE_SEQUENCE);
        }
    }

    private void drawLadder(final Graphics2D g) {
        //base
        for (int x = -50; x < -50 + 768; x += 64) {
            for (int y = 1504; y <= 1504 + 896; y += 64) {
                drawRandomTile(g, this.platform_fills, x, y, RANDOM_THREE_SEQUENCE);
            }
        }
        for (int x = -50; x < -50 + 768; x += 16) {
            drawRandomTile(g, this.platform_top, x, 1504, RANDOM_THREE_SEQUENCE);
        }
        for (int y = 1520; y <= 1504 + 880; y += 16) {
            drawRandomTile(g, this.platform_right, -50 + 768 - 16, y, RANDOM_THREE_SEQUENCE);
        }
        g.drawImage(this.platform_corners_inner[0], -50 + 768 - 16, 1504 + 896, null);
        g.drawImage(this.platform_corners[3], -50 + 768 - 16, 1504, null);

        // step 1
        for (int x = -42; x < -42 + 192; x += 64) {
            drawRandomTile(g, this.platform_fills, x, 1250, RANDOM_THREE_SEQUENCE);
        }
        for (int x = -42 + 16; x < -42 + 192 - 16; x += 16) {
            drawRandomTile(g, this.platform_top, x, 1250, RANDOM_THREE_SEQUENCE);
        }
        for (int x = -42 + 16; x < -42 + 192 - 16; x += 16) {
            drawRandomTile(g, this.platform_bottom, x, 1250 + 64 - 16, RANDOM_THREE_SEQUENCE);
        }
        for (int y = 1250 + 16; y <= 1250 + 32; y += 16) {
            drawRandomTile(g, this.platform_left, -42, y, RANDOM_THREE_SEQUENCE);
            drawRandomTile(g, this.platform_right, -42 + 192 - 16, y, RANDOM_THREE_SEQUENCE);
        }
        g.drawImage(this.platform_corners[0], -42, 1250 + 64 - 16, null);
        g.drawImage(this.platform_corners[1], -42 + 192 - 16, 1250 + 64 - 16, null);
        g.drawImage(this.platform_corners[2], -42, 1250, null);
        g.drawImage(this.platform_corners[3], -42 + 192 - 16, 1250, null);

        // step 2
        for (int x = -42; x < -42 + 192; x += 64) {
            drawRandomTile(g, this.platform_fills, x, 1000, RANDOM_THREE_SEQUENCE);
        }
        for (int x = -42 + 16; x < -42 + 192 - 16; x += 16) {
            drawRandomTile(g, this.platform_top, x, 1000, RANDOM_THREE_SEQUENCE);
        }
        for (int x = -42 + 16; x < -42 + 192 - 16; x += 16) {
            drawRandomTile(g, this.platform_bottom, x, 1000 + 64 - 16, RANDOM_THREE_SEQUENCE);
        }
        for (int y = 1000 + 16; y <= 1000 + 32; y += 16) {
            drawRandomTile(g, this.platform_left, -42, y, RANDOM_THREE_SEQUENCE);
            drawRandomTile(g, this.platform_right, -42 + 192 - 16, y, RANDOM_THREE_SEQUENCE);
        }
        g.drawImage(this.platform_corners[0], -42, 1000 + 64 - 16, null);
        g.drawImage(this.platform_corners[1], -42 + 192 - 16, 1000 + 64 - 16, null);
        g.drawImage(this.platform_corners[2], -42, 1000, null);
        g.drawImage(this.platform_corners[3], -42 + 192 - 16, 1000, null);
    }

    private void drawFireplace(final Graphics2D g) {
        for (int x = 900; x < 900 + 512; x += 64) {
            drawRandomTile(g, this.platform_fills, x, 1700, RANDOM_THREE_SEQUENCE);
        }
        for (int x = 900 + 16; x < 900 + 512 - 16; x += 16) {
            drawRandomTile(g, this.platform_top, x, 1700, RANDOM_THREE_SEQUENCE);
        }
        for (int x = 900 + 16; x < 900 + 512 - 16; x += 16) {
            drawRandomTile(g, this.platform_bottom, x, 1700 + 64 - 16, RANDOM_THREE_SEQUENCE);
        }

        for (int y = 1700 + 16; y <= 1700 + 32; y += 16) {
            drawRandomTile(g, this.platform_left, 900, y, RANDOM_THREE_SEQUENCE);
            drawRandomTile(g, this.platform_right, 900 + 512 - 16, y, RANDOM_THREE_SEQUENCE);
        }
        g.drawImage(this.platform_corners[0], 900, 1700 + 64 - 16, null);
        g.drawImage(this.platform_corners[1], 900 + 512 - 16, 1700 + 64 - 16, null);
        g.drawImage(this.platform_corners[2], 900, 1700, null);
        g.drawImage(this.platform_corners[3], 900 + 512 - 16, 1700, null);
        g.drawImage(this.fireplace, 1000, 2400 - this.fireplace.getHeight(), null);

        g.drawImage(this.candles[this.candleFrame[0]], 1010, 2140, null);
        g.drawImage(this.candles[this.candleFrame[1]], 1180, 2140, null);
    }

    public void drawMidBase(final Graphics2D g) {
        for (int x = 1600; x < 1600 + 448; x += 64) {
            for (int y = 1888; y <= 1888 + 512; y += 64) {
                drawRandomTile(g, this.platform_fills, x, y, RANDOM_THREE_SEQUENCE);
            }
        }
        for (int x = 1600 + 16; x < 1600 + 448 - 16; x += 16) {
            drawRandomTile(g, this.platform_top, x, 1888, RANDOM_THREE_SEQUENCE);
        }
        for (int y = 1888 + 16; y <= 2176 - 16; y += 16) {
            drawRandomTile(g, this.platform_left, 1600, y, RANDOM_THREE_SEQUENCE);
        }

        for (int y = 2176 + 64; y <= 2400 - 16; y += 16) {
            drawRandomTile(g, this.platform_left, 1600, y, RANDOM_THREE_SEQUENCE);
        }
        for (int y = 1888 + 16; y <= 1888 + 512 - 16; y += 16) {
            drawRandomTile(g, this.platform_right, 1600 + 448 - 16, y, RANDOM_THREE_SEQUENCE);
        }

        g.drawImage(this.platform_corners[2], 1600, 1888, null);
        g.drawImage(this.platform_corners[3], 1600 + 448 - 16, 1888, null);
        g.drawImage(this.platform_corners_inner[1], 1600, 2400, null);
        g.drawImage(this.platform_corners_inner[0], 1600 + 448 - 16, 2400, null);

        drawRandomTile(g, this.platform_fills, 1536, 2176, RANDOM_THREE_SEQUENCE);
        for (int x = 1536 + 16; x < 1536 + 64; x += 16) {
            drawRandomTile(g, this.platform_top, x, 2176, RANDOM_THREE_SEQUENCE);
        }
        for (int x = 1536 + 16; x < 1536 + 64; x += 16) {
            drawRandomTile(g, this.platform_bottom, x, 2176 + 64 - 16, RANDOM_THREE_SEQUENCE);
        }
        for (int y = 2176 + 16; y <= 2176 + 48 - 16; y += 16) {
            drawRandomTile(g, this.platform_left, 1536, y, RANDOM_THREE_SEQUENCE);
        }

        g.drawImage(this.platform_corners[2], 1536, 2176, null);
        g.drawImage(this.platform_corners[0], 1536, 2176 + 64 - 16, null);
        g.drawImage(this.platform_corners_inner[1], 1600, 2176, null);
        g.drawImage(this.platform_corners_inner[3], 1600, 2176 + 64 - 16, null);
    }

    public void drawCandles(final Graphics2D g) {
        for (int x = 2250; x < 2250 + 704; x += 64) {
            drawRandomTile(g, this.platform_fills, x, 2100, RANDOM_THREE_SEQUENCE);
        }
        for (int x = 2250 + 16; x < 2250 + 704 - 16; x += 16) {
            drawRandomTile(g, this.platform_top, x, 2100, RANDOM_THREE_SEQUENCE);
        }
        for (int x = 2250 + 16; x < 2250 + 704 - 16; x += 16) {
            drawRandomTile(g, this.platform_bottom, x, 2100 + 64 - 16, RANDOM_THREE_SEQUENCE);
        }

        for (int y = 2100 + 16; y <= 2100 + 32; y += 16) {
            drawRandomTile(g, this.platform_left, 2250, y, RANDOM_THREE_SEQUENCE);
            drawRandomTile(g, this.platform_right, 2250 + 704 - 16, y, RANDOM_THREE_SEQUENCE);
        }
        g.drawImage(this.platform_corners[0], 2250, 2100 + 64 - 16, null);
        g.drawImage(this.platform_corners[1], 2250 + 704 - 16, 2100 + 64 - 16, null);
        g.drawImage(this.platform_corners[2], 2250, 2100, null);
        g.drawImage(this.platform_corners[3], 2250 + 704 - 16, 2100, null);

        for (int i = 0; i < 5; i++) {
            g.drawImage(this.candles[this.candleFrame[2 + i]], 2120 + 230 * i, 1950, null);
        }
        for (int i = 0; i < 5; i++) {
            g.drawImage(this.candles[this.candleFrame[7 + i]], 2120 + 230 * i, 2250, null);
        }
    }

    public void drawShelves(final Graphics2D g) {
        //g.fill(new Rectangle2D.Double(3200, 1120, 640, 1280));
        for (int x = 3200; x < 3200 + 640; x += 64) {
            for (int y = 1120; y <= 1120 + 1280; y += 64) {
                drawRandomTile(g, this.platform_fills, x, y, RANDOM_THREE_SEQUENCE);
            }
        }
        for (int x = 3200; x < 3200 + 640; x += 16) {
            drawRandomTile(g, this.platform_top, x, 1120, RANDOM_THREE_SEQUENCE);
        }
        for (int y = 1120 + 16; y <= 2400 - 16; y += 16) {
            drawRandomTile(g, this.platform_left, 3200, y, RANDOM_THREE_SEQUENCE);
        }

        g.drawImage(this.platform_corners[2], 3200, 1120, null);
        g.drawImage(this.platform_corners_inner[1], 3200, 2400, null);

        //g.fill(new Rectangle2D.Double(2816, 1820, 384, 30));
        for (int x = 2816; x < 2816 + 384; x += 64) {
            drawRandomTile(g, this.platform_fills, x, 1820, RANDOM_THREE_SEQUENCE);
        }
        for (int x = 2816 + 16; x < 2816 + 384; x += 16) {
            drawRandomTile(g, this.platform_top, x, 1820, RANDOM_THREE_SEQUENCE);
        }
        for (int x = 2816 + 16; x < 2816 + 384; x += 16) {
            drawRandomTile(g, this.platform_bottom, x, 1820 + 64 - 16, RANDOM_THREE_SEQUENCE);
        }
        for (int y = 1820 + 16; y <= 1820 + 48 - 16; y += 16) {
            drawRandomTile(g, this.platform_left, 2816, y, RANDOM_THREE_SEQUENCE);
        }
        g.drawImage(this.platform_corners[0], 2816, 1820 + 64 - 16, null);
        g.drawImage(this.platform_corners[2], 2816, 1820, null);

        // g.fill(new Rectangle2D.Double(2624, 1270, 576, 30));
        for (int x = 2624; x < 2624 + 576; x += 64) {
            drawRandomTile(g, this.platform_fills, x, 1270, RANDOM_THREE_SEQUENCE);
        }
        for (int x = 2624 + 16; x < 2624 + 576; x += 16) {
            drawRandomTile(g, this.platform_top, x, 1270, RANDOM_THREE_SEQUENCE);
        }
        for (int x = 2624 + 16; x < 2624 + 576; x += 16) {
            drawRandomTile(g, this.platform_bottom, x, 1270 + 64 - 16, RANDOM_THREE_SEQUENCE);
        }
        for (int y = 1270 + 16; y <= 1270 + 48 - 16; y += 16) {
            drawRandomTile(g, this.platform_left, 2624, y, RANDOM_THREE_SEQUENCE);
        }
        g.drawImage(this.platform_corners[0], 2624, 1270 + 64 - 16, null);
        g.drawImage(this.platform_corners[2], 2624, 1270, null);

        int shelfSpacing = 0;
        for (BufferedImage shelf : this.shelves) {
            g.drawImage(shelf, 3250 + shelfSpacing, 1120 - shelf.getHeight(), null);
            shelfSpacing += shelf.getWidth();
        }
    }

    public void drawMid(final Graphics2D g) {
        for (int x = 1604; x < 1604 + 1216; x += 64) {
            drawRandomTile(g, this.platform_fills, x, 1550, RANDOM_THREE_SEQUENCE);
        }
        for (int x = 1604 + 16; x < 1604 + 1216; x += 16) {
            drawRandomTile(g, this.platform_top, x, 1550, RANDOM_THREE_SEQUENCE);
        }
        for (int x = 1604 + 16; x < 1604 + 1216; x += 16) {
            drawRandomTile(g, this.platform_bottom, x, 1550 + 64 - 16, RANDOM_THREE_SEQUENCE);
        }
        for (int y = 1550 + 16; y <= 1550 + 32; y += 16) {
            drawRandomTile(g, this.platform_left, 1604, y, RANDOM_THREE_SEQUENCE);
            drawRandomTile(g, this.platform_right, 1604 + 1216 - 16, y, RANDOM_THREE_SEQUENCE);
        }
        g.drawImage(this.platform_corners[0], 1604, 1550 + 64 - 16, null);
        g.drawImage(this.platform_corners[1], 1604 + 1216 - 16, 1550 + 64 - 16, null);
        g.drawImage(this.platform_corners[2], 1604, 1550, null);
        g.drawImage(this.platform_corners[3], 1604 + 1216 - 16, 1550, null);

        // g.fill(new Rectangle2D.Double(1526, 1000, 1024, 30));
        for (int x = 1526; x < 1526 + 1024; x += 64) {
            drawRandomTile(g, this.platform_fills, x, 1000, RANDOM_THREE_SEQUENCE);
        }
        for (int x = 1526 + 16; x < 1526 + 1024 - 16; x += 16) {
            drawRandomTile(g, this.platform_top, x, 1000, RANDOM_THREE_SEQUENCE);
        }
        for (int x = 1526 + 16; x < 1526 + 1024 - 16; x += 16) {
            drawRandomTile(g, this.platform_bottom, x, 1000 + 64 - 16, RANDOM_THREE_SEQUENCE);
        }
        for (int y = 1000 + 16; y <= 1000 + 32; y += 16) {
            drawRandomTile(g, this.platform_left, 1526, y, RANDOM_THREE_SEQUENCE);
            drawRandomTile(g, this.platform_right, 1526 + 1024 - 16, y, RANDOM_THREE_SEQUENCE);
        }
        g.drawImage(this.platform_corners[0], 1526, 1000 + 64 - 16, null);
        g.drawImage(this.platform_corners[1], 1526 + 1024 - 16, 1000 + 64 - 16, null);
        g.drawImage(this.platform_corners[2], 1526, 1000, null);
        g.drawImage(this.platform_corners[3], 1526 + 1024 - 16, 1000, null);

    }

    public void drawPillars(final Graphics2D g) {
        for (int x = -36; x < -36 + 1536; x += 64) {
            drawRandomTile(g, this.platform_fills, x, 750, RANDOM_THREE_SEQUENCE);
        }
        for (int x = -36 + 16; x < -36 + 1520; x += 16) {
            drawRandomTile(g, this.platform_top, x, 750, RANDOM_THREE_SEQUENCE);
        }
        for (int x = -36 + 16; x < -36 + 1520; x += 16) {
            drawRandomTile(g, this.platform_bottom, x, 750 + 64 - 16, RANDOM_THREE_SEQUENCE);
        }
        for (int y = 750 + 16; y <= 750 + 32; y += 16) {
            drawRandomTile(g, this.platform_left, -36, y, RANDOM_THREE_SEQUENCE);
            drawRandomTile(g, this.platform_right, -36 + 1536 - 16, y, RANDOM_THREE_SEQUENCE);
        }
        g.drawImage(this.platform_corners[0], -36, 750 + 64 - 16, null);
        g.drawImage(this.platform_corners[1], -36 + 1520, 750 + 64 - 16, null);
        g.drawImage(this.platform_corners[2], -36, 750, null);
        g.drawImage(this.platform_corners[3], -36 + 1520, 750, null);

        for (int i = 0; i < 8; i++) {
            g.drawImage(this.pillar, 30 + 190 * i, 750 - this.pillar.getHeight(), null);
        }
    }

    public void drawTop(final Graphics2D g) {

        //g.fill(new Rectangle2D.Double(1574, 500, 576, 30));
        for (int x = 1574; x < 1574 + 576; x += 64) {
            drawRandomTile(g, this.platform_fills, x, 500, RANDOM_THREE_SEQUENCE);
        }
        for (int x = 1574 + 16; x < 1574 + 560; x += 16) {
            drawRandomTile(g, this.platform_top, x, 500, RANDOM_THREE_SEQUENCE);
        }
        for (int x = 1574 + 16; x < 1574 + 560; x += 16) {
            drawRandomTile(g, this.platform_bottom, x, 500 + 64 - 16, RANDOM_THREE_SEQUENCE);
        }
        for (int y = 500 + 16; y <= 500 + 32; y += 16) {
            drawRandomTile(g, this.platform_left, 1574, y, RANDOM_THREE_SEQUENCE);
            drawRandomTile(g, this.platform_right, 1574 + 576 - 16, y, RANDOM_THREE_SEQUENCE);
        }
        g.drawImage(this.platform_corners[0], 1574, 500 + 64 - 16, null);
        g.drawImage(this.platform_corners[1], 1574 + 560, 500 + 64 - 16, null);
        g.drawImage(this.platform_corners[2], 1574, 500, null);
        g.drawImage(this.platform_corners[3], 1574 + 560, 500, null);

        for (int x = 2314; x < 2314 + 1536; x += 64) {
            drawRandomTile(g, this.platform_fills, x, 250, RANDOM_THREE_SEQUENCE);
        }
        for (int x = 2314 + 16; x < 2314 + 1520; x += 16) {
            drawRandomTile(g, this.platform_top, x, 250, RANDOM_THREE_SEQUENCE);
        }
        for (int x = 2314 + 16; x < 2314 + 1520; x += 16) {
            drawRandomTile(g, this.platform_bottom, x, 250 + 64 - 16, RANDOM_THREE_SEQUENCE);
        }
        for (int y = 250 + 16; y <= 250 + 64 - 32; y += 16) {
            drawRandomTile(g, this.platform_left, 2314, y, RANDOM_THREE_SEQUENCE);
            drawRandomTile(g, this.platform_right, 2314 + 1520, y, RANDOM_THREE_SEQUENCE);
        }
        g.drawImage(this.platform_corners[0], 2314, 250 + 64 - 16, null);
        g.drawImage(this.platform_corners[1], 2314 + 1520, 250 + 64 - 16, null);
        g.drawImage(this.platform_corners[2], 2314, 250, null);
        g.drawImage(this.platform_corners[3], 2314 + 1520, 250, null);

        for (int i = 0; i < 8; i++) {
            for (int j = i; j > 0; j--) {
                g.drawImage(this.chest, 2300 + i * (100 + this.chest.getWidth()), 250 - (j * this.chest.getHeight()), null);
            }
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        g.setColor(Color.BLACK);
        sequenceCounter = 0;

        drawBase(g);
        drawLadder(g);
        drawFireplace(g);
        drawMidBase(g);
        drawCandles(g);
        drawShelves(g);
        drawMid(g);
        drawPillars(g);
        drawTop(g);
        super.draw(g);
    }

    @Override
    public void loadAssets() throws Exception {
        Globals.log(GameMapAsymArena.class, "Loading Map " + getMapName() + " Assets...", Globals.LOG_TYPE_DATA);
        this.bg = new BufferedImage[3];
        for (int i = 0; i < this.bg.length; i++) {
            String path = "sprites/maps/" + Globals.GameMaps.ASYM_ARENA.getMapCode() + "/bg_layer_" + i + ".png";
            this.bg[i] = Globals.loadTextureResource(path);
            if (this.bg[i] == null) {
                throw new NullPointerException("Failed to load map " + Globals.GameMaps.ARENA.getMapCode() + " resource:" + path);
            }
        }

        for (int i = 0; i < this.platform_fills.length; i++) {
            String path = "sprites/maps/" + Globals.GameMaps.ASYM_ARENA.getMapCode() + "/platform_fill_" + i + ".png";
            this.platform_fills[i] = Globals.loadTextureResource(path);
            if (this.platform_fills[i] == null) {
                throw new NullPointerException("Failed to load map " + Globals.GameMaps.ARENA.getMapCode() + " resource:" + path);
            }
        }
        for (int i = 0; i < this.platform_bottom.length; i++) {
            String path = "sprites/maps/" + Globals.GameMaps.ASYM_ARENA.getMapCode() + "/platform_bottom_" + i + ".png";
            this.platform_bottom[i] = Globals.loadTextureResource(path);
            if (this.platform_bottom[i] == null) {
                throw new NullPointerException("Failed to load map " + Globals.GameMaps.ARENA.getMapCode() + " resource:" + path);
            }
        }

        for (int i = 0; i < this.platform_top.length; i++) {
            String path = "sprites/maps/" + Globals.GameMaps.ASYM_ARENA.getMapCode() + "/platform_top_" + i + ".png";
            this.platform_top[i] = Globals.loadTextureResource(path);
            if (this.platform_top[i] == null) {
                throw new NullPointerException("Failed to load map " + Globals.GameMaps.ARENA.getMapCode() + " resource:" + path);
            }
        }

        for (int i = 0; i < this.platform_left.length; i++) {
            String path = "sprites/maps/" + Globals.GameMaps.ASYM_ARENA.getMapCode() + "/platform_left_" + i + ".png";
            this.platform_left[i] = Globals.loadTextureResource(path);
            if (this.platform_left[i] == null) {
                throw new NullPointerException("Failed to load map " + Globals.GameMaps.ARENA.getMapCode() + " resource:" + path);
            }
        }

        for (int i = 0; i < this.platform_right.length; i++) {
            String path = "sprites/maps/" + Globals.GameMaps.ASYM_ARENA.getMapCode() + "/platform_right_" + i + ".png";
            this.platform_right[i] = Globals.loadTextureResource(path);
            if (this.platform_right[i] == null) {
                throw new NullPointerException("Failed to load map " + Globals.GameMaps.ARENA.getMapCode() + " resource:" + path);
            }
        }
        for (int i = 0; i < this.platform_corners.length; i++) {
            String path = "sprites/maps/" + Globals.GameMaps.ASYM_ARENA.getMapCode() + "/platform_corner_" + i + ".png";
            this.platform_corners[i] = Globals.loadTextureResource(path);
            if (this.platform_corners[i] == null) {
                throw new NullPointerException("Failed to load map " + Globals.GameMaps.ARENA.getMapCode() + " resource:" + path);
            }
        }
        for (int i = 0; i < this.platform_corners_inner.length; i++) {
            String path = "sprites/maps/" + Globals.GameMaps.ASYM_ARENA.getMapCode() + "/platform_corner_inner_" + i + ".png";
            this.platform_corners_inner[i] = Globals.loadTextureResource(path);
            if (this.platform_corners_inner[i] == null) {
                throw new NullPointerException("Failed to load map " + Globals.GameMaps.ARENA.getMapCode() + " resource:" + path);
            }
        }

        String fireplacePath = "sprites/maps/" + Globals.GameMaps.ASYM_ARENA.getMapCode() + "/fireplace.png";
        this.fireplace = Globals.loadTextureResource(fireplacePath);
        if (this.fireplace == null) {
            throw new NullPointerException("Failed to load map " + Globals.GameMaps.ARENA.getMapCode() + " resource:" + fireplacePath);
        }

        for (int i = 0; i < this.candles.length; i++) {
            String path = "sprites/maps/" + Globals.GameMaps.ASYM_ARENA.getMapCode() + "/candle_" + i + ".png";
            this.candles[i] = Globals.loadTextureResource(path);
            if (this.candles[i] == null) {
                throw new NullPointerException("Failed to load map " + Globals.GameMaps.ARENA.getMapCode() + " resource:" + path);
            }
        }

        String pillarPath = "sprites/maps/" + Globals.GameMaps.ASYM_ARENA.getMapCode() + "/pillar.png";
        this.pillar = Globals.loadTextureResource(pillarPath);
        if (this.pillar == null) {
            throw new NullPointerException("Failed to load map " + Globals.GameMaps.ARENA.getMapCode() + " resource:" + pillarPath);
        }

        for (int i = 0; i < this.shelves.length; i++) {
            String path = "sprites/maps/" + Globals.GameMaps.ASYM_ARENA.getMapCode() + "/bookshelf_" + i + ".png";
            this.shelves[i] = Globals.loadTextureResource(path);
            if (this.shelves[i] == null) {
                throw new NullPointerException("Failed to load map " + Globals.GameMaps.ARENA.getMapCode() + " resource:" + path);
            }
        }

        String chestPath = "sprites/maps/" + Globals.GameMaps.ASYM_ARENA.getMapCode() + "/chest_1.png";
        this.chest = Globals.loadTextureResource(chestPath);
        if (this.chest == null) {
            throw new NullPointerException("Failed to load map " + Globals.GameMaps.ARENA.getMapCode() + " resource:" + pillarPath);
        }
        this.bgm = BGMS[(byte) Globals.rng(BGMS.length)];
    }

    @Override
    public byte getBgmCode() {
        return bgm.getBgmCode();
    }

    @Override
    public void prerender(Graphics2D g
    ) {
        g.drawImage(this.bg[0], 0, 0, null);
        g.drawImage(this.bg[1], 0, 0, null);
        g.drawImage(this.bg[2], 0, 0, null);
        for (BufferedImage sprite : this.platform_fills) {
            g.drawImage(sprite, 0, 0, null);
        }
        for (BufferedImage sprite : this.platform_bottom) {
            g.drawImage(sprite, 0, 0, null);
        }
        for (BufferedImage sprite : this.platform_top) {
            g.drawImage(sprite, 0, 0, null);
        }
        for (BufferedImage sprite : this.platform_left) {
            g.drawImage(sprite, 0, 0, null);
        }
        for (BufferedImage sprite : this.platform_right) {
            g.drawImage(sprite, 0, 0, null);
        }
    }

    @Override
    public void unloadAssets() {
    }

    @Override
    public void update() {
        super.update();
        final long now = Core.getLogicModule().getTime(); // Get time now
        if (now - this.lastCandleUpdateTime >= Globals.msToNs(100)) {

            for (int i = 0; i < this.candleFrame.length; i++) {
                if (this.candleFrame[i] + 1 >= candles.length) {
                    this.candleFrame[i] = 0;
                } else {
                    this.candleFrame[i]++;
                }
            }
            this.lastCandleUpdateTime = now;
        }
    }
}
