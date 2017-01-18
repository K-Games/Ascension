package blockfighter.client.entities.emotes;

import blockfighter.client.AscensionClient;
import blockfighter.client.LogicModule;
import blockfighter.client.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

public abstract class Emote implements Callable<Emote> {

    protected int x, y;
    protected double size = 10.0;
    protected int frameDuration;
    protected long lastFrameTime = 0,
            emoteStartTime = 0;
    protected int frame = 0;

    protected final Player owner;

    private static boolean LOADED = false;

    protected static LogicModule logic;
    protected int duration;

    public static void init() {
        logic = AscensionClient.getLogicModule();
    }

    public static void unloadEmotes() {
        Globals.log(Emote.class, "Unloading Emotes...", Globals.LOG_TYPE_DATA);
        for (Globals.Emotes emote : Globals.Emotes.values()) {
            for (int j = 0; emote.getSprite() != null && j < emote.getSprite().length; j++) {
                emote.getSprite()[j] = null;
            }
            emote.setSprite(null);;
        }
        LOADED = false;
        System.gc();
    }

    public static void loadEmotes() {
        if (LOADED) {
            return;
        }
        Globals.log(Emote.class, "Loading Emotes...", Globals.LOG_TYPE_DATA);
        LOADED = true;

        for (Globals.Emotes emote : Globals.Emotes.values()) {
            if (emote.getSpriteFolder() != null && emote.getNumFrames() > 0) {
                BufferedImage[] loadSprite = new BufferedImage[emote.getNumFrames()];
                for (int frame = 0; frame < loadSprite.length; frame++) {
                    loadSprite[frame] = Globals.loadTextureResource("sprites/emote/" + emote.getSpriteFolder() + "/" + frame + ".png");
                }
                emote.setSprite(loadSprite);
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
    public Emote call() {
        update();
        return this;
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
    }

    public void draw(final Graphics2D g) {
    }

    public void setExpire() {
        this.duration = 0;
    }

}
