package blockfighter.client.entities.emotes;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.Main;
import blockfighter.client.entities.player.Player;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class Emote extends Thread {

    protected int x, y;
    protected double size = 10.0;
    protected int frameDuration;
    protected long lastFrameTime = 0,
            emoteStartTime = 0;
    protected int frame = 0;

    protected final Player owner;

    protected static BufferedImage[][] EMOTE_SPRITE;
    private final static String[] EMOTE_SPRITE_FOLDER = new String[Globals.NUM_EMOTES];
    private final static int[] EMOTE_FRAMES = new int[Globals.NUM_EMOTES];

    private static boolean LOADED = false;

    protected static LogicModule logic;
    protected int duration;

    public static void init() {
        logic = Main.getLogicModule();

        EMOTE_SPRITE_FOLDER[Globals.EMOTE_ALERT] = "alert";
        EMOTE_FRAMES[Globals.EMOTE_ALERT] = 1;

    }

    public static void unloadEmotes() {
        System.out.println("Unloading Emotes...");
        for (int i = 0; EMOTE_SPRITE != null && i < EMOTE_SPRITE.length; i++) {
            for (int j = 0; EMOTE_SPRITE[i] != null && j < EMOTE_SPRITE[i].length; j++) {
                EMOTE_SPRITE[i][j] = null;
            }
            EMOTE_SPRITE[i] = null;
        }
        EMOTE_SPRITE = null;
        LOADED = false;
        System.gc();
    }

    public static void loadEmotes() throws Exception {
        if (LOADED) {
            return;
        }
        System.out.println("Loading Emotes...");
        LOADED = true;
        EMOTE_SPRITE = new BufferedImage[Globals.NUM_EMOTES][];
        for (int emoteID = 0; emoteID < EMOTE_SPRITE.length; emoteID++) {
            if (EMOTE_SPRITE_FOLDER[emoteID] != null && EMOTE_FRAMES[emoteID] > 0) {
                EMOTE_SPRITE[emoteID] = new BufferedImage[EMOTE_FRAMES[emoteID]];
                for (int frame = 0; frame < EMOTE_SPRITE[emoteID].length; frame++) {
                    EMOTE_SPRITE[emoteID][frame] = Globals.loadTextureResource("sprites/emote/" + EMOTE_SPRITE_FOLDER[emoteID] + "/" + frame + ".png");
                }
            }
        }
    }

    public void update() {
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    @Override
    public void run() {
        update();
    }

    public boolean isExpired() {
        return Globals.nsToMs(logic.getTime() - this.emoteStartTime) >= this.duration;
    }

    public Emote(final Player owner) {
        if (logic != null) {
            this.emoteStartTime = logic.getTime();
            this.lastFrameTime = this.emoteStartTime;
        } else {
            this.emoteStartTime = System.nanoTime();
            this.lastFrameTime = this.emoteStartTime;
        }
        this.duration = 1000;
        this.owner = owner;
        setDaemon(true);
    }

    public void draw(final Graphics2D g) {
    }

    public void setExpire() {
        this.duration = 0;
    }

    public static BufferedImage[][] getParticleSprites() {
        return EMOTE_SPRITE;
    }
}
