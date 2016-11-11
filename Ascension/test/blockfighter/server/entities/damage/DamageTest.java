package blockfighter.server.entities.damage;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.maps.GameMapArena;
import blockfighter.shared.Globals;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import static org.junit.Assert.*;
import org.junit.Test;

public class DamageTest {

    @Test
    public void testDamageMobOwnerConstructors() {
        int expDamage = Globals.rng(1000);
        Mob expMob = new Mob(new LogicModule((byte) 0, (byte) 0), null, 0, 0, (byte) 0) {
            @Override
            public void update() {
            }
        };

        Player expPlayer = new Player(new LogicModule((byte) 0, (byte) 0), (byte) 0, null, new GameMapArena());
        Point2D.Double expPoint = new Point2D.Double(0, 0);
        Damage instance = new Damage(expDamage, expMob, expPlayer, expPoint);
        assertEquals(expDamage, instance.getDamage());
        assertEquals(expMob, instance.getMobOwner());
        assertEquals(expPlayer, instance.getTarget());
        assertEquals(expPoint, instance.getDmgPoint());
        assertFalse(instance.canProc());
        assertFalse(instance.isCrit());
        assertFalse(instance.isHidden());
        assertFalse(instance.isTrueDamage());
        assertTrue(instance.canReflect());
        assertEquals(Globals.NUMBER_TYPE_MOB, instance.getDamageType());

        instance = new Damage(expDamage, expMob, expPlayer,
                new Rectangle2D.Double(Globals.rng(50), Globals.rng(50), 100, 100),
                new Rectangle2D.Double(Globals.rng(50), Globals.rng(50), 100, 100));
        assertEquals(expDamage, instance.getDamage());
        assertEquals(expMob, instance.getMobOwner());
        assertEquals(expPlayer, instance.getTarget());
        assertTrue(instance.getDmgPoint() != null);
        assertFalse(instance.canProc());
        assertFalse(instance.isCrit());
        assertFalse(instance.isHidden());
        assertFalse(instance.isTrueDamage());
        assertTrue(instance.canReflect());
        assertEquals(Globals.NUMBER_TYPE_MOB, instance.getDamageType());

        instance = new Damage(expDamage, true, expMob, expPlayer, expPoint);
        assertEquals(expDamage, instance.getDamage());
        assertEquals(expMob, instance.getMobOwner());
        assertEquals(expPlayer, instance.getTarget());
        assertEquals(expPoint, instance.getDmgPoint());
        assertFalse(instance.canProc());
        assertFalse(instance.isCrit());
        assertFalse(instance.isHidden());
        assertTrue(instance.isTrueDamage());
        assertTrue(instance.canReflect());
        assertEquals(Globals.NUMBER_TYPE_MOB, instance.getDamageType());

        instance = new Damage(expDamage, false, expMob, expPlayer, expPoint);
        assertEquals(expDamage, instance.getDamage());
        assertEquals(expMob, instance.getMobOwner());
        assertEquals(expPlayer, instance.getTarget());
        assertEquals(expPoint, instance.getDmgPoint());
        assertFalse(instance.canProc());
        assertFalse(instance.isCrit());
        assertFalse(instance.isHidden());
        assertFalse(instance.isTrueDamage());
        assertTrue(instance.canReflect());
        assertEquals(Globals.NUMBER_TYPE_MOB, instance.getDamageType());

        instance = new Damage(expDamage, true, expMob, expPlayer,
                new Rectangle2D.Double(Globals.rng(50), Globals.rng(50), 100, 100),
                new Rectangle2D.Double(Globals.rng(50), Globals.rng(50), 100, 100));
        assertEquals(expDamage, instance.getDamage());
        assertEquals(expMob, instance.getMobOwner());
        assertEquals(expPlayer, instance.getTarget());
        assertTrue(instance.getDmgPoint() != null);
        assertFalse(instance.canProc());
        assertFalse(instance.isCrit());
        assertFalse(instance.isHidden());
        assertTrue(instance.isTrueDamage());
        assertTrue(instance.canReflect());
        assertEquals(Globals.NUMBER_TYPE_MOB, instance.getDamageType());

        instance = new Damage(expDamage, false, expMob, expPlayer,
                new Rectangle2D.Double(Globals.rng(50), Globals.rng(50), 100, 100),
                new Rectangle2D.Double(Globals.rng(50), Globals.rng(50), 100, 100));
        assertEquals(expDamage, instance.getDamage());
        assertEquals(expMob, instance.getMobOwner());
        assertEquals(expPlayer, instance.getTarget());
        assertTrue(instance.getDmgPoint() != null);
        assertFalse(instance.canProc());
        assertFalse(instance.isCrit());
        assertFalse(instance.isHidden());
        assertFalse(instance.isTrueDamage());
        assertTrue(instance.canReflect());
        assertEquals(Globals.NUMBER_TYPE_MOB, instance.getDamageType());
    }

