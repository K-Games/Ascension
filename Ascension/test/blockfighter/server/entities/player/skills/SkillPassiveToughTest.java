package blockfighter.server.entities.player.skills;

import blockfighter.shared.Globals;
import java.util.HashMap;
import static org.junit.Assert.*;
import org.junit.Test;

public class SkillPassiveToughTest {

    private static final byte SKILL_CODE = Globals.PASSIVE_TOUGH;
    private static final String[] DATA = Globals.loadSkillData(SKILL_CODE);
    private static final HashMap<String, Integer> DATA_HEADERS = Globals.getDataHeaders(DATA, null);
    private static final Skill INSTANCE = new SkillPassiveTough(null);

    @Test
    public void testCustomValues() {
        assertNull(INSTANCE.getCustomValue(null));
    }

    @Test
    public void testCastPlayerState() {
        byte expResult = -1;
        byte result = INSTANCE.castPlayerState();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetBaseValue() {
        double expResult = Globals.loadDoubleValue(DATA, DATA_HEADERS, Globals.SKILL_BASEVALUE_HEADER);
        double result = INSTANCE.getBaseValue();
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetMaxCooldown() {
        double expResult = Globals.loadDoubleValue(DATA, DATA_HEADERS, Globals.SKILL_MAXCOOLDOWN_HEADER);
        double result = INSTANCE.getMaxCooldown();
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetMultValue() {
        double expResult = Globals.loadDoubleValue(DATA, DATA_HEADERS, Globals.SKILL_MULTVALUE_HEADER);
        double result = INSTANCE.getMultValue();
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testGetReqEquipSlot() {
        byte expResult = -1;
        byte result = INSTANCE.getReqEquipSlot();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetReqWeapon() {
        byte expResult = Globals.loadReqWeapon(DATA, DATA_HEADERS);
        byte result = INSTANCE.getReqWeapon();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetSkillCode() {
        byte expResult = SKILL_CODE;
        byte result = INSTANCE.getSkillCode();
        assertEquals(expResult, result);
    }

    @Test
    public void testIsPassive() {
        boolean expResult = Globals.loadBooleanValue(DATA, DATA_HEADERS, Globals.SKILL_PASSIVE_HEADER);
        boolean result = INSTANCE.isPassive();
        assertEquals(expResult, result);
    }
}
