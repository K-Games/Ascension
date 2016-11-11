package blockfighter.server.entities.damage;

import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.shared.Globals;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Damage {

    private final byte type;
    private final int damage;
    private boolean canProc = false,
            isTrueDamage = false,
            isCrit = false,
            canReflect = true,
            isHidden = false;

    private Player owner, target;
    private Mob mobOwner, mobTarget;
    private final Point2D.Double dmgPoint;

    public Damage(final int dmg, final boolean canProc, final Player o, final Player t, final boolean isCrit, final Point2D.Double p) {
        this.damage = dmg;
        this.canProc = canProc;
        this.owner = o;
        this.target = t;
        this.dmgPoint = p;
        this.isCrit = isCrit;
        this.type = (this.isCrit) ? Globals.NUMBER_TYPE_PLAYERCRIT : Globals.NUMBER_TYPE_PLAYER;
    }

    public Damage(final int dmg, final boolean isTrueDamage, final Mob o, final Player t, final Point2D.Double p) {
        this.damage = dmg;
        this.isTrueDamage = isTrueDamage;
        this.mobOwner = o;
        this.target = t;
        this.dmgPoint = p;
        this.type = Globals.NUMBER_TYPE_MOB;
    }

    public Damage(final int dmg, final boolean canProc, final Player o, final Mob t, final boolean isCrit, final Point2D.Double p) {
        this.damage = dmg;
        this.canProc = canProc;
        this.owner = o;
        this.mobTarget = t;
        this.dmgPoint = p;
        this.isCrit = isCrit;
        this.type = (this.isCrit) ? Globals.NUMBER_TYPE_PLAYERCRIT : Globals.NUMBER_TYPE_PLAYER;
    }

    public Damage(final int dmg, final Mob o, final Player t, final Point2D.Double p) {
        this(dmg, false, o, t, p);
    }

    public Damage(final int dmg, final Player o, final Mob t, final boolean isCrit, final Point2D.Double p) {
        this(dmg, true, o, t, isCrit, p);
    }

    public Damage(final int dmg, final boolean canProc, final Player o, final Player t, final boolean isCrit, final Rectangle2D.Double box1,
            final Rectangle2D.Double box2) {
        this(dmg, canProc, o, t, isCrit, new Point2D.Double(box1.createIntersection(box2).getX() + box1.createIntersection(box2).getWidth() / 2, box1.createIntersection(box2).getY() + box1.createIntersection(box2).getHeight() / 2));
    }

    public Damage(final int dmg, final boolean isTrueDamage, final Mob o, final Player t, final Rectangle2D.Double box1,
            final Rectangle2D.Double box2) {
        this(dmg, isTrueDamage, o, t, new Point2D.Double(box1.createIntersection(box2).getX() + box1.createIntersection(box2).getWidth() / 2, box1.createIntersection(box2).getY() + box1.createIntersection(box2).getHeight() / 2));
    }

    public Damage(final int dmg, final Mob o, final Player t, final Rectangle2D.Double box1, final Rectangle2D.Double box2) {
        this(dmg, o, t, new Point2D.Double(box1.createIntersection(box2).getX() + box1.createIntersection(box2).getWidth() / 2, box1.createIntersection(box2).getY() + box1.createIntersection(box2).getHeight() / 2));
    }

    public Damage(final int dmg, final boolean canProc, final Player o, final Mob t, final boolean isCrit, final Rectangle2D.Double box1,
            final Rectangle2D.Double box2) {
        this(dmg, canProc, o, t, isCrit, new Point2D.Double(box1.createIntersection(box2).getX() + box1.createIntersection(box2).getWidth() / 2, box1.createIntersection(box2).getY() + box1.createIntersection(box2).getHeight() / 2));
    }

    public Damage(final int dmg, final Player o, final Mob t, final boolean isCrit, final Rectangle2D.Double box1,
            final Rectangle2D.Double box2) {
        this(dmg, true, o, t, isCrit, new Point2D.Double(box1.createIntersection(box2).getX() + box1.createIntersection(box2).getWidth() / 2, box1.createIntersection(box2).getY() + box1.createIntersection(box2).getHeight() / 2));
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
            this.owner.damageProc(this);
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
}
