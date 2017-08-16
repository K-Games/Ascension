package blockfighter.client.screen;

import blockfighter.client.Core;
import blockfighter.client.entities.emotes.Emote;
import blockfighter.client.entities.ingamenumber.IngameNumber;
import blockfighter.client.entities.items.Item;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.items.ItemUpgrade;
import blockfighter.client.entities.mob.Mob;
import blockfighter.client.entities.notification.Notification;
import blockfighter.client.entities.particles.*;
import blockfighter.client.entities.particles.skills.other.ParticleItemDrop;
import blockfighter.client.entities.player.Player;
import blockfighter.client.entities.player.skills.Skill;
import blockfighter.client.maps.GameMap;
import blockfighter.client.net.GameClient;
import blockfighter.client.net.PacketSender;
import blockfighter.client.savedata.SaveData;
import blockfighter.client.screen.window.Window;
import blockfighter.client.screen.window.WindowMatchResult;
import blockfighter.client.screen.window.WindowScoreboard;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

public class ScreenIngame extends Screen {

    private static final Color HUD_BG_COLOR = new Color(0, 0, 0, 190);
    private static final Color HUD_BG_COLOR2 = new Color(190, 190, 190, 190);
    private static final String MATCH_OBJECTIVE_TEXT = "Kill 30 players to win";

    private final GameClient client;
    private final Rectangle2D.Double[] hotkeySlots = new Rectangle2D.Double[12];

    private final DecimalFormat COOLDOWN_FORMAT = new DecimalFormat("0.0");
    private final ConcurrentHashMap<Byte, Player> players;
    private final ConcurrentHashMap<Integer, Mob> mobs = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Particle> particles = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, IngameNumber> ingameNumber = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<Notification> notifications = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<Integer, Emote> emotes = new ConcurrentHashMap<>();
    private double screenShakeX, screenShakeY;
    private double screenShakeXAmount, screenShakeYAmount;
    private boolean screenShake = false;
    private long screenShakeDuration, screenShakeStartTime;

    private Rectangle2D logoutBox;

    private int ping = 0;

    private long lastScreenShakeTime = 0;
    private long lastUpdateTime = System.nanoTime();
    private long lastRequestTime = 50;
    private long lastQueueTime = 0;
    private long lastPingTime = 0;
    private long lastSendKeyTime = 0;
    private long matchTimeRemaining = 0;

    private final boolean[] moveKeyDown = {false, false, false, false};
    private final boolean[] skillKeyDown = new boolean[12];

    private final SaveData c;
    private final GameMap map;

    private int drawInfoHotkey = -1;
    private double expBarWidth, expBarDelta, expBarLevel;
    private boolean showScoreboard = false;

    private final Window[] windows = {
        new WindowMatchResult(this),
        new WindowScoreboard(this)};

    private boolean showMatchResult = false;

    public ScreenIngame(final GameMap m, final GameClient cl) {
        this.client = cl;
        this.c = Core.getLogicModule().getSelectedChar();
        this.expBarLevel = this.c.getBaseStats()[Globals.STAT_LEVEL];

        this.players = new ConcurrentHashMap<>();
        for (int j = 0; j < this.hotkeySlots.length; j++) {
            this.hotkeySlots[j] = new Rectangle2D.Double(Globals.WINDOW_WIDTH / 2 - Globals.HUD[0].getWidth() / 2 + 10 + (j * 66), 656, 60,
                    60);
        }
        final Skill[] skills = this.c.getSkills();
        for (final Skill skill : skills) {
            if (skill != null) {
                skill.resetCooldown();
            }
        }
        this.map = m;
    }

    @Override
    public byte getBgmCode() {
        return this.map.getBgmCode();
    }

