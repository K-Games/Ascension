package blockfighter.server.entities.mob;

import blockfighter.server.LogicModule;
import blockfighter.server.Room;
import blockfighter.server.entities.GameEntity;
import blockfighter.server.entities.buff.Buff;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.Player;
import blockfighter.server.maps.GameMap;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Mob extends Thread implements GameEntity {

    public final static int NUM_STATS = 3;
    public final static byte STAT_LEVEL = 0,
            STAT_MAXHP = 1,
            STAT_MINHP = 2;

    public final static byte STATE_STAND = 0x00,
            STATE_WALK = 0x01,
            STATE_JUMP = 0x02,
            STATE_DYING = 0x03,
            STATE_DEAD = 0x04;

    public final static byte ANIM_STAND = 0x00,
            ANIM_WALK = 0x01,
            ANIM_JUMP = 0x02,
            ANIM_DYING = 0x03,
            ANIM_DEAD = 0x04;

    public final static byte MOB_BOSS_LIGHTNING = 0x00,
            MOB_BOSS_SHADOWFIEND = 0x01;

    protected final int key;
    protected final LogicModule logic;
    protected final Room room;
    protected double x, y, ySpeed, xSpeed;
    protected boolean isFalling = false, isDead = false;
    protected boolean updatePos = false, updateFacing = false, updateAnimState = false;
    protected byte mobState, animState, facing, frame;
    protected Rectangle2D.Double hitbox;
    protected double[] stats = new double[NUM_STATS];

    private final HashMap<Byte, Object> validMobSkillStates;
    protected ConcurrentHashMap<Integer, Buff> buffs = new ConcurrentHashMap<>(10, 0.9f, 1);
    protected Buff isStun, isKnockback;
    protected double dmgReduct, dmgAmp;
    protected Byte nextState;
    protected int stunReduction = 0;

    protected final GameMap map;

    protected ConcurrentLinkedQueue<Damage> damageQueue = new ConcurrentLinkedQueue<>();
    protected ConcurrentLinkedQueue<Integer> healQueue = new ConcurrentLinkedQueue<>();
    protected ConcurrentLinkedQueue<Buff> buffQueue = new ConcurrentLinkedQueue<>();
    protected ConcurrentLinkedQueue<Integer> buffKeys = new ConcurrentLinkedQueue<>();

    protected ConcurrentHashMap<Player, Double> aggroCounter = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Byte, MobSkill> skills = new ConcurrentHashMap<>(2, 0.9f, 1);
    protected long skillCastTime = 0, lastHPSend = 0, lastFrameTime = 0;
    protected int skillCounter = 0;
    protected byte type;
    protected int maxBuffKeys = 0;

    public Mob(final LogicModule l, final GameMap map, final double x, final double y, final byte numSkills) {
        this.logic = l;
        this.room = l.getRoom();
        this.key = this.room.getNextMobKey();
        this.x = x;
        this.y = y;
        this.hitbox = new Rectangle2D.Double(x - 50, y - 180, 100, 180);
        this.map = map;
        this.facing = Globals.RIGHT;
        this.mobState = STATE_STAND;
        this.frame = 0;
        initializeBuffKeys();
        this.validMobSkillStates = new HashMap<>(numSkills);
    }

    private void initializeBuffKeys() {
        for (int i = this.maxBuffKeys; i < this.maxBuffKeys + 150; i++) {
            this.buffKeys.add(i);
        }
        this.maxBuffKeys += 150;
    }

    public void addValidMobSkillState(final byte state) {
        this.validMobSkillStates.put(state, null);
    }

    public void addSkill(final byte sc, final MobSkill s) {
        this.skills.put(sc, s);
    }

    public boolean canCast(final byte sc) {
        return this.skills.get(sc).canCast();
    }

    public void setCooldown(final byte sc) {
        this.skills.get(sc).setCooldown();
    }

    public void reduceCooldown(final byte sc, final int amount) {
        this.skills.get(sc).reduceCooldown(amount);
    }

    public Player getTarget() {
        Player target = null;
        double maxAggro = 0;
        return target;
    }

    public boolean isUsingSkill() {
        return validMobSkillStates.containsKey(this.mobState);
    }

    public boolean isDead() {
        return this.isDead;
    }

    private void returnBuffKey(final int bKey) {
        this.buffKeys.add(bKey);
    }

    public Rectangle2D.Double getHitbox() {
        return this.hitbox;
    }

    private Integer getNextBuffKey() {
        Integer nextKey = this.buffKeys.poll();
        while (nextKey == null) {
            this.buffKeys.add(this.maxBuffKeys);
            this.maxBuffKeys++;
            nextKey = this.buffKeys.poll();
        }
        return nextKey;
    }

    public double[] getStats() {
        return this.stats;
    }

    public byte getType() {
        return this.type;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public int getKey() {
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

    public MobSkill getSkill(final byte skillCode) {
        return this.skills.get(skillCode);
    }

    public int getSkillCounter() {
        return this.skillCounter;
    }

    public long getSkillCastTime() {
        return this.skillCastTime;
    }

    public void incrementSkillCounter() {
        this.skillCounter++;
    }

    public void setPos(final double x, final double y) {
        this.x = x;
        this.y = y;
        this.updatePos = true;
    }

    public void setYSpeed(final double speed) {
        this.ySpeed = speed;
    }

    public void setXSpeed(final double speed) {
        this.xSpeed = speed;
    }

    @Override
    public void run() {
        try {
            update();
        } catch (final Exception ex) {
            Globals.logError(ex.toString(), ex, true);
        }
    }

    @Override
    public abstract void update();

    public void updateMobState() {
        if (this.nextState != null && this.mobState != this.nextState) {
            setMobState(this.nextState);
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
                double amount = dmg.getDamage() * this.dmgAmp;
                // Buff Reductions
                amount = amount * this.dmgReduct;

                dmg.proc();
                addAggro(dmg.getOwner(), amount);
                if (!dmg.isHidden()) {
                    sendDamage(dmg, (int) amount);
                }
                this.stats[STAT_MINHP] -= amount;
                this.lastHPSend = 0;
            }
        }

        while (!this.healQueue.isEmpty()) {
            final Integer heal = this.healQueue.poll();
            if (heal != null) {
                this.stats[STAT_MINHP] += heal;
                this.lastHPSend = 0;
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

        if (Globals.nsToMs(this.logic.getTime() - this.lastHPSend) >= 150) {
            final byte[] minHP = Globals.intToBytes((int) (this.stats[STAT_MINHP] / this.stats[STAT_MAXHP] * 10000));
            final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_MOB_GET_STAT;
            final byte[] intKey = Globals.intToBytes(this.key);
            System.arraycopy(intKey, 0, bytes, 1, intKey.length);
            bytes[5] = STAT_MINHP;
            System.arraycopy(minHP, 0, bytes, 6, minHP.length);
            PacketSender.sendAll(bytes, this.room.getRoomNumber());
            this.lastHPSend = this.logic.getTime();
        }
    }

    private void die() {
        for (final Map.Entry<Byte, Player> player : this.room.getPlayers().entrySet()) {
            player.getValue().giveEXP(Globals.calcEXPtoNxtLvl(this.stats[STAT_LEVEL]) / 3);
            player.getValue().giveDrop(this.stats[STAT_LEVEL]);
        }
        this.isDead = true;
    }

    public void updateBuffs() {
        //Reset buff trackers
        this.isStun = null;
        this.isKnockback = null;
        this.dmgReduct = 1;
        this.dmgAmp = 1;

        // While buff queue has things
            //Poll buff
            //Get next buff key
            //Ensure key and buff isnt null
                //add buff

        //Iterate through buffs
            //Update buffs

            //Track stun if stun is null
            //Track Knockback if it is null

            // Add all the damage reduction buffs(Multiplicative)

            // Add all the damage intake amplification(Additive)

            //Check buff expired
            //removed from iterator
            //return buff key
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
            this.y = this.map.getValidY(this.x, this.y);
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

    public boolean updateX(final double change) {
        if (change == 0) {
            return false;
        }

        if (this.map.isOutOfBounds(this.x + change)) {
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

        this.y = this.y + change;
        this.updatePos = true;
        return true;
    }

    public void queueMobState(final byte newState) {
        this.nextState = newState;
    }

    public void setMobState(final byte newState) {
        this.mobState = newState;
        this.frame = 0;
        this.updateAnimState = true;
    }

    public void sendPos() {
        final byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_INT * 3];
        bytes[0] = Globals.DATA_MOB_SET_POS;
        final byte[] intKey = Globals.intToBytes(this.key);
        System.arraycopy(intKey, 0, bytes, 1, intKey.length);
        final byte[] posXInt = Globals.intToBytes((int) this.x);
        System.arraycopy(posXInt, 0, bytes, 5, posXInt.length);
        final byte[] posYInt = Globals.intToBytes((int) this.y);
        System.arraycopy(posYInt, 0, bytes, 9, posYInt.length);
        PacketSender.sendAll(bytes, this.room.getRoomNumber());
        this.updatePos = false;
    }

    public void sendFacing() {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_MOB_SET_FACING;
        final byte[] intKey = Globals.intToBytes(this.key);
        System.arraycopy(intKey, 0, bytes, 1, intKey.length);
        bytes[5] = this.facing;
        PacketSender.sendAll(bytes, this.room.getRoomNumber());
        this.updateFacing = false;
    }

    public void sendState() {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3+ Globals.PACKET_INT];
        bytes[0] = Globals.DATA_MOB_SET_STATE;
        final byte[] intKey = Globals.intToBytes(this.key);
        System.arraycopy(intKey, 0, bytes, 1, intKey.length);
        bytes[5] = this.animState;
        bytes[6] = this.frame;
        PacketSender.sendAll(bytes, this.room.getRoomNumber());
        this.updateAnimState = false;
    }

    public void sendDamage(final Damage dmg, final int dmgDealt) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 3];
        bytes[0] = Globals.DATA_NUMBER;
        bytes[1] = dmg.getDamageType();
        final byte[] posXInt = Globals.intToBytes((int) dmg.getDmgPoint().x);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        final byte[] posYInt = Globals.intToBytes((int) dmg.getDmgPoint().y);
        bytes[6] = posYInt[0];
        bytes[7] = posYInt[1];
        bytes[8] = posYInt[2];
        bytes[9] = posYInt[3];
        final byte[] d = Globals.intToBytes(dmgDealt);
        bytes[10] = d[0];
        bytes[11] = d[1];
        bytes[12] = d[2];
        bytes[13] = d[3];
        PacketSender.sendAll(bytes, this.room.getRoomNumber());
    }

    public static void sendMobParticle(final int key, final byte roomNumber, final byte particleID, final double x, final double y) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_MOB_PARTICLE_EFFECT;
        final byte[] intKey = Globals.intToBytes(key);
        System.arraycopy(intKey, 0, bytes, 1, intKey.length);
        bytes[5] = particleID;
        final byte[] posXInt = Globals.intToBytes((int) x);
        System.arraycopy(posXInt, 0, bytes, 6, posXInt.length);
        final byte[] posYInt = Globals.intToBytes((int) y);
        System.arraycopy(posYInt, 0, bytes, 10, posYInt.length);
        PacketSender.sendAll(bytes, roomNumber);
    }
}
