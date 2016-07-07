/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockfighter.server;

import blockfighter.server.entities.player.Player;
import java.util.concurrent.ConcurrentLinkedQueue;
import static org.hamcrest.CoreMatchers.*;
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

    LogicModule LOGIC = new LogicModule((byte) 0);

    @Before
    public void setUp() {
        //when(this.playerKeys.isEmpty()).thenReturn(true);
    }

    @Test
    public void testGetNextPlayerKeyReturnInvalidKeyWhenPlayerKeysIsEmpty() {
        System.out.println("getNextPlayerKey: Return -1 when there are no player keys in queue");
        LOGIC.setPlayerKeys(playerKeys);
        when(playerKeys.isEmpty()).thenReturn(true);

        byte expResult = -1;
        byte result = LOGIC.getNextPlayerKey();

        System.out.println("Expected: " + expResult);
        System.out.println("Result: " + result);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextPlayerKeyReturnAKeyWhenPlayerKeysIsNotEmpty() {
        System.out.println("getNextPlayerKey: Return a valid key when there are player keys in queue");
        LOGIC.setPlayerKeys(playerKeys);
        when(playerKeys.isEmpty()).thenReturn(false);
        when(playerKeys.poll()).thenReturn((byte) 1);

        byte expResult = 1;
        byte result = LOGIC.getNextPlayerKey();

        System.out.println("Expected: " + expResult);
        System.out.println("Result: " + result);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextMobKeyReturnInvalidKeyWhenPlayerKeysIsEmpty() {
        System.out.println("getNextMobKey: Return -1 when there are no mob keys in queue");
        LOGIC.setMobKeys(mobKeys);
        when(mobKeys.isEmpty()).thenReturn(true);

        byte expResult = -1;
        byte result = LOGIC.getNextMobKey();

        System.out.println("Expected: " + expResult);
        System.out.println("Result: " + result);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetNextMobKeyReturnAKeyWhenPlayerKeysIsNotEmpty() {
        System.out.println("getNextMobKey: Return a valid key when there are mob keys in queue");
        LOGIC.setMobKeys(mobKeys);
        when(mobKeys.isEmpty()).thenReturn(false);
        when(mobKeys.poll()).thenReturn((byte) 1);

        byte expResult = 1;
        byte result = LOGIC.getNextMobKey();

        System.out.println("Expected: " + expResult);
        System.out.println("Result: " + result);
        assertEquals(expResult, result);
    }

    @Test
    public void testQueueAddPlayer() {
        System.out.println("queueAddPlayer: Player Queue will add a new element");
        ConcurrentLinkedQueue<Player> mockQueue = mock(ConcurrentLinkedQueue.class);
        Player mockPlayer = mock(Player.class);
        
        LOGIC.setPlayAddQueue(mockQueue);
        LOGIC.queueAddPlayer(mockPlayer);
        
        verify(mockQueue).add(any());
    }

    @Test
    public void testQueuePlayerDirKeydown() {
        System.out.println("queuePlayerDirKeydown: Player Queue will add a new element");
        ConcurrentLinkedQueue<byte[]> mockQueue = mock(ConcurrentLinkedQueue.class);
        byte[] mockData = {0, 1, 2, 3};
        
        LOGIC.setPlayDirKeydownQueue(mockQueue);
        LOGIC.queuePlayerDirKeydown(mockData);
        
        verify(mockQueue).add(any());
    }

    @Test
    public void testQueuePlayerUseSkill() {
    }

    @Test
    public void testQueueAddProj() {
    }

    @Test
    public void testQueueAddMob() {
    }

    @Test
    public void testQueueProjEffect() {
    }

    @Test
    public void testGetNextProjKey() {
    }

    @Test
    public void testReturnProjKey() {
    }

    @Test
    public void testReturnMobKey() {
    }

    @Test
    public void testContainsPlayerID() {
    }

    @Test
    public void testGetPlayerKey() {
    }

}
