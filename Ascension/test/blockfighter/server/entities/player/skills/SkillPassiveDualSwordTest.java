package blockfighter.server.entities.player.skills;

import blockfighter.shared.Globals;
import java.util.HashMap;
import static org.junit.Assert.*;
import org.junit.Test;

public class SkillPassiveDualSwordTest {

    public static final String CUSTOMHEADER_DMGREDUCTMULT = "[dmgreductmult]";

    private static final String[] CUSTOM_DATA_HEADERS = {
        CUSTOMHEADER_DMGREDUCTMULT
    };

    private static final byte SKILL_CODE = Globals.PASSIVE_DUALSWORD;
    private static final String[] DATA = Globals.loadSkillData(SKILL_CODE);
    private static final HashMap<String, Integer> DATA_HEADERS = Globals.getDataHeaders(DATA, CUSTOM_DATA_HEADERS);
    private static final Skill INSTANCE = new SkillPassiveDualSword(null);

    @Test
    public void testCustomValues() {
        for (String customHeader : CUSTOM_DATA_HEADERS) {
            double expResult = Globals.loadDoubleValue(DATA, DATA_HEADERS, customHeader);
            double result = INSTANCE.getCustomValue(customHeader);
            assertEquals(expResult, result, 0.0);
        }
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