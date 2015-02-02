package blockfighter.server.entities.player;

import blockfighter.server.maps.GameMap;
import blockfighter.server.net.PacketSender;
import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.Buff;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.buff.BuffStun;
import blockfighter.server.entities.player.skills.*;
import blockfighter.server.entities.proj.ProjSwordDrive;
import blockfighter.server.entities.proj.ProjSwordSlash;

import java.awt.geom.Rectangle2D;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
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
            PLAYER_STATE_BOW_VOLLEY = 0x0E;

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

    private ConcurrentHashMap<Byte, Buff> buffs = new ConcurrentHashMap<>(10, 0.9f, 1);
    private Buff isStun, isKnockback;

    private final InetAddress address;
    private final int port;
    private final PacketSender packetSender;
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

    private ConcurrentLinkedQueue<Byte> buffKeys = new ConcurrentLinkedQueue<>();

    private long lastActionTime = Globals.SERVER_MAX_IDLE;
    private long skillDuration = 0;
    private long nextHPSend = 0;

    /**
     * Create a new player entity in the server.
     *
     * @param key The key of this player in the player array in logic module
     * @param address IP address of player
     * @param port Connected port
     * @param x Spawning x location in double
     * @param y Spawning y location in double
     * @param bc Reference to Server PacketSender
     * @param map Reference to server's loaded map
     * @param l Reference to Logic module
     */
    public Player(PacketSender bc, LogicModule l, byte key, InetAddress address, int port, GameMap map, double x, double y) {
        packetSender = bc;
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
        for (byte i = -128; i < 127; i++) {
            buffKeys.add(i);
        }
    }

    public void returnBuffKey(byte bKey) {
        buffKeys.add(bKey);
    }

    public Byte getNextBuffKey() {
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

    public int[] getEquip() {
        return equip;
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
            case Skill.SHIELD_IRONFORT:
                newSkill = new SkillShieldIron();
                break;
            case Skill.SHIELD_3:
                newSkill = new SkillShield3();
                break;
            case Skill.SHIELD_4:
                newSkill = new SkillShield4();
                break;
            case Skill.SHIELD_5:
                newSkill = new SkillShield5();
                break;
            case Skill.SHIELD_6:
                newSkill = new SkillShield6();
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
            if (skills.containsKey(data[3]) && skills.get(data[3]).getCooldown() <= 0) {
                skillDuration = 0;
                setXSpeed(0);
                switch (data[3]) {
                    case Skill.SWORD_SLASH:
                        queuePlayerState(PLAYER_STATE_SWORD_SLASH);
                        animState = Globals.PLAYER_STATE_ATTACK1;
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 40000000;
                        break;
                    case Skill.BOW_ARC:
                        queuePlayerState(PLAYER_STATE_BOW_ARC);
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 20000000;
                        break;
                    case Skill.BOW_POWER:
                        queuePlayerState(PLAYER_STATE_BOW_POWER);
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 40000000;
                        break;
                    case Skill.SWORD_VORPAL:
                        queuePlayerState(PLAYER_STATE_SWORD_VORPAL);
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 40000000;
                        break;
                    case Skill.SWORD_DRIVE:
                        queuePlayerState(PLAYER_STATE_SWORD_DRIVE);
                        skills.get(data[3]).setCooldown();
                        sendCooldown(data);
                        nextFrameTime = 40000000;
                        break;
                }
            }
        }
    }

    private void updateSkillSwordSlash() {
        if (skillDuration == 0) {
            ProjSwordSlash proj = new ProjSwordSlash(packetSender, logic, logic.getNextProjKey(), this, x, y, 1);
            logic.queueAddProj(proj);
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
            bytes[1] = Globals.PARTICLE_SWORD_SLASH1;
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
            packetSender.sendAll(bytes, logic.getRoom());
        } else if (skillDuration == 200) {
            ProjSwordSlash proj = new ProjSwordSlash(packetSender, logic, logic.getNextProjKey(), this, x, y, 2);
            logic.queueAddProj(proj);
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
            bytes[1] = Globals.PARTICLE_SWORD_SLASH2;
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
            packetSender.sendAll(bytes, logic.getRoom());
        } else if (skillDuration == 400) {
            ProjSwordSlash proj = new ProjSwordSlash(packetSender, logic, logic.getNextProjKey(), this, x, y, 3);
            logic.queueAddProj(proj);
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PARTICLE_EFFECT;
            bytes[1] = Globals.PARTICLE_SWORD_SLASH3;
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
            packetSender.sendAll(bytes, logic.getRoom());
        }

        if (skillDuration >= 900) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillSwordDrive() {
        if (skillDuration == 0 || skillDuration == 250 || skillDuration == 500 || skillDuration == 750) {
            ProjSwordDrive proj = new ProjSwordDrive(packetSender, logic, logic.getNextProjKey(), this, x, y);
            logic.queueAddProj(proj);
            if (skillDuration == 0) {
                byte[] bytes = new byte[Globals.PACKET_BYTE * 4 + Globals.PACKET_INT * 2];
                bytes[0] = Globals.DATA_PARTICLE_EFFECT;
                bytes[1] = Globals.PARTICLE_SWORD_DRIVE;
                bytes[10] = facing;
                bytes[11] = key;
                packetSender.sendAll(bytes, logic.getRoom());
            }
        }
        if (skillDuration >= 1000) {
            setPlayerState(PLAYER_STATE_STAND);
        }
    }

    private void updateSkillUse() {
        switch (playerState) {
            case PLAYER_STATE_SWORD_SLASH:
                updateSkillSwordSlash();
                break;
            case PLAYER_STATE_SWORD_DRIVE:
                updateSkillSwordDrive();
                break;
            case PLAYER_STATE_SWORD_VORPAL:
                if (skillDuration >= 900) {
                    setPlayerState(PLAYER_STATE_STAND);
                }
                break;
            case PLAYER_STATE_BOW_ARC:
                if (skillDuration >= 500) {
                    setPlayerState(PLAYER_STATE_STAND);
                }
                break;
            case PLAYER_STATE_BOW_POWER:
                if (skillDuration >= 1000 || isStunned() || isKnockback()) {
                    setPlayerState(PLAYER_STATE_STAND);
                }
                break;
            case PLAYER_STATE_BOW_RAPID:
                if (skillDuration >= 1000) {
                    setPlayerState(PLAYER_STATE_STAND);
                }
                break;
        }
    }

    private void updateSkillCd() {
        for (Map.Entry<Byte, Skill> s : skills.entrySet()) {
            s.getValue().reduceCooldown((long) (Globals.LOGIC_UPDATE / 1000000));
        }
    }

    private void updateHP() {
        while (!damageQueue.isEmpty()) {
            Integer dmg = damageQueue.poll();
            if (dmg != null) {
                stats[Globals.STAT_MINHP] -= dmg;
                nextHPSend = 0;
            }
        }

        while (!healQueue.isEmpty()) {
            Integer heal = healQueue.poll();
            if (heal != null) {
                stats[Globals.STAT_MINHP] += heal;
                nextHPSend = 0;
            }
        }

        stats[Globals.STAT_MINHP] += stats[Globals.STAT_REGEN] / 100D;
        if (stats[Globals.STAT_MINHP] > stats[Globals.STAT_MAXHP]) {
            stats[Globals.STAT_MINHP] = stats[Globals.STAT_MAXHP];
        } else if (stats[Globals.STAT_MINHP] < 0) {
            stats[Globals.STAT_MINHP] = 0;
        }

        if (nextHPSend <= 0) {
            byte[] stat = Globals.intToByte((int) stats[Globals.STAT_MINHP]);
            byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
            bytes[0] = Globals.DATA_PLAYER_GET_STAT;
            bytes[1] = key;
            bytes[2] = Globals.STAT_MINHP;
            System.arraycopy(stat, 0, bytes, 3, stat.length);
            packetSender.sendAll(bytes, logic.getRoom());
            nextHPSend = 100;
        }
    }

    private void updateBuffs() {
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
    }

    public double rollDamage() {
        double dmg = rng.nextInt((int) (stats[Globals.STAT_MAXDMG] - stats[Globals.STAT_MINDMG])) + stats[Globals.STAT_MINDMG];
        if (rollCrit()) {
            dmg = dmg * (1 + stats[Globals.STAT_CRITDMG]);
        }
        return dmg;
    }

    public boolean rollCrit() {
        return rng.nextInt(10000) + 1 < (int) (stats[Globals.STAT_CRITCHANCE] * 10000);
    }

    private void updateStats() {
        stats[Globals.STAT_ARMOR] = Globals.calcArmor((int) (stats[Globals.STAT_DEFENSE] + bonusStats[Globals.STAT_DEFENSE]));
        stats[Globals.STAT_REGEN] = Globals.calcRegen((int) (stats[Globals.STAT_SPIRIT] + bonusStats[Globals.STAT_SPIRIT]));
        stats[Globals.STAT_MAXHP] = Globals.calcMaxHP((int) (stats[Globals.STAT_DEFENSE] + bonusStats[Globals.STAT_DEFENSE]));
        stats[Globals.STAT_MINHP] = 0;
        stats[Globals.STAT_MINDMG] = Globals.calcMinDmg((int) (stats[Globals.STAT_POWER] + bonusStats[Globals.STAT_POWER]));
        stats[Globals.STAT_MAXDMG] = Globals.calcMaxDmg((int) (stats[Globals.STAT_POWER] + bonusStats[Globals.STAT_POWER]));
        stats[Globals.STAT_CRITCHANCE] = Globals.calcCritChance((int) (stats[Globals.STAT_SPIRIT] + bonusStats[Globals.STAT_SPIRIT]));
        stats[Globals.STAT_CRITDMG] = Globals.calcCritDmg((int) (stats[Globals.STAT_SPIRIT] + bonusStats[Globals.STAT_SPIRIT]));

        stats[Globals.STAT_CRITCHANCE] = stats[Globals.STAT_CRITCHANCE] + bonusStats[Globals.STAT_CRITCHANCE];
        stats[Globals.STAT_CRITDMG] = stats[Globals.STAT_CRITDMG] + bonusStats[Globals.STAT_CRITDMG];
        stats[Globals.STAT_REGEN] = stats[Globals.STAT_REGEN] + bonusStats[Globals.STAT_REGEN];
        stats[Globals.STAT_ARMOR] = stats[Globals.STAT_ARMOR] + bonusStats[Globals.STAT_ARMOR];
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

    public boolean isUsingSkill() {
        return playerState == PLAYER_STATE_SWORD_SLASH
                || playerState == PLAYER_STATE_SWORD_VORPAL
                || playerState == PLAYER_STATE_SWORD_DRIVE
                || playerState == PLAYER_STATE_BOW_ARC
                || playerState == PLAYER_STATE_BOW_POWER;
    }

    /**
     * Add a buff/debuff to this player
     *
     * @param b New Buff
     */
    public void addBuff(Buff b) {
        Byte bKey = getNextBuffKey();
        if (bKey != null) {
            buffs.put(bKey, b);
        }
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

    public void processUseSkill(byte[] data) {
        lastActionTime = Globals.SERVER_MAX_IDLE;
        skillUseQueue.add(data);
    }

    public void queueDamage(int damage) {
        damageQueue.add(damage);
    }

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
                if (nextFrameTime <= 0) {
                    if (frame < 4) {
                        frame++;
                        nextFrameTime = 40000000;
                    }
                }
                break;
            case PLAYER_STATE_BOW_ARC:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACKBOW;
                if (nextFrameTime <= 0) {
                    if (frame < 4) {
                        frame++;
                        nextFrameTime = 20000000;
                    }
                }
                break;
            case PLAYER_STATE_BOW_POWER:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACKBOW;
                if (nextFrameTime <= 0) {
                    if (skillDuration < 800) {
                        if (frame != 2) {
                            frame++;
                        }
                        nextFrameTime = 40000000;
                    } else {
                        if (frame != 4) {
                            frame++;
                        }
                        nextFrameTime = 20000000;
                    }
                }
                break;
            case PLAYER_STATE_SWORD_VORPAL:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = Globals.PLAYER_STATE_ATTACK2;
                if (nextFrameTime <= 0) {
                    if (skillDuration < 200) {
                        if (frame < 4) {
                            frame++;
                        }
                    } else if (skillDuration < 400) {
                        if (frame < 4) {
                            frame++;
                        }
                    } else if (skillDuration < 900) {
                        if (frame < 4) {
                            frame++;
                        }
                    }
                    nextFrameTime = 40000000;
                }
                if (skillDuration == 200 || skillDuration == 400) {
                    frame = 0;
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
        packetSender.sendAll(bytes, logic.getRoom());
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
        packetSender.sendAll(bytes, logic.getRoom());
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
        packetSender.sendAll(bytes, logic.getRoom());
        updateAnimState = false;
    }

    public void sendCooldown(byte[] data) {
        skills.get(data[3]).setCooldown();
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
        bytes[0] = Globals.DATA_PLAYER_SET_COOLDOWN;
        bytes[1] = data[3];
        packetSender.sendPlayer(bytes, address, port);
    }

    public void sendName() {
        byte[] data = name.getBytes(StandardCharsets.UTF_8);
        byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + data.length];
        bytes[0] = Globals.DATA_PLAYER_GET_NAME;
        bytes[1] = key;
        System.arraycopy(data, 0, bytes, 2, data.length);
        packetSender.sendAll(bytes, logic.getRoom());
    }

    public void setUniqueID(int id) {
        uniqueID = id;
    }

    public int getUniqueID() {
        return uniqueID;
    }

    public void setPlayerName(String s) {
        name = s;
    }

    public String getPlayerName() {
        return name;
    }

    public void setStat(byte stat, double amount) {
        stats[stat] = amount;
        updateStats();
    }

    public double[] getStats() {
        return stats;
    }

    public void setBonusStat(byte stat, double amount) {
        bonusStats[stat] = amount;
        updateStats();
    }

    public double[] getBonusStats() {
        return bonusStats;
    }

    public void setEquip(int slot, int itemCode) {
        equip[slot] = itemCode;
    }

    public void disconnect() {
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

    public static byte getItemType(int i) {
        if (i >= 100000 && i <= 109999) { //Swords
            return Globals.ITEM_WEAPON;
        } else if (i >= 110000 && i <= 119999) { //Shields
            return Globals.ITEM_OFFHAND;
        } else if (i >= 120000 && i <= 129999) { //Bows
            return Globals.ITEM_BOW;
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
