package blockfighter.server.entities.boss.Lightning;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.player.Player;
import blockfighter.server.maps.GameMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Debug_Lightning boss Work in progress
 *
 * @author Ken
 */
public class BossLightning extends Boss {

    public static final byte SKILL_BOLT = 0x00, SKILL_BALL = 0x01, SKILL_ATT1 = 0x02, SKILL_ATT2 = 0x03;
    public static final byte PARTICLE_ATT1 = 0x00,
            PARTICLE_ATT2 = 0x01,
            PARTICLE_BOLT = 0x02,
            PARTICLE_BALL1 = 0x03,
            PARTICLE_BALL2 = 0x04;

    public static final byte STATE_BOLTCHARGE = 0x03,
            STATE_BALLCHARGE = 0x04,
            STATE_ATTACK1 = 0x05,
            STATE_ATTACK2 = 0x06,
            STATE_BOLTCAST = 0x07,
            STATE_AI_ATTACK1 = 0x08,
            STATE_AI_ATTACK2 = 0x09,
            STATE_AI_BOLT = 0x0A,
            STATE_AI_BALL = 0x0B;

    public BossLightning(final LogicModule l, final byte key, final GameMap map, final double x, final double y) {
        super(l, key, map, x, y);
        this.type = BOSS_LIGHTNING;
        this.stats = new double[NUM_STATS];
        this.stats[STAT_LEVEL] = l.getRoom();
        this.stats[STAT_MAXHP] = 1000000 * Math.pow(1.09, this.stats[STAT_LEVEL]);
        this.stats[STAT_MINHP] = this.stats[STAT_MAXHP];
        this.hitbox.width = 330;
        this.hitbox.height = 270;
        addSkill(SKILL_BOLT, new SkillBolt());
        addSkill(SKILL_BALL, new SkillBall());
        addSkill(SKILL_ATT1, new SkillAttack1());
        addSkill(SKILL_ATT2, new SkillAttack2());
        this.logic.queueAddProj(new ProjTouch(this.logic, this.logic.getNextProjKey(), this));
    }

    @Override
    public boolean isUsingSkill() {
        return this.bossState == STATE_AI_ATTACK1
                || this.bossState == STATE_AI_ATTACK2
                || this.bossState == STATE_AI_BOLT
                || this.bossState == STATE_AI_BALL;
    }

    @Override
    public void update() {
        if (this.isDead) {
            return;
        }
        this.nextHPSend -= Globals.LOGIC_UPDATE / 1000000;

        if (isUsingSkill()) {
            this.skillDuration += Globals.LOGIC_UPDATE / 1000000;
        }
        // Update Timers/Game principles(Gravity)
        updateSkillCd();
        updateBuffs();

        updateFall();
        updateX(this.xSpeed);
        this.hitbox.x = this.x - this.hitbox.width / 2;
        this.hitbox.y = this.y - this.hitbox.height;

        // Update Actions
        if (isStunned()) {
            setXSpeed(0);
            queueBossState(STATE_STAND);
        } else // Update AI
         if (this.aggroCounter.isEmpty()) {
                // No aggro, just sit there.
                queueBossState(STATE_STAND);
            } else {
                updateAI();
            }

        updateBossState();
        updateHP();
        updateAnimState();
        if (this.updatePos) {
            sendPos();
        }
        if (this.updateFacing) {
            sendFacing();
        }
        if (this.updateAnimState) {
            sendState();
        }
    }

    @Override
    public Player getTarget() {
        Player target = null;
        double maxAggro = 0;
        final LinkedList<Player> remove = new LinkedList<>();
        for (final Map.Entry<Player, Double> p : this.aggroCounter.entrySet()) {
            if (!p.getKey().isConnected() || p.getKey().isDead()) {
                remove.add(p.getKey());
                continue;
            }
            if (p.getValue() > maxAggro) {
                maxAggro = p.getValue();
                target = p.getKey();
            }
        }
        while (!remove.isEmpty()) {
            this.aggroCounter.remove(remove.poll());
        }
        return target;
    }

