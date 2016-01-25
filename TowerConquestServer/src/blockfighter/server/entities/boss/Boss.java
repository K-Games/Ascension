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
    private ConcurrentHashMap<Byte, Skill> skills = new ConcurrentHashMap<>(2, 0.9f, 1);
    protected long nextHPSend = 0;
    protected long skillDuration = 0;
    protected byte type;

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

    public void addSkill(byte sc, Skill s) {
        skills.put(sc, s);
    }

    public boolean canCast(byte sc) {
        return skills.get(sc).canCast();
    }

    public void setCooldown(byte sc) {
        skills.get(sc).setCooldown();
    }

    public void reduceCooldown(byte sc, long amount) {
        skills.get(sc).reduceCooldown(amount);
    }

    public abstract Player getTarget();

    public abstract boolean isUsingSkill();

    public boolean isDead() {
        return isDead;
    }

    private void returnBuffKey(byte bKey) {
        buffKeys.add(bKey);
    }

    public Rectangle2D.Double getHitbox() {
        return hitbox;
    }

    private Byte getNextBuffKey() {
        if (!buffKeys.isEmpty()) {
            return buffKeys.poll();
        }
        return null;
    }

    public double[] getStats() {
        return stats;
    }

    public byte getType() {
        return type;
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

    @Override
    public abstract void update();

    public void updateBossState() {
        if (nextState != null && bossState != nextState) {
            setBossState(nextState);
            nextState = null;
        }
    }

    public void addAggro(Player p, double amount) {
        double aggro = (aggroCounter.containsKey(p)) ? aggroCounter.get(p) : 0;
        aggro += amount;
        aggroCounter.put(p, aggro);
    }

    public void updateHP() {
        while (!damageQueue.isEmpty()) {
            Damage dmg = damageQueue.poll();
            if (dmg != null) {
                int amount = dmg.getDamage();
                dmg.proc();
                addAggro(dmg.getOwner(), dmg.getDamage());
                if (!dmg.isHidden()) {
                    sendDamage(dmg);
                }
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

        if (stats[STAT_MINHP] == 0) {
            die();
        }

        if (nextHPSend <= 0) {
            byte[] minHP = Globals.intToByte((int) (stats[STAT_MINHP] / stats[STAT_MAXHP] * 10000));
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
            bytes[0] = Globals.DATA_BOSS_GET_STAT;
            bytes[1] = key;
            bytes[2] = STAT_MINHP;
            System.arraycopy(minHP, 0, bytes, 3, minHP.length);
            sender.sendAll(bytes, logic.getRoom());
            nextHPSend = 150;
        }
    }

    private void die() {
        for (Map.Entry<Byte, Player> player : logic.getPlayers().entrySet()) {
            player.getValue().giveEXP(Globals.calcEXPtoNxtLvl(stats[STAT_LEVEL]) / 3);
            player.getValue().giveDrop(stats[STAT_LEVEL]);
        }
        isDead = true;
    }

    public void updateBuffs() {
        isStun = null;
        isKnockback = null;
        LinkedList<Byte> remove = new LinkedList<>();
        for (Map.Entry<Byte, Buff> bEntry : buffs.entrySet()) {
            Buff b = bEntry.getValue();
            b.update();
            if (isStun == null && b instanceof BuffStun) {
                isStun = b;
                stunReduction += 100;
            } else if (isKnockback == null && b instanceof BuffKnockback) {
                isKnockback = b;
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
                if (b instanceof BuffStun) {
                    b.reduceDuration(stunReduction);
                }
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
            //queueBossState(STATE_JUMP);
        }

        setYSpeed(ySpeed + Globals.GRAVITY);
        if (ySpeed >= Globals.MAX_FALLSPEED) {
            setYSpeed(Globals.MAX_FALLSPEED);
        }

        isFalling = map.isFalling(x, y, ySpeed);
        if (!isFalling && ySpeed > 0) {
            y = map.getValidY(x, y, ySpeed);
            setYSpeed(0);
            //queueBossState(STATE_STAND);
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

    public void updateSkillCd() {
        for (Map.Entry<Byte, Skill> s : skills.entrySet()) {
            s.getValue().reduceCooldown((long) (Globals.LOGIC_UPDATE / 1000000));
        }
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
        nextState = newState;
    }

    public void setBossState(byte newState) {
        bossState = newState;
        frame = 0;
        updateAnimState = true;
    }

    public void sendPos() {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_BOSS_SET_POS;
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
        bytes[0] = Globals.DATA_BOSS_SET_FACING;
        bytes[1] = key;
        bytes[2] = facing;
        sender.sendAll(bytes, logic.getRoom());
        updateFacing = false;
    }

    public void sendState() {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_BOSS_SET_STATE;
        bytes[1] = key;
        bytes[2] = animState;
        bytes[3] = frame;
        sender.sendAll(bytes, logic.getRoom());
        updateAnimState = false;
    }

    public void sendDamage(Damage dmg) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 3];
        bytes[0] = Globals.DATA_DAMAGE;
        bytes[1] = dmg.getDamageType();
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

    public static void sendBossParticle(byte key, byte room, byte particleID, double x, double y) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_BOSS_PARTICLE_EFFECT;
        bytes[1] = key;
        bytes[2] = particleID;
        byte[] posXInt = Globals.intToByte((int) x);
        bytes[3] = posXInt[0];
        bytes[4] = posXInt[1];
        bytes[5] = posXInt[2];
        bytes[6] = posXInt[3];
        byte[] posYInt = Globals.intToByte((int) y);
        bytes[7] = posYInt[0];
        bytes[8] = posYInt[1];
        bytes[9] = posYInt[2];
        bytes[10] = posYInt[3];
        sender.sendAll(bytes, room);
    }
}
