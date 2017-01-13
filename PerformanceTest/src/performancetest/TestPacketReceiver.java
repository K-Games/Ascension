package performancetest;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class TestPacketReceiver extends Listener {

    private boolean isConnected = true;
    private final TestGameClient gC;

    public TestPacketReceiver(final TestGameClient g) {
        this.gC = g;
    }

    @Override
    public void connected(Connection connection) {
    }

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof byte[][]) {
            for (byte[] data : (byte[][]) object) {
                TestPacketHandler.process(data, this.gC);
            }
        }
    }

    @Override
    public void disconnected(Connection connection) {
        this.isConnected = false;
    }

    public boolean isConnected() {
        return this.isConnected;
    }
}
