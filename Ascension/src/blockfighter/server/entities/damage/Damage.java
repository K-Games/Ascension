package blockfighter.server.entities.damage;

import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.player.skills.passive.SkillPassiveShadowAttack;
import blockfighter.shared.Globals;
import java.awt.geom.Point2D;

public class Damage {

    private final byte type;
    private final int damage;
    private final boolean canProc,
            isTrueDamage,
            isCrit,
            canReflect,
            isHidden,
            showParticle;

    private final Player owner;
    private final Player target;
    private final Point2D.Double dmgPoint;

    public Damage(final byte type, final int dmg,
            final boolean canProc, final boolean isTrueDamage, final boolean isCrit,
            final boolean canReflect, final boolean isHidden, final boolean showParticle,
            final Player owner, final Player target,
            final Point2D.Double dmgPoint) {

        this.type = type;
        this.damage = dmg;

        this.canProc = canProc;
        this.isTrueDamage = isTrueDamage;
        this.isCrit = isCrit;
        this.canReflect = canReflect;
        this.isHidden = isHidden;
        this.showParticle = showParticle;

        this.owner = owner;
        this.target = target;

        this.dmgPoint = dmgPoint;
    }

    public int getDamage() {
        return this.damage;
    }

    public Player getOwner() {
        return this.owner;
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