    private void nextAIstate(final Player t) {
        if (t == null) {
            queueBossState(STATE_STAND);
            return;
        }
        // Face your target
        if (t.getX() > this.x) {
            setFacing(Globals.RIGHT);
        } else {
            setFacing(Globals.LEFT);
        }

        if (canCast(SKILL_BOLT)) {
            queueBossState(STATE_AI_BOLT);
            setCooldown(SKILL_BOLT);
            this.skillDuration = 0;
        } else if (canCast(SKILL_BALL)) {
            queueBossState(STATE_AI_BALL);
            setCooldown(SKILL_BALL);
            this.skillDuration = 0;
        } else if (Math.abs(this.x - t.getX()) > 450) {
            queueBossState(STATE_WALK);
        } else if (canCast(SKILL_ATT1)) {
            queueBossState(STATE_AI_ATTACK1);
            setCooldown(SKILL_ATT1);
            this.skillDuration = 0;
        } else if (canCast(SKILL_ATT2)) {
            queueBossState(STATE_AI_ATTACK2);
            setCooldown(SKILL_ATT2);
            this.skillDuration = 0;
        } else {
            queueBossState(STATE_STAND);
        }
    }

    private void updateAI() {
        final Player t = getTarget();
        byte phase = 0;
        final double hp = this.stats[STAT_MINHP] / this.stats[STAT_MAXHP];
        if (hp < 0.33) {
            phase = 2;
        } else if (hp < 0.66) {
            phase = 1;
        }
        switch (this.bossState) {
            case STATE_STAND:
                nextAIstate(t);
                break;
            case STATE_WALK:
                nextAIstate(t);
                if (t != null && t.getX() > this.x) {
                    setXSpeed(3);
                } else {
                    setXSpeed(-3);
                }
                break;
            case STATE_AI_BOLT:
                setXSpeed(0);
                if (this.skillDuration == 1100) {
                    ProjBolt proj;
                    byte count;
                    switch (phase) {
                        case 0:
                            proj = new ProjBolt(this.logic, this.logic.getNextProjKey(), this, t.getX(), t.getY());
                            this.logic.queueAddProj(proj);
                            sendBossParticle(this.key, this.logic.getRoom(), PARTICLE_BOLT, proj.getHitbox()[0].getX(),
                                    proj.getHitbox()[0].getY());
                            break;
                        case 1:
                            count = 0;
                            for (final Map.Entry<Player, Double> player : this.aggroCounter.entrySet()) {
                                if (count == 2) {
                                    break;
                                }
                                proj = new ProjBolt(this.logic, this.logic.getNextProjKey(), this, player.getKey().getX(),
                                        player.getKey().getY());
                                this.logic.queueAddProj(proj);
                                sendBossParticle(this.key, this.logic.getRoom(), PARTICLE_BOLT, proj.getHitbox()[0].getX(),
                                        proj.getHitbox()[0].getY());
                            }
                            break;
                        case 2:
                            for (final Map.Entry<Player, Double> player : this.aggroCounter.entrySet()) {
                                proj = new ProjBolt(this.logic, this.logic.getNextProjKey(), this, player.getKey().getX(),
                                        player.getKey().getY());
                                this.logic.queueAddProj(proj);
                                sendBossParticle(this.key, this.logic.getRoom(), PARTICLE_BOLT, proj.getHitbox()[0].getX(),
                                        proj.getHitbox()[0].getY());
                                reduceCooldown(SKILL_BOLT, 5000);
                            }
                            break;
                    }
                }
                if (this.skillDuration == 2000) {
                    queueBossState(STATE_STAND);
                }
                break;
            case STATE_AI_BALL:
                setXSpeed(0);
                if (this.skillDuration == 1100) {
                    final ProjBall proj = new ProjBall(this.logic, this.logic.getNextProjKey(), this, this.x, this.y);
                    this.logic.queueAddProj(proj);
                    sendBossParticle(this.key, this.logic.getRoom(), PARTICLE_BALL1, proj.getHitbox()[0].getX(),
                            proj.getHitbox()[0].getY());
                }
                if ((phase == 1 || phase == 2) && this.skillDuration == 1550) {
                    final ProjBall proj = new ProjBall(this.logic, this.logic.getNextProjKey(), this, this.x, this.y);
                    this.logic.queueAddProj(proj);
                    sendBossParticle(this.key, this.logic.getRoom(), PARTICLE_BALL1, proj.getHitbox()[0].getX(),
                            proj.getHitbox()[0].getY());
                    reduceCooldown(SKILL_BALL, 2000);
                }
                if (phase == 2 && this.skillDuration == 2000) {
                    final ProjBall proj = new ProjBall(this.logic, this.logic.getNextProjKey(), this, this.x, this.y);
                    this.logic.queueAddProj(proj);
                    sendBossParticle(this.key, this.logic.getRoom(), PARTICLE_BALL1, proj.getHitbox()[0].getX(),
                            proj.getHitbox()[0].getY());
                    reduceCooldown(SKILL_BALL, 1000);
                }
                if (this.skillDuration == 2300) {
                    queueBossState(STATE_STAND);
                }
                break;
            case STATE_AI_ATTACK1:
                setXSpeed(0);
                if (this.skillDuration == 50) {
                    final ProjAttack proj = new ProjAttack(this.logic, this.logic.getNextProjKey(), this, this.x, this.y);
                    this.logic.queueAddProj(proj);
                    sendBossParticle(this.key, this.logic.getRoom(), PARTICLE_ATT1, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY());
                    if (phase == 1) {
                        reduceCooldown(SKILL_ATT1, 500);
                    } else if (phase == 2) {
                        reduceCooldown(SKILL_ATT1, 1000);
                    }
                }
                if (this.skillDuration == 400) {
                    queueBossState(STATE_STAND);
                }
                break;
            case STATE_AI_ATTACK2:
                setXSpeed(0);
                if (this.skillDuration == 50) {
                    final ProjAttack proj = new ProjAttack(this.logic, this.logic.getNextProjKey(), this, this.x, this.y);
                    this.logic.queueAddProj(proj);
                    sendBossParticle(this.key, this.logic.getRoom(), PARTICLE_ATT1, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY());
                    if (phase == 1) {
                        reduceCooldown(SKILL_ATT2, 500);
                    } else if (phase == 2) {
                        reduceCooldown(SKILL_ATT2, 1000);
                    }
                }
                if (this.skillDuration == 400) {
                    queueBossState(STATE_STAND);
                }
                break;
        }
    }

