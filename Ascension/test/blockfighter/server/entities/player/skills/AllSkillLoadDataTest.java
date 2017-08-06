package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.shared.Globals;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import static org.junit.Assert.*;
import org.junit.Test;

public class AllSkillLoadDataTest {

    private enum SkillServerClass {
        SWORD_VORPAL(Globals.SWORD_VORPAL, SkillSwordVorpal.class, Globals.EQUIP_WEAPON, Player.PLAYER_STATE_SWORD_VORPAL, SkillSwordVorpal.CUSTOM_DATA_HEADERS),
        SWORD_PHANTOM(Globals.SWORD_PHANTOM, SkillSwordPhantom.class, Globals.EQUIP_WEAPON, Player.PLAYER_STATE_SWORD_PHANTOM, null),
        SWORD_CINDER(Globals.SWORD_CINDER, SkillSwordCinder.class, Globals.EQUIP_WEAPON, Player.PLAYER_STATE_SWORD_CINDER, SkillSwordCinder.CUSTOM_DATA_HEADERS),
        SWORD_GASH(Globals.SWORD_GASH, SkillSwordGash.class, Globals.EQUIP_WEAPON, Player.PLAYER_STATE_SWORD_GASH, SkillSwordGash.CUSTOM_DATA_HEADERS),
        SWORD_SLASH(Globals.SWORD_SLASH, SkillSwordSlash.class, Globals.EQUIP_WEAPON, Player.PLAYER_STATE_SWORD_SLASH, SkillSwordSlash.CUSTOM_DATA_HEADERS),
        SWORD_TAUNT(Globals.SWORD_TAUNT, SkillSwordTaunt.class, Globals.EQUIP_WEAPON, Player.PLAYER_STATE_SWORD_TAUNT, SkillSwordTaunt.CUSTOM_DATA_HEADERS),
        BOW_ARC(Globals.BOW_ARC, SkillBowArc.class, Globals.EQUIP_WEAPON, Player.PLAYER_STATE_BOW_ARC, SkillBowArc.CUSTOM_DATA_HEADERS),
        BOW_POWER(Globals.BOW_POWER, SkillBowPower.class, Globals.EQUIP_WEAPON, Player.PLAYER_STATE_BOW_POWER, SkillBowPower.CUSTOM_DATA_HEADERS),
        BOW_RAPID(Globals.BOW_RAPID, SkillBowRapid.class, Globals.EQUIP_WEAPON, Player.PLAYER_STATE_BOW_RAPID, SkillBowRapid.CUSTOM_DATA_HEADERS),
        BOW_FROST(Globals.BOW_FROST, SkillBowFrost.class, Globals.EQUIP_WEAPON, Player.PLAYER_STATE_BOW_FROST, SkillBowFrost.CUSTOM_DATA_HEADERS),
        BOW_STORM(Globals.BOW_STORM, SkillBowStorm.class, Globals.EQUIP_WEAPON, Player.PLAYER_STATE_BOW_STORM, SkillBowStorm.CUSTOM_DATA_HEADERS),
        BOW_VOLLEY(Globals.BOW_VOLLEY, SkillBowVolley.class, Globals.EQUIP_WEAPON, Player.PLAYER_STATE_BOW_VOLLEY, SkillBowVolley.CUSTOM_DATA_HEADERS),
        UTILITY_ADRENALINE(Globals.UTILITY_ADRENALINE, SkillUtilityAdrenaline.class, (byte) -1, Player.PLAYER_STATE_UTILITY_ADRENALINE, SkillUtilityAdrenaline.CUSTOM_DATA_HEADERS),
        SHIELD_ROAR(Globals.SHIELD_ROAR, SkillShieldRoar.class, Globals.EQUIP_OFFHAND, Player.PLAYER_STATE_SHIELD_ROAR, SkillShieldRoar.CUSTOM_DATA_HEADERS),
        SHIELD_CHARGE(Globals.SHIELD_CHARGE, SkillShieldCharge.class, Globals.EQUIP_OFFHAND, Player.PLAYER_STATE_SHIELD_CHARGE, SkillShieldCharge.CUSTOM_DATA_HEADERS),
        SHIELD_REFLECT(Globals.SHIELD_REFLECT, SkillShieldReflect.class, Globals.EQUIP_OFFHAND, Player.PLAYER_STATE_SHIELD_REFLECT, SkillShieldReflect.CUSTOM_DATA_HEADERS),
        SHIELD_MAGNETIZE(Globals.SHIELD_MAGNETIZE, SkillShieldMagnetize.class, Globals.EQUIP_OFFHAND, Player.PLAYER_STATE_SHIELD_MAGNETIZE, SkillShieldMagnetize.CUSTOM_DATA_HEADERS),
        UTILITY_DASH(Globals.UTILITY_DASH, SkillUtilityDash.class, (byte) -1, Player.PLAYER_STATE_UTILITY_DASH, null),
        PASSIVE_DUALSWORD(Globals.PASSIVE_DUALSWORD, SkillPassiveDualSword.class, (byte) -1, (byte) -1, SkillPassiveDualSword.CUSTOM_DATA_HEADERS),
        PASSIVE_KEENEYE(Globals.PASSIVE_KEENEYE, SkillPassiveKeenEye.class, (byte) -1, (byte) -1, null),
        PASSIVE_VITALHIT(Globals.PASSIVE_VITALHIT, SkillPassiveVitalHit.class, (byte) -1, (byte) -1, null),
        PASSIVE_SHIELDMASTERY(Globals.PASSIVE_SHIELDMASTERY, SkillPassiveShieldMastery.class, (byte) -1, (byte) -1, SkillPassiveShieldMastery.CUSTOM_DATA_HEADERS),
        PASSIVE_BARRIER(Globals.PASSIVE_BARRIER, SkillPassiveBarrier.class, (byte) -1, (byte) -1, null),
        PASSIVE_RESIST(Globals.PASSIVE_RESIST, SkillPassiveResistance.class, (byte) -1, (byte) -1, null),
        PASSIVE_BOWMASTERY(Globals.PASSIVE_BOWMASTERY, SkillPassiveBowMastery.class, (byte) -1, (byte) -1, null),
        PASSIVE_WILLPOWER(Globals.PASSIVE_WILLPOWER, SkillPassiveWillpower.class, (byte) -1, (byte) -1, null),
        PASSIVE_HARMONY(Globals.PASSIVE_HARMONY, SkillPassiveHarmony.class, (byte) -1, (byte) -1, null),
        PASSIVE_TOUGH(Globals.PASSIVE_TOUGH, SkillPassiveTough.class, (byte) -1, (byte) -1, null),
        PASSIVE_SHADOWATTACK(Globals.PASSIVE_SHADOWATTACK, SkillPassiveShadowAttack.class, (byte) -1, (byte) -1, null),
        PASSIVE_STATIC(Globals.PASSIVE_STATIC, SkillPassiveStatic.class, (byte) -1, (byte) -1, null);

