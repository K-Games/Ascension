package blockfighter.server.entities.damage;

import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.SkillPassiveShadowAttack;
import blockfighter.shared.Globals;
import java.awt.geom.Point2D;

public class Damage {

    private static final int RANDOM_DELTA = 40, RANDOM_CONST = -20;

    private final byte type;
    private final int damage;
    private boolean canProc = false,
            isTrueDamage = false,
            isCrit = false,
            canReflect = true,
            isHidden = false,
            showParticle = false;

    private Player owner, target;
    private Mob mobOwner, mobTarget;
    private final Point2D.Double dmgPoint;

    public Damage(final int dmg, final boolean canProc, final Player o, final Player t, final boolean isCrit, final Point2D.Double p, final boolean showParticle) {
        this.damage = dmg;
        this.canProc = canProc;
        this.owner = o;
        this.target = t;
        this.dmgPoint = p;
        this.isCrit = isCrit;
        this.type = (this.isCrit) ? Globals.NUMBER_TYPE_PLAYERCRIT : Globals.NUMBER_TYPE_PLAYER;
        this.showParticle = showParticle;
    }

    public Damage(final int dmg, final boolean isTrueDamage, final Mob o, final Player t, final Point2D.Double p, final boolean showParticle) {
        this.damage = dmg;
        this.isTrueDamage = isTrueDamage;
        this.mobOwner = o;
        this.target = t;
        this.dmgPoint = p;
        this.type = Globals.NUMBER_TYPE_MOB;
        this.showParticle = showParticle;
    }

    public Damage(final int dmg, final boolean canProc, final Player o, final Mob t, final boolean isCrit, final Point2D.Double p, final boolean showParticle) {
        this.damage = dmg;
        this.canProc = canProc;
        this.owner = o;
        this.mobTarget = t;
        this.dmgPoint = p;
        this.isCrit = isCrit;
        this.type = (this.isCrit) ? Globals.NUMBER_TYPE_PLAYERCRIT : Globals.NUMBER_TYPE_PLAYER;
        this.showParticle = showParticle;
    }

    public Damage(final int dmg, final Player o, final Player t, final boolean showParticle) {
        this(dmg, false, o, t, false, new Point2D.Double(t.getHitbox().getCenterX() + Globals.rng(RANDOM_DELTA) + RANDOM_CONST, t.getHitbox().getCenterY() - 20 + Globals.rng(RANDOM_DELTA) + RANDOM_CONST), showParticle);
    }

    public Damage(final int dmg, final boolean canProc, final Player o, final Player t, final boolean isCrit, final boolean showParticle) {
        this(dmg, canProc, o, t, isCrit, new Point2D.Double(t.getHitbox().getCenterX() + Globals.rng(RANDOM_DELTA) + RANDOM_CONST, t.getHitbox().getCenterY() - 20 + Globals.rng(RANDOM_DELTA) + RANDOM_CONST), showParticle);
    }

    public Damage(final int dmg, final Player o, final Mob t, final boolean showParticle) {
        this(dmg, false, o, t, false, new Point2D.Double(t.getHitbox().getCenterX() + Globals.rng(RANDOM_DELTA) + RANDOM_CONST, t.getHitbox().getCenterY() - 20 + Globals.rng(RANDOM_DELTA) + RANDOM_CONST), showParticle);
    }

    public Damage(final int dmg, final Player o, final Mob t, final boolean isCrit, final boolean showParticle) {
        this(dmg, true, o, t, isCrit, new Point2D.Double(t.getHitbox().getCenterX() + Globals.rng(RANDOM_DELTA) + RANDOM_CONST, t.getHitbox().getCenterY() - 20 + Globals.rng(RANDOM_DELTA) + RANDOM_CONST), showParticle);
    }

    public Damage(final int dmg, final boolean canProc, final Player o, final Mob t, final boolean isCrit, final boolean showParticle) {
        this(dmg, canProc, o, t, isCrit, new Point2D.Double(t.getHitbox().getCenterX() + Globals.rng(RANDOM_DELTA) + RANDOM_CONST, t.getHitbox().getCenterY() - 20 + Globals.rng(RANDOM_DELTA) + RANDOM_CONST), showParticle);
    }

    public Damage(final int dmg, final Player o, final Mob t, final boolean isCrit, final Point2D.Double p, final boolean showParticle) {
        this(dmg, true, o, t, isCrit, p, showParticle);
    }

    public Damage(final int dmg, final Mob o, final Player t, final Point2D.Double p, final boolean showParticle) {
        this(dmg, false, o, t, p, showParticle);
    }

    public Damage(final int dmg, final Mob o, final Player t, final boolean showParticle) {
        this(dmg, false, o, t, new Point2D.Double(t.getHitbox().getCenterX() + Globals.rng(RANDOM_DELTA) + RANDOM_CONST, t.getHitbox().getCenterY() - 20 + Globals.rng(RANDOM_DELTA) + RANDOM_CONST), showParticle);
    }

    public Damage(final int dmg, final boolean isTrueDamage, final Mob o, final Player t, final boolean showParticle) {
        this(dmg, isTrueDamage, o, t, new Point2D.Double(t.getHitbox().getCenterX() + Globals.rng(RANDOM_DELTA) + RANDOM_CONST, t.getHitbox().getCenterY() - 20 + Globals.rng(RANDOM_DELTA) + RANDOM_CONST), showParticle);
    }

    public int getDamage() {
        return this.damage;
    }

    public Mob getMobOwner() {
        return this.mobOwner;
    }

    public Player getOwner() {
        return this.owner;
    }

    public Mob getMobTarget() {
        return this.mobTarget;
    }

    public Player getTarget() {
        return this.target;
    }

    public boolean isTrueDamage() {
        return this.isTrueDamage;
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    public boolean canProc() {
        return this.canProc;
    }

    public Point2D.Double getDmgPoint() {
        return this.dmgPoint;
    }

    public byte getDamageType() {
        return this.type;
    }

    public void proc() {
        if (this.canProc && this.owner != null) {
            if (this.owner.hasSkill(Globals.PASSIVE_SHADOWATTACK) && this.owner.getSkill(Globals.PASSIVE_SHADOWATTACK).canCast()) {
                ((SkillPassiveShadowAttack) this.owner.getSkill(Globals.PASSIVE_SHADOWATTACK)).updateSkillUse(this.owner, this);
            }

            if (this.owner.hasSkill(Globals.PASSIVE_STATIC)) {
                this.owner.getSkill(Globals.PASSIVE_STATIC).updateSkillUse(this.owner);
            }
        }
    }

    public void setCanReflect(final boolean set) {
        this.canReflect = set;
    }

    public void setHidden(final boolean set) {
        this.isHidden = set;
    }

    public boolean canReflect() {
        return this.canReflect;
    }

    public boolean isCrit() {
        return this.isCrit;
    }

    public boolean showParticle() {
        return this.showParticle;
    }
}
