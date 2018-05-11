package blockfighter.server.entities.damage;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.server.maps.GameMapArena;
import blockfighter.shared.Globals;
import java.awt.geom.Point2D;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class DamageTest {

    @Before
    public void before() {
        Globals.SERVER_MODE = true;
    }

    @Test
    public void testDamagePlayerOwnerPlayerTargetConstructors() {
        int expDamage = Globals.rng(1000);
        Player expOwner = new Player(new LogicModule((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64)), (byte) 0, null, new GameMapArena());
        Player expTarget = new Player(new LogicModule((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64)), (byte) 0, null, new GameMapArena());
        Point2D.Double expPoint = new Point2D.Double(0, 0);
        Damage instance = new DamageBuilder()
                .setDamage(expDamage)
                .setCanProc(true)
                .setOwner(expOwner)
                .setTarget(expTarget)
                .setIsCrit(true)
                .setShowParticle(true)
                .setDmgPoint(expPoint)
                .build();
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

        instance = new DamageBuilder()
                .setDamage(expDamage)
                .setOwner(expOwner)
                .setTarget(expTarget)
                .setCanReflect(true)
                .build();
        assertTrue(instance.canReflect());

        instance = new DamageBuilder()
                .setDamage(expDamage)
                .setOwner(expOwner)
                .setTarget(expTarget)
                .setCanReflect(false)
                .build();
        assertFalse(instance.canReflect());

        instance = new DamageBuilder()
                .setDamage(expDamage)
                .setOwner(expOwner)
                .setTarget(expTarget)
                .setIsHidden(true)
                .build();
        assertTrue(instance.isHidden());

        instance = new DamageBuilder()
                .setDamage(expDamage)
                .setOwner(expOwner)
                .setTarget(expTarget)
                .setIsHidden(false)
                .build();
        assertFalse(instance.isHidden());

        instance = new DamageBuilder()
                .setDamage(expDamage)
                .setOwner(expOwner)
                .setTarget(expTarget)
                .setDmgPoint(expPoint)
                .setCanProc(false)
                .build();
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

        instance = new DamageBuilder()
                .setDamage(expDamage)
                .setOwner(expOwner)
                .setTarget(expTarget)
                .setIsCrit(true)
                .build();
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

        instance = new DamageBuilder()
                .setDamage(expDamage)
                .setOwner(expOwner)
                .setCanProc(false)
                .setTarget(expTarget)
                .build();
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
