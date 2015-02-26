package blockfighter.server;

import blockfighter.server.entities.boss.BossBase;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.ProjBase;
import blockfighter.server.net.PacketHandler;
import blockfighter.server.net.PacketReceiver;
import blockfighter.server.net.PacketSender;
import java.awt.Dimension;
import java.util.GregorianCalendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * Start module of server
 *
 * @author Ken Kwan
 */
public class Main {

    private static ScheduledExecutorService senderSch = Executors.newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder()
            .namingPattern("PacketSenderScheduler-%d")
            .daemon(true)
            .priority(Thread.NORM_PRIORITY)
            .build());

    private static ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(Globals.SERVER_ROOMS,
            new BasicThreadFactory.Builder()
            .namingPattern("ServerRoom-%d")
            .daemon(true)
            .priority(Thread.NORM_PRIORITY)
            .build());

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
        try {
            LogicModule[] server_rooms = new LogicModule[Globals.SERVER_ROOMS];
            PacketSender.setLogic(server_rooms);
            PacketHandler.setLogic(server_rooms);
            PacketReceiver.setLogic(server_rooms);

            PacketSender packetSender = new PacketSender();
            PacketReceiver packetReceiver = new PacketReceiver();

            LogicModule.setPacketSender(packetSender);
            PacketReceiver.setPacketSender(packetSender);
            PacketHandler.setPacketSender(packetSender);

            Player.setPacketSender(packetSender);
            BossBase.setPacketSender(packetSender);
            ProjBase.setPacketSender(packetSender);

            GregorianCalendar date = new GregorianCalendar();
            Globals.log("Server started", String.format("%1$td/%1$tm/%1$tY %1$tT", date), Globals.LOG_TYPE_ERR, false);
            Globals.log("Server started", String.format("%1$td/%1$tm/%1$tY %1$tT", date), Globals.LOG_TYPE_DATA, true);

            senderSch.scheduleAtFixedRate(packetSender, 0, 500, TimeUnit.MICROSECONDS);
            for (byte i = 0; i < server_rooms.length; i++) {
                server_rooms[i] = new LogicModule(i);
                threadPool.scheduleAtFixedRate(server_rooms[i], 0, 1, TimeUnit.MILLISECONDS);
                Globals.log("Initialization", "Room " + i, Globals.LOG_TYPE_ERR, false);
                Globals.log("Initialization", "Room " + i, Globals.LOG_TYPE_DATA, true);
            }
            packetReceiver.setDaemon(true);
            packetReceiver.setName("PacketReceiver");
            packetReceiver.start();

        } catch (Exception ex) {
            Globals.log(ex.getLocalizedMessage(), ex, true);
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame(Globals.WINDOW_TITLE);

        //frame.setUndecorated(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setPreferredSize(new Dimension(320, 30));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
