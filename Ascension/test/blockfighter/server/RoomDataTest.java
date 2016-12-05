package blockfighter.server;

import blockfighter.server.entities.player.Player;
import blockfighter.shared.Globals;
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
public class RoomDataTest {

    @Mock
    ConcurrentLinkedQueue<Byte> playerKeys;
    @Mock
    ConcurrentLinkedQueue<Integer> mobKeys;

    @Test
    public void testGetNextPlayerKeyReturnInvalidKeyWhenPlayerKeysIsEmpty() {
        RoomData room = new RoomData((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64));
        room.setPlayerKeys(playerKeys);
        when(playerKeys.isEmpty()).thenReturn(true);

        byte expResult = -1;
        byte result = room.getNextPlayerKey();

        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextPlayerKeyReturnAKeyWhenPlayerKeysIsNotEmpty() {
        RoomData room = new RoomData((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64));
        room.setPlayerKeys(playerKeys);
        when(playerKeys.isEmpty()).thenReturn(false);
        when(playerKeys.poll()).thenReturn((byte) 1);

        byte expResult = 1;
        byte result = room.getNextPlayerKey();

        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextMobKeyReturnInvalidKeyWhenPlayerKeysIsEmpty() {
        RoomData room = new RoomData((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64));
        room.setMobKeys(mobKeys);
        when(mobKeys.isEmpty()).thenReturn(true);

        int expResult = -1;
        int result = room.getNextMobKey();

        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextMobKeyReturnAKeyWhenPlayerKeysIsNotEmpty() {
        RoomData room = new RoomData((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64));
        room.setMobKeys(mobKeys);
        when(mobKeys.isEmpty()).thenReturn(false);
        when(mobKeys.poll()).thenReturn(1);

        int expResult = 1;
        int result = room.getNextMobKey();

        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextProjKey() {
        RoomData room = new RoomData((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64));
        for (int i = 0; i < 10000; i++) {
            int result = room.getNextProjKey();
            assertEquals(i, result);
        }
    }

    @Test
    public void testReturnProjKey() {
        ConcurrentLinkedQueue<Integer> projKeys = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Integer> spy = spy(projKeys);

        RoomData room = new RoomData((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64));
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
        RoomData room = new RoomData((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64));
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
        RoomData room = new RoomData((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64)) {
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
        RoomData room = new RoomData((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64));
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
        RoomData room = new RoomData((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64)) {
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

        RoomData room = new RoomData((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64));
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
        RoomData room = new RoomData((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64));
        byte expResult = -1;
        byte result = room.getPlayerKey(UUID.randomUUID());

        assertEquals(expResult, result);
    }

    @Test
    public void testIsFullTrueWhenPlayerKeysIsEmpty() {
        RoomData room = new RoomData((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64));
        room.setPlayerKeys(playerKeys);
        when(playerKeys.isEmpty()).thenReturn(true);

        boolean expResult = true;
        boolean result = room.isFull();

        assertEquals(expResult, result);
    }

    @Test
    public void testIsInLevelRangeTrueWhenInRange() {
        RoomData room = new RoomData((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64));
        room.setMinLevel(1);
        room.setMaxLevel(30);
        for (int i = 1; i <= 30; i++) {
            assertTrue(room.isInLevelRange(i));
        }
    }

    @Test
    public void testIsInLevelRangeFalseWhenOutOfRange() {
        RoomData room = new RoomData((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64));
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
