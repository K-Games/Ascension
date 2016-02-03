package blockfighter.server;

import blockfighter.server.entities.boss.Boss;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.Projectile;
import blockfighter.server.net.PacketHandler;
import blockfighter.server.net.PacketReceiver;
import blockfighter.server.net.PacketSender;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
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
    ;
    private static ScheduledExecutorService logicSchThreadPool = Executors.newScheduledThreadPool(Math.max(Globals.SERVER_ROOMS / 30, 1),
            new BasicThreadFactory.Builder()
            .namingPattern("LogicModuleScheduler-%d")
            .daemon(false)
            .priority(Thread.NORM_PRIORITY)
            .build());
    ;

    private static final JTextArea DATA_LOG = new JTextArea(),
            ERROR_LOG = new JTextArea();

    static {
        Globals.setGUILog(DATA_LOG, ERROR_LOG);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        System.out.println("Tower Conquest Server " + Globals.GAME_RELEASE_VERSION);

        boolean isGUI = true, isDefault = false;

        if (args.length > 0) {
            final HashSet<String> arguments = new HashSet<>();
            arguments.addAll(Arrays.asList(args));
            isGUI = !arguments.contains("--nogui");
            isDefault = arguments.contains("--default");
        }

        if (!isDefault) {
            Globals.setServerProp();
        }

        if (isGUI) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                createAndShowGUI();
            });
        }
        try {
            final LogicModule[] server_rooms = new LogicModule[Globals.SERVER_ROOMS];
            PacketSender.setLogic(server_rooms);
            PacketHandler.setLogic(server_rooms);

            final PacketSender packetSender = new PacketSender();
            final PacketReceiver packetReceiver = new PacketReceiver();

            LogicModule.setPacketSender(packetSender);
            PacketHandler.setPacketSender(packetSender);

            Player.setPacketSender(packetSender);
            Boss.setPacketSender(packetSender);
            Projectile.setPacketSender(packetSender);

            Globals.log("Server started", String.format("%1$td/%1$tm/%1$tY %1$tT", System.currentTimeMillis()), Globals.LOG_TYPE_ERR,
                    false);
            Globals.log("Server started", String.format("%1$td/%1$tm/%1$tY %1$tT", System.currentTimeMillis()), Globals.LOG_TYPE_DATA,
                    true);

            senderSch.scheduleAtFixedRate(packetSender, 0, 500, TimeUnit.MICROSECONDS);
            for (byte i = 0; i < server_rooms.length; i++) {
                server_rooms[i] = new LogicModule(i);
                logicSchThreadPool.scheduleAtFixedRate(server_rooms[i], 0, 20, TimeUnit.MICROSECONDS);
            }
            Globals.log("Initialization", "Initialized " + server_rooms.length + " rooms", Globals.LOG_TYPE_ERR, false);
            Globals.log("Initialization", "Initialized " + server_rooms.length + " rooms", Globals.LOG_TYPE_DATA, true);

            packetReceiver.setDaemon(true);
            packetReceiver.setName("PacketReceiver");
            packetReceiver.start();

        } catch (final Exception ex) {
            Globals.log(ex.getLocalizedMessage(), ex, true);
        }
    }

    private static void createAndShowGUI() {
        final JFrame frame = new JFrame(Globals.WINDOW_TITLE);

        // frame.setUndecorated(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setPreferredSize(new Dimension(500, 600));
        final JPanel panel = new JPanel();

        panel.setLayout(null);
        final JScrollPane dataLogPane = new JScrollPane(DATA_LOG);
        dataLogPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        dataLogPane.setBounds(0, 0, 500, 300);
        DATA_LOG.setEditable(false);
        DATA_LOG.setText("Data Log");

        final JScrollPane errLogPane = new JScrollPane(ERROR_LOG);
        errLogPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        errLogPane.setBounds(0, 300, 500, 300);
        ERROR_LOG.setEditable(false);
        ERROR_LOG.setText("Error Log");

        panel.add(dataLogPane);
        panel.add(errLogPane);

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
