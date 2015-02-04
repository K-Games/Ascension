package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.SaveData;
import blockfighter.client.entities.Player;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.particles.*;
import blockfighter.client.entities.skills.Skill;
import blockfighter.client.maps.GameMap;
import blockfighter.client.maps.GameMapLvl1;
import blockfighter.client.net.PacketSender;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ken Kwan
 */
public class ScreenIngame extends Screen {

    private Rectangle2D.Double[] hotkeySlots = new Rectangle2D.Double[12];
    //Ingame Data
    private ConcurrentLinkedQueue<byte[]> dataQueue = new ConcurrentLinkedQueue<>();
    private DecimalFormat df = new DecimalFormat("0.0");
    private ConcurrentHashMap<Byte, Player> players;
    private ConcurrentHashMap<Integer, Particle> particles = new ConcurrentHashMap<>(500);
    private final ConcurrentLinkedQueue<Integer> particleKeys = new ConcurrentLinkedQueue<>();
    private int numParticleKeys = 500;

    private byte myKey = -1;

    private long pingTime = 0;
    private int ping = 0;
    private byte pID = 0;
    private PacketSender sender = null;

    private double lastUpdateTime = 0;
    private double lastRequestTime = 50;
    private double lastQueueTime = 0;
    private double lastPingTime = 0;
    private double lastSendKeyTime = 0;

    private boolean[] keyDownMove = {false, false, false, false};

    private LogicModule logic;
    private SaveData c;
    private GameMap map;

    private int drawInfoHotkey = -1;

    public ScreenIngame(LogicModule l, byte i, byte numPlayer, PacketSender s) {
        logic = l;
        myKey = i;
        c = logic.getSelectedChar();
        players = new ConcurrentHashMap<>(numPlayer);
        sender = s;
        for (int j = 0; j < hotkeySlots.length; j++) {
            hotkeySlots[j] = new Rectangle2D.Double(Globals.WINDOW_WIDTH / 2 - Globals.HUD[0].getWidth() / 2 + 10 + (j * 66), 656, 60, 60);
        }
        for (int key = 0; key < numParticleKeys; key++) {
            particleKeys.add(key);
        }
        Skill[] skills = c.getSkills();
        for (Skill skill : skills) {
            if (skill != null) {
                skill.resetCooldown();
            }
        }
        map = new GameMapLvl1();
        Particle.loadParticles();
    }

