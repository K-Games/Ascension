package blockfighter.server.entities.boss;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.Buff;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.buff.BuffStun;
import blockfighter.server.entities.damage.Damage;
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
public abstract class Boss extends Thread {

    public final static int NUM_STATS = 9;
    public final static byte STAT_LEVEL = 0,
            STAT_MAXHP = 1,
            STAT_MINHP = 2;

    public final static byte STATE_STAND = 0x00,
            STATE_WALK = 0x01,
            STATE_JUMP = 0x02;

    protected final byte key;
    protected final LogicModule logic;
    protected double x, y, ySpeed, xSpeed;
    protected boolean isFalling = false;
    protected boolean updatePos = false, updateFacing = false, updateAnimState = false;
    protected byte bossState, animState, facing, frame;
    protected double nextFrameTime = 0;
    protected Rectangle2D.Double hitbox;
    protected double[] stats = new double[NUM_STATS];

    protected ConcurrentHashMap<Byte, Buff> buffs = new ConcurrentHashMap<>(10, 0.9f, 1);
    protected Buff isStun, isKnockback;

    protected static PacketSender sender;
    private final GameMap map;

    private ConcurrentLinkedQueue<Damage> damageQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Integer> healQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Byte> stateQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Buff> buffQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Byte> buffKeys = new ConcurrentLinkedQueue<>();

    private long nextHPSend = 0;

    /**
     * Create a new player entity in the server.
     *
     * @param key The key of this player in the player array in logic module
     * @param x Spawning x location in double
     * @param y Spawning y location in double
     * @param map Reference to server's loaded map
     * @param l Reference to Logic module
     */
    public Boss(LogicModule l, byte key, GameMap map, double x, double y) {
        logic = l;
        this.key = key;
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double(x - 50, y - 180, 100, 180);
        this.map = map;
        facing = Globals.RIGHT;
        bossState = STATE_STAND;
        frame = 0;
        for (byte i = -128; i < 127; i++) {
            buffKeys.add(i);
        }
    }

    /**
     * Set a reference to the Server PacketSender.
     *
     * @param ps Server PacketSender
     */
    public static void setPacketSender(PacketSender ps) {
        sender = ps;
    }

    private void returnBuffKey(byte bKey) {
        buffKeys.add(bKey);
    }

    private Byte getNextBuffKey() {
        if (!buffKeys.isEmpty()) {
            return buffKeys.poll();
        }
        return null;
    }

    /**
     * Return this player's current X position.
     *
     * @return The player's X in double
     */
    public double getX() {
        return x;
    }

    /**
     * Return this player's current Y position.
     *
     * @return The player's Y in double
     */
    public double getY() {
        return y;
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
        return key;
    }

    public byte getAnimState() {
        return animState;
    }

    public byte getFacing() {
        return facing;
    }

    public byte getFrame() {
        return frame;
    }

    public void setPos(double x, double y) {
        this.x = x;
        this.y = y;
        updatePos = true;
    }

    /**
     * Set change in Y on the next tick.
     *
     * @param speed Distance in double
     */
    public void setYSpeed(double speed) {
        ySpeed = speed;
    }

    /**
     * Set change in X on the next tick.
     *
     * @param speed Distance in double
     */
    public void setXSpeed(double speed) {
        xSpeed = speed;
    }

    @Override
    public void run() {
        try {
            update();
        } catch (Exception ex) {
            Globals.log(ex.getLocalizedMessage(), ex, true);
        }
    }

    public abstract void update();

    public void updateBossState() {
        while (!stateQueue.isEmpty()) {
            Byte newState = stateQueue.poll();
            if (newState != null && bossState != newState) {
                setBossState(newState);
                stateQueue.clear();
            }
        }
    }

    public void updateHP() {
        while (!damageQueue.isEmpty()) {
            Damage dmg = damageQueue.poll();
            if (dmg != null) {
                int amount = dmg.getDamage();
                dmg.proc();
                stats[STAT_MINHP] -= amount;
                nextHPSend = 0;
            }
        }

        while (!healQueue.isEmpty()) {
            Integer heal = healQueue.poll();
            if (heal != null) {
                stats[STAT_MINHP] += heal;
                nextHPSend = 0;
            }
        }

        if (stats[STAT_MINHP] > stats[STAT_MAXHP]) {
            stats[STAT_MINHP] = stats[STAT_MAXHP];
        } else if (stats[STAT_MINHP] < 0) {
            stats[STAT_MINHP] = 0;
        }

        if (nextHPSend <= 0) {
            byte[] minHP = Globals.intToByte((int) stats[STAT_MINHP]);
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
            //bytes[0] = Globals.DATA_BOSS_GET_STAT;
            bytes[1] = key;
            bytes[2] = STAT_MINHP;
            System.arraycopy(minHP, 0, bytes, 3, minHP.length);
            sender.sendAll(bytes, logic.getRoom());
            nextHPSend = 150;
        }
    }

