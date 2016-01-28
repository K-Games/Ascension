package blockfighter.server.entities.boss;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.GameEntity;
import blockfighter.server.entities.buff.Buff;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.buff.BuffStun;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.maps.GameMap;
import blockfighter.server.net.PacketSender;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Ken Kwan
 */
public abstract class Boss extends Thread implements GameEntity {

    public final static int NUM_STATS = 3;
    public final static byte STAT_LEVEL = 0,
            STAT_MAXHP = 1,
            STAT_MINHP = 2;

    public final static byte STATE_STAND = 0x00,
            STATE_WALK = 0x01,
            STATE_JUMP = 0x02;

    public final static byte BOSS_LIGHTNING = 0x00;

    protected final byte key;
    protected final LogicModule logic;
    protected double x, y, ySpeed, xSpeed;
    protected boolean isFalling = false, isDead = false;
    protected boolean updatePos = false, updateFacing = false, updateAnimState = false;
    protected byte bossState, animState, facing, frame;
    protected double nextFrameTime = 0;
    protected Rectangle2D.Double hitbox;
    protected double[] stats = new double[NUM_STATS];

    protected ConcurrentHashMap<Byte, Buff> buffs = new ConcurrentHashMap<>(10, 0.9f, 1);
    protected Buff isStun, isKnockback;
    protected Byte nextState;
    protected long stunReduction = 0;

    protected static PacketSender sender;
    protected final GameMap map;

    protected ConcurrentLinkedQueue<Damage> damageQueue = new ConcurrentLinkedQueue<>();
    protected ConcurrentLinkedQueue<Integer> healQueue = new ConcurrentLinkedQueue<>();
    protected ConcurrentLinkedQueue<Buff> buffQueue = new ConcurrentLinkedQueue<>();
    protected ConcurrentLinkedQueue<Byte> buffKeys = new ConcurrentLinkedQueue<>();

    protected ConcurrentHashMap<Player, Double> aggroCounter = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Byte, Skill> skills = new ConcurrentHashMap<>(2, 0.9f, 1);
    protected long nextHPSend = 0;
    protected long skillDuration = 0;
    protected byte type;

    public Boss(final LogicModule l, final byte key, final GameMap map, final double x, final double y) {
        this.logic = l;
        this.key = key;
        this.x = x;
        this.y = y;
        this.hitbox = new Rectangle2D.Double(x - 50, y - 180, 100, 180);
        this.map = map;
        this.facing = Globals.RIGHT;
        this.bossState = STATE_STAND;
        this.frame = 0;
        for (byte i = -128; i < 127; i++) {
            this.buffKeys.add(i);
        }
    }

    /**
     * Set a reference to the Server PacketSender.
     *
     * @param ps Server PacketSender
     */
    public static void setPacketSender(final PacketSender ps) {
        sender = ps;
    }

    public void addSkill(final byte sc, final Skill s) {
        this.skills.put(sc, s);
    }

    public boolean canCast(final byte sc) {
        return this.skills.get(sc).canCast();
    }

    public void setCooldown(final byte sc) {
        this.skills.get(sc).setCooldown();
    }

    public void reduceCooldown(final byte sc, final long amount) {
        this.skills.get(sc).reduceCooldown(amount);
    }

    public abstract Player getTarget();

    public abstract boolean isUsingSkill();

    public boolean isDead() {
        return this.isDead;
    }

    private void returnBuffKey(final byte bKey) {
        this.buffKeys.add(bKey);
    }

    public Rectangle2D.Double getHitbox() {
        return this.hitbox;
    }

    private Byte getNextBuffKey() {
        if (!this.buffKeys.isEmpty()) {
            return this.buffKeys.poll();
        }
        return null;
    }

    public double[] getStats() {
        return this.stats;
    }

    public byte getType() {
        return this.type;
    }

    /**
     * Return this player's current X position.
     *
     * @return The player's X in double
     */
    public double getX() {
        return this.x;
    }

    /**
     * Return this player's current Y position.
     *
     * @return The player's Y in double
     */
    public double getY() {
        return this.y;
    }

    /**
     * Return this player's key.
     * <p>
     * This key is the same key in the player array in the logic module.
     * </p>
     *
     * @return The key of this player in byte
     */
    public byte getKey() {
        return this.key;
    }

    public byte getAnimState() {
        return this.animState;
    }

    public byte getFacing() {
        return this.facing;
    }

    public byte getFrame() {
        return this.frame;
    }

