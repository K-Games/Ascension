package blockfighter.client.entities.emotes;

import blockfighter.client.AscensionClient;
import blockfighter.client.LogicModule;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
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
        logic = AscensionClient.getLogicModule();

        EMOTE_SPRITE_FOLDER[Globals.EMOTE_ALERT] = "alert";
        EMOTE_FRAMES[Globals.EMOTE_ALERT] = 1;
        EMOTE_SPRITE_FOLDER[Globals.EMOTE_QUESTION] = "question";
        EMOTE_FRAMES[Globals.EMOTE_QUESTION] = 1;
        EMOTE_SPRITE_FOLDER[Globals.EMOTE_SWEAT] = "sweat";
        EMOTE_FRAMES[Globals.EMOTE_SWEAT] = 5;
        EMOTE_SPRITE_FOLDER[Globals.EMOTE_SLEEP] = "sleep";
        EMOTE_FRAMES[Globals.EMOTE_SLEEP] = 3;
        EMOTE_SPRITE_FOLDER[Globals.EMOTE_ANGRY] = "angry";
        EMOTE_FRAMES[Globals.EMOTE_ANGRY] = 1;
    }

    public static void unloadEmotes() {
        Globals.log(Emote.class, "Unloading Emotes...", Globals.LOG_TYPE_DATA, true);
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

    public static void loadEmotes() {
        if (LOADED) {
            return;
        }
        Globals.log(Emote.class, "Loading Emotes...", Globals.LOG_TYPE_DATA, true);
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

    public static BufferedImage[][] getEmoteSprites() {
        return EMOTE_SPRITE;
    }
}
