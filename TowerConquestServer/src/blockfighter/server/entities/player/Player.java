package blockfighter.server.entities.player;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.Buff;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.buff.BuffStun;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.player.skills.SkillBowArc;
import blockfighter.server.entities.player.skills.SkillBowFrost;
import blockfighter.server.entities.player.skills.SkillBowPower;
import blockfighter.server.entities.player.skills.SkillBowRapid;
import blockfighter.server.entities.player.skills.SkillBowStorm;
import blockfighter.server.entities.player.skills.SkillBowVolley;
import blockfighter.server.entities.player.skills.SkillPassive1;
import blockfighter.server.entities.player.skills.SkillPassive10;
import blockfighter.server.entities.player.skills.SkillPassive11;
import blockfighter.server.entities.player.skills.SkillPassive12;
import blockfighter.server.entities.player.skills.SkillPassive2;
import blockfighter.server.entities.player.skills.SkillPassive3;
import blockfighter.server.entities.player.skills.SkillPassive4;
import blockfighter.server.entities.player.skills.SkillPassive5;
import blockfighter.server.entities.player.skills.SkillPassive6;
import blockfighter.server.entities.player.skills.SkillPassive7;
import blockfighter.server.entities.player.skills.SkillPassive8;
import blockfighter.server.entities.player.skills.SkillPassive9;
import blockfighter.server.entities.player.skills.SkillShieldToss;
import blockfighter.server.entities.player.skills.SkillShieldDash;
import blockfighter.server.entities.player.skills.SkillShieldCharge;
import blockfighter.server.entities.player.skills.SkillShieldFortify;
import blockfighter.server.entities.player.skills.SkillShieldIron;
import blockfighter.server.entities.player.skills.SkillShieldReflect;
import blockfighter.server.entities.player.skills.SkillSwordCinder;
import blockfighter.server.entities.player.skills.SkillSwordDrive;
import blockfighter.server.entities.player.skills.SkillSwordMulti;
import blockfighter.server.entities.player.skills.SkillSwordSlash;
import blockfighter.server.entities.player.skills.SkillSwordTaunt;
import blockfighter.server.entities.player.skills.SkillSwordVorpal;
import blockfighter.server.entities.proj.ProjBowArc;
import blockfighter.server.entities.proj.ProjBowFrost;
import blockfighter.server.entities.proj.ProjBowPower;
import blockfighter.server.entities.proj.ProjBowRapid;
import blockfighter.server.entities.proj.ProjBowStorm;
import blockfighter.server.entities.proj.ProjBowVolley;
import blockfighter.server.entities.proj.ProjSwordCinder;
import blockfighter.server.entities.proj.ProjSwordDrive;
import blockfighter.server.entities.proj.ProjSwordMulti;
import blockfighter.server.entities.proj.ProjSwordSlash;
import blockfighter.server.entities.proj.ProjSwordTaunt;
import blockfighter.server.entities.proj.ProjSwordVorpal;
import blockfighter.server.maps.GameMap;
import blockfighter.server.net.PacketSender;
import java.awt.geom.Rectangle2D;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Player entities on the server.
 *
 * @author Ken Kwan
 */
public class Player extends Thread {

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
            PLAYER_STATE_SHIELD_TOSS = 0x14;

    private final byte key;
    private final LogicModule logic;
    private int uniqueID = -1;
    private String name = "";
    private double x, y, ySpeed, xSpeed;
    private boolean[] dirKeydown = new boolean[4];
    private boolean isFalling = false, isJumping = false;
    private boolean updatePos = false, updateFacing = false, updateAnimState = false;
    private byte playerState, animState, facing, frame;
    private double nextFrameTime = 0;
    private Rectangle2D.Double hitbox;

    private HashMap<Integer, Buff> buffs = new HashMap<>(500, 0.9f);
    private Buff isStun, isKnockback;

    private final InetAddress address;
    private final int port;
    private static PacketSender sender;
    private final GameMap map;
    private double[] stats = new double[Globals.NUM_STATS], bonusStats = new double[Globals.NUM_STATS];

    private int[] equip = new int[Globals.NUM_EQUIP_SLOTS];
    private ConcurrentHashMap<Byte, Skill> skills = new ConcurrentHashMap<>(Skill.NUM_SKILLS, 0.9f, 1);
    private boolean connected = true;
    private Random rng = new Random();

    private ConcurrentLinkedQueue<Integer> damageQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Integer> healQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Byte> stateQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<byte[]> skillUseQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Buff> buffQueue = new ConcurrentLinkedQueue<>();