    public void setPos(final double x, final double y) {
        this.x = x;
        this.y = y;
        this.updatePos = true;
    }

    /**
     * Set change in Y on the next tick.
     *
     * @param speed Distance in double
     */
    public void setYSpeed(final double speed) {
        this.ySpeed = speed;
    }

    /**
     * Set change in X on the next tick.
     *
     * @param speed Distance in double
     */
    public void setXSpeed(final double speed) {
        this.xSpeed = speed;
    }

    @Override
    public void run() {
        try {
            update();
        } catch (final Exception ex) {
            Globals.log(ex.getLocalizedMessage(), ex, true);
        }
    }

    @Override
    public abstract void update();

    public void updateBossState() {
        if (this.nextState != null && this.bossState != this.nextState) {
            setBossState(this.nextState);
            this.nextState = null;
        }
    }

    public void addAggro(final Player p, final double amount) {
        double aggro = (this.aggroCounter.containsKey(p)) ? this.aggroCounter.get(p) : 0;
        aggro += amount;
        this.aggroCounter.put(p, aggro);
    }

    public void updateHP() {
        while (!this.damageQueue.isEmpty()) {
            final Damage dmg = this.damageQueue.poll();
            if (dmg != null) {
                final int amount = dmg.getDamage();
                dmg.proc();
                addAggro(dmg.getOwner(), dmg.getDamage());
                if (!dmg.isHidden()) {
                    sendDamage(dmg);
                }
                this.stats[STAT_MINHP] -= amount;
                this.nextHPSend = 0;
            }
        }

        while (!this.healQueue.isEmpty()) {
            final Integer heal = this.healQueue.poll();
            if (heal != null) {
                this.stats[STAT_MINHP] += heal;
                this.nextHPSend = 0;
            }
        }

        if (this.stats[STAT_MINHP] > this.stats[STAT_MAXHP]) {
            this.stats[STAT_MINHP] = this.stats[STAT_MAXHP];
        } else if (this.stats[STAT_MINHP] < 0) {
            this.stats[STAT_MINHP] = 0;
        }

        if (this.stats[STAT_MINHP] == 0) {
            die();
        }

        if (this.nextHPSend <= 0) {
            final byte[] minHP = Globals.intToByte((int) (this.stats[STAT_MINHP] / this.stats[STAT_MAXHP] * 10000));
            final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
            bytes[0] = Globals.DATA_BOSS_GET_STAT;
            bytes[1] = this.key;
            bytes[2] = STAT_MINHP;
            System.arraycopy(minHP, 0, bytes, 3, minHP.length);
            sender.sendAll(bytes, this.logic.getRoom());
            this.nextHPSend = 150;
        }
    }

    private void die() {
        for (final Map.Entry<Byte, Player> player : this.logic.getPlayers().entrySet()) {
            player.getValue().giveEXP(Globals.calcEXPtoNxtLvl(this.stats[STAT_LEVEL]) / 3);
            player.getValue().giveDrop(this.stats[STAT_LEVEL]);
        }
        this.isDead = true;
    }

    public void updateBuffs() {
        this.isStun = null;
        this.isKnockback = null;
        final LinkedList<Byte> remove = new LinkedList<>();
        for (final Map.Entry<Byte, Buff> bEntry : this.buffs.entrySet()) {
            final Buff b = bEntry.getValue();
            b.update();
            if (this.isStun == null && b instanceof BuffStun) {
                this.isStun = b;
                this.stunReduction += 100;
            } else if (this.isKnockback == null && b instanceof BuffKnockback) {
                this.isKnockback = b;
            }
            if (b.isExpired()) {
                remove.add(bEntry.getKey());
            }
        }
        for (final byte bKey : remove) {
            this.buffs.remove(bKey);
            returnBuffKey(bKey);
        }
        // Empty and add buffs from queue
        while (!this.buffQueue.isEmpty()) {
            final Buff b = this.buffQueue.poll();
            final Byte bKey = getNextBuffKey();
            if (bKey != null && b != null) {
                if (b instanceof BuffStun) {
                    b.reduceDuration(this.stunReduction);
                }
                this.buffs.put(bKey, b);
            }
        }
    }

    public boolean intersectHitbox(final Rectangle2D.Double box) {
        return this.hitbox.intersects(box);
    }

    public boolean isStunned() {
        return this.isStun != null;
    }

    public boolean isKnockback() {
        return this.isKnockback != null;
    }

