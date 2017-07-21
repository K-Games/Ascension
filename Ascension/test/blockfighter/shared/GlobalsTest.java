package blockfighter.shared;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GlobalsTest {

    @Test
    public void testGetStatNameReturnInvalidStatWhenStatIDIsInvalid() {
        byte statID = 40;
        String expResult = "INVALID STAT";
        String result = Globals.getStatName(statID);

        assertEquals(expResult, result);
    }

    @Test
    public void testGetStatNameReturnCorrectNameWhenStatIDIsValid() {
        String expResult = "INVALID STAT";
        String result;
        for (byte i = 0; i < Globals.NUM_STATS; i++) {
            result = Globals.getStatName(i);
            assertNotEquals(expResult, result);
        }

        byte statID = Globals.STAT_ARMOUR;
        expResult = "Armour";
        result = Globals.getStatName(statID);
        assertEquals(expResult, result);

        statID = Globals.STAT_POWER;
        expResult = "Power";
        result = Globals.getStatName(statID);
        assertEquals(expResult, result);

        statID = Globals.STAT_DEFENSE;
        expResult = "Defense";
        result = Globals.getStatName(statID);
        assertEquals(expResult, result);

        statID = Globals.STAT_SPIRIT;
        expResult = "Spirit";
        result = Globals.getStatName(statID);
        assertEquals(expResult, result);

        statID = Globals.STAT_MINHP;
        expResult = "Current HP";
        result = Globals.getStatName(statID);
        assertEquals(expResult, result);

        statID = Globals.STAT_MAXHP;
        expResult = "Max HP";
        result = Globals.getStatName(statID);
        assertEquals(expResult, result);

        statID = Globals.STAT_MINDMG;
        expResult = "Minimum Damage";
        result = Globals.getStatName(statID);
        assertEquals(expResult, result);

        statID = Globals.STAT_MAXDMG;
        expResult = "Maximum Damage";
        result = Globals.getStatName(statID);
        assertEquals(expResult, result);

        statID = Globals.STAT_CRITCHANCE;
        expResult = "Critical Hit Chance";
        result = Globals.getStatName(statID);
        assertEquals(expResult, result);

        statID = Globals.STAT_CRITDMG;
        expResult = "Critical Hit Damage";
        result = Globals.getStatName(statID);
        assertEquals(expResult, result);

        statID = Globals.STAT_REGEN;
        expResult = "Regen(HP/Sec)";
        result = Globals.getStatName(statID);
        assertEquals(expResult, result);

        statID = Globals.STAT_LEVEL;
        expResult = "Level";
        result = Globals.getStatName(statID);
        assertEquals(expResult, result);

        statID = Globals.STAT_POINTS;
        expResult = "Stat Points";
        result = Globals.getStatName(statID);
        assertEquals(expResult, result);

        statID = Globals.STAT_EXP;
        expResult = "Experience";
        result = Globals.getStatName(statID);
        assertEquals(expResult, result);

        statID = Globals.STAT_SKILLPOINTS;
        expResult = "Skill Points";
        result = Globals.getStatName(statID);
        assertEquals(expResult, result);

        statID = Globals.STAT_DAMAGEREDUCT;
        expResult = "Damage Reduction";
        result = Globals.getStatName(statID);
        assertEquals(expResult, result);

        statID = Globals.STAT_MAXEXP;
        expResult = "Required EXP";
        result = Globals.getStatName(statID);
        assertEquals(expResult, result);
    }

    @Test
    public void testLongToBytes() {
        long input = Long.MAX_VALUE;
        byte[] expResult = {(byte) 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        byte[] result = Globals.longToBytes(input);

        assertArrayEquals(expResult, result);
    }

    @Test
    public void testBytesToLong() {
        byte[] bytes = {(byte) 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        long expResult = Long.MAX_VALUE;
        long result = Globals.bytesToLong(bytes);

        assertEquals(expResult, result);
    }

    @Test
    public void testIntToBytes() {
        int input = Integer.MIN_VALUE;
        byte[] expResult = {(byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        byte[] result = Globals.intToBytes(input);

        assertArrayEquals(expResult, result);
    }

    @Test
    public void testBytesToInt() {
        byte[] bytes = {(byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        int expResult = Integer.MIN_VALUE;
        int result = Globals.bytesToInt(bytes);

        assertEquals(expResult, result);
    }

    @Test
    public void testNsToMs() {
        long time = 1000000000L;
        long expResult = TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS);
        long result = Globals.nsToMs(time);

        assertEquals(expResult, result);
    }

    @Test
    public void testMsToNs() {
        long time = 1000L;
        long expResult = TimeUnit.NANOSECONDS.convert(time, TimeUnit.MILLISECONDS);
        long result = Globals.msToNs(time);

        assertEquals(expResult, result);
    }

    @Test
    public void testRngReturnInvalidNumberWhenInputIsZero() {
        int i = 0;
        int expResult = -1;
        int result = Globals.rng(i);

        assertEquals(expResult, result);
    }

    @Test
    public void testDurationHasPastFalseWhenDurationHasNotPast() {
        int currentDuration = 5000;
        int durationToPast = 6000;
        boolean result = Globals.hasPastDuration(currentDuration, durationToPast);
        assertFalse(result);
    }

    @Test
    public void testDurationHasPastTrueWhenDurationHasPast() {
        int currentDuration = 5000;
        int durationToPast = 0;

        boolean result = Globals.hasPastDuration(currentDuration, durationToPast);
        assertTrue(result);
    }

    @Test
    public void testGetDataHeaders() {
        String[] data = new String[Globals.DATA_HEADERS.length + 2];
        System.arraycopy(Globals.DATA_HEADERS, 0, data, 0, Globals.DATA_HEADERS.length);
        data[Globals.DATA_HEADERS.length] = "[test1]";
        data[Globals.DATA_HEADERS.length + 1] = "[test2]";
        String[] customDataHeaders = {"[test1]", "[test2]"};
        HashMap<String, Integer> result = Globals.getDataHeaders(data, customDataHeaders);
        for (String header : data) {
            assertTrue(result.containsKey(header));

        }
    }

    @Test
    public void testLoadBooleanValue() {
        String[] customHeaders = {"[testBool]", "[testBool2]"};
        String[] data = {customHeaders[0], "true", customHeaders[1], "false"};

        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, customHeaders);

        String header = customHeaders[0];
        boolean result = Globals.loadBooleanValue(data, dataHeaders, header);

        assertTrue(result);

        header = customHeaders[1];
        result = Globals.loadBooleanValue(data, dataHeaders, header);

        assertFalse(result);
    }

    @Test
    public void testLoadDoubleValue() {
        String[] customHeaders = {"[testDouble]", "[testDouble2]"};
        double[] customDataValue = {0.123, 10019239784.222};
        String[] data = {customHeaders[0], String.valueOf(customDataValue[0]), customHeaders[1], String.valueOf(customDataValue[1])};

        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, customHeaders);

        String header = customHeaders[0];
        double result = Globals.loadDoubleValue(data, dataHeaders, header);

        assertEquals(customDataValue[0], result, 0);

        header = customHeaders[1];
        result = Globals.loadDoubleValue(data, dataHeaders, header);

        assertEquals(customDataValue[1], result, 0);
    }

    @Test
    public void testLoadReqWeapon() {
        String[] data = {Globals.SKILL_REQWEAPON_HEADER, String.valueOf(Globals.NUM_EQUIP_TYPES + 1)};

        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, null);

        assertEquals(-1, Globals.loadReqWeapon(data, dataHeaders));
        for (int i = 0; i < Globals.NUM_EQUIP_TYPES; i++) {
            data = new String[]{Globals.SKILL_REQWEAPON_HEADER, String.valueOf(i)};

            byte result = Globals.loadReqWeapon(data, dataHeaders);
            dataHeaders = Globals.getDataHeaders(data, null);

            assertEquals(i, result);
        }
    }

    @Test
    public void testLoadSkillData() {
        Globals.LOGGING = false;
        for (byte i = 0; i < Globals.NUM_SKILLS; i++) {
            String[] result = Globals.loadSkillData(i);
            assertNotNull(result);
        }
    }

    @Test
    public void testLoadSkillDesc() {
        String[] desc = {"Test Desc", "line2"};
        String[] data = {Globals.SKILL_DESC_HEADER, String.valueOf(2), desc[0], desc[1]};

        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, null);

        String[] result = Globals.loadSkillDesc(data, dataHeaders);

        assertTrue(result[0].equals(desc[0]));
        assertTrue(result[1].equals(desc[1]));
    }

    @Test
    public void testLoadSkillName() {
        String name = "testName";
        String[] data = {Globals.SKILL_NAME_HEADER, name};

        HashMap<String, Integer> dataHeaders = Globals.getDataHeaders(data, null);

        String result = Globals.loadSkillName(data, dataHeaders);

        assertTrue(result.equals(name));
    }

    @Test
    public void testCalcArmour() {
        double defense = Globals.rng(1000000);
        double expResult = defense * Globals.ARMOUR_MULT;
        double result = Globals.calcArmour(defense);

        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCalcRegen() {
        double spirit = Globals.rng(1000000);
        double expResult = spirit * Globals.REGEN_MULT + Globals.HP_BASE * 0.02;
        double result = Globals.calcRegen(spirit);

        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCalcMaxHP() {
        double defense = Globals.rng(1000000);
        double expResult = defense * Globals.HP_MULT + Globals.HP_BASE;
        double result = Globals.calcMaxHP(defense);

        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCalcMinDmg() {
        double power = Globals.rng(1000000);
        double expResult = (1 - Globals.DMG_VARIANCE_PERCENT) * (power * Globals.DMG_MULT + Globals.DMG_BASE);
        double result = Globals.calcMinDmg(power);

        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCalcMaxDmg() {
        double power = Globals.rng(1000000);
        double expResult = (1 + Globals.DMG_VARIANCE_PERCENT) * (power * Globals.DMG_MULT + Globals.DMG_BASE);
        double result = Globals.calcMaxDmg(power);

        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCalcCritChance() {
        double spirit = Globals.rng(1000000);
        double expResult = spirit / (spirit + Globals.CRITCHC_CONST) + Globals.CRITCHC_BASE;
        double result = Globals.calcCritChance(spirit);

        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCalcCritDmg() {
        double spirit = Globals.rng(1000000);
        double expResult = spirit / Globals.CRITDMG_FACT * Globals.CRITDMG_MULT + Globals.CRITDMG_BASE;
        double result = Globals.calcCritDmg(spirit);

        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCalcReduction() {
        double armour = Globals.rng(1000000) / Globals.rng(200) * 1D;
        double expResult = 1 - (armour / (armour + Globals.REDUCT_CONST));
        double result = Globals.calcReduction(armour);

        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCalcEHP() {
        double reduct = Globals.rng(10000) / 10000D;
        double maxHP = Globals.rng(1000000) / Globals.rng(200) * 1D;
        double expResult = maxHP / reduct;
        double result = Globals.calcEHP(reduct, maxHP);

        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testCalcEXPtoNxtLvl() {
        for (byte i = 1; i < 101; i++) {
            double level = i;
            int expResult = (int) (Math.round(Math.pow(level, 3.75) + 100));
            int result = Globals.calcEXPtoNxtLvl(level);

            assertEquals(expResult, result);
        }
    }

}