        byte skillCode;
        Class serverClass;
        byte reqEquipSlot;
        byte playerCastState;
        String[] data;
        HashMap<String, Integer> dataHeaders;
        String[] customHeaders;

        SkillServerClass(final byte skillCode, final Class serverClass, final byte reqEquipSlot, final byte playerCastState, final String[] customHeaders) {
            this.skillCode = skillCode;
            this.serverClass = serverClass;
            this.reqEquipSlot = reqEquipSlot;
            this.playerCastState = playerCastState;
            this.data = Globals.loadSkillData(skillCode);
            this.customHeaders = customHeaders;
            this.dataHeaders = Globals.getDataHeaders(data, this.customHeaders);
        }
    }

    private Skill newSkillInstance(Class skillServerClass) {
        try {
            Constructor<? extends Skill> constructor = skillServerClass.getDeclaredConstructor(LogicModule.class);
            return constructor.newInstance((LogicModule) null);
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return null;
    }

    @Test
    public void testCustomValues() {
        System.out.println("testCustomValues");
        for (SkillServerClass skill : SkillServerClass.values()) {
            String[] data = skill.data;
            HashMap<String, Integer> dataHeaders = skill.dataHeaders;
            Skill skillInstance = newSkillInstance(skill.serverClass);
            if (skill.customHeaders != null) {
                for (String customHeader : skill.customHeaders) {
                    double expResult = Globals.loadDoubleValue(data, dataHeaders, customHeader);
                    double result = skillInstance.getCustomValue(customHeader);
                    System.out.println("Testing " + skill + ": Expected=" + expResult + " Result=" + result);
                    assertEquals(expResult, result, 0.0);
                }
            } else {
                Double result = skillInstance.getCustomValue(null);
                System.out.println("Testing " + skill + ": Expected=" + null + " Result=" + result);
                assertNull(result);
            }
        }
    }

    @Test
    public void testCastPlayerState() {
        System.out.println("testCastPlayerState");
        for (SkillServerClass skill : SkillServerClass.values()) {
            Skill skillInstance = newSkillInstance(skill.serverClass);

            byte expResult = skill.playerCastState;
            byte result = skillInstance.castPlayerState();
            System.out.println("Testing " + skill + ": Expected=" + expResult + " Result=" + result);
            assertEquals(expResult, result);
        }
    }

    @Test
    public void testGetBaseValue() {
        System.out.println("testGetBaseValue");
        for (SkillServerClass skill : SkillServerClass.values()) {
            String[] data = skill.data;
            HashMap<String, Integer> dataHeaders = skill.dataHeaders;
            Skill skillInstance = newSkillInstance(skill.serverClass);

            double expResult = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_BASEVALUE_HEADER);
            double result = skillInstance.getBaseValue();
            System.out.println("Testing " + skill + ": Expected=" + expResult + " Result=" + result);
            assertEquals(expResult, result, 0.0);
        }
    }