    @Override
    public void update() {
        double now = System.nanoTime(); //Get time now

        map.update();
        if (now - lastQueueTime >= Globals.QUEUES_UPDATE) {
            processDataQueue();
            lastQueueTime = now;
        }

        if (now - lastSendKeyTime >= Globals.SEND_KEYDOWN_UPDATE) {
            sender.sendMove(logic.getSelectedRoom(), myKey, Globals.UP, keyDownMove[Globals.UP]);
            sender.sendMove(logic.getSelectedRoom(), myKey, Globals.DOWN, keyDownMove[Globals.DOWN]);
            sender.sendMove(logic.getSelectedRoom(), myKey, Globals.LEFT, keyDownMove[Globals.LEFT]);
            sender.sendMove(logic.getSelectedRoom(), myKey, Globals.RIGHT, keyDownMove[Globals.RIGHT]);
            lastSendKeyTime = now;
        }

        if (now - lastUpdateTime >= Globals.LOGIC_UPDATE) {
            Skill[] skills = c.getSkills();
            for (Skill skill : skills) {
                if (skill != null) {
                    skill.reduceCooldown((long) (Globals.LOGIC_UPDATE / 1000000));
                }
            }
            updateParticles(particles);
            updatePlayers();
            lastUpdateTime = now;
        }

        if (now - lastRequestTime >= Globals.REQUESTALL_UPDATE) {
            sender.sendGetAll(logic.getSelectedRoom(), myKey);
            lastRequestTime = now;
        }
        if (now - lastPingTime >= Globals.PING_UPDATE) {
            pID = (byte) (Math.random() * 256);
            pingTime = System.currentTimeMillis();
            sender.sendGetPing(pID, logic.getSelectedRoom(), myKey);
            lastPingTime = now;
        }

        try {
            Thread.sleep(0, 1);
        } catch (InterruptedException ex) {
            Logger.getLogger(ScreenInventory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void updateParticles(ConcurrentHashMap<Integer, Particle> particles) {
        for (Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
            threadPool.execute(pEntry.getValue());
        }
        LinkedList<Integer> remove = new LinkedList<>();
        for (Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
            try {
                pEntry.getValue().join();
                if (pEntry.getValue().isExpired()) {
                    remove.add(pEntry.getKey());
                }
            } catch (InterruptedException ex) {
            }
        }
        removeParticles(particles, remove);
    }

    private void removeParticles(ConcurrentHashMap<Integer, Particle> particles, LinkedList<Integer> remove) {
        while (!remove.isEmpty()) {
            int p = remove.pop();
            returnParticleKey(p);
            particles.remove(p);
        }
    }

    private void updatePlayers() {
        for (Map.Entry<Byte, Player> pEntry : players.entrySet()) {
            threadPool.execute(pEntry.getValue());
        }
        LinkedList<Byte> remove = new LinkedList<>();
        for (Map.Entry<Byte, Player> pEntry : players.entrySet()) {
            try {
                pEntry.getValue().join();
                if (pEntry.getValue().isDisconnected()) {
                    remove.add(pEntry.getKey());
                }
            } catch (InterruptedException ex) {
            }
        }
        while (!remove.isEmpty()) {
            players.remove(remove.poll());
        }
    }

    @Override
    public void draw(Graphics2D g) {
        AffineTransform resetForm = g.getTransform();

        if (players != null && myKey != -1 && players.containsKey(myKey)) {
            g.translate(640.0 - players.get(myKey).getX(), 500.0 - players.get(myKey).getY());
        }
        map.draw(g);
        if (players != null) {
            for (Map.Entry<Byte, Player> pEntry : players.entrySet()) {
                pEntry.getValue().draw(g);
            }
        }
        for (Map.Entry<Integer, Particle> pEntry : particles.entrySet()) {
            pEntry.getValue().draw(g);
        }

        g.setTransform(resetForm);
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        BufferedImage hud = Globals.HUD[0];
        g.drawImage(hud, Globals.WINDOW_WIDTH / 2 - hud.getWidth() / 2, Globals.WINDOW_HEIGHT - hud.getHeight(), null);
        if (players.containsKey(myKey)) {
            BufferedImage hpbar = Globals.HUD[1];
            g.drawImage(hpbar,
                    Globals.WINDOW_WIDTH / 2 - hud.getWidth() / 2 + 2, Globals.WINDOW_HEIGHT - hud.getHeight() + 2,
                    (int) (players.get(myKey).getStat(Globals.STAT_MINHP) / players.get(myKey).getStat(Globals.STAT_MAXHP) * 802D), 38,
                    null);

            int width = g.getFontMetrics().stringWidth((int) players.get(myKey).getStat(Globals.STAT_MINHP) + "/" + (int) players.get(myKey).getStat(Globals.STAT_MAXHP));
            drawStringOutline(g, (int) players.get(myKey).getStat(Globals.STAT_MINHP) + "/" + (int) players.get(myKey).getStat(Globals.STAT_MAXHP), Globals.WINDOW_WIDTH / 2 - width / 2, Globals.WINDOW_HEIGHT - hud.getHeight() + 28, 1);
            g.setColor(Color.WHITE);
            g.drawString((int) players.get(myKey).getStat(Globals.STAT_MINHP) + "/" + (int) players.get(myKey).getStat(Globals.STAT_MAXHP), Globals.WINDOW_WIDTH / 2 - width / 2, Globals.WINDOW_HEIGHT - hud.getHeight() + 28);
        }
        Skill[] hotkey = c.getHotkeys();
        for (int j = 0; j < hotkeySlots.length; j++) {
            if (hotkey[j] != null) {
                hotkey[j].draw(g, (int) hotkeySlots[j].x, (int) hotkeySlots[j].y);
                g.setColor(new Color(100, 100, 100, 125));
                int cdHeight = (int) ((hotkey[j].getCooldown() / hotkey[j].getMaxCooldown()) * hotkeySlots[j].height);
                g.fillRect((int) hotkeySlots[j].x, (int) (hotkeySlots[j].y + hotkeySlots[j].height - cdHeight), (int) hotkeySlots[j].width, cdHeight);
                if (hotkey[j].getCooldown() > 0) {
                    g.setFont(Globals.ARIAL_18PT);
                    int width = g.getFontMetrics().stringWidth(df.format(hotkey[j].getCooldown() / 1000D));
                    drawStringOutline(g, df.format(hotkey[j].getCooldown() / 1000D), (int) hotkeySlots[j].x + 28 - width / 2, (int) hotkeySlots[j].y + 33, 1);
                    g.setColor(Color.white);
                    g.drawString(df.format(hotkey[j].getCooldown() / 1000D), (int) hotkeySlots[j].x + 28 - width / 2, (int) hotkeySlots[j].y + 33);
                }
            }
            g.setFont(Globals.ARIAL_15PT);
            String key = "?";
            if (c.getKeyBind()[j] != -1) {
                key = KeyEvent.getKeyText(c.getKeyBind()[j]);
            }
            int width = g.getFontMetrics().stringWidth(key);
            drawStringOutline(g, key, (int) hotkeySlots[j].x + 58 - width, (int) hotkeySlots[j].y + 58, 1);
            g.setColor(Color.WHITE);
            g.drawString(key, (int) hotkeySlots[j].x + 58 - width, (int) hotkeySlots[j].y + 58);

        }
        if (drawInfoHotkey != -1) {
            drawSkillInfo(g, hotkeySlots[drawInfoHotkey], c.getHotkeys()[drawInfoHotkey]);
        }
        g.setColor(new Color(25, 25, 25, 150));
        g.fillRect(1210, 5, 65, 45);
        g.setFont(Globals.ARIAL_12PT);
        g.setColor(Color.WHITE);
        g.drawString("Ping: " + ping, 1220, 40);
    }

    private void drawSkillInfo(Graphics2D g, Rectangle2D.Double box, Skill skill) {
        skill.drawInfo(g, (int) box.x, (int) box.y);
    }

    @Override
    public ConcurrentHashMap<Integer, Particle> getParticles() {
        return particles;
    }

    public Point getPlayerPos(byte key) {
        if (!players.containsKey(key)) {
            return null;
        }
        return new Point(players.get(key).getX(), players.get(key).getY());
    }

    public void returnParticleKey(int key) {
        particleKeys.add(key);
    }

    public int getNextParticleKey() {
        if (particleKeys.isEmpty()) {
            for (int i = numParticleKeys; i < numParticleKeys + 500; i++) {
                particleKeys.add(i);
            }
            numParticleKeys += 500;
        }
        return particleKeys.remove();
    }

    private void processDataQueue() {
        while (players != null && !dataQueue.isEmpty()) {
            byte[] data = dataQueue.remove();
            byte dataType = data[0];
            switch (dataType) {
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
            }
        }
    }

    private void dataPlayerSetCooldown(byte[] data) {
        c.getSkills()[data[1]].setCooldown();
    }

    private void dataPlayerGetEquip(byte[] data) {
        byte key = data[1];
        if (players.containsKey(key)) {
            for (byte i = 0; i < Globals.NUM_EQUIP_SLOTS; i++) {
                byte[] temp = new byte[4];
                System.arraycopy(data, i * 4 + 2, temp, 0, temp.length);
                players.get(key).setEquip(i, Globals.bytesToInt(temp));
            }
        }
    }

    private void dataPlayerGetStat(byte[] data) {
        byte key = data[1];
        if (players.containsKey(key)) {
            byte stat = data[2];
            int amount = Globals.bytesToInt(Arrays.copyOfRange(data, 3, 7));
            players.get(key).setStat(stat, amount);
        }
    }

    private void dataPlayerSetPos(byte[] data) {
        byte key = data[1];
        int x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
        int y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
        if (players.containsKey(key)) {
            players.get(key).setPos(x, y);
        } else {
            players.put(key, new Player(x, y, logic, key));
        }
    }

    private void dataPlayerSetFacing(byte[] data) {
        byte key = data[1];
        byte facing = data[2];
        if (players.containsKey(key)) {
            players.get(key).setFacing(facing);
        }
    }

    private void dataPlayerSetState(byte[] data) {
        byte key = data[1];
        if (players.containsKey(key)) {
            byte state = data[2];
            byte frame = data[3];
            players.get(key).setState(state);
            players.get(key).setFrame(frame);
        }
    }

    private void dataPlayerDisconnect(byte[] data) {
        byte key = data[1];
        if (players.containsKey(key)) {
            players.remove(key);
        }
    }

    private void dataParticleEffect(byte[] data) {
        byte particleID = data[1];
        int x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
        int y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
        byte facing = data[10];

        int key = getNextParticleKey();
        byte playerKey;
        switch (particleID) {
            case Globals.PARTICLE_SWORD_SLASH1:
                particles.put(key, new ParticleSwordSlash1(logic, key, x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_SLASH2:
                particles.put(key, new ParticleSwordSlash2(logic, key, x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_SLASH3:
                particles.put(key, new ParticleSwordSlash3(logic, key, x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_DRIVE:
                playerKey = data[11];
                particles.put(key, new ParticleSwordDrive(logic, key, x, y, facing, playerKey));
                break;
            case Globals.PARTICLE_SWORD_VORPAL:
                particles.put(key, new ParticleSwordVorpal(logic, key, x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_MULTI:
                particles.put(key, new ParticleSwordMulti(logic, key, x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_CINDER:
                particles.put(key, new ParticleSwordCinder(logic, key, x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_TAUNT:
                particles.put(key, new ParticleSwordTaunt(logic, key, x, y, facing));
                break;
            case Globals.PARTICLE_SWORD_TAUNTAURA1:
                playerKey = data[11];
                particles.put(key, new ParticleSwordTauntAura(logic, key, x, y, facing, playerKey));
                break;
        }
    }

    private void dataPlayerGetName(byte[] data) {
        byte key = data[1];
        if (players.containsKey(key)) {
            byte[] temp = new byte[Globals.MAX_NAME_LENGTH];
            System.arraycopy(data, 2, temp, 0, temp.length);
            players.get(key).setPlayerName(new String(temp, StandardCharsets.UTF_8).trim());
        }
    }

    public void addParticle(Particle newP) {
        particles.put(newP.getKey(), newP);
    }

    public void queueData(byte[] data) {
        dataQueue.add(data);
    }

    public void disconnect() {
        logic.sendDisconnect(myKey);
    }

    public void setPing(byte rID) {
        if (rID != pID) {
            return;
        }
        ping = (int) (System.currentTimeMillis() - pingTime);
        if (ping >= 1000) {
            ping = 9999;
        }
    }

    public void setKeyDown(int direction, boolean move) {
        keyDownMove[direction] = move;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == c.getKeyBind()[Globals.KEYBIND_JUMP]) {
            setKeyDown(Globals.UP, true);
        } else if (key == c.getKeyBind()[Globals.KEYBIND_DOWN]) {
            setKeyDown(Globals.DOWN, true);
        } else if (key == c.getKeyBind()[Globals.KEYBIND_LEFT]) {
            setKeyDown(Globals.LEFT, true);
        } else if (key == c.getKeyBind()[Globals.KEYBIND_RIGHT]) {
            setKeyDown(Globals.RIGHT, true);
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == c.getKeyBind()[Globals.KEYBIND_JUMP]) {
            setKeyDown(Globals.UP, false);
        } else if (key == c.getKeyBind()[Globals.KEYBIND_DOWN]) {
            setKeyDown(Globals.DOWN, false);
        } else if (key == c.getKeyBind()[Globals.KEYBIND_LEFT]) {
            setKeyDown(Globals.LEFT, false);
        } else if (key == c.getKeyBind()[Globals.KEYBIND_RIGHT]) {
            setKeyDown(Globals.RIGHT, false);
        } else if (key == c.getKeyBind()[Globals.KEYBIND_SKILL1]) {
            if (c.getHotkeys()[0] != null) {
                logic.sendUseSkill(myKey, c.getHotkeys()[0].getSkillCode());
            }
        } else if (key == c.getKeyBind()[Globals.KEYBIND_SKILL2]) {
            if (c.getHotkeys()[1] != null) {
                logic.sendUseSkill(myKey, c.getHotkeys()[1].getSkillCode());
            }
        } else if (key == c.getKeyBind()[Globals.KEYBIND_SKILL3]) {
            if (c.getHotkeys()[2] != null) {
                logic.sendUseSkill(myKey, c.getHotkeys()[2].getSkillCode());
            }
        } else if (key == c.getKeyBind()[Globals.KEYBIND_SKILL4]) {
            if (c.getHotkeys()[3] != null) {
                logic.sendUseSkill(myKey, c.getHotkeys()[3].getSkillCode());
            }
        } else if (key == c.getKeyBind()[Globals.KEYBIND_SKILL5]) {
            if (c.getHotkeys()[4] != null) {
                logic.sendUseSkill(myKey, c.getHotkeys()[4].getSkillCode());
            }
        } else if (key == c.getKeyBind()[Globals.KEYBIND_SKILL6]) {
            if (c.getHotkeys()[5] != null) {
                logic.sendUseSkill(myKey, c.getHotkeys()[5].getSkillCode());
            }
        } else if (key == c.getKeyBind()[Globals.KEYBIND_SKILL7]) {
            if (c.getHotkeys()[6] != null) {
                logic.sendUseSkill(myKey, c.getHotkeys()[6].getSkillCode());
            }
        } else if (key == c.getKeyBind()[Globals.KEYBIND_SKILL8]) {
            if (c.getHotkeys()[7] != null) {
                logic.sendUseSkill(myKey, c.getHotkeys()[7].getSkillCode());
            }
        } else if (key == c.getKeyBind()[Globals.KEYBIND_SKILL9]) {
            if (c.getHotkeys()[8] != null) {
                logic.sendUseSkill(myKey, c.getHotkeys()[8].getSkillCode());
            }
        } else if (key == c.getKeyBind()[Globals.KEYBIND_SKILL10]) {
            if (c.getHotkeys()[9] != null) {
                logic.sendUseSkill(myKey, c.getHotkeys()[9].getSkillCode());
            }
        } else if (key == c.getKeyBind()[Globals.KEYBIND_SKILL11]) {
            if (c.getHotkeys()[10] != null) {
                logic.sendUseSkill(myKey, c.getHotkeys()[10].getSkillCode());
            }
        } else if (key == c.getKeyBind()[Globals.KEYBIND_SKILL12]) {
            if (c.getHotkeys()[11] != null) {
                logic.sendUseSkill(myKey, c.getHotkeys()[11].getSkillCode());
            }
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                logic.sendDisconnect(myKey);
                break;
        }
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
        drawInfoHotkey = -1;
        for (int i = 0; i < hotkeySlots.length; i++) {
            if (hotkeySlots[i].contains(e.getPoint()) && c.getHotkeys()[i] != null) {
                drawInfoHotkey = i;
                return;
            }
        }
    }

    @Override
    public void unload() {
        Particle.unloadParticles();
        ItemEquip.unloadSprites();
    }

}
