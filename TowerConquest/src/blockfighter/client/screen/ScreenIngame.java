package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.SaveData;
import blockfighter.client.entities.ingamenumber.IngameNumber;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.items.ItemUpgrade;
import blockfighter.client.entities.mob.Mob;
import blockfighter.client.entities.notification.Notification;
import blockfighter.client.entities.particles.*;
import blockfighter.client.entities.player.Player;
import blockfighter.client.entities.player.skills.Skill;
import blockfighter.client.maps.GameMap;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Ken Kwan
 */
public class ScreenIngame extends Screen {

    private final Rectangle2D.Double[] hotkeySlots = new Rectangle2D.Double[12];
    // Ingame Data
    private final ConcurrentLinkedQueue<byte[]> dataQueue = new ConcurrentLinkedQueue<>();
    private final DecimalFormat df = new DecimalFormat("0.0");
    private final ConcurrentHashMap<Byte, Player> players;
    private final ConcurrentHashMap<Byte, Mob> mobs = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Particle> particles = new ConcurrentHashMap<>(500, 0.9f, 1);
    private final ConcurrentHashMap<Integer, IngameNumber> ingameNumber = new ConcurrentHashMap<>(500, 0.9f, 1);
    private final ConcurrentLinkedQueue<Notification> notifications = new ConcurrentLinkedQueue<>();

    private final ConcurrentLinkedQueue<Integer> ingameNumKeys = new ConcurrentLinkedQueue<>();
    private int numIngameNumKeys = 500;

    private double screenShakeX = 0, screenShakeY = 0;
    private boolean screenShake = false;

    private byte myKey = -1;
    private Rectangle2D logoutBox;

    private long pingTime = 0;
    private int ping = 0;
    private byte pingID = 0;

    private long lastUpdateTime = 0;
    private long lastRequestTime = 50;
    private long lastQueueTime = 0;
    private long lastPingTime = 0;
    private long lastSendKeyTime = 0;

    private final boolean[] moveKeyDown = {false, false, false, false};
    private final boolean[] skillKeyDown = new boolean[12];

    private final SaveData c;
    private final GameMap map;

    private int drawInfoHotkey = -1;

    public ScreenIngame(final byte i, final byte numPlayer, final GameMap m) {
        this.myKey = i;
        this.c = logic.getSelectedChar();
        this.players = new ConcurrentHashMap<>(numPlayer);
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
        logic.playBGM(this.map.getBGM());
    }

    @Override
    public void update() {
        final long now = logic.getTime(); // Get time now
        this.map.update();
        if (now - this.lastQueueTime >= Globals.QUEUES_UPDATE) {
            processDataQueue();
            this.lastQueueTime = now;
        }

        if (now - this.lastSendKeyTime >= Globals.SEND_KEYDOWN_UPDATE) {
            for (byte i = 0; i < this.moveKeyDown.length; i++) {
                logic.sendMoveKey(this.myKey, i, this.moveKeyDown[i]);
            }
            for (int i = 0; i < this.skillKeyDown.length; i++) {
                if (this.skillKeyDown[i]) {
                    logic.sendUseSkill(this.myKey, this.c.getHotkeys()[i].getSkillCode());
                }
            }
            this.lastSendKeyTime = now;
        }

        if (now - this.lastUpdateTime >= Globals.LOGIC_UPDATE) {
            updateIngameNumber();
            updateParticles(this.particles);
            updatePlayers();
            updateMobs();
            updateNotifications();

            if (this.players.containsKey(this.myKey)) {
                logic.setSoundLisenterPos(this.players.get(this.myKey).getX(), this.players.get(this.myKey).getY());
            }
            this.lastUpdateTime = now;
        }

        if (now - this.lastRequestTime >= Globals.REQUESTALL_UPDATE) {
            logic.sendGetAll(this.myKey);
            this.lastRequestTime = now;
        }
        if (now - this.lastPingTime >= Globals.PING_UPDATE) {
            this.pingID = (byte) (Globals.rng(256));
            this.pingTime = System.currentTimeMillis();
            logic.sendGetPing(this.myKey, this.pingID);
            this.lastPingTime = now;
        }
    }

