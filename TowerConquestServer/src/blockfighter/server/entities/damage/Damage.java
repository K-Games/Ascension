package blockfighter.server.entities.damage;

import blockfighter.server.Globals;
import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.player.Player;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Ken Kwan
 */
public class Damage {
    
    private final byte type;
    private final int damage;
    private boolean canProc = false,
            isTrueDamage = false,
            isCrit = false,
            canReflect = true,
            isHidden = false;

    private Player owner, target;
    private Boss bossOwner, bossTarget;
    private final Point dmgPoint;

    public Damage(final int dmg, final boolean proc, final Player o, final Player t, final boolean crit, final Point p) {
        this.damage = dmg;
        this.canProc = proc;
        this.owner = o;
        this.target = t;
        this.dmgPoint = p;
        this.isCrit = crit;
        this.type = (this.isCrit) ? Globals.NUMBER_TYPE_PLAYERCRIT : Globals.NUMBER_TYPE_PLAYER;
    }

    public Damage(final int dmg, final boolean trueDmg, final Boss o, final Player t, final Point p) {
        this.damage = dmg;
        this.isTrueDamage = trueDmg;
        this.bossOwner = o;
        this.target = t;
        this.dmgPoint = p;
        this.type = Globals.NUMBER_TYPE_BOSS;
    }

    public Damage(final int dmg, final Boss o, final Player t, final Point p) {
        this.damage = dmg;
        this.bossOwner = o;
        this.target = t;
        this.dmgPoint = p;
        this.type = Globals.NUMBER_TYPE_BOSS;
    }

    public Damage(final int dmg, final boolean proc, final Player o, final Boss t, final boolean crit, final Point p) {
        this.damage = dmg;
        this.canProc = proc;
        this.owner = o;
        this.bossTarget = t;
        this.dmgPoint = p;
        this.isCrit = crit;
        this.type = (this.isCrit) ? Globals.NUMBER_TYPE_PLAYERCRIT : Globals.NUMBER_TYPE_PLAYER;
    }

    public Damage(final int dmg, final Player o, final Boss t, final boolean crit, final Point p) {
        this.damage = dmg;
        this.canProc = true;
        this.owner = o;
        this.bossTarget = t;
        this.dmgPoint = p;
        this.isCrit = crit;
        this.type = (this.isCrit) ? Globals.NUMBER_TYPE_PLAYERCRIT : Globals.NUMBER_TYPE_PLAYER;
    }

    public Damage(final int dmg, final boolean proc, final Player o, final Player t, final boolean crit, final Rectangle2D.Double box1,
            final Rectangle2D.Double box2) {
        this.damage = dmg;
        this.canProc = proc;
        this.owner = o;
        this.target = t;
        final Rectangle2D box = box1.createIntersection(box2);
        this.dmgPoint = new Point((int) (box.getX() + box.getWidth() / 2), (int) (box.getY() + box.getHeight() / 2));
        this.isCrit = crit;
        this.type = (this.isCrit) ? Globals.NUMBER_TYPE_PLAYERCRIT : Globals.NUMBER_TYPE_PLAYER;
    }

    public Damage(final int dmg, final boolean trueDmg, final Boss o, final Player t, final Rectangle2D.Double box1,
            final Rectangle2D.Double box2) {
        this.damage = dmg;
        this.isTrueDamage = trueDmg;
        this.bossOwner = o;
        this.target = t;
        final Rectangle2D box = box1.createIntersection(box2);
        this.dmgPoint = new Point((int) (box.getX() + box.getWidth() / 2), (int) (box.getY() + box.getHeight() / 2));
        this.type = Globals.NUMBER_TYPE_BOSS;
    }

    public Damage(final int dmg, final Boss o, final Player t, final Rectangle2D.Double box1, final Rectangle2D.Double box2) {
        this.damage = dmg;
        this.bossOwner = o;
        this.target = t;
        final Rectangle2D box = box1.createIntersection(box2);
        this.dmgPoint = new Point((int) (box.getX() + box.getWidth() / 2), (int) (box.getY() + box.getHeight() / 2));
        this.type = Globals.NUMBER_TYPE_BOSS;
    }

    public Damage(final int dmg, final boolean proc, final Player o, final Boss t, final boolean crit, final Rectangle2D.Double box1,
            final Rectangle2D.Double box2) {
        this.damage = dmg;
        this.canProc = proc;
        this.owner = o;
        this.bossTarget = t;
        final Rectangle2D box = box1.createIntersection(box2);
        this.dmgPoint = new Point((int) (box.getX() + box.getWidth() / 2), (int) (box.getY() + box.getHeight() / 2));
        this.isCrit = crit;
        this.type = (this.isCrit) ? Globals.NUMBER_TYPE_PLAYERCRIT : Globals.NUMBER_TYPE_PLAYER;
    }

    public Damage(final int dmg, final Player o, final Boss t, final boolean crit, final Rectangle2D.Double box1,
            final Rectangle2D.Double box2) {
        this.damage = dmg;
        this.canProc = true;
        this.owner = o;
        this.bossTarget = t;
        final Rectangle2D box = box1.createIntersection(box2);
        this.dmgPoint = new Point((int) (box.getX() + box.getWidth() / 2), (int) (box.getY() + box.getHeight() / 2));
        this.isCrit = crit;
        this.type = (this.isCrit) ? Globals.NUMBER_TYPE_PLAYERCRIT : Globals.NUMBER_TYPE_PLAYER;
    }

    public int getDamage() {
        return this.damage;
    }

    public Boss getBossOwner() {
        return this.bossOwner;
    }

    public Player getOwner() {
        return this.owner;
    }

    public Boss getBossTarget() {
        return this.bossTarget;
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

    public Point getDmgPoint() {
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

}
