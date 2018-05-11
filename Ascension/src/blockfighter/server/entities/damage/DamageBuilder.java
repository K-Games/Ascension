package blockfighter.server.entities.damage;

import blockfighter.server.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.geom.Point2D;

public class DamageBuilder {

    private static final int RANDOM_DELTA = 40, RANDOM_CONST = -20;

    private Byte type;
    private int damage;
    private boolean canProc = true,
            isTrueDamage = false,
            isCrit = false,
            canReflect = true,
            isHidden = false,
            showParticle = true;

    private Player owner, target;
    private Point2D.Double dmgPoint;

    public DamageBuilder setType(final byte type) {
        this.type = type;
        return this;
    }

    public DamageBuilder setDamage(final int damage) {
        this.damage = damage;
        return this;
    }

    public DamageBuilder setCanProc(final boolean canProc) {
        this.canProc = canProc;
        return this;
    }

    public DamageBuilder setIsTrueDamage(final boolean isTrueDamage) {
        this.isTrueDamage = isTrueDamage;
        return this;
    }

    public DamageBuilder setIsCrit(final boolean isCrit) {
        this.isCrit = isCrit;
        return this;
    }

    public DamageBuilder setCanReflect(final boolean canReflect) {
        this.canReflect = canReflect;
        return this;
    }

    public DamageBuilder setIsHidden(final boolean isHidden) {
        this.isHidden = isHidden;
        return this;
    }

    public DamageBuilder setShowParticle(final boolean showParticle) {
        this.showParticle = showParticle;
        return this;
    }

    public DamageBuilder setOwner(final Player owner) {
        this.owner = owner;
        return this;
    }

    public DamageBuilder setTarget(final Player target) {
        this.target = target;
        return this;
    }

    public DamageBuilder setDmgPoint(Point2D.Double dmgPoint) {
        this.dmgPoint = dmgPoint;
        return this;
    }

    public Damage build() {
        if (this.owner == null) {
            throw new NullPointerException("owner cannot be null.");
        }
        if (this.target == null) {
            throw new NullPointerException("target cannot be null.");
        }
        this.setType((this.type != null) ? this.type
                : (!this.isCrit) ? Globals.NUMBER_TYPE_PLAYER : Globals.NUMBER_TYPE_PLAYERCRIT);
        this.setDmgPoint((this.dmgPoint != null) ? this.dmgPoint
                : new Point2D.Double(this.target.getHitbox().getCenterX() + Globals.rng(RANDOM_DELTA) + RANDOM_CONST,
                        this.target.getHitbox().getCenterY() - 20 + Globals.rng(RANDOM_DELTA) + RANDOM_CONST));
        return new Damage(this.type, this.damage,
                this.canProc, this.isTrueDamage, this.isCrit, this.canReflect, this.isHidden, this.showParticle,
                this.owner, this.target,
                this.dmgPoint);
    }

}
