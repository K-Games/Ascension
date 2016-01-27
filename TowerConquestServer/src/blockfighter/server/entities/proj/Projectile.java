package blockfighter.server.entities.proj;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.GameEntity;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.player.Player;
import blockfighter.server.net.PacketSender;

/**
 * Abstract class for projectiles/attacks
 *
 * @author Ken Kwan
 */
public abstract class Projectile extends Thread implements GameEntity {

	/**
	 * Hash map key paired with this
	 */
	protected final int key;
	/**
	 * Reference to Logic Module.
	 */
	protected final LogicModule logic;

	/**
	 * Projectile x position.
	 */
	protected double x,
			/**
			 * Projectile y position.
			 */
			y;

	/**
	 * Owning Player of Projectile
	 */
	private Player owner;

	/**
	 * Owning Boss of Projectile
	 */
	private Boss bossOwner;
	/**
	 * Array of players hit by this projectile
	 */
	protected ArrayList<Player> pHit = new ArrayList<>();

	/**
	 * Queue of players to be hit by projectile
	 */
	protected final LinkedList<Player> playerQueue = new LinkedList<>();
	protected ArrayList<Boss> bHit = new ArrayList<>();
	protected final LinkedList<Boss> bossQueue = new LinkedList<>();
	/**
	 * The duration of this projectile in nanoseconds.
	 */
	protected long duration;

	/**
	 * Hit boxes of this projectile
	 */
	protected Rectangle2D.Double[] hitbox;

	/**
	 * Reference to Server PacketSender
	 */
	protected static PacketSender sender;

	/**
	 * Checks if this projectile has already been queued to have effects to be applied
	 */
	protected boolean queuedEffect = false;

	/**
	 * Constructor called by subclasses to reference sender and logic.
	 *
	 * @param l Reference to Logic module
	 * @param k Hash map key
	 */
	public Projectile(final LogicModule l, final int k) {
		this.logic = l;
		this.key = k;
	}

	/**
	 * Constructor for a empty projectile.
	 *
	 * @param l Reference to Logic module
	 * @param k Hash map key
	 * @param o Owning player
	 * @param x Spawning x
	 * @param y Spawning y
	 * @param duration
	 */
	public Projectile(final LogicModule l, final int k, final Player o, final double x, final double y, final long duration) {
		this(l, k);
		this.owner = o;
		this.x = x;
		this.y = y;
		this.hitbox = new Rectangle2D.Double[1];
		this.hitbox[0] = new Rectangle2D.Double(0, 0, 0, 0);
		this.duration = duration;
	}

	public Projectile(final LogicModule l, final int k, final Boss o, final double x, final double y, final long duration) {
		this(l, k);
		this.bossOwner = o;
		this.x = x;
		this.y = y;
		this.hitbox = new Rectangle2D.Double[1];
		this.duration = duration;
	}

	@Override
	public void update() {
		this.duration -= Globals.LOGIC_UPDATE / 1000000;
		if (this.hitbox[0] == null) {
			return;
		}
		if (this.logic.getMap().isPvP()) {
			for (final Map.Entry<Byte, Player> pEntry : this.logic.getPlayers().entrySet()) {
				final Player p = pEntry.getValue();
				if (p != getOwner() && !this.pHit.contains(p) && !p.isInvulnerable() && p.intersectHitbox(this.hitbox[0])) {
					this.playerQueue.add(p);
					this.pHit.add(p);
					queueEffect(this);
				}
			}
		}

		for (final Map.Entry<Byte, Boss> bEntry : this.logic.getBosses().entrySet()) {
			final Boss b = bEntry.getValue();
			if (!this.bHit.contains(b) && b.intersectHitbox(this.hitbox[0])) {
				this.bossQueue.add(b);
				this.bHit.add(b);
				queueEffect(this);
			}
		}
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	/**
	 * Set the static packet sender.
	 *
	 * @param ps Server PacketSender.
	 */
	public static void setPacketSender(final PacketSender ps) {
		sender = ps;
	}

	public void setOwner(final Player owner) {
		this.owner = owner;
	}

	public Player getOwner() {
		return this.owner;
	}

	public void setBossOwner(final Boss owner) {
		this.bossOwner = owner;
	}

	public Boss getBossOwner() {
		return this.bossOwner;
	}

	@Override
	public void run() {
		try {
			update();
		} catch (final Exception ex) {
			Globals.log(ex.getLocalizedMessage(), ex, true);
		}
	}

	public boolean isExpired() {
		return this.duration <= 0;
	}

	public boolean isQueued() {
		return this.queuedEffect;
	}

	public int getKey() {
		return this.key;
	}

	public void processQueue() {
	}

	public Rectangle2D.Double[] getHitbox() {
		return this.hitbox;
	}

	public void queueEffect(final Projectile p) {
		if (!isQueued()) {
			this.logic.queueProjEffect(p);
			this.queuedEffect = true;
		}
	}
}
