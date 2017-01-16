package blockfighter.hub.net;

import blockfighter.hub.HubModule;
import blockfighter.shared.Globals;
import blockfighter.shared.ServerInfo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class HubReceiver implements Listener {

    @Override
    public void received(Connection c, Object object) {
        if (object instanceof byte[]) {
            HubHandler.process((byte[]) object, c);
        }

        if (object instanceof ServerInfo) {
            HubHandler.process((ServerInfo) object, c);
        }
    }

    @Override
    public void disconnected(Connection c) {
        try {
            HubModule.removeServerInfo(c);
            Globals.log(HubReceiver.class, "Disconnected " + c, Globals.LOG_TYPE_DATA);
        } catch (Exception e) {
            Globals.logError(e.toString(), e);
        }
    }
}
