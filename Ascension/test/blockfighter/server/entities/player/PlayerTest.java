package blockfighter.server.entities.player;

import blockfighter.server.LogicModule;
import blockfighter.server.RoomData;
import blockfighter.server.entities.buff.Buff;
import blockfighter.server.entities.buff.BuffDmgReduct;
import blockfighter.server.entities.buff.BuffPassiveBarrier;
import blockfighter.server.entities.buff.BuffPassiveResist;
import blockfighter.server.entities.buff.BuffShieldReflect;
import blockfighter.server.entities.buff.BuffSwordSlash;
import blockfighter.server.entities.buff.BuffSwordTaunt;
import blockfighter.server.entities.buff.BuffUtilityAdrenaline;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.damage.DamageBuilder;
import blockfighter.server.entities.player.skills.Skill;
import blockfighter.server.entities.player.skills.passive.SkillPassiveDualSword;
import blockfighter.server.entities.player.skills.passive.SkillPassiveShieldMastery;
import blockfighter.server.entities.player.skills.shield.SkillShieldReflect;
import blockfighter.server.entities.player.skills.sword.SkillSwordSlash;
import blockfighter.server.entities.player.skills.sword.SkillSwordTaunt;
import blockfighter.server.entities.player.skills.utility.SkillUtilityAdrenaline;
import blockfighter.server.maps.GameMap;
import blockfighter.server.maps.GameMapDebug;
import blockfighter.shared.Globals;
import com.esotericsoftware.kryonet.Connection;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlayerTest {

    LogicModule lm;
    RoomData roomData;
    Connection c;

    GameMap map = new GameMapDebug();

    @Before
    public void before() {
        lm = mock(LogicModule.class);
        roomData = mock(RoomData.class);
        c = mock(Connection.class);
        when(lm.getRoomData()).thenReturn(roomData);
        when(roomData.getPlayers()).thenReturn(new ConcurrentHashMap<>());
        when(c.sendUDP(any())).thenReturn(0);
        Globals.SERVER_MODE = true;
    }

    private Player newPlayer() {
        Player player = new Player(lm, (byte) 0, c, map);
        player.setStat(Globals.STAT_LEVEL, 100);
        player.setStat(Globals.STAT_DEFENSE, 100);
        return player;
    }

    private void testDamageReduction(Player player) {
        testDamageReduction(player, 0);
    }

    private void testDamageReduction(Player player, double extraReduct) {
        Damage dmg = new DamageBuilder()
                .setDamage(10000)
                .setOwner(player)
                .setTarget(new Player(lm, (byte) 0, c, map))
                .build();

        player.queueDamage(dmg);

        double finalDamage = dmg.getDamage() * player.getStats()[Globals.STAT_DAMAGEREDUCT];
        finalDamage = finalDamage * (1 - extraReduct);

        double expResult = player.getStats()[Globals.STAT_MINHP] - (int) finalDamage;
        expResult += player.getStats()[Globals.STAT_REGEN] / Globals.SERVER_LOGIC_TICKS_PER_SEC;
        if (expResult > player.getStats()[Globals.STAT_MAXHP]) {
            expResult = player.getStats()[Globals.STAT_MAXHP];
        }
        player.update();
        System.out.println("Testing Dmg Reduct: Expected=" + expResult + " Result=" + player.getStats()[Globals.STAT_MINHP]);
        Assert.assertEquals(expResult, player.getStats()[Globals.STAT_MINHP], 0);
    }

    @Test
    public void testStatReduction() {
        Player player = newPlayer();
        testDamageReduction(player);
    }

    @Test
    public void testShieldMasteryReduction() {
        Player player = newPlayer();

        player.setSkill(Globals.PASSIVE_SHIELDMASTERY, (byte) 30);
        player.setEquip(Globals.EQUIP_WEAPON, 100000);
        player.setEquip(Globals.EQUIP_OFFHAND, 110000);

        double baseReduct = player.getSkill(Globals.PASSIVE_SHIELDMASTERY).getCustomValue(SkillPassiveShieldMastery.CUSTOM_DATA_HEADERS[0]);
        double multReduct = player.getSkill(Globals.PASSIVE_SHIELDMASTERY).getCustomValue(SkillPassiveShieldMastery.CUSTOM_DATA_HEADERS[1]);
        double reduction = baseReduct + multReduct * player.getSkillLevel(Globals.PASSIVE_SHIELDMASTERY);

        testDamageReduction(player, reduction);
    }

    @Test
    public void testToughSkinReduction() {
        Player player = newPlayer();

        player.setSkill(Globals.PASSIVE_TOUGH, (byte) 30);

        double baseReduct = player.getSkill(Globals.PASSIVE_TOUGH).getBaseValue();
        double multReduct = player.getSkill(Globals.PASSIVE_TOUGH).getMultValue();
        double reduction = baseReduct + multReduct * player.getSkillLevel(Globals.PASSIVE_TOUGH);

        testDamageReduction(player, reduction);
    }

    @Test
    public void testDualMasteryReduction() {
        Player player = newPlayer();

        player.setSkill(Globals.PASSIVE_DUALSWORD, (byte) 30);

        player.setEquip(Globals.EQUIP_WEAPON, 100000);
        player.setEquip(Globals.EQUIP_OFFHAND, 100000);

        double multReduct = player.getSkill(Globals.PASSIVE_DUALSWORD).getCustomValue(SkillPassiveDualSword.CUSTOM_DATA_HEADERS[0]);
        double reduction = multReduct * player.getSkillLevel(Globals.PASSIVE_DUALSWORD);

        testDamageReduction(player, reduction);
    }

    @Test
    public void testResistanceBuffReduction() {
        Player player = newPlayer();

        player.setSkill(Globals.PASSIVE_RESIST, (byte) 30);
        player.queueBuff(new BuffPassiveResist(lm, 2000, 1));

        testDamageReduction(player, 1);
    }

    @Test
    public void testBarrierBuffReduction() {
        Player player = newPlayer();
        Damage dmg = new DamageBuilder()
                .setDamage(20000)
                .setOwner(player)
                .setTarget(new Player(lm, (byte) 0, c, map))
                .build();

        player.setStat(Globals.STAT_LEVEL, 100);
        player.setStat(Globals.STAT_DEFENSE, 500);
        player.setSkill(Globals.PASSIVE_RESIST, (byte) 30);
        player.queueBuff(new BuffPassiveBarrier(lm, 500, player));
        player.queueDamage(dmg);

        double finalDamage = dmg.getDamage() * player.getStats()[Globals.STAT_DAMAGEREDUCT];
        finalDamage -= 500;

        double expResult = player.getStats()[Globals.STAT_MINHP] - (int) finalDamage;
        expResult += player.getStats()[Globals.STAT_REGEN] / Globals.SERVER_LOGIC_TICKS_PER_SEC;
        if (expResult > player.getStats()[Globals.STAT_MAXHP]) {
            expResult = player.getStats()[Globals.STAT_MAXHP];
        }
        player.update();
        Assert.assertEquals(expResult, player.getStats()[Globals.STAT_MINHP], 0);

    }

    @Test
    public void testSlashBuffReduction() {
        Player player = newPlayer();
        player.setSkill(Globals.SWORD_SLASH, (byte) 30);

        double buffduration = player.getSkill(Globals.SWORD_SLASH).getCustomValue(SkillSwordSlash.CUSTOM_DATA_HEADERS[0]);
        BuffSwordSlash buff = new BuffSwordSlash(lm, (int) buffduration, player.getSkill(Globals.SWORD_SLASH).getCustomValue(SkillSwordSlash.CUSTOM_DATA_HEADERS[1]), player);
        player.queueBuff(buff);

        testDamageReduction(player, buff.getDmgReduction());
    }

    @Test
    public void testTauntBuffReduction() {
        Player player = newPlayer();

        player.setSkill(Globals.SWORD_TAUNT, (byte) 30);

        double buffDuration = player.getSkill(Globals.SWORD_TAUNT).getCustomValue(SkillSwordTaunt.CUSTOM_DATA_HEADERS[0]);
        BuffSwordTaunt buff = new BuffSwordTaunt(lm, (int) buffDuration,
                player.getSkill(Globals.SWORD_TAUNT).getCustomValue(SkillSwordTaunt.CUSTOM_DATA_HEADERS[2]),
                player.getSkill(Globals.SWORD_TAUNT).getCustomValue(SkillSwordTaunt.CUSTOM_DATA_HEADERS[1]),
                player);
        player.queueBuff(buff);

        testDamageReduction(player, buff.getDmgReduction());
    }

    @Test
    public void testAdrenalineBuffReduction() {
        Player player = newPlayer();

        player.setSkill(Globals.UTILITY_ADRENALINE, (byte) 30);

        Skill skill = player.getSkill(Globals.UTILITY_ADRENALINE);

        double buffDuration = skill.getCustomValue(SkillUtilityAdrenaline.CUSTOM_DATA_HEADERS[0]);
        BuffUtilityAdrenaline buff = new BuffUtilityAdrenaline(lm, (int) buffDuration,
                skill.getBaseValue() + skill.getMultValue() * player.getSkillLevel(Globals.UTILITY_ADRENALINE),
                player);
        player.queueBuff(buff);

        testDamageReduction(player, buff.getDmgReduction());
    }

    @Test
    public void testReflectBuffReduction() {

        Player player = newPlayer();

        player.setSkill(Globals.SHIELD_REFLECT, (byte) 30);

        Skill skill = player.getSkill(Globals.SHIELD_REFLECT);

        double buffDuration = skill.getCustomValue(SkillShieldReflect.CUSTOM_DATA_HEADERS[0]);

        BuffShieldReflect buff = new BuffShieldReflect(lm, (int) buffDuration,
                skill.getBaseValue() + skill.getMultValue() * player.getSkillLevel(Globals.SHIELD_REFLECT),
                player, player, skill.getCustomValue(SkillShieldReflect.CUSTOM_DATA_HEADERS[0]));

        player.queueBuff(buff);
        testDamageReduction(player, buff.getDmgReduction());
    }

    @Test
    public void testMultipleBuffReduction() {
        Player player = newPlayer();

        player.setSkill(Globals.SHIELD_REFLECT, (byte) 30);
        player.setSkill(Globals.UTILITY_ADRENALINE, (byte) 30);
        player.setSkill(Globals.SWORD_TAUNT, (byte) 30);
        player.setSkill(Globals.SWORD_SLASH, (byte) 30);

        Skill[] skills = {player.getSkill(Globals.SHIELD_REFLECT),
            player.getSkill(Globals.UTILITY_ADRENALINE),
            player.getSkill(Globals.SWORD_TAUNT),
            player.getSkill(Globals.SWORD_SLASH)
        };

        Buff[] buffs = new Buff[4];

        double buffDuration = skills[0].getCustomValue(SkillShieldReflect.CUSTOM_DATA_HEADERS[0]);
        buffs[0] = new BuffShieldReflect(lm, (int) buffDuration,
                skills[0].getBaseValue() + skills[0].getMultValue() * player.getSkillLevel(Globals.SHIELD_REFLECT),
                player, player, skills[0].getCustomValue(SkillShieldReflect.CUSTOM_DATA_HEADERS[0]));

        buffDuration = skills[1].getCustomValue(SkillUtilityAdrenaline.CUSTOM_DATA_HEADERS[0]);
        buffs[1] = new BuffUtilityAdrenaline(lm, (int) buffDuration,
                skills[1].getBaseValue() + skills[1].getMultValue() * player.getSkillLevel(Globals.UTILITY_ADRENALINE),
                player);

        buffDuration = skills[2].getCustomValue(SkillSwordTaunt.CUSTOM_DATA_HEADERS[0]);
        buffs[2] = new BuffSwordTaunt(lm, (int) buffDuration,
                skills[2].getCustomValue(SkillSwordTaunt.CUSTOM_DATA_HEADERS[2]),
                skills[2].getCustomValue(SkillSwordTaunt.CUSTOM_DATA_HEADERS[1]),
                player);

        buffDuration = skills[3].getCustomValue(SkillSwordSlash.CUSTOM_DATA_HEADERS[0]);
        buffs[3] = new BuffSwordSlash(lm, (int) buffDuration,
                skills[3].getCustomValue(SkillSwordSlash.CUSTOM_DATA_HEADERS[1]),
                player);

        double dmgReduct = 1;
        for (Buff buff : buffs) {
            player.queueBuff(buff);
            dmgReduct *= ((BuffDmgReduct) buff).getDmgTakenMult();
        }
        testDamageReduction(player, 1D - dmgReduct);
    }
}