    @Test
    public void testGetMaxCooldown() {
        System.out.println("testGetMaxCooldown");
        for (SkillServerClass skill : SkillServerClass.values()) {
            String[] data = skill.data;
            HashMap<String, Integer> dataHeaders = skill.dataHeaders;
            Skill skillInstance = newSkillInstance(skill.serverClass);

            double expResult = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MAXCOOLDOWN_HEADER);
            double result = skillInstance.getMaxCooldown();
            System.out.println("Testing " + skill + ": Expected=" + expResult + " Result=" + result);
            assertEquals(expResult, result, 0.0);
        }
    }

    @Test
    public void testGetMultValue() {
        System.out.println("testGetMultValue");
        for (SkillServerClass skill : SkillServerClass.values()) {
            String[] data = skill.data;
            HashMap<String, Integer> dataHeaders = skill.dataHeaders;
            Skill skillInstance = newSkillInstance(skill.serverClass);

            double expResult = Globals.loadDoubleValue(data, dataHeaders, Globals.SKILL_MULTVALUE_HEADER);
            double result = skillInstance.getMultValue();
            System.out.println("Testing " + skill + ": Expected=" + expResult + " Result=" + result);
            assertEquals(expResult, result, 0.0);
        }
    }

    @Test
    public void testGetReqEquipSlot() {
        System.out.println("testGetReqEquipSlot");
        for (SkillServerClass skill : SkillServerClass.values()) {
            Skill skillInstance = newSkillInstance(skill.serverClass);

            byte expResult = skill.reqEquipSlot;
            byte result = skillInstance.getReqEquipSlot();
            System.out.println("Testing " + skill + ": Expected=" + expResult + " Result=" + result);
            assertEquals(expResult, result);
        }
    }

    @Test
    public void testGetReqWeapon() {
        System.out.println("testGetReqWeapon");
        for (SkillServerClass skill : SkillServerClass.values()) {
            String[] data = skill.data;
            HashMap<String, Integer> dataHeaders = skill.dataHeaders;
            Skill skillInstance = newSkillInstance(skill.serverClass);

            byte expResult = Globals.loadReqWeapon(data, dataHeaders);
            byte result = skillInstance.getReqWeapon();
            System.out.println("Testing " + skill + ": Expected=" + expResult + " Result=" + result);
            assertEquals(expResult, result);
        }
    }

    @Test
    public void testGetSkillCode() {
        System.out.println("testGetSkillCode");
        for (SkillServerClass skill : SkillServerClass.values()) {
            Skill skillInstance = newSkillInstance(skill.serverClass);
            byte expResult = skill.skillCode;
            byte result = skillInstance.getSkillCode();
            System.out.println("Testing " + skill + ": Expected=" + expResult + " Result=" + result);
            assertEquals(expResult, result);
        }
    }

    @Test
    public void testIsPassive() {
        System.out.println("testIsPassive");
        for (SkillServerClass skill : SkillServerClass.values()) {
            String[] data = skill.data;
            HashMap<String, Integer> dataHeaders = skill.dataHeaders;
            Skill skillInstance = newSkillInstance(skill.serverClass);

            boolean expResult = Globals.loadBooleanValue(data, dataHeaders, Globals.SKILL_PASSIVE_HEADER);
            boolean result = skillInstance.isPassive();
            System.out.println("Testing " + skill + ": Expected=" + expResult + " Result=" + result);
            assertEquals(expResult, result);
        }
    }

    @Test
    public void testGetReqLevel() {
        System.out.println("testGetReqLevel");
        for (SkillServerClass skill : SkillServerClass.values()) {
            String[] data = skill.data;
            HashMap<String, Integer> dataHeaders = skill.dataHeaders;
            Skill skillInstance = newSkillInstance(skill.serverClass);

            int expResult = Globals.loadSkillReqLevel(data, dataHeaders);
            int result = skillInstance.getReqLevel();
            System.out.println("Testing " + skill + ": Expected=" + expResult + " Result=" + result);
            assertEquals(expResult, result);
        }
    }
}
