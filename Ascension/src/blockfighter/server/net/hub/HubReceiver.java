package blockfighter.server.net.hub;

import blockfighter.shared.Globals;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class HubReceiver extends Listener {

    @Override
    public void received(Connection c, Object object) {
        if (object instanceof byte[]) {
            HubHandler.process((byte[]) object, c);
        }
    }

    @Override
    public void disconnected(Connection c) {
        Globals.log(HubReceiver.class, "Lost connection to Hub Server.", Globals.LOG_TYPE_DATA, true);
    }
}
