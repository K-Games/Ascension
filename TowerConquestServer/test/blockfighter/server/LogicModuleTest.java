/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockfighter.server;

import blockfighter.server.entities.player.Player;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import static org.junit.Assert.*;
import org.junit.Before;
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

    @Before
    public void setUp() {
        this.logic = new LogicModule((byte) 0);
    }

    @Test
    public void testGetNextPlayerKeyReturnInvalidKeyWhenPlayerKeysIsEmpty() {
        System.out.println("getNextPlayerKey: Return -1 when there are no player keys in queue");
        this.logic.setPlayerKeys(this.playerKeys);
        when(this.playerKeys.isEmpty()).thenReturn(true);

        byte expResult = -1;
        byte result = this.logic.getNextPlayerKey();

        System.out.println("Expected: <" + expResult + ">, Result: <" + result + ">");

        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextPlayerKeyReturnAKeyWhenPlayerKeysIsNotEmpty() {
        System.out.println("getNextPlayerKey: Return a valid key when there are player keys in queue");
        this.logic.setPlayerKeys(this.playerKeys);
        when(this.playerKeys.isEmpty()).thenReturn(false);
        when(this.playerKeys.poll()).thenReturn((byte) 1);

        byte expResult = 1;
        byte result = this.logic.getNextPlayerKey();

        System.out.println("Expected: <" + expResult + ">, Result: <" + result + ">");

        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextMobKeyReturnInvalidKeyWhenPlayerKeysIsEmpty() {
        System.out.println("getNextMobKey: Return -1 when there are no mob keys in queue");
        this.logic.setMobKeys(this.mobKeys);
        when(this.mobKeys.isEmpty()).thenReturn(true);

        byte expResult = -1;
        byte result = this.logic.getNextMobKey();

        System.out.println("Expected: <" + expResult + ">, Result: <" + result + ">");

        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextMobKeyReturnAKeyWhenPlayerKeysIsNotEmpty() {
        System.out.println("getNextMobKey: Return a valid key when there are mob keys in queue");
        this.logic.setMobKeys(this.mobKeys);
        when(this.mobKeys.isEmpty()).thenReturn(false);
        when(this.mobKeys.poll()).thenReturn((byte) 1);

        byte expResult = 1;
        byte result = this.logic.getNextMobKey();

        System.out.println("Expected: <" + expResult + ">, Result: <" + result + ">");

        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextProjKey() {
        System.out.println("getNextProjKey: Get 10,000 Projectile Keys with no duplicates.");
        for (int i = 0; i < 10000; i++) {
            int result = this.logic.getNextProjKey();
            assertEquals(i, result);
        }
    }

    @Test
    public void testReturnProjKey() {
        System.out.println("returnProjKey: Return 200 Projectile Keys.");
        ConcurrentLinkedQueue<Integer> projKeys = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Integer> spy = spy(projKeys);

        this.logic.setProjKeys(spy);

        for (int i = 0; i < 200; i++) {
            int result = this.logic.getNextProjKey();
            assertEquals(i, result);
        }
        for (int i = 0; i < 200; i++) {
            this.logic.returnProjKey(i);
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

        this.logic.setMobKeys(spy);

        for (int i = 0; i < 200; i++) {
            this.logic.returnMobKey((byte) i);
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

        mockPlayers.put((byte) 0, mockPlayer);
        this.logic.setPlayers(mockPlayers);

        UUID id = UUID.randomUUID();

        when(mockPlayer.getUniqueID()).thenReturn(id);

        assertTrue(this.logic.containsPlayerID(id));
    }

    @Test
    public void testContainsPlayerIDFalseWhenNotInPlayers() {
        System.out.println("containsPlayerID: False if no players have this UUID");
        ConcurrentHashMap<Byte, Player> mockPlayers = new ConcurrentHashMap<>();
        Player mockPlayer = mock(Player.class);

        mockPlayers.put((byte) 0, mockPlayer);
        this.logic.setPlayers(mockPlayers);

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        when(mockPlayer.getUniqueID()).thenReturn(id1);

        assertFalse(this.logic.containsPlayerID(id2));
    }

    @Test
    public void testGetPlayerKeyReturnValidKeyWithMatchingUUID() {
        System.out.println("getPlayerKey: Return player key with matching UUID");
        ConcurrentHashMap<Byte, Player> mockPlayers = new ConcurrentHashMap<>();
        Player mockPlayer = mock(Player.class);

        byte playerKey = 8;
        mockPlayers.put(playerKey, mockPlayer);
        this.logic.setPlayers(mockPlayers);

        UUID id = UUID.randomUUID();

        when(mockPlayer.getUniqueID()).thenReturn(id);
        when(mockPlayer.getKey()).thenReturn(playerKey);

        byte expResult = playerKey;
        byte result = this.logic.getPlayerKey(id);

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
        this.logic.setPlayers(mockPlayers);

        UUID id = UUID.randomUUID();

        when(mockPlayer.getUniqueID()).thenReturn(id);
        when(mockPlayer.getKey()).thenReturn(playerKey);

        byte expResult = -1;
        byte result = this.logic.getPlayerKey(UUID.randomUUID());

        System.out.println("Expected: <" + expResult + ">, Result: <" + result + ">");

        assertEquals(expResult, result);
    }

    @Test
    public void testGetPlayerKeyReturnInvalidKeyWithNoPlayers() {
        System.out.println("getPlayerKey: Return -1 when no players exist");
        byte expResult = -1;
        byte result = this.logic.getPlayerKey(UUID.randomUUID());

        System.out.println("Expected: <" + expResult + ">, Result: <" + result + ">");

        assertEquals(expResult, result);
    }
}
