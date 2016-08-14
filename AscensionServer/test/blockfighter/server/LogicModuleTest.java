package blockfighter.server;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LogicModuleTest {

    @Mock
    ConcurrentLinkedQueue<Byte> playerKeys;
    @Mock
    ConcurrentLinkedQueue<Integer> mobKeys;

    LogicModule logic;

}
