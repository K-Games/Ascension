package blockfighter.server.entities.boss.damage;

import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.player.Player;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Ken Kwan
 */
public class Damage {

    public final static byte DAMAGE_TYPE_PLAYER = 0x00,
            DAMAGE_TYPE_PLAYERCRIT = 0x01,
            DAMAGE_TYPE_BOSS = 0x02;

    private int damage;
    private boolean canProc = false,
            isTrueDamage = false,
            isCrit = false;
    private Player owner, target;
    private Boss bossOwner, bossTarget;
    private Point dmgPoint;

    public Damage(int dmg, boolean proc, boolean trueDmg, Player o, Player t, boolean crit, Point p) {
        damage = dmg;
        canProc = proc;
        isTrueDamage = trueDmg;
        owner = o;
        target = t;
        dmgPoint = p;
        isCrit = crit;
    }

    public Damage(int dmg, boolean trueDmg, Boss o, Player t, Point p) {
        damage = dmg;
        isTrueDamage = trueDmg;
        bossOwner = o;
        target = t;
        dmgPoint = p;
    }

    public Damage(int dmg, Boss o, Player t, Point p) {
        damage = dmg;
        bossOwner = o;
        target = t;
        dmgPoint = p;
    }

    public Damage(int dmg, boolean proc, Player o, Boss t, boolean crit, Point p) {
        damage = dmg;
        canProc = proc;
        owner = o;
        bossTarget = t;
        dmgPoint = p;
        isCrit = crit;
    }

    public Damage(int dmg, Player o, Boss t, boolean crit, Point p) {
        damage = dmg;
        canProc = true;
        owner = o;
        bossTarget = t;
        dmgPoint = p;
        isCrit = crit;
    }

    public Damage(int dmg, boolean proc, Player o, Player t, boolean crit, Rectangle2D.Double box1, Rectangle2D.Double box2) {
        damage = dmg;
        canProc = proc;
        owner = o;
        target = t;
        Rectangle2D box = box1.createIntersection(box2);
        dmgPoint = new Point((int) (box.getX() + box.getWidth() / 2), (int) (box.getY() + box.getHeight() / 2));
        isCrit = crit;
    }

    public Damage(int dmg, boolean trueDmg, Boss o, Player t, Rectangle2D.Double box1, Rectangle2D.Double box2) {
        damage = dmg;
        isTrueDamage = trueDmg;
        bossOwner = o;
        target = t;
        Rectangle2D box = box1.createIntersection(box2);
        dmgPoint = new Point((int) (box.getX() + box.getWidth() / 2), (int) (box.getY() + box.getHeight() / 2));
    }

    public Damage(int dmg, Boss o, Player t, Rectangle2D.Double box1, Rectangle2D.Double box2) {
        damage = dmg;
        bossOwner = o;
        target = t;
        Rectangle2D box = box1.createIntersection(box2);
        dmgPoint = new Point((int) (box.getX() + box.getWidth() / 2), (int) (box.getY() + box.getHeight() / 2));
    }

    public Damage(int dmg, boolean proc, Player o, Boss t, boolean crit, Rectangle2D.Double box1, Rectangle2D.Double box2) {
        damage = dmg;
        canProc = proc;
        owner = o;
        bossTarget = t;
        Rectangle2D box = box1.createIntersection(box2);
        dmgPoint = new Point((int) (box.getX() + box.getWidth() / 2), (int) (box.getY() + box.getHeight() / 2));
        isCrit = crit;
    }

    public Damage(int dmg, Player o, Boss t, boolean crit, Rectangle2D.Double box1, Rectangle2D.Double box2) {
        damage = dmg;
        canProc = true;
        owner = o;
        bossTarget = t;
        Rectangle2D box = box1.createIntersection(box2);
        dmgPoint = new Point((int) (box.getX() + box.getWidth() / 2), (int) (box.getY() + box.getHeight() / 2));
        isCrit = crit;
    }

    public int getDamage() {
        return damage;
    }

    public Boss getBossOwner() {
        return bossOwner;
    }

    public Player getOwner() {
        return owner;
    }

    public Boss getBossTarget() {
        return bossTarget;
    }

    public Player getTarget() {
        return target;
    }

    public boolean isTrueDamage() {
        return isTrueDamage;
    }

    public boolean canProc() {
        return canProc;
    }

    public Point getDmgPoint() {
        return dmgPoint;
    }

    public boolean isCrit() {
        return isCrit;
    }

    public void proc() {
        if (canProc && owner != null) {
            owner.damageProc(this);
        }
    }
}
