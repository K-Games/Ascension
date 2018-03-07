package blockfighter.server.entities.player;

import blockfighter.server.LogicModule;
import blockfighter.server.RoomData;
import blockfighter.server.entities.GameEntity;
import blockfighter.server.entities.buff.*;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.skills.*;
import blockfighter.server.entities.player.skills.passive.SkillPassiveDualSword;
import blockfighter.server.entities.player.skills.passive.SkillPassiveShieldMastery;
import blockfighter.server.entities.player.skills.shield.SkillShieldReflect;
import blockfighter.server.maps.GameMap;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
import com.esotericsoftware.kryonet.Connection;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Player implements GameEntity, Callable<Player> {

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
            PLAYER_STATE_UTILITY_DASH = 0x10,
            PLAYER_STATE_UTILITY_ADRENALINE = 0x11,
            PLAYER_STATE_SHIELD_ROAR = 0x12,
            PLAYER_STATE_SHIELD_REFLECT = 0x13,
            PLAYER_STATE_SHIELD_MAGNETIZE = 0x14,
            PLAYER_STATE_DEAD = 0x15;

    private final byte key;
    private final LogicModule logic;
    private final RoomData room;

    private UUID uniqueID;
    private String name = "";
    private double x, y, ySpeed, xSpeed, targetXSpeed, xSpeedMultiplier, ySpeedMultiplier;
    private final boolean[] dirKeydown = new boolean[4];
    private boolean isFalling = false, isJumping = false, isInvulnerable = false, isDead = false, isRemoveDebuff = false, isHyperStance = false;
    private boolean updatePos = false, updateFacing = false, updateAnimState = false;
    private boolean updateScore = false;
    private byte playerState, animState, facing, frame;

    private final Rectangle2D.Double hitbox;

    private final ConcurrentHashMap<Integer, Buff> buffs = new ConcurrentHashMap<>(150, 0.9f, 1);
    private final ConcurrentHashMap<Integer, Buff> reflects = new ConcurrentHashMap<>(10, 0.9f, 1);
    private double dmgReduct, dmgAmp;
    private double barrierDmgTaken = 0;

    private final Connection connection;
    private final GameMap map;
    private final double[] stats = new double[Globals.NUM_STATS], bonusStats = new double[Globals.NUM_STATS];

    private final int[] equips = new int[Globals.NUM_EQUIP_SLOTS];
    private final ConcurrentHashMap<Byte, Skill> skills = new ConcurrentHashMap<>(Globals.NUM_SKILLS, 0.9f, 1);
    private boolean connected = true;

    private final ConcurrentLinkedQueue<Damage> damageQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Integer> healQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<byte[]> skillUseQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Buff> buffQueue = new ConcurrentLinkedQueue<>();
    private final HashMap<Player, Double> playerDamageCount = new HashMap<>();

    private final ConcurrentLinkedQueue<Integer> buffKeys = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Double> resistDamageSum;

    private final HashMap<Class<? extends Buff>, Buff> buffsMap = new HashMap<>();

    private int maxBuffKeys = 0;
    private Byte nextState;

    private long lastActionTime = 0;
    private long lastEmoteTime = 0;
    private long skillCastTime = 0,
            deathTime = 0,
            lastHPSendTime = 0,
            lastFrameTime = 0;
    private int skillCounter = 0;
    private int score = 0;

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
            PLAYER_STATE_UTILITY_DASH,
            PLAYER_STATE_UTILITY_ADRENALINE,
            PLAYER_STATE_SHIELD_ROAR,
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
            PLAYER_STATE_UTILITY_DASH,
            PLAYER_STATE_SHIELD_ROAR,
            PLAYER_STATE_SHIELD_MAGNETIZE
        };
        IMMOVABLE_PLAYER_SKILL_STATES = new HashSet<>(Arrays.asList(immovableSkills));

        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SWORD_VORPAL, Globals.SWORD_VORPAL);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SWORD_PHANTOM, Globals.SWORD_PHANTOM);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SWORD_CINDER, Globals.SWORD_CINDER);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SWORD_GASH, Globals.SWORD_GASH);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SWORD_SLASH, Globals.SWORD_SLASH);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SWORD_TAUNT, Globals.SWORD_TAUNT);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_BOW_ARC, Globals.BOW_ARC);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_BOW_POWER, Globals.BOW_POWER);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_BOW_RAPID, Globals.BOW_RAPID);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_BOW_FROST, Globals.BOW_FROST);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_BOW_STORM, Globals.BOW_STORM);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_BOW_VOLLEY, Globals.BOW_VOLLEY);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SHIELD_CHARGE, Globals.SHIELD_CHARGE);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_UTILITY_DASH, Globals.UTILITY_DASH);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_UTILITY_ADRENALINE, Globals.UTILITY_ADRENALINE);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SHIELD_ROAR, Globals.SHIELD_ROAR);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SHIELD_REFLECT, Globals.SHIELD_REFLECT);
        PLAYER_STATE_SKILLCODE.put(PLAYER_STATE_SHIELD_MAGNETIZE, Globals.SHIELD_MAGNETIZE);

    }

    public Player(final LogicModule l, final byte key, final Connection c, final GameMap map) {
        this.resistDamageSum = new ConcurrentLinkedQueue<>();
        this.logic = l;
        this.room = l.getRoomData();
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

    public void addScore(final int amount) {
        this.score += amount;
        updateClientScore();
    }

    public long getLastFrameTime() {
        return this.lastFrameTime;
    }

    public void setLastFrameTime(long time) {
        lastFrameTime = time;
    }

    public int getScore() {
        return this.score;
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

    public void setAnimState(byte newAnimState) {
        this.animState = newAnimState;
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

    public boolean isUpdatePos() {
        return this.updatePos;
    }

    public boolean isUpdateAnimState() {
        return this.updateAnimState;
    }

    public void incrementSkillCounter() {
        this.skillCounter++;
    }

    public int[] getEquips() {
        return this.equips;
    }

    public ConcurrentHashMap<Byte, Skill> getSkills() {
        return this.skills;
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

    public boolean hasBuff(final Class<? extends Buff> buffClass) {
        return this.buffsMap.get(buffClass) != null;
    }

    public Map.Entry<Integer, Buff> getExistingBuff(final Class<? extends Buff> buffClass) {
        for (final Map.Entry<Integer, Buff> bEntry : this.buffs.entrySet()) {
            final Buff b = bEntry.getValue();
            if (buffClass.isInstance(b)) {
                return bEntry;
            }
        }
        return null;
    }

    public void setDirKeydown(final int direction, final boolean move) {
        if (move) {
            this.lastActionTime = this.logic.getTime();
        }
        this.dirKeydown[direction] = move;
    }

    public void setPos(final double x, final double y) {
        this.x = this.map.getValidX(x, y);
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
        this.targetXSpeed = this.xSpeed;
    }

    public void accelerateXSpeed(final double targetSpeed) {
        this.targetXSpeed = targetSpeed;
    }

    public void setSkill(final byte skillCode, final byte level) {
        Skill newSkill = null;
        try {
            Constructor<? extends Skill> constructor = Globals.SkillClassMap.get(skillCode).getServerClass().getDeclaredConstructor(LogicModule.class);
            newSkill = constructor.newInstance(this.logic);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Globals.logError(ex.toString(), ex);
        }
        if (newSkill != null && getStats()[Globals.STAT_LEVEL] >= newSkill.getReqLevel()) {
            newSkill.setLevel(level);
            this.skills.put(skillCode, newSkill);
        }
    }

    @Override
    public Player call() {
        try {
            update();
        } catch (final Exception ex) {
            Globals.logError(ex.getMessage(), ex);
        }
        return this;
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
        if (this.xSpeed != this.targetXSpeed) {
            updateXAcceleration();
        }
        final boolean xChanged = updateX(this.xSpeed);

        if (isDead()) {
            // Update respawn Timer
            updateDead();
        } else {
            // Update Actions
            if (isStunned() && !isKnockback()) {
                setXSpeed(0);
            }

            if (!isImmovableUsingSkill() && !isStunned()) {
                updateFacing();
                if (!isKnockback()) {
                    updateMove(xChanged);
                    if (!this.isJumping && !this.isFalling) {
                        updateJump();
                    }
                }
            }

            updateSkillCast();
            updatePlayerState();
            if (isUsingSkill()) {
                updateSkillUse();
            }

            updateHP();
        }
        this.hitbox.x = this.x - 20;
        this.hitbox.y = this.y - 100;

        updateAnimState();
        if (this.updateFacing) {
            sendFacing();
        }
        if (this.updateScore) {
            sendScore();
        }
        if (this.connected && Globals.nsToMs(this.logic.getTime() - this.lastActionTime) >= (Integer) Globals.ServerConfig.MAX_PLAYER_IDLE.getValue()) {
            Globals.log(Player.class, this.connection + " Disconnecting <" + this.name + "> due to idling.", Globals.LOG_TYPE_DATA);
            disconnect();
        }
    }

    private void updateDead() {
        final long deathDuration = Globals.nsToMs(this.logic.getTime() - this.deathTime);
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
        if (this.skills.get(skillCode).isPassive() || !this.skills.get(skillCode).canCast(this)) {
            return;
        }
        this.skillCounter = 0;
        this.skillCastTime = this.logic.getTime();
        // Globals.log("DATA_PLAYER_CASTSKILL", "Key: " + key + " RoomData: " + logic.getRoomData() + " Player: " + getPlayerName() + " Skill: " +
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
            if (skillCode == Globals.SHIELD_ROAR || !isStunned()) {
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
            setHyperStance(false);
            setPlayerState(PLAYER_STATE_STAND);
            return true;
        }
        return false;
    }

    public boolean updateSkillEnd(final long currentSkillDuration, final int skillEndDuration, final boolean isCanceledByStun, final boolean isCanceledByKnockback) {
        return updateSkillEnd(currentSkillDuration >= skillEndDuration || (isCanceledByStun && isStunned() || (isCanceledByKnockback && isKnockback())));
    }

    private void updateHP() {
        long sinceLastHPSend = Globals.nsToMs(this.logic.getTime() - this.lastHPSendTime);
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
                            SkillShieldReflect reflectSkill = (SkillShieldReflect) reflectOwner.getSkill(Globals.SHIELD_REFLECT);
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
                if (hasSkill(Globals.PASSIVE_SHIELDMASTERY) && getSkill(Globals.PASSIVE_SHIELDMASTERY).canCast(this)) {
                    double baseReduct = getSkill(Globals.PASSIVE_SHIELDMASTERY).getCustomValue(SkillPassiveShieldMastery.CUSTOM_DATA_HEADERS[0]);
                    double multReduct = getSkill(Globals.PASSIVE_SHIELDMASTERY).getCustomValue(SkillPassiveShieldMastery.CUSTOM_DATA_HEADERS[1]);
                    passiveReduct += baseReduct + multReduct * getSkillLevel(Globals.PASSIVE_SHIELDMASTERY);
                }

                //Passive Tough Skin
                if (hasSkill(Globals.PASSIVE_TOUGH)) {
                    double baseValue = getSkill(Globals.PASSIVE_TOUGH).getBaseValue();
                    double multValue = getSkill(Globals.PASSIVE_TOUGH).getMultValue();
                    passiveReduct += baseValue + multValue * getSkillLevel(Globals.PASSIVE_TOUGH);
                }

                // Dual Wield Passive Reduction
                if (hasSkill(Globals.PASSIVE_DUALSWORD) && getSkill(Globals.PASSIVE_DUALSWORD).canCast(this)) {
                    double dmgReductMult = getSkill(Globals.PASSIVE_DUALSWORD).getCustomValue(SkillPassiveDualSword.CUSTOM_DATA_HEADERS[0]);
                    passiveReduct += dmgReductMult * getSkillLevel(Globals.PASSIVE_DUALSWORD);
                }
                finalDamage = finalDamage * (1 - passiveReduct);

                // Barrier reduction
                if (hasBuff(BuffPassiveBarrier.class)) {
                    finalDamage = ((BuffPassiveBarrier) this.buffsMap.get(BuffPassiveBarrier.class)).reduceDmg(finalDamage);
                    PacketSender.sendParticle(this.logic, Globals.Particles.PASSIVE_BARRIER.getParticleCode(), dmg.getDmgPoint().x, dmg.getDmgPoint().y);
                }

                if (hasBuff(BuffPassiveResist.class)) {
                    PacketSender.sendParticle(this.logic, Globals.Particles.PASSIVE_RESIST.getParticleCode(), dmg.getDmgPoint().x, dmg.getDmgPoint().y);
                }

                // Send client damage display
                if (!dmg.isHidden()) {
                    if (dmg.getOwner() != null && dmg.isCrit()) {
                        PacketSender.sendParticle(this.logic, Globals.Particles.BLOOD_EMITTER.getParticleCode(), dmg.getOwner().getKey(), this.key);
                    }
                    byte hitFacing = (this.x < dmg.getOwner().getX()) ? Globals.RIGHT : Globals.LEFT;
                    if (dmg.showParticle()) {
                        PacketSender.sendParticle(this.logic, Globals.Particles.HIT.getParticleCode(), dmg.getDmgPoint().x, dmg.getDmgPoint().y, hitFacing);
                    }
                    sendDamage(dmg, (int) finalDamage);
                }
                // Final damage taken
                this.stats[Globals.STAT_MINHP] -= (int) finalDamage;
                totalDamageInTick += finalDamage;
                //Globals.log(Player.class, dmg.getOwner().getPlayerName() + " dealt " + Globals.NUMBER_FORMAT.format(finalDamage) + "(" + dmg.getDamage() + ") Damage to " + getPlayerName(), Globals.LOG_TYPE_DATA, true);

                if (this.playerDamageCount.containsKey(dmg.getOwner())) {
                    double damageCount = this.playerDamageCount.get(dmg.getOwner());
                    damageCount += finalDamage;
                    this.playerDamageCount.put(dmg.getOwner(), damageCount);
                } else {
                    this.playerDamageCount.put(dmg.getOwner(), finalDamage);
                }

                if (finalDamage > 0) {
                    sinceLastHPSend = 150;
                }
                if (hasSkill(Globals.PASSIVE_BARRIER) && this.skills.get(Globals.PASSIVE_BARRIER).canCast()) {
                    this.barrierDmgTaken += finalDamage;
                    if (this.barrierDmgTaken >= this.stats[Globals.STAT_MAXHP] * 0.5) {
                        this.barrierDmgTaken = 0;
                        double baseValue = getSkill(Globals.PASSIVE_BARRIER).getBaseValue();
                        double multValue = getSkill(Globals.PASSIVE_BARRIER).getMultValue();
                        queueBuff(new BuffPassiveBarrier(this.logic,
                                this.stats[Globals.STAT_MAXHP] * (baseValue + multValue * getSkillLevel(Globals.PASSIVE_BARRIER)),
                                this));
                        PacketSender.sendParticle(this.logic, Globals.Particles.PASSIVE_BARRIER.getParticleCode(), dmg.getDmgPoint().x, dmg.getDmgPoint().y);
                        this.skills.get(Globals.PASSIVE_BARRIER).setCooldown();
                        sendCooldown(Globals.PASSIVE_BARRIER);
                    }
                }
            }
        }

        // Resistance Passive Snapshot HP
        if (hasSkill(Globals.PASSIVE_RESIST) && this.skills.get(Globals.PASSIVE_RESIST).canCast()) {
            if (resistDamageSum.size() >= Globals.SERVER_LOGIC_TICKS_PER_SEC * 2) {
                resistDamageSum.poll();
            }
            resistDamageSum.add(totalDamageInTick);
            double damageSum = 0;
            for (double resistDmg : this.resistDamageSum) {
                damageSum += resistDmg;
            }
            if (damageSum >= 0.25 * this.stats[Globals.STAT_MAXHP]) {
                queueBuff(new BuffPassiveResist(this.logic, 2000, 1));
                this.skills.get(Globals.PASSIVE_RESIST).setCooldown();
                sendCooldown(Globals.PASSIVE_RESIST);
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
        this.stats[Globals.STAT_MINHP] += this.stats[Globals.STAT_REGEN] / Globals.SERVER_LOGIC_TICKS_PER_SEC;

        if (this.stats[Globals.STAT_MINHP] > this.stats[Globals.STAT_MAXHP]) {
            this.stats[Globals.STAT_MINHP] = this.stats[Globals.STAT_MAXHP];
        } else if (this.stats[Globals.STAT_MINHP] < 0) {
            this.stats[Globals.STAT_MINHP] = 0;
        }

        if (this.stats[Globals.STAT_MINHP] <= 0) {
            die(lastHitter);
        }

        // Update client hp every 150ms or if damaged/healed(excluding regen).
        if (sinceLastHPSend >= 150) {
            final byte[] stat = Globals.intToBytes((int) this.stats[Globals.STAT_MINHP]);
            final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
            bytes[0] = Globals.DATA_PLAYER_GET_STAT;
            bytes[1] = this.key;
            bytes[2] = Globals.STAT_MINHP;
            System.arraycopy(stat, 0, bytes, 3, stat.length);
            PacketSender.sendAll(bytes, this.logic);
            //this.nextHPSend = 150;
            this.lastHPSendTime = this.logic.getTime();
        }
    }

    private void updateBuffs() {
        // Update exisiting buffs
        this.buffsMap.clear();
        this.reflects.clear();
        this.dmgReduct = 1;
        this.dmgAmp = 1;
        this.xSpeedMultiplier = 1;
        this.ySpeedMultiplier = 1;

        // Empty and add buffs from queue
        while (!this.buffQueue.isEmpty()) {
            final Buff b = this.buffQueue.poll();
            if (b != null) {
                if (isHyperStance() && (b instanceof BuffKnockback || b instanceof BuffStun)) {
                    // if in Hyper Stance and buff is a kb or stun don't apply
                    continue;
                }
                if (!canDebuffAffect() && b.isDebuff()) {
                    // Don't apply debuff when invulnerable
                    continue;
                }

                if (b instanceof BuffUtilityDash) {
                    final Map.Entry<Integer, Buff> prevBuff = getExistingBuff(BuffUtilityDash.class);
                    if (prevBuff != null) {
                        this.buffs.remove(prevBuff.getKey());
                    }
                } else if (b instanceof BuffSwordSlash) {
                    final Map.Entry<Integer, Buff> prevBuff = getExistingBuff(BuffSwordSlash.class);
                    if (prevBuff != null) {
                        this.buffs.remove(prevBuff.getKey());
                    }
                }
                final Integer bKey = getNextBuffKey();
                if (bKey != null) {
                    this.buffs.put(bKey, b);
                    //Globals.log(Player.class, "Applied " + b.getClass().getSimpleName() + " " + (b.isDebuff() ? "Debuff" : "Buff") + " to " + this.getPlayerName(), Globals.LOG_TYPE_DATA);
                }
            }
        }

        Iterator<Entry<Integer, Buff>> buffsIter = this.buffs.entrySet().iterator();
        while (buffsIter.hasNext()) {
            Entry<Integer, Buff> bEntry = buffsIter.next();
            final Buff buff = bEntry.getValue();
            buff.update();

            // Track if stunned, knocked or has a barrier buff.
            if (canDebuffAffect()) {
                if (!isHyperStance()) {
                    if (buff instanceof BuffStun) {
                        if (!hasBuff(BuffStun.class)) {
                            this.buffsMap.put(BuffStun.class, buff);
                        }
                    } else if (buff instanceof BuffKnockback) {
                        if (!hasBuff(BuffKnockback.class)) {
                            this.buffsMap.put(BuffKnockback.class, buff);
                        }
                    }
                }
            }

            if (buff instanceof BuffShieldReflect) {
                this.reflects.put(bEntry.getKey(), buff);
            }

            if (buff instanceof BuffPassiveBarrier) {
                if (!hasBuff(BuffPassiveBarrier.class)) {
                    this.buffsMap.put(BuffPassiveBarrier.class, buff);
                }
            }

            if (buff instanceof BuffPassiveResist) {
                if (!hasBuff(BuffPassiveResist.class)) {
                    this.buffsMap.put(BuffPassiveResist.class, buff);
                }
            }

            // Add all the damage reduction buffs(Multiplicative)
            if (buff instanceof BuffDmgReduct) {
                this.dmgReduct = this.dmgReduct * ((BuffDmgReduct) buff).getDmgTakenMult();
            }

            // Add all the damage intake amplification(Additive)
            if (buff instanceof BuffDmgTakenAmp) {
                this.dmgAmp = this.dmgAmp + ((BuffDmgTakenAmp) buff).getDmgTakenAmp();
            }

            if (buff instanceof BuffXSpeedIncrease) {
                this.xSpeedMultiplier += ((BuffXSpeedIncrease) buff).getXSpeedIncrease();
            }

            if (buff instanceof BuffSpeedDecrease) {
                this.xSpeedMultiplier -= ((BuffSpeedDecrease) buff).getXSpeedDecrease();
                this.ySpeedMultiplier -= ((BuffSpeedDecrease) buff).getYSpeedDecrease();
            }

            // Remove expired buffs/remove debuffs when invulnerable/special state
            if (buff.isExpired()
                    || (!canDebuffAffect() && buff.isDebuff())
                    || (isHyperStance() && (buff instanceof BuffKnockback || buff instanceof BuffStun))) {
                buffsIter.remove();
                returnBuffKey(bEntry.getKey());
            }
        }
        this.xSpeedMultiplier = (this.xSpeedMultiplier < 0.3) ? 0.3 : this.xSpeedMultiplier;
        this.xSpeedMultiplier = (this.xSpeedMultiplier > 2) ? 2 : this.xSpeedMultiplier;
        this.ySpeedMultiplier = (this.ySpeedMultiplier < 0.2) ? 0.2 : this.ySpeedMultiplier;
    }

    protected void die(final Player killer) {
        if (killer != null) {
            killer.addScore(1);
            killer.giveDrop(this.stats[Globals.STAT_LEVEL]);

            final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
            bytes[0] = Globals.DATA_NOTIFICATION_KILL;
            bytes[1] = killer.getKey();
            bytes[2] = this.key;
            PacketSender.sendAll(bytes, this.logic);
        }

        double totalDamageTaken = 0;
        Iterator<Entry<Player, Double>> iter = this.playerDamageCount.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Player, Double> damageCount = iter.next();
            Player source = damageCount.getKey();
            if (source.isConnected()) {
                double damage = damageCount.getValue();
                totalDamageTaken += damage;
            } else {
                iter.remove();
            }
        }

        double totalExpGiven = this.stats[Globals.STAT_MAXEXP] * (Double) Globals.ServerConfig.EXP_MULTIPLIER.getValue();
        for (final Entry<Player, Double> damageCount : this.playerDamageCount.entrySet()) {
            Player source = damageCount.getKey();
            double damage = damageCount.getValue();
            if (source.isConnected()) {
                double expGivenFactor = damage / totalDamageTaken;
                source.giveEXP(expGivenFactor * totalExpGiven * ((killer != null && source == killer) ? 1.1 : 1));
            }
        }
        this.playerDamageCount.clear();

        PacketSender.sendParticle(this.logic, Globals.Particles.BLOOD_DEATH_EMITTER.getParticleCode(), this.key);
        setInvulnerable(false);
        setRemovingDebuff(false);
        setHyperStance(false);
        setDead(true);
        setPlayerState(PLAYER_STATE_DEAD);
        this.damageQueue.clear();
        this.healQueue.clear();
        this.skillUseQueue.clear();
        this.buffQueue.clear();
        this.barrierDmgTaken = 0;
        this.resistDamageSum.clear();
        this.buffs.clear();
        this.deathTime = this.logic.getTime();
    }

    protected void respawn() {
        this.stats[Globals.STAT_MINHP] = this.stats[Globals.STAT_MAXHP];
        setXSpeed(0);
        Point2D.Double spawn = map.getRandomSpawnPoint();
        setPos(spawn.x, spawn.y);
        setInvulnerable(false);
        setHyperStance(false);
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
        if (hasSkill(Globals.PASSIVE_SHIELDMASTERY) && getSkill(Globals.PASSIVE_SHIELDMASTERY).canCast(this)) {
            double baseValue = getSkill(Globals.PASSIVE_SHIELDMASTERY).getBaseValue();
            double multValue = getSkill(Globals.PASSIVE_SHIELDMASTERY).getMultValue();
            mult += baseValue + multValue * getSkillLevel(Globals.PASSIVE_SHIELDMASTERY);
        }
        // Power of Will Passive
        if (hasSkill(Globals.PASSIVE_WILLPOWER)) {
            // (5% + 0.5% Per Level) * %HP Left
            double baseValue = getSkill(Globals.PASSIVE_WILLPOWER).getBaseValue();
            double multValue = getSkill(Globals.PASSIVE_WILLPOWER).getMultValue();
            mult += (baseValue + multValue * getSkillLevel(Globals.PASSIVE_WILLPOWER))
                    * (this.stats[Globals.STAT_MINHP] / this.stats[Globals.STAT_MAXHP]);
        }

        //Passive Harmony
        if (hasSkill(Globals.PASSIVE_HARMONY)) {
            double baseValue = getSkill(Globals.PASSIVE_HARMONY).getBaseValue();
            double multValue = getSkill(Globals.PASSIVE_HARMONY).getMultValue();
            dmg += this.stats[Globals.STAT_MAXHP] * (baseValue + multValue * getSkillLevel(Globals.PASSIVE_HARMONY));
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
        if (hasSkill(Globals.PASSIVE_DUALSWORD)
                && Globals.getEquipType(this.equips[Globals.EQUIP_WEAPON]) == Globals.ITEM_SWORD
                && Globals.getEquipType(this.equips[Globals.EQUIP_WEAPON]) == Globals.ITEM_SWORD) {
            // Check if has Dual Sword passive AND Mainhand/Offhand are both Swords.
            double baseValue = getSkill(Globals.PASSIVE_DUALSWORD).getBaseValue();
            double multValue = getSkill(Globals.PASSIVE_DUALSWORD).getMultValue();
            totalCritChance += baseValue + multValue * getSkillLevel(Globals.PASSIVE_DUALSWORD);
        }
        // Keen Eye Passive
        if (hasSkill(Globals.PASSIVE_KEENEYE)) {
            double baseValue = getSkill(Globals.PASSIVE_KEENEYE).getBaseValue();
            double multValue = getSkill(Globals.PASSIVE_KEENEYE).getMultValue();
            totalCritChance += baseValue + multValue * getSkillLevel(Globals.PASSIVE_KEENEYE);
        }
        return Globals.rng(10000) + 1 < (int) (totalCritChance * 10000);
    }

    public double criticalDamage(final double dmg) {
        return criticalDamage(dmg, 0);
    }

    public double criticalDamage(final double dmg, final double bonusCritDmg) {
        double totalCritDmg = 1 + this.stats[Globals.STAT_CRITDMG] + bonusCritDmg;
        // Bow Mastery Passive
        if (hasSkill(Globals.PASSIVE_BOWMASTERY) && getSkill(Globals.PASSIVE_BOWMASTERY).canCast(this)) {
            double baseValue = getSkill(Globals.PASSIVE_BOWMASTERY).getBaseValue();
            double multValue = getSkill(Globals.PASSIVE_BOWMASTERY).getMultValue();
            totalCritDmg += baseValue + multValue * getSkillLevel(Globals.PASSIVE_BOWMASTERY);
        }
        // Keen Eye Passive
        if (hasSkill(Globals.PASSIVE_VITALHIT)) {
            double baseValue = getSkill(Globals.PASSIVE_VITALHIT).getBaseValue();
            double multValue = getSkill(Globals.PASSIVE_VITALHIT).getMultValue();
            totalCritDmg += baseValue + multValue * getSkillLevel(Globals.PASSIVE_VITALHIT);
        }
        return dmg * (totalCritDmg);
    }

    private void updateStats() {
        this.stats[Globals.STAT_ARMOUR] = Globals
                .calcArmour((int) (this.stats[Globals.STAT_DEFENSE] + this.bonusStats[Globals.STAT_DEFENSE]));
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
        this.stats[Globals.STAT_ARMOUR] = this.stats[Globals.STAT_ARMOUR] + this.bonusStats[Globals.STAT_ARMOUR];
        this.stats[Globals.STAT_DAMAGEREDUCT] = Globals.calcReduction(this.stats[Globals.STAT_ARMOUR]);
        this.stats[Globals.STAT_MAXEXP] = Globals.calcEXPtoNxtLvl(this.stats[Globals.STAT_LEVEL]);
    }

    public void giveEquipDrop(final double lvl, final boolean guaranteed) {
        giveEquipDrop(lvl, null, guaranteed);
    }

    public void giveEquipDrop(final double lvl, final Byte equipType, final boolean guaranteed) {
        if (Globals.rng((Integer) Globals.ServerConfig.EQUIP_DROP_RATE_ROLL.getValue()) < (Integer) Globals.ServerConfig.EQUIP_DROP_SUCCESS_RATE.getValue() || guaranteed) {
            final byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PLAYER_GIVEDROP;

            final byte[] level = Globals.intToBytes((int) lvl);
            System.arraycopy(level, 0, bytes, 1, level.length);

            // Fetch random item equip type table
            HashSet<Integer> equipsOfType;
            if (equipType != null) {
                equipsOfType = Globals.ITEM_EQUIP_TYPE_CODES_MAP.get(equipType);
            } else {
                equipsOfType = Globals.ITEM_EQUIP_TYPE_CODES_MAP.get((byte) Globals.rng(Globals.ITEM_EQUIP_TYPE_CODES_MAP.size()));
            }

            if (equipsOfType != null) {
                // Random code from type
                Integer[] equipCodes = equipsOfType.toArray(new Integer[equipsOfType.size()]);

                final byte[] itemCode = Globals.intToBytes(equipCodes[Globals.rng(equipCodes.length)]);
                System.arraycopy(itemCode, 0, bytes, 5, itemCode.length);

                PacketSender.sendPlayer(bytes, this);
            }
        }
    }

    public void giveUpgradeDrop(final double lvl, final boolean guaranteed) {
        if (Globals.rng((Integer) Globals.ServerConfig.UPGRADE_DROP_RATE_ROLL.getValue()) < (Integer) Globals.ServerConfig.UPGRADE_DROP_SUCCESS_RATE.getValue() || guaranteed) {
            final byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_INT * 2];
            bytes[0] = Globals.DATA_PLAYER_GIVEDROP;

            final byte[] level = Globals.intToBytes((int) lvl);
            System.arraycopy(level, 0, bytes, 1, level.length);

            Integer[] upgradeCodes = Globals.ITEM_UPGRADE_CODES.toArray(new Integer[Globals.ITEM_UPGRADE_CODES.size()]);
            final byte[] itemCode = Globals.intToBytes(upgradeCodes[Globals.rng(upgradeCodes.length)]);
            System.arraycopy(itemCode, 0, bytes, 5, itemCode.length);
            PacketSender.sendPlayer(bytes, this);
        }
    }

    public void giveDrop(final double lvl) {
        giveDrop(lvl, false);
    }

    public void giveDrop(final double lvl, final boolean guaranteed) {
        giveEquipDrop(lvl, guaranteed);
        giveUpgradeDrop(lvl, guaranteed);
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
        //Globals.log(Player.class, "Giving " + this.name + " " + (int) amount + " EXP", Globals.LOG_TYPE_DATA);
    }

    public boolean intersectHitbox(final Rectangle2D.Double box) {
        return this.hitbox.intersects(box);
    }

    public Rectangle2D.Double getHitbox() {
        return this.hitbox;
    }

    public boolean isStunned() {
        return hasBuff(BuffStun.class);
    }

    public boolean isKnockback() {
        return hasBuff(BuffKnockback.class);
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
            setYSpeed(-15 * this.ySpeedMultiplier);
        }
    }

    private void updateFall() {
        updateY(this.ySpeed);
        if (this.ySpeed != 0) {
            queuePlayerState(PLAYER_STATE_JUMP);
        }

        this.isJumping = this.ySpeed < 0;
        if (this.isFalling || this.isJumping) {
            setYSpeed(this.ySpeed + Globals.GRAVITY);
            if (this.ySpeed >= Globals.MAX_FALLSPEED) {
                setYSpeed(Globals.MAX_FALLSPEED);
            }
        }
        this.isFalling = this.map.isFalling(this.x, this.y, this.ySpeed);

        if (!this.isFalling) {
            if (this.ySpeed > 0) {
                setYSpeed(0);
            }
        }

    }

    private void updateMove(final boolean xChanged) {
        double totalXSpeed = Globals.WALK_SPEED * this.xSpeedMultiplier;
        if (this.dirKeydown[Globals.RIGHT] && !this.dirKeydown[Globals.LEFT]) {
            if (this.ySpeed == 0) {
                setXSpeed(totalXSpeed);
            } else {
                accelerateXSpeed(totalXSpeed);
            }
            if (xChanged && this.ySpeed == 0) {
                queuePlayerState(PLAYER_STATE_WALK);
            }
        } else if (this.dirKeydown[Globals.LEFT] && !this.dirKeydown[Globals.RIGHT]) {
            if (this.ySpeed == 0) {
                setXSpeed(-totalXSpeed);
            } else {
                accelerateXSpeed(-totalXSpeed);
            }
            if (xChanged && this.ySpeed == 0) {
                queuePlayerState(PLAYER_STATE_WALK);
            }
        } else if (this.ySpeed == 0) {
            setXSpeed(0);
        } else {
            accelerateXSpeed(0);
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
        this.lastActionTime = this.logic.getTime();
        if (!isDead() && !this.skills.get(data[3]).isPassive() && this.skills.get(data[3]).canCast(this) && !isUsingSkill()) {
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

    private void updateXAcceleration() {
        if (this.xSpeed > this.targetXSpeed) {
            this.xSpeed -= 0.15;
            if (this.xSpeed < this.targetXSpeed) {
                this.xSpeed = this.targetXSpeed;
            }
        } else if (this.xSpeed < this.targetXSpeed) {
            this.xSpeed += 0.15;
            if (this.xSpeed > this.targetXSpeed) {
                this.xSpeed = this.targetXSpeed;
            }
        }
    }

    private boolean updateX(final double change) {
        if (change == 0) {
            return false;
        }

        if (this.map.isOutOfBounds(this.x + change)) {
            return false;
        }
        this.x = this.map.getValidX(this.x + change, this.y);
        this.updatePos = true;
        return true;
    }

    private boolean updateY(final double change) {
        if (change == 0) {
            return false;
        }

        this.y = this.map.getValidY(this.x, this.y + change);
        this.updatePos = true;
        return true;
    }

    public void queuePlayerState(final byte newState) {
        this.nextState = newState;
    }

    public void setPlayerState(final byte newState) {
        this.playerState = newState;
        this.frame = 0;
        this.lastFrameTime = this.logic.getTime();
        this.updateAnimState = true;
    }

    private void updateAnimState() {
        final byte prevAnimState = this.animState, prevFrame = this.frame;
        final long frameDuration = Globals.nsToMs(this.logic.getTime() - this.lastFrameTime);
        switch (this.playerState) {
            case PLAYER_STATE_STAND:
                this.animState = Globals.PLAYER_ANIM_STATE_STAND;
                if (frameDuration >= 250) {
                    if (this.frame == 3) {
                        this.frame = 0;
                    } else {
                        this.frame++;
                    }
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_DEAD:
                this.animState = Globals.PLAYER_ANIM_STATE_DEAD;
                if ((this.frame == 2 && frameDuration >= 250) || (this.frame != 2 && frameDuration >= 50)) {
                    if (this.frame < 10) {
                        this.frame++;
                    }
                    this.lastFrameTime = this.logic.getTime();
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
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_JUMP:
                this.animState = Globals.PLAYER_ANIM_STATE_JUMP;
                if (frameDuration >= 60) {
                    if (this.isFalling && this.map.isWithinDistanceToGround(this.x, this.y, 110)) {
                        this.frame = 2;
                    } else if (this.frame < 1) {
                        this.frame++;
                    } else if (!this.isFalling && this.frame == 2) {
                        this.frame = 0;
                    }
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_SHIELD_CHARGE:
                this.animState = Globals.PLAYER_ANIM_STATE_ATTACK;
                if (frameDuration >= ((this.frame == 1) ? 4 : 20) && this.frame < 4) {
                    this.frame++;
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_SHIELD_REFLECT:
                this.animState = Globals.PLAYER_ANIM_STATE_BUFF;
                if (frameDuration >= 20 && this.frame < 4) {
                    this.frame++;
                }
                break;
            case PLAYER_STATE_SHIELD_ROAR:
                this.animState = Globals.PLAYER_ANIM_STATE_BUFF;
                if (frameDuration >= 30 && this.frame < 4) {
                    this.frame++;
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case PLAYER_STATE_SHIELD_MAGNETIZE:
                this.animState = Globals.PLAYER_ANIM_STATE_BUFF;
                if (getSkillCounter() == 1) {
                    this.frame = 0;
                } else if (frameDuration >= 30 && this.frame < 4) {
                    this.frame++;
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            default:
                if (getSkill(PLAYER_STATE_SKILLCODE.get(this.playerState)) != null) {
                    getSkill(PLAYER_STATE_SKILLCODE.get(this.playerState)).updatePlayerAnimState(this);
                }
        }
        if (this.animState != prevAnimState || this.frame != prevFrame) {
            this.updateAnimState = true;
        }
    }

    public void sendEmote(final byte emoteID) {
        this.lastActionTime = this.logic.getTime();
        if (this.logic.getTime() - this.lastEmoteTime >= Globals.msToNs(1000)) {
            final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
            bytes[0] = Globals.DATA_PLAYER_EMOTE;
            bytes[1] = this.key;
            bytes[2] = emoteID;
            PacketSender.sendAll(bytes, this.logic);
            this.lastEmoteTime = this.logic.getTime();
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

        PacketSender.sendAll(bytes, this.logic);
        this.updatePos = false;
        this.updateFacing = false;
        this.updateAnimState = false;
    }

    public byte[] getData() {
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

        return bytes;
    }

    public byte[] getPosData() {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 2];
        bytes[0] = this.key;
        final byte[] posXInt = Globals.intToBytes((int) this.x);
        System.arraycopy(posXInt, 0, bytes, 1, posXInt.length);

        final byte[] posYInt = Globals.intToBytes((int) this.y);
        System.arraycopy(posYInt, 0, bytes, 5, posYInt.length);

        bytes[9] = this.facing;
        this.updatePos = false;
        return bytes;
    }

    public void sendFacing() {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3];
        bytes[0] = Globals.DATA_PLAYER_SET_FACING;
        bytes[1] = this.key;
        bytes[2] = this.facing;
        PacketSender.sendAll(bytes, this.logic);
        this.updateFacing = false;
    }

    public byte[] getStateData() {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT * 2];
        bytes[0] = this.key;
        bytes[1] = this.animState;
        if (this.frame < 0) {
            bytes[2] = 0;
        } else {
            bytes[2] = this.frame;
        }
        final byte[] posXInt = Globals.intToBytes((int) this.x);
        System.arraycopy(posXInt, 0, bytes, 3, posXInt.length);

        final byte[] posYInt = Globals.intToBytes((int) this.y);
        System.arraycopy(posYInt, 0, bytes, 7, posYInt.length);

        this.updateAnimState = false;
        return bytes;
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
            pvpBytes[1] = (!dmg.isCrit()) ? Globals.NUMBER_TYPE_MOB : Globals.NUMBER_TYPE_MOBCRIT;
            PacketSender.sendPlayer(pvpBytes, this);
        } else {
            PacketSender.sendAll(bytes, this.logic);
        }
    }

    public void sendName() {
        final byte[] data = this.name.getBytes(StandardCharsets.UTF_8);
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + data.length];
        bytes[0] = Globals.DATA_PLAYER_GET_NAME;
        bytes[1] = this.key;
        System.arraycopy(data, 0, bytes, 2, data.length);
        PacketSender.sendAll(bytes, this.logic);
    }

    public void sendStat(final byte statID) {
        final byte[] stat = Globals.intToBytes((int) getStats()[statID]);
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 3 + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_PLAYER_GET_STAT;
        bytes[1] = this.key;
        bytes[2] = statID;
        System.arraycopy(stat, 0, bytes, 3, stat.length);
        PacketSender.sendAll(bytes, this.logic);
    }

    public void sendScore() {
        final byte[] scoreBytes = Globals.intToBytes(this.score);
        final byte[] ping = Globals.intToBytes(this.connection.getReturnTripTime());
        final byte[] timeLeft = Globals.intToBytes(this.logic.getMatchTimeRemaining());
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2 + Globals.PACKET_INT * 3];
        bytes[0] = Globals.DATA_PLAYER_SCORE;
        bytes[1] = this.key;
        System.arraycopy(scoreBytes, 0, bytes, 2, scoreBytes.length);
        System.arraycopy(ping, 0, bytes, 6, ping.length);
        System.arraycopy(timeLeft, 0, bytes, 10, ping.length);
        PacketSender.sendAll(bytes, this.logic);
        this.updateScore = false;
    }

    public void sendMatchResult(final Globals.VictoryStatus placing) {
        final byte[] bytes = new byte[Globals.PACKET_BYTE * 2];
        bytes[0] = Globals.DATA_PLAYER_MATCH_RESULT;
        bytes[1] = placing.getByteCode();
        PacketSender.sendPlayer(bytes, this);
        this.updateScore = false;
    }

    public void updateClientScore() {
        this.updateScore = true;
    }

    public void setInvulnerable(final boolean set) {
        this.isInvulnerable = set;
    }

    public boolean isInvulnerable() {
        return this.isInvulnerable;
    }

    public void setHyperStance(final boolean set) {
        this.isHyperStance = set;
    }

    public boolean isHyperStance() {
        return this.isHyperStance;
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
            PacketSender.sendAll(bytes, this.logic);
            this.connection.close();
            Globals.log(Player.class, "Disconnected <" + getPlayerName() + ">", Globals.LOG_TYPE_DATA);
        }
    }

    public boolean isConnected() {
        return this.connected;
    }

}
