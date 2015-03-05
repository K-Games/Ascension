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

    private long touchDamageTime = 0;

    public BossLightning(LogicModule l, byte key, GameMap map, double x, double y) {
        super(l, key, map, x, y);
        type = BOSS_LIGHTNING;
        stats = new double[NUM_STATS];
        stats[STAT_LEVEL] = l.getRoom();
        stats[STAT_MAXHP] = 1000000 * Math.pow(1.09, stats[STAT_LEVEL]);
        stats[STAT_MINHP] = stats[STAT_MAXHP];
        hitbox.width = 330;
        hitbox.height = 270;
        addSkill(SKILL_BOLT, new SkillBolt());
        addSkill(SKILL_BALL, new SkillBall());
        addSkill(SKILL_ATT1, new SkillAttack1());
        addSkill(SKILL_ATT2, new SkillAttack2());
    }

    @Override
    public boolean isUsingSkill() {
        return bossState == STATE_AI_ATTACK1
                || bossState == STATE_AI_ATTACK2
                || bossState == STATE_AI_BOLT
                || bossState == STATE_AI_BALL;
    }

    @Override
    public void update() {
        if (isDead) {
            return;
        }
        nextHPSend -= Globals.LOGIC_UPDATE / 1000000;
        touchDamageTime -= Globals.LOGIC_UPDATE / 1000000;
        if (touchDamageTime <= 0) {
            ProjTouch proj = new ProjTouch(logic, logic.getNextProjKey(), this);
            logic.queueAddProj(proj);
            touchDamageTime = 500;
        }

        if (isUsingSkill()) {
            skillDuration += Globals.LOGIC_UPDATE / 1000000;
        }
        //Update Timers/Game principles(Gravity)
        updateSkillCd();
        updateBuffs();

        updateFall();
        updateX(xSpeed);
        hitbox.x = x - hitbox.width / 2;
        hitbox.y = y - hitbox.height;

        //Update Actions
        if (isStunned()) {
            setXSpeed(0);
            queueBossState(STATE_STAND);
        } else {
            //Update AI
            if (aggroCounter.isEmpty()) {
                //No aggro, just sit there.
                queueBossState(STATE_STAND);
            } else {
                updateAI();
            }
        }

        updateBossState();
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
    }

    @Override
    public Player getTarget() {
        Player target = null;
        double maxAggro = 0;
        LinkedList<Player> remove = new LinkedList<>();
        for (Map.Entry<Player, Double> p : aggroCounter.entrySet()) {
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
            aggroCounter.remove(remove.poll());
        }
        return target;
    }

    private void nextAIstate(Player t) {
        if (t == null) {
            queueBossState(STATE_STAND);
            return;
        }
        //Face your target
        if (t.getX() > x) {
            setFacing(Globals.RIGHT);
        } else {
            setFacing(Globals.LEFT);
        }

        if (canCast(SKILL_BOLT)) {
            queueBossState(STATE_AI_BOLT);
            setCooldown(SKILL_BOLT);
            skillDuration = 0;
        } else if (canCast(SKILL_BALL)) {
            queueBossState(STATE_AI_BALL);
            setCooldown(SKILL_BALL);
            skillDuration = 0;
        } else if (Math.abs(x - t.getX()) > 450) {
            queueBossState(STATE_WALK);
        } else if (canCast(SKILL_ATT1)) {
            queueBossState(STATE_AI_ATTACK1);
            setCooldown(SKILL_ATT1);
            skillDuration = 0;
        } else if (canCast(SKILL_ATT2)) {
            queueBossState(STATE_AI_ATTACK2);
            setCooldown(SKILL_ATT2);
            skillDuration = 0;
        } else {
            queueBossState(STATE_STAND);
        }
    }

    private void updateAI() {
        Player t = getTarget();
        byte phase = 0;
        double hp = stats[STAT_MINHP] / stats[STAT_MAXHP];
        if (hp < 0.33) {
            phase = 2;
        } else if (hp < 0.66) {
            phase = 1;
        }
        switch (bossState) {
            case STATE_STAND:
                nextAIstate(t);
                break;
            case STATE_WALK:
                nextAIstate(t);
                if (t != null && t.getX() > x) {
                    setXSpeed(3);
                } else {
                    setXSpeed(-3);
                }
                break;
            case STATE_AI_BOLT:
                setXSpeed(0);
                if (skillDuration == 1100) {
                    ProjBolt proj;
                    byte count;
                    switch (phase) {
                        case 0:
                            proj = new ProjBolt(logic, logic.getNextProjKey(), this, t.getX(), t.getY());
                            logic.queueAddProj(proj);
                            sendBossParticle(key, logic.getRoom(), PARTICLE_BOLT, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY());
                            break;
                        case 1:
                            count = 0;
                            for (Map.Entry<Player, Double> player : aggroCounter.entrySet()) {
                                if (count == 2) {
                                    break;
                                }
                                proj = new ProjBolt(logic, logic.getNextProjKey(), this, player.getKey().getX(), player.getKey().getY());
                                logic.queueAddProj(proj);
                                sendBossParticle(key, logic.getRoom(), PARTICLE_BOLT, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY());
                            }
                            break;
                        case 2:
                            for (Map.Entry<Player, Double> player : aggroCounter.entrySet()) {
                                proj = new ProjBolt(logic, logic.getNextProjKey(), this, player.getKey().getX(), player.getKey().getY());
                                logic.queueAddProj(proj);
                                sendBossParticle(key, logic.getRoom(), PARTICLE_BOLT, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY());
                                reduceCooldown(SKILL_BOLT, 5000);
                            }
                            break;
                    }
                }
                if (skillDuration == 2000) {
                    queueBossState(STATE_STAND);
                }
                break;
            case STATE_AI_BALL:
                setXSpeed(0);
                if (skillDuration == 1100) {
                    ProjBall proj = new ProjBall(logic, logic.getNextProjKey(), this, x, y);
                    logic.queueAddProj(proj);
                    sendBossParticle(key, logic.getRoom(), PARTICLE_BALL1, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY());
                }
                if ((phase == 1 || phase == 2) && skillDuration == 1550) {
                    ProjBall proj = new ProjBall(logic, logic.getNextProjKey(), this, x, y);
                    logic.queueAddProj(proj);
                    sendBossParticle(key, logic.getRoom(), PARTICLE_BALL1, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY());
                    reduceCooldown(SKILL_BALL, 2000);
                }
                if (phase == 2 && skillDuration == 2000) {
                    ProjBall proj = new ProjBall(logic, logic.getNextProjKey(), this, x, y);
                    logic.queueAddProj(proj);
                    sendBossParticle(key, logic.getRoom(), PARTICLE_BALL1, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY());
                    reduceCooldown(SKILL_BALL, 1000);
                }
                if (skillDuration == 2300) {
                    queueBossState(STATE_STAND);
                }
                break;
            case STATE_AI_ATTACK1:
                setXSpeed(0);
                if (skillDuration == 50) {
                    ProjAttack proj = new ProjAttack(logic, logic.getNextProjKey(), this, x, y);
                    logic.queueAddProj(proj);
                    sendBossParticle(key, logic.getRoom(), PARTICLE_ATT1, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY());
                    if (phase == 1) {
                        reduceCooldown(SKILL_ATT1, 500);
                    } else if (phase == 2) {
                        reduceCooldown(SKILL_ATT1, 1000);
                    }
                }
                if (skillDuration == 400) {
                    queueBossState(STATE_STAND);
                }
                break;
            case STATE_AI_ATTACK2:
                setXSpeed(0);
                if (skillDuration == 50) {
                    ProjAttack proj = new ProjAttack(logic, logic.getNextProjKey(), this, x, y);
                    logic.queueAddProj(proj);
                    sendBossParticle(key, logic.getRoom(), PARTICLE_ATT1, proj.getHitbox()[0].getX(), proj.getHitbox()[0].getY());
                    if (phase == 1) {
                        reduceCooldown(SKILL_ATT2, 500);
                    } else if (phase == 2) {
                        reduceCooldown(SKILL_ATT2, 1000);
                    }
                }
                if (skillDuration == 400) {
                    queueBossState(STATE_STAND);
                }
                break;
        }
    }

    private void updateAnimState() {
        byte prevAnimState = animState, prevFrame = frame;
        switch (bossState) {
            case STATE_STAND:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = STATE_STAND;
                if (nextFrameTime <= 0) {
                    frame++;
                    if (frame == 10) {
                        frame = 0;
                    }
                    nextFrameTime = 40000000;
                }
                break;
            case STATE_WALK:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = STATE_STAND;
                if (nextFrameTime <= 0) {
                    frame++;
                    if (frame == 10) {
                        frame = 0;
                    }
                    nextFrameTime = 40000000;
                }
                break;
            case STATE_AI_BOLT:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                if (nextFrameTime <= 0) {
                    if (skillDuration < 1000) {
                        animState = STATE_BOLTCHARGE;
                        frame++;
                        if (frame == 10) {
                            frame = 0;
                        }
                    } else {
                        animState = STATE_BOLTCAST;
                        if (frame != 9) {
                            frame++;
                        }
                    }
                    if (skillDuration == 1000) {
                        frame = 0;
                    }
                    nextFrameTime = 30000000;
                }
                break;
            case STATE_AI_BALL:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                if (nextFrameTime <= 0) {
                    if (skillDuration < 1000) {
                        animState = STATE_BALLCHARGE;
                        frame++;
                        if (frame == 10) {
                            frame = 0;
                        }
                    } else {
                        animState = STATE_ATTACK2;
                        if (frame < 9) {
                            frame++;
                        }
                    }
                    if (skillDuration == 1000) {
                        frame = 0;
                    }
                    nextFrameTime = 30000000;
                }
                break;
            case STATE_AI_ATTACK1:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = STATE_ATTACK1;
                if (nextFrameTime <= 0) {
                    if (frame < 9) {
                        frame++;
                    }
                    nextFrameTime = 20000000;
                }
                break;
            case STATE_AI_ATTACK2:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                animState = STATE_ATTACK2;
                if (nextFrameTime <= 0) {
                    if (frame < 9) {
                        frame++;
                    }
                    nextFrameTime = 20000000;
                }
                break;
        }
        if (animState != prevAnimState || frame != prevFrame) {
            updateAnimState = true;
        }
    }
}
