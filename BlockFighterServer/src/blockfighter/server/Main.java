package blockfighter.server;

import blockfighter.server.net.PacketReceiver;
import blockfighter.server.net.PacketSender;
import java.util.GregorianCalendar;

/**
 * Start module of server
 *
 * @author Ken
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            LogicModule[] server_rooms = new LogicModule[Globals.SERVER_ROOMS];
            PacketSender sender = new PacketSender(server_rooms);
            PacketReceiver server_BossThread = new PacketReceiver(server_rooms, sender);

            GregorianCalendar date = new GregorianCalendar();
            Globals.log("Server started", String.format("%1$td/%1$tm/%1$tY %1$tT", date), Globals.LOG_TYPE_ERR, false);
            Globals.log("Server started", String.format("%1$td/%1$tm/%1$tY %1$tT", date), Globals.LOG_TYPE_DATA, true);

            for (byte i = 0; i < server_rooms.length; i++) {
                server_rooms[i] = new LogicModule(i);
                server_rooms[i].setPacketSender(sender);
                server_rooms[i].start();
                Globals.log("Initialization", "Room " + i, Globals.LOG_TYPE_ERR, false);
                Globals.log("Initialization", "Room " + i, Globals.LOG_TYPE_DATA, true);
            }
            server_BossThread.start();

        } catch (Exception ex) {
            Globals.log(ex.getLocalizedMessage(), ex, true);
        }
    }

}
