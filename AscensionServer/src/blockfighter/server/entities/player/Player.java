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
import blockfighter.server.entities.buff.BuffPassiveResist;
import blockfighter.server.entities.buff.BuffShieldDash;
import blockfighter.server.entities.buff.BuffShieldReflect;
import blockfighter.server.entities.buff.BuffStun;
import blockfighter.server.entities.buff.BuffSwordSlash;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.items.Items;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.player.skills.SkillBowArc;
import blockfighter.server.entities.player.skills.SkillBowFrost;
import blockfighter.server.entities.player.skills.SkillBowPower;
import blockfighter.server.entities.player.skills.SkillBowRapid;
import blockfighter.server.entities.player.skills.SkillBowStorm;
import blockfighter.server.entities.player.skills.SkillBowVolley;
import blockfighter.server.entities.player.skills.SkillPassiveBarrier;
import blockfighter.server.entities.player.skills.SkillPassiveBowMastery;
import blockfighter.server.entities.player.skills.SkillPassiveDualSword;
import blockfighter.server.entities.player.skills.SkillPassiveHarmony;
import blockfighter.server.entities.player.skills.SkillPassiveKeenEye;
import blockfighter.server.entities.player.skills.SkillPassiveResistance;
import blockfighter.server.entities.player.skills.SkillPassiveShadowAttack;
import blockfighter.server.entities.player.skills.SkillPassiveShieldMastery;
import blockfighter.server.entities.player.skills.SkillPassiveStatic;
import blockfighter.server.entities.player.skills.SkillPassiveTough;
import blockfighter.server.entities.player.skills.SkillPassiveVitalHit;
import blockfighter.server.entities.player.skills.SkillPassiveWillpower;
import blockfighter.server.entities.player.skills.SkillShieldCharge;
import blockfighter.server.entities.player.skills.SkillShieldDash;
import blockfighter.server.entities.player.skills.SkillShieldFortify;
import blockfighter.server.entities.player.skills.SkillShieldIron;
import blockfighter.server.entities.player.skills.SkillShieldMagnetize;
import blockfighter.server.entities.player.skills.SkillShieldReflect;
import blockfighter.server.entities.player.skills.SkillSwordCinder;
import blockfighter.server.entities.player.skills.SkillSwordGash;
import blockfighter.server.entities.player.skills.SkillSwordPhantom;
import blockfighter.server.entities.player.skills.SkillSwordSlash;
import blockfighter.server.entities.player.skills.SkillSwordTaunt;
import blockfighter.server.entities.player.skills.SkillSwordVorpal;
import blockfighter.server.maps.GameMap;
import blockfighter.server.net.PacketSender;
import com.esotericsoftware.kryonet.Connection;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Player extends Thread implements GameEntity {

    private static HashSet<Byte> VALID_PLAYER_SKILL_STATES;
    private static HashSet<Byte> IMMOVABLE_PLAYER_SKILL_STATES;
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
            PLAYER_STATE_SHIELD_MAGNETIZE = 0x14,
            PLAYER_STATE_DEAD = 0x15;

    private final byte key;
    private final LogicModule room;
    private UUID uniqueID;
    private String name = "";
    private double x, y, ySpeed, xSpeed;
    private final boolean[] dirKeydown = new boolean[4];
    private boolean isFalling = false, isJumping = false, isInvulnerable = false, isDead = false, isRemoveDebuff = false;
    private boolean updatePos = false, updateFacing = false, updateAnimState = false;
    private byte playerState, animState, facing, frame;

    private final Rectangle2D.Double hitbox;

    private final ConcurrentHashMap<Integer, Buff> buffs = new ConcurrentHashMap<>(150, 0.9f, 1);
    private Buff stunDebuff, knockbackDebuff, barrierBuff, resistBuff;
    private final ConcurrentHashMap<Integer, Buff> reflects = new ConcurrentHashMap<>(10, 0.9f, 1);
    private double dmgReduct, dmgAmp;
    private double barrierDmgTaken = 0;

    private final Connection connection;
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
    private final ConcurrentLinkedQueue<Double> resistDamageSum;

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
        Byte[] validSkillStates = {
            PLAYER_STATE_SWORD_VORPAL,
            PLAYER_STATE_SWORD_PHANTOM,
            PLAYER_STATE_SWORD_CINDER,
            PLAYER_STATE_SWORD_GASH,
            PLAYER_STATE_SWORD_SLASH,
            PLAYER_STATE_SWORD_TAUNT,
            PLAYER_STATE_BOW_ARC,
            PLAYER_STATE_BOW_POWER,
            PLAYER_STATE_BOW_RAPID,
            PLAYER_STATE_BOW_FROST,
            PLAYER_STATE_BOW_STORM,
            PLAYER_STATE_BOW_VOLLEY,
            PLAYER_STATE_SHIELD_CHARGE,
            PLAYER_STATE_SHIELD_DASH,
            PLAYER_STATE_SHIELD_FORTIFY,
            PLAYER_STATE_SHIELD_IRON,
            PLAYER_STATE_SHIELD_REFLECT,
            PLAYER_STATE_SHIELD_MAGNETIZE
        };
        VALID_PLAYER_SKILL_STATES = new HashSet<>(Arrays.asList(validSkillStates));

        Byte[] immovableSkills = {
            PLAYER_STATE_SWORD_VORPAL,
            PLAYER_STATE_SWORD_PHANTOM,
            PLAYER_STATE_BOW_ARC,
            PLAYER_STATE_BOW_POWER,
            PLAYER_STATE_BOW_RAPID,
            PLAYER_STATE_BOW_FROST,
            PLAYER_STATE_BOW_VOLLEY,
            PLAYER_STATE_SHIELD_CHARGE,
            PLAYER_STATE_SHIELD_DASH,
            PLAYER_STATE_SHIELD_IRON,
            PLAYER_STATE_SHIELD_MAGNETIZE
        };
        IMMOVABLE_PLAYER_SKILL_STATES = new HashSet<>(Arrays.asList(immovableSkills));

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
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SHIELD_MAGNETIZE, Skill.SHIELD_MAGNETIZE);

    }

    public Player(final LogicModule l, final byte key, final Connection c, final GameMap map) {
        this.resistDamageSum = new ConcurrentLinkedQueue<>();
        this.room = l;
        this.lastActionTime = l.getTime();
        this.key = key;
        this.connection = c;
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

    public void returnBuffKey(final int bKey) {
        this.buffKeys.add(bKey);
    }

    public Integer getNextBuffKey() {
        Integer nextKey = this.buffKeys.poll();
        while (nextKey == null) {
            this.buffKeys.add(this.maxBuffKeys);
            this.maxBuffKeys++;
            nextKey = this.buffKeys.poll();
        }
        return nextKey;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

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

    public Connection getConnection() {
        return this.connection;
    }

    public byte getFacing() {
        return this.facing;
    }

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

    public int[] getEquips() {
        return this.equips;
    }

    public boolean isDead() {
        return this.isDead;
    }

    private void setDead(final boolean set) {
        this.isDead = set;
    }

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

    public void setDirKeydown(final int direction, final boolean move) {
        if (move) {
            this.lastActionTime = this.room.getTime();
        }
        this.dirKeydown[direction] = move;
    }

    public void setPos(final double x, final double y) {
        if (this.map.isOutOfBounds(x)) {
            this.x = this.map.getValidX(x);
        } else {
            this.x = x;
        }
        this.y = y;
        this.updatePos = true;
    }

    public void setYSpeed(final double speed) {
        this.ySpeed = speed;
    }

    public void setXSpeed(final double speed) {
        if (isDead()) {
            this.xSpeed = 0;
        }
        this.xSpeed = speed;
    }

    public void setSkill(final byte skillCode, final byte level) {
        Skill newSkill = null;
        switch (skillCode) {
            case Skill.SWORD_CINDER:
                newSkill = new SkillSwordCinder(this.room);
                break;
            case Skill.SWORD_GASH:
                newSkill = new SkillSwordGash(this.room);
                break;
            case Skill.SWORD_PHANTOM:
                newSkill = new SkillSwordPhantom(this.room);
                break;
            case Skill.SWORD_SLASH:
                newSkill = new SkillSwordSlash(this.room);
                break;
            case Skill.SWORD_TAUNT:
                newSkill = new SkillSwordTaunt(this.room);
                break;
            case Skill.SWORD_VORPAL:
                newSkill = new SkillSwordVorpal(this.room);
                break;
            case Skill.BOW_ARC:
                newSkill = new SkillBowArc(this.room);
                break;
            case Skill.BOW_FROST:
                newSkill = new SkillBowFrost(this.room);
                break;
            case Skill.BOW_POWER:
                newSkill = new SkillBowPower(this.room);
                break;
            case Skill.BOW_RAPID:
                newSkill = new SkillBowRapid(this.room);
                break;
            case Skill.BOW_STORM:
                newSkill = new SkillBowStorm(this.room);
                break;
            case Skill.BOW_VOLLEY:
                newSkill = new SkillBowVolley(this.room);
                break;
            case Skill.SHIELD_FORTIFY:
                newSkill = new SkillShieldFortify(this.room);
                break;
            case Skill.SHIELD_IRON:
                newSkill = new SkillShieldIron(this.room);
                break;
            case Skill.SHIELD_CHARGE:
                newSkill = new SkillShieldCharge(this.room);
                break;
            case Skill.SHIELD_REFLECT:
                newSkill = new SkillShieldReflect(this.room);
                break;
            case Skill.SHIELD_MAGNETIZE:
                newSkill = new SkillShieldMagnetize(this.room);
                break;
            case Skill.SHIELD_DASH:
                newSkill = new SkillShieldDash(this.room);
                break;
            case Skill.PASSIVE_DUALSWORD:
                newSkill = new SkillPassiveDualSword(this.room);
                break;
            case Skill.PASSIVE_KEENEYE:
                newSkill = new SkillPassiveKeenEye(this.room);
                break;
            case Skill.PASSIVE_VITALHIT:
                newSkill = new SkillPassiveVitalHit(this.room);
                break;
            case Skill.PASSIVE_SHIELDMASTERY:
                newSkill = new SkillPassiveShieldMastery(this.room);
                break;
            case Skill.PASSIVE_BARRIER:
                newSkill = new SkillPassiveBarrier(this.room);
                break;
            case Skill.PASSIVE_RESIST:
                newSkill = new SkillPassiveResistance(this.room);
                break;
            case Skill.PASSIVE_BOWMASTERY:
                newSkill = new SkillPassiveBowMastery(this.room);
                break;
            case Skill.PASSIVE_WILLPOWER:
                newSkill = new SkillPassiveWillpower(this.room);
                break;
            case Skill.PASSIVE_HARMONY:
                newSkill = new SkillPassiveHarmony(this.room);
                break;
            case Skill.PASSIVE_TOUGH:
                newSkill = new SkillPassiveTough(this.room);
                break;
            case Skill.PASSIVE_SHADOWATTACK:
                newSkill = new SkillPassiveShadowAttack(this.room);
                break;
            case Skill.PASSIVE_STATIC:
                newSkill = new SkillPassiveStatic(this.room);
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

            if (!isImmovableUsingSkill() && !isStunned() && !isKnockback()) {
                updateFacing();
                updateWalk(movedX);
                if (!this.isJumping && !this.isFalling) {
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

        if (this.connected && Globals.nsToMs(this.room.getTime() - this.lastActionTime) >= Globals.SERVER_MAX_IDLE) {
            Globals.log(Player.class, this.connection + " Disconnecting <" + this.name + "> due to idling.", Globals.LOG_TYPE_DATA, true);
            disconnect();
        }
    }

    private void updateDead() {
        final long deathDuration = Globals.nsToMs(this.room.getTime() - this.deathTime);
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
        this.skillCastTime = this.room.getTime();
        // Globals.log("DATA_PLAYER_CASTSKILL", "Key: " + key + " Room: " + logic.getRoom() + " Player: " + getPlayerName() + " Skill: " +
        // data[3], Globals.LOG_TYPE_DATA, true);

        queuePlayerState(this.skills.get(skillCode).castPlayerState());
        this.skills.get(skillCode).setCooldown();
        sendCooldown(skillCode);
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
        while (!this.skillUseQueue.isEmpty()) {
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
        if (!isKnockback() && isImmovableUsingSkill()) {
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

    public boolean updateSkillEnd(final long currentSkillDuration, final int skillEndDuration, final boolean isCanceledByStun, final boolean isCanceledByKnockback) {
        return updateSkillEnd(currentSkillDuration >= skillEndDuration || (isCanceledByStun && isStunned() || (isCanceledByKnockback && isKnockback())));
    }

    private void updateHP() {
        long sinceLastHPSend = Globals.nsToMs(this.room.getTime() - this.lastHPSendTime);
        // Empty damage queued
        if (isInvulnerable()) {
            // Take no damage
            this.damageQueue.clear();
        }
        Player lastHitter = null;
        double totalDamageInTick = 0;
        while (!this.damageQueue.isEmpty()) {
            final Damage dmg = this.damageQueue.poll();
            if (dmg != null) {
                lastHitter = dmg.getOwner();
                double finalDamage = dmg.getDamage() * this.dmgAmp;
                // Proc stuff like shadow attack
                dmg.proc();

                // Check if I have reflect damage buff and reflect off owner
                if (dmg.canReflect()) {
                    for (final Map.Entry<Integer, Buff> bEntry : this.buffs.entrySet()) {
                        final Buff b = bEntry.getValue();
                        if (b instanceof BuffShieldReflect) {
                            Player reflectOwner = ((BuffShieldReflect) b).getOwner();
                            SkillShieldReflect reflectSkill = (SkillShieldReflect) reflectOwner.getSkill(Skill.SHIELD_REFLECT);
                            reflectSkill.updateSkillReflectHit(finalDamage, ((BuffShieldReflect) b).getMultiplier(), reflectOwner);
                        }
                    }
                }
                // If it isnt true damage do reduction
                if (!dmg.isTrueDamage()) {
                    finalDamage = finalDamage * this.stats[Globals.STAT_DAMAGEREDUCT];
                }

                // Buff Reductions
                finalDamage = finalDamage * this.dmgReduct;
                //Passive damage reduction
                double passiveReduct = 0;
                // Defender Mastery Passive Reduction
                if (hasSkill(Skill.PASSIVE_SHIELDMASTERY) && getSkill(Skill.PASSIVE_SHIELDMASTERY).canCast(this)) {
                    passiveReduct += 0.05 + 0.005 * getSkillLevel(Skill.PASSIVE_SHIELDMASTERY);
                }

                //Passive Tough Skin
                if (hasSkill(Skill.PASSIVE_TOUGH)) {
                    passiveReduct += 0.06 + 0.003 * getSkillLevel(Skill.PASSIVE_TOUGH);
                }

                // Dual Wield Passive Reduction
                if (hasSkill(Skill.PASSIVE_DUALSWORD) && getSkill(Skill.PASSIVE_DUALSWORD).canCast(this)) {
                    passiveReduct += 0.01 * getSkillLevel(Skill.PASSIVE_DUALSWORD);
                }
                finalDamage = finalDamage * (1 - passiveReduct);

                // Barrier reduction
                if (this.barrierBuff != null) {
                    finalDamage = ((BuffPassiveBarrier) this.barrierBuff).reduceDmg(finalDamage);
                    PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_PASSIVE_BARRIER, dmg.getDmgPoint().x, dmg.getDmgPoint().y);
                }

                if (this.resistBuff != null) {
                    PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_PASSIVE_RESIST, dmg.getDmgPoint().x, dmg.getDmgPoint().y);
                }

                // Send client damage display
                if (!dmg.isHidden()) {
                    if (dmg.getOwner() != null && dmg.isCrit()) {
                        PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_BLOOD_HIT, dmg.getOwner().getKey(), this.key);
                    }
                    sendDamage(dmg, (int) finalDamage);
                }
                // Final damage taken
                this.stats[Globals.STAT_MINHP] -= (int) finalDamage;
                totalDamageInTick += finalDamage;
                if (finalDamage > 0) {
                    sinceLastHPSend = 150;
                }
                if (hasSkill(Skill.PASSIVE_BARRIER) && this.skills.get(Skill.PASSIVE_BARRIER).canCast()) {
                    this.barrierDmgTaken += finalDamage;
                    if (this.barrierDmgTaken >= this.stats[Globals.STAT_MAXHP] * 0.5) {
                        this.barrierDmgTaken = 0;
                        queueBuff(new BuffPassiveBarrier(this.room,
                                this.stats[Globals.STAT_MAXHP] * (0.1 + 0.005 * getSkillLevel(Skill.PASSIVE_BARRIER)),
                                this));
                        PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_PASSIVE_BARRIER, dmg.getDmgPoint().x, dmg.getDmgPoint().y);
                        this.skills.get(Skill.PASSIVE_BARRIER).setCooldown();
                        sendCooldown(Skill.PASSIVE_BARRIER);
                    }
                }
                //Globals.log(Player.class.getSimpleName(), "<" + this.getPlayerName() + "> taking damage"
                //        + " | Source: <" + ((dmg.getOwner() != null) ? dmg.getOwner().getPlayerName() : dmg.getMobOwner().getClass().getSimpleName()) + ">"
                //        + " | Damage Amp: " + this.dmgAmp
                //        + " | Raw: " + dmg.getDamage()
                //        + " | Taken: " + amount, Globals.LOG_TYPE_DATA, true);
            }
        }

        // Resistance Passive Snapshot HP 
        if (hasSkill(Skill.PASSIVE_RESIST) && this.skills.get(Skill.PASSIVE_RESIST).canCast()) {
            if (resistDamageSum.size() >= Globals.LOGIC_TICKS_PER_SEC * 2) {
                resistDamageSum.poll();
            }
            resistDamageSum.add(totalDamageInTick);
            double damageSum = 0;
            for (double resistDmg : this.resistDamageSum) {
                damageSum += resistDmg;
            }
            if (damageSum >= 0.25 * this.stats[Globals.STAT_MAXHP]) {
                queueBuff(new BuffPassiveResist(this.room, 2000, 1));
                this.skills.get(Skill.PASSIVE_RESIST).setCooldown();
                sendCooldown(Skill.PASSIVE_RESIST);
                resistDamageSum.clear();
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
            if (lastHitter != null) {
                lastHitter.giveEXP(Globals.calcEXPtoNxtLvl(this.stats[Globals.STAT_LEVEL]) / 20);
                lastHitter.giveDrop(this.stats[Globals.STAT_LEVEL]);
            }
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
            PacketSender.sendAll(bytes, this.room.getRoom());
            //this.nextHPSend = 150;
            this.lastHPSendTime = this.room.getTime();
        }
    }

    private void updateBuffs() {
        // Update exisiting buffs
        this.stunDebuff = null;
        this.knockbackDebuff = null;
        this.barrierBuff = null;
        this.resistBuff = null;
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
                this.reflects.put(bEntry.getKey(), b);
            } else if (b instanceof BuffPassiveBarrier) {
                if (this.barrierBuff == null) {
                    this.barrierBuff = b;
                }
            } else if (b instanceof BuffPassiveResist) {
                if (this.resistBuff == null) {
                    this.resistBuff = b;
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
        PacketSender.sendParticle(this.room.getRoom(), Globals.PARTICLE_BLOOD, this.key);
        setInvulnerable(false);
        setRemovingDebuff(false);
        setDead(true);
        setPlayerState(PLAYER_STATE_DEAD);
        this.damageQueue.clear();
        this.healQueue.clear();
        this.skillUseQueue.clear();
        this.buffQueue.clear();
        this.barrierDmgTaken = 0;
        this.resistDamageSum.clear();
        this.buffs.clear();
        this.deathTime = this.room.getTime();
    }

    private void respawn() {
        this.stats[Globals.STAT_MINHP] = this.stats[Globals.STAT_MAXHP];
        setXSpeed(0);
        Point2D.Double spawn = map.getRandomSpawnPoint();
        setPos(spawn.x, spawn.y);
        setInvulnerable(false);
        setRemovingDebuff(false);
        setDead(false);
        queuePlayerState(PLAYER_STATE_STAND);
        this.buffs.clear();
        this.damageQueue.clear();
        this.healQueue.clear();
        this.skillUseQueue.clear();
        this.buffQueue.clear();
        this.barrierDmgTaken = 0;
        this.resistDamageSum.clear();
    }

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
        if (hasSkill(Skill.PASSIVE_SHIELDMASTERY) && getSkill(Skill.PASSIVE_SHIELDMASTERY).canCast(this)) {
            mult += 0.09 + 0.002 * getSkillLevel(Skill.PASSIVE_SHIELDMASTERY);
        }
        // Power of Will Passive
        if (hasSkill(Skill.PASSIVE_WILLPOWER)) {
            // (5% + 0.5% Per Level) * %HP Left
            mult += (0.05 + 0.005 * getSkillLevel(Skill.PASSIVE_WILLPOWER))
                    * (this.stats[Globals.STAT_MINHP] / this.stats[Globals.STAT_MAXHP]);
        }

        //Passive Harmony
        if (hasSkill(Skill.PASSIVE_HARMONY)) {
            dmg += this.stats[Globals.STAT_MAXHP] * (0.001 + 0.001 * getSkillLevel(Skill.PASSIVE_HARMONY));
        }

        dmg *= mult;
        return dmg;
    }

    public boolean rollCrit() {
        return rollCrit(0);
    }

    public boolean rollCrit(final double bonusCritChance) {
        double totalCritChance = this.stats[Globals.STAT_CRITCHANCE] + bonusCritChance;
        // Dual Sword Passive
        if (hasSkill(Skill.PASSIVE_DUALSWORD)
                && Items.getItemType(this.equips[Globals.ITEM_WEAPON]) == Globals.ITEM_SWORD
                && Items.getItemType(this.equips[Globals.ITEM_OFFHAND]) == Globals.ITEM_SWORD) {
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
        if (hasSkill(Skill.PASSIVE_BOWMASTERY) && getSkill(Skill.PASSIVE_BOWMASTERY).canCast(this)) {
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
        for (int equipCode : Items.ITEM_CODES) {
            if (Globals.rng(100) < 2) {
                final byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_INT * 2];
                bytes[0] = Globals.DATA_PLAYER_GIVEDROP;

                final byte[] level = Globals.intToBytes((int) lvl);
                System.arraycopy(level, 0, bytes, 1, level.length);

                final byte[] itemCode = Globals.intToBytes(equipCode);
                System.arraycopy(itemCode, 0, bytes, 5, itemCode.length);

                PacketSender.sendPlayer(bytes, this);
            }
        }

        for (int upgradeCode : Items.ITEM_UPGRADE_CODES) {
            if (Globals.rng(100) < 2) {
                final byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_INT * 2];
                bytes[0] = Globals.DATA_PLAYER_GIVEDROP;

                final byte[] level = Globals.intToBytes((int) lvl);
                System.arraycopy(level, 0, bytes, 1, level.length);

                final byte[] itemCode = Globals.intToBytes(upgradeCode);
                System.arraycopy(itemCode, 0, bytes, 5, itemCode.length);

                PacketSender.sendPlayer(bytes, this);
            }
            break;
        }
    }

    public void giveEXP(final double amount) {
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_PLAYER_GIVEEXP;
        final byte[] exp = Globals.intToBytes((int) amount);
        bytes[1] = exp[0];
        bytes[2] = exp[1];
        bytes[3] = exp[2];
        bytes[4] = exp[3];
        PacketSender.sendPlayer(bytes, this);
    }

    public boolean intersectHitbox(final Rectangle2D.Double box) {
        return this.hitbox.intersects(box);
    }

    public Rectangle2D.Double getHitbox() {
        return this.hitbox;
    }

    public boolean isStunned() {
        return this.stunDebuff != null;
    }

    public boolean isKnockback() {
        return this.knockbackDebuff != null;
    }

    public boolean isUsingSkill() {
        return VALID_PLAYER_SKILL_STATES.contains(this.playerState);
    }

    public boolean isImmovableUsingSkill() {
        return IMMOVABLE_PLAYER_SKILL_STATES.contains(this.playerState);
    }

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

    public void queueSkillUse(final byte[] data) {
        this.lastActionTime = this.room.getTime();
        if (!isDead() && this.skills.get(data[3]).canCast(this) && !isUsingSkill()) {
            this.skillUseQueue.add(data);
        }
    }

    public void queueDamage(final Damage damage) {
        if (!isDead()) {
            this.damageQueue.add(damage);
        }
    }

    public void queueHeal(final int heal) {
        if (!isDead()) {
            this.healQueue.add(heal);
        }
    }

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
        if (hasSkill(Skill.PASSIVE_SHADOWATTACK) && getSkill(Skill.PASSIVE_SHADOWATTACK).canCast()) {
            ((SkillPassiveShadowAttack) getSkill(Skill.PASSIVE_SHADOWATTACK)).updateSkillUse(this, dmg);
        }

        if (hasSkill(Skill.PASSIVE_STATIC)) {
            getSkill(Skill.PASSIVE_STATIC).updateSkillUse(this);
        }
    }

    public void queuePlayerState(final byte newState) {
        this.nextState = newState;
    }

    public void setPlayerState(final byte newState) {
        this.playerState = newState;
        this.frame = -1;
        this.lastFrameTime = 0;
        this.updateAnimState = true;
    }

    private void updateAnimState() {
        final byte prevAnimState = this.animState, prevFrame = this.frame;
        final long skillDuration = Globals.nsToMs(this.room.getTime() - this.skillCastTime);
        final long frameDuration = Globals.nsToMs(this.room.getTime() - this.lastFrameTime);
        switch (this.playerState) {
            case PLAYER_STATE_STAND:
                this.animState = Globals.PLAYER_ANIM_STATE_STAND;
                if (frameDuration >= 250) {
                    if (this.frame == 3) {
                        this.frame = 0;
                    } else {
                        this.frame++;
                    }
                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_DEAD:
                this.animState = Globals.PLAYER_ANIM_STATE_DEAD;
                if ((this.frame == 2 && frameDuration >= 250) || (this.frame != 2 && frameDuration >= 50)) {
                    if (this.frame < 10) {
                        this.frame++;
                    }
                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_WALK:
                this.animState = Globals.PLAYER_ANIM_STATE_WALK;
                if (frameDuration >= 70) {
                    if (this.frame == 7) {
                        this.frame = 0;
                    } else {
                        this.frame++;
                    }
                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_JUMP:
                this.animState = Globals.PLAYER_ANIM_STATE_JUMP;
                if (frameDuration >= 30) {
                    if (this.isFalling && this.map.isWithinDistanceToGround(this.x, this.y, 110)) {
                        this.frame = 2;
                    } else if (this.frame < 1) {
                        this.frame++;
                    } else {
                        this.frame = 1;
                    }
                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_SWORD_SLASH:
                if (frameDuration >= 20) {
                    this.animState = Globals.PLAYER_ANIM_STATE_ATTACK;
                    if (this.frame < 5) {
                        this.frame++;
                    }
                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_SWORD_GASH:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACK;
                if (frameDuration >= ((this.frame == 4) ? 150 : 20) && this.frame < 5) {
                    this.frame++;

                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_SWORD_PHANTOM:
                this.animState = Globals.PLAYER_ANIM_STATE_INVIS;
                break;
            case PLAYER_STATE_SWORD_VORPAL:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACK;
                if (frameDuration >= 20 && this.frame < 5) {
                    this.frame++;
                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_SWORD_CINDER:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACK;
                if (frameDuration >= ((this.frame == 4) ? 40 : 30) && this.frame < 5) {
                    this.frame++;
                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_SWORD_TAUNT:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACK;
                if (frameDuration >= ((this.frame == 4) ? 150 : 30) && this.frame < 5) {
                    this.frame++;
                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_BOW_ARC:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACKBOW;
                if (this.frame < 7 && frameDuration >= 30) {
                    this.frame++;
                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_BOW_RAPID:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACKBOW;
                if (this.frame < 7 && frameDuration >= 30) {
                    this.frame++;
                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_BOW_POWER:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACKBOW;
                if (frameDuration >= ((this.frame < 3) ? 30 : 70)) {
                    if (getSkillCounter() < 20 && this.frame != 3) {
                        this.frame++;
                    } else if (getSkillCounter() == 21 && this.frame < 7) {
                        this.frame++;
                    }
                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_BOW_VOLLEY:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACKBOW;
                if (this.frame < 3 && frameDuration >= 30) {
                    this.frame++;
                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_BOW_STORM:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACKBOW;
                if (this.frame < 7 && frameDuration >= 30) {
                    this.frame++;
                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_BOW_FROST:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACKBOW;
                if (this.frame < 7 && frameDuration >= 30) {
                    this.frame++;
                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_SHIELD_DASH:
                this.animState = Globals.PLAYER_ANIM_STATE_ROLL;
                if (frameDuration >= 40 && this.frame < 9) {
                    this.frame++;
                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_SHIELD_CHARGE:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACK;
                if (frameDuration >= ((this.frame == 1) ? 4 : 20) && this.frame < 4) {
                    this.frame++;
                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_SHIELD_FORTIFY:
                this.animState = Globals.PLAYER_ANIM_STATE_BUFF;
                if (frameDuration >= 30 && this.frame < 4) {
                    this.frame++;
                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_SHIELD_REFLECT:
                this.animState = Globals.PLAYER_ANIM_STATE_BUFF;
                if (frameDuration >= 20 && this.frame < 4) {
                    this.frame++;
                }
                break;
            case PLAYER_STATE_SHIELD_IRON:
                this.animState = Globals.PLAYER_ANIM_STATE_BUFF;
                if (frameDuration >= 30 && this.frame < 4) {
                    this.frame++;
                    this.lastFrameTime = this.room.getTime();
                }
                break;
            case PLAYER_STATE_SHIELD_MAGNETIZE:
                this.animState = Globals.PLAYER_ANIM_STATE_BUFF;
                if (getSkillCounter() == 1) {
                    this.frame = 0;
                } else if (frameDuration >= 30 && this.frame < 4) {
                    this.frame++;
                    this.lastFrameTime = this.room.getTime();
                }
                break;
        }
        if (this.animState != prevAnimState || this.frame != prevFrame) {
            this.updateAnimState = true;
        }
    }

    public void sendData() {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 5 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_PLAYER_GET_ALL;
        bytes[1] = this.key;
        final byte[] posXInt = Globals.intToBytes((int) this.x);
        System.arraycopy(posXInt, 0, bytes, 2, posXInt.length);

        final byte[] posYInt = Globals.intToBytes((int) this.y);
        System.arraycopy(posYInt, 0, bytes, 6, posYInt.length);

        bytes[10] = this.facing;
        bytes[11] = this.animState;
        if (this.frame < 0) {
            bytes[12] = 0;
        } else {
            bytes[12] = this.frame;
        }

        PacketSender.sendAll(bytes, this.room.getRoom());
        this.updatePos = false;
        this.updateFacing = false;
        this.updateAnimState = false;
    }

    public void sendPos() {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
        bytes[0] = Globals.DATA_PLAYER_SET_POS;
        bytes[1] = this.key;
        final byte[] posXInt = Globals.intToBytes((int) this.x);
        System.arraycopy(posXInt, 0, bytes, 2, posXInt.length);

        final byte[] posYInt = Globals.intToBytes((int) this.y);
        System.arraycopy(posYInt, 0, bytes, 6, posYInt.length);
        bytes[10] = this.facing;
        PacketSender.sendAll(bytes, this.room.getRoom());
        this.updatePos = false;
    }

    public void sendFacing() {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_SET_FACING;
        bytes[1] = this.key;
        bytes[2] = this.facing;
        PacketSender.sendAll(bytes, this.room.getRoom());
        this.updateFacing = false;
    }

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
        PacketSender.sendAll(bytes, this.room.getRoom());
        this.updateAnimState = false;
    }

    public void sendCooldown(final byte[] data) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
        bytes[0] = Globals.DATA_PLAYER_SET_COOLDOWN;
        bytes[1] = data[3];
        PacketSender.sendPlayer(bytes, this);
    }

    public void sendCooldown(final byte skillCode) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
        bytes[0] = Globals.DATA_PLAYER_SET_COOLDOWN;
        bytes[1] = skillCode;
        PacketSender.sendPlayer(bytes, this);
    }

    public void sendDamage(final Damage dmg, final int dmgDealt) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 3];
        bytes[0] = Globals.DATA_NUMBER;
        bytes[1] = dmg.getDamageType();

        final byte[] posXInt = Globals.intToBytes((int) this.x);
        System.arraycopy(posXInt, 0, bytes, 2, posXInt.length);

        final byte[] posYInt = Globals.intToBytes((int) this.y - 20);
        System.arraycopy(posYInt, 0, bytes, 6, posYInt.length);

        final byte[] d = Globals.intToBytes(dmgDealt);
        System.arraycopy(d, 0, bytes, 10, d.length);

        if (map.isPvP()) {
            PacketSender.sendPlayer(bytes, dmg.getOwner());

            final byte[] pvpBytes = Arrays.copyOf(bytes, bytes.length);
            pvpBytes[1] = Globals.NUMBER_TYPE_MOB;
            PacketSender.sendPlayer(pvpBytes, this);
        } else {
            PacketSender.sendAll(bytes, this.room.getRoom());
        }
    }

    public void sendName() {
        final byte[] data = this.name.getBytes(StandardCharsets.UTF_8);
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + data.length];
        bytes[0] = Globals.DATA_PLAYER_GET_NAME;
        bytes[1] = this.key;
        System.arraycopy(data, 0, bytes, 2, data.length);
        PacketSender.sendAll(bytes, this.room.getRoom());
    }

    public void sendStat(final byte statID) {
        final byte[] stat = Globals.intToBytes((int) getStats()[statID]);
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_PLAYER_GET_STAT;
        bytes[1] = this.key;
        bytes[2] = statID;
        System.arraycopy(stat, 0, bytes, 3, stat.length);
        PacketSender.sendAll(bytes, this.room.getRoom());
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

    public void setUniqueID(final UUID id) {
        this.uniqueID = id;
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public void setPlayerName(final String s) {
        this.name = s;
    }

    public String getPlayerName() {
        return this.name;
    }

    public void setStat(final byte stat, final double amount) {
        this.stats[stat] = amount;
        updateStats();
    }

    public double[] getStats() {
        return this.stats;
    }

    public void setBonusStat(final byte stat, final double amount) {
        this.bonusStats[stat] = amount;
        updateStats();
    }

    public double[] getBonusStats() {
        return this.bonusStats;
    }

    public void setEquip(final int slot, final int itemCode) {
        this.equips[slot] = itemCode;
    }

    public void disconnect() {
        if (isConnected()) {
            this.connected = false;
            final byte[] bytes = new byte[2];
            bytes[0] = Globals.DATA_PLAYER_DISCONNECT;
            bytes[1] = this.key;
            PacketSender.sendAll(bytes, room.getRoom());
            this.connection.close();
            Globals.log(Player.class, "Disconnected <" + getPlayerName() + ">", Globals.LOG_TYPE_DATA, true);
        }
    }

    public boolean isConnected() {
        return this.connected;
    }

}