    private void updateIngameNumber() {
        for (final Map.Entry<Integer, IngameNumber> pEntry : this.ingameNumber.entrySet()) {
            threadPool.execute(pEntry.getValue());
        }
        final LinkedList<Integer> remove = new LinkedList<>();
        for (final Map.Entry<Integer, IngameNumber> pEntry : this.ingameNumber.entrySet()) {
            try {
                pEntry.getValue().join();
                if (pEntry.getValue().isExpired()) {
                    remove.add(pEntry.getKey());
                }
            } catch (final InterruptedException ex) {
            }
        }
        removeIngameNumber(remove);
    }

    private void removeIngameNumber(final LinkedList<Integer> remove) {
        while (!remove.isEmpty()) {
            final int p = remove.pop();
            returnDmgKey(p);
            this.ingameNumber.remove(p);
        }
    }

    @Override
    public void updateParticles(final ConcurrentHashMap<Integer, Particle> updateParticles) {
        for (final Map.Entry<Integer, Particle> pEntry : updateParticles.entrySet()) {
            threadPool.execute(pEntry.getValue());
        }
        final LinkedList<Integer> remove = new LinkedList<>();
        for (final Map.Entry<Integer, Particle> pEntry : updateParticles.entrySet()) {
            try {
                pEntry.getValue().join();
                if (pEntry.getValue().isExpired()) {
                    remove.add(pEntry.getKey());
                }
            } catch (final InterruptedException ex) {
            }
        }
        removeParticles(updateParticles, remove);
    }

    private void removeParticles(final ConcurrentHashMap<Integer, Particle> removeParticles, final LinkedList<Integer> remove) {
        while (!remove.isEmpty()) {
            final int p = remove.pop();
            returnParticleKey(p);
            removeParticles.remove(p);
        }
    }

    public void spawnParticle() {
        final int key = getNextParticleKey();
    }

    private void updateNotifications() {
        for (Notification n : this.notifications) {
            threadPool.execute(n);
        }
        for (Notification n : this.notifications) {
            try {
                n.join();
            } catch (final InterruptedException ex) {
            }
        }
        if (this.notifications.peek() != null && this.notifications.peek().isExpired()) {
            this.notifications.remove();
        }
    }

    private void updateMobs() {
        for (final Map.Entry<Byte, Mob> pEntry : this.mobs.entrySet()) {
            threadPool.execute(pEntry.getValue());
        }
        for (final Map.Entry<Byte, Mob> pEntry : this.mobs.entrySet()) {
            try {
                pEntry.getValue().join();
            } catch (final InterruptedException ex) {
            }
        }
    }