    private ConcurrentLinkedQueue<Integer> buffKeys = new ConcurrentLinkedQueue<>();
    private int maxBuffKeys = 0;

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
        hitbox = new Rectangle2D.Double(x - 50, y - 180, 100, 180);
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
        for (int i = maxBuffKeys; i < maxBuffKeys + 500; i++) {
            buffKeys.add(i);
        }
        maxBuffKeys += 500;
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

    /**
     * Return this player's current state.
     * <p>
     * Used for updating animation state and player interactions. States are listed in Globals.
     * </p>
     *
     * @return The player's state in byte
     */
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

    /**
     * Get the skill level of specific skill using a skill code
     *
     * @param skillCode Skill code of skill
     * @return The level of the skill
     */
    public int getSkillLevel(byte skillCode) {
        return skills.get(skillCode).getLevel();
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
            case Skill.PASSIVE_1:
                newSkill = new SkillPassive1();
                break;
            case Skill.PASSIVE_2:
                newSkill = new SkillPassive2();
                break;
            case Skill.PASSIVE_3:
                newSkill = new SkillPassive3();
                break;
            case Skill.PASSIVE_4:
                newSkill = new SkillPassive4();
                break;
            case Skill.PASSIVE_5:
                newSkill = new SkillPassive5();
                break;
            case Skill.PASSIVE_6:
                newSkill = new SkillPassive6();
                break;
            case Skill.PASSIVE_7:
                newSkill = new SkillPassive7();
                break;
            case Skill.PASSIVE_8:
                newSkill = new SkillPassive8();
                break;
            case Skill.PASSIVE_9:
                newSkill = new SkillPassive9();
                break;
            case Skill.PASSIVE_10:
                newSkill = new SkillPassive10();
                break;
            case Skill.PASSIVE_11:
                newSkill = new SkillPassive11();
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
        update();
    }