    public void queueBuff(final Buff b) {
        this.buffQueue.add(b);
    }

    public void updateFall() {
        if (this.ySpeed != 0) {
            updateY(this.ySpeed);
            // queueBossState(STATE_JUMP);
        }

        setYSpeed(this.ySpeed + Globals.GRAVITY);
        if (this.ySpeed >= Globals.MAX_FALLSPEED) {
            setYSpeed(Globals.MAX_FALLSPEED);
        }

        this.isFalling = this.map.isFalling(this.x, this.y, this.ySpeed);
        if (!this.isFalling && this.ySpeed > 0) {
            this.y = this.map.getValidY(this.x, this.y, this.ySpeed);
            setYSpeed(0);
            // queueBossState(STATE_STAND);
        }
    }

    public void queueDamage(final Damage damage) {
        this.damageQueue.add(damage);
    }

    public void queueHeal(final int heal) {
        this.healQueue.add(heal);
    }

    public void setFacing(final byte f) {
        this.facing = f;
        this.updateFacing = true;
    }

    public void updateSkillCd() {
        for (final Map.Entry<Byte, Skill> s : this.skills.entrySet()) {
            s.getValue().reduceCooldown((long) (Globals.LOGIC_UPDATE / 1000000));
        }
    }

    public boolean updateX(final double change) {
        if (change == 0) {
            return false;
        }

        if (this.map.isOutOfBounds(this.x + change, this.y)) {
            return false;
        }
        this.x = this.x + change;
        this.updatePos = true;
        return true;
    }

    public boolean updateY(final double change) {
        if (change == 0) {
            return false;
        }

        if (this.map.isOutOfBounds(this.x, this.y + change)) {
            return false;
        }
        this.y = this.y + change;
        this.updatePos = true;
        return true;
    }

    public void queueBossState(final byte newState) {
        this.nextState = newState;
    }

    public void setBossState(final byte newState) {
        this.bossState = newState;
        this.frame = 0;
        this.updateAnimState = true;
    }

    public void sendPos() {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_BOSS_SET_POS;
        bytes[1] = this.key;
        final byte[] posXInt = Globals.intToByte((int) this.x);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        final byte[] posYInt = Globals.intToByte((int) this.y);
        bytes[6] = posYInt[0];
        bytes[7] = posYInt[1];
        bytes[8] = posYInt[2];
        bytes[9] = posYInt[3];
        sender.sendAll(bytes, this.logic.getRoom());
        this.updatePos = false;
    }

    public void sendFacing() {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_BOSS_SET_FACING;
        bytes[1] = this.key;
        bytes[2] = this.facing;
        sender.sendAll(bytes, this.logic.getRoom());
        this.updateFacing = false;
    }

    public void sendState() {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_BOSS_SET_STATE;
        bytes[1] = this.key;
        bytes[2] = this.animState;
        bytes[3] = this.frame;
        sender.sendAll(bytes, this.logic.getRoom());
        this.updateAnimState = false;
    }

    public void sendDamage(final Damage dmg) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 3];
        bytes[0] = Globals.DATA_DAMAGE;
        bytes[1] = dmg.getDamageType();
        final byte[] posXInt = Globals.intToByte(dmg.getDmgPoint().x);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        final byte[] posYInt = Globals.intToByte(dmg.getDmgPoint().y);
        bytes[6] = posYInt[0];
        bytes[7] = posYInt[1];
        bytes[8] = posYInt[2];
        bytes[9] = posYInt[3];
        final byte[] d = Globals.intToByte(dmg.getDamage());
        bytes[10] = d[0];
        bytes[11] = d[1];
        bytes[12] = d[2];
        bytes[13] = d[3];
        sender.sendAll(bytes, this.logic.getRoom());
    }

    public static void sendBossParticle(final byte key, final byte room, final byte particleID, final double x, final double y) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_BOSS_PARTICLE_EFFECT;
        bytes[1] = key;
        bytes[2] = particleID;
        final byte[] posXInt = Globals.intToByte((int) x);
        bytes[3] = posXInt[0];
        bytes[4] = posXInt[1];
        bytes[5] = posXInt[2];
        bytes[6] = posXInt[3];
        final byte[] posYInt = Globals.intToByte((int) y);
        bytes[7] = posYInt[0];
        bytes[8] = posYInt[1];
        bytes[9] = posYInt[2];
        bytes[10] = posYInt[3];
        sender.sendAll(bytes, room);
    }
}
