package blockfighter.server;

import blockfighter.server.entities.player.Player;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LogicModuleTest {

    @Mock
    ConcurrentLinkedQueue<Byte> playerKeys;
    @Mock
    ConcurrentLinkedQueue<Byte> mobKeys;

    LogicModule logic;

    @Test
    public void testGetNextPlayerKeyReturnInvalidKeyWhenPlayerKeysIsEmpty() {
        System.out.println("getNextPlayerKey: Return -1 when there are no player keys in queue");
        logic = new LogicModule(playerKeys, null);

        when(playerKeys.isEmpty()).thenReturn(true);

        byte expResult = -1;
        byte result = logic.getNextPlayerKey();

        System.out.println("Expected: <" + expResult + ">, Result: <" + result + ">");

        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextPlayerKeyReturnAKeyWhenPlayerKeysIsNotEmpty() {
        System.out.println("getNextPlayerKey: Return a valid key when there are player keys in queue");

        logic = new LogicModule(playerKeys, null);
        when(playerKeys.isEmpty()).thenReturn(false);
        when(playerKeys.poll()).thenReturn((byte) 1);

        byte expResult = 1;
        byte result = logic.getNextPlayerKey();

        System.out.println("Expected: <" + expResult + ">, Result: <" + result + ">");

        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextMobKeyReturnInvalidKeyWhenPlayerKeysIsEmpty() {
        System.out.println("getNextMobKey: Return -1 when there are no mob keys in queue");

        logic = new LogicModule(null, mobKeys);
        when(mobKeys.isEmpty()).thenReturn(true);

        byte expResult = -1;
        byte result = logic.getNextMobKey();

        System.out.println("Expected: <" + expResult + ">, Result: <" + result + ">");

        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextMobKeyReturnAKeyWhenPlayerKeysIsNotEmpty() {
        System.out.println("getNextMobKey: Return a valid key when there are mob keys in queue");

        logic = new LogicModule(null, mobKeys);
        when(mobKeys.isEmpty()).thenReturn(false);
        when(mobKeys.poll()).thenReturn((byte) 1);

        byte expResult = 1;
        byte result = logic.getNextMobKey();

        System.out.println("Expected: <" + expResult + ">, Result: <" + result + ">");

        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextProjKey() {
        System.out.println("getNextProjKey: Get 10,000 Projectile Keys with no duplicates.");
        logic = new LogicModule((byte) 0);
        for (int i = 0; i < 10000; i++) {
            int result = logic.getNextProjKey();
            assertEquals(i, result);
        }
    }

    @Test
    public void testReturnProjKey() {
        System.out.println("returnProjKey: Return 200 Projectile Keys.");
        ConcurrentLinkedQueue<Integer> projKeys = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Integer> spy = spy(projKeys);

        logic = new LogicModule(spy);

        for (int i = 0; i < 200; i++) {
            int result = logic.getNextProjKey();
            assertEquals(i, result);
        }
        for (int i = 0; i < 200; i++) {
            logic.returnProjKey(i);
        }
        verify(spy, atLeast(200)).add(any());

        int expResult = 200;
        int result = spy.size();

        System.out.println("Expected: <" + expResult + ">, Result: <" + result + ">");

        assertEquals(expResult, result);
    }

    @Test
    public void testReturnMobKey() {
        System.out.println("returnMobKey: Return 200 Mob Keys.");
        ConcurrentLinkedQueue<Byte> spyMobKeys = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Byte> spy = spy(spyMobKeys);

        logic = new LogicModule(null, spy);

        for (int i = 0; i < 200; i++) {
            logic.returnMobKey((byte) i);
        }
        verify(spy, atLeast(200)).add(any());

        int expResult = 200;
        int result = spy.size();

        System.out.println("Expected: <" + expResult + ">, Result: <" + result + ">");

        assertEquals(expResult, result);
    }

    @Test
    public void testContainsPlayerIDTrueWhenAPlayerHasThisUUID() {
        System.out.println("containsPlayerID: True if a player has this UUID");
        ConcurrentHashMap<Byte, Player> mockPlayers = new ConcurrentHashMap<>();
        Player mockPlayer = mock(Player.class);
        this.logic = new LogicModule((byte) 0) {
            @Override
            public ConcurrentHashMap<Byte, Player> getPlayers() {
                return mockPlayers;
            }
        };
        mockPlayers.put((byte) 0, mockPlayer);

        logic = new LogicModule(mockPlayers);

        UUID id = UUID.randomUUID();

        when(mockPlayer.getUniqueID()).thenReturn(id);

        assertTrue(logic.containsPlayerID(id));
    }

    @Test
    public void testContainsPlayerIDFalseWhenNotInPlayers() {
        System.out.println("containsPlayerID: False if no players have this UUID");
        ConcurrentHashMap<Byte, Player> mockPlayers = new ConcurrentHashMap<>();
        Player mockPlayer = mock(Player.class);

        mockPlayers.put((byte) 0, mockPlayer);

        logic = new LogicModule(mockPlayers);

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        when(mockPlayer.getUniqueID()).thenReturn(id1);

        assertFalse(logic.containsPlayerID(id2));
    }

    @Test
    public void testGetPlayerKeyReturnValidKeyWithMatchingUUID() {
        System.out.println("getPlayerKey: Return player key with matching UUID");
        ConcurrentHashMap<Byte, Player> mockPlayers = new ConcurrentHashMap<>();
        Player mockPlayer = mock(Player.class);
        this.logic = new LogicModule((byte) 0) {
            @Override
            public ConcurrentHashMap<Byte, Player> getPlayers() {
                return mockPlayers;
            }
        };
        byte playerKey = 8;
        mockPlayers.put(playerKey, mockPlayer);

        logic = new LogicModule(mockPlayers);

        UUID id = UUID.randomUUID();

        when(mockPlayer.getUniqueID()).thenReturn(id);
        when(mockPlayer.getKey()).thenReturn(playerKey);

        byte expResult = playerKey;
        byte result = logic.getPlayerKey(id);

        System.out.println("Expected: <" + expResult + ">, Result: <" + result + ">");

        assertEquals(expResult, result);
    }

    @Test
    public void testGetPlayerKeyReturnInvalidKeyWithNoMatchingUUID() {
        System.out.println("getPlayerKey: Return -1 when no players have a matching UUID");
        ConcurrentHashMap<Byte, Player> mockPlayers = new ConcurrentHashMap<>();
        Player mockPlayer = mock(Player.class);

        byte playerKey = 8;
        mockPlayers.put(playerKey, mockPlayer);

        logic = new LogicModule(mockPlayers);

        UUID id = UUID.randomUUID();

        when(mockPlayer.getUniqueID()).thenReturn(id);
        when(mockPlayer.getKey()).thenReturn(playerKey);

        byte expResult = -1;
        byte result = logic.getPlayerKey(UUID.randomUUID());

        System.out.println("Expected: <" + expResult + ">, Result: <" + result + ">");

        assertEquals(expResult, result);
    }

    @Test
    public void testGetPlayerKeyReturnInvalidKeyWithNoPlayers() {
        System.out.println("getPlayerKey: Return -1 when no players exist");
        logic = new LogicModule((byte) 0);
        byte expResult = -1;
        byte result = logic.getPlayerKey(UUID.randomUUID());

        System.out.println("Expected: <" + expResult + ">, Result: <" + result + ">");

        assertEquals(expResult, result);
    }

    @Test
    public void testIsFullTrueWhenPlayerKeysIsEmpty() {
        System.out.println("isFull: Return true when there are no player keys in queue");

        logic = new LogicModule(playerKeys, null);
        when(playerKeys.isEmpty()).thenReturn(true);

        boolean expResult = true;
        boolean result = logic.isFull();

        System.out.println("Expected: <" + expResult + ">, Result: <" + result + ">");

        assertEquals(expResult, result);
    }

    @Test
    public void testIsInLevelRangeTrueWhenInRange() {
        System.out.println("isFull: Return true when there are level is in room's level range");

        logic = new LogicModule(1, 30);

        for (int i = 1; i <= 30; i++) {
            assertTrue(logic.isInLevelRange(i));
        }
    }

    @Test
    public void testIsInLevelRangeFalseWhenOutOfRange() {
        System.out.println("isFull: Return false when there are level is not in room's level range");

        logic = new LogicModule(1, 30);

        for (int i = -20; i <= 0; i++) {
            assertFalse(logic.isInLevelRange(i));
        }

        for (int i = 31; i <= 100; i++) {
            assertFalse(logic.isInLevelRange(i));
        }
    }
}