    @Test
    public void testDamagePlayerOwnerMobTargetConstructors() {
        int expDamage = Globals.rng(1000);
        Mob expMob = new Mob(new LogicModule((byte) 0, (byte) 0), null, 0, 0, (byte) 0) {
            @Override
            public void update() {
            }
        };
        Player expOwner = new Player(new LogicModule((byte) 0, (byte) 0), (byte) 0, null, new GameMapArena());
        Point2D.Double expPoint = new Point2D.Double(0, 0);
        Damage instance = new Damage(expDamage, expOwner, expMob, true, expPoint);
        assertEquals(expDamage, instance.getDamage());
        assertEquals(expMob, instance.getMobTarget());
        assertEquals(expOwner, instance.getOwner());
        assertEquals(expPoint, instance.getDmgPoint());
        assertTrue(instance.canProc());
        assertTrue(instance.isCrit());
        assertFalse(instance.isHidden());
        assertFalse(instance.isTrueDamage());
        assertTrue(instance.canReflect());
        assertEquals(Globals.NUMBER_TYPE_PLAYERCRIT, instance.getDamageType());

        instance = new Damage(expDamage, expOwner, expMob, false, expPoint);
        assertEquals(expDamage, instance.getDamage());
        assertEquals(expMob, instance.getMobTarget());
        assertEquals(expOwner, instance.getOwner());
        assertEquals(expPoint, instance.getDmgPoint());
        assertTrue(instance.canProc());
        assertFalse(instance.isCrit());
        assertFalse(instance.isHidden());
        assertFalse(instance.isTrueDamage());
        assertTrue(instance.canReflect());
        assertEquals(Globals.NUMBER_TYPE_PLAYER, instance.getDamageType());

        instance = new Damage(expDamage, expOwner, expMob, true,
                new Rectangle2D.Double(Globals.rng(50), Globals.rng(50), 100, 100),
                new Rectangle2D.Double(Globals.rng(50), Globals.rng(50), 100, 100));
        assertEquals(expDamage, instance.getDamage());
        assertEquals(expMob, instance.getMobTarget());
        assertEquals(expOwner, instance.getOwner());
        assertTrue(instance.getDmgPoint() != null);
        assertTrue(instance.canProc());
        assertTrue(instance.isCrit());
        assertFalse(instance.isHidden());
        assertFalse(instance.isTrueDamage());
        assertTrue(instance.canReflect());
        assertEquals(Globals.NUMBER_TYPE_PLAYERCRIT, instance.getDamageType());
        instance = new Damage(expDamage, expOwner, expMob, false,
                new Rectangle2D.Double(Globals.rng(50), Globals.rng(50), 100, 100),
                new Rectangle2D.Double(Globals.rng(50), Globals.rng(50), 100, 100));
        assertEquals(expDamage, instance.getDamage());
        assertEquals(expMob, instance.getMobTarget());
        assertEquals(expOwner, instance.getOwner());
        assertTrue(instance.getDmgPoint() != null);
        assertTrue(instance.canProc());
        assertFalse(instance.isCrit());
        assertFalse(instance.isHidden());
        assertFalse(instance.isTrueDamage());
        assertTrue(instance.canReflect());
        assertEquals(Globals.NUMBER_TYPE_PLAYER, instance.getDamageType());

        instance = new Damage(expDamage, true, expOwner, expMob, true, expPoint);
        assertEquals(expDamage, instance.getDamage());
        assertEquals(expMob, instance.getMobTarget());
        assertEquals(expOwner, instance.getOwner());
        assertEquals(expPoint, instance.getDmgPoint());
        assertTrue(instance.canProc());
        assertTrue(instance.isCrit());
        assertFalse(instance.isHidden());
        assertFalse(instance.isTrueDamage());
        assertTrue(instance.canReflect());
        assertEquals(Globals.NUMBER_TYPE_PLAYERCRIT, instance.getDamageType());

        instance = new Damage(expDamage, false, expOwner, expMob, false, expPoint);
        assertEquals(expDamage, instance.getDamage());
        assertEquals(expMob, instance.getMobTarget());
        assertEquals(expOwner, instance.getOwner());
        assertEquals(expPoint, instance.getDmgPoint());
        assertFalse(instance.canProc());
        assertFalse(instance.isCrit());
        assertFalse(instance.isHidden());
        assertFalse(instance.isTrueDamage());
        assertTrue(instance.canReflect());
        assertEquals(Globals.NUMBER_TYPE_PLAYER, instance.getDamageType());

        instance = new Damage(expDamage, true, expOwner, expMob, true,
                new Rectangle2D.Double(Globals.rng(50), Globals.rng(50), 100, 100),
                new Rectangle2D.Double(Globals.rng(50), Globals.rng(50), 100, 100));
        assertEquals(expDamage, instance.getDamage());
        assertEquals(expMob, instance.getMobTarget());
        assertEquals(expOwner, instance.getOwner());
        assertTrue(instance.getDmgPoint() != null);
        assertTrue(instance.canProc());
        assertTrue(instance.isCrit());
        assertFalse(instance.isHidden());
        assertFalse(instance.isTrueDamage());
        assertTrue(instance.canReflect());
        assertEquals(Globals.NUMBER_TYPE_PLAYERCRIT, instance.getDamageType());

        instance = new Damage(expDamage, false, expOwner, expMob, false,
                new Rectangle2D.Double(Globals.rng(50), Globals.rng(50), 100, 100),
                new Rectangle2D.Double(Globals.rng(50), Globals.rng(50), 100, 100));
        assertEquals(expDamage, instance.getDamage());
        assertEquals(expMob, instance.getMobTarget());
        assertEquals(expOwner, instance.getOwner());
        assertTrue(instance.getDmgPoint() != null);
        assertFalse(instance.canProc());
        assertFalse(instance.isCrit());
        assertFalse(instance.isHidden());
        assertFalse(instance.isTrueDamage());
        assertTrue(instance.canReflect());
        assertEquals(Globals.NUMBER_TYPE_PLAYER, instance.getDamageType());
    }

