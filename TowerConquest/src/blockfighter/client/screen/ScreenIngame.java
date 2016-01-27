package blockfighter.client.screen;

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

import blockfighter.client.Globals;
import blockfighter.client.SaveData;
import blockfighter.client.entities.boss.Boss;
import blockfighter.client.entities.damage.Damage;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.particles.ParticleBloodEmitter;
import blockfighter.client.entities.particles.ParticleBowArc;
import blockfighter.client.entities.particles.ParticleBowFrostArrow;
import blockfighter.client.entities.particles.ParticleBowPower;
import blockfighter.client.entities.particles.ParticleBowPowerCharge;
import blockfighter.client.entities.particles.ParticleBowRapid;
import blockfighter.client.entities.particles.ParticleBowStormEmitter;
import blockfighter.client.entities.particles.ParticleBowVolleyArrow;
import blockfighter.client.entities.particles.ParticleBowVolleyBow;
import blockfighter.client.entities.particles.ParticleBowVolleyBuffEmitter;
import blockfighter.client.entities.particles.ParticleBurnBuffEmitter;
import blockfighter.client.entities.particles.ParticlePassiveBarrier;
import blockfighter.client.entities.particles.ParticlePassiveResist;
import blockfighter.client.entities.particles.ParticlePassiveShadowAttack;
import blockfighter.client.entities.particles.ParticleShieldCharge;
import blockfighter.client.entities.particles.ParticleShieldDashBuffEmitter;
import blockfighter.client.entities.particles.ParticleShieldDashEmitter;
import blockfighter.client.entities.particles.ParticleShieldFortify;
import blockfighter.client.entities.particles.ParticleShieldFortifyEmitter;
import blockfighter.client.entities.particles.ParticleShieldIron;
import blockfighter.client.entities.particles.ParticleShieldIronAlly;
import blockfighter.client.entities.particles.ParticleShieldReflectCast;
import blockfighter.client.entities.particles.ParticleShieldReflectEmitter;
import blockfighter.client.entities.particles.ParticleShieldReflectHit;
import blockfighter.client.entities.particles.ParticleShieldToss;
import blockfighter.client.entities.particles.ParticleSwordCinder;
import blockfighter.client.entities.particles.ParticleSwordDrive;
import blockfighter.client.entities.particles.ParticleSwordMulti;
import blockfighter.client.entities.particles.ParticleSwordSlash1;
import blockfighter.client.entities.particles.ParticleSwordSlash2;
import blockfighter.client.entities.particles.ParticleSwordSlash3;
import blockfighter.client.entities.particles.ParticleSwordSlashBuffEmitter;
import blockfighter.client.entities.particles.ParticleSwordTaunt;
import blockfighter.client.entities.particles.ParticleSwordTauntAura;
import blockfighter.client.entities.particles.ParticleSwordTauntBuffEmitter;
import blockfighter.client.entities.particles.ParticleSwordVorpal;
import blockfighter.client.entities.player.Player;
import blockfighter.client.entities.player.skills.Skill;
import blockfighter.client.maps.GameMap;

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
	private final ConcurrentHashMap<Byte, Boss> bosses = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Integer, Particle> particles = new ConcurrentHashMap<>(500, 0.9f, 1);
	private final ConcurrentHashMap<Integer, Damage> dmgNum = new ConcurrentHashMap<>(500, 0.9f, 1);

	private final ConcurrentLinkedQueue<Integer> dmgKeys = new ConcurrentLinkedQueue<>();
	private int numDmgKeys = 500;

	private final ConcurrentLinkedQueue<Integer> particleKeys = new ConcurrentLinkedQueue<>();
	private int numParticleKeys = 500;

	private byte myKey = -1;

	private long pingTime = 0;
	private int ping = 0;
	private byte pID = 0;

	private double lastUpdateTime = 0, lastDmgUpdateTime = 0;
	private double lastRequestTime = 50;
	private double lastQueueTime = 0;
	private double lastPingTime = 0;
	private double lastSendKeyTime = 0;

	private final boolean[] moveKeyDown = { false, false, false, false };
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
		for (int key = 0; key < this.numParticleKeys; key++) {
			this.particleKeys.add(key);
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
	public void update() {
		final double now = System.nanoTime(); // Get time now

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

		if (now - this.lastDmgUpdateTime >= Globals.DMG_UPDATE) {
			updateDmgNum();
			this.lastDmgUpdateTime = now;
		}

		if (now - this.lastUpdateTime >= Globals.LOGIC_UPDATE) {
			final Skill[] skills = this.c.getSkills();
			for (final Skill skill : skills) {
				if (skill != null) {
					skill.reduceCooldown((long) (Globals.LOGIC_UPDATE / 1000000));
				}
			}
			updateParticles(this.particles);
			updatePlayers();
			updateBosses();
			this.lastUpdateTime = now;
		}

		if (now - this.lastRequestTime >= Globals.REQUESTALL_UPDATE) {
			logic.sendGetAll(this.myKey);
			this.lastRequestTime = now;
		}
		if (now - this.lastPingTime >= Globals.PING_UPDATE) {
			this.pID = (byte) (Globals.rng(256));
			this.pingTime = System.currentTimeMillis();
			logic.sendGetPing(this.myKey, this.pID);
			this.lastPingTime = now;
		}
	}

	private void updateDmgNum() {
		for (final Map.Entry<Integer, Damage> pEntry : this.dmgNum.entrySet()) {
			threadPool.execute(pEntry.getValue());
		}
		final LinkedList<Integer> remove = new LinkedList<>();
		for (final Map.Entry<Integer, Damage> pEntry : this.dmgNum.entrySet()) {
			try {
				pEntry.getValue().join();
				if (pEntry.getValue().isExpired()) {
					remove.add(pEntry.getKey());
				}
			} catch (final InterruptedException ex) {
			}
		}
		removeDmgNum(remove);
	}

	private void removeDmgNum(final LinkedList<Integer> remove) {
		while (!remove.isEmpty()) {
			final int p = remove.pop();
			returnDmgKey(p);
			this.dmgNum.remove(p);
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

	private void updateBosses() {
		for (final Map.Entry<Byte, Boss> pEntry : this.bosses.entrySet()) {
			threadPool.execute(pEntry.getValue());
		}
		for (final Map.Entry<Byte, Boss> pEntry : this.bosses.entrySet()) {
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
		this.map.drawBg(g);
		if (this.players != null && this.myKey != -1 && this.players.containsKey(this.myKey)) {
			g.translate(640.0 - this.players.get(this.myKey).getX(), 500.0 - this.players.get(this.myKey).getY());
		}
		this.map.draw(g);

		if (this.bosses != null) {
			for (final Map.Entry<Byte, Boss> pEntry : this.bosses.entrySet()) {
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
		for (final Map.Entry<Integer, Damage> pEntry : this.dmgNum.entrySet()) {
			pEntry.getValue().draw(g);
		}

		g.setTransform(resetForm);
		g.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		final BufferedImage hud = Globals.HUD[0];
		g.drawImage(hud, Globals.WINDOW_WIDTH / 2 - hud.getWidth() / 2, Globals.WINDOW_HEIGHT - hud.getHeight(), null);

		drawHPbar(g, hud);
		drawHotkeys(g);

		if (this.drawInfoHotkey != -1) {
			drawSkillInfo(g, this.hotkeySlots[this.drawInfoHotkey], this.c.getHotkeys()[this.drawInfoHotkey]);
		}
		g.setColor(new Color(25, 25, 25, 150));
		g.fillRect(1210, 5, 65, 45);
		g.setFont(Globals.ARIAL_12PT);
		g.setColor(Color.WHITE);
		g.drawString("Ping: " + this.ping, 1220, 40);
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

	private void drawHPbar(final Graphics2D g, final BufferedImage hud) {
		if (this.players.containsKey(this.myKey)) {
			final BufferedImage hpbar = Globals.HUD[1];
			g.drawImage(hpbar,
					Globals.WINDOW_WIDTH / 2 - hud.getWidth() / 2 + 2, Globals.WINDOW_HEIGHT - hud.getHeight() + 2,
					(int) (this.players.get(this.myKey).getStat(Globals.STAT_MINHP)
							/ this.players.get(this.myKey).getStat(Globals.STAT_MAXHP) * 802D),
					38,
					null);
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
		this.dmgKeys.add(key);
	}

	public void returnParticleKey(final int key) {
		this.particleKeys.add(key);
	}

	public int getNextDmgKey() {
		if (this.dmgKeys.isEmpty()) {
			for (int i = this.numDmgKeys; i < this.numDmgKeys + 500; i++) {
				this.dmgKeys.add(i);
			}
			this.numDmgKeys += 500;
		}
		return this.dmgKeys.remove();
	}

	public int getNextParticleKey() {
		if (this.particleKeys.isEmpty()) {
			for (int i = this.numParticleKeys; i < this.numParticleKeys + 500; i++) {
				this.particleKeys.add(i);
			}
			this.numParticleKeys += 500;
		}
		return this.particleKeys.remove();
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
				case Globals.DATA_DAMAGE:
					dataDamage(data);
					break;
				case Globals.DATA_PLAYER_GIVEEXP:
					dataPlayerGiveEXP(data);
					break;
				case Globals.DATA_BOSS_GET_STAT:
					dataBossGetStat(data);
					break;
				case Globals.DATA_BOSS_PARTICLE_EFFECT:
					dataBossParticleEffect(data);
					break;
				case Globals.DATA_BOSS_SET_POS:
					dataBossSetPos(data);
					break;
				case Globals.DATA_BOSS_SET_FACING:
					dataBossSetFacing(data);
					break;
				case Globals.DATA_BOSS_SET_STATE:
					dataBossSetState(data);
					break;
				case Globals.DATA_BOSS_SET_TYPE:
					dataBossSetType(data);
					break;
				case Globals.DATA_PLAYER_GIVEDROP:
					dataPlayerGiveDrop(data);
					break;
			}
		}
	}

	private void dataPlayerGiveDrop(final byte[] data) {
		final int lvl = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
		this.c.addDrops(lvl);
	}

	private void dataPlayerGiveEXP(final byte[] data) {
		final int amount = Globals.bytesToInt(Arrays.copyOfRange(data, 1, 5));
		this.c.addExp(amount);
	}

	private void dataPlayerSetCooldown(final byte[] data) {
		this.c.getSkills()[data[1]].setCooldown();
	}

	private void dataPlayerGetEquip(final byte[] data) {
		final byte key = data[1];
		if (this.players.containsKey(key)) {
			for (byte i = 0; i < Globals.NUM_EQUIP_SLOTS; i++) {
				final byte[] temp = new byte[4];
				System.arraycopy(data, i * 4 + 2, temp, 0, temp.length);
				this.players.get(key).setEquip(i, Globals.bytesToInt(temp));
			}
		}
	}

	private void dataPlayerGetStat(final byte[] data) {
		final byte key = data[1];
		if (this.players.containsKey(key)) {
			final byte stat = data[2];
			final int amount = Globals.bytesToInt(Arrays.copyOfRange(data, 3, 7));
			this.players.get(key).setStat(stat, amount);
		}
	}

	private void dataPlayerGetAll(final byte[] data) {
		final byte key = data[1];
		final int x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
		final int y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
		final byte facing = data[10], state = data[11], frame = data[12];
		if (this.players.containsKey(key)) {
			this.players.get(key).setPos(x, y);
		} else {
			this.players.put(key, new Player(x, y, key));
		}
		this.players.get(key).setFacing(facing);
		this.players.get(key).setState(state);
		this.players.get(key).setFrame(frame);
	}

	private void dataPlayerSetPos(final byte[] data) {
		final byte key = data[1];
		final int x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
		final int y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
		if (this.players.containsKey(key)) {
			this.players.get(key).setPos(x, y);
		} else {
			this.players.put(key, new Player(x, y, key));
		}
	}

	private void dataPlayerSetFacing(final byte[] data) {
		final byte key = data[1];
		final byte facing = data[2];
		if (this.players.containsKey(key)) {
			this.players.get(key).setFacing(facing);
		}
	}

	private void dataPlayerSetState(final byte[] data) {
		final byte key = data[1];
		if (this.players.containsKey(key)) {
			final byte state = data[2];
			final byte frame = data[3];
			this.players.get(key).setState(state);
			this.players.get(key).setFrame(frame);
		}
	}

	private void dataPlayerDisconnect(final byte[] data) {
		final byte key = data[1];
		if (this.players.containsKey(key) && key != this.myKey) {
			this.players.get(key).disconnect();
		}
	}

	private void dataDamage(final byte[] data) {
		final byte type = data[1];
		final int x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
		final int y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
		final int dmg = Globals.bytesToInt(Arrays.copyOfRange(data, 10, 14));
		final int key = getNextDmgKey();
		this.dmgNum.put(key, new Damage(dmg, type, new Point(x, y)));
	}

	private void dataParticleEffect(final byte[] data) {
		final byte particleID = data[1];
		int x, y;
		byte facing, playerKey;

		final int key = getNextParticleKey();
		switch (particleID) {
			case Globals.PARTICLE_SWORD_SLASH1:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				facing = data[10];
				this.particles.put(key, new ParticleSwordSlash1(key, x, y, facing));
				break;
			case Globals.PARTICLE_SWORD_SLASH2:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				facing = data[10];
				this.particles.put(key, new ParticleSwordSlash2(key, x, y, facing));
				break;
			case Globals.PARTICLE_SWORD_SLASH3:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				facing = data[10];
				this.particles.put(key, new ParticleSwordSlash3(key, x, y, facing));
				break;
			case Globals.PARTICLE_SWORD_DRIVE:
				facing = data[2];
				playerKey = data[3];
				if (this.players.containsKey(playerKey)) {
					this.particles.put(key, new ParticleSwordDrive(key, facing, this.players.get(playerKey)));
				}
				break;
			case Globals.PARTICLE_SWORD_VORPAL:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				facing = data[10];
				this.particles.put(key, new ParticleSwordVorpal(key, x, y, facing));
				break;
			case Globals.PARTICLE_SWORD_MULTI:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				facing = data[10];
				this.particles.put(key, new ParticleSwordMulti(key, x, y, facing));
				break;
			case Globals.PARTICLE_SWORD_CINDER:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				facing = data[10];
				this.particles.put(key, new ParticleSwordCinder(key, x, y, facing));
				break;
			case Globals.PARTICLE_SWORD_TAUNT:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				facing = data[10];
				this.particles.put(key, new ParticleSwordTaunt(key, x, y, facing));
				break;
			case Globals.PARTICLE_SWORD_TAUNTAURA1:
				playerKey = data[2];
				if (this.players.containsKey(playerKey)) {
					this.particles.put(key, new ParticleSwordTauntAura(key, this.players.get(playerKey)));
				}
				break;
			case Globals.PARTICLE_BOW_ARC:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				facing = data[10];
				this.particles.put(key, new ParticleBowArc(key, x, y, facing));
				break;
			case Globals.PARTICLE_BOW_RAPID:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				facing = data[10];
				this.particles.put(key, new ParticleBowRapid(key, x, y, facing));
				break;
			case Globals.PARTICLE_BOW_POWER:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				facing = data[10];
				this.particles.put(key, new ParticleBowPower(key, x, y, facing));
				break;
			case Globals.PARTICLE_BOW_POWERCHARGE:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				facing = data[10];
				this.particles.put(key, new ParticleBowPowerCharge(key, x, y, facing));
				break;
			case Globals.PARTICLE_BOW_VOLLEYBOW:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				facing = data[10];
				this.particles.put(key, new ParticleBowVolleyBow(key, x, y, facing));
				break;
			case Globals.PARTICLE_BOW_VOLLEYARROW:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				facing = data[10];
				this.particles.put(key, new ParticleBowVolleyArrow(key, x, y, facing));
				break;
			case Globals.PARTICLE_BOW_STORM:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				facing = data[10];
				this.particles.put(key, new ParticleBowStormEmitter(key, x, y, facing));
				break;
			case Globals.PARTICLE_BOW_FROSTARROW:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				facing = data[10];
				this.particles.put(key, new ParticleBowFrostArrow(key, x, y, facing));
				break;
			case Globals.PARTICLE_SHIELD_DASH:
				facing = data[2];
				playerKey = data[3];
				if (this.players.containsKey(playerKey)) {
					this.particles.put(key, new ParticleShieldDashEmitter(key, facing, this.players.get(playerKey)));
				}
				break;
			case Globals.PARTICLE_SHIELD_FORTIFY:
				playerKey = data[2];
				if (this.players.containsKey(playerKey)) {
					this.particles.put(key, new ParticleShieldFortify(key, this.players.get(playerKey)));
				}
				break;
			case Globals.PARTICLE_SHIELD_CHARGE:
				facing = data[2];
				playerKey = data[3];
				if (this.players.containsKey(playerKey)) {
					this.particles.put(key, new ParticleShieldCharge(key, facing, this.players.get(playerKey)));
				}
				break;
			case Globals.PARTICLE_SHIELD_REFLECTCAST:
				playerKey = data[2];
				if (this.players.containsKey(playerKey)) {
					this.particles.put(key, new ParticleShieldReflectCast(key, this.players.get(playerKey)));
				}
				break;
			case Globals.PARTICLE_SHIELD_REFLECTBUFF:
				playerKey = data[2];
				if (this.players.containsKey(playerKey)) {
					this.particles.put(key, new ParticleShieldReflectEmitter(key, this.players.get(playerKey)));
				}
				break;
			case Globals.PARTICLE_SHIELD_REFLECTHIT:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				this.particles.put(key, new ParticleShieldReflectHit(key, x, y));
				break;
			case Globals.PARTICLE_SHIELD_IRON:
				playerKey = data[2];
				if (this.players.containsKey(playerKey)) {
					this.particles.put(key, new ParticleShieldIron(key, this.players.get(playerKey)));
				}
				break;
			case Globals.PARTICLE_SHIELD_IRONALLY:
				playerKey = data[2];
				if (this.players.containsKey(playerKey)) {
					this.particles.put(key, new ParticleShieldIronAlly(key, this.players.get(playerKey)));
				}
				break;
			case Globals.PARTICLE_SHIELD_FORTIFYBUFF:
				playerKey = data[2];
				if (this.players.containsKey(playerKey)) {
					this.particles.put(key, new ParticleShieldFortifyEmitter(key, this.players.get(playerKey)));
				}
				break;
			case Globals.PARTICLE_SHIELD_TOSS:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				facing = data[10];
				this.particles.put(key, new ParticleShieldToss(key, x, y, facing));
				break;
			case Globals.PARTICLE_SWORD_TAUNTBUFF:
				playerKey = data[2];
				if (this.players.containsKey(playerKey)) {
					this.particles.put(key, new ParticleSwordTauntBuffEmitter(key, this.players.get(playerKey)));
				}
				break;
			case Globals.PARTICLE_SWORD_SLASHBUFF:
				playerKey = data[2];
				if (this.players.containsKey(playerKey)) {
					this.particles.put(key, new ParticleSwordSlashBuffEmitter(key, this.players.get(playerKey)));
				}
				break;
			case Globals.PARTICLE_SHIELD_DASHBUFF:
				playerKey = data[2];
				if (this.players.containsKey(playerKey)) {
					this.particles.put(key, new ParticleShieldDashBuffEmitter(key, this.players.get(playerKey)));
				}
				break;
			case Globals.PARTICLE_BOW_VOLLEYBUFF:
				playerKey = data[2];
				if (this.players.containsKey(playerKey)) {
					this.particles.put(key, new ParticleBowVolleyBuffEmitter(key, this.players.get(playerKey)));
				}
				break;
			case Globals.PARTICLE_BURN:
				playerKey = data[2];
				if (this.players.containsKey(playerKey)) {
					this.particles.put(key, new ParticleBurnBuffEmitter(key, this.players.get(playerKey)));
				}
				break;
			case Globals.PARTICLE_PASSIVE_RESIST:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				this.particles.put(key, new ParticlePassiveResist(key, x, y));
				break;
			case Globals.PARTICLE_PASSIVE_BARRIER:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				this.particles.put(key, new ParticlePassiveBarrier(key, x, y));
				break;
			case Globals.PARTICLE_PASSIVE_SHADOWATTACK:
				x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
				y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
				this.particles.put(key, new ParticlePassiveShadowAttack(key, x, y));
				break;
			case Globals.PARTICLE_BLOOD:
				playerKey = data[2];
				if (this.players.containsKey(playerKey)) {
					this.particles.put(key, new ParticleBloodEmitter(key, this.players.get(playerKey)));
				}
				break;
		}
	}

	private void dataPlayerGetName(final byte[] data) {
		final byte key = data[1];
		if (this.players.containsKey(key)) {
			final byte[] temp = new byte[Globals.MAX_NAME_LENGTH];
			System.arraycopy(data, 2, temp, 0, temp.length);
			this.players.get(key).setPlayerName(new String(temp, StandardCharsets.UTF_8).trim());
		}
	}

	private void dataBossParticleEffect(final byte[] data) {
		final byte key = data[1];
		if (this.bosses.containsKey(key)) {
			this.bosses.get(key).addParticle(data);
		}
	}

	private void dataBossSetType(final byte[] data) {
		final byte key = data[1], type = data[2];
		if (!this.bosses.containsKey(key)) {
			final int x = Globals.bytesToInt(Arrays.copyOfRange(data, 3, 7));
			final int y = Globals.bytesToInt(Arrays.copyOfRange(data, 7, 11));
			this.bosses.put(key, Boss.spawnBoss(type, key, x, y));
		}
	}

	private void dataBossSetPos(final byte[] data) {
		final byte key = data[1];
		if (this.bosses.containsKey(key)) {
			final int x = Globals.bytesToInt(Arrays.copyOfRange(data, 2, 6));
			final int y = Globals.bytesToInt(Arrays.copyOfRange(data, 6, 10));
			this.bosses.get(key).setPos(x, y);
		} else {
			logic.sendSetBossType(key);
		}
	}

	private void dataBossSetFacing(final byte[] data) {
		final byte key = data[1];
		if (this.bosses.containsKey(key)) {
			final byte facing = data[2];
			this.bosses.get(key).setFacing(facing);
		} else {
			logic.sendSetBossType(key);
		}
	}

	private void dataBossSetState(final byte[] data) {
		final byte key = data[1];
		if (this.bosses.containsKey(key)) {
			final byte state = data[2];
			final byte frame = data[3];
			this.bosses.get(key).setState(state);
			this.bosses.get(key).setFrame(frame);
		} else {
			logic.sendSetBossType(key);
		}
	}

	private void dataBossGetStat(final byte[] data) {
		final byte key = data[1];
		if (this.bosses.containsKey(key)) {
			final byte stat = data[2];
			final int amount = Globals.bytesToInt(Arrays.copyOfRange(data, 3, 7));
			this.bosses.get(key).setStat(stat, amount);
		} else {
			logic.sendSetBossType(key);
		}
	}

	public void addDmgNum(final Damage d) {
		this.dmgNum.put(getNextDmgKey(), d);
	}

	public void addParticle(final Particle newP) {
		this.particles.put(newP.getKey(), newP);
	}

	public void queueData(final byte[] data) {
		this.dataQueue.add(data);
	}

	public void disconnect() {
		logic.sendDisconnect(this.myKey);
	}

	public void setPing(final byte rID) {
		if (rID != this.pID) {
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
		// Particle.unloadParticles();
		ItemEquip.unloadSprites();
		for (final Map.Entry<Byte, Boss> pEntry : this.bosses.entrySet()) {
			pEntry.getValue().unload();
		}
	}

}
