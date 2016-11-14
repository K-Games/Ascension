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
public class RoomTest {

    @Mock
    ConcurrentLinkedQueue<Byte> playerKeys;
    @Mock
    ConcurrentLinkedQueue<Integer> mobKeys;

    @Test
    public void testGetNextPlayerKeyReturnInvalidKeyWhenPlayerKeysIsEmpty() {

        Room room = new Room((byte) 0, (byte) 0);
        room.setPlayerKeys(playerKeys);
        when(playerKeys.isEmpty()).thenReturn(true);

        byte expResult = -1;
        byte result = room.getNextPlayerKey();

        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextPlayerKeyReturnAKeyWhenPlayerKeysIsNotEmpty() {

        Room room = new Room((byte) 0, (byte) 0);
        room.setPlayerKeys(playerKeys);
        when(playerKeys.isEmpty()).thenReturn(false);
        when(playerKeys.poll()).thenReturn((byte) 1);

        byte expResult = 1;
        byte result = room.getNextPlayerKey();

        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextMobKeyReturnInvalidKeyWhenPlayerKeysIsEmpty() {

        Room room = new Room((byte) 0, (byte) 0);
        room.setMobKeys(mobKeys);
        when(mobKeys.isEmpty()).thenReturn(true);

        int expResult = -1;
        int result = room.getNextMobKey();

        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextMobKeyReturnAKeyWhenPlayerKeysIsNotEmpty() {

        Room room = new Room((byte) 0, (byte) 0);
        room.setMobKeys(mobKeys);
        when(mobKeys.isEmpty()).thenReturn(false);
        when(mobKeys.poll()).thenReturn(1);

        int expResult = 1;
        int result = room.getNextMobKey();

        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextProjKey() {

        Room room = new Room((byte) 0, (byte) 0);
        for (int i = 0; i < 10000; i++) {
            int result = room.getNextProjKey();
            assertEquals(i, result);
        }
    }

    @Test
    public void testReturnProjKey() {

        ConcurrentLinkedQueue<Integer> projKeys = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Integer> spy = spy(projKeys);

        Room room = new Room((byte) 0, (byte) 0);
        room.setProjKeys(spy);
        for (int i = 0; i < 200; i++) {
            int result = room.getNextProjKey();
            assertEquals(i, result);
        }
        for (int i = 0; i < 200; i++) {
            room.returnProjKey(i);
        }
        verify(spy, atLeast(200)).add(any());

        int expResult = 200;
        int result = spy.size();

        assertEquals(expResult, result);
    }

    @Test
    public void testReturnMobKey() {

        ConcurrentLinkedQueue<Integer> spyMobKeys = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Integer> spy = spy(spyMobKeys);
        Room room = new Room((byte) 0, (byte) 0);
        room.setMobKeys(spy);
        for (int i = 0; i < 200; i++) {
            room.returnMobKey((byte) i);
        }
        verify(spy, atLeast(200)).add(any());

        int expResult = 200;
        int result = spy.size();

        assertEquals(expResult, result);
    }

    @Test
    public void testContainsPlayerIDTrueWhenAPlayerHasThisUUID() {

        ConcurrentHashMap<Byte, Player> mockPlayers = new ConcurrentHashMap<>();
        Player mockPlayer = mock(Player.class);
        Room room = new Room((byte) 0, (byte) 0) {
            @Override
            public ConcurrentHashMap<Byte, Player> getPlayers() {
                return mockPlayers;
            }
        };
        mockPlayers.put((byte) 0, mockPlayer);
        room.setPlayers(mockPlayers);

        UUID id = UUID.randomUUID();

        when(mockPlayer.getUniqueID()).thenReturn(id);

        assertTrue(room.containsPlayerID(id));
    }

    @Test
    public void testContainsPlayerIDFalseWhenNotInPlayers() {

        ConcurrentHashMap<Byte, Player> mockPlayers = new ConcurrentHashMap<>();
        Player mockPlayer = mock(Player.class);

        mockPlayers.put((byte) 0, mockPlayer);
        Room room = new Room((byte) 0, (byte) 0);
        room.setPlayers(mockPlayers);

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        when(mockPlayer.getUniqueID()).thenReturn(id1);

        assertFalse(room.containsPlayerID(id2));
    }

    @Test
    public void testGetPlayerKeyReturnValidKeyWithMatchingUUID() {

        ConcurrentHashMap<Byte, Player> mockPlayers = new ConcurrentHashMap<>();
        Player mockPlayer = mock(Player.class);
        Room room = new Room((byte) 0, (byte) 0) {
            @Override
            public ConcurrentHashMap<Byte, Player> getPlayers() {
                return mockPlayers;
            }
        };
        byte playerKey = 8;
        mockPlayers.put(playerKey, mockPlayer);

        room.setPlayers(mockPlayers);

        UUID id = UUID.randomUUID();

        when(mockPlayer.getUniqueID()).thenReturn(id);
        when(mockPlayer.getKey()).thenReturn(playerKey);

        byte expResult = playerKey;
        byte result = room.getPlayerKey(id);

        assertEquals(expResult, result);
    }

    @Test
    public void testGetPlayerKeyReturnInvalidKeyWithNoMatchingUUID() {

        ConcurrentHashMap<Byte, Player> mockPlayers = new ConcurrentHashMap<>();
        Player mockPlayer = mock(Player.class);

        byte playerKey = 8;
        mockPlayers.put(playerKey, mockPlayer);

        Room room = new Room((byte) 0, (byte) 0);
        room.setPlayers(mockPlayers);

        UUID id = UUID.randomUUID();

        when(mockPlayer.getUniqueID()).thenReturn(id);
        when(mockPlayer.getKey()).thenReturn(playerKey);

        byte expResult = -1;
        byte result = room.getPlayerKey(UUID.randomUUID());

        assertEquals(expResult, result);
    }

    @Test
    public void testGetPlayerKeyReturnInvalidKeyWithNoPlayers() {

        Room room = new Room((byte) 0, (byte) 0);
        byte expResult = -1;
        byte result = room.getPlayerKey(UUID.randomUUID());

        assertEquals(expResult, result);
    }

    @Test
    public void testIsFullTrueWhenPlayerKeysIsEmpty() {

        Room room = new Room((byte) 0, (byte) 0);
        room.setPlayerKeys(playerKeys);
        when(playerKeys.isEmpty()).thenReturn(true);

        boolean expResult = true;
        boolean result = room.isFull();

        assertEquals(expResult, result);
    }

    @Test
    public void testIsInLevelRangeTrueWhenInRange() {

        Room room = new Room((byte) 0, (byte) 0);
        room.setMinLevel(1);
        room.setMaxLevel(30);
        for (int i = 1; i <= 30; i++) {
            assertTrue(room.isInLevelRange(i));
        }
    }

    @Test
    public void testIsInLevelRangeFalseWhenOutOfRange() {

        Room room = new Room((byte) 0, (byte) 0);
        room.setMinLevel(1);
        room.setMaxLevel(30);

        for (int i = -20; i <= 0; i++) {
            assertFalse(room.isInLevelRange(i));
        }

        for (int i = 31; i <= 100; i++) {
            assertFalse(room.isInLevelRange(i));
        }
    }
}
