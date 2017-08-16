package blockfighter.server.entities.player;

import blockfighter.server.LogicModule;
import blockfighter.server.RoomData;
import blockfighter.server.entities.damage.Damage;
import blockfighter.server.entities.player.skills.SkillPassiveDualSword;
import blockfighter.server.entities.player.skills.SkillPassiveShieldMastery;
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

    }

    @Test
    public void testStatReduction() {
        Player player = new Player(lm, (byte) 0, c, map);
        Damage dmg = new Damage(Globals.rng(2999), player, new Player(lm, (byte) 0, c, map));
        player.setStat(Globals.STAT_LEVEL, 100);
        player.setStat(Globals.STAT_DEFENSE, Globals.rng(1500));
        player.queueDamage(dmg);

        double finalDamage = dmg.getDamage() * player.getStats()[Globals.STAT_DAMAGEREDUCT];
        double expResult = player.getStats()[Globals.STAT_MINHP] - (int) finalDamage;
        expResult += player.getStats()[Globals.STAT_REGEN] / Globals.SERVER_LOGIC_TICKS_PER_SEC;

        player.update();
        Assert.assertEquals(expResult, player.getStats()[Globals.STAT_MINHP], 0);
    }

    @Test
    public void testShieldMasteryReduction() {
        Player player = new Player(lm, (byte) 0, c, map);
        Damage dmg = new Damage(Globals.rng(2999), player, new Player(lm, (byte) 0, c, map));
        player.setStat(Globals.STAT_LEVEL, 100);
        player.setStat(Globals.STAT_DEFENSE, Globals.rng(1500));
        player.setSkill(Globals.PASSIVE_SHIELDMASTERY, (byte) 30);
        player.setEquip(Globals.EQUIP_WEAPON, 100000);
        player.setEquip(Globals.EQUIP_OFFHAND, 110000);
        player.queueDamage(dmg);

        double baseReduct = player.getSkill(Globals.PASSIVE_SHIELDMASTERY).getCustomValue(SkillPassiveShieldMastery.CUSTOM_DATA_HEADERS[0]);
        double multReduct = player.getSkill(Globals.PASSIVE_SHIELDMASTERY).getCustomValue(SkillPassiveShieldMastery.CUSTOM_DATA_HEADERS[1]);
        double reduction = baseReduct + multReduct * player.getSkillLevel(Globals.PASSIVE_SHIELDMASTERY);

        double finalDamage = dmg.getDamage();
        finalDamage = finalDamage * player.getStats()[Globals.STAT_DAMAGEREDUCT];
        finalDamage = finalDamage * (1 - reduction);

        double expResult = player.getStats()[Globals.STAT_MINHP] - (int) finalDamage;
        expResult += player.getStats()[Globals.STAT_REGEN] / Globals.SERVER_LOGIC_TICKS_PER_SEC;

        player.update();
        Assert.assertEquals(expResult, player.getStats()[Globals.STAT_MINHP], 0);
    }

    @Test
    public void testToughSkinReduction() {
        Player player = new Player(lm, (byte) 0, c, map);
        Damage dmg = new Damage(Globals.rng(2999), player, new Player(lm, (byte) 0, c, map));
        player.setStat(Globals.STAT_LEVEL, 100);
        player.setStat(Globals.STAT_DEFENSE, Globals.rng(1500));
        player.setSkill(Globals.PASSIVE_TOUGH, (byte) 30);
        player.queueDamage(dmg);

        double baseReduct = player.getSkill(Globals.PASSIVE_TOUGH).getBaseValue();
        double multReduct = player.getSkill(Globals.PASSIVE_TOUGH).getMultValue();
        double reduction = baseReduct + multReduct * player.getSkillLevel(Globals.PASSIVE_TOUGH);

        double finalDamage = dmg.getDamage();
        finalDamage = finalDamage * player.getStats()[Globals.STAT_DAMAGEREDUCT];
        finalDamage = finalDamage * (1 - reduction);

        double expResult = player.getStats()[Globals.STAT_MINHP] - (int) finalDamage;
        expResult += player.getStats()[Globals.STAT_REGEN] / Globals.SERVER_LOGIC_TICKS_PER_SEC;

        player.update();
        Assert.assertEquals(expResult, player.getStats()[Globals.STAT_MINHP], 0);
    }

    @Test
    public void testDualMasteryReduction() {
        Player player = new Player(lm, (byte) 0, c, map);
        Damage dmg = new Damage(Globals.rng(2999), player, new Player(lm, (byte) 0, c, map));
        player.setStat(Globals.STAT_LEVEL, 100);
        player.setStat(Globals.STAT_DEFENSE, Globals.rng(1500));
        player.setSkill(Globals.PASSIVE_DUALSWORD, (byte) 30);

        player.setEquip(Globals.EQUIP_WEAPON, 100000);
        player.setEquip(Globals.EQUIP_OFFHAND, 100000);
        player.queueDamage(dmg);

        double multReduct = player.getSkill(Globals.PASSIVE_DUALSWORD).getCustomValue(SkillPassiveDualSword.CUSTOM_DATA_HEADERS[0]);
        double reduction = multReduct * player.getSkillLevel(Globals.PASSIVE_DUALSWORD);

        double finalDamage = dmg.getDamage();
        finalDamage = finalDamage * player.getStats()[Globals.STAT_DAMAGEREDUCT];
        finalDamage = finalDamage * (1 - reduction);

        double expResult = player.getStats()[Globals.STAT_MINHP] - (int) finalDamage;
        expResult += player.getStats()[Globals.STAT_REGEN] / Globals.SERVER_LOGIC_TICKS_PER_SEC;

        player.update();
        Assert.assertEquals(expResult, player.getStats()[Globals.STAT_MINHP], 0);
    }
}