    private void updatePlayers() {
        for (final Map.Entry<Byte, Player> pEntry : this.players.entrySet()) {
            threadPool.execute(pEntry.getValue());
        }
        final LinkedList<Byte> remove = new LinkedList<>();
        for (final Map.Entry<Byte, Player> pEntry : this.players.entrySet()) {
            try {
                pEntry.getValue().join();
                if (pEntry.getValue().isDisconnected()) {
                    remove.add(pEntry.getKey());
                }
            } catch (final InterruptedException ex) {
            }
        }
        while (!remove.isEmpty()) {
            this.players.remove(remove.poll());
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        g.setClip(0, 0, 1280, 720);
        final AffineTransform resetForm = g.getTransform();
        if (this.players != null && this.myKey != -1 && this.players.containsKey(this.myKey)) {
            this.map.drawBg(g, this.players.get(this.myKey).getX(), this.players.get(this.myKey).getY());
            double scale = 1;
            g.scale(scale, scale);
            g.translate(640.0 / scale - this.players.get(this.myKey).getX(), 500.0 / scale - this.players.get(this.myKey).getY());
            if (screenShake) {
                g.translate(screenShakeX, screenShakeY);
            }
        } else {
            this.map.drawBg(g, 0, 0);
        }

        this.map.draw(g);

        if (this.mobs != null) {
            for (final Map.Entry<Byte, Mob> pEntry : this.mobs.entrySet()) {
                pEntry.getValue().draw(g);
            }
        }
        if (this.players != null) {
            for (final Map.Entry<Byte, Player> pEntry : this.players.entrySet()) {
                pEntry.getValue().draw(g);
            }
        }
        for (final Map.Entry<Integer, Particle> pEntry : this.particles.entrySet()) {
            pEntry.getValue().draw(g);
        }
        for (final Map.Entry<Integer, IngameNumber> pEntry : this.ingameNumber.entrySet()) {
            pEntry.getValue().draw(g);
        }

        g.setTransform(resetForm);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        final BufferedImage hud = Globals.HUD[0];
        g.drawImage(hud, Globals.WINDOW_WIDTH / 2 - hud.getWidth() / 2, Globals.WINDOW_HEIGHT - hud.getHeight(), null);
        
        drawHUD(g, hud);
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
    }

    private void drawNotifications(final Graphics2D g) {
        int x = 10, y = 710 + g.getFontMetrics(Globals.ARIAL_15PT).getHeight() - this.notifications.size() * (5 + g.getFontMetrics(Globals.ARIAL_15PT).getHeight());
        for (Notification n : this.notifications) {
            n.draw(g, x, y);
            y += (5 + g.getFontMetrics(Globals.ARIAL_15PT).getHeight());
        }
    }

    private void drawHotkeys(final Graphics2D g) {
        final Skill[] hotkey = this.c.getHotkeys();
        for (int j = 0; j < this.hotkeySlots.length; j++) {
            if (hotkey[j] != null) {
                hotkey[j].draw(g, (int) this.hotkeySlots[j].x, (int) this.hotkeySlots[j].y);
                g.setColor(new Color(100, 100, 100, 125));
                final int cdHeight = (int) ((hotkey[j].getCooldown() / hotkey[j].getMaxCooldown()) * this.hotkeySlots[j].height);
                g.fillRect((int) this.hotkeySlots[j].x, (int) (this.hotkeySlots[j].y + this.hotkeySlots[j].height - cdHeight),
                        (int) this.hotkeySlots[j].width,
                        cdHeight);
                if (hotkey[j].getCooldown() > 0) {
                    g.setFont(Globals.ARIAL_18PT);
                    final int width = g.getFontMetrics().stringWidth(this.df.format(hotkey[j].getCooldown() / 1000D));
                    drawStringOutline(g, this.df.format(hotkey[j].getCooldown() / 1000D), (int) this.hotkeySlots[j].x + 28 - width / 2,
                            (int) this.hotkeySlots[j].y + 33, 1);
                    g.setColor(Color.white);
                    g.drawString(this.df.format(hotkey[j].getCooldown() / 1000D), (int) this.hotkeySlots[j].x + 28 - width / 2,
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

    private void drawHUD(final Graphics2D g, final BufferedImage hud) {
        if (this.players.containsKey(this.myKey)) {
            final BufferedImage hpbar = Globals.HUD[1];
            final double hpbarWidth;

            if (this.players.get(this.myKey).getStat(Globals.STAT_MINHP) <= this.players.get(this.myKey).getStat(Globals.STAT_MAXHP)) {
                hpbarWidth = this.players.get(this.myKey).getStat(Globals.STAT_MINHP) / this.players.get(this.myKey).getStat(Globals.STAT_MAXHP);
            } else {
                hpbarWidth = 1;
            }

            g.drawImage(hpbar,
                    Globals.WINDOW_WIDTH / 2 - hud.getWidth() / 2 + 2, Globals.WINDOW_HEIGHT - hud.getHeight() + 2,
                    (int) (hpbarWidth * 802D), 38, null);
            g.setFont(Globals.ARIAL_18PT);
            final int width = g.getFontMetrics().stringWidth(
                    (int) this.players.get(this.myKey).getStat(Globals.STAT_MINHP) + "/"
                    + (int) this.players.get(this.myKey).getStat(Globals.STAT_MAXHP));
            drawStringOutline(g,
                    (int) this.players.get(this.myKey).getStat(Globals.STAT_MINHP) + "/"
                    + (int) this.players.get(this.myKey).getStat(Globals.STAT_MAXHP),
                    Globals.WINDOW_WIDTH / 2 - width / 2, Globals.WINDOW_HEIGHT - hud.getHeight() + 28, 1);
            g.setColor(Color.WHITE);
            g.drawString((int) this.players.get(this.myKey).getStat(Globals.STAT_MINHP) + "/"
                    + (int) this.players.get(this.myKey).getStat(Globals.STAT_MAXHP),
                    Globals.WINDOW_WIDTH / 2 - width / 2, Globals.WINDOW_HEIGHT - hud.getHeight() + 28);
        }
    }

    private void drawSkillInfo(final Graphics2D g, final Rectangle2D.Double box, final Skill skill) {
        skill.drawInfo(g, (int) box.x, (int) box.y);
    }

    @Override
    public ConcurrentHashMap<Integer, Particle> getParticles() {
        return this.particles;
    }

    public Point getPlayerPos(final byte key) {
        if (!this.players.containsKey(key)) {
            return null;
        }
        return new Point(this.players.get(key).getX(), this.players.get(key).getY());
    }

    public void returnDmgKey(final int key) {
        this.ingameNumKeys.add(key);
    }

    public int getNextIngameNumKey() {
        if (this.ingameNumKeys.isEmpty()) {
            for (int i = this.numIngameNumKeys; i < this.numIngameNumKeys + 500; i++) {
                this.ingameNumKeys.add(i);
            }
            this.numIngameNumKeys += 500;
        }
        return this.ingameNumKeys.remove();
    }

    private void processDataQueue() {
        while (this.players != null && !this.dataQueue.isEmpty()) {
            final byte[] data = this.dataQueue.remove();
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
            }
        }
    }

    private void dataSoundEffect(final byte[] data) {
        final byte sfxID = data[1];
        final int x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
        final int y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
        logic.playSound(sfxID, x, y);
    }

    private void dataPlayerGiveDrop(final byte[] data) {
        final int lvl = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
        final int dropCode = Globals.bytesToInt(Arrays.copyOfRange(data, 5, 9));
        //TODO - Add drop gained notification
        if (ItemEquip.isValidItem(dropCode)) {
            this.notifications.add(new Notification(dropCode, Globals.NOTIFICATION_ITEMEQUIP));
        }
        if (ItemUpgrade.isValidItem(dropCode)) {
            this.notifications.add(new Notification(dropCode, Globals.NOTIFICATION_ITEMUPGRADE));
        }
        this.c.addDrops(lvl, dropCode);
    }

    private void dataPlayerGiveEXP(final byte[] data) {
        final int amount = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
        this.notifications.add(new Notification(amount, Globals.NOTIFICATION_EXP));
        this.c.addExp(amount);
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
        final byte key = data[1];
        final int x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
        final int y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
        final byte facing = data[10];
        spawnPlayer(key, x, y);
        this.players.get(key).setPos(x, y);
        this.players.get(key).setFacing(facing);
    }

    private void dataPlayerSetFacing(final byte[] data) {
        final byte key = data[1];
        final byte facing = data[2];
        spawnPlayer(key);
        this.players.get(key).setFacing(facing);
    }

    private void dataPlayerSetState(final byte[] data) {
        final byte key = data[1];
        spawnPlayer(key);
        final byte state = data[2];
        final byte frame = data[3];
        this.players.get(key).setState(state);
        this.players.get(key).setFrame(frame);
    }

    private void dataPlayerDisconnect(final byte[] data) {
        final byte key = data[1];
        if (this.players.containsKey(key) && key != this.myKey) {
            this.players.get(key).disconnect();
        }
    }

    private void dataIngameNumber(final byte[] data) {
        final byte type = data[1];
        final int x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
        final int y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
        final int value = Globals.bytesToInt(Arrays.copyOfRange(data, 10, 14));
        final int key = getNextIngameNumKey();
        this.ingameNumber.put(key, new IngameNumber(value, type, new Point(x, y)));
    }

    private void dataParticleEffect(final byte[] data) {
        final byte particleID = data[1];
        int x, y;
        byte facing, playerKey;

        switch (particleID) {
            case Globals.PARTICLE_SWORD_SLASH1:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleSwordSlash1(x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_SLASH2:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleSwordSlash2(x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_SLASH3:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleSwordSlash3(x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_GASH1:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleSwordGash(x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_GASH2:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleSwordGash2(x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_GASH3:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleSwordGash3(x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_GASH4:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleSwordGash4(x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_VORPAL:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleSwordVorpal(x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_PHANTOM:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleSwordPhantom(x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_PHANTOM2:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleSwordPhantom2(x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_MULTI:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleSwordMulti(x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_CINDER:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleSwordCinder(x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_TAUNT:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleSwordTaunt(x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_TAUNTAURA1:
                playerKey = data[2];
                if (this.players.containsKey(playerKey)) {
                    addParticle(new ParticleSwordTauntAura(this.players.get(playerKey)));
                }
                break;
            case Globals.PARTICLE_BOW_ARC:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleBowArc(x, y, facing));
                break;
            case Globals.PARTICLE_BOW_RAPID:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleBowRapid(x, y, facing));
                break;
            case Globals.PARTICLE_BOW_RAPID2:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleBowRapid2(x, y, facing));
                break;
            case Globals.PARTICLE_BOW_POWER:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleBowPower(x, y, facing));
                break;
            case Globals.PARTICLE_BOW_POWERCHARGE:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleBowPowerCharge(x, y, facing));
                break;
            case Globals.PARTICLE_BOW_VOLLEYBOW:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleBowVolleyBow(x, y, facing));
                break;
            case Globals.PARTICLE_BOW_VOLLEYARROW:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleBowVolleyArrow(x, y, facing));
                break;
            case Globals.PARTICLE_BOW_STORM:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleBowStormEmitter(x, y, facing));
                break;
            case Globals.PARTICLE_BOW_FROSTARROW:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleBowFrostArrow(x, y, facing));
                break;
            case Globals.PARTICLE_SHIELD_DASH:
                facing = data[2];
                playerKey = data[3];
                if (this.players.containsKey(playerKey)) {
                    addParticle(new ParticleShieldDashEmitter(facing, this.players.get(playerKey)));
                }
                break;
            case Globals.PARTICLE_SHIELD_FORTIFY:
                playerKey = data[2];
                if (this.players.containsKey(playerKey)) {
                    addParticle(new ParticleShieldFortify(this.players.get(playerKey)));
                }
                break;
            case Globals.PARTICLE_SHIELD_CHARGE:
                facing = data[2];
                playerKey = data[3];
                if (this.players.containsKey(playerKey)) {
                    addParticle(new ParticleShieldCharge(facing, this.players.get(playerKey)));
                }
                break;
            case Globals.PARTICLE_SHIELD_REFLECTCAST:
                playerKey = data[2];
                if (this.players.containsKey(playerKey)) {
                    addParticle(new ParticleShieldReflectCast(this.players.get(playerKey)));
                }
                break;
            case Globals.PARTICLE_SHIELD_REFLECTBUFF:
                playerKey = data[2];
                if (this.players.containsKey(playerKey)) {
                    addParticle(new ParticleShieldReflectEmitter(this.players.get(playerKey)));
                }
                break;
            case Globals.PARTICLE_SHIELD_REFLECTHIT:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                addParticle(new ParticleShieldReflectHit(x, y));
                break;
            case Globals.PARTICLE_SHIELD_IRON:
                playerKey = data[2];
                if (this.players.containsKey(playerKey)) {
                    addParticle(new ParticleShieldIron(this.players.get(playerKey)));
                }
                break;
            case Globals.PARTICLE_SHIELD_IRONALLY:
                playerKey = data[2];
                if (this.players.containsKey(playerKey)) {
                    addParticle(new ParticleShieldIronAlly(this.players.get(playerKey)));
                }
                break;
            case Globals.PARTICLE_SHIELD_FORTIFYBUFF:
                playerKey = data[2];
                if (this.players.containsKey(playerKey)) {
                    addParticle(new ParticleShieldFortifyEmitter(this.players.get(playerKey)));
                }
                break;
            case Globals.PARTICLE_SHIELD_TOSS:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                facing = data[10];
                addParticle(new ParticleShieldToss(x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_TAUNTBUFF:
                playerKey = data[2];
                if (this.players.containsKey(playerKey)) {
                    addParticle(new ParticleSwordTauntBuffEmitter(this.players.get(playerKey)));
                }
                break;
            case Globals.PARTICLE_SWORD_SLASHBUFF:
                playerKey = data[2];
                if (this.players.containsKey(playerKey)) {
                    addParticle(new ParticleSwordSlashBuffEmitter(this.players.get(playerKey)));
                }
                break;
            case Globals.PARTICLE_SHIELD_DASHBUFF:
                playerKey = data[2];
                if (this.players.containsKey(playerKey)) {
                    addParticle(new ParticleShieldDashBuffEmitter(this.players.get(playerKey)));
                }
                break;
            case Globals.PARTICLE_BOW_VOLLEYBUFF:
                playerKey = data[2];
                if (this.players.containsKey(playerKey)) {
                    addParticle(new ParticleBowVolleyBuffEmitter(this.players.get(playerKey)));
                }
                break;
            case Globals.PARTICLE_BURN:
                playerKey = data[2];
                if (this.players.containsKey(playerKey)) {
                    addParticle(new ParticleBurnBuffEmitter(this.players.get(playerKey)));
                }
                break;
            case Globals.PARTICLE_PASSIVE_RESIST:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                addParticle(new ParticlePassiveResist(x, y));
                break;
            case Globals.PARTICLE_PASSIVE_BARRIER:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                addParticle(new ParticlePassiveBarrier(x, y));
                break;
            case Globals.PARTICLE_PASSIVE_SHADOWATTACK:
                x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
                y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
                addParticle(new ParticlePassiveShadowAttack(x, y));
                break;
            case Globals.PARTICLE_BLOOD:
                playerKey = data[2];
                if (this.players.containsKey(playerKey)) {
                    addParticle(new ParticleBloodEmitter(this.players.get(playerKey)));
                }
                break;
            case Globals.PARTICLE_BLOOD_HIT:
                playerKey = data[2];
                byte sourceKey = data[3];
                if (this.players.containsKey(playerKey)) {
                    addParticle(new ParticleBloodEmitter(this.players.get(playerKey), this.players.get(sourceKey), true));
                }
                break;
        }
    }

    private void dataPlayerGetName(final byte[] data) {
        final byte key = data[1];
        spawnPlayer(key);
        final byte[] temp = new byte[Globals.MAX_NAME_LENGTH];
        System.arraycopy(data, 2, temp, 0, temp.length);
        this.players.get(key).setPlayerName(new String(temp, StandardCharsets.UTF_8).trim());
    }

    private void dataMobParticleEffect(final byte[] data) {
        final byte key = data[1];
        if (this.mobs.containsKey(key)) {
            this.mobs.get(key).addParticle(data);
        }
    }

    private void dataMobSetType(final byte[] data) {
        final byte key = data[1], type = data[2];
        if (!this.mobs.containsKey(key)) {
            final int x = Globals.bytesToInt(Arrays.copyOfRange(data, 3, 7));
            final int y = Globals.bytesToInt(Arrays.copyOfRange(data, 7, 11));
            this.mobs.put(key, Mob.spawnMob(type, key, x, y));
        }
    }

    private void dataMobSetPos(final byte[] data) {
        final byte key = data[1];
        if (isExistingMob(key)) {
            final int x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
            final int y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
            this.mobs.get(key).setPos(x, y);
        }

    }

    private void dataMobSetFacing(final byte[] data) {
        final byte key = data[1];
        if (isExistingMob(key)) {
            final byte facing = data[2];
            this.mobs.get(key).setFacing(facing);
        }
    }

    private void dataMobSetState(final byte[] data) {
        final byte key = data[1];
        if (isExistingMob(key)) {
            final byte state = data[2];
            final byte frame = data[3];
            this.mobs.get(key).setState(state);
            this.mobs.get(key).setFrame(frame);
        }
    }

    private void dataMobGetStat(final byte[] data) {
        final byte key = data[1];
        if (isExistingMob(key)) {
            final byte stat = data[2];
            final int amount = Globals.bytesToInt(Arrays.copyOfRange(data, 3, 7));
            this.mobs.get(key).setStat(stat, amount);
        }
    }

    public void addDmgNum(final IngameNumber d) {
        this.ingameNumber.put(getNextIngameNumKey(), d);
    }

    @Override
    public void addParticle(final Particle newParticle) {
        this.particles.put(newParticle.getKey(), newParticle);
    }

    public void queueData(final byte[] data) {
        this.dataQueue.add(data);
    }

    public void disconnect() {
        logic.sendDisconnect(this.myKey);
    }

    public void setPing(final byte rID) {
        if (rID != this.pingID) {
            return;
        }
        this.ping = (int) (System.currentTimeMillis() - this.pingTime);
        if (this.ping >= 1000) {
            this.ping = 9999;
        }
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

    private boolean isExistingMob(final byte key) {
        if (!this.mobs.containsKey(key)) {
            logic.sendSetMobType(key);
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
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_DOWN]) {
            setKeyDown(Globals.DOWN, true);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_LEFT]) {
            setKeyDown(Globals.LEFT, true);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_RIGHT]) {
            setKeyDown(Globals.RIGHT, true);
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
        }
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        final int key = e.getKeyCode();
        if (key == this.c.getKeyBind()[Globals.KEYBIND_JUMP]) {
            setKeyDown(Globals.UP, false);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_DOWN]) {
            setKeyDown(Globals.DOWN, false);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_LEFT]) {
            setKeyDown(Globals.LEFT, false);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_RIGHT]) {
            setKeyDown(Globals.RIGHT, false);
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL1]) {
            if (this.c.getHotkeys()[0] != null) {
                setSkillKeyDown(0, false);
                logic.sendUseSkill(this.myKey, this.c.getHotkeys()[0].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL2]) {
            if (this.c.getHotkeys()[1] != null) {
                setSkillKeyDown(1, false);
                logic.sendUseSkill(this.myKey, this.c.getHotkeys()[1].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL3]) {
            if (this.c.getHotkeys()[2] != null) {
                setSkillKeyDown(2, false);
                logic.sendUseSkill(this.myKey, this.c.getHotkeys()[2].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL4]) {
            if (this.c.getHotkeys()[3] != null) {
                setSkillKeyDown(3, false);
                logic.sendUseSkill(this.myKey, this.c.getHotkeys()[3].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL5]) {
            if (this.c.getHotkeys()[4] != null) {
                setSkillKeyDown(4, false);
                logic.sendUseSkill(this.myKey, this.c.getHotkeys()[4].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL6]) {
            if (this.c.getHotkeys()[5] != null) {
                setSkillKeyDown(5, false);
                logic.sendUseSkill(this.myKey, this.c.getHotkeys()[5].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL7]) {
            if (this.c.getHotkeys()[6] != null) {
                setSkillKeyDown(6, false);
                logic.sendUseSkill(this.myKey, this.c.getHotkeys()[6].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL8]) {
            if (this.c.getHotkeys()[7] != null) {
                setSkillKeyDown(7, false);
                logic.sendUseSkill(this.myKey, this.c.getHotkeys()[7].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL9]) {
            if (this.c.getHotkeys()[8] != null) {
                setSkillKeyDown(8, false);
                logic.sendUseSkill(this.myKey, this.c.getHotkeys()[8].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL10]) {
            if (this.c.getHotkeys()[9] != null) {
                setSkillKeyDown(9, false);
                logic.sendUseSkill(this.myKey, this.c.getHotkeys()[9].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL11]) {
            if (this.c.getHotkeys()[10] != null) {
                setSkillKeyDown(10, false);
                logic.sendUseSkill(this.myKey, this.c.getHotkeys()[10].getSkillCode());
            }
        } else if (key == this.c.getKeyBind()[Globals.KEYBIND_SKILL12]) {
            if (this.c.getHotkeys()[11] != null) {
                setSkillKeyDown(11, false);
                logic.sendUseSkill(this.myKey, this.c.getHotkeys()[11].getSkillCode());
            }
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                logic.sendDisconnect(this.myKey);
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
        Rectangle2D.Double box = new Rectangle2D.Double(10, 10, this.logoutBox.getWidth() + 6, this.logoutBox.getHeight() + 6);
        if (box.contains(e.getPoint())) {
            logic.sendDisconnect(this.myKey);
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
        this.drawInfoHotkey = -1;
        for (int i = 0; i < this.hotkeySlots.length; i++) {
            if (this.hotkeySlots[i].contains(e.getPoint()) && this.c.getHotkeys()[i] != null) {
                this.drawInfoHotkey = i;
                return;
            }
        }
    }

    @Override
    public void unload() {
        final LinkedList<Integer> remove = new LinkedList<>();
        for (final Map.Entry<Integer, Particle> pEntry : this.particles.entrySet()) {
            pEntry.getValue().setExpire();
            remove.add(pEntry.getKey());
        }
        removeParticles(this.particles, remove);

        Particle.unloadParticles();
        ItemEquip.unloadSprites();
        for (final Map.Entry<Byte, Mob> mobEntry : this.mobs.entrySet()) {
            mobEntry.getValue().unload();
        }

    }

}
