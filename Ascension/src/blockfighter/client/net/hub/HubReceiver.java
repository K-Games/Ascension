package blockfighter.client.net.hub;

import blockfighter.shared.Globals;
import blockfighter.shared.ServerInfo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class HubReceiver extends Listener {

    @Override
    public void received(Connection c, Object object) {
        if (object instanceof ServerInfo[]) {
            HubHandler.process((ServerInfo[]) object, c);
        }
    }

    @Override
    public void disconnected(Connection c) {
        Globals.log(HubReceiver.class, "Hub Server disconnected " + c, Globals.LOG_TYPE_DATA, true);
    }
}
