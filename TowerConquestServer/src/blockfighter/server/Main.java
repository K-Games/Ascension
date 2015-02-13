package blockfighter.server;

import blockfighter.server.entities.boss.BossBase;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBase;
import blockfighter.server.net.PacketHandler;
import blockfighter.server.net.PacketReceiver;
import blockfighter.server.net.PacketSender;
import java.awt.Dimension;
import java.util.GregorianCalendar;
import javax.swing.JFrame;

/**
 * Start module of server
 *
 * @author Ken Kwan
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });

    }

    private static void createAndShowGUI() {
        try {
            LogicModule[] server_rooms = new LogicModule[Globals.SERVER_ROOMS];
            PacketSender.setLogic(server_rooms);
            PacketHandler.setLogic(server_rooms);
            PacketReceiver.setLogic(server_rooms);

            PacketSender sender = new PacketSender();
            PacketReceiver server_BossThread = new PacketReceiver();

            LogicModule.setPacketSender(sender);
            PacketReceiver.setPacketSender(sender);
            PacketHandler.setPacketSender(sender);

            Player.setPacketSender(sender);
            BossBase.setPacketSender(sender);
            ProjBase.setPacketSender(sender);

            GregorianCalendar date = new GregorianCalendar();
            Globals.log("Server started", String.format("%1$td/%1$tm/%1$tY %1$tT", date), Globals.LOG_TYPE_ERR, false);
            Globals.log("Server started", String.format("%1$td/%1$tm/%1$tY %1$tT", date), Globals.LOG_TYPE_DATA, true);

            for (byte i = 0; i < server_rooms.length; i++) {
                server_rooms[i] = new LogicModule(i);
                server_rooms[i].start();
                Globals.log("Initialization", "Room " + i, Globals.LOG_TYPE_ERR, false);
                Globals.log("Initialization", "Room " + i, Globals.LOG_TYPE_DATA, true);
            }
            server_BossThread.start();

        } catch (Exception ex) {
            Globals.log(ex.getLocalizedMessage(), ex, true);
        }
        JFrame frame = new JFrame("Tower Conquest Server ALPHA 0");

        //frame.setUndecorated(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setPreferredSize(new Dimension(350, 10));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
