package blockfighter.server;

import blockfighter.server.net.ConnectionThread;
import blockfighter.server.net.Broadcaster;
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
            LogicModule logic = new LogicModule();
            Broadcaster broadcaster = new Broadcaster(logic);
            ConnectionThread server = new ConnectionThread(logic, broadcaster);

            logic.setBroadcaster(broadcaster);
            GregorianCalendar date = new GregorianCalendar();
            Globals.log("Server started", String.format("%1$td/%1$tm/%1$tY %1$tT", date), Globals.LOG_TYPE_ERR, false);
            Globals.log("Server started", String.format("%1$td/%1$tm/%1$tY %1$tT", date), Globals.LOG_TYPE_DATA, true);
            logic.start();
            server.start();
        } catch (Exception ex) {
            Globals.log(ex.getLocalizedMessage(), ex, true);
        }
    }

}
