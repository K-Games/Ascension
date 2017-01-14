package blockfighter.client.net;

import blockfighter.client.AscensionClient;
import blockfighter.client.LogicModule;
import blockfighter.client.screen.ScreenIngame;
import blockfighter.client.screen.ScreenLoading;
import blockfighter.client.screen.ScreenServerList;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class PacketReceiver extends Listener {

    private static LogicModule logic;

    public static void init() {
        logic = AscensionClient.getLogicModule();
    }

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
        if (logic.getScreen() instanceof ScreenIngame || logic.getScreen() instanceof ScreenLoading) {
            logic.returnMenu();
        } else if (logic.getScreen() instanceof ScreenServerList) {
            logic.shutdownClient(ScreenServerList.STATUS_DISCONNECTED);
        }
    }

}
