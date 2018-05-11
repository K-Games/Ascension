package blockfighter.server;

import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.Projectile;
import blockfighter.shared.Globals;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LogicModuleTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Mock
    ConcurrentLinkedQueue<Byte> playerKeys;
    @Mock
    ConcurrentLinkedQueue<Integer> mobKeys;

    LogicModule logic;

    @Test
    public void testQueueAddPlayer() {
        ConcurrentLinkedQueue<Player> playAddQueue = new ConcurrentLinkedQueue<>();
        Player mockPlayer = mock(Player.class);
        LogicModule lm = new LogicModule((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64));
        lm.setPlayAddQueue(playAddQueue);
        lm.queueAddPlayer(mockPlayer);

        assertTrue(playAddQueue.contains(mockPlayer));
    }

    @Test
    public void testQueuePlayerDirKeydown() {
        ConcurrentLinkedQueue<byte[]> playDirKeydownQueue = new ConcurrentLinkedQueue<>();
        byte[] data = new byte[0];
        LogicModule lm = new LogicModule((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64));
        lm.setPlayDirKeydownQueue(playDirKeydownQueue);
        lm.queuePlayerDirKeydown(data);

        assertTrue(playDirKeydownQueue.contains(data));
    }

    @Test
    public void testQueuePlayerUseSkill() {
        ConcurrentLinkedQueue<byte[]> useSkillQueue = new ConcurrentLinkedQueue<>();
        byte[] data = new byte[0];
        LogicModule lm = new LogicModule((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64));
        lm.setPlayUseSkillQueue(useSkillQueue);
        lm.queuePlayerUseSkill(data);

        assertTrue(useSkillQueue.contains(data));
    }

    @Test
    public void testQueueAddProj() {
        ConcurrentLinkedQueue<Projectile> projAddQueue = new ConcurrentLinkedQueue<>();
        Projectile projectile = mock(Projectile.class);
        LogicModule lm = new LogicModule((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64));
        lm.setProjAddQueue(projAddQueue);
        lm.queueAddProj(projectile);

        assertTrue(projAddQueue.contains(projectile));
    }

    @Test
    public void testQueueProjEffect() {
        ConcurrentLinkedQueue<Projectile> projQueue = new ConcurrentLinkedQueue<>();
        Projectile projectile = mock(Projectile.class);
        LogicModule lm = new LogicModule((byte) 0, (byte) Globals.rng(65), (byte) ((byte) Globals.rng(65) + 64));
        lm.setProjEffectQueue(projQueue);
        lm.queueProjEffect(projectile);

        assertTrue(projQueue.contains(projectile));
    }

}
