package blockfighter.server.entities.player;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.GameEntity;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.buff.Buff;
import blockfighter.server.entities.buff.BuffDmgIncrease;
import blockfighter.server.entities.buff.BuffDmgReduct;
import blockfighter.server.entities.buff.BuffDmgTakenAmp;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.buff.BuffPassiveBarrier;
import blockfighter.server.entities.buff.BuffShieldDash;
import blockfighter.server.entities.buff.BuffShieldFortify;
import blockfighter.server.entities.buff.BuffShieldIron;
import blockfighter.server.entities.buff.BuffShieldReflect;
import blockfighter.server.entities.buff.BuffStun;
import blockfighter.server.entities.buff.BuffSwordSlash;
import blockfighter.server.entities.buff.BuffSwordTaunt;
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
import blockfighter.server.entities.proj.ProjBowArc;
import blockfighter.server.entities.proj.ProjBowFrost;
import blockfighter.server.entities.proj.ProjBowPower;
import blockfighter.server.entities.proj.ProjBowRapid;
import blockfighter.server.entities.proj.ProjBowStorm;
import blockfighter.server.entities.proj.ProjBowVolley;
import blockfighter.server.entities.proj.ProjShieldCharge;
import blockfighter.server.entities.proj.ProjShieldReflect;
import blockfighter.server.entities.proj.ProjShieldToss;
import blockfighter.server.entities.proj.ProjSwordCinder;
import blockfighter.server.entities.proj.ProjSwordGash;
import blockfighter.server.entities.proj.ProjSwordPhantom;
import blockfighter.server.entities.proj.ProjSwordSlash;
import blockfighter.server.entities.proj.ProjSwordTaunt;
import blockfighter.server.entities.proj.ProjSwordVorpal;
import blockfighter.server.maps.GameMap;
import blockfighter.server.net.PacketSender;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Player entities on the server.
 *
 * @author Ken Kwan
 */
public class Player extends Thread implements GameEntity {

    public final static byte PLAYER_STATE_STAND = 0x00,
            PLAYER_STATE_WALK = 0x01,
            PLAYER_STATE_JUMP = 0x02,
            PLAYER_STATE_SWORD_VORPAL = 0x03,
            PLAYER_STATE_SWORD_MULTI = 0x04,
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
            PLAYER_STATE_DEAD = 0x15,
            PLAYER_STATE_SWORD_PHANTOM = 0x16;

    private final byte key;
    private final LogicModule logic;
    private int uniqueID = -1;
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