    private void updateAnimState() {
        final byte prevAnimState = this.animState, prevFrame = this.frame;
        switch (this.bossState) {
            case STATE_STAND:
                this.nextFrameTime -= Globals.LOGIC_UPDATE;
                this.animState = STATE_STAND;
                if (this.nextFrameTime <= 0) {
                    this.frame++;
                    if (this.frame == 10) {
                        this.frame = 0;
                    }
                    this.nextFrameTime = 40000000;
                }
                break;
            case STATE_WALK:
                this.nextFrameTime -= Globals.LOGIC_UPDATE;
                this.animState = STATE_STAND;
                if (this.nextFrameTime <= 0) {
                    this.frame++;
                    if (this.frame == 10) {
                        this.frame = 0;
                    }
                    this.nextFrameTime = 40000000;
                }
                break;
            case STATE_AI_BOLT:
                this.nextFrameTime -= Globals.LOGIC_UPDATE;
                if (this.nextFrameTime <= 0) {
                    if (this.skillDuration < 1000) {
                        this.animState = STATE_BOLTCHARGE;
                        this.frame++;
                        if (this.frame == 10) {
                            this.frame = 0;
                        }
                    } else {
                        this.animState = STATE_BOLTCAST;
                        if (this.frame != 9) {
                            this.frame++;
                        }
                    }
                    if (this.skillDuration == 1000) {
                        this.frame = 0;
                    }
                    this.nextFrameTime = 30000000;
                }
                break;
            case STATE_AI_BALL:
                this.nextFrameTime -= Globals.LOGIC_UPDATE;
                if (this.nextFrameTime <= 0) {
                    if (this.skillDuration < 1000) {
                        this.animState = STATE_BALLCHARGE;
                        this.frame++;
                        if (this.frame == 10) {
                            this.frame = 0;
                        }
                    } else {
                        this.animState = STATE_ATTACK2;
                        if (this.frame < 9) {
                            this.frame++;
                        }
                    }
                    if (this.skillDuration == 1000) {
                        this.frame = 0;
                    }
                    this.nextFrameTime = 30000000;
                }
                break;
            case STATE_AI_ATTACK1:
                this.nextFrameTime -= Globals.LOGIC_UPDATE;
                this.animState = STATE_ATTACK1;
                if (this.nextFrameTime <= 0) {
                    if (this.frame < 9) {
                        this.frame++;
                    }
                    this.nextFrameTime = 20000000;
                }
                break;
            case STATE_AI_ATTACK2:
                this.nextFrameTime -= Globals.LOGIC_UPDATE;
                this.animState = STATE_ATTACK2;
                if (this.nextFrameTime <= 0) {
                    if (this.frame < 9) {
                        this.frame++;
                    }
                    this.nextFrameTime = 20000000;
                }
                break;
        }
        if (this.animState != prevAnimState || this.frame != prevFrame) {
            this.updateAnimState = true;
        }
    }
}