    public void updateBuffs() {
        isStun = null;
        isKnockback = null;
        LinkedList<Byte> remove = new LinkedList<>();
        for (Map.Entry<Byte, Buff> bEntry : buffs.entrySet()) {
            Buff b = bEntry.getValue();
            b.update();
            if (b instanceof BuffStun) {
                if (isStun == null || (isStun != null && isStun.getDuration() < b.getDuration())) {
                    isStun = b;
                }
            } else if (b instanceof BuffKnockback) {
                if (isKnockback == null || (isKnockback != null && isKnockback.getDuration() < b.getDuration())) {
                    isKnockback = b;
                }
            }
            if (b.isExpired()) {
                remove.add(bEntry.getKey());
            }
        }
        for (byte bKey : remove) {
            buffs.remove(bKey);
            returnBuffKey(bKey);
        }
        //Empty and add buffs from queue
        while (!buffQueue.isEmpty()) {
            Buff b = buffQueue.poll();
            Byte bKey = getNextBuffKey();
            if (bKey != null && b != null) {
                buffs.put(bKey, b);
            }
        }
    }

    public boolean intersectHitbox(Rectangle2D.Double box) {
        return hitbox.intersects(box);
    }

    public boolean isStunned() {
        return isStun != null;
    }

    public boolean isKnockback() {
        return isKnockback != null;
    }

    public void queueBuff(Buff b) {
        buffQueue.add(b);
    }

    public void updateFall() {
        if (ySpeed != 0) {
            updateY(ySpeed);
            queueBossState(STATE_JUMP);
        }

        setYSpeed(ySpeed + Globals.GRAVITY);
        if (ySpeed >= Globals.MAX_FALLSPEED) {
            setYSpeed(Globals.MAX_FALLSPEED);
        }

        isFalling = map.isFalling(x, y, ySpeed);
        if (!isFalling && ySpeed > 0) {
            y = map.getValidY(x, y, ySpeed);
            setYSpeed(0);
            queueBossState(STATE_STAND);
        }
    }

    public void queueDamage(Damage damage) {
        damageQueue.add(damage);
    }

    public void queueHeal(int heal) {
        healQueue.add(heal);
    }

    public void setFacing(byte f) {
        facing = f;
        updateFacing = true;
    }

    public boolean updateX(double change) {
        if (change == 0) {
            return false;
        }

        if (map.isOutOfBounds(x + change, y)) {
            return false;
        }
        x = x + change;
        updatePos = true;
        return true;
    }

    public boolean updateY(double change) {
        if (change == 0) {
            return false;
        }

        if (map.isOutOfBounds(x, y + change)) {
            return false;
        }
        y = y + change;
        updatePos = true;
        return true;
    }

    public void queueBossState(byte newState) {
        stateQueue.clear();
        stateQueue.add(newState);
    }

    public void setBossState(byte newState) {
        bossState = newState;
        frame = 0;
        updateAnimState = true;
    }

    public void sendPos() {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 2];
        //bytes[0] = Globals.DATA_BOSS_SET_POS;
        bytes[1] = key;
        byte[] posXInt = Globals.intToByte((int) x);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        byte[] posYInt = Globals.intToByte((int) y);
        bytes[6] = posYInt[0];
        bytes[7] = posYInt[1];
        bytes[8] = posYInt[2];
        bytes[9] = posYInt[3];
        sender.sendAll(bytes, logic.getRoom());
        updatePos = false;
    }

    public void sendFacing() {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        //bytes[0] = Globals.DATA_BOSS_SET_FACING;
        bytes[1] = key;
        bytes[2] = facing;
        sender.sendAll(bytes, logic.getRoom());
        updateFacing = false;
    }

    public void sendState() {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        //bytes[0] = Globals.DATA_BOSS_SET_STATE;
        bytes[1] = key;
        bytes[2] = animState;
        bytes[3] = frame;
        sender.sendAll(bytes, logic.getRoom());
        updateAnimState = false;
    }

    public void sendDamage(Damage dmg) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 3];
        bytes[0] = Globals.DATA_DAMAGE;
        bytes[1] = (!dmg.isCrit()) ? Damage.DAMAGE_TYPE_PLAYER : Damage.DAMAGE_TYPE_PLAYERCRIT;
        byte[] posXInt = Globals.intToByte(dmg.getDmgPoint().x);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        byte[] posYInt = Globals.intToByte(dmg.getDmgPoint().y);
        bytes[6] = posYInt[0];
        bytes[7] = posYInt[1];
        bytes[8] = posYInt[2];
        bytes[9] = posYInt[3];
        byte[] d = Globals.intToByte(dmg.getDamage());
        bytes[10] = d[0];
        bytes[11] = d[1];
        bytes[12] = d[2];
        bytes[13] = d[3];
        sender.sendAll(bytes, logic.getRoom());
    }
}
