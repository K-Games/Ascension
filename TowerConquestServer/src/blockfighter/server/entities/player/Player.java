package blockfighter.server.entities.player;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.GameEntity;
import blockfighter.server.entities.buff.Buff;
import blockfighter.server.entities.buff.BuffDmgIncrease;
import blockfighter.server.entities.buff.BuffDmgReduct;
import blockfighter.server.entities.buff.BuffDmgTakenAmp;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.buff.BuffPassiveBarrier;
import blockfighter.server.entities.buff.BuffShieldDash;
import blockfighter.server.entities.buff.BuffShieldReflect;
import blockfighter.server.entities.buff.BuffStun;
import blockfighter.server.entities.buff.BuffSwordSlash;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.player.skills.SkillBowArc;
import blockfighter.server.entities.player.skills.SkillBowFrost;
import blockfighter.server.entities.player.skills.SkillBowPower;
import blockfighter.server.entities.player.skills.SkillBowRapid;
import blockfighter.server.entities.player.skills.SkillBowStorm;
import blockfighter.server.entities.player.skills.SkillBowVolley;
import blockfighter.server.entities.player.skills.SkillPassive12;
import blockfighter.server.entities.player.skills.SkillPassiveBarrier;
import blockfighter.server.entities.player.skills.SkillPassiveBowMastery;
import blockfighter.server.entities.player.skills.SkillPassiveDualSword;
import blockfighter.server.entities.player.skills.SkillPassiveKeenEye;
import blockfighter.server.entities.player.skills.SkillPassiveResistance;
import blockfighter.server.entities.player.skills.SkillPassiveRevive;
import blockfighter.server.entities.player.skills.SkillPassiveShadowAttack;
import blockfighter.server.entities.player.skills.SkillPassiveShieldMastery;
import blockfighter.server.entities.player.skills.SkillPassiveTactical;
import blockfighter.server.entities.player.skills.SkillPassiveVitalHit;
import blockfighter.server.entities.player.skills.SkillPassiveWillpower;
import blockfighter.server.entities.player.skills.SkillShieldCharge;
import blockfighter.server.entities.player.skills.SkillShieldDash;
import blockfighter.server.entities.player.skills.SkillShieldFortify;
import blockfighter.server.entities.player.skills.SkillShieldIron;
import blockfighter.server.entities.player.skills.SkillShieldReflect;
import blockfighter.server.entities.player.skills.SkillShieldToss;
import blockfighter.server.entities.player.skills.SkillSwordCinder;
import blockfighter.server.entities.player.skills.SkillSwordGash;
import blockfighter.server.entities.player.skills.SkillSwordPhantom;
import blockfighter.server.entities.player.skills.SkillSwordSlash;
import blockfighter.server.entities.player.skills.SkillSwordTaunt;
import blockfighter.server.entities.player.skills.SkillSwordVorpal;
import blockfighter.server.maps.GameMap;
import blockfighter.server.net.PacketSender;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Player entities on the server.
 *
 * @author Ken Kwan
 */
public class Player extends Thread implements GameEntity {

    private static final HashMap<Byte, Object> VALID_PLAYER_SKILL_STATES = new HashMap<>(18);
    private static final HashMap<Byte, Byte> PLAYER_STATE_SKILLCODE = new HashMap<>(18);

    public final static byte PLAYER_STATE_STAND = 0x00,
            PLAYER_STATE_WALK = 0x01,
            PLAYER_STATE_JUMP = 0x02,
            PLAYER_STATE_SWORD_VORPAL = 0x03,
            PLAYER_STATE_SWORD_PHANTOM = 0x04,
            PLAYER_STATE_SWORD_CINDER = 0x05,
            PLAYER_STATE_SWORD_GASH = 0x06,
            PLAYER_STATE_SWORD_SLASH = 0x07,
            PLAYER_STATE_SWORD_TAUNT = 0x08,
            PLAYER_STATE_BOW_ARC = 0x09,
            PLAYER_STATE_BOW_POWER = 0x0A,
            PLAYER_STATE_BOW_RAPID = 0x0B,
            PLAYER_STATE_BOW_FROST = 0x0C,
            PLAYER_STATE_BOW_STORM = 0x0D,
            PLAYER_STATE_BOW_VOLLEY = 0x0E,
            PLAYER_STATE_SHIELD_CHARGE = 0x0F,
            PLAYER_STATE_SHIELD_DASH = 0x10,
            PLAYER_STATE_SHIELD_FORTIFY = 0x11,
            PLAYER_STATE_SHIELD_IRON = 0x12,
            PLAYER_STATE_SHIELD_REFLECT = 0x13,
            PLAYER_STATE_SHIELD_TOSS = 0x14,
            PLAYER_STATE_DEAD = 0x15;

    private final byte key;
    private final LogicModule logic;
    private UUID uniqueID;
    private String name = "";
    private double x, y, ySpeed, xSpeed;
    private final boolean[] dirKeydown = new boolean[4];
    private boolean isFalling = false, isJumping = false, isInvulnerable = false, isDead = false, isRemoveDebuff = false;
    private boolean updatePos = false, updateFacing = false, updateAnimState = false;
    private byte playerState, animState, facing, frame;

    private final Rectangle2D.Double hitbox;

    private final ConcurrentHashMap<Integer, Buff> buffs = new ConcurrentHashMap<>(150, 0.9f, 1);
    private Buff stunDebuff, knockbackDebuff, barrierBuff;
    private final ArrayList<Buff> reflects = new ArrayList<>(10);
    private double dmgReduct, dmgAmp;
    private double barrierDmgTaken = 0, tacticalDmgMult = 0;

    private final InetAddress address;
    private final int port;
    private static PacketSender sender;
    private final GameMap map;
    private final double[] stats = new double[Globals.NUM_STATS], bonusStats = new double[Globals.NUM_STATS];

    private final int[] equips = new int[Globals.NUM_EQUIP_SLOTS];
    private final ConcurrentHashMap<Byte, Skill> skills = new ConcurrentHashMap<>(Skill.NUM_SKILLS, 0.9f, 1);
    private boolean connected = true;

    private final ConcurrentLinkedQueue<Damage> damageQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Integer> healQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<byte[]> skillUseQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Buff> buffQueue = new ConcurrentLinkedQueue<>();

    private final ConcurrentLinkedQueue<Integer> buffKeys = new ConcurrentLinkedQueue<>();
    private int maxBuffKeys = 0;
    private Byte nextState;

    private long lastActionTime = 0;
    private long skillCastTime = 0,
            deathTime = 0,
            lastHPSendTime = 0,
            lastFrameTime = 0;
    private int skillCounter = 0;

    static {
        initializeValidPlayerSkillStates();
    }