    @Test
    public void testDamagePlayerOwnerPlayerTargetConstructors() {
        int expDamage = Globals.rng(1000);
        Player expOwner = new Player(new LogicModule((byte) 0, (byte) 0), (byte) 0, null, new GameMapArena());
        Player expTarget = new Player(new LogicModule((byte) 0, (byte) 0), (byte) 0, null, new GameMapArena());
        Point2D.Double expPoint = new Point2D.Double(0, 0);
        Damage instance = new Damage(expDamage, true, expOwner, expTarget, true, expPoint);
        assertEquals(expDamage, instance.getDamage());
        assertEquals(expTarget, instance.getTarget());
        assertEquals(expOwner, instance.getOwner());
        assertEquals(expPoint, instance.getDmgPoint());
        assertTrue(instance.canProc());
        assertTrue(instance.isCrit());
        assertFalse(instance.isHidden());
        assertFalse(instance.isTrueDamage());
        assertTrue(instance.canReflect());
        assertEquals(Globals.NUMBER_TYPE_PLAYERCRIT, instance.getDamageType());

        instance.setCanReflect(true);
        assertTrue(instance.canReflect());

        instance.setCanReflect(false);
        assertFalse(instance.canReflect());

        instance.setHidden(true);
        assertTrue(instance.isHidden());

        instance.setHidden(false);
        assertFalse(instance.isHidden());

        instance = new Damage(expDamage, false, expOwner, expTarget, false, expPoint);
        assertEquals(expDamage, instance.getDamage());
        assertEquals(expTarget, instance.getTarget());
        assertEquals(expOwner, instance.getOwner());
        assertEquals(expPoint, instance.getDmgPoint());
        assertFalse(instance.canProc());
        assertFalse(instance.isCrit());
        assertFalse(instance.isHidden());
        assertFalse(instance.isTrueDamage());
        assertTrue(instance.canReflect());
        assertEquals(Globals.NUMBER_TYPE_PLAYER, instance.getDamageType());

        instance = new Damage(expDamage, true, expOwner, expTarget, true,
                new Rectangle2D.Double(Globals.rng(50), Globals.rng(50), 100, 100),
                new Rectangle2D.Double(Globals.rng(50), Globals.rng(50), 100, 100));
        assertEquals(expDamage, instance.getDamage());
        assertEquals(expTarget, instance.getTarget());
        assertEquals(expOwner, instance.getOwner());
        assertTrue(instance.getDmgPoint() != null);
        assertTrue(instance.canProc());
        assertTrue(instance.isCrit());
        assertFalse(instance.isHidden());
        assertFalse(instance.isTrueDamage());
        assertTrue(instance.canReflect());
        assertEquals(Globals.NUMBER_TYPE_PLAYERCRIT, instance.getDamageType());

        instance = new Damage(expDamage, false, expOwner, expTarget, false,
                new Rectangle2D.Double(Globals.rng(50), Globals.rng(50), 100, 100),
                new Rectangle2D.Double(Globals.rng(50), Globals.rng(50), 100, 100));
        assertEquals(expDamage, instance.getDamage());
        assertEquals(expTarget, instance.getTarget());
        assertEquals(expOwner, instance.getOwner());
        assertTrue(instance.getDmgPoint() != null);
        assertFalse(instance.canProc());
        assertFalse(instance.isCrit());
        assertFalse(instance.isHidden());
        assertFalse(instance.isTrueDamage());
        assertTrue(instance.canReflect());
        assertEquals(Globals.NUMBER_TYPE_PLAYER, instance.getDamageType());
    }
}