    @Override
    public void update() {
        final long now = Core.getLogicModule().getTime(); // Get time now
        this.map.update();
        if (now - this.lastQueueTime >= Globals.QUEUES_UPDATE) {
            processDataQueue();
            this.lastQueueTime = now;
        }

        if (now - this.lastSendKeyTime >= Globals.SEND_KEYDOWN_UPDATE) {
            for (byte i = 0; i < this.moveKeyDown.length; i++) {
                PacketSender.sendMove(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), i, this.moveKeyDown[i]);
            }
            for (int i = 0; i < this.skillKeyDown.length; i++) {
                if (this.skillKeyDown[i]) {
                    PacketSender.sendUseSkill(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), this.c.getHotkeys()[i].getSkillCode());
                }
            }
            this.lastSendKeyTime = now;
        }

        if (now - this.lastUpdateTime >= Globals.CLIENT_LOGIC_UPDATE) {
            updateIngameNumber();
            updateParticles(this.particles);
            updatePlayers();
            updateMobs();
            updateNotifications();
            updateEmotes();
            updateHudTimer();
            for (Window window : this.windows) {
                window.update();
            }
            this.ping = GameClient.getPing();
            this.lastUpdateTime = now;
        }

        if (now - this.lastScreenShakeTime >= Globals.msToNs(25)) {
            if (now - this.screenShakeStartTime <= Globals.msToNs(this.screenShakeDuration)) {
                screenShakeXAmount *= 0.92;
                screenShakeYAmount *= 0.92;
                screenShakeX = Globals.rng(3) * screenShakeXAmount - screenShakeXAmount;
                screenShakeY = Globals.rng(3) * screenShakeYAmount - screenShakeYAmount;
                this.screenShake = true;
            } else {
                this.screenShake = false;
            }
            this.lastScreenShakeTime = now;
        }

        if (now - this.lastRequestTime >= Globals.REQUESTALL_UPDATE) {
            PacketSender.sendGetAll(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey());
            this.lastRequestTime = now;
        }

        if (now - this.lastPingTime >= Globals.PING_UPDATE) {
            PacketSender.sendGetPing();
            this.lastPingTime = now;
        }
    }

    private void updateHudTimer() {
        if (this.matchTimeRemaining > 0) {
            this.matchTimeRemaining -= Globals.nsToMs(Core.getLogicModule().getTime() - this.lastUpdateTime);
        } else {
            this.matchTimeRemaining = 0;
        }
    }

    private void updateEmotes() {
        LinkedList<Future<Emote>> futures = new LinkedList<>();
        this.emotes.entrySet().forEach((emote) -> {
            futures.add(Core.SHARED_THREADPOOL.submit(emote.getValue()));
        });
        futures.forEach((task) -> {
            try {
                Emote n = task.get();
                if (n.isExpired()) {
                    this.emotes.remove(n.getKey());
                    Emote.returnKey(n.getKey());
                }
            } catch (final Exception ex) {
                Globals.logError(ex.toString(), ex);
            }
        });
    }

    private void updateIngameNumber() {
        LinkedList<Future<IngameNumber>> futures = new LinkedList<>();
        this.ingameNumber.entrySet().forEach((n) -> {
            futures.add(Core.SHARED_THREADPOOL.submit(n.getValue()));
        });
        futures.forEach((task) -> {
            try {
                IngameNumber n = task.get();
                if (n.isExpired()) {
                    this.ingameNumber.remove(n.getKey());
                    IngameNumber.returnKey(n.getKey());
                }
            } catch (final Exception ex) {
                Globals.logError(ex.toString(), ex);
            }
        });

    }

    private void updateNotifications() {
        LinkedList<Future<Notification>> futures = new LinkedList<>();
        this.notifications.forEach((n) -> {
            futures.add(Core.SHARED_THREADPOOL.submit(n));
        });
        futures.forEach((task) -> {
            try {
                task.get();
            } catch (final Exception ex) {
                Globals.logError(ex.toString(), ex);
            }
        });

        if (this.notifications.peek() != null && this.notifications.peek().isExpired()) {
            this.notifications.remove();
        }

        Core.SHARED_THREADPOOL.execute(() -> {
            while (this.notifications.size() > Notification.MAX_NUM_NOTIFICATIONS) {
                this.notifications.remove();
            }
        });
    }

    private void updateMobs() {
        LinkedList<Future<Mob>> futures = new LinkedList<>();
        this.mobs.entrySet().forEach((pEntry) -> {
            futures.add(Core.SHARED_THREADPOOL.submit(pEntry.getValue()));
        });
        futures.forEach((task) -> {
            try {
                task.get();
            } catch (final Exception ex) {
                Globals.logError(ex.toString(), ex);
            }
        });
    }

    private void updatePlayers() {
        LinkedList<Future<Player>> futures = new LinkedList<>();
        this.players.entrySet().forEach((pEntry) -> {
            futures.add(Core.SHARED_THREADPOOL.submit(pEntry.getValue()));
        });
        futures.forEach((task) -> {
            try {
                Player player = task.get();
                if (player.isDisconnected()) {
                    this.players.remove(player.getKey());
                }
            } catch (final Exception ex) {
                Globals.logError(ex.toString(), ex);
            }
        });
    }

    @Override
    public void draw(final Graphics2D g) {
        g.setClip(0, 0, 1280, 720);
        final AffineTransform resetForm = g.getTransform();

        this.players.entrySet().forEach((pEntry) -> {
            pEntry.getValue().updatePos();
        });
        if (this.players != null && Core.getLogicModule().getMyPlayerKey() != null && this.players.containsKey(Core.getLogicModule().getMyPlayerKey())) {
            try {
                this.map.drawBg(g, this.players.get(Core.getLogicModule().getMyPlayerKey()).getX(), this.players.get(Core.getLogicModule().getMyPlayerKey()).getY());
                double scale = 1;
                g.scale(scale, scale);
                g.translate(640D / scale - this.players.get(Core.getLogicModule().getMyPlayerKey()).getX(), 500D / scale - this.players.get(Core.getLogicModule().getMyPlayerKey()).getY());
                if (screenShake) {
                    g.translate(screenShakeX, screenShakeY);
                }
            } catch (NullPointerException e) {
                //Expected null if disconnecting while rendering a frame.
            }
        } else {
            this.map.drawBg(g, 0, 0);
        }

        this.map.draw(g);

        if (this.mobs != null) {
            this.mobs.entrySet().forEach((pEntry) -> {
                pEntry.getValue().draw(g);
            });
        }

        if (this.players != null) {
            this.players.entrySet().stream().filter((pEntry) -> (pEntry.getValue().getKey() != Core.getLogicModule().getMyPlayerKey())).forEachOrdered((pEntry) -> {
                pEntry.getValue().draw(g);
            });
            if (this.players.containsKey(Core.getLogicModule().getMyPlayerKey())) {
                this.players.get(Core.getLogicModule().getMyPlayerKey()).draw(g);
            }
        }

        this.particles.entrySet().forEach((pEntry) -> {
            try {
                if (!pEntry.getValue().isExpired()) {
                    pEntry.getValue().draw(g);
                }
            } catch (Exception e) {
                Globals.logError(e.toString(), e);
            }
        });
        this.emotes.entrySet().forEach((emote) -> {
            try {
                if (!emote.getValue().isExpired()) {
                    emote.getValue().draw(g);
                }
            } catch (Exception e) {
                Globals.logError(e.toString(), e);
            }
        });

        g.setTransform(resetForm);
        this.ingameNumber.entrySet().stream().filter((number) -> (!number.getValue().isExpired())).forEachOrdered((number) -> {
            number.getValue().draw(g);
        });
        drawHUD(g);
        drawHotkeys(g);

        if (this.drawInfoHotkey != -1) {
            drawSkillInfo(g, this.hotkeySlots[this.drawInfoHotkey], this.c.getHotkeys()[this.drawInfoHotkey]);
        }

        drawNotifications(g);

        g.setColor(new Color(25, 25, 25, 150));
        g.fillRect(1210, 5, 65, 45);
        g.setFont(Globals.ARIAL_12PT);
        g.setColor(Color.WHITE);
        g.drawString("Ping: " + this.ping, 1220, 40);

        g.setColor(Color.BLACK);
        g.setFont(Globals.ARIAL_15PT);

        this.logoutBox = g.getFontMetrics().getStringBounds("Leave", g);
        g.fillRect(10, 10, (int) this.logoutBox.getWidth() + 6, (int) this.logoutBox.getHeight() + 6);
        g.setColor(Color.WHITE);
        g.drawString("Leave", 13, (int) (9 + this.logoutBox.getHeight()));
        g.drawRect(10, 10, (int) this.logoutBox.getWidth() + 6, (int) this.logoutBox.getHeight() + 6);

        if (this.showScoreboard) {
            this.windows[1].draw(g);
        }
    }

    private void drawNotifications(final Graphics2D g) {
        Notification[] copy = this.notifications.toArray(new Notification[this.notifications.size()]);
        int y = 580 - Math.min(Notification.MAX_NUM_NOTIFICATIONS, copy.length) * Notification.BG_HEIGHT;
        for (int i = 0; i < Math.min(Notification.MAX_NUM_NOTIFICATIONS, copy.length); i++) {
            copy[i].draw(g, 0, y);
            y += 25;
        }
    }

    private void drawHotkeys(final Graphics2D g) {
        final Skill[] hotkey = this.c.getHotkeys();
        for (int j = 0; j < this.hotkeySlots.length; j++) {
            if (hotkey[j] != null) {
                hotkey[j].draw(g, (int) this.hotkeySlots[j].x, (int) this.hotkeySlots[j].y);
                g.setColor(new Color(100, 100, 100, 125));
                final int cdHeight = (int) ((hotkey[j].getCooldown() / (hotkey[j].getMaxCooldown() * 1D)) * this.hotkeySlots[j].height);
                g.fillRect((int) this.hotkeySlots[j].x, (int) (this.hotkeySlots[j].y + this.hotkeySlots[j].height - cdHeight),
                        (int) this.hotkeySlots[j].width,
                        cdHeight);
                if (hotkey[j].getCooldown() > 0) {
                    g.setFont(Globals.ARIAL_18PT);
                    final int width = g.getFontMetrics().stringWidth(this.COOLDOWN_FORMAT.format(hotkey[j].getCooldown() / 1000D));
                    drawStringOutline(g, this.COOLDOWN_FORMAT.format(hotkey[j].getCooldown() / 1000D), (int) this.hotkeySlots[j].x + 28 - width / 2,
                            (int) this.hotkeySlots[j].y + 33, 1);
                    g.setColor(Color.white);
                    g.drawString(this.COOLDOWN_FORMAT.format(hotkey[j].getCooldown() / 1000D), (int) this.hotkeySlots[j].x + 28 - width / 2,
                            (int) this.hotkeySlots[j].y + 33);
                }
            }
            g.setFont(Globals.ARIAL_15PT);
            String key = "?";
            if (this.c.getKeyBind()[j] != -1) {
                key = KeyEvent.getKeyText(this.c.getKeyBind()[j]);
            }
            final int width = g.getFontMetrics().stringWidth(key);
            drawStringOutline(g, key, (int) this.hotkeySlots[j].x + 58 - width, (int) this.hotkeySlots[j].y + 58, 1);
            g.setColor(Color.WHITE);
            g.drawString(key, (int) this.hotkeySlots[j].x + 58 - width, (int) this.hotkeySlots[j].y + 58);
        }
    }

    private void drawHUD(final Graphics2D g) {
        final BufferedImage hud = Globals.HUD[0];
        final BufferedImage expHud = Globals.HUD[2];

        g.setColor(HUD_BG_COLOR);
        int minutes = (int) (this.matchTimeRemaining / (60 * 1000));
        int seconds = (int) ((matchTimeRemaining / 1000) % 60);
        String str = String.format("%d:%02d", minutes, seconds);
        g.setFont(Globals.ARIAL_24PTBOLD);
        final int x1 = Globals.WINDOW_WIDTH / 2 - g.getFontMetrics().stringWidth(str) / 2;
        final int x2 = Globals.WINDOW_WIDTH / 2 + g.getFontMetrics().stringWidth(str) / 2;

        final Polygon timerBg = new Polygon(new int[]{
            x1 - 30, x2 + 30, x2 + 15, x1 - 15},
                new int[]{0, 0, 40, 40}, 4);
        g.fillPolygon(timerBg);

        final Polygon timerBg2 = new Polygon(new int[]{
            x1 - 40, x1 - 30, x1 - 15, x1 - 25},
                new int[]{0, 0, 40, 40}, 4);
        final Polygon timerBg3 = new Polygon(new int[]{
            x2 + 40, x2 + 30, x2 + 15, x2 + 25},
                new int[]{0, 0, 40, 40}, 4);
        g.setColor(HUD_BG_COLOR2);
        g.fillPolygon(timerBg2);
        g.fillPolygon(timerBg3);

        g.setColor(Color.WHITE);
        g.drawString(str, x1, timerBg.getBounds().y + 30);

        String matchObj = MATCH_OBJECTIVE_TEXT;
        g.setFont(Globals.ARIAL_15PT);
        final int matchObjX = Globals.WINDOW_WIDTH / 2 - g.getFontMetrics().stringWidth(matchObj) / 2;
        drawStringOutline(g, matchObj, matchObjX, (int) (timerBg.getBounds().getMaxY() + 18), 2);
        g.setColor(Color.WHITE);
        g.drawString(matchObj, matchObjX, (int) (timerBg.getBounds().getMaxY() + 18));

        g.drawImage(expHud, Globals.WINDOW_WIDTH / 2 - expHud.getWidth() / 2, Globals.WINDOW_HEIGHT - hud.getHeight() - expHud.getHeight(), null);
        g.drawImage(hud, Globals.WINDOW_WIDTH / 2 - hud.getWidth() / 2, Globals.WINDOW_HEIGHT - hud.getHeight(), null);

        if (this.players.containsKey(Core.getLogicModule().getMyPlayerKey())) {
            final BufferedImage hpbar = Globals.HUD[1];
            final BufferedImage expbar = Globals.HUD[3];
            final double hpBarWidth;

            if (this.players.get(Core.getLogicModule().getMyPlayerKey()).getStat(Globals.STAT_MINHP) <= this.players.get(Core.getLogicModule().getMyPlayerKey()).getStat(Globals.STAT_MAXHP)) {
                hpBarWidth = this.players.get(Core.getLogicModule().getMyPlayerKey()).getStat(Globals.STAT_MINHP) / this.players.get(Core.getLogicModule().getMyPlayerKey()).getStat(Globals.STAT_MAXHP);
            } else {
                hpBarWidth = 1;
            }

            if (this.c.getBaseStats()[Globals.STAT_EXP] <= this.c.getBaseStats()[Globals.STAT_MAXEXP]) {
                if (this.expBarLevel != this.c.getBaseStats()[Globals.STAT_LEVEL]) {
                    if (this.expBarWidth >= 1) {
                        this.expBarDelta = 0;
                        this.expBarWidth = 0;
                        this.expBarLevel++;
                    }
                }
                double expBarRealWidth = (this.expBarLevel == this.c.getBaseStats()[Globals.STAT_LEVEL]) ? this.c.getBaseStats()[Globals.STAT_EXP] / this.c.getBaseStats()[Globals.STAT_MAXEXP] : 1;
                if (this.expBarWidth < expBarRealWidth) {
                    if (expBarDelta > 0) {
                        this.expBarWidth += this.expBarDelta;
                        if (this.expBarWidth > 1) {
                            this.expBarWidth = 1;
                        }
                    } else {
                        expBarDelta = (expBarRealWidth - expBarWidth) / (1.5 * Globals.RENDER_FPS);
                    }
                } else {
                    this.expBarWidth = expBarRealWidth;
                    this.expBarDelta = 0;
                }

            } else {
                this.expBarWidth = 0;
            }

            g.drawImage(expbar,
                    Globals.WINDOW_WIDTH / 2 - expHud.getWidth() / 2 + 2, Globals.WINDOW_HEIGHT - hud.getHeight() - expHud.getHeight() + 2,
                    (int) (expBarWidth * 802D), expbar.getHeight(), null);
            g.drawImage(hpbar,
                    Globals.WINDOW_WIDTH / 2 - hud.getWidth() / 2 + 2, Globals.WINDOW_HEIGHT - hud.getHeight() + 2,
                    (int) (hpBarWidth * 802D), 38, null);
            g.setFont(Globals.ARIAL_18PT);
            final int width = g.getFontMetrics().stringWidth(
                    (int) this.players.get(Core.getLogicModule().getMyPlayerKey()).getStat(Globals.STAT_MINHP) + "/"
                    + (int) this.players.get(Core.getLogicModule().getMyPlayerKey()).getStat(Globals.STAT_MAXHP));
            drawStringOutline(g,
                    (int) this.players.get(Core.getLogicModule().getMyPlayerKey()).getStat(Globals.STAT_MINHP) + "/"
                    + (int) this.players.get(Core.getLogicModule().getMyPlayerKey()).getStat(Globals.STAT_MAXHP),
                    Globals.WINDOW_WIDTH / 2 - width / 2, Globals.WINDOW_HEIGHT - hud.getHeight() + 28, 1);
            g.setColor(Color.WHITE);
            g.drawString((int) this.players.get(Core.getLogicModule().getMyPlayerKey()).getStat(Globals.STAT_MINHP) + "/"
                    + (int) this.players.get(Core.getLogicModule().getMyPlayerKey()).getStat(Globals.STAT_MAXHP),
                    Globals.WINDOW_WIDTH / 2 - width / 2, Globals.WINDOW_HEIGHT - hud.getHeight() + 28);
        }
        if (this.showMatchResult) {
            this.windows[0].draw(g);
        }
    }

    private void drawSkillInfo(final Graphics2D g, final Rectangle2D.Double box, final Skill skill) {
        skill.drawInfo(g, (int) box.x, (int) box.y);
    }

    @Override
    public ConcurrentHashMap<Integer, Particle> getParticles() {
        return this.particles;
    }

    public ConcurrentHashMap<Byte, Player> getPlayers() {
        return this.players;
    }

    public Point getPlayerPos(final byte key) {
        if (!this.players.containsKey(key)) {
            return null;
        }
        return new Point(this.players.get(key).getX(), this.players.get(key).getY());
    }

    private void processDataQueue() {
        ConcurrentLinkedQueue<byte[]> dataQueue = this.client.getDataQueue();
        while (this.players != null && !dataQueue.isEmpty()) {
            final byte[] data = dataQueue.remove();
            final byte dataType = data[0];
            switch (dataType) {
                case Globals.DATA_PLAYER_GET_ALL:
                    dataPlayerGetAll(data);
                    break;
                case Globals.DATA_PLAYER_SET_POS:
                    dataPlayerSetPos(data);
                    break;
                case Globals.DATA_PLAYER_SET_FACING:
                    dataPlayerSetFacing(data);
                    break;
                case Globals.DATA_PLAYER_SET_STATE:
                    dataPlayerSetState(data);
                    break;
                case Globals.DATA_PARTICLE_EFFECT:
                    dataParticleEffect(data);
                    break;
                case Globals.DATA_PLAYER_DISCONNECT:
                    dataPlayerDisconnect(data);
                    break;
                case Globals.DATA_PLAYER_GET_NAME:
                    dataPlayerGetName(data);
                    break;
                case Globals.DATA_PLAYER_GET_STAT:
                    dataPlayerGetStat(data);
                    break;
                case Globals.DATA_PLAYER_GET_EQUIP:
                    dataPlayerGetEquip(data);
                    break;
                case Globals.DATA_PLAYER_SET_COOLDOWN:
                    dataPlayerSetCooldown(data);
                    break;
                case Globals.DATA_NUMBER:
                    dataIngameNumber(data);
                    break;
                case Globals.DATA_PLAYER_GIVEEXP:
                    dataPlayerGiveEXP(data);
                    break;
                case Globals.DATA_MOB_GET_STAT:
                    dataMobGetStat(data);
                    break;
                case Globals.DATA_MOB_PARTICLE_EFFECT:
                    dataMobParticleEffect(data);
                    break;
                case Globals.DATA_MOB_SET_POS:
                    dataMobSetPos(data);
                    break;
                case Globals.DATA_MOB_SET_FACING:
                    dataMobSetFacing(data);
                    break;
                case Globals.DATA_MOB_SET_STATE:
                    dataMobSetState(data);
                    break;
                case Globals.DATA_MOB_SET_TYPE:
                    dataMobSetType(data);
                    break;
                case Globals.DATA_PLAYER_GIVEDROP:
                    dataPlayerGiveDrop(data);
                    break;
                case Globals.DATA_SOUND_EFFECT:
                    dataSoundEffect(data);
                    break;
                case Globals.DATA_SCREEN_SHAKE:
                    dataScreenShake(data);
                    break;
                case Globals.DATA_PLAYER_EMOTE:
                    dataPlayerEmote(data);
                    break;
                case Globals.DATA_NOTIFICATION_KILL:
                    dataNotificationKill(data);
                    break;
                case Globals.DATA_PLAYER_SCORE:
                    dataPlayerScore(data);
                    break;
                case Globals.DATA_PLAYER_MATCH_RESULT:
                    dataPlayerMatchResult(data);
                    break;
            }
        }
    }

    private void dataPlayerMatchResult(final byte[] data) {
        final Globals.VictoryStatus status = Globals.VictoryStatus.get(data[1]);
        WindowMatchResult window = (WindowMatchResult) windows[0];
        window.startCountdown();
        window.setVictoryStatus(status);
        window.update();
        this.showMatchResult = true;
    }

    private void dataPlayerScore(final byte[] data) {
        final byte playerKey = data[1];
        if (!this.players.containsKey(playerKey)) {
            return;
        }
        final int score = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
        final int playerPing = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
        final int timeLeft = Globals.bytesToInt(Arrays.copyOfRange(data, 10, 14));
        this.players.get(playerKey).setScore(score);
        this.players.get(playerKey).setPing(playerPing);
        this.matchTimeRemaining = timeLeft;
    }

    private void dataNotificationKill(final byte[] data) {
        final byte killerKey = data[1];
        final byte victimKey = data[2];
        if (this.players.containsKey(killerKey) && this.players.containsKey(victimKey)) {
            this.notifications.add(new Notification(this.players.get(killerKey), this.players.get(victimKey)));
        }
    }

    private void dataPlayerEmote(final byte[] data) {
        final byte playerKey = data[1];
        if (!this.players.containsKey(playerKey)) {
            return;
        }
        final byte emoteID = data[2];
        Emote newEmote = Globals.Emotes.get(emoteID).newEmote(this.players.get(playerKey));
        this.emotes.put(newEmote.getKey(), newEmote);
    }

    private void dataScreenShake(final byte[] data) {
        final int xAmount = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
        final int yAmount = Globals.bytesToInt(Arrays.copyOfRange(data, 5, 9));
        final int duration = Globals.bytesToInt(Arrays.copyOfRange(data, 9, 13));
        if (!this.screenShake || xAmount > this.screenShakeXAmount || yAmount > this.screenShakeYAmount) {
            this.screenShake = true;
            this.screenShakeStartTime = Core.getLogicModule().getTime();
            this.screenShakeDuration = duration;
            this.screenShakeXAmount = xAmount;
            this.screenShakeYAmount = yAmount;
        }
    }

    private void dataSoundEffect(final byte[] data) {
        final byte sfxID = data[1];
        final int x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
        final int y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));

        if (Point.distance(this.players.get(Core.getLogicModule().getMyPlayerKey()).getX(), this.players.get(Core.getLogicModule().getMyPlayerKey()).getY(), x, y) <= 800) {
            Core.getSoundModule().playSound(sfxID, x, y);
        }
    }

    private void dataPlayerGiveDrop(final byte[] data) {
        final int lvl = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
        final int dropCode = Globals.bytesToInt(Arrays.copyOfRange(data, 5, 9));
        Item dropItem = null;
        if (ItemUpgrade.isValidItem(dropCode)) {
            dropItem = new ItemUpgrade(dropCode, lvl + Globals.rng(6));
        }

        if (ItemEquip.isValidItem(dropCode)) {
            dropItem = new ItemEquip(dropCode, lvl, Globals.rng(100) < 7);
        }

        if (dropItem != null) {
            this.notifications.add(new Notification(dropItem));
            this.c.addDrops(lvl, dropItem);
            ((WindowMatchResult) windows[0]).addItemGained(dropItem);

            ParticleItemDrop effect = new ParticleItemDrop(this.players.get(Core.getLogicModule().getMyPlayerKey()), this.players.get(Core.getLogicModule().getMyPlayerKey()));
            effect.setItem(dropItem);
            addParticle(effect);
        }
    }

    private void dataPlayerGiveEXP(final byte[] data) {
        final int amount = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
        this.notifications.add(new Notification(amount));
        this.c.addExp(amount);
        this.expBarDelta = 0;
        ((WindowMatchResult) windows[0]).addExpGained(amount);
    }

    private void dataPlayerSetCooldown(final byte[] data) {
        this.c.getSkills()[data[1]].setCooldown();
    }

    private void dataPlayerGetEquip(final byte[] data) {
        final byte key = data[1];
        spawnPlayer(key);
        for (byte i = 0; i < Globals.NUM_EQUIP_SLOTS; i++) {
            final byte[] temp = new byte[4];
            System.arraycopy(data, i * 4 + 2, temp, 0, temp.length);
            this.players.get(key).setEquip(i, Globals.bytesToInt(temp));
        }
    }

    private void dataPlayerGetStat(final byte[] data) {
        final byte key = data[1];
        spawnPlayer(key);
        final byte stat = data[2];
        final int amount = Globals.bytesToInt(Arrays.copyOfRange(data, 3, 7));
        this.players.get(key).setStat(stat, amount);

    }

    private void dataPlayerGetAll(final byte[] data) {
        final byte key = data[1];
        final int x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
        final int y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
        final byte facing = data[10], state = data[11], frame = data[12];
        spawnPlayer(key);
        this.players.get(key).setPos(x, y);
        this.players.get(key).setFacing(facing);
        this.players.get(key).setState(state);
        this.players.get(key).setFrame(frame);
    }

    private void dataPlayerSetPos(final byte[] data) {
        for (int i = 0; i < (data.length - 1) / 10; i++) {
            final byte key = data[i * 10 + 1];
            if (key != -1) {
                final int x = Globals.bytesToInt(Arrays.copyOfRange(data, i * 10 + 2, i * 10 + 6));
                final int y = Globals.bytesToInt(Arrays.copyOfRange(data, i * 10 + 6, i * 10 + 10));
                final byte facing = data[i * 10 + 10];
                spawnPlayer(key, x, y);
                this.players.get(key).setPos(x, y);
                this.players.get(key).setFacing(facing);
            }
        }
    }

    private void dataPlayerSetFacing(final byte[] data) {
        final byte key = data[1];
        final byte facing = data[2];
        spawnPlayer(key);
        this.players.get(key).setFacing(facing);
    }

    private void dataPlayerSetState(final byte[] data) {
        for (int i = 0; i < (data.length - 1) / 11; i++) {
            final byte key = data[i * 11 + 1];
            if (key != -1) {
                spawnPlayer(key);
                final byte state = data[i * 11 + 2];
                final byte frame = data[i * 11 + 3];
                final int x = Globals.bytesToInt(Arrays.copyOfRange(data, i * 11 + 4, i * 11 + 8));
                final int y = Globals.bytesToInt(Arrays.copyOfRange(data, i * 11 + 8, i * 11 + 12));
                this.players.get(key).setPos(x, y);
                this.players.get(key).setState(state);
                this.players.get(key).setFrame(frame);
            }
        }

    }

    private void dataPlayerDisconnect(final byte[] data) {
        final byte key = data[1];
        if (this.players.containsKey(key) && key != Core.getLogicModule().getMyPlayerKey()) {
            this.players.get(key).disconnect();
        }
    }

    private void dataIngameNumber(final byte[] data) {
        if (this.players.containsKey(Core.getLogicModule().getMyPlayerKey())) {
            final byte type = data[1];
            final int x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
            final int y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
            final int value = Globals.bytesToInt(Arrays.copyOfRange(data, 10, 14));
            IngameNumber newNumber = new IngameNumber(value, type, new Point(x, y), this.players.get(Core.getLogicModule().getMyPlayerKey()));
            this.ingameNumber.put(newNumber.getKey(), newNumber);
        }
    }

    private void dataParticleEffect(final byte[] data) {
        final byte particleID = data[1];
        int x, y;
        byte facing, playerKey, targetKey;
        Globals.Particles particle = Globals.Particles.get(particleID);
        if (particle.getParameterTypes() == Globals.PARTICLE_PARAM_POS_AND_FACING) {
            x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
            y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
            facing = data[10];
            addParticle(particle.newParticle(x, y, facing));
        } else if (particle.getParameterTypes() == Globals.PARTICLE_PARAM_PLAYER) {
            playerKey = data[2];
            if (this.players.containsKey(playerKey)) {
                addParticle(particle.newParticle(this.players.get(playerKey)));
            }
        } else if (particle.getParameterTypes() == Globals.PARTICLE_PARAM_FACING_AND_PLAYER) {
            facing = data[2];
            playerKey = data[3];
            if (this.players.containsKey(playerKey)) {
                addParticle(particle.newParticle(facing, this.players.get(playerKey)));
            }
        } else if (particle.getParameterTypes() == Globals.PARTICLE_PARAM_POS) {
            x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
            y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
            addParticle(particle.newParticle(x, y));
        } else if (particle.getParameterTypes() == Globals.PARTICLE_PARAM_PLAYER_AND_TARGET) {
            playerKey = data[2];
            targetKey = data[3];
            if (this.players.containsKey(playerKey) && this.players.containsKey(targetKey)) {
                addParticle(particle.newParticle(this.players.get(playerKey), this.players.get(targetKey)));
            }
        }
    }

    private void dataPlayerGetName(final byte[] data) {
        final byte key = data[1];
        spawnPlayer(key);
        final byte[] temp = new byte[Globals.MAX_NAME_LENGTH];
        System.arraycopy(data, 2, temp, 0, data.length - 2);
        this.players.get(key).setPlayerName(new String(temp, StandardCharsets.UTF_8).trim());
    }

    private void dataMobParticleEffect(final byte[] data) {
        final int key = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
        if (this.mobs.containsKey(key)) {
            this.mobs.get(key).addParticle(data);
        }
    }

    private void dataMobSetType(final byte[] data) {
        final int key = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
        final byte type = data[5];
        if (!this.mobs.containsKey(key)) {
            final int x = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
            final int y = Globals.bytesToInt(Arrays.copyOfRange(data, 10, 14));
            this.mobs.put(key, Mob.spawnMob(type, key, x, y, this.client));
        }
    }

    private void dataMobSetPos(final byte[] data) {
        final int key = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
        if (isExistingMob(key)) {
            final int x = Globals.bytesToInt(Arrays.copyOfRange(data, 5, 9));
            final int y = Globals.bytesToInt(Arrays.copyOfRange(data, 9, 13));
            this.mobs.get(key).setPos(x, y);
        }
    }

    private void dataMobSetFacing(final byte[] data) {
        final int key = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
        if (isExistingMob(key)) {
            final byte facing = data[5];
            this.mobs.get(key).setFacing(facing);
        }
    }

    private void dataMobSetState(final byte[] data) {
        final int key = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
        if (isExistingMob(key)) {
            final byte state = data[5];
            final byte frame = data[6];
            this.mobs.get(key).setState(state);
            this.mobs.get(key).setFrame(frame);
        }
    }

    private void dataMobGetStat(final byte[] data) {
        final int key = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
        if (isExistingMob(key)) {
            final byte stat = data[5];
            final int amount = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
            this.mobs.get(key).setStat(stat, amount);
        }
    }

    @Override
    public void addParticle(final Particle newParticle) {
        this.particles.put(newParticle.getKey(), newParticle);
    }

    public void disconnect() {
        PacketSender.sendDisconnect(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey());
    }

    private void setKeyDown(final int direction, final boolean set) {
        this.moveKeyDown[direction] = set;
    }

    private void setSkillKeyDown(final int slot, final boolean set) {
        this.skillKeyDown[slot] = set;
    }

    private void spawnPlayer(final byte key) {
        spawnPlayer(key, 0, 0);
    }

    private boolean isExistingMob(final int key) {
        if (!this.mobs.containsKey(key)) {
            PacketSender.sendSetMobType(Core.getLogicModule().getConnectedRoom(), key);
            return false;
        }
        return true;
    }

    private void spawnPlayer(final byte key, final int x, final int y) {
        if (!this.players.containsKey(key)) {
            this.players.put(key, new Player(x, y, key));
        }
    }

    @Override
    public void keyTyped(final KeyEvent e) {

    }

    @Override
    public void keyPressed(final KeyEvent e) {
        final int key = e.getKeyCode();
        if (key == this.c.getKeyBind()[Globals.KEYBIND_JUMP]) {
            setKeyDown(Globals.UP, true);
            PacketSender.sendMove(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.UP, this.moveKeyDown[Globals.UP]);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_DOWN]) {
            setKeyDown(Globals.DOWN, true);
            PacketSender.sendMove(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.DOWN, this.moveKeyDown[Globals.DOWN]);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_LEFT]) {
            setKeyDown(Globals.LEFT, true);
            PacketSender.sendMove(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.LEFT, this.moveKeyDown[Globals.LEFT]);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_RIGHT]) {
            setKeyDown(Globals.RIGHT, true);
            PacketSender.sendMove(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.RIGHT, this.moveKeyDown[Globals.RIGHT]);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL1]) {
            if (this.c.getHotkeys()[0] != null) {
                setSkillKeyDown(0, true);
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL2]) {
            if (this.c.getHotkeys()[1] != null) {
                setSkillKeyDown(1, true);
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL3]) {
            if (this.c.getHotkeys()[2] != null) {
                setSkillKeyDown(2, true);
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL4]) {
            if (this.c.getHotkeys()[3] != null) {
                setSkillKeyDown(3, true);
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL5]) {
            if (this.c.getHotkeys()[4] != null) {
                setSkillKeyDown(4, true);
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL6]) {
            if (this.c.getHotkeys()[5] != null) {
                setSkillKeyDown(5, true);
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL7]) {
            if (this.c.getHotkeys()[6] != null) {
                setSkillKeyDown(6, true);
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL8]) {
            if (this.c.getHotkeys()[7] != null) {
                setSkillKeyDown(7, true);
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL9]) {
            if (this.c.getHotkeys()[8] != null) {
                setSkillKeyDown(8, true);
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL10]) {
            if (this.c.getHotkeys()[9] != null) {
                setSkillKeyDown(9, true);
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL11]) {
            if (this.c.getHotkeys()[10] != null) {
                setSkillKeyDown(10, true);
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL12]) {
            if (this.c.getHotkeys()[11] != null) {
                setSkillKeyDown(11, true);
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE1]) {
            PacketSender.sendUseEmote(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.Emotes.ALERT.getEmoteCode());
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE2]) {
            PacketSender.sendUseEmote(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.Emotes.QUESTION.getEmoteCode());
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE3]) {
            PacketSender.sendUseEmote(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.Emotes.SWEAT.getEmoteCode());
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE4]) {
            PacketSender.sendUseEmote(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.Emotes.SLEEP.getEmoteCode());
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE5]) {
            PacketSender.sendUseEmote(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.Emotes.ANGRY.getEmoteCode());
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE6]) {
//            PacketSender.sendUseEmote(Core.getLogicModule().getSelectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.EMOTE_5);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE7]) {
//            PacketSender.sendUseEmote(Core.getLogicModule().getSelectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.EMOTE_6);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE8]) {
//            PacketSender.sendUseEmote(Core.getLogicModule().getSelectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.EMOTE_7);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE9]) {
//            PacketSender.sendUseEmote(Core.getLogicModule().getSelectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.EMOTE_8);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE10]) {
//            PacketSender.sendUseEmote(Core.getLogicModule().getSelectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.EMOTE_9);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SCOREBOARD]) {
            this.showScoreboard = true;
        }

    }

    @Override
    public void keyReleased(final KeyEvent e) {
        final int key = e.getKeyCode();
        if (key == this.c.getKeyBind()[Globals.KEYBIND_JUMP]) {
            setKeyDown(Globals.UP, false);
            PacketSender.sendMove(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.UP, this.moveKeyDown[Globals.UP]);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_DOWN]) {
            setKeyDown(Globals.DOWN, false);
            PacketSender.sendMove(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.DOWN, this.moveKeyDown[Globals.DOWN]);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_LEFT]) {
            setKeyDown(Globals.LEFT, false);
            PacketSender.sendMove(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.LEFT, this.moveKeyDown[Globals.LEFT]);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_RIGHT]) {
            setKeyDown(Globals.RIGHT, false);
            PacketSender.sendMove(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.RIGHT, this.moveKeyDown[Globals.RIGHT]);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL1]) {
            if (this.c.getHotkeys()[0] != null) {
                setSkillKeyDown(0, false);
                PacketSender.sendUseSkill(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), this.c.getHotkeys()[0].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL2]) {
            if (this.c.getHotkeys()[1] != null) {
                setSkillKeyDown(1, false);
                PacketSender.sendUseSkill(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), this.c.getHotkeys()[1].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL3]) {
            if (this.c.getHotkeys()[2] != null) {
                setSkillKeyDown(2, false);
                PacketSender.sendUseSkill(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), this.c.getHotkeys()[2].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL4]) {
            if (this.c.getHotkeys()[3] != null) {
                setSkillKeyDown(3, false);
                PacketSender.sendUseSkill(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), this.c.getHotkeys()[3].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL5]) {
            if (this.c.getHotkeys()[4] != null) {
                setSkillKeyDown(4, false);
                PacketSender.sendUseSkill(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), this.c.getHotkeys()[4].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL6]) {
            if (this.c.getHotkeys()[5] != null) {
                setSkillKeyDown(5, false);
                PacketSender.sendUseSkill(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), this.c.getHotkeys()[5].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL7]) {
            if (this.c.getHotkeys()[6] != null) {
                setSkillKeyDown(6, false);
                PacketSender.sendUseSkill(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), this.c.getHotkeys()[6].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL8]) {
            if (this.c.getHotkeys()[7] != null) {
                setSkillKeyDown(7, false);
                PacketSender.sendUseSkill(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), this.c.getHotkeys()[7].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL9]) {
            if (this.c.getHotkeys()[8] != null) {
                setSkillKeyDown(8, false);
                PacketSender.sendUseSkill(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), this.c.getHotkeys()[8].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL10]) {
            if (this.c.getHotkeys()[9] != null) {
                setSkillKeyDown(9, false);
                PacketSender.sendUseSkill(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), this.c.getHotkeys()[9].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL11]) {
            if (this.c.getHotkeys()[10] != null) {
                setSkillKeyDown(10, false);
                PacketSender.sendUseSkill(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), this.c.getHotkeys()[10].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL12]) {
            if (this.c.getHotkeys()[11] != null) {
                setSkillKeyDown(11, false);
                PacketSender.sendUseSkill(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), this.c.getHotkeys()[11].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE1]) {
            PacketSender.sendUseEmote(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.Emotes.ALERT.getEmoteCode());
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE2]) {
            PacketSender.sendUseEmote(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.Emotes.QUESTION.getEmoteCode());
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE3]) {
            PacketSender.sendUseEmote(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.Emotes.SWEAT.getEmoteCode());
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE4]) {
            PacketSender.sendUseEmote(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.Emotes.SLEEP.getEmoteCode());
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE5]) {
            PacketSender.sendUseEmote(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.Emotes.ANGRY.getEmoteCode());
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE6]) {
            PacketSender.sendUseEmote(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.Emotes.WELL_PLAYED.getEmoteCode());
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE7]) {
            PacketSender.sendUseEmote(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.Emotes.GOOD_GAME.getEmoteCode());
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE8]) {
//            PacketSender.sendUseEmote(Core.getLogicModule().getSelectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.EMOTE_7);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE9]) {
//            PacketSender.sendUseEmote(Core.getLogicModule().getSelectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.EMOTE_8);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_EMOTE10]) {
//            PacketSender.sendUseEmote(Core.getLogicModule().getSelectedRoom(), Core.getLogicModule().getMyPlayerKey(), Globals.EMOTE_9);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SCOREBOARD]) {
            this.showScoreboard = false;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                PacketSender.sendDisconnect(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey());
                break;
        }
    }

    @Override
    public void mouseClicked(final MouseEvent e) {

    }

    @Override
    public void mousePressed(final MouseEvent e) {

    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        Point2D.Double scaled;
        if (Globals.WINDOW_SCALE_ENABLED) {
            scaled = new Point2D.Double(e.getX() / Globals.WINDOW_SCALE, e.getY() / Globals.WINDOW_SCALE);
        } else {
            scaled = new Point2D.Double(e.getX(), e.getY());
        }

        if (this.showMatchResult) {
            this.windows[0].mouseReleased(e);
        }

        if (this.logoutBox != null) {
            Rectangle2D.Double box = new Rectangle2D.Double(10, 10, this.logoutBox.getWidth() + 6, this.logoutBox.getHeight() + 6);
            if (box.contains(scaled)) {
                PacketSender.sendDisconnect(Core.getLogicModule().getConnectedRoom(), Core.getLogicModule().getMyPlayerKey());
            }
        }
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
        Point2D.Double scaled;
        if (Globals.WINDOW_SCALE_ENABLED) {
            scaled = new Point2D.Double(e.getX() / Globals.WINDOW_SCALE, e.getY() / Globals.WINDOW_SCALE);
        } else {
            scaled = new Point2D.Double(e.getX(), e.getY());
        }
        this.drawInfoHotkey = -1;
        for (int i = 0; i < this.hotkeySlots.length; i++) {
            if (this.hotkeySlots[i].contains(scaled) && this.c.getHotkeys()[i] != null) {
                this.drawInfoHotkey = i;
                return;
            }
        }
    }

    @Override
    public void unload() {
        Iterator<Entry<Integer, Particle>> particleIter = this.particles.entrySet().iterator();
        while (particleIter.hasNext()) {
            Entry<Integer, Particle> particle = particleIter.next();
            particle.getValue().setExpire();
            particleIter.remove();
            Particle.returnKey(particle.getKey());
        }

        Particle.unloadParticles();
        Emote.unloadEmotes();
        ItemEquip.unloadSprites();
        this.mobs.entrySet().forEach((mobEntry) -> {
            mobEntry.getValue().unload();
        });
    }

}
