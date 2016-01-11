package blockfighter.server.entities.player;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.GameEntity;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.buff.*;
import blockfighter.server.entities.player.skills.*;
import blockfighter.server.entities.proj.*;
import blockfighter.server.maps.GameMap;
import blockfighter.server.net.PacketSender;
import java.awt.geom.Rectangle2D;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
            PLAYER_STATE_SWORD_DRIVE = 0x06,
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
    private int uniqueID = -1;
    private String name = "";
    private double x, y, ySpeed, xSpeed;
    private boolean[] dirKeydown = new boolean[4];
    private boolean isFalling = false, isJumping = false, isInvulnerable = false, isDead = false, isRemoveDebuff = false;
    private boolean updatePos = false, updateFacing = false, updateAnimState = false;
    private byte playerState, animState, facing, frame;
    private double nextFrameTime = 0, respawnTimer = 0;
    private Rectangle2D.Double hitbox;

    private ConcurrentHashMap<Integer, Buff> buffs = new ConcurrentHashMap<>(150, 0.9f, 1);
    private Buff stunDebuff, knockbackDebuff, barrierBuff;
    private ArrayList<Buff> reflects = new ArrayList<>(10);
    private double dmgReduct, dmgAmp;
    private double barrierDmgTaken = 0, tacticalDmgMult = 0;

    private final InetAddress address;
    private final int port;
    private static PacketSender sender;
    private final GameMap map;
    private double[] stats = new double[Globals.NUM_STATS], bonusStats = new double[Globals.NUM_STATS];

    private int[] equip = new int[Globals.NUM_EQUIP_SLOTS];
    private ConcurrentHashMap<Byte, Skill> skills = new ConcurrentHashMap<>(Skill.NUM_SKILLS, 0.9f, 1);
    private boolean connected = true;

    private ConcurrentLinkedQueue<Damage> damageQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Integer> healQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> skillUseQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Buff> buffQueue = new ConcurrentLinkedQueue<>();

    private ConcurrentLinkedQueue<Integer> buffKeys = new ConcurrentLinkedQueue<>();
    private int maxBuffKeys = 0;
    private Byte nextState;
    private long lastActionTime = Globals.SERVER_MAX_IDLE;
    private long skillDuration = 0;
    private int skillCounter = 0;
    private long nextHPSend = 0;

    /**
     * Create a new player entity in the server.
     *
     * @param key The key of this player in the player array in logic module
     * @param address IP address of player
     * @param port Connected port
     * @param x Spawning x location in double
     * @param y Spawning y location in double
     * @param map Reference to server's loaded map
     * @param l Reference to Logic module
     */
    public Player(LogicModule l, byte key, InetAddress address, int port, GameMap map, double x, double y) {
        logic = l;
        this.key = key;
        this.address = address;
        this.port = port;
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double(x - 20, y - 100, 40, 100);
        this.map = map;
        facing = Globals.RIGHT;
        playerState = PLAYER_STATE_STAND;
        frame = 0;
        extendBuffKeys();
    }

    /**
     * Set the static packet sender for Player class
     *
     * @param ps Server PacketSender
     */
    public static void setPacketSender(PacketSender ps) {
        sender = ps;
    }

    /**
     * Return a freed buff key to the queue
     *
     * @param bKey Buff key to be queued
     */
    public void returnBuffKey(int bKey) {
        buffKeys.add(bKey);
    }

    /**
     * Get the next buff key from queue
     *
     * @return Byte - Free buff key, null if none are available.
     */
    public Integer getNextBuffKey() {
        if (buffKeys.isEmpty()) {
            extendBuffKeys();
        }
        if (!buffKeys.isEmpty()) {
            return buffKeys.poll();
        }
        return null;
    }

    private void extendBuffKeys() {
        for (int i = maxBuffKeys; i < maxBuffKeys + 150; i++) {
            buffKeys.add(i);
        }
        maxBuffKeys += 150;
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

    public byte getPlayerState() {
        return playerState;
    }

    public byte getAnimState() {
        return animState;
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
        return address;
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
        return port;
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
        return facing;
    }

    /**
     * Return this player's current animation frame.
     *
     * @return The player's current animation frame
     */
    public byte getFrame() {
        return frame;
    }

    /**
     * Get the item codes of the equipment on this Player
     *
     * @return int[] - Equipment Item Codes
     */
    public int[] getEquip() {
        return equip;
    }

    public boolean isDead() {
        return isDead;
    }

    private void setDead(boolean set) {
        isDead = set;
    }

    /**
     * Get the skill level of specific skill using a skill code
     *
     * @param skillCode Skill code of skill
     * @return The level of the skill
     */
    public int getSkillLevel(byte skillCode) {
        if (!hasSkill(skillCode)) {
            return -1;
        }
        return skills.get(skillCode).getLevel();
    }

    public Skill getSkill(byte skillCode) {
        return skills.get(skillCode);
    }

    public boolean isSkillMaxed(byte skillCode) {
        if (!hasSkill(skillCode)) {
            return false;
        }
        return skills.get(skillCode).isMaxed();
    }

    public boolean hasSkill(byte skillCode) {
        return skills.containsKey(skillCode);
    }

    public Map.Entry<Integer, Buff> hasBuff(Class buffType) {
        for (Map.Entry<Integer, Buff> bEntry : buffs.entrySet()) {
            Buff b = bEntry.getValue();
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
    public void setDirKeydown(int direction, boolean move) {
        if (move) {
            lastActionTime = Globals.SERVER_MAX_IDLE;
        }
        dirKeydown[direction] = move;
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
        if (isDead()) {
            xSpeed = 0;
        }
        xSpeed = speed;
    }

    /**
     * Set the level of skill with skill code
     *
     * @param skillCode Skill to be set
     * @param level Level of skill to be set
     */
    public void setSkill(byte skillCode, byte level) {
        Skill newSkill = null;
        switch (skillCode) {
            case Skill.SWORD_CINDER:
                newSkill = new SkillSwordCinder();
                break;
            case Skill.SWORD_DRIVE:
                newSkill = new SkillSwordDrive();
                break;
            case Skill.SWORD_MULTI:
                newSkill = new SkillSwordMulti();
                break;
            case Skill.SWORD_SLASH:
                newSkill = new SkillSwordSlash();
                break;
            case Skill.SWORD_TAUNT:
                newSkill = new SkillSwordTaunt();
                break;
            case Skill.SWORD_VORPAL:
                newSkill = new SkillSwordVorpal();
                break;
            case Skill.BOW_ARC:
                newSkill = new SkillBowArc();
                break;
            case Skill.BOW_FROST:
                newSkill = new SkillBowFrost();
                break;
            case Skill.BOW_POWER:
                newSkill = new SkillBowPower();
                break;
            case Skill.BOW_RAPID:
                newSkill = new SkillBowRapid();
                break;
            case Skill.BOW_STORM:
                newSkill = new SkillBowStorm();
                break;
            case Skill.BOW_VOLLEY:
                newSkill = new SkillBowVolley();
                break;
            case Skill.SHIELD_FORTIFY:
                newSkill = new SkillShieldFortify();
                break;
            case Skill.SHIELD_IRON:
                newSkill = new SkillShieldIron();
                break;
            case Skill.SHIELD_CHARGE:
                newSkill = new SkillShieldCharge();
                break;
            case Skill.SHIELD_REFLECT:
                newSkill = new SkillShieldReflect();
                break;
            case Skill.SHIELD_TOSS:
                newSkill = new SkillShieldToss();
                break;
            case Skill.SHIELD_DASH:
                newSkill = new SkillShieldDash();
                break;
            case Skill.PASSIVE_DUALSWORD:
                newSkill = new SkillPassiveDualSword();
                break;
            case Skill.PASSIVE_KEENEYE:
                newSkill = new SkillPassiveKeenEye();
                break;
            case Skill.PASSIVE_VITALHIT:
                newSkill = new SkillPassiveVitalHit();
                break;
            case Skill.PASSIVE_SHIELDMASTERY:
                newSkill = new SkillPassiveShieldMastery();
                break;
            case Skill.PASSIVE_BARRIER:
                newSkill = new SkillPassiveBarrier();
                break;
            case Skill.PASSIVE_RESIST:
                newSkill = new SkillPassiveResistance();
                break;
            case Skill.PASSIVE_BOWMASTERY:
                newSkill = new SkillPassiveBowMastery();
                break;
            case Skill.PASSIVE_WILLPOWER:
                newSkill = new SkillPassiveWillpower();
                break;
            case Skill.PASSIVE_TACTICAL:
                newSkill = new SkillPassiveTactical();
                break;
            case Skill.PASSIVE_REVIVE:
                newSkill = new SkillPassiveRevive();
                break;
            case Skill.PASSIVE_SHADOWATTACK:
                newSkill = new SkillPassiveShadowAttack();
                break;
            case Skill.PASSIVE_12:
                newSkill = new SkillPassive12();
                break;
        }
        if (newSkill != null) {
            newSkill.setLevel(level);
            skills.put(skillCode, newSkill);
        }
    }

    @Override
    public void run() {
        try {
            update();
        } catch (Exception ex) {
            Globals.log(ex.getLocalizedMessage(), ex, true);
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
        lastActionTime -= Globals.LOGIC_UPDATE / 1000000;
        nextHPSend -= Globals.LOGIC_UPDATE / 1000000;
        if (isUsingSkill()) {
            skillDuration += Globals.LOGIC_UPDATE / 1000000;
        }
        //Update Timers/Game principles(Gravity)
        updateSkillCd();
        updateBuffs();

        queuePlayerState(PLAYER_STATE_STAND);
        updateFall();
        boolean movedX = updateX(xSpeed);
        hitbox.x = x - 20;
        hitbox.y = y - 100;

        if (isDead()) {
            //Update respawn Timer
            updateDead();
        } else {
            //Update Actions
            if (isStunned() && !isKnockback()) {
                setXSpeed(0);
            }

            if (!isUsingSkill() && !isStunned() && !isKnockback()) {
                updateFacing();
                if (!isJumping && !isFalling) {
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
        if (updatePos) {
            sendPos();
        }
        if (updateFacing) {
            sendFacing();
        }
        if (updateAnimState) {
            sendState();
        }

        if (connected && lastActionTime <= 0) {
            Globals.log("Player", address + ":" + port + " Idle disconnected Key: " + key, Globals.LOG_TYPE_DATA, true);
            disconnect();
        }
    }

    private void updateDead() {
        respawnTimer -= Globals.LOGIC_UPDATE;
        damageQueue.clear();
        healQueue.clear();
        skillUseQueue.clear();
        buffQueue.clear();
        setXSpeed(0);
        if (respawnTimer <= 0) {
            respawn();
        }
    }

    private void updatePlayerState() {
        if (nextState != null && !isUsingSkill() && playerState != nextState) {
            setPlayerState(nextState);
            nextState = null;
        }
        if (isUsingSkill()) {
            nextState = null;
        }
    }

    private void castSkill(byte[] data, byte newState, byte weaponSlot) {
        if (!skills.get(data[3]).canCast(getItemType(equip[weaponSlot]))) {
            return;
        }
        queuePlayerState(newState);
        skills.get(data[3]).setCooldown();
        sendCooldown(data);

        //Tactical Execution Passive
        //Add after being able to cast skill
        if (hasSkill(Skill.PASSIVE_TACTICAL) && tacticalDmgMult < 0.01 + 0.01 * getSkillLevel(Skill.PASSIVE_TACTICAL)) {
            tacticalDmgMult += 0.01;
        }
    }

    private void updateSkillCast() {
        if (isUsingSkill()) {
            skillUseQueue.clear();
            return;
        }
        if (skillUseQueue.isEmpty()) {
            return;
        }

        byte[] data = skillUseQueue.poll();
        skillUseQueue.clear();
        if (data != null) {
            if (data[3] == Skill.SHIELD_IRON || (!isStunned() && !isKnockback())) {
                if (hasSkill(data[3])) {
                    skillDuration = 0;
                    skillCounter = 0;
                    switch (data[3]) {
                        case Skill.SWORD_SLASH:
                            castSkill(data, PLAYER_STATE_SWORD_SLASH, Globals.ITEM_WEAPON);
                            break;
                        case Skill.SWORD_VORPAL:
                            castSkill(data, PLAYER_STATE_SWORD_VORPAL, Globals.ITEM_WEAPON);
                            break;
                        case Skill.SWORD_DRIVE:
                            castSkill(data, PLAYER_STATE_SWORD_DRIVE, Globals.ITEM_WEAPON);
                            break;
                        case Skill.SWORD_MULTI:
                            castSkill(data, PLAYER_STATE_SWORD_MULTI, Globals.ITEM_WEAPON);
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
        if (isSkillMaxed(Skill.SWORD_SLASH) && skillDuration == 0) {
            queueBuff(new BuffSwordSlash(4000, .1, this));
            sendParticle(logic.getRoom(), Globals.PARTICLE_SWORD_SLASHBUFF, key);
        }
        if (skillDuration % 200 == 0 && skillDuration < 600) {
            skillCounter++;
            ProjSwordSlash proj = new ProjSwordSlash(logic, logic.getNextProjKey(), this, x, y, skillCounter);
            logic.queueAddProj(proj);
            if (skillCounter == 1) {
                sendParticle(logic.getRoom(), Globals.PARTICLE_SWORD_SLASH1, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(), facing);
            } else if (skillCounter == 2) {
                sendParticle(logic.getRoom(), Globals.PARTICLE_SWORD_SLASH2, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(), facing);
            } else if (skillCounter == 3) {
                sendParticle(logic.getRoom(), Globals.PARTICLE_SWORD_SLASH3, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(), facing);
            }
        }

        if (skillDuration >= 600 || isStunned() || isKnockback()) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillSwordDrive() {
        if (skillDuration % 250 == 0 && skillDuration < 1000) {
            ProjSwordDrive proj = new ProjSwordDrive(logic, logic.getNextProjKey(), this, x, y);
            logic.queueAddProj(proj);
            if (skillDuration == 0) {
                sendParticle(logic.getRoom(), Globals.PARTICLE_SWORD_DRIVE, key, facing);
            }
        }
        if (skillDuration >= 1000) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillSwordVorpal() {
        int skillTime = 170, numHits = 3;
        if (skills.get(Skill.SWORD_VORPAL).getLevel() == 30) {
            skillTime = 150;
            numHits = 5;
        }
        if (skillCounter == numHits) {
            skillCounter++;
        }
        if (skillDuration % skillTime == 0 && skillCounter < numHits) {
            ProjSwordVorpal proj = new ProjSwordVorpal(logic, logic.getNextProjKey(), this, x, y);
            logic.queueAddProj(proj);
            sendParticle(logic.getRoom(), Globals.PARTICLE_SWORD_VORPAL, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(), facing);
            skillCounter++;
        }

        if (skillDuration >= 800) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillSwordTaunt() {
        if (skillDuration == 0) {
            if (isSkillMaxed(Skill.SWORD_TAUNT)) {
                queueBuff(new BuffSwordTaunt(10000, 0.2, 0.2, this));
                sendParticle(logic.getRoom(), Globals.PARTICLE_SWORD_TAUNTBUFF, key);
            }
            sendParticle(logic.getRoom(), Globals.PARTICLE_SWORD_TAUNTAURA1, key);
        } else if (skillDuration == 50) {
            ProjSwordTaunt proj = new ProjSwordTaunt(logic, logic.getNextProjKey(), this, x, y);
            logic.queueAddProj(proj);
            sendParticle(logic.getRoom(), Globals.PARTICLE_SWORD_TAUNT, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(), facing);
        }
        if (skillDuration >= 350) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillSwordMulti() {
        int numHits = skills.get(Skill.SWORD_MULTI).getLevel() + 6;
        if (isSkillMaxed(Skill.SWORD_MULTI) && !isInvulnerable()) {
            setInvulnerable(true);
        }
        if (skillCounter == numHits) {
            skillCounter++;
        }
        if (skillDuration % 50 == 0 && skillCounter < numHits) {
            ProjSwordMulti proj = new ProjSwordMulti(logic, logic.getNextProjKey(), this, x, y + (Globals.rng(40) - 20));
            logic.queueAddProj(proj);
            sendParticle(logic.getRoom(), Globals.PARTICLE_SWORD_MULTI, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(), facing);
            skillCounter++;
        }
        if (skillDuration >= numHits * 50 + 110 || (!isInvulnerable() && (isStunned() || isKnockback()))) {
            setInvulnerable(false);
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillSwordCinder() {
        if (skillDuration == 50) {
            ProjSwordCinder proj = new ProjSwordCinder(logic, logic.getNextProjKey(), this, x, y);
            logic.queueAddProj(proj);
            sendParticle(logic.getRoom(), Globals.PARTICLE_SWORD_CINDER, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(), facing);
        }
        if (skillDuration >= 350) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillBowArc() {
        if (skillDuration == 100) {
            ProjBowArc proj = new ProjBowArc(logic, logic.getNextProjKey(), this, x, y);
            logic.queueAddProj(proj);
            sendParticle(logic.getRoom(), Globals.PARTICLE_BOW_ARC, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(), facing);
        }
        if (skillDuration == 150 || skillDuration == 200) {
            ProjBowArc proj = new ProjBowArc(logic, logic.getNextProjKey(), this, x, y);
            logic.queueAddProj(proj);
        }
        if (skillDuration >= 300) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillBowFrost() {
        if (skillDuration == 160) {
            ProjBowFrost proj = new ProjBowFrost(logic, logic.getNextProjKey(), this, x, y, false);
            logic.queueAddProj(proj);
            sendParticle(logic.getRoom(), Globals.PARTICLE_BOW_FROSTARROW, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(), facing);
        }

        if (isSkillMaxed(Skill.BOW_FROST) && (skillDuration == 250 || skillDuration == 340)) {
            ProjBowFrost proj = new ProjBowFrost(logic, logic.getNextProjKey(), this, x, y, true);
            logic.queueAddProj(proj);
            sendParticle(logic.getRoom(), Globals.PARTICLE_BOW_FROSTARROW, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(), facing);
        }
        if (skillDuration >= 380) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillBowStorm() {
        if (skillDuration == 100) {
            ProjBowStorm proj = new ProjBowStorm(logic, logic.getNextProjKey(), this, x, y);
            logic.queueAddProj(proj);
            sendParticle(logic.getRoom(), Globals.PARTICLE_BOW_STORM, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(), facing);
        }
        if (skillDuration >= 200) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillBowRapid() {
        if (skillDuration == 150 || skillDuration == 300 || skillDuration == 450) {
            double projY = y;
            if (skillDuration == 150) {
                projY = y - 20;
            } else if (skillDuration == 450) {
                projY = y + 20;
            }
            ProjBowRapid proj = new ProjBowRapid(logic, logic.getNextProjKey(), this, x, projY);
            logic.queueAddProj(proj);
            sendParticle(logic.getRoom(), Globals.PARTICLE_BOW_RAPID, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(), facing);
        }
        if (skillDuration >= 550) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillBowVolley() {
        if (skillDuration % 100 == 0 && skillCounter < 20) {
            ProjBowVolley proj = new ProjBowVolley(logic, logic.getNextProjKey(), this, x, y - 10 + Globals.rng(40));
            logic.queueAddProj(proj);
            sendParticle(logic.getRoom(), Globals.PARTICLE_BOW_VOLLEYARROW, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(), facing);
            sendParticle(logic.getRoom(), Globals.PARTICLE_BOW_VOLLEYBOW, getX(), getY(), facing);
            skillCounter++;
        }
        if (skillDuration >= 1900 || isStunned() || isKnockback()) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillBowPower() {
        if (skillDuration <= 400 && skillDuration % 50 == 0) {
            sendParticle(logic.getRoom(), Globals.PARTICLE_BOW_POWERCHARGE, x + ((facing == Globals.RIGHT) ? 75 : -75), y - 250, facing);
        } else if (skillDuration == 800) {
            ProjBowPower proj = new ProjBowPower(logic, logic.getNextProjKey(), this, x, y);
            logic.queueAddProj(proj);
            sendParticle(logic.getRoom(), Globals.PARTICLE_BOW_POWER, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(), facing);
        }
        if (skillDuration >= 1400 || (!isSkillMaxed(Skill.BOW_POWER) && skillDuration < 800 && (isStunned() || isKnockback()))) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillShieldFortify() {
        if (skillDuration == 0) {
            sendParticle(logic.getRoom(), Globals.PARTICLE_SHIELD_FORTIFY, key);
        }
        if (skillDuration >= 350) {
            queueBuff(new BuffShieldFortify(5000, 0.01 + 0.005 * getSkillLevel(Skill.SHIELD_FORTIFY), this));
            sendParticle(logic.getRoom(), Globals.PARTICLE_SHIELD_FORTIFYBUFF, key);
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillShieldIron() {
        if (skillDuration == 0) {
            sendParticle(logic.getRoom(), Globals.PARTICLE_SHIELD_IRON, key);
        }
        if (skillDuration == 100) {
            setRemovingDebuff(true);
            queueBuff(new BuffShieldIron(2000, 0.55 + 0.01 * getSkillLevel(Skill.SHIELD_IRON)));
            if (isSkillMaxed(Skill.SHIELD_IRON)) {
                for (Map.Entry<Byte, Player> player : logic.getPlayers().entrySet()) {
                    Player p = player.getValue();
                    if (p != this) {
                        p.queueBuff(new BuffShieldIron(2000, 0.4));
                        sendParticle(logic.getRoom(), Globals.PARTICLE_SHIELD_IRONALLY, p.getKey());
                    }
                }
            }
        }
        if (skillDuration >= 2100) {
            setRemovingDebuff(false);
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillShieldReflectHit(double dmgTaken, double mult) {
        ProjShieldReflect proj = new ProjShieldReflect(logic, logic.getNextProjKey(), this, x, y, dmgTaken * mult);
        logic.queueAddProj(proj);
        sendParticle(logic.getRoom(), Globals.PARTICLE_SHIELD_REFLECTHIT, x, y);
    }

    private void updateSkillShieldReflectCast() {
        if (skillDuration == 0) {
            queueBuff(new BuffShieldReflect(3000, .4 + 0.02 * getSkillLevel(Skill.SHIELD_REFLECT), this, this));
            sendParticle(logic.getRoom(), Globals.PARTICLE_SHIELD_REFLECTCAST, key);
            sendParticle(logic.getRoom(), Globals.PARTICLE_SHIELD_REFLECTBUFF, key);
            if (isSkillMaxed(Skill.SHIELD_REFLECT)) {
                for (Map.Entry<Byte, Player> player : logic.getPlayers().entrySet()) {
                    Player p = player.getValue();
                    if (p != this) {
                        p.queueBuff(new BuffShieldReflect(3000, 0.4, this, p));
                        sendParticle(logic.getRoom(), Globals.PARTICLE_SHIELD_REFLECTCAST, p.getKey());
                    }
                }
            }
        }
        if (skillDuration >= 250) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillShieldToss() {
        if (skillDuration == 100 || (isSkillMaxed(Skill.SHIELD_TOSS) && (skillDuration == 300 || skillDuration == 500))) {
            ProjShieldToss proj = new ProjShieldToss(logic, logic.getNextProjKey(), this, x, y);
            logic.queueAddProj(proj);
            sendParticle(logic.getRoom(), Globals.PARTICLE_SHIELD_TOSS, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY(), facing);
        }
        if (skillDuration >= 700) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillShieldCharge() {
        setXSpeed((facing == Globals.RIGHT) ? 18 : -18);
        if (skillDuration == 0) {
            ProjShieldCharge proj = new ProjShieldCharge(logic, logic.getNextProjKey(), this, x, y);
            logic.queueAddProj(proj);
            sendParticle(logic.getRoom(), Globals.PARTICLE_SHIELD_CHARGE, key, facing);
        }
        if (skillDuration >= 750) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillShieldDash() {
        if (!isStunned() && !isKnockback()) {
            setXSpeed((facing == Globals.RIGHT) ? 15 : -15);
        }
        if (isSkillMaxed(Skill.SHIELD_DASH) && !isInvulnerable()) {
            setInvulnerable(true);
        }

        if (skillDuration == 0) {
            queueBuff(new BuffShieldDash(5000, 0.01 + 0.003 * getSkillLevel(Skill.SHIELD_DASH), this));
            sendParticle(logic.getRoom(), Globals.PARTICLE_SHIELD_DASHBUFF, key);
            sendParticle(logic.getRoom(), Globals.PARTICLE_SHIELD_DASH, key, facing);
            setYSpeed(-4);
        }

        if (skillDuration >= 250 || isStunned() || isKnockback()) {
            setInvulnerable(false);
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillUse() {
        if (!isKnockback()) {
            setXSpeed(0);
        }
        switch (playerState) {
            case PLAYER_STATE_SWORD_SLASH:
                updateSkillSwordSlash();
                break;
            case PLAYER_STATE_SWORD_DRIVE:
                updateSkillSwordDrive();
                break;
            case PLAYER_STATE_SWORD_VORPAL:
                updateSkillSwordVorpal();
                break;
            case PLAYER_STATE_SWORD_MULTI:
                updateSkillSwordMulti();
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

    private void updateSkillCd() {
        for (Map.Entry<Byte, Skill> s : skills.entrySet()) {
            s.getValue().reduceCooldown((long) (Globals.LOGIC_UPDATE / 1000000));
        }
    }

    private void updateHP() {
        //Empty damage queued
        if (isInvulnerable()) {
            //Take no damage
            damageQueue.clear();
        }
        while (!damageQueue.isEmpty()) {
            Damage dmg = damageQueue.poll();
            if (dmg != null) {
                int amount = (int) (dmg.getDamage() * dmgAmp);

                //Proc stuff like shadow attack
                dmg.proc();

                //Check if I have reflect damage buff and reflect off owner
                if (dmg.canReflect()) {
                    for (Buff b : reflects) {
                        if (b instanceof BuffShieldReflect) {
                            ((BuffShieldReflect) b).getOwner().updateSkillShieldReflectHit(amount, ((BuffShieldReflect) b).getMultiplier());
                        }
                    }
                }
                //If it isnt true damage do reduction
                if (!dmg.isTrueDamage()) {
                    amount = (int) (amount * stats[Globals.STAT_DAMAGEREDUCT]);
                }

                //Buff Reductions
                amount = (int) (amount * dmgReduct);

                //Defender Mastery Passive Reduction
                if (hasSkill(Skill.PASSIVE_SHIELDMASTERY)
                        && getItemType(equip[Globals.ITEM_WEAPON]) == Globals.ITEM_SWORD
                        && getItemType(equip[Globals.ITEM_OFFHAND]) == Globals.ITEM_SHIELD) {
                    amount = (int) (amount * (1 - (0.05 + 0.005 * getSkillLevel(Skill.PASSIVE_SHIELDMASTERY))));
                }

                //Dual Wield Passive Reduction
                if (hasSkill(Skill.PASSIVE_DUALSWORD)
                        && getItemType(equip[Globals.ITEM_WEAPON]) == Globals.ITEM_SWORD
                        && getItemType(equip[Globals.ITEM_OFFHAND]) == Globals.ITEM_SWORD) {
                    amount = (int) (amount * (1 - (0.01 * getSkillLevel(Skill.PASSIVE_DUALSWORD))));
                }

                //Resistance Passive
                if (hasSkill(Skill.PASSIVE_RESIST) && stats[Globals.STAT_MINHP] > stats[Globals.STAT_MAXHP] * 0.5 && skills.get(Skill.PASSIVE_RESIST).canCast()) {
                    if (amount > stats[Globals.STAT_MAXHP] * 0.5) {
                        amount = (int) (stats[Globals.STAT_MAXHP] * 0.5);
                        skills.get(Skill.PASSIVE_RESIST).setCooldown();
                        sendCooldown(Skill.PASSIVE_RESIST);
                        sendParticle(logic.getRoom(), Globals.PARTICLE_PASSIVE_RESIST, x, y);
                    }
                }
                //Barrier reduction
                if (barrierBuff != null) {
                    amount = (int) ((BuffPassiveBarrier) barrierBuff).reduceDmg(amount);
                    sendParticle(logic.getRoom(), Globals.PARTICLE_PASSIVE_BARRIER, dmg.getDmgPoint().x, dmg.getDmgPoint().y);
                }
                tacticalDmgMult = 0;
                //Send client damage display
                sendDamage(dmg, amount);
                //Final damage taken
                stats[Globals.STAT_MINHP] -= amount;
                if (amount > 0) {
                    nextHPSend = 0;
                }
                if (hasSkill(Skill.PASSIVE_BARRIER) && skills.get(Skill.PASSIVE_BARRIER).canCast()) {
                    barrierDmgTaken += amount;
                    if (barrierDmgTaken >= stats[Globals.STAT_MAXHP] * 0.5) {
                        barrierDmgTaken = 0;
                        queueBuff(new BuffPassiveBarrier(stats[Globals.STAT_MAXHP] * (0.1 + 0.005 * getSkillLevel(Skill.PASSIVE_BARRIER)), this));
                        sendParticle(logic.getRoom(), Globals.PARTICLE_PASSIVE_BARRIER, dmg.getDmgPoint().x, dmg.getDmgPoint().y);
                        skills.get(Skill.PASSIVE_BARRIER).setCooldown();
                        sendCooldown(Skill.PASSIVE_BARRIER);
                    }
                }
            }
        }
        //Empty healing queued
        while (!healQueue.isEmpty()) {
            Integer heal = healQueue.poll();
            if (heal != null) {
                stats[Globals.STAT_MINHP] += heal;
                nextHPSend = 0;
            }
        }
        //Add regenerated HP(1% of REGEN per 10ms tick)
        stats[Globals.STAT_MINHP] += stats[Globals.STAT_REGEN] / 100D;

        if (stats[Globals.STAT_MINHP] > stats[Globals.STAT_MAXHP]) {
            stats[Globals.STAT_MINHP] = stats[Globals.STAT_MAXHP];
        } else if (stats[Globals.STAT_MINHP] < 0) {
            stats[Globals.STAT_MINHP] = 0;
        }

        if (stats[Globals.STAT_MINHP] <= 0) {
            die();
        }

        //Update client hp every 150ms or if damaged/healed(excluding regen).
        if (nextHPSend <= 0) {
            byte[] stat = Globals.intToByte((int) stats[Globals.STAT_MINHP]);
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
            bytes[0] = Globals.DATA_PLAYER_GET_STAT;
            bytes[1] = key;
            bytes[2] = Globals.STAT_MINHP;
            System.arraycopy(stat, 0, bytes, 3, stat.length);
            sender.sendPlayer(bytes, address, port);
            nextHPSend = 150;
        }
    }

    private void updateBuffs() {
        //Update exisiting buffs
        stunDebuff = null;
        knockbackDebuff = null;
        barrierBuff = null;
        reflects.clear();
        dmgReduct = 1;
        dmgAmp = 1;
        LinkedList<Integer> remove = new LinkedList<>();

        for (Map.Entry<Integer, Buff> bEntry : buffs.entrySet()) {
            Buff b = bEntry.getValue();
            b.update();

            //Track if stunned, knocked or has a barrier buff.
            if (canDebuffAffect() && b instanceof BuffStun) {
                if (stunDebuff == null) {
                    stunDebuff = b;
                }
            } else if (canDebuffAffect() && b instanceof BuffKnockback) {
                if (knockbackDebuff == null) {
                    knockbackDebuff = b;
                }
            } else if (b instanceof BuffShieldReflect) {
                reflects.add(b);
            } else if (b instanceof BuffPassiveBarrier) {
                if (barrierBuff == null) {
                    barrierBuff = b;
                }
            }

            //Add all the damage reduction buffs(Multiplicative)
            if (b instanceof BuffDmgReduct) {
                dmgReduct = dmgReduct * ((BuffDmgReduct) b).getDmgTakenMult();
            }

            //Add all the damage intake amplification(Additive)
            if (b instanceof BuffDmgTakenAmp) {
                dmgAmp = dmgAmp + ((BuffDmgTakenAmp) b).getDmgTakenAmp();
            }

            //Remove expired buffs/remove debuffs when invulnerable/special state
            if (b.isExpired() || (!canDebuffAffect() && b.isDebuff())) {
                remove.add(bEntry.getKey());
            }
        }
        for (Integer bKey : remove) {
            buffs.remove(bKey);
            returnBuffKey(bKey);
        }

        //Empty and add buffs from queue
        while (!buffQueue.isEmpty()) {
            Buff b = buffQueue.poll();
            if (b != null) {
                if (!canDebuffAffect() && b.isDebuff()) {
                    //Don't apply debuff when invulnerable
                    continue;
                }

                if (b instanceof BuffShieldDash) {
                    Map.Entry<Integer, Buff> prevBuff = hasBuff(BuffShieldDash.class);
                    if (prevBuff != null) {
                        buffs.remove(prevBuff.getKey());
                    }
                } else if (b instanceof BuffSwordSlash) {
                    Map.Entry<Integer, Buff> prevBuff = hasBuff(BuffSwordSlash.class);
                    if (prevBuff != null) {
                        buffs.remove(prevBuff.getKey());
                    }
                }
                Integer bKey = getNextBuffKey();
                if (bKey != null) {
                    buffs.put(bKey, b);
                }
            }
        }
    }

    private void die() {
        if (logic.getMap().isPvP()) {
            for (Map.Entry<Byte, Player> player : logic.getPlayers().entrySet()) {
                if (player.getValue() != this) {
                    player.getValue().giveEXP(Globals.calcEXPtoNxtLvl(stats[Globals.STAT_LEVEL]) / 7);
                }
            }
        }
        setInvulnerable(false);
        setRemovingDebuff(false);
        setDead(true);
        setFacing(Globals.RIGHT);
        setPlayerState(PLAYER_STATE_DEAD);
        damageQueue.clear();
        healQueue.clear();
        skillUseQueue.clear();
        buffQueue.clear();
        barrierDmgTaken = 0;
        respawnTimer = 5000000000D;
    }

    private void respawn() {
        setInvulnerable(false);
        setRemovingDebuff(false);
        setDead(false);
        respawnTimer = 0;
        buffs.clear();
        stats[Globals.STAT_MINHP] = stats[Globals.STAT_MAXHP];
        setXSpeed(0);
        double xSpawnBound = logic.getMap().getBoundary()[Globals.MAP_RIGHT] - logic.getMap().getBoundary()[Globals.MAP_LEFT];
        setPos(Globals.rng((int) xSpawnBound) + logic.getMap().getBoundary()[Globals.MAP_LEFT], -100);
        queuePlayerState(PLAYER_STATE_STAND);
    }

    /**
     * Roll a damage number between the max and min damage of player.
     *
     * @return Randomly rolled damage.
     */
    public double rollDamage() {
        double dmg = Globals.rng((int) (stats[Globals.STAT_MAXDMG] - stats[Globals.STAT_MINDMG])) + stats[Globals.STAT_MINDMG];
        double mult = 1;
        for (Map.Entry<Integer, Buff> bEntry : buffs.entrySet()) {
            Buff b = bEntry.getValue();
            if (b instanceof BuffDmgIncrease) {
                mult += ((BuffDmgIncrease) b).getDmgIncrease();
            }
        }
        //Defender Mastery Passive
        if (hasSkill(Skill.PASSIVE_SHIELDMASTERY)
                && getItemType(equip[Globals.ITEM_WEAPON]) == Globals.ITEM_SWORD
                && getItemType(equip[Globals.ITEM_OFFHAND]) == Globals.ITEM_SHIELD) {
            mult += 0.09 + 0.002 * getSkillLevel(Skill.PASSIVE_SHIELDMASTERY);
        }
        //Power of Will Passive
        if (hasSkill(Skill.PASSIVE_WILLPOWER)) {
            //(5% + 0.5% Per Level) * %HP Left
            mult += (0.05 + 0.005 * getSkillLevel(Skill.PASSIVE_WILLPOWER)) * (stats[Globals.STAT_MINHP] / stats[Globals.STAT_MAXHP]);
        }
        //Tactical Execution Passive
        mult += tacticalDmgMult;
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
    public boolean rollCrit(double bonusCritChance) {
        double totalCritChance = stats[Globals.STAT_CRITCHANCE] + bonusCritChance;
        //Dual Sword Passive
        if (hasSkill(Skill.PASSIVE_DUALSWORD)
                && getItemType(equip[Globals.ITEM_WEAPON]) == Globals.ITEM_SWORD
                && getItemType(equip[Globals.ITEM_OFFHAND]) == Globals.ITEM_SWORD) {
            //Check if has Dual Sword passive AND Mainhand/Offhand are both Swords.
            totalCritChance += 0.06 + 0.003 * getSkillLevel(Skill.PASSIVE_DUALSWORD);
        }
        //Keen Eye Passive
        if (hasSkill(Skill.PASSIVE_KEENEYE)) {
            totalCritChance += 0.01 + 0.003 * getSkillLevel(Skill.PASSIVE_KEENEYE);
        }
        return Globals.rng(10000) + 1 < (int) (totalCritChance * 10000);
    }

    public double criticalDamage(double dmg) {
        return criticalDamage(dmg, 0);
    }

    public double criticalDamage(double dmg, double bonusCritDmg) {
        double totalCritDmg = 1 + stats[Globals.STAT_CRITDMG] + bonusCritDmg;
        //Bow Mastery Passive
        if (hasSkill(Skill.PASSIVE_BOWMASTERY)
                && getItemType(equip[Globals.ITEM_WEAPON]) == Globals.ITEM_BOW
                && getItemType(equip[Globals.ITEM_OFFHAND]) == Globals.ITEM_QUIVER) {
            //Check if has Dual Sword passive AND Mainhand/Offhand are both Swords.
            totalCritDmg += 0.3 + 0.04 * getSkillLevel(Skill.PASSIVE_BOWMASTERY);
        }
        //Keen Eye Passive
        if (hasSkill(Skill.PASSIVE_VITALHIT)) {
            totalCritDmg += 0.1 + 0.02 * getSkillLevel(Skill.PASSIVE_VITALHIT);
        }
        return dmg * (totalCritDmg);
    }

    private void updateStats() {
        stats[Globals.STAT_ARMOR] = Globals.calcArmor((int) (stats[Globals.STAT_DEFENSE] + bonusStats[Globals.STAT_DEFENSE]));
        stats[Globals.STAT_REGEN] = Globals.calcRegen((int) (stats[Globals.STAT_SPIRIT] + bonusStats[Globals.STAT_SPIRIT]));
        double hpPercent = 1;
        if (stats[Globals.STAT_MAXHP] > 0) {
            hpPercent = stats[Globals.STAT_MINHP] / stats[Globals.STAT_MAXHP];
        }
        stats[Globals.STAT_MAXHP] = Globals.calcMaxHP((int) (stats[Globals.STAT_DEFENSE] + bonusStats[Globals.STAT_DEFENSE]));
        stats[Globals.STAT_MINHP] = hpPercent * stats[Globals.STAT_MAXHP];
        stats[Globals.STAT_MINDMG] = Globals.calcMinDmg((int) (stats[Globals.STAT_POWER] + bonusStats[Globals.STAT_POWER]));
        stats[Globals.STAT_MAXDMG] = Globals.calcMaxDmg((int) (stats[Globals.STAT_POWER] + bonusStats[Globals.STAT_POWER]));
        stats[Globals.STAT_CRITCHANCE] = Globals.calcCritChance((int) (stats[Globals.STAT_SPIRIT] + bonusStats[Globals.STAT_SPIRIT]));
        stats[Globals.STAT_CRITDMG] = Globals.calcCritDmg((int) (stats[Globals.STAT_SPIRIT] + bonusStats[Globals.STAT_SPIRIT]));

        stats[Globals.STAT_CRITCHANCE] = stats[Globals.STAT_CRITCHANCE] + bonusStats[Globals.STAT_CRITCHANCE];
        stats[Globals.STAT_CRITDMG] = stats[Globals.STAT_CRITDMG] + bonusStats[Globals.STAT_CRITDMG];
        stats[Globals.STAT_REGEN] = stats[Globals.STAT_REGEN] + bonusStats[Globals.STAT_REGEN];
        stats[Globals.STAT_ARMOR] = stats[Globals.STAT_ARMOR] + bonusStats[Globals.STAT_ARMOR];
        stats[Globals.STAT_DAMAGEREDUCT] = 1 - Globals.calcReduction(stats[Globals.STAT_ARMOR]);
    }

    public void giveDrop(double lvl) {
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_PLAYER_GIVEDROP;
        byte[] lev = Globals.intToByte((int) lvl);
        bytes[1] = lev[0];
        bytes[2] = lev[1];
        bytes[3] = lev[2];
        bytes[4] = lev[3];
        sender.sendPlayer(bytes, address, port);
    }

    public void giveEXP(double amount) {
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_PLAYER_GIVEEXP;
        byte[] exp = Globals.intToByte((int) amount);
        bytes[1] = exp[0];
        bytes[2] = exp[1];
        bytes[3] = exp[2];
        bytes[4] = exp[3];
        sender.sendPlayer(bytes, address, port);

        bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 3];
        bytes[0] = Globals.DATA_DAMAGE;
        bytes[1] = Damage.DAMAGE_TYPE_EXP;
        byte[] posXInt = Globals.intToByte((int) x - 20);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        byte[] posYInt = Globals.intToByte((int) y);
        bytes[6] = posYInt[0];
        bytes[7] = posYInt[1];
        bytes[8] = posYInt[2];
        bytes[9] = posYInt[3];
        byte[] d = Globals.intToByte((int) amount);
        bytes[10] = d[0];
        bytes[11] = d[1];
        bytes[12] = d[2];
        bytes[13] = d[3];
        sender.sendAll(bytes, logic.getRoom());
    }

    /**
     * Check if a rectangle intersects with this player's hitbox
     *
     * @param box Box to be checked
     * @return True if the boxes intersect
     */
    public boolean intersectHitbox(Rectangle2D.Double box) {
        return hitbox.intersects(box);
    }

    public Rectangle2D.Double getHitbox() {
        return hitbox;
    }

    /**
     * Return if player is stunned
     *
     * @return isStun
     */
    public boolean isStunned() {
        return stunDebuff != null;
    }

    /**
     * Return if player is being knocked back.
     *
     * @return isKnockback
     */
    public boolean isKnockback() {
        return knockbackDebuff != null;
    }

    /**
     * Check if player is in a skill use state
     *
     * @return True if player is in a skill use state.
     */
    public boolean isUsingSkill() {
        return playerState == PLAYER_STATE_SWORD_SLASH
                || playerState == PLAYER_STATE_SWORD_VORPAL
                || playerState == PLAYER_STATE_SWORD_DRIVE
                || playerState == PLAYER_STATE_SWORD_MULTI
                || playerState == PLAYER_STATE_SWORD_TAUNT
                || playerState == PLAYER_STATE_SWORD_CINDER
                || playerState == PLAYER_STATE_BOW_ARC
                || playerState == PLAYER_STATE_BOW_POWER
                || playerState == PLAYER_STATE_BOW_RAPID
                || playerState == PLAYER_STATE_BOW_FROST
                || playerState == PLAYER_STATE_BOW_STORM
                || playerState == PLAYER_STATE_BOW_VOLLEY
                || playerState == PLAYER_STATE_SHIELD_CHARGE
                || playerState == PLAYER_STATE_SHIELD_DASH
                || playerState == PLAYER_STATE_SHIELD_FORTIFY
                || playerState == PLAYER_STATE_SHIELD_IRON
                || playerState == PLAYER_STATE_SHIELD_REFLECT
                || playerState == PLAYER_STATE_SHIELD_TOSS;
    }

    /**
     * Queue a buff/debuff to this player
     *
     * @param b New Buff
     */
    public void queueBuff(Buff b) {
        if (!isDead()) {
            buffQueue.add(b);
        }
    }

    private void updateJump() {
        if (dirKeydown[Globals.UP]) {
            setYSpeed(-14);
        }
    }

    private void updateFall() {
        if (ySpeed != 0) {
            updateY(ySpeed);
            queuePlayerState(PLAYER_STATE_JUMP);
        }

        setYSpeed(ySpeed + Globals.GRAVITY);
        if (ySpeed >= Globals.MAX_FALLSPEED) {
            setYSpeed(Globals.MAX_FALLSPEED);
        }
        if (ySpeed < 0) {
            isJumping = true;
        }
        isFalling = map.isFalling(x, y, ySpeed);
        if (!isFalling && ySpeed > 0) {
            y = map.getValidY(x, y, ySpeed);
            setYSpeed(0);
            isJumping = false;
        }
    }

    private void updateWalk(boolean moved) {
        if (dirKeydown[Globals.RIGHT] && !dirKeydown[Globals.LEFT]) {
            setXSpeed(4.5);
            if (moved) {
                if (ySpeed == 0) {
                    queuePlayerState(PLAYER_STATE_WALK);
                }
            }
        } else if (dirKeydown[Globals.LEFT] && !dirKeydown[Globals.RIGHT]) {
            setXSpeed(-4.5);
            if (moved) {
                if (ySpeed == 0) {
                    queuePlayerState(PLAYER_STATE_WALK);
                }
            }
        } else {
            setXSpeed(0);
        }
    }

    private void updateFacing() {
        if (dirKeydown[Globals.RIGHT] && !dirKeydown[Globals.LEFT]) {
            if (facing != Globals.RIGHT) {
                setFacing(Globals.RIGHT);
            }
        } else if (dirKeydown[Globals.LEFT] && !dirKeydown[Globals.RIGHT]) {
            if (facing != Globals.LEFT) {
                setFacing(Globals.LEFT);
            }
        }
    }

    /**
     * Queue skill to be use. Processed in the next tick.
     *
     * @param data
     */
    public void queueSkillUse(byte[] data) {
        lastActionTime = Globals.SERVER_MAX_IDLE;
        skillUseQueue.clear();
        if (!isDead()) {
            skillUseQueue.add(data);
        }
    }

    /**
     * Queue damage to be dealt. Processed in HP updated in the next tick.
     *
     * @param damage
     */
    public void queueDamage(Damage damage) {
        if (!isDead()) {
            damageQueue.add(damage);
        }
    }

    /**
     * Queue heal to be applied. Processed in HP updated in the next tick.
     *
     * @param heal
     */
    public void queueHeal(int heal) {
        if (!isDead()) {
            healQueue.add(heal);
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
    public void setFacing(byte f) {
        facing = f;
        updateFacing = true;
    }

    private boolean updateX(double change) {
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

    private boolean updateY(double change) {
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

    public void damageProc(Damage dmg) {
        if (hasSkill(Skill.PASSIVE_SHADOWATTACK) && skills.get(Skill.PASSIVE_SHADOWATTACK).canCast()) {
            if (Globals.rng(100) + 1 <= 20 + getSkillLevel(Skill.PASSIVE_SHADOWATTACK)) {
                skills.get(Skill.PASSIVE_SHADOWATTACK).setCooldown();
                sendCooldown(Skill.PASSIVE_SHADOWATTACK);
                sendParticle(logic.getRoom(), Globals.PARTICLE_PASSIVE_SHADOWATTACK, dmg.getDmgPoint().x, dmg.getDmgPoint().y);
                if (dmg.getTarget() != null) {
                    dmg.getTarget().queueDamage(new Damage((int) (dmg.getDamage() * 0.5D), false, dmg.getOwner(), dmg.getTarget(), false, dmg.getDmgPoint()));
                } else if (dmg.getBossTarget() != null) {
                    dmg.getBossTarget().queueDamage(new Damage((int) (dmg.getDamage() * 0.5D), false, dmg.getOwner(), dmg.getBossTarget(), false, dmg.getDmgPoint()));
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
    public void queuePlayerState(byte newState) {
        nextState = newState;
    }

    /**
     * Force set a player state.
     *
     * @param newState State to be set
     */
    public void setPlayerState(byte newState) {
        playerState = newState;
        frame = -1;
        nextFrameTime = 0;
        updateAnimState = true;
    }

    private void updateAnimState() {
        byte prevAnimState = animState, prevFrame = frame;
        switch (playerState) {
            case PLAYER_STATE_STAND:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_STAND;
                if (nextFrameTime <= 0) {
                    if (frame == 5) {
                        frame = 0;
                    } else {
                        frame++;
                    }
                    nextFrameTime = 150000000;
                }
                break;
            case PLAYER_STATE_DEAD:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_DEAD;
                if (nextFrameTime <= 0) {
                    if (frame == 14) {
                        frame = 0;
                    } else {
                        frame++;
                    }
                    nextFrameTime = 30000000;
                }
                break;
            case PLAYER_STATE_WALK:
                animState = Globals.PLAYER_STATE_WALK;
                nextFrameTime -= Globals.LOGIC_UPDATE;
                if (nextFrameTime <= 0) {
                    if (frame == 15) {
                        frame = 0;
                    } else {
                        frame++;
                    }
                    nextFrameTime = 40000000;
                }
                break;
            case PLAYER_STATE_JUMP:
                animState = Globals.PLAYER_STATE_JUMP;
                if (frame != 0) {
                    frame = 0;
                }
                break;
            case PLAYER_STATE_SWORD_SLASH:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                if (nextFrameTime <= 0) {
                    if (skillDuration < 200) {
                        animState = Globals.PLAYER_STATE_ATTACK;
                        if (frame < 11) {
                            frame++;
                        }
                    } else if (skillDuration < 400) {
                        animState = Globals.PLAYER_STATE_ATTACK;
                        if (frame > 0) {
                            frame--;
                        }
                    } else {
                        animState = Globals.PLAYER_STATE_ATTACK;
                        if (frame < 11) {
                            frame++;
                        }
                    }
                    nextFrameTime = 30000000;
                }
                if (skillDuration == 0 || skillDuration == 400) {
                    frame = 0;
                } else if (skillDuration == 200) {
                    frame = 11;
                }
                break;
            case PLAYER_STATE_SWORD_DRIVE:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACK;
                if (nextFrameTime <= 0 && frame < 11) {
                    frame++;
                    nextFrameTime = 40000000;
                }
                break;
            case PLAYER_STATE_SWORD_VORPAL:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACK;
                if (nextFrameTime <= 0 && skillDuration < 800 && frame < 11) {
                    frame++;
                    nextFrameTime = 40000000;
                }
                int skillTime = 170,
                 numHits = 3;
                if (skills.get(Skill.SWORD_VORPAL).getLevel() == 30) {
                    skillTime = 150;
                    numHits = 5;
                }
                if (skillDuration % skillTime == 0 && skillCounter <= numHits) {
                    frame = 0;
                }
                break;
            case PLAYER_STATE_SWORD_MULTI:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACK;
                if (nextFrameTime <= 0 && frame < 11) {
                    frame++;
                    nextFrameTime = 40000000;
                }
                break;
            case PLAYER_STATE_SWORD_CINDER:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACK;
                if (nextFrameTime <= 0 && frame < 11) {
                    frame++;
                    nextFrameTime = 40000000;
                }
                break;
            case PLAYER_STATE_SWORD_TAUNT:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACK;
                if (nextFrameTime <= 0 && frame < 11) {
                    frame++;
                    nextFrameTime = 40000000;
                }
                break;
            case PLAYER_STATE_BOW_ARC:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACKBOW;
                if (frame < 5 && nextFrameTime <= 0) {
                    frame++;
                    nextFrameTime = (frame < 5) ? 20000000 : 70000000;
                }
                break;
            case PLAYER_STATE_BOW_RAPID:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACKBOW;
                if (frame < 5 && nextFrameTime <= 0) {
                    frame++;
                    nextFrameTime = (frame < 5) ? 20000000 : 70000000;
                }
                if (skillDuration == 150 || skillDuration == 300 || skillDuration == 450) {
                    frame = 2;
                }
                break;
            case PLAYER_STATE_BOW_POWER:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACKBOW;
                if (nextFrameTime <= 0) {
                    if (skillDuration < 800) {
                        if (frame != 5) {
                            frame++;
                        }
                    }
                    nextFrameTime = (frame < 5) ? 20000000 : 70000000;
                }
                break;
            case PLAYER_STATE_BOW_VOLLEY:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACKBOW;
                if (frame != 5) {
                    frame = 5;
                }
                break;
            case PLAYER_STATE_BOW_STORM:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACKBOW;
                if (frame < 5 && nextFrameTime <= 0) {
                    frame++;
                    nextFrameTime = (frame < 5) ? 20000000 : 70000000;
                }
                break;
            case PLAYER_STATE_BOW_FROST:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACKBOW;
                if (frame < 5 && nextFrameTime <= 0) {
                    frame++;
                    nextFrameTime = (frame < 5) ? 20000000 : 70000000;
                }
                break;
            case PLAYER_STATE_SHIELD_DASH:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACK;
                if (nextFrameTime <= 0 && frame < 2) {
                    frame++;
                    nextFrameTime = 20000000;
                }
                break;
            case PLAYER_STATE_SHIELD_CHARGE:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACK;
                if (nextFrameTime <= 0 && frame < 11) {
                    frame++;
                    nextFrameTime = 20000000;
                }
                break;
            case PLAYER_STATE_SHIELD_FORTIFY:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_BUFF;
                if (nextFrameTime <= 0 && frame < 6) {
                    frame++;
                    nextFrameTime = 30000000;
                }
                break;
            case PLAYER_STATE_SHIELD_REFLECT:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_BUFF;
                if (nextFrameTime <= 0 && frame < 6) {
                    frame++;
                    nextFrameTime = 20000000;
                }
                break;
            case PLAYER_STATE_SHIELD_IRON:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_BUFF;
                if (nextFrameTime <= 0 && frame < 6) {
                    frame++;
                    nextFrameTime = 30000000;
                }
                break;
            case PLAYER_STATE_SHIELD_TOSS:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACK;
                if (nextFrameTime <= 0 && frame < 11) {
                    frame++;
                    nextFrameTime = 40000000;
                }
                break;
        }
        if (animState != prevAnimState || frame != prevFrame) {
            updateAnimState = true;
        }
    }

    public void sendData() {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 5 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_PLAYER_GET_ALL;
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
        bytes[10] = facing;
        bytes[11] = animState;
        bytes[12] = frame;

        sender.sendAll(bytes, logic.getRoom());
        updatePos = false;
        updateFacing = false;
        updateAnimState = false;
    }

    /**
     * Send the player's current position to every connected player
     * <p>
     * X and y are casted and sent as integer.
     * <br/>
     * Uses Server PacketSender to send to all<br/>
     * Byte sent: 0 - Data type 1 - Key 2,3,4,5 - x 6,7,8,9 - y
     * </p>
     */
    public void sendPos() {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_PLAYER_SET_POS;
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

    /**
     * Send the player's current facing direction to every connected player
     * <p>
     * Facing uses direction constants in Globals.<br/>
     * Uses Server PacketSender to send to all
     * <br/>Byte sent: 0 - Data type 1 - Key 2 - Facing direction
     * </p>
     */
    public void sendFacing() {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_SET_FACING;
        bytes[1] = key;
        bytes[2] = facing;
        sender.sendAll(bytes, logic.getRoom());
        updateFacing = false;
    }

    /**
     * Send the player's current state(for animation) and current frame of animation to every connected player
     * <p>
     * State constants are in Globals.<br/>
     * Uses Server PacketSender to send to all<br/>
     * Byte sent: 0 - Data type 1 - Key 2 - Player state 3 - Current frame
     * </p>
     */
    public void sendState() {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PLAYER_SET_STATE;
        bytes[1] = key;
        bytes[2] = animState;
        bytes[3] = frame;
        sender.sendAll(bytes, logic.getRoom());
        updateAnimState = false;
    }

    /**
     * Send set cooldown to player client.
     *
     * @param data
     */
    public void sendCooldown(byte[] data) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
        bytes[0] = Globals.DATA_PLAYER_SET_COOLDOWN;
        bytes[1] = data[3];
        sender.sendPlayer(bytes, address, port);
    }

    public void sendCooldown(byte skillCode) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
        bytes[0] = Globals.DATA_PLAYER_SET_COOLDOWN;
        bytes[1] = skillCode;
        sender.sendPlayer(bytes, address, port);
    }

    public void sendDamage(Damage dmg, int dmgDealt) {
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
        byte[] d = Globals.intToByte(dmgDealt);
        bytes[10] = d[0];
        bytes[11] = d[1];
        bytes[12] = d[2];
        bytes[13] = d[3];
        sender.sendAll(bytes, logic.getRoom());
    }

    /**
     * Send name to all clients.
     */
    public void sendName() {
        byte[] data = name.getBytes(StandardCharsets.UTF_8);
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + data.length];
        bytes[0] = Globals.DATA_PLAYER_GET_NAME;
        bytes[1] = key;
        System.arraycopy(data, 0, bytes, 2, data.length);
        sender.sendAll(bytes, logic.getRoom());
    }

    public void setInvulnerable(boolean set) {
        isInvulnerable = set;
    }

    public boolean isInvulnerable() {
        return isInvulnerable;
    }

    private void setRemovingDebuff(boolean set) {
        isRemoveDebuff = set;
    }

    public boolean isRemovingDebuff() {
        return isRemoveDebuff;
    }

    public boolean canDebuffAffect() {
        return !isInvulnerable() && !isRemovingDebuff();
    }

    /**
     * Set the uID of this player.
     *
     * @param id uID
     */
    public void setUniqueID(int id) {
        uniqueID = id;
    }

    /**
     * Get the uID of this player.
     *
     * @return
     */
    public int getUniqueID() {
        return uniqueID;
    }

    /**
     * Set the name of this player.
     *
     * @param s
     */
    public void setPlayerName(String s) {
        name = s;
    }

    /**
     * Get the name of this player.
     *
     * @return
     */
    public String getPlayerName() {
        return name;
    }

    /**
     * Set an amount of a specific stat.
     *
     * @param stat Stat Type
     * @param amount Amount of stats
     */
    public void setStat(byte stat, double amount) {
        stats[stat] = amount;
        updateStats();
    }

    /**
     * Get the stats of this player.
     *
     * @return double[] - Player Stats
     */
    public double[] getStats() {
        return stats;
    }

    /**
     * Set bonus stats of this player(CRITCHANCE, CRITDMG, REGEN, ARMOR).
     *
     * @param stat Stat Type
     * @param amount Amount of stats.
     */
    public void setBonusStat(byte stat, double amount) {
        bonusStats[stat] = amount;
        updateStats();
    }

    /**
     * Get the bonus stats of this player(CRITCHANCE, CRITDMG, REGEN, ARMOR).
     *
     * @return double[] - Player Bonus Stats
     */
    public double[] getBonusStats() {
        return bonusStats;
    }

    /**
     * Set the item code in a specific equipment slot
     *
     * @param slot Equipment Slot
     * @param itemCode Item Code
     */
    public void setEquip(int slot, int itemCode) {
        equip[slot] = itemCode;
    }

    /**
     * Disconnect a player in the next tick.
     */
    public void disconnect() {
        connected = false;
    }

    /**
     * Check if player is still connected
     *
     * @return True if connected
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Get the item type of an item code.
     *
     * @param i Item Code
     * @return Byte - Item Type
     */
    public static byte getItemType(int i) {
        if (i >= 100000 && i <= 109999) { //Swords
            return Globals.ITEM_WEAPON;
        } else if (i >= 110000 && i <= 119999) { //Shields
            return Globals.ITEM_SHIELD;
        } else if (i >= 120000 && i <= 129999) { //Bows
            return Globals.ITEM_BOW;
        } else if (i >= 130000 && i <= 199999) { //Quivers
            return Globals.ITEM_QUIVER;
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

    public static void sendParticle(byte room, byte particleID, double x, double y, byte facing) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_PARTICLE_EFFECT;
        bytes[1] = particleID;
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
        bytes[10] = facing;
        sender.sendAll(bytes, room);
    }

    public static void sendParticle(byte room, byte particleID, double x, double y) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_PARTICLE_EFFECT;
        bytes[1] = particleID;
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
        sender.sendAll(bytes, room);
    }

    public static void sendParticle(byte room, byte particleID, byte key) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PARTICLE_EFFECT;
        bytes[1] = particleID;
        bytes[2] = key;
        sender.sendAll(bytes, room);
    }

    public static void sendParticle(byte room, byte particleID, byte key, byte facing) {
        byte[] bytes = new byte[Globals.PACKET_BYTE * 4];
        bytes[0] = Globals.DATA_PARTICLE_EFFECT;
        bytes[1] = particleID;
        bytes[2] = facing;
        bytes[3] = key;
        sender.sendAll(bytes, room);
    }

}