    /**
     * Updates all logic of this player.
     * <p>
     * Must be called every tick. Specific logic updates are separated into other methods. Specific logic updates must be private.
     * </p>
     */
    public void update() {
        if (!isConnected()) {
            return;
        }
        lastActionTime -= Globals.LOGIC_UPDATE / 1000000;
        nextHPSend -= Globals.LOGIC_UPDATE / 1000000;
        if (isUsingSkill()) {
            skillDuration += Globals.LOGIC_UPDATE / 1000000;
        }

        updateSkillCd();
        updateSkillCast();

        updatePlayerState();
        updateBuffs();
        updateFall();

        if (isStunned() && !isKnockback()) {
            setXSpeed(0);
        }

        boolean movedX = updateX(xSpeed);
        hitbox.x = x - 50;
        hitbox.y = y - 180;

        if (isUsingSkill()) {
            updateSkillUse();
        }

        if (!isUsingSkill() && !isStunned() && !isKnockback()) {
            updateFacing();
            if (!isJumping && !isFalling) {
                updateWalk(movedX);
                updateJump();
            }
        }

        updateHP();
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

    private void updatePlayerState() {
        while (!stateQueue.isEmpty()) {
            Byte newState = stateQueue.poll();
            if (newState != null && !isUsingSkill() && playerState != newState) {
                setPlayerState(newState);
            }
            if (isUsingSkill()) {
                stateQueue.clear();
            }
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
        if (data != null && !isStunned() && !isKnockback()) {
            if (skills.containsKey(data[3])) {
                skillDuration = 0;
                skillCounter = 0;
                switch (data[3]) {
                    case Skill.SWORD_SLASH:
                        if (!skills.get(data[3]).canCast(getItemType(equip[Globals.ITEM_WEAPON]))) {
                            return;
                        }
                        queuePlayerState(PLAYER_STATE_SWORD_SLASH);
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 40000000;
                        break;
                    case Skill.SWORD_VORPAL:
                        if (!skills.get(data[3]).canCast(getItemType(equip[Globals.ITEM_WEAPON]))) {
                            return;
                        }
                        queuePlayerState(PLAYER_STATE_SWORD_VORPAL);
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 40000000;
                        break;
                    case Skill.SWORD_DRIVE:
                        if (!skills.get(data[3]).canCast(getItemType(equip[Globals.ITEM_WEAPON]))) {
                            return;
                        }
                        queuePlayerState(PLAYER_STATE_SWORD_DRIVE);
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 40000000;
                        break;
                    case Skill.SWORD_MULTI:
                        if (!skills.get(data[3]).canCast(getItemType(equip[Globals.ITEM_WEAPON]))) {
                            return;
                        }
                        queuePlayerState(PLAYER_STATE_SWORD_MULTI);
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 40000000;
                        break;
                    case Skill.SWORD_CINDER:
                        if (!skills.get(data[3]).canCast(getItemType(equip[Globals.ITEM_WEAPON]))) {
                            return;
                        }
                        queuePlayerState(PLAYER_STATE_SWORD_CINDER);
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 40000000;
                        break;
                    case Skill.SWORD_TAUNT:
                        if (!skills.get(data[3]).canCast(getItemType(equip[Globals.ITEM_WEAPON]))) {
                            return;
                        }
                        queuePlayerState(PLAYER_STATE_SWORD_TAUNT);
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 40000000;
                        break;
                    case Skill.BOW_ARC:
                        if (!skills.get(data[3]).canCast(getItemType(equip[Globals.ITEM_WEAPON]))) {
                            return;
                        }
                        queuePlayerState(PLAYER_STATE_BOW_ARC);
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 20000000;
                        break;
                    case Skill.BOW_POWER:
                        if (!skills.get(data[3]).canCast(getItemType(equip[Globals.ITEM_WEAPON]))) {
                            return;
                        }
                        queuePlayerState(PLAYER_STATE_BOW_POWER);
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 40000000;
                        break;
                    case Skill.BOW_RAPID:
                        if (!skills.get(data[3]).canCast(getItemType(equip[Globals.ITEM_WEAPON]))) {
                            return;
                        }
                        queuePlayerState(PLAYER_STATE_BOW_RAPID);
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 20000000;
                        break;
                    case Skill.BOW_VOLLEY:
                        if (!skills.get(data[3]).canCast(getItemType(equip[Globals.ITEM_WEAPON]))) {
                            return;
                        }
                        queuePlayerState(PLAYER_STATE_BOW_VOLLEY);
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 0;
                        break;
                    case Skill.BOW_STORM:
                        if (!skills.get(data[3]).canCast(getItemType(equip[Globals.ITEM_WEAPON]))) {
                            return;
                        }
                        queuePlayerState(PLAYER_STATE_BOW_STORM);
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 20000000;
                        break;
                    case Skill.BOW_FROST:
                        if (!skills.get(data[3]).canCast(getItemType(equip[Globals.ITEM_WEAPON]))) {
                            return;
                        }
                        queuePlayerState(PLAYER_STATE_BOW_FROST);
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 40000000;
                        break;
                    case Skill.SHIELD_CHARGE:
                        if (!skills.get(data[3]).canCast(getItemType(equip[Globals.ITEM_OFFHAND]))) {
                            return;
                        }
                        queuePlayerState(PLAYER_STATE_SHIELD_CHARGE);
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 20000000;
                        break;
                    case Skill.SHIELD_DASH:
                        if (!skills.get(data[3]).canCast(getItemType(equip[Globals.ITEM_OFFHAND]))) {
                            return;
                        }
                        queuePlayerState(PLAYER_STATE_SHIELD_DASH);
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 10000000;
                        break;
                    case Skill.SHIELD_FORTIFY:
                        if (!skills.get(data[3]).canCast(getItemType(equip[Globals.ITEM_OFFHAND]))) {
                            return;
                        }
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        break;
                    case Skill.SHIELD_IRON:
                        if (!skills.get(data[3]).canCast(getItemType(equip[Globals.ITEM_OFFHAND]))) {
                            return;
                        }
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        break;
                    case Skill.SHIELD_REFLECT:
                        if (!skills.get(data[3]).canCast(getItemType(equip[Globals.ITEM_OFFHAND]))) {
                            return;
                        }
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        break;
                    case Skill.SHIELD_TOSS:
                        if (!skills.get(data[3]).canCast(getItemType(equip[Globals.ITEM_OFFHAND]))) {
                            return;
                        }
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        break;
                }
            }
        }
    }

    private void updateSkillSwordSlash() {
        if (skillDuration % 200 == 0 && skillDuration < 600) {
            skillCounter++;
            ProjSwordSlash proj = new ProjSwordSlash(logic, logic.getNextProjKey(), this, x, y, skillCounter);
            logic.queueAddProj(proj);
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
            if (skillCounter == 1) {
                bytes[1] = Globals.PARTICLE_SWORD_SLASH1;
            } else if (skillCounter == 2) {
                bytes[1] = Globals.PARTICLE_SWORD_SLASH2;
            } else if (skillCounter == 3) {
                bytes[1] = Globals.PARTICLE_SWORD_SLASH3;
            }
            byte[] posXInt = Globals.intToByte((int) proj.getHitbox()[0].getX());
            bytes[2] = posXInt[0];
            bytes[3] = posXInt[1];
            bytes[4] = posXInt[2];
            bytes[5] = posXInt[3];
            byte[] posYInt = Globals.intToByte((int) proj.getHitbox()[0].getY());
            bytes[6] = posYInt[0];
            bytes[7] = posYInt[1];
            bytes[8] = posYInt[2];
            bytes[9] = posYInt[3];
            bytes[10] = facing;
            sender.sendAll(bytes, logic.getRoom());
        }

        if (skillDuration >= 700) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillSwordDrive() {
        if (skillDuration % 250 == 0 && skillDuration < 1000) {
            ProjSwordDrive proj = new ProjSwordDrive(logic, logic.getNextProjKey(), this, x, y);
            logic.queueAddProj(proj);
            if (skillDuration == 0) {
                byte[] bytes = new byte[Globals.PACKET_BYTE * 4 + Globals.PACKET_INT * 2];
                bytes[0] = Globals.DATA_PARTICLE_EFFECT;
                bytes[1] = Globals.PARTICLE_SWORD_DRIVE;
                bytes[10] = facing;
                bytes[11] = key;
                sender.sendAll(bytes, logic.getRoom());
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
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
            bytes[1] = Globals.PARTICLE_SWORD_VORPAL;
            byte[] posXInt = Globals.intToByte((int) proj.getHitbox()[0].getX());
            bytes[2] = posXInt[0];
            bytes[3] = posXInt[1];
            bytes[4] = posXInt[2];
            bytes[5] = posXInt[3];
            byte[] posYInt = Globals.intToByte((int) proj.getHitbox()[0].getY());
            bytes[6] = posYInt[0];
            bytes[7] = posYInt[1];
            bytes[8] = posYInt[2];
            bytes[9] = posYInt[3];
            bytes[10] = facing;
            sender.sendAll(bytes, logic.getRoom());
            skillCounter++;
        }

        if (skillDuration >= 800) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillSwordTaunt() {
        if (skillDuration == 0) {
            byte[] bytes = new byte[Globals.PACKET_BYTE * 4 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
            bytes[1] = Globals.PARTICLE_SWORD_TAUNTAURA1;
            bytes[10] = facing;
            bytes[11] = key;
            sender.sendAll(bytes, logic.getRoom());
        } else if (skillDuration == 50) {
            ProjSwordTaunt proj = new ProjSwordTaunt(logic, logic.getNextProjKey(), this, x, y);
            logic.queueAddProj(proj);
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
            bytes[1] = Globals.PARTICLE_SWORD_TAUNT;
            byte[] posXInt = Globals.intToByte((int) proj.getHitbox()[0].getX());
            bytes[2] = posXInt[0];
            bytes[3] = posXInt[1];
            bytes[4] = posXInt[2];
            bytes[5] = posXInt[3];
            byte[] posYInt = Globals.intToByte((int) proj.getHitbox()[0].getY());
            bytes[6] = posYInt[0];
            bytes[7] = posYInt[1];
            bytes[8] = posYInt[2];
            bytes[9] = posYInt[3];
            bytes[10] = facing;
            sender.sendAll(bytes, logic.getRoom());
        }
        if (skillDuration >= 500) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillSwordMulti() {
        int numHits = skills.get(Skill.SWORD_MULTI).getLevel() + 6;
        if (skillCounter == numHits) {
            skillCounter++;
        }
        if (skillDuration % 50 == 0 && skillCounter < numHits) {
            ProjSwordMulti proj = new ProjSwordMulti(logic, logic.getNextProjKey(), this, x, y + (rng.nextInt(40) - 20));
            logic.queueAddProj(proj);
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
            bytes[1] = Globals.PARTICLE_SWORD_MULTI;
            byte[] posXInt = Globals.intToByte((int) proj.getHitbox()[0].getX());
            bytes[2] = posXInt[0];
            bytes[3] = posXInt[1];
            bytes[4] = posXInt[2];
            bytes[5] = posXInt[3];
            byte[] posYInt = Globals.intToByte((int) proj.getHitbox()[0].getY());
            bytes[6] = posYInt[0];
            bytes[7] = posYInt[1];
            bytes[8] = posYInt[2];
            bytes[9] = posYInt[3];
            bytes[10] = facing;
            sender.sendAll(bytes, logic.getRoom());
            skillCounter++;
        }
        if (skillDuration >= numHits * 50 + 600 || isStunned() || isKnockback()) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillSwordCinder() {
        if (skillDuration == 70) {
            ProjSwordCinder proj = new ProjSwordCinder(logic, logic.getNextProjKey(), this, x, y);
            logic.queueAddProj(proj);
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
            bytes[1] = Globals.PARTICLE_SWORD_CINDER;
            byte[] posXInt = Globals.intToByte((int) proj.getHitbox()[0].getX());
            bytes[2] = posXInt[0];
            bytes[3] = posXInt[1];
            bytes[4] = posXInt[2];
            bytes[5] = posXInt[3];
            byte[] posYInt = Globals.intToByte((int) proj.getHitbox()[0].getY());
            bytes[6] = posYInt[0];
            bytes[7] = posYInt[1];
            bytes[8] = posYInt[2];
            bytes[9] = posYInt[3];
            bytes[10] = facing;
            sender.sendAll(bytes, logic.getRoom());
        }
        if (skillDuration >= 600) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillBowArc() {
        if (skillDuration == 100) {
            ProjBowArc proj = new ProjBowArc(logic, logic.getNextProjKey(), this, x, y);
            logic.queueAddProj(proj);
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
            bytes[1] = Globals.PARTICLE_BOW_ARC;
            byte[] posXInt = Globals.intToByte((int) proj.getHitbox()[0].getX());
            bytes[2] = posXInt[0];
            bytes[3] = posXInt[1];
            bytes[4] = posXInt[2];
            bytes[5] = posXInt[3];
            byte[] posYInt = Globals.intToByte((int) proj.getHitbox()[0].getY());
            bytes[6] = posYInt[0];
            bytes[7] = posYInt[1];
            bytes[8] = posYInt[2];
            bytes[9] = posYInt[3];
            bytes[10] = facing;
            sender.sendAll(bytes, logic.getRoom());
        }
        if (skillDuration >= 500) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillBowFrost() {
        if (skillDuration == 160) {
            ProjBowFrost proj = new ProjBowFrost(logic, logic.getNextProjKey(), this, x, y);
            logic.queueAddProj(proj);
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
            bytes[1] = Globals.PARTICLE_BOW_FROSTARROW;
            byte[] posXInt = Globals.intToByte((int) proj.getHitbox()[0].getX());
            bytes[2] = posXInt[0];
            bytes[3] = posXInt[1];
            bytes[4] = posXInt[2];
            bytes[5] = posXInt[3];
            byte[] posYInt = Globals.intToByte((int) proj.getHitbox()[0].getY());
            bytes[6] = posYInt[0];
            bytes[7] = posYInt[1];
            bytes[8] = posYInt[2];
            bytes[9] = posYInt[3];
            bytes[10] = facing;
            sender.sendAll(bytes, logic.getRoom());
        }
        if (skillDuration >= 500) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillBowStorm() {
        if (skillDuration == 100) {
            ProjBowStorm proj = new ProjBowStorm(logic, logic.getNextProjKey(), this, x, y);
            logic.queueAddProj(proj);
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
            bytes[1] = Globals.PARTICLE_BOW_STORM;
            byte[] posXInt = Globals.intToByte((int) proj.getHitbox()[0].getX());
            bytes[2] = posXInt[0];
            bytes[3] = posXInt[1];
            bytes[4] = posXInt[2];
            bytes[5] = posXInt[3];
            byte[] posYInt = Globals.intToByte((int) proj.getHitbox()[0].getY());
            bytes[6] = posYInt[0];
            bytes[7] = posYInt[1];
            bytes[8] = posYInt[2];
            bytes[9] = posYInt[3];
            bytes[10] = facing;
            sender.sendAll(bytes, logic.getRoom());
        }
        if (skillDuration >= 500) {
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
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
            bytes[1] = Globals.PARTICLE_BOW_RAPID;
            byte[] posXInt = Globals.intToByte((int) proj.getHitbox()[0].getX());
            bytes[2] = posXInt[0];
            bytes[3] = posXInt[1];
            bytes[4] = posXInt[2];
            bytes[5] = posXInt[3];
            byte[] posYInt = Globals.intToByte((int) proj.getHitbox()[0].getY());
            bytes[6] = posYInt[0];
            bytes[7] = posYInt[1];
            bytes[8] = posYInt[2];
            bytes[9] = posYInt[3];
            bytes[10] = facing;
            sender.sendAll(bytes, logic.getRoom());
        }
        if (skillDuration >= 800) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillBowVolley() {
        if (skillDuration % 100 == 0 && skillCounter < 20) {
            ProjBowVolley proj = new ProjBowVolley(logic, logic.getNextProjKey(), this, x, y - 10 + rng.nextInt(40));
            logic.queueAddProj(proj);
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
            bytes[1] = Globals.PARTICLE_BOW_VOLLEYARROW;
            byte[] posXInt = Globals.intToByte((int) proj.getHitbox()[0].getX());
            bytes[2] = posXInt[0];
            bytes[3] = posXInt[1];
            bytes[4] = posXInt[2];
            bytes[5] = posXInt[3];
            byte[] posYInt = Globals.intToByte((int) proj.getHitbox()[0].getY());
            bytes[6] = posYInt[0];
            bytes[7] = posYInt[1];
            bytes[8] = posYInt[2];
            bytes[9] = posYInt[3];
            bytes[10] = facing;
            sender.sendAll(bytes, logic.getRoom());

            bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
            bytes[1] = Globals.PARTICLE_BOW_VOLLEYBOW;
            posXInt = Globals.intToByte((int) x);
            bytes[2] = posXInt[0];
            bytes[3] = posXInt[1];
            bytes[4] = posXInt[2];
            bytes[5] = posXInt[3];
            posYInt = Globals.intToByte((int) y);
            bytes[6] = posYInt[0];
            bytes[7] = posYInt[1];
            bytes[8] = posYInt[2];
            bytes[9] = posYInt[3];
            bytes[10] = facing;
            sender.sendAll(bytes, logic.getRoom());
            skillCounter++;
        }
        if (skillDuration >= 2100 || isStunned() || isKnockback()) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillBowPower() {
        if (skillDuration <= 400 && skillDuration % 50 == 0) {
            //Charging particles
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
            bytes[1] = Globals.PARTICLE_BOW_POWERCHARGE;
            byte[] posXInt = Globals.intToByte((int) x + ((facing == Globals.RIGHT) ? 75 : -75));
            bytes[2] = posXInt[0];
            bytes[3] = posXInt[1];
            bytes[4] = posXInt[2];
            bytes[5] = posXInt[3];
            byte[] posYInt = Globals.intToByte((int) y - 250);
            bytes[6] = posYInt[0];
            bytes[7] = posYInt[1];
            bytes[8] = posYInt[2];
            bytes[9] = posYInt[3];
            bytes[10] = facing;
            sender.sendAll(bytes, logic.getRoom());
        } else if (skillDuration == 800) {
            ProjBowPower proj = new ProjBowPower(logic, logic.getNextProjKey(), this, x, y);
            logic.queueAddProj(proj);
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
            bytes[1] = Globals.PARTICLE_BOW_POWER;
            byte[] posXInt = Globals.intToByte((int) proj.getHitbox()[0].getX());
            bytes[2] = posXInt[0];
            bytes[3] = posXInt[1];
            bytes[4] = posXInt[2];
            bytes[5] = posXInt[3];
            byte[] posYInt = Globals.intToByte((int) proj.getHitbox()[0].getY());
            bytes[6] = posYInt[0];
            bytes[7] = posYInt[1];
            bytes[8] = posYInt[2];
            bytes[9] = posYInt[3];
            bytes[10] = facing;
            sender.sendAll(bytes, logic.getRoom());
        }
        if (skillDuration >= 1400 || (skillDuration < 800 && (isStunned() || isKnockback()))) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillShieldCharge() {
        setXSpeed((facing == Globals.RIGHT) ? 13 : -13);
        if (skillDuration >= 750) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillShieldDash() {
        if (!isStunned() && !isKnockback()) {
            setXSpeed((facing == Globals.RIGHT) ? 15 : -15);
        }
        if (skillDuration % 20 == 0) {
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
            bytes[1] = Globals.PARTICLE_SHIELD_DASH;
            byte[] posXInt = Globals.intToByte((int) (x + ((facing == Globals.RIGHT) ? -172 : -200)));
            bytes[2] = posXInt[0];
            bytes[3] = posXInt[1];
            bytes[4] = posXInt[2];
            bytes[5] = posXInt[3];
            byte[] posYInt = Globals.intToByte((int) (y - 330));
            bytes[6] = posYInt[0];
            bytes[7] = posYInt[1];
            bytes[8] = posYInt[2];
            bytes[9] = posYInt[3];
            bytes[10] = facing;
            sender.sendAll(bytes, logic.getRoom());
        }
        if (skillDuration == 0) {

            setYSpeed(-4);
        }

        if (skillDuration >= 250 || isStunned() || isKnockback()) {
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
        }
    }

    private void updateSkillCd() {
        for (Map.Entry<Byte, Skill> s : skills.entrySet()) {
            s.getValue().reduceCooldown((long) (Globals.LOGIC_UPDATE / 1000000));
        }
    }

    private void updateHP() {
        //Empty damage queued
        while (!damageQueue.isEmpty()) {
            Integer dmg = damageQueue.poll();
            if (dmg != null) {
                dmg = (int) (dmg * stats[Globals.STAT_DAMAGEREDUCT]);
                stats[Globals.STAT_MINHP] -= dmg;
                nextHPSend = 0;
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

        //Update client hp every 150ms or if damaged/healed(excluding regen).
        if (nextHPSend <= 0) {
            byte[] stat = Globals.intToByte((int) stats[Globals.STAT_MINHP]);
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
            bytes[0] = Globals.DATA_PLAYER_GET_STAT;
            bytes[1] = key;
            bytes[2] = Globals.STAT_MINHP;
            System.arraycopy(stat, 0, bytes, 3, stat.length);
            sender.sendAll(bytes, logic.getRoom());
            nextHPSend = 150;
        }
    }

    private void updateBuffs() {
        //Update exisiting buffs
        isStun = null;
        isKnockback = null;
        LinkedList<Integer> remove = new LinkedList<>();
        for (Map.Entry<Integer, Buff> bEntry : buffs.entrySet()) {
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
        for (Integer bKey : remove) {
            buffs.remove(bKey);
            returnBuffKey(bKey);
        }

        //Empty and add buffs from queue
        while (!buffQueue.isEmpty()) {
            Buff b = buffQueue.poll();
            Integer bKey = getNextBuffKey();
            if (bKey != null && b != null) {
                buffs.put(bKey, b);
            }
        }
    }

    /**
     * Roll a damage number between the max and min damage of player.
     *
     * @return Randomly rolled damage.
     */
    public double rollDamage() {
        double dmg = rng.nextInt((int) (stats[Globals.STAT_MAXDMG] - stats[Globals.STAT_MINDMG])) + stats[Globals.STAT_MINDMG];
        return dmg;
    }

    /**
     * Roll a chance to do critical hit.
     *
     * @return True if rolls a critical chance.
     */
    public boolean rollCrit() {
        return rng.nextInt(10000) + 1 < (int) (stats[Globals.STAT_CRITCHANCE] * 10000);
    }

    /**
     * Roll a chance to do critical hit with addition critical chance(from skills)
     *
     * @param bonusCritChance Bonus chance % in decimal(40% = 0.4).
     * @return True if rolls a critical chance.
     */
    public boolean rollCrit(double bonusCritChance) {
        return rng.nextInt(10000) + 1 < (int) ((stats[Globals.STAT_CRITCHANCE] + bonusCritChance) * 10000);
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

    /**
     * Check if a rectangle intersects with this player's hitbox
     *
     * @param box Box to be checked
     * @return True if the boxes intersect
     */
    public boolean intersectHitbox(Rectangle2D.Double box) {
        return hitbox.intersects(box);
    }

    /**
     * Return if player is stunned
     *
     * @return isStun
     */
    public synchronized boolean isStunned() {
        return isStun != null;
    }

    /**
     * Return if player is being knocked back.
     *
     * @return isKnockback
     */
    public synchronized boolean isKnockback() {
        return isKnockback != null;
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
        buffQueue.add(b);
    }

    private void updateJump() {
        if (dirKeydown[Globals.UP]) {
            isJumping = true;
            setYSpeed(-12.5);
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

        isFalling = map.isFalling(x, y, ySpeed);
        if (!isFalling && ySpeed > 0) {
            y = map.getValidY(x, y, ySpeed);
            setYSpeed(0);
            isJumping = false;
            queuePlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateWalk(boolean moved) {
        if (dirKeydown[Globals.RIGHT] && !dirKeydown[Globals.LEFT]) {
            setXSpeed(4.5);
            if (moved) {
                if (ySpeed == 0) {
                    queuePlayerState(PLAYER_STATE_WALK);
                }
            } else {
                if (ySpeed == 0) {
                    queuePlayerState(PLAYER_STATE_STAND);
                }
            }
        } else if (dirKeydown[Globals.LEFT] && !dirKeydown[Globals.RIGHT]) {
            setXSpeed(-4.5);
            if (moved) {
                if (ySpeed == 0) {
                    queuePlayerState(PLAYER_STATE_WALK);
                }
            } else {
                if (ySpeed == 0) {
                    queuePlayerState(PLAYER_STATE_STAND);
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
        skillUseQueue.add(data);
    }

    /**
     * Queue damage to be dealt. Processed in HP updated in the next tick.
     *
     * @param damage
     */
    public void queueDamage(int damage) {
        damageQueue.add(damage);
    }

    /**
     * Queue heal to be applied. Processed in HP updated in the next tick.
     *
     * @param heal
     */
    public void queueHeal(int heal) {
        healQueue.add(heal);
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

    /**
     * Queue player state to be set.
     * <p>
     * States constants in Globals
     * </p>
     *
     * @param newState New state the player is in
     */
    public void queuePlayerState(byte newState) {
        stateQueue.clear();
        stateQueue.add(newState);
    }

    /**
     * Force set a player state.
     *
     * @param newState State to be set
     */
    public void setPlayerState(byte newState) {
        playerState = newState;
        frame = 0;
        updateAnimState = true;
    }

    private void updateAnimState() {
        byte prevAnimState = animState, prevFrame = frame;
        switch (playerState) {
            case PLAYER_STATE_STAND:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_STAND;
                if (nextFrameTime <= 0) {
                    if (frame >= 8) {
                        frame = 0;
                    } else {
                        frame++;
                    }
                    nextFrameTime = 150000000;
                }
                break;
            case PLAYER_STATE_WALK:
                animState = Globals.PLAYER_STATE_WALK;
                nextFrameTime -= Globals.LOGIC_UPDATE;
                if (nextFrameTime <= 0) {
                    if (frame == 18) {
                        frame = 0;
                    } else {
                        frame++;
                    }
                    nextFrameTime = 33000000 * .75;
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
                        animState = Globals.PLAYER_STATE_ATTACK1;
                        if (frame < 4) {
                            frame++;
                        }
                    } else if (skillDuration < 400) {
                        animState = Globals.PLAYER_STATE_ATTACK1;
                        if (frame > 0) {
                            frame--;
                        }
                    } else {
                        animState = Globals.PLAYER_STATE_ATTACK2;
                        if (frame < 4) {
                            frame++;
                        }
                    }
                    nextFrameTime = 40000000;
                }
                if (skillDuration == 0 || skillDuration == 400) {
                    frame = 0;
                } else if (skillDuration == 200) {
                    frame = 4;
                }
                break;
            case PLAYER_STATE_SWORD_DRIVE:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACK2;
                if (nextFrameTime <= 0 && frame < 4) {
                    frame++;
                    nextFrameTime = 40000000;
                }
                break;
            case PLAYER_STATE_SWORD_VORPAL:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACK2;
                if (nextFrameTime <= 0) {
                    if (skillDuration < 800) {
                        if (frame < 4) {
                            frame++;
                        }
                    }
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
                animState = Globals.PLAYER_STATE_ATTACK1;
                if (nextFrameTime <= 0 && frame < 4) {
                    frame++;
                    nextFrameTime = 40000000;
                }
                break;
            case PLAYER_STATE_SWORD_CINDER:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACK1;
                if (nextFrameTime <= 0 && frame < 4) {
                    frame++;
                    nextFrameTime = 40000000;
                }
                break;
            case PLAYER_STATE_SWORD_TAUNT:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACK2;
                if (nextFrameTime <= 0 && frame < 4) {
                    frame++;
                    nextFrameTime = 40000000;
                }
                break;
            case PLAYER_STATE_BOW_ARC:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACKBOW;
                if (frame < 4 && nextFrameTime <= 0) {
                    frame++;
                    nextFrameTime = 20000000;
                }
                break;
            case PLAYER_STATE_BOW_RAPID:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACKBOW;
                if (frame < 4 && nextFrameTime <= 0) {
                    frame++;
                    nextFrameTime = 30000000;
                }
                if (skillDuration == 150 || skillDuration == 300 || skillDuration == 450) {
                    frame = 0;
                }
                break;
            case PLAYER_STATE_BOW_POWER:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACKBOW;
                if (nextFrameTime <= 0) {
                    if (skillDuration < 800) {
                        if (frame != 3) {
                            frame++;
                        }
                        nextFrameTime = 40000000;
                    } else {
                        if (frame != 4) {
                            frame++;
                        }
                        nextFrameTime = 40000000;
                    }
                }
                break;
            case PLAYER_STATE_BOW_VOLLEY:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACKBOW;
                if (frame != 4) {
                    frame = 4;
                }
                break;
            case PLAYER_STATE_BOW_STORM:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACKBOW;
                if (frame < 4 && nextFrameTime <= 0) {
                    if (frame < 4) {
                        frame++;
                        nextFrameTime = 20000000;
                    }
                }
                break;
            case PLAYER_STATE_BOW_FROST:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACKBOW;
                if (frame < 4 && nextFrameTime <= 0) {
                    frame++;
                    nextFrameTime = 40000000;

                }
                break;
            case PLAYER_STATE_SHIELD_DASH:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACKOFF2;
                if (nextFrameTime <= 0 && frame < 2) {
                    frame++;
                    nextFrameTime = 10000000;
                }
                break;
            case PLAYER_STATE_SHIELD_CHARGE:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACKOFF2;
                if (nextFrameTime <= 0 && frame < 4) {
                    frame++;
                    nextFrameTime = 20000000;
                }
                break;
        }
        if (animState != prevAnimState || frame != prevFrame) {
            updateAnimState = true;
        }
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
        skills.get(data[3]).setCooldown();
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
        bytes[0] = Globals.DATA_PLAYER_SET_COOLDOWN;
        bytes[1] = data[3];
        sender.sendPlayer(bytes, address, port);
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
}