    private final int[] equip = new int[Globals.NUM_EQUIP_SLOTS];
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
        extendBuffKeys();
    }

    private static boolean hasPastDuration(int currentDuration, int durationToPast) {
        if (durationToPast <= 0) {
            return true;
        }
        return currentDuration / durationToPast >= 1;
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
        if (this.buffKeys.isEmpty()) {
            extendBuffKeys();
        }
        if (!this.buffKeys.isEmpty()) {
            return this.buffKeys.poll();
        }
        return null;
    }

    private void extendBuffKeys() {
        for (int i = this.maxBuffKeys; i < this.maxBuffKeys + 150; i++) {
            this.buffKeys.add(i);
        }
        this.maxBuffKeys += 150;
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

    /**
     * Get the item codes of the equipment on this Player
     *
     * @return int[] - Equipment Item Codes
     */
    public int[] getEquip() {
        return this.equip;
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
            //case Skill.SWORD_MULTI:
            //    newSkill = new SkillSwordMulti();
            //    break;
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

    private void castSkill(final byte[] data, final byte newState, final byte weaponSlot) {
        if (!this.skills.get(data[3]).canCast(getItemType(this.equip[weaponSlot]))) {
            return;
        }
        this.skillCounter = 0;
        this.skillCastTime = this.logic.getTime();
        // Globals.log("DATA_PLAYER_CASTSKILL", "Key: " + key + " Room: " + logic.getRoom() + " Player: " + getPlayerName() + " Skill: " +
        // data[3], Globals.LOG_TYPE_DATA, true);

        queuePlayerState(newState);
        this.skills.get(data[3]).setCooldown();
        sendCooldown(data);

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
            if (data[3] == Skill.SHIELD_IRON || (!isStunned() && !isKnockback())) {
                if (hasSkill(data[3])) {
                    switch (data[3]) {
                        case Skill.SWORD_SLASH:
                            castSkill(data, PLAYER_STATE_SWORD_SLASH, Globals.ITEM_WEAPON);
                            break;
                        case Skill.SWORD_VORPAL:
                            castSkill(data, PLAYER_STATE_SWORD_VORPAL, Globals.ITEM_WEAPON);
                            break;
                        case Skill.SWORD_GASH:
                            castSkill(data, PLAYER_STATE_SWORD_GASH, Globals.ITEM_WEAPON);
                            break;
                        //case Skill.SWORD_MULTI:
                        //    castSkill(data, PLAYER_STATE_SWORD_MULTI, Globals.ITEM_WEAPON);
                        //    break;
                        case Skill.SWORD_PHANTOM:
                            castSkill(data, PLAYER_STATE_SWORD_PHANTOM, Globals.ITEM_WEAPON);
                            break;
                        case Skill.SWORD_CINDER:
                            castSkill(data, PLAYER_STATE_SWORD_CINDER, Globals.ITEM_WEAPON);
                            break;
                        case Skill.SWORD_TAUNT:
                            castSkill(data, PLAYER_STATE_SWORD_TAUNT, Globals.ITEM_WEAPON);
                            break;
                        case Skill.BOW_ARC:
                            castSkill(data, PLAYER_STATE_BOW_ARC, Globals.ITEM_WEAPON);
                            break;
                        case Skill.BOW_POWER:
                            castSkill(data, PLAYER_STATE_BOW_POWER, Globals.ITEM_WEAPON);
                            break;
                        case Skill.BOW_RAPID:
                            castSkill(data, PLAYER_STATE_BOW_RAPID, Globals.ITEM_WEAPON);
                            break;
                        case Skill.BOW_VOLLEY:
                            castSkill(data, PLAYER_STATE_BOW_VOLLEY, Globals.ITEM_WEAPON);
                            break;
                        case Skill.BOW_STORM:
                            castSkill(data, PLAYER_STATE_BOW_STORM, Globals.ITEM_WEAPON);
                            break;
                        case Skill.BOW_FROST:
                            castSkill(data, PLAYER_STATE_BOW_FROST, Globals.ITEM_WEAPON);
                            break;
                        case Skill.SHIELD_CHARGE:
                            castSkill(data, PLAYER_STATE_SHIELD_CHARGE, Globals.ITEM_OFFHAND);
                            break;
                        case Skill.SHIELD_DASH:
                            castSkill(data, PLAYER_STATE_SHIELD_DASH, Globals.ITEM_OFFHAND);
                            break;
                        case Skill.SHIELD_FORTIFY:
                            castSkill(data, PLAYER_STATE_SHIELD_FORTIFY, Globals.ITEM_OFFHAND);
                            break;
                        case Skill.SHIELD_IRON:
                            castSkill(data, PLAYER_STATE_SHIELD_IRON, Globals.ITEM_OFFHAND);
                            break;
                        case Skill.SHIELD_REFLECT:
                            castSkill(data, PLAYER_STATE_SHIELD_REFLECT, Globals.ITEM_OFFHAND);
                            break;
                        case Skill.SHIELD_TOSS:
                            castSkill(data, PLAYER_STATE_SHIELD_TOSS, Globals.ITEM_OFFHAND);
                            break;
                    }
                }
            }
        }
    }

    private void updateSkillSwordSlash() {
        final int numHits = 3;
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        if (isSkillMaxed(Skill.SWORD_SLASH) && duration == 0) {
            queueBuff(new BuffSwordSlash(this.logic, 2000, .1, this));
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_SLASHBUFF, this.key);
        }
        if (hasPastDuration(duration, (30 + 110 * this.skillCounter)) && this.skillCounter < numHits) {
            this.skillCounter++;
            final ProjSwordSlash proj = new ProjSwordSlash(this.logic, this.logic.getNextProjKey(), this, this.x, this.y,
                    this.skillCounter);
            this.logic.queueAddProj(proj);
            switch (this.skillCounter) {
                case 1:
                    sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_SLASH1, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                            this.facing);
                    sendSFX(this.logic.getRoom(), Globals.SFX_SLASH, getX(), getY());
                    break;
                case 2:
                    sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_SLASH2, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                            this.facing);
                    sendSFX(this.logic.getRoom(), Globals.SFX_SLASH, getX(), getY());
                    break;
                case 3:
                    sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_SLASH3, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                            this.facing);
                    sendSFX(this.logic.getRoom(), Globals.SFX_SLASH, getX(), getY());
                    break;
                default:
                    break;
            }
        }

        updateSkillEnd(duration, 350, true, false);
    }

    private void updateSkillSwordGash() {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        final byte numHits = 4;
        if (hasPastDuration(duration, (80 * this.skillCounter)) && this.skillCounter < numHits) {
            this.skillCounter++;
            final ProjSwordGash proj = new ProjSwordGash(this.logic, this.logic.getNextProjKey(), this, this.x, this.y, (byte) this.skillCounter);
            this.logic.queueAddProj(proj);
            sendSFX(this.logic.getRoom(), Globals.SFX_GASH, getX(), getY());
            switch (this.skillCounter) {
                case 1:
                    sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_GASH1, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                            this.facing);
                    break;
                case 2:
                    sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_GASH2, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                            this.facing);
                    break;
                case 3:
                    sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_GASH3, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                            this.facing);
                    break;
                case 4:
                    sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_GASH4, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                            this.facing);
                    break;
            }
        }
        updateSkillEnd(duration, 450, true, false);
    }

    private void updateSkillSwordVorpal() {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        int skillTime = isSkillMaxed(Skill.SWORD_VORPAL) ? 150 : 170,
                numHits = isSkillMaxed(Skill.SWORD_VORPAL) ? 5 : 3;
        if (hasPastDuration(duration, skillTime * this.skillCounter) && this.skillCounter < numHits) {
            final ProjSwordVorpal proj = new ProjSwordVorpal(this.logic, this.logic.getNextProjKey(), this, this.x, this.y);
            this.logic.queueAddProj(proj);
            this.frame = 0;
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_VORPAL, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    this.facing);
            this.skillCounter++;
        }

        updateSkillEnd(duration, 800, true, false);
    }

    private void updateSkillSwordTaunt() {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        if (duration == 0) {
            if (isSkillMaxed(Skill.SWORD_TAUNT)) {
                queueBuff(new BuffSwordTaunt(this.logic, 10000, 0.2, 0.2, this));
                sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_TAUNTBUFF, this.key);
            }
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_TAUNTAURA1, this.key);
        }
        if (hasPastDuration(duration, 50) && this.skillCounter < 1) {
            this.skillCounter++;
            final ProjSwordTaunt proj = new ProjSwordTaunt(this.logic, this.logic.getNextProjKey(), this, this.x, this.y);
            this.logic.queueAddProj(proj);
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_TAUNT, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    this.facing);
        }
        updateSkillEnd(duration, 350, false, false);
    }

    private void updateSkillSwordPhantom() {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        final int numHits = this.skills.get(Skill.SWORD_PHANTOM).getLevel() / 2 + 5;
        final int radius = 350;
        boolean endPhantom = false;
        setInvulnerable(true);
        setYSpeed(0);

        //Send initial phase effect
        if (duration == 0) {
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_PHANTOM, getX(), getY(), this.facing);
        }

        if (hasPastDuration(duration, 150 + 150 * this.skillCounter) && this.skillCounter < numHits) {
            if (map.isPvP()) {
                Player target;
                ArrayList<Player> playersInRange = new ArrayList<>(Globals.SERVER_MAX_PLAYERS);
                for (final Map.Entry<Byte, Player> player : this.logic.getPlayers().entrySet()) {
                    final Player p = player.getValue();
                    if (p != this && !p.isDead() && !p.isInvulnerable()) {
                        double distance = Math.sqrt(Math.pow((this.x - p.x), 2) + Math.pow((this.y - p.y), 2));
                        if (distance <= radius) {
                            playersInRange.add(p);
                        }
                    }
                }
                if (!playersInRange.isEmpty()) {
                    target = playersInRange.get(Globals.rng(playersInRange.size()));
                    double teleX = (Globals.rng(2) == 0) ? target.getHitbox().x + target.getHitbox().width + 100 : target.getHitbox().x - 100;
                    this.setPos(teleX, target.getY());
                    if (target.x < this.x) {
                        this.setFacing(Globals.LEFT);
                    } else if (target.x > this.x) {
                        this.setFacing(Globals.RIGHT);
                    }
                } else {
                    endPhantom = true;
                }
            } else {
                Boss target;
                ArrayList<Boss> enemyInRange = new ArrayList<>(Globals.SERVER_MAX_PLAYERS);
                for (final Map.Entry<Byte, Boss> bEntry : this.logic.getBosses().entrySet()) {
                    final Boss b = bEntry.getValue();
                    double distance = Math.sqrt(Math.pow((this.x - b.getX()), 2) + Math.pow((this.y - b.getY()), 2));
                    if (distance <= radius) {
                        enemyInRange.add(b);
                    }
                }
                if (!enemyInRange.isEmpty()) {
                    target = enemyInRange.get(Globals.rng(enemyInRange.size()));
                    double teleX = (Globals.rng(2) == 0) ? target.getHitbox().x + target.getHitbox().width + 100 : target.getHitbox().x - 100;
                    this.setPos(teleX, target.getY());
                    if (target.getX() < this.x) {
                        this.setFacing(Globals.LEFT);
                    } else if (target.getX() > this.x) {
                        this.setFacing(Globals.RIGHT);
                    }
                } else {
                    endPhantom = true;
                }
            }
            if (!endPhantom) {
                final ProjSwordPhantom proj = new ProjSwordPhantom(this.logic, this.logic.getNextProjKey(), this, this.x, this.y);
                this.logic.queueAddProj(proj);
                sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_PHANTOM, getX(), getY(), this.facing);
                sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_PHANTOM2, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(), this.facing);
                this.skillCounter++;
            }
        }
        if (endPhantom || this.skillCounter >= numHits) {
            setInvulnerable(false);
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillSwordCinder() {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        if (hasPastDuration(duration, 50) && this.skillCounter < 1) {
            this.skillCounter++;
            final ProjSwordCinder proj = new ProjSwordCinder(this.logic, this.logic.getNextProjKey(), this, this.x, this.y);
            this.logic.queueAddProj(proj);
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_SWORD_CINDER, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    this.facing);
        }
        updateSkillEnd(duration, 250, true, false);
    }

    private void updateSkillBowArc() {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        final int numHits = 3;
        if (this.skillCounter < numHits && hasPastDuration(duration, 100 + this.skillCounter * 50)) {
            this.skillCounter++;
            final ProjBowArc proj = new ProjBowArc(this.logic, this.logic.getNextProjKey(), this, this.x, this.y);
            this.logic.queueAddProj(proj);
            if (this.skillCounter == 1) {
                sendParticle(this.logic.getRoom(), Globals.PARTICLE_BOW_ARC, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                        this.facing);
                sendSFX(this.logic.getRoom(), Globals.SFX_ARC, getX(), getY());
            }
        }
        updateSkillEnd(duration, 300, false, false);
    }

    private void updateSkillBowFrost() {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        final int numHits = isSkillMaxed(Skill.BOW_FROST) ? 3 : 1;
        if (hasPastDuration(duration, 160 + this.skillCounter * 90) && this.skillCounter < numHits) {
            this.skillCounter++;
            final ProjBowFrost proj = new ProjBowFrost(this.logic, this.logic.getNextProjKey(), this, this.x, this.y, false);
            this.logic.queueAddProj(proj);
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_BOW_FROSTARROW, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    this.facing);
        }

        updateSkillEnd(duration, 380, false, false);
    }

    private void updateSkillBowStorm() {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        if (hasPastDuration(duration, 100) && this.skillCounter < 1) {
            this.skillCounter++;
            final ProjBowStorm proj = new ProjBowStorm(this.logic, this.logic.getNextProjKey(), this, this.x, this.y);
            this.logic.queueAddProj(proj);
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_BOW_STORM, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    this.facing);
        }
        updateSkillEnd(duration, 200, false, false);
    }

    private void updateSkillBowRapid() {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        final int numHits = 3;

        if (hasPastDuration(duration, 150 + this.skillCounter * 150) && this.skillCounter < numHits) {
            if (skillCounter != 0) {
                this.frame = 2;
            }
            this.skillCounter++;
            double projY = this.y;
            if (skillCounter == 1) {
                projY = this.y - 20;
            } else if (skillCounter == 3) {
                projY = this.y + 20;
            }
            final ProjBowRapid proj = new ProjBowRapid(this.logic, this.logic.getNextProjKey(), this, this.x, projY);
            this.logic.queueAddProj(proj);
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_BOW_RAPID, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    this.facing);
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_BOW_RAPID2, (getFacing() == Globals.LEFT) ? x - 20 : x - 40, proj.getHitbox()[0].getY() - 40,
                    this.facing);
            sendSFX(this.logic.getRoom(), Globals.SFX_RAPID, getX(), getY());
        }
        updateSkillEnd(duration, 550, true, false);
    }

    private void updateSkillBowVolley() {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        final int numHits = 20;
        if (hasPastDuration(duration, this.skillCounter * 100) && this.skillCounter < numHits) {
            final ProjBowVolley proj = new ProjBowVolley(this.logic, this.logic.getNextProjKey(), this, this.x,
                    this.y);
            this.logic.queueAddProj(proj);
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_BOW_VOLLEYARROW, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    this.facing);
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_BOW_VOLLEYBOW, getX(), getY() + 30, this.facing);
            this.skillCounter++;
            sendSFX(this.logic.getRoom(), Globals.SFX_VOLLEY, getX(), getY());
        }
        updateSkillEnd(duration, 1900, true, true);
    }

    private void updateSkillBowPower() {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        if (duration == 0) {
            sendSFX(this.logic.getRoom(), Globals.SFX_POWER2, getX(), getY());
        }
        if (duration <= 400 && hasPastDuration(duration, this.skillCounter * 50) && this.skillCounter < 6) {
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_BOW_POWERCHARGE, this.x + ((this.facing == Globals.RIGHT) ? 75 : -75),
                    this.y - 215, this.facing);
            this.skillCounter++;
        } else if (hasPastDuration(duration, 800) && this.skillCounter < 7) {
            this.skillCounter++;
            final ProjBowPower proj = new ProjBowPower(this.logic, this.logic.getNextProjKey(), this, this.x, this.y);
            this.logic.queueAddProj(proj);
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_BOW_POWER, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    this.facing);
            sendSFX(this.logic.getRoom(), Globals.SFX_POWER, getX(), getY());
        }
        if (duration >= 1400 || (!isSkillMaxed(Skill.BOW_POWER) && duration < 800 && (isStunned() || isKnockback()))) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillShieldFortify() {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        if (duration == 0) {
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_FORTIFY, this.key);
            sendSFX(this.logic.getRoom(), Globals.SFX_FORTIFY, getX(), getY());
        }
        if (hasPastDuration(duration, 350) && this.skillCounter < 1) {
            this.skillCounter++;
            queueBuff(new BuffShieldFortify(this.logic, 5000, 0.01 + 0.005 * getSkillLevel(Skill.SHIELD_FORTIFY), this));
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_FORTIFYBUFF, this.key);
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillShieldIron() {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        if (duration == 0) {
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_IRON, this.key);
            sendSFX(this.logic.getRoom(), Globals.SFX_IRON, getX(), getY());
        }
        if (hasPastDuration(duration, 100) && this.skillCounter < 1) {
            this.skillCounter++;
            setRemovingDebuff(true);
            queueBuff(new BuffShieldIron(this.logic, 2000, 0.55 + 0.01 * getSkillLevel(Skill.SHIELD_IRON)));
            if (isSkillMaxed(Skill.SHIELD_IRON) && !map.isPvP()) {
                for (final Map.Entry<Byte, Player> player : this.logic.getPlayers().entrySet()) {
                    final Player p = player.getValue();
                    if (p != this && !p.isDead()) {
                        p.queueBuff(new BuffShieldIron(this.logic, 2000, 0.4));
                        sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_IRONALLY, p.getKey());
                    }
                }
            }
        }
        if (duration >= 2100) {
            setRemovingDebuff(false);
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillShieldReflectHit(final double dmgTaken, final double mult) {
        final ProjShieldReflect proj = new ProjShieldReflect(this.logic, this.logic.getNextProjKey(), this, this.x, this.y,
                dmgTaken * mult);
        this.logic.queueAddProj(proj);
        sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_REFLECTHIT, this.x, this.y);
    }

    private void updateSkillShieldReflectCast() {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        if (duration == 0) {
            queueBuff(new BuffShieldReflect(this.logic, 3000, .4 + 0.02 * getSkillLevel(Skill.SHIELD_REFLECT), this, this));
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_REFLECTCAST, this.key);
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_REFLECTBUFF, this.key);
            if (isSkillMaxed(Skill.SHIELD_REFLECT)) {
                for (final Map.Entry<Byte, Player> player : this.logic.getPlayers().entrySet()) {
                    final Player p = player.getValue();
                    if (p != this && !p.isDead()) {
                        p.queueBuff(new BuffShieldReflect(this.logic, 3000, 0.4, this, p));
                        if (!map.isPvP()) {
                            sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_REFLECTCAST, p.getKey());
                        }
                    }
                }
            }
        }
        updateSkillEnd(duration, 250, false, false);
    }

    private void updateSkillShieldToss() {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        final int numHits = isSkillMaxed(Skill.SHIELD_TOSS) ? 3 : 1;

        if (hasPastDuration(duration, 100 + this.skillCounter * 200) && this.skillCounter < numHits) {
            this.skillCounter++;
            final ProjShieldToss proj = new ProjShieldToss(this.logic, this.logic.getNextProjKey(), this, this.x, this.y);
            this.logic.queueAddProj(proj);
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_TOSS, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(),
                    this.facing);
        }
        updateSkillEnd(duration, 700, true, false);
    }

    private void updateSkillShieldCharge() {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        setXSpeed((this.facing == Globals.RIGHT) ? 18 : -18);
        if (duration == 0) {
            final ProjShieldCharge proj = new ProjShieldCharge(this.logic, this.logic.getNextProjKey(), this, this.x, this.y);
            this.logic.queueAddProj(proj);
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_CHARGE, this.key, this.facing);
        }
        updateSkillEnd(duration, 750, false, false);
    }

    private void updateSkillShieldDash() {
        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        if (!isStunned() && !isKnockback()) {
            setXSpeed((this.facing == Globals.RIGHT) ? 15 : -15);
        }
        if (isSkillMaxed(Skill.SHIELD_DASH) && !isInvulnerable()) {
            setInvulnerable(true);
        }

        if (duration == 0) {
            queueBuff(new BuffShieldDash(this.logic, 5000, 0.01 + 0.003 * getSkillLevel(Skill.SHIELD_DASH), this));
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_DASHBUFF, this.key);
            sendParticle(this.logic.getRoom(), Globals.PARTICLE_SHIELD_DASH, this.key, this.facing);
            setYSpeed(-4);
        }

        if (duration >= 250 || isStunned() || isKnockback()) {
            setInvulnerable(false);
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillUse() {
        if (!isKnockback()) {
            setXSpeed(0);
        }
        switch (this.playerState) {
            case PLAYER_STATE_SWORD_SLASH:
                updateSkillSwordSlash();
                break;
            case PLAYER_STATE_SWORD_GASH:
                updateSkillSwordGash();
                break;
            case PLAYER_STATE_SWORD_VORPAL:
                updateSkillSwordVorpal();
                break;
            case PLAYER_STATE_SWORD_PHANTOM:
                updateSkillSwordPhantom();
                break;
            case PLAYER_STATE_SWORD_CINDER:
                updateSkillSwordCinder();
                break;
            case PLAYER_STATE_SWORD_TAUNT:
                updateSkillSwordTaunt();
                break;
            case PLAYER_STATE_BOW_ARC:
                updateSkillBowArc();
                break;
            case PLAYER_STATE_BOW_RAPID:
                updateSkillBowRapid();
                break;
            case PLAYER_STATE_BOW_POWER:
                updateSkillBowPower();
                break;
            case PLAYER_STATE_BOW_VOLLEY:
                updateSkillBowVolley();
                break;
            case PLAYER_STATE_BOW_STORM:
                updateSkillBowStorm();
                break;
            case PLAYER_STATE_BOW_FROST:
                updateSkillBowFrost();
                break;
            case PLAYER_STATE_SHIELD_DASH:
                updateSkillShieldDash();
                break;
            case PLAYER_STATE_SHIELD_CHARGE:
                updateSkillShieldCharge();
                break;
            case PLAYER_STATE_SHIELD_FORTIFY:
                updateSkillShieldFortify();
                break;
            case PLAYER_STATE_SHIELD_REFLECT:
                updateSkillShieldReflectCast();
                break;
            case PLAYER_STATE_SHIELD_IRON:
                updateSkillShieldIron();
                break;
            case PLAYER_STATE_SHIELD_TOSS:
                updateSkillShieldToss();
                break;
        }
    }

    private void updateSkillEnd(final int duration, final int endTime, final boolean stunCancel, final boolean kbCancel) {
        if (duration >= endTime || (stunCancel && isStunned() || (kbCancel && isKnockback()))) {
            setPlayerState(PLAYER_STATE_STAND);
        }
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
                            ((BuffShieldReflect) b).getOwner().updateSkillShieldReflectHit(amount, ((BuffShieldReflect) b).getMultiplier());
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
                        && getItemType(this.equip[Globals.ITEM_WEAPON]) == Globals.ITEM_SWORD
                        && getItemType(this.equip[Globals.ITEM_OFFHAND]) == Globals.ITEM_SHIELD) {
                    amount = (int) (amount * (1 - (0.05 + 0.005 * getSkillLevel(Skill.PASSIVE_SHIELDMASTERY))));
                }

                // Dual Wield Passive Reduction
                if (hasSkill(Skill.PASSIVE_DUALSWORD)
                        && getItemType(this.equip[Globals.ITEM_WEAPON]) == Globals.ITEM_SWORD
                        && getItemType(this.equip[Globals.ITEM_OFFHAND]) == Globals.ITEM_SWORD) {
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
                // System.out.println("Player Damage Raw: " + dmg.getDamage() + ", Taken: " + amount);
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
            final byte[] stat = Globals.intToByte((int) this.stats[Globals.STAT_MINHP]);
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
                }
            }
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
                && getItemType(this.equip[Globals.ITEM_WEAPON]) == Globals.ITEM_SWORD
                && getItemType(this.equip[Globals.ITEM_OFFHAND]) == Globals.ITEM_SHIELD) {
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
                && getItemType(this.equip[Globals.ITEM_WEAPON]) == Globals.ITEM_SWORD
                && getItemType(this.equip[Globals.ITEM_OFFHAND]) == Globals.ITEM_SWORD) {
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
                && getItemType(this.equip[Globals.ITEM_WEAPON]) == Globals.ITEM_BOW
                && getItemType(this.equip[Globals.ITEM_OFFHAND]) == Globals.ITEM_ARROW) {
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
        this.stats[Globals.STAT_DAMAGEREDUCT] = 1 - Globals.calcReduction(this.stats[Globals.STAT_ARMOR]);
    }

    public void giveDrop(final double lvl) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_PLAYER_GIVEDROP;
        final byte[] lev = Globals.intToByte((int) lvl);
        bytes[1] = lev[0];
        bytes[2] = lev[1];
        bytes[3] = lev[2];
        bytes[4] = lev[3];
        sender.sendPlayer(bytes, this);
    }

    public void giveEXP(final double amount) {
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_PLAYER_GIVEEXP;
        final byte[] exp = Globals.intToByte((int) amount);
        bytes[1] = exp[0];
        bytes[2] = exp[1];
        bytes[3] = exp[2];
        bytes[4] = exp[3];
        sender.sendPlayer(bytes, this);

        bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 3];
        bytes[0] = Globals.DATA_NUMBER;
        bytes[1] = Globals.NUMBER_TYPE_EXP;
        final byte[] posXInt = Globals.intToByte((int) this.x - 20);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        final byte[] posYInt = Globals.intToByte((int) this.y);
        bytes[6] = posYInt[0];
        bytes[7] = posYInt[1];
        bytes[8] = posYInt[2];
        bytes[9] = posYInt[3];
        final byte[] d = Globals.intToByte((int) amount);
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
        return this.playerState == PLAYER_STATE_SWORD_SLASH
                || this.playerState == PLAYER_STATE_SWORD_VORPAL
                || this.playerState == PLAYER_STATE_SWORD_GASH
                || this.playerState == PLAYER_STATE_SWORD_MULTI
                || this.playerState == PLAYER_STATE_SWORD_TAUNT
                || this.playerState == PLAYER_STATE_SWORD_CINDER
                || this.playerState == PLAYER_STATE_BOW_ARC
                || this.playerState == PLAYER_STATE_BOW_POWER
                || this.playerState == PLAYER_STATE_BOW_RAPID
                || this.playerState == PLAYER_STATE_BOW_FROST
                || this.playerState == PLAYER_STATE_BOW_STORM
                || this.playerState == PLAYER_STATE_BOW_VOLLEY
                || this.playerState == PLAYER_STATE_SHIELD_CHARGE
                || this.playerState == PLAYER_STATE_SHIELD_DASH
                || this.playerState == PLAYER_STATE_SHIELD_FORTIFY
                || this.playerState == PLAYER_STATE_SHIELD_IRON
                || this.playerState == PLAYER_STATE_SHIELD_REFLECT
                || this.playerState == PLAYER_STATE_SHIELD_TOSS
                || this.playerState == PLAYER_STATE_SWORD_PHANTOM;
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
                } else if (dmg.getBossTarget() != null) {
                    final Damage shadow = new Damage((int) (dmg.getDamage() * 0.5D), false, dmg.getOwner(), dmg.getBossTarget(), false,
                            dmg.getDmgPoint());
                    shadow.setHidden(true);
                    dmg.getBossTarget().queueDamage(shadow);
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
            case PLAYER_STATE_SWORD_MULTI:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACK;
                if (frameDuration >= 10) {
                    if (this.frame == 6) {
                        this.frame = 3;
                    } else {
                        this.frame++;
                    }
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
        final byte[] posXInt = Globals.intToByte((int) soundX);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        final byte[] posYInt = Globals.intToByte((int) soundY);
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
        final byte[] d = Globals.intToByte(dmgDealt);
        bytes[10] = d[0];
        bytes[11] = d[1];
        bytes[12] = d[2];
        bytes[13] = d[3];
        if (map.isPvP()) {
            sender.sendPlayer(bytes, dmg.getOwner());

            final byte[] pvpBytes = Arrays.copyOf(bytes, bytes.length);
            pvpBytes[1] = Globals.NUMBER_TYPE_BOSS;
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

    public void setInvulnerable(final boolean set) {
        this.isInvulnerable = set;
    }

    public boolean isInvulnerable() {
        return this.isInvulnerable;
    }

    private void setRemovingDebuff(final boolean set) {
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
    public void setUniqueID(final int id) {
        this.uniqueID = id;
    }

    /**
     * Get the uID of this player.
     *
     * @return
     */
    public int getUniqueID() {
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
        this.equip[slot] = itemCode;
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
        final byte[] posXInt = Globals.intToByte((int) x);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        final byte[] posYInt = Globals.intToByte((int) y);
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
        final byte[] posXInt = Globals.intToByte((int) x);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        final byte[] posYInt = Globals.intToByte((int) y);
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
