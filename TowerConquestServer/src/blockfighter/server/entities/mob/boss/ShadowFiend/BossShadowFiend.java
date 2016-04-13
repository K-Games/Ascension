package blockfighter.server.entities.mob.boss.ShadowFiend;

import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.maps.GameMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Debug_Lightning boss Work in progress
 *
 * @author Ken
 */
public class BossShadowFiend extends Mob {

    private static final byte NUM_SKILLS = 4;
    public static final byte SKILL_RAZE = 0x00, SKILL_ARMSWING = 0x01, SKILL_DARKLINES = 0x02, SKILL_ORBS = 0x03;

    public static final byte ANIM_RAZE = 0x05,
            ANIM_ARMSWING = 0x06,
            ANIM_DARKLINES = 0x07,
            ANIM_ORBS = 0x08;

    public static final byte STATE_RAZE = 0x05,
            STATE_ARMSWING = 0x06,
            STATE_DARKLINES = 0x07,
            STATE_ORBS = 0x08;

    public BossShadowFiend(final LogicModule l, final GameMap map, final double x, final double y) {
        super(l, map, x, y, NUM_SKILLS);
        this.type = MOB_BOSS_LIGHTNING;
        this.stats = new double[NUM_STATS];
        this.stats[STAT_LEVEL] = l.getRoom();
        this.stats[STAT_MAXHP] = 1000000 * Math.pow(1.09, this.stats[STAT_LEVEL]);
        this.stats[STAT_MINHP] = this.stats[STAT_MAXHP];
        this.hitbox.width = 330;
        this.hitbox.height = 280;

        super.addValidMobSkillState(STATE_RAZE);
        super.addValidMobSkillState(STATE_ARMSWING);
        super.addValidMobSkillState(STATE_DARKLINES);
        super.addValidMobSkillState(STATE_ORBS);

        //super.addSkill(SKILL_ATT2, new SkillAttack2(this.logic));
        //this.logic.queueAddProj(new ProjTouch(this.logic, this));
    }

    @Override
    public void update() {
        if (this.isDead) {
            return;
        }

        // Update Timers/Game principles(Gravity)
        updateBuffs();

        updateFall();
        updateX(this.xSpeed);
        this.hitbox.x = this.x - this.hitbox.width / 2;
        this.hitbox.y = this.y - this.hitbox.height;

        // Update Actions
        if (isStunned()) {
            setXSpeed(0);
            queueMobState(STATE_STAND);
        } else if (this.aggroCounter.isEmpty()) {
            // No aggro, just sit there.
            setXSpeed(0);
            queueMobState(STATE_STAND);
        } else {
            updateAI();
        }

        updateMobState();
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
            queueMobState(STATE_STAND);
            return;
        }
        // Face your target
        if (t.getX() > this.x) {
            setFacing(Globals.RIGHT);
        } else {
            setFacing(Globals.LEFT);
        }

        if (Math.abs(this.x - t.getX()) > 450) {
            queueMobState(STATE_WALK);
        } else {
            queueMobState(STATE_STAND);
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

        int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        switch (this.mobState) {
            case STATE_STAND:
                nextAIstate(t);
                setXSpeed(0);
                break;
            case STATE_WALK:
                nextAIstate(t);
                if (t != null && t.getX() > this.x) {
                    setXSpeed(3);
                } else {
                    setXSpeed(-3);
                }
                break;
        }
    }

    private void updateAnimState() {
        final byte prevAnimState = this.animState, prevFrame = this.frame;

        final int duration = Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
        final int frameDuration = Globals.nsToMs(this.logic.getTime() - this.lastFrameTime);
        switch (this.mobState) {
            case STATE_STAND:
                this.animState = STATE_STAND;
                if (frameDuration >= 40) {
                    this.frame++;
                    if (this.frame == 10) {
                        this.frame = 0;
                    }
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
            case STATE_WALK:
                this.animState = STATE_STAND;
                if (frameDuration >= 40) {
                    this.frame++;
                    if (this.frame == 10) {
                        this.frame = 0;
                    }
                    this.lastFrameTime = this.logic.getTime();
                }
                break;
        }
        if (this.animState != prevAnimState || this.frame != prevFrame) {
            this.updateAnimState = true;
        }
    }
}