    public static void initializeValidPlayerSkillStates() {
        VALID_PLAYER_SKILL_STATES.put(PLAYER_STATE_SWORD_VORPAL, null);
        VALID_PLAYER_SKILL_STATES.put(PLAYER_STATE_SWORD_PHANTOM, null);
        VALID_PLAYER_SKILL_STATES.put(PLAYER_STATE_SWORD_CINDER, null);
        VALID_PLAYER_SKILL_STATES.put(PLAYER_STATE_SWORD_GASH, null);
        VALID_PLAYER_SKILL_STATES.put(PLAYER_STATE_SWORD_SLASH, null);
        VALID_PLAYER_SKILL_STATES.put(PLAYER_STATE_SWORD_TAUNT, null);
        VALID_PLAYER_SKILL_STATES.put(PLAYER_STATE_BOW_ARC, null);
        VALID_PLAYER_SKILL_STATES.put(PLAYER_STATE_BOW_POWER, null);
        VALID_PLAYER_SKILL_STATES.put(PLAYER_STATE_BOW_RAPID, null);
        VALID_PLAYER_SKILL_STATES.put(PLAYER_STATE_BOW_FROST, null);
        VALID_PLAYER_SKILL_STATES.put(PLAYER_STATE_BOW_STORM, null);
        VALID_PLAYER_SKILL_STATES.put(PLAYER_STATE_BOW_VOLLEY, null);
        VALID_PLAYER_SKILL_STATES.put(PLAYER_STATE_SHIELD_CHARGE, null);
        VALID_PLAYER_SKILL_STATES.put(PLAYER_STATE_SHIELD_DASH, null);
        VALID_PLAYER_SKILL_STATES.put(PLAYER_STATE_SHIELD_FORTIFY, null);
        VALID_PLAYER_SKILL_STATES.put(PLAYER_STATE_SHIELD_IRON, null);
        VALID_PLAYER_SKILL_STATES.put(PLAYER_STATE_SHIELD_REFLECT, null);
        VALID_PLAYER_SKILL_STATES.put(PLAYER_STATE_SHIELD_TOSS, null);

        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SWORD_VORPAL, Skill.SWORD_VORPAL);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SWORD_PHANTOM, Skill.SWORD_PHANTOM);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SWORD_CINDER, Skill.SWORD_CINDER);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SWORD_GASH, Skill.SWORD_GASH);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SWORD_SLASH, Skill.SWORD_SLASH);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SWORD_TAUNT, Skill.SWORD_TAUNT);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_BOW_ARC, Skill.BOW_ARC);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_BOW_POWER, Skill.BOW_POWER);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_BOW_RAPID, Skill.BOW_RAPID);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_BOW_FROST, Skill.BOW_FROST);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_BOW_STORM, Skill.BOW_STORM);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_BOW_VOLLEY, Skill.BOW_VOLLEY);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SHIELD_CHARGE, Skill.SHIELD_CHARGE);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SHIELD_DASH, Skill.SHIELD_DASH);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SHIELD_FORTIFY, Skill.SHIELD_FORTIFY);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SHIELD_IRON, Skill.SHIELD_IRON);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SHIELD_REFLECT, Skill.SHIELD_REFLECT);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SHIELD_TOSS, Skill.SHIELD_TOSS);

    }

    /**
     * Create a new player entity in the server.
     *
     * @param key The key of this player in the player array in logic module
     * @param address IP address of player
     * @param port Connected port
     * @param map Reference to server's loaded map
     * @param l Reference to Logic module
     */
    public Player(final LogicModule l, final byte key, final InetAddress address, final int port, final GameMap map) {
        this.logic = l;
        this.lastActionTime = l.getTime();
        this.key = key;
        this.address = address;
        this.port = port;
        Point2D.Double spawn = map.getRandomSpawnPoint();
        this.x = spawn.x;
        this.y = spawn.y;
        this.hitbox = new Rectangle2D.Double(x - 20, y - 100, 40, 100);
        this.map = map;
        this.facing = Globals.RIGHT;
        this.playerState = PLAYER_STATE_STAND;
        this.frame = 0;
        initializeBuffKeys();
    }

    private void initializeBuffKeys() {
        for (int i = this.maxBuffKeys; i < this.maxBuffKeys + 150; i++) {
            this.buffKeys.add(i);
        }
        this.maxBuffKeys += 150;
    }

    /**
     * Set the static packet sender for Player class
     *
     * @param ps Server PacketSender
     */
    public static void setPacketSender(final PacketSender ps) {
        sender = ps;
    }

    /**
     * Return a freed buff key to the queue
     *
     * @param bKey Buff key to be queued
     */
    public void returnBuffKey(final int bKey) {
        this.buffKeys.add(bKey);
    }

    /**
     * Get the next buff key from queue
     *
     * @return Byte - Free buff key, null if none are available.
     */
    public Integer getNextBuffKey() {
        Integer nextKey = this.buffKeys.poll();
        while (nextKey == null) {
            this.buffKeys.add(this.maxBuffKeys);
            this.maxBuffKeys++;
            nextKey = this.buffKeys.poll();
        }
        return nextKey;
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

    public GameMap getMap() {
        return map;
    }

    public byte getPlayerState() {
        return this.playerState;
    }

    public byte getAnimState() {
        return this.animState;
    }

    /**
     * Return this player's IP address.
     * <p>
     * Used for broadcasting to player with UDP.
     * </p>
     *
     * @return The player's IP
     */
    public InetAddress getAddress() {
        return this.address;
    }

    /**
     * Return this player's connected port.
     * <p>
     * Used for broadcasting to player with UDP.
     * </p>
     *
     * @return The player's port in int
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Return this player's facing direction.
     * <p>
     * Direction value is found in Globals.
     * </p>
     *
     * @return The player's facing direction in byte
     */
    public byte getFacing() {
        return this.facing;
    }

    /**
     * Return this player's current animation frame.
     *
     * @return The player's current animation frame
     */
    public byte getFrame() {
        return this.frame;
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

    /**
     * Get the item codes of the equipment on this Player
     *
     * @return int[] - Equipment Item Codes
     */
    public int[] getEquips() {
        return this.equips;
    }

    public boolean isDead() {
        return this.isDead;
    }

    private void setDead(final boolean set) {
        this.isDead = set;
    }

    /**
     * Get the skill level of specific skill using a skill code
     *
     * @param skillCode Skill code of skill
     * @return The level of the skill
     */
    public int getSkillLevel(final byte skillCode) {
        if (!hasSkill(skillCode)) {
            return -1;
        }
        return this.skills.get(skillCode).getLevel();
    }

    public Skill getSkill(final byte skillCode) {
        return this.skills.get(skillCode);
    }

    public boolean isSkillMaxed(final byte skillCode) {
        if (!hasSkill(skillCode)) {
            return false;
        }
        return this.skills.get(skillCode).isMaxed();
    }

    public boolean hasSkill(final byte skillCode) {
        return this.skills.containsKey(skillCode);
    }

    public Map.Entry<Integer, Buff> hasBuff(final Class<?> buffType) {
        for (final Map.Entry<Integer, Buff> bEntry : this.buffs.entrySet()) {
            final Buff b = bEntry.getValue();
            if (buffType.isInstance(b)) {
                return bEntry;
            }
        }
        return null;
    }

    /**
     * Set this player's movement when server receives packet that key is pressed.
     *
     * @param direction The direction to be set
     * @param move True when pressed, false when released
     */
    public void setDirKeydown(final int direction, final boolean move) {
        if (move) {
            this.lastActionTime = this.logic.getTime();
        }
        this.dirKeydown[direction] = move;
    }

    /**
     * Set the player's x and y position.
     * <p>
     * This does not interpolate. The player is instantly moved to this location.
     * </p>
     *
     * @param x New x location in double
     * @param y New y location in double
     */
    public void setPos(final double x, final double y) {
        if (this.map.isOutOfBounds(x)) {
            this.x = this.map.getValidX(x);
        } else {
            this.x = x;
        }
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
        if (isDead()) {
            this.xSpeed = 0;
        }
        this.xSpeed = speed;
    }

    /**
     * Set the level of skill with skill code
     *
     * @param skillCode Skill to be set
     * @param level Level of skill to be set
     */
    public void setSkill(final byte skillCode, final byte level) {
        Skill newSkill = null;
        switch (skillCode) {
            case Skill.SWORD_CINDER:
                newSkill = new SkillSwordCinder(this.logic);
                break;
            case Skill.SWORD_GASH:
                newSkill = new SkillSwordGash(this.logic);
                break;
            case Skill.SWORD_PHANTOM:
                newSkill = new SkillSwordPhantom(this.logic);
                break;
            case Skill.SWORD_SLASH:
                newSkill = new SkillSwordSlash(this.logic);
                break;
            case Skill.SWORD_TAUNT:
                newSkill = new SkillSwordTaunt(this.logic);
                break;
            case Skill.SWORD_VORPAL:
                newSkill = new SkillSwordVorpal(this.logic);
                break;
            case Skill.BOW_ARC:
                newSkill = new SkillBowArc(this.logic);
                break;
            case Skill.BOW_FROST:
                newSkill = new SkillBowFrost(this.logic);
                break;
            case Skill.BOW_POWER:
                newSkill = new SkillBowPower(this.logic);
                break;
            case Skill.BOW_RAPID:
                newSkill = new SkillBowRapid(this.logic);
                break;
            case Skill.BOW_STORM:
                newSkill = new SkillBowStorm(this.logic);
                break;
            case Skill.BOW_VOLLEY:
                newSkill = new SkillBowVolley(this.logic);
                break;
            case Skill.SHIELD_FORTIFY:
                newSkill = new SkillShieldFortify(this.logic);
                break;
            case Skill.SHIELD_IRON:
                newSkill = new SkillShieldIron(this.logic);
                break;
            case Skill.SHIELD_CHARGE:
                newSkill = new SkillShieldCharge(this.logic);
                break;
            case Skill.SHIELD_REFLECT:
                newSkill = new SkillShieldReflect(this.logic);
                break;
            case Skill.SHIELD_TOSS:
                newSkill = new SkillShieldToss(this.logic);
                break;
            case Skill.SHIELD_DASH:
                newSkill = new SkillShieldDash(this.logic);
                break;
            case Skill.PASSIVE_DUALSWORD:
                newSkill = new SkillPassiveDualSword(this.logic);
                break;
            case Skill.PASSIVE_KEENEYE:
                newSkill = new SkillPassiveKeenEye(this.logic);
                break;
            case Skill.PASSIVE_VITALHIT:
                newSkill = new SkillPassiveVitalHit(this.logic);
                break;
            case Skill.PASSIVE_SHIELDMASTERY:
                newSkill = new SkillPassiveShieldMastery(this.logic);
                break;
            case Skill.PASSIVE_BARRIER:
                newSkill = new SkillPassiveBarrier(this.logic);
                break;
            case Skill.PASSIVE_RESIST:
                newSkill = new SkillPassiveResistance(this.logic);
                break;
            case Skill.PASSIVE_BOWMASTERY:
                newSkill = new SkillPassiveBowMastery(this.logic);
                break;
            case Skill.PASSIVE_WILLPOWER:
                newSkill = new SkillPassiveWillpower(this.logic);
                break;
            case Skill.PASSIVE_TACTICAL:
                newSkill = new SkillPassiveTactical(this.logic);
                break;
            case Skill.PASSIVE_REVIVE:
                newSkill = new SkillPassiveRevive(this.logic);
                break;
            case Skill.PASSIVE_SHADOWATTACK:
                newSkill = new SkillPassiveShadowAttack(this.logic);
                break;
            case Skill.PASSIVE_12:
                newSkill = new SkillPassive12(this.logic);
                break;
        }
        if (newSkill != null) {
            newSkill.setLevel(level);
            this.skills.put(skillCode, newSkill);
        }
    }

    @Override
    public void run() {
        try {
            update();
        } catch (final Exception ex) {
            Globals.logError(ex.getLocalizedMessage(), ex, true);
        }
    }

    /**
     * Updates all logic of this player.
     * <p>
     * Must be called every tick. Specific logic updates are separated into other methods. Specific logic updates must be private.
     * </p>
     */
    @Override
    public void update() {
        if (!isConnected()) {
            return;
        }

        // Update Timers/Game principles(Gravity)
        updateBuffs();

        queuePlayerState(PLAYER_STATE_STAND);
        updateFall();
        final boolean movedX = updateX(this.xSpeed);
        this.hitbox.x = this.x - 20;
        this.hitbox.y = this.y - 100;

        if (isDead()) {
            // Update respawn Timer
            updateDead();
        } else {
            // Update Actions
            if (isStunned() && !isKnockback()) {
                setXSpeed(0);
            }

            if (!isUsingSkill() && !isStunned() && !isKnockback()) {
                updateFacing();
                if (!this.isJumping && !this.isFalling) {
                    updateWalk(movedX);
                    updateJump();
                }
            }

            updateSkillCast();
            updatePlayerState();
            if (isUsingSkill()) {
                updateSkillUse();
            }

            updateHP();
        }
        updateAnimState();
        if (this.updatePos) {
            sendPos();
            this.updateFacing = false;
        }
        if (this.updateFacing) {
            sendFacing();
        }
        if (this.updateAnimState) {
            sendState();
        }

        if (this.connected && Globals.nsToMs(this.logic.getTime() - this.lastActionTime) >= Globals.SERVER_MAX_IDLE) {
            Globals.log("Player", this.address + ":" + this.port + " Idle disconnected Key: " + this.key, Globals.LOG_TYPE_DATA, true);
            disconnect();
        }
    }

    private void updateDead() {
        final int deathDuration = Globals.nsToMs(this.logic.getTime() - this.deathTime);
        this.damageQueue.clear();
        this.healQueue.clear();
        this.skillUseQueue.clear();
        this.buffQueue.clear();
        if (deathDuration < 500 && this.xSpeed == 0) {
            setXSpeed((this.facing == Globals.LEFT) ? 1.5 : -1.5);
        } else if (deathDuration >= 500) {
            setXSpeed(0);
        }
        if (deathDuration >= 5000) {
            respawn();
        }
    }

    private void updatePlayerState() {
        if (this.nextState != null && !isUsingSkill() && this.playerState != this.nextState) {
            setPlayerState(this.nextState);
            this.nextState = null;
        }
        if (isUsingSkill()) {
            this.nextState = null;
        }
    }

    private void castSkill(final byte skillCode) {
        if (!this.skills.get(skillCode).canCast(this)) {
            return;
        }
        this.skillCounter = 0;
        this.skillCastTime = this.logic.getTime();
        // Globals.log("DATA_PLAYER_CASTSKILL", "Key: " + key + " Room: " + logic.getRoom() + " Player: " + getPlayerName() + " Skill: " +
        // data[3], Globals.LOG_TYPE_DATA, true);

        queuePlayerState(this.skills.get(skillCode).castPlayerState());
        this.skills.get(skillCode).setCooldown();
        sendCooldown(skillCode);

        // Tactical Execution Passive
        // Add after being able to cast skill
        if (hasSkill(Skill.PASSIVE_TACTICAL) && this.tacticalDmgMult < 0.01 + 0.01 * getSkillLevel(Skill.PASSIVE_TACTICAL)) {
            this.tacticalDmgMult += 0.01;
        }
    }

    private void updateSkillCast() {
        if (isUsingSkill()) {
            this.skillUseQueue.clear();
            return;
        }
        if (this.skillUseQueue.isEmpty()) {
            return;
        }

        byte[] data = this.skillUseQueue.poll();
        while (!this.skillUseQueue.isEmpty() && this.skills.get(data[3]).getCooldown() < this.skills.get(data[3]).getMaxCooldown()) {
            data = this.skillUseQueue.poll();
        }

        this.skillUseQueue.clear();
        if (data != null) {
            byte skillCode = data[3];
            if (skillCode == Skill.SHIELD_IRON || (!isStunned() && !isKnockback())) {
                if (hasSkill(skillCode)) {
                    castSkill(skillCode);
                }
            }
        }
    }

    private void updateSkillUse() {
        if (!isKnockback()) {
            setXSpeed(0);
        }
        getSkill(PLAYER_STATE_SKILLCODE.get(this.playerState)).updateSkillUse(this);
    }

    public boolean updateSkillEnd(boolean ended) {
        if (ended) {
            setInvulnerable(false);
            setPlayerState(PLAYER_STATE_STAND);
            return true;
        }
        return false;
    }

    public boolean updateSkillEnd(final int currentSkillDuration, final int skillEndDuration, final boolean isCanceledByStun, final boolean isCanceledByKnockback) {
        return updateSkillEnd(currentSkillDuration >= skillEndDuration || (isCanceledByStun && isStunned() || (isCanceledByKnockback && isKnockback())));
    }

    private void updateHP() {
        int sinceLastHPSend = Globals.nsToMs(this.logic.getTime() - this.lastHPSendTime);
        // Empty damage queued
        if (isInvulnerable()) {
            // Take no damage
            this.damageQueue.clear();
        }
        while (!this.damageQueue.isEmpty()) {
            final Damage dmg = this.damageQueue.poll();
            if (dmg != null) {
                int amount = (int) (dmg.getDamage() * this.dmgAmp);
                // Proc stuff like shadow attack
                dmg.proc();

                // Check if I have reflect damage buff and reflect off owner
                if (dmg.canReflect()) {
                    for (final Buff b : this.reflects) {
                        if (b instanceof BuffShieldReflect) {
                            Player reflectOwner = ((BuffShieldReflect) b).getOwner();
                            SkillShieldReflect reflectSkill = (SkillShieldReflect) reflectOwner.getSkill(Skill.SHIELD_REFLECT);
                            reflectSkill.updateSkillReflectHit(amount, ((BuffShieldReflect) b).getMultiplier(), reflectOwner);
                        }
                    }
                }
                // If it isnt true damage do reduction
                if (!dmg.isTrueDamage()) {
                    amount = (int) (amount * this.stats[Globals.STAT_DAMAGEREDUCT]);
                }

                // Buff Reductions
                amount = (int) (amount * this.dmgReduct);

                // Defender Mastery Passive Reduction
                if (hasSkill(Skill.PASSIVE_SHIELDMASTERY)
                        && getItemType(this.equips[Globals.ITEM_WEAPON]) == Globals.ITEM_SWORD
                        && getItemType(this.equips[Globals.ITEM_OFFHAND]) == Globals.ITEM_SHIELD) {
                    amount = (int) (amount * (1 - (0.05 + 0.005 * getSkillLevel(Skill.PASSIVE_SHIELDMASTERY))));
                }

                // Dual Wield Passive Reduction
                if (hasSkill(Skill.PASSIVE_DUALSWORD)
                        && getItemType(this.equips[Globals.ITEM_WEAPON]) == Globals.ITEM_SWORD
                        && getItemType(this.equips[Globals.ITEM_OFFHAND]) == Globals.ITEM_SWORD) {
                    amount = (int) (amount * (1 - (0.01 * getSkillLevel(Skill.PASSIVE_DUALSWORD))));
                }

                // Resistance Passive
                if (hasSkill(Skill.PASSIVE_RESIST) && this.stats[Globals.STAT_MINHP] > this.stats[Globals.STAT_MAXHP] * 0.5
                        && this.skills.get(Skill.PASSIVE_RESIST).canCast()) {
                    if (amount > this.stats[Globals.STAT_MAXHP] * 0.5) {
                        amount = (int) (this.stats[Globals.STAT_MAXHP] * 0.5);
                        this.skills.get(Skill.PASSIVE_RESIST).setCooldown();
                        sendCooldown(Skill.PASSIVE_RESIST);
                        sendParticle(this.logic.getRoom(), Globals.PARTICLE_PASSIVE_RESIST, this.x, this.y);
                    }
                }
                // Barrier reduction
                if (this.barrierBuff != null) {
                    amount = (int) ((BuffPassiveBarrier) this.barrierBuff).reduceDmg(amount);
                    sendParticle(this.logic.getRoom(), Globals.PARTICLE_PASSIVE_BARRIER, dmg.getDmgPoint().x, dmg.getDmgPoint().y);
                }
                this.tacticalDmgMult = 0;
                // Send client damage display
                if (!dmg.isHidden()) {
                    if (dmg.getOwner() != null) {
                        sendParticle(this.logic.getRoom(), Globals.PARTICLE_BLOOD_HIT, dmg.getOwner().getKey(), this.key);
                    }
                    sendDamage(dmg, amount);
                }
                // Final damage taken
                this.stats[Globals.STAT_MINHP] -= amount;
                if (amount > 0) {
                    sinceLastHPSend = 150;
                }
                if (hasSkill(Skill.PASSIVE_BARRIER) && this.skills.get(Skill.PASSIVE_BARRIER).canCast()) {
                    this.barrierDmgTaken += amount;
                    if (this.barrierDmgTaken >= this.stats[Globals.STAT_MAXHP] * 0.5) {
                        this.barrierDmgTaken = 0;
                        queueBuff(new BuffPassiveBarrier(this.logic,
                                this.stats[Globals.STAT_MAXHP] * (0.1 + 0.005 * getSkillLevel(Skill.PASSIVE_BARRIER)),
                                this));
                        sendParticle(this.logic.getRoom(), Globals.PARTICLE_PASSIVE_BARRIER, dmg.getDmgPoint().x, dmg.getDmgPoint().y);
                        this.skills.get(Skill.PASSIVE_BARRIER).setCooldown();
                        sendCooldown(Skill.PASSIVE_BARRIER);
                    }
                }
                //System.out.println(this.getPlayerName() + ": Damage Multiplier: " + this.dmgAmp + " Raw: " + dmg.getDamage() + ", Taken: " + amount);
            }
        }
        // Empty healing queued
        while (!this.healQueue.isEmpty()) {
            final Integer heal = this.healQueue.poll();
            if (heal != null) {
                this.stats[Globals.STAT_MINHP] += heal;
                sinceLastHPSend = 150;
            }
        }
        // Add regenerated HP(1% of REGEN per 10ms tick)
        this.stats[Globals.STAT_MINHP] += this.stats[Globals.STAT_REGEN] / 100D;

        if (this.stats[Globals.STAT_MINHP] > this.stats[Globals.STAT_MAXHP]) {
            this.stats[Globals.STAT_MINHP] = this.stats[Globals.STAT_MAXHP];
        } else if (this.stats[Globals.STAT_MINHP] < 0) {
            this.stats[Globals.STAT_MINHP] = 0;
        }

        if (this.stats[Globals.STAT_MINHP] <= 0) {
            die();
        }

        // Update client hp every 150ms or if damaged/healed(excluding regen).
        if (sinceLastHPSend >= 150) {
            final byte[] stat = Globals.intToBytes((int) this.stats[Globals.STAT_MINHP]);
            final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
            bytes[0] = Globals.DATA_PLAYER_GET_STAT;
            bytes[1] = this.key;
            bytes[2] = Globals.STAT_MINHP;
            System.arraycopy(stat, 0, bytes, 3, stat.length);
            sender.sendPlayer(bytes, this);
            //this.nextHPSend = 150;
            this.lastHPSendTime = this.logic.getTime();
        }
    }

    private void updateBuffs() {
        // Update exisiting buffs
        this.stunDebuff = null;
        this.knockbackDebuff = null;
        this.barrierBuff = null;
        this.reflects.clear();
        this.dmgReduct = 1;
        this.dmgAmp = 1;

        // Empty and add buffs from queue
        while (!this.buffQueue.isEmpty()) {
            final Buff b = this.buffQueue.poll();
            if (b != null) {
                if (!canDebuffAffect() && b.isDebuff()) {
                    // Don't apply debuff when invulnerable
                    continue;
                }

                if (b instanceof BuffShieldDash) {
                    final Map.Entry<Integer, Buff> prevBuff = hasBuff(BuffShieldDash.class);
                    if (prevBuff != null) {
                        this.buffs.remove(prevBuff.getKey());
                    }
                } else if (b instanceof BuffSwordSlash) {
                    final Map.Entry<Integer, Buff> prevBuff = hasBuff(BuffSwordSlash.class);
                    if (prevBuff != null) {
                        this.buffs.remove(prevBuff.getKey());
                    }
                }
                final Integer bKey = getNextBuffKey();
                if (bKey != null) {
                    this.buffs.put(bKey, b);
                    //System.out.println(this.getPlayerName() + ":Applied " + b.getClass().getSimpleName() + " Buff");
                }
            }
        }

        final LinkedList<Integer> remove = new LinkedList<>();
        for (final Map.Entry<Integer, Buff> bEntry : this.buffs.entrySet()) {
            final Buff b = bEntry.getValue();
            b.update();

            // Track if stunned, knocked or has a barrier buff.
            if (canDebuffAffect() && b instanceof BuffStun) {
                if (this.stunDebuff == null) {
                    this.stunDebuff = b;
                }
            } else if (canDebuffAffect() && b instanceof BuffKnockback) {
                if (this.knockbackDebuff == null) {
                    this.knockbackDebuff = b;
                }
            } else if (b instanceof BuffShieldReflect) {
                this.reflects.add(b);
            } else if (b instanceof BuffPassiveBarrier) {
                if (this.barrierBuff == null) {
                    this.barrierBuff = b;
                }
            }

            // Add all the damage reduction buffs(Multiplicative)
            if (b instanceof BuffDmgReduct) {
                this.dmgReduct = this.dmgReduct * ((BuffDmgReduct) b).getDmgTakenMult();
            }

            // Add all the damage intake amplification(Additive)
            if (b instanceof BuffDmgTakenAmp) {
                this.dmgAmp = this.dmgAmp + ((BuffDmgTakenAmp) b).getDmgTakenAmp();
            }

            // Remove expired buffs/remove debuffs when invulnerable/special state
            if (b.isExpired() || (!canDebuffAffect() && b.isDebuff())) {
                remove.add(bEntry.getKey());
            }
        }
        for (final Integer bKey : remove) {
            this.buffs.remove(bKey);
            returnBuffKey(bKey);
        }
    }

    private void die() {
        sendParticle(this.logic.getRoom(), Globals.PARTICLE_BLOOD, this.key);
        setInvulnerable(false);
        setRemovingDebuff(false);
        setDead(true);
        setPlayerState(PLAYER_STATE_DEAD);
        this.damageQueue.clear();
        this.healQueue.clear();
        this.skillUseQueue.clear();
        this.buffQueue.clear();
        this.barrierDmgTaken = 0;
        this.deathTime = this.logic.getTime();
    }

    private void respawn() {
        this.buffs.clear();
        this.stats[Globals.STAT_MINHP] = this.stats[Globals.STAT_MAXHP];
        setXSpeed(0);
        Point2D.Double spawn = map.getRandomSpawnPoint();
        setPos(spawn.x, spawn.y);
        setInvulnerable(false);
        setRemovingDebuff(false);
        setDead(false);
        queuePlayerState(PLAYER_STATE_STAND);
    }

    /**
     * Roll a damage number between the max and min damage of player.
     *
     * @return Randomly rolled damage.
     */
    public double rollDamage() {
        double dmg = Globals.rng((int) (this.stats[Globals.STAT_MAXDMG] - this.stats[Globals.STAT_MINDMG]))
                + this.stats[Globals.STAT_MINDMG];
        double mult = 1;
        for (final Map.Entry<Integer, Buff> bEntry : this.buffs.entrySet()) {
            final Buff b = bEntry.getValue();
            if (b instanceof BuffDmgIncrease) {
                mult += ((BuffDmgIncrease) b).getDmgIncrease();
            }
        }
        // Defender Mastery Passive
        if (hasSkill(Skill.PASSIVE_SHIELDMASTERY)
                && getItemType(this.equips[Globals.ITEM_WEAPON]) == Globals.ITEM_SWORD
                && getItemType(this.equips[Globals.ITEM_OFFHAND]) == Globals.ITEM_SHIELD) {
            mult += 0.09 + 0.002 * getSkillLevel(Skill.PASSIVE_SHIELDMASTERY);
        }
        // Power of Will Passive
        if (hasSkill(Skill.PASSIVE_WILLPOWER)) {
            // (5% + 0.5% Per Level) * %HP Left
            mult += (0.05 + 0.005 * getSkillLevel(Skill.PASSIVE_WILLPOWER))
                    * (this.stats[Globals.STAT_MINHP] / this.stats[Globals.STAT_MAXHP]);
        }
        // Tactical Execution Passive
        mult += this.tacticalDmgMult;
        dmg *= mult;
        return dmg;
    }

    /**
     * Roll a chance to do critical hit.
     *
     * @return True if rolls a critical chance.
     */
    public boolean rollCrit() {
        return rollCrit(0);
    }

    /**
     * Roll a chance to do critical hit with addition critical chance(from skills)
     *
     * @param bonusCritChance Bonus chance % in decimal(40% = 0.4).
     * @return True if rolls a critical chance.
     */
    public boolean rollCrit(final double bonusCritChance) {
        double totalCritChance = this.stats[Globals.STAT_CRITCHANCE] + bonusCritChance;
        // Dual Sword Passive
        if (hasSkill(Skill.PASSIVE_DUALSWORD)
                && getItemType(this.equips[Globals.ITEM_WEAPON]) == Globals.ITEM_SWORD
                && getItemType(this.equips[Globals.ITEM_OFFHAND]) == Globals.ITEM_SWORD) {
            // Check if has Dual Sword passive AND Mainhand/Offhand are both Swords.
            totalCritChance += 0.04 + 0.002 * getSkillLevel(Skill.PASSIVE_DUALSWORD);
        }
        // Keen Eye Passive
        if (hasSkill(Skill.PASSIVE_KEENEYE)) {
            totalCritChance += 0.01 + 0.003 * getSkillLevel(Skill.PASSIVE_KEENEYE);
        }
        return Globals.rng(10000) + 1 < (int) (totalCritChance * 10000);
    }

    public double criticalDamage(final double dmg) {
        return criticalDamage(dmg, 0);
    }

    public double criticalDamage(final double dmg, final double bonusCritDmg) {
        double totalCritDmg = 1 + this.stats[Globals.STAT_CRITDMG] + bonusCritDmg;
        // Bow Mastery Passive
        if (hasSkill(Skill.PASSIVE_BOWMASTERY)
                && getItemType(this.equips[Globals.ITEM_WEAPON]) == Globals.ITEM_BOW
                && getItemType(this.equips[Globals.ITEM_OFFHAND]) == Globals.ITEM_ARROW) {
            totalCritDmg += 0.3 + 0.04 * getSkillLevel(Skill.PASSIVE_BOWMASTERY);
        }
        // Keen Eye Passive
        if (hasSkill(Skill.PASSIVE_VITALHIT)) {
            totalCritDmg += 0.1 + 0.02 * getSkillLevel(Skill.PASSIVE_VITALHIT);
        }
        return dmg * (totalCritDmg);
    }

    private void updateStats() {
        this.stats[Globals.STAT_ARMOR] = Globals
                .calcArmor((int) (this.stats[Globals.STAT_DEFENSE] + this.bonusStats[Globals.STAT_DEFENSE]));
        this.stats[Globals.STAT_REGEN] = Globals.calcRegen((int) (this.stats[Globals.STAT_SPIRIT] + this.bonusStats[Globals.STAT_SPIRIT]));
        double hpPercent = 1;
        if (this.stats[Globals.STAT_MAXHP] > 0) {
            hpPercent = this.stats[Globals.STAT_MINHP] / this.stats[Globals.STAT_MAXHP];
        }
        this.stats[Globals.STAT_MAXHP] = Globals
                .calcMaxHP((int) (this.stats[Globals.STAT_DEFENSE] + this.bonusStats[Globals.STAT_DEFENSE]));
        this.stats[Globals.STAT_MINHP] = hpPercent * this.stats[Globals.STAT_MAXHP];
        this.stats[Globals.STAT_MINDMG] = Globals.calcMinDmg((int) (this.stats[Globals.STAT_POWER] + this.bonusStats[Globals.STAT_POWER]));
        this.stats[Globals.STAT_MAXDMG] = Globals.calcMaxDmg((int) (this.stats[Globals.STAT_POWER] + this.bonusStats[Globals.STAT_POWER]));
        this.stats[Globals.STAT_CRITCHANCE] = Globals
                .calcCritChance((int) (this.stats[Globals.STAT_SPIRIT] + this.bonusStats[Globals.STAT_SPIRIT]));
        this.stats[Globals.STAT_CRITDMG] = Globals
                .calcCritDmg((int) (this.stats[Globals.STAT_SPIRIT] + this.bonusStats[Globals.STAT_SPIRIT]));

        this.stats[Globals.STAT_CRITCHANCE] = this.stats[Globals.STAT_CRITCHANCE] + this.bonusStats[Globals.STAT_CRITCHANCE];
        this.stats[Globals.STAT_CRITDMG] = this.stats[Globals.STAT_CRITDMG] + this.bonusStats[Globals.STAT_CRITDMG];
        this.stats[Globals.STAT_REGEN] = this.stats[Globals.STAT_REGEN] + this.bonusStats[Globals.STAT_REGEN];
        this.stats[Globals.STAT_ARMOR] = this.stats[Globals.STAT_ARMOR] + this.bonusStats[Globals.STAT_ARMOR];
        this.stats[Globals.STAT_DAMAGEREDUCT] = Globals.calcReduction(this.stats[Globals.STAT_ARMOR]);
    }

    public void giveDrop(final double lvl) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_PLAYER_GIVEDROP;
        final byte[] lev = Globals.intToBytes((int) lvl);
        bytes[1] = lev[0];
        bytes[2] = lev[1];
        bytes[3] = lev[2];
        bytes[4] = lev[3];
        sender.sendPlayer(bytes, this);
    }

    public void giveEXP(final double amount) {
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_PLAYER_GIVEEXP;
        final byte[] exp = Globals.intToBytes((int) amount);
        bytes[1] = exp[0];
        bytes[2] = exp[1];
        bytes[3] = exp[2];
        bytes[4] = exp[3];
        sender.sendPlayer(bytes, this);

        bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 3];
        bytes[0] = Globals.DATA_NUMBER;
        bytes[1] = Globals.NUMBER_TYPE_EXP;
        final byte[] posXInt = Globals.intToBytes((int) this.x - 20);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        final byte[] posYInt = Globals.intToBytes((int) this.y);
        bytes[6] = posYInt[0];
        bytes[7] = posYInt[1];
        bytes[8] = posYInt[2];
        bytes[9] = posYInt[3];
        final byte[] d = Globals.intToBytes((int) amount);
        bytes[10] = d[0];
        bytes[11] = d[1];
        bytes[12] = d[2];
        bytes[13] = d[3];
        sender.sendAll(bytes, this.logic.getRoom());
    }

    /**
     * Check if a rectangle intersects with this player's hitbox
     *
     * @param box Box to be checked
     * @return True if the boxes intersect
     */
    public boolean intersectHitbox(final Rectangle2D.Double box) {
        return this.hitbox.intersects(box);
    }

    public Rectangle2D.Double getHitbox() {
        return this.hitbox;
    }

    /**
     * Return if player is stunned
     *
     * @return isStun
     */
    public boolean isStunned() {
        return this.stunDebuff != null;
    }

    /**
     * Return if player is being knocked back.
     *
     * @return isKnockback
     */
    public boolean isKnockback() {
        return this.knockbackDebuff != null;
    }

    /**
     * Check if player is in a skill use state
     *
     * @return True if player is in a skill use state.
     */
    public boolean isUsingSkill() {
        return VALID_PLAYER_SKILL_STATES.containsKey(this.playerState);
    }

    /**
     * Queue a buff/debuff to this player
     *
     * @param b New Buff
     */
    public void queueBuff(final Buff b) {
        if (!isDead()) {
            this.buffQueue.add(b);
        }
    }

    private void updateJump() {
        if (this.dirKeydown[Globals.UP]) {
            setYSpeed(-14);
        }
    }

    private void updateFall() {
        if (this.ySpeed != 0) {
            updateY(this.ySpeed);
            queuePlayerState(PLAYER_STATE_JUMP);
        }

        setYSpeed(this.ySpeed + Globals.GRAVITY);
        if (this.ySpeed >= Globals.MAX_FALLSPEED) {
            setYSpeed(Globals.MAX_FALLSPEED);
        }
        if (this.ySpeed < 0) {
            this.isJumping = true;
        }
        this.isFalling = this.map.isFalling(this.x, this.y, this.ySpeed);
        if (!this.isFalling && this.ySpeed > 0) {
            this.y = this.map.getValidY(this.x, this.y, this.ySpeed);
            setYSpeed(0);
            this.isJumping = false;
        }
    }

    private void updateWalk(final boolean moved) {
        if (this.dirKeydown[Globals.RIGHT] && !this.dirKeydown[Globals.LEFT]) {
            setXSpeed(4.5);
            if (moved) {
                if (this.ySpeed == 0) {
                    queuePlayerState(PLAYER_STATE_WALK);
                }
            }
        } else if (this.dirKeydown[Globals.LEFT] && !this.dirKeydown[Globals.RIGHT]) {
            setXSpeed(-4.5);
            if (moved) {
                if (this.ySpeed == 0) {
                    queuePlayerState(PLAYER_STATE_WALK);
                }
            }
        } else {
            setXSpeed(0);
        }
    }

    private void updateFacing() {
        if (this.dirKeydown[Globals.RIGHT] && !this.dirKeydown[Globals.LEFT]) {
            if (this.facing != Globals.RIGHT) {
                setFacing(Globals.RIGHT);
            }
        } else if (this.dirKeydown[Globals.LEFT] && !this.dirKeydown[Globals.RIGHT]) {
            if (this.facing != Globals.LEFT) {
                setFacing(Globals.LEFT);
            }
        }
    }

    /**
     * Queue skill to be use. Processed in the next tick.
     *
     * @param data
     */
    public void queueSkillUse(final byte[] data) {
        this.lastActionTime = this.logic.getTime();
        if (!isDead()) {
            this.skillUseQueue.add(data);
        }
    }

    /**
     * Queue damage to be dealt. Processed in HP updated in the next tick.
     *
     * @param damage
     */
    public void queueDamage(final Damage damage) {
        if (!isDead()) {
            this.damageQueue.add(damage);
        }
    }

    /**
     * Queue heal to be applied. Processed in HP updated in the next tick.
     *
     * @param heal
     */
    public void queueHeal(final int heal) {
        if (!isDead()) {
            this.healQueue.add(heal);
        }
    }

    /**
     * Set player facing direction.
     * <p>
     * Direction constants in Globals
     * </p>
     *
     * @param f Direction in byte
     */
    public void setFacing(final byte f) {
        this.facing = f;
        this.updateFacing = true;
    }

    public void setFrame(final byte f) {
        this.frame = f;
    }

    private boolean updateX(final double change) {
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

    private boolean updateY(final double change) {
        if (change == 0) {
            return false;
        }
        this.y = this.y + change;
        this.updatePos = true;
        return true;
    }

    public void damageProc(final Damage dmg) {
        if (hasSkill(Skill.PASSIVE_SHADOWATTACK) && this.skills.get(Skill.PASSIVE_SHADOWATTACK).canCast()) {
            if (Globals.rng(100) + 1 <= 20 + getSkillLevel(Skill.PASSIVE_SHADOWATTACK)) {
                this.skills.get(Skill.PASSIVE_SHADOWATTACK).setCooldown();
                sendCooldown(Skill.PASSIVE_SHADOWATTACK);
                sendParticle(this.logic.getRoom(), Globals.PARTICLE_PASSIVE_SHADOWATTACK, dmg.getDmgPoint().x, dmg.getDmgPoint().y);
                if (dmg.getTarget() != null) {
                    final Damage shadow = new Damage((int) (dmg.getDamage() * 0.5D), false, dmg.getOwner(), dmg.getTarget(), false,
                            dmg.getDmgPoint());
                    shadow.setHidden(true);
                    dmg.getTarget().queueDamage(shadow);
                } else if (dmg.getMobTarget() != null) {
                    final Damage shadow = new Damage((int) (dmg.getDamage() * 0.5D), false, dmg.getOwner(), dmg.getMobTarget(), false,
                            dmg.getDmgPoint());
                    shadow.setHidden(true);
                    dmg.getMobTarget().queueDamage(shadow);
                }
            }
        }
    }

    /**
     * Queue player state to be set.
     * <p>
     * States constants in Globals
     * </p>
     *
     * @param newState New state the player is in
     */
    public void queuePlayerState(final byte newState) {
        this.nextState = newState;
    }

    /**
     * Force set a player state.
     *
     * @param newState State to be set
     */
    public void setPlayerState(final byte newState) {
        this.playerState = newState;
        this.frame = -1;
        this.lastFrameTime = 0;
        this.updateAnimState = true;
    }

    private void updateAnimState() {
        final byte prevAnimState = this.animState, prevFrame = this.frame;
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        final int frameDuration = Globals.nsToMs(this.logic.getTime() - this.lastFrameTime);
        switch (this.playerState) {
            case PLAYER_STATE_STAND:
                this.animState = Globals.PLAYER_ANIM_STATE_STAND;
                if (frameDuration >= 150) {
                    if (this.frame == 5) {
                        this.frame = 0;
                    } else {
                        this.frame++;
                    }
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_DEAD:
                this.animState = Globals.PLAYER_ANIM_STATE_DEAD;
                if (frameDuration >= 50) {
                    if (this.frame < 10) {
                        this.frame++;
                    }
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_WALK:
                this.animState = Globals.PLAYER_ANIM_STATE_WALK;
                if (frameDuration >= 40) {
                    if (this.frame == 15) {
                        this.frame = 0;
                    } else {
                        this.frame++;
                    }
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_JUMP:
                this.animState = Globals.PLAYER_ANIM_STATE_JUMP;
                if (this.frame != 0) {
                    this.frame = 0;
                }
                break;
            case PLAYER_STATE_SWORD_SLASH:
                if (frameDuration >= 10) {
                    if (duration < 200 && duration > 100) {
                        this.animState = Globals.PLAYER_ANIM_STATE_ATTACK;
                        if (this.frame > 0) {
                            this.frame--;
                        }
                    } else {
                        this.animState = Globals.PLAYER_ANIM_STATE_ATTACK;
                        if (this.frame < 10) {
                            this.frame++;
                        }
                    }
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_SWORD_GASH:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACK;
                if (frameDuration >= ((this.frame == 1) ? 150 : 40) && this.frame < 10) {
                    this.frame++;

                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_SWORD_PHANTOM:
                this.animState = Globals.PLAYER_ANIM_STATE_INVIS;
                break;
            case PLAYER_STATE_SWORD_VORPAL:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACK;
                if (frameDuration >= 40 && this.frame < 10) {
                    this.frame++;
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_SWORD_CINDER:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACK;
                if (frameDuration >= ((this.frame == 1) ? 40 : 30) && this.frame < 10) {
                    this.frame++;
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_SWORD_TAUNT:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACK;
                if (frameDuration >= ((this.frame == 1) ? 150 : 30) && this.frame < 10) {
                    this.frame++;
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_BOW_ARC:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACKBOW;
                if (this.frame < 5 && frameDuration >= ((this.frame < 5) ? 20 : 70)) {
                    this.frame++;
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_BOW_RAPID:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACKBOW;
                if (this.frame < 5 && frameDuration >= 20) {
                    this.frame++;
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_BOW_POWER:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACKBOW;
                if (frameDuration >= ((this.frame < 5) ? 20 : 70)) {
                    if (duration < 800) {
                        if (this.frame != 5) {
                            this.frame++;
                        }
                    }
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_BOW_VOLLEY:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACKBOW;
                if (this.frame != 5) {
                    this.frame = 5;
                }
                break;
            case PLAYER_STATE_BOW_STORM:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACKBOW;
                if (this.frame < 5 && frameDuration >= ((this.frame < 5) ? 20 : 70)) {
                    this.frame++;
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_BOW_FROST:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACKBOW;
                if (this.frame < 5 && frameDuration >= ((this.frame < 5) ? 20 : 70)) {
                    this.frame++;
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_SHIELD_DASH:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACK;
                if (frameDuration >= 20 && this.frame < 2) {
                    this.frame++;
                }
                break;
            case PLAYER_STATE_SHIELD_CHARGE:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACK;
                if (frameDuration >= ((this.frame == 1) ? 600 : 20) && this.frame < 10) {
                    this.frame++;
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_SHIELD_FORTIFY:
                this.animState = Globals.PLAYER_ANIM_STATE_BUFF;
                if (frameDuration >= 30 && this.frame < 6) {
                    this.frame++;
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_SHIELD_REFLECT:
                this.animState = Globals.PLAYER_ANIM_STATE_BUFF;
                if (frameDuration >= 20 && this.frame < 6) {
                    this.frame++;
                }
                break;
            case PLAYER_STATE_SHIELD_IRON:
                this.animState = Globals.PLAYER_ANIM_STATE_BUFF;
                if (frameDuration >= 30 && this.frame < 6) {
                    this.frame++;
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_SHIELD_TOSS:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACK;
                if (frameDuration >= 40 && this.frame < 10) {
                    this.frame++;
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
        }
        if (this.animState != prevAnimState || this.frame != prevFrame) {
            this.updateAnimState = true;
        }
    }

    public static void sendSFX(final byte room, final byte sfxID, final double soundX, final double soundY) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_SOUND_EFFECT;
        bytes[1] = sfxID;
        final byte[] posXInt = Globals.intToBytes((int) soundX);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        final byte[] posYInt = Globals.intToBytes((int) soundY);
        bytes[6] = posYInt[0];
        bytes[7] = posYInt[1];
        bytes[8] = posYInt[2];
        bytes[9] = posYInt[3];
        sender.sendAll(bytes, room);
    }

    public void sendData() {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 5 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_PLAYER_GET_ALL;
        bytes[1] = this.key;
        final byte[] posXInt = Globals.intToBytes((int) this.x);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        final byte[] posYInt = Globals.intToBytes((int) this.y);
        bytes[6] = posYInt[0];
        bytes[7] = posYInt[1];
        bytes[8] = posYInt[2];
        bytes[9] = posYInt[3];
        bytes[10] = this.facing;
        bytes[11] = this.animState;
        if (this.frame < 0) {
            bytes[12] = 0;
        } else {
            bytes[12] = this.frame;
        }

        sender.sendAll(bytes, this.logic.getRoom());
        this.updatePos = false;
        this.updateFacing = false;
        this.updateAnimState = false;
    }

    /**
     * Send the player's current position to every connected player
     * <p>
     * X and y are casted and sent as integer. Uses Server PacketSender to send to all Byte sent: 0 - Data type 1 - Key 2,3,4,5 - x 6,7,8,9 - y
     * </p>
     */
    public void sendPos() {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_PLAYER_SET_POS;
        bytes[1] = this.key;
        final byte[] posXInt = Globals.intToBytes((int) this.x);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        final byte[] posYInt = Globals.intToBytes((int) this.y);
        bytes[6] = posYInt[0];
        bytes[7] = posYInt[1];
        bytes[8] = posYInt[2];
        bytes[9] = posYInt[3];
        bytes[10] = this.facing;
        sender.sendAll(bytes, this.logic.getRoom());
        this.updatePos = false;
    }

    /**
     * Send the player's current facing direction to every connected player
     * <p>
     * Facing uses direction constants in Globals. Uses Server PacketSender to send to all Byte sent: 0 - Data type 1 - Key 2 - Facing direction
     * </p>
     */
    public void sendFacing() {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_SET_FACING;
        bytes[1] = this.key;
        bytes[2] = this.facing;
        sender.sendAll(bytes, this.logic.getRoom());
        this.updateFacing = false;
    }

    /**
     * Send the player's current state(for animation) and current frame of animation to every connected player
     * <p>
     * State constants are in Globals. Uses Server PacketSender to send to all Byte sent: 0 - Data type 1 - Key 2 - Player state 3 - Current frame
     * </p>
     */
    public void sendState() {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PLAYER_SET_STATE;
        bytes[1] = this.key;
        bytes[2] = this.animState;
        if (this.frame < 0) {
            bytes[3] = 0;
        } else {
            bytes[3] = this.frame;
        }
        sender.sendAll(bytes, this.logic.getRoom());
        this.updateAnimState = false;
    }

    /**
     * Send set cooldown to player client.
     *
     * @param data
     */
    public void sendCooldown(final byte[] data) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
        bytes[0] = Globals.DATA_PLAYER_SET_COOLDOWN;
        bytes[1] = data[3];
        sender.sendPlayer(bytes, this);
    }

    public void sendCooldown(final byte skillCode) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
        bytes[0] = Globals.DATA_PLAYER_SET_COOLDOWN;
        bytes[1] = skillCode;
        sender.sendPlayer(bytes, this);
    }

    public void sendDamage(final Damage dmg, final int dmgDealt) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 3];
        bytes[0] = Globals.DATA_NUMBER;
        bytes[1] = dmg.getDamageType();
        final byte[] posXInt = Globals.intToBytes(dmg.getDmgPoint().x);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        final byte[] posYInt = Globals.intToBytes(dmg.getDmgPoint().y);
        bytes[6] = posYInt[0];
        bytes[7] = posYInt[1];
        bytes[8] = posYInt[2];
        bytes[9] = posYInt[3];
        final byte[] d = Globals.intToBytes(dmgDealt);
        bytes[10] = d[0];
        bytes[11] = d[1];
        bytes[12] = d[2];
        bytes[13] = d[3];
        if (map.isPvP()) {
            sender.sendPlayer(bytes, dmg.getOwner());

            final byte[] pvpBytes = Arrays.copyOf(bytes, bytes.length);
            pvpBytes[1] = Globals.NUMBER_TYPE_MOB;
            sender.sendPlayer(pvpBytes, this);
        } else {
            sender.sendAll(bytes, this.logic.getRoom());
        }
    }

    /**
     * Send name to all clients.
     */
    public void sendName() {
        final byte[] data = this.name.getBytes(StandardCharsets.UTF_8);
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + data.length];
        bytes[0] = Globals.DATA_PLAYER_GET_NAME;
        bytes[1] = this.key;
        System.arraycopy(data, 0, bytes, 2, data.length);
        sender.sendAll(bytes, this.logic.getRoom());
    }

    public void sendStat(final byte statID) {
        final byte[] stat = Globals.intToBytes((int) getStats()[statID]);
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_PLAYER_GET_STAT;
        bytes[1] = this.key;
        bytes[2] = statID;
        System.arraycopy(stat, 0, bytes, 3, stat.length);
        sender.sendAll(bytes, this.logic.getRoom());
    }

    public void setInvulnerable(final boolean set) {
        this.isInvulnerable = set;
    }

    public boolean isInvulnerable() {
        return this.isInvulnerable;
    }

    public void setRemovingDebuff(final boolean set) {
        this.isRemoveDebuff = set;
    }

    public boolean isRemovingDebuff() {
        return this.isRemoveDebuff;
    }

    public boolean canDebuffAffect() {
        return !isInvulnerable() && !isRemovingDebuff();
    }

    /**
     * Set the uID of this player.
     *
     * @param id uID
     */
    public void setUniqueID(final UUID id) {
        this.uniqueID = id;
    }

    /**
     * Get the uID of this player.
     *
     * @return
     */
    public UUID getUniqueID() {
        return this.uniqueID;
    }

    /**
     * Set the name of this player.
     *
     * @param s
     */
    public void setPlayerName(final String s) {
        this.name = s;
    }

    /**
     * Get the name of this player.
     *
     * @return
     */
    public String getPlayerName() {
        return this.name;
    }

    /**
     * Set an amount of a specific stat.
     *
     * @param stat Stat Type
     * @param amount Amount of stats
     */
    public void setStat(final byte stat, final double amount) {
        this.stats[stat] = amount;
        updateStats();
    }

    /**
     * Get the stats of this player.
     *
     * @return double[] - Player Stats
     */
    public double[] getStats() {
        return this.stats;
    }

    /**
     * Set bonus stats of this player(CRITCHANCE, CRITDMG, REGEN, ARMOR).
     *
     * @param stat Stat Type
     * @param amount Amount of stats.
     */
    public void setBonusStat(final byte stat, final double amount) {
        this.bonusStats[stat] = amount;
        updateStats();
    }

    /**
     * Get the bonus stats of this player(CRITCHANCE, CRITDMG, REGEN, ARMOR).
     *
     * @return double[] - Player Bonus Stats
     */
    public double[] getBonusStats() {
        return this.bonusStats;
    }

    /**
     * Set the item code in a specific equipment slot
     *
     * @param slot Equipment Slot
     * @param itemCode Item Code
     */
    public void setEquip(final int slot, final int itemCode) {
        this.equips[slot] = itemCode;
    }

    /**
     * Disconnect a player in the next tick.
     */
    public void disconnect() {
        this.connected = false;
    }

    /**
     * Check if player is still connected
     *
     * @return True if connected
     */
    public boolean isConnected() {
        return this.connected;
    }

    /**
     * Get the item type of an item code.
     *
     * @param i Item Code
     * @return Byte - Item Type
     */
    public static byte getItemType(final int i) {
        if (i >= 100000 && i <= 109999) { // Swords
            return Globals.ITEM_WEAPON;
        } else if (i >= 110000 && i <= 119999) { // Shields
            return Globals.ITEM_SHIELD;
        } else if (i >= 120000 && i <= 129999) { // Bows
            return Globals.ITEM_BOW;
        } else if (i >= 130000 && i <= 199999) {
            return Globals.ITEM_ARROW;
        } else if (i >= 200000 && i <= 209999) {
            return Globals.ITEM_HEAD;
        } else if (i >= 300000 && i <= 309999) {
            return Globals.ITEM_CHEST;
        } else if (i >= 400000 && i <= 409999) {
            return Globals.ITEM_PANTS;
        } else if (i >= 500000 && i <= 509999) {
            return Globals.ITEM_SHOULDER;
        } else if (i >= 600000 && i <= 609999) {
            return Globals.ITEM_GLOVE;
        } else if (i >= 700000 && i <= 709999) {
            return Globals.ITEM_SHOE;
        } else if (i >= 800000 && i <= 809999) {
            return Globals.ITEM_BELT;
        } else if (i >= 900000 && i <= 909999) {
            return Globals.ITEM_RING;
        } else if (i >= 1000000 && i <= 1009999) {
            return Globals.ITEM_AMULET;
        }
        return -1;

    }

    public static void sendParticle(final byte room, final byte particleID, final double x, final double y, final byte facing) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_PARTICLE_EFFECT;
        bytes[1] = particleID;
        final byte[] posXInt = Globals.intToBytes((int) x);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        final byte[] posYInt = Globals.intToBytes((int) y);
        bytes[6] = posYInt[0];
        bytes[7] = posYInt[1];
        bytes[8] = posYInt[2];
        bytes[9] = posYInt[3];
        bytes[10] = facing;
        sender.sendAll(bytes, room);
    }

    public static void sendParticle(final byte room, final byte particleID, final double x, final double y) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_PARTICLE_EFFECT;
        bytes[1] = particleID;
        final byte[] posXInt = Globals.intToBytes((int) x);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        final byte[] posYInt = Globals.intToBytes((int) y);
        bytes[6] = posYInt[0];
        bytes[7] = posYInt[1];
        bytes[8] = posYInt[2];
        bytes[9] = posYInt[3];
        sender.sendAll(bytes, room);
    }

    public static void sendParticle(final byte room, final byte particleID, final byte key) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PARTICLE_EFFECT;
        bytes[1] = particleID;
        bytes[2] = key;
        sender.sendAll(bytes, room);
    }

    public static void sendParticle(final byte room, final byte particleID, final byte key, final byte facing) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PARTICLE_EFFECT;
        bytes[1] = particleID;
        bytes[2] = facing;
        bytes[3] = key;
        sender.sendAll(bytes, room);
    }

}
