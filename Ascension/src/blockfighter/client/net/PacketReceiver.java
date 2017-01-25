package blockfighter.client.net;

import blockfighter.client.Core;
import blockfighter.client.screen.ScreenIngame;
import blockfighter.client.screen.ScreenLoading;
import blockfighter.client.screen.ScreenServerList;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class PacketReceiver implements Listener {

    @Override
    public void received(Connection connection, Object object) {
        if (object instanceof byte[][]) {
            for (byte[] data : (byte[][]) object) {
                PacketHandler.process(data);
            }
        }
    }

    @Override
    public void disconnected(Connection connection) {
        if (Core.getLogicModule().getScreen() instanceof ScreenIngame || Core.getLogicModule().getScreen() instanceof ScreenLoading) {
            Core.getLogicModule().returnMenu();
        } else if (Core.getLogicModule().getScreen() instanceof ScreenServerList) {
            Core.getLogicModule().shutdownClient(ScreenServerList.STATUS_DISCONNECTED);
        }
    }

}
