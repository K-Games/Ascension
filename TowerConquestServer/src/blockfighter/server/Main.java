package blockfighter.server;

import blockfighter.server.net.GameServer;
import blockfighter.server.net.PacketHandler;
import blockfighter.server.net.PacketSender;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
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

    private final static ScheduledExecutorService PACKETSENDER_SCHEDULER = Executors.newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder()
            .namingPattern("PACKETSENDER_SCHEDULER-%d")
            .daemon(true)
            .priority(Thread.NORM_PRIORITY)
            .build());

    private final static ScheduledExecutorService LOGIC_SCHEDULER = Executors.newScheduledThreadPool(Math.max(Globals.SERVER_ROOMS.size() / 30, 1),
            new BasicThreadFactory.Builder()
            .namingPattern("LOGIC_SCHEDULER-%d")
            .daemon(false)
            .priority(Thread.NORM_PRIORITY)
            .build());

    private final static ScheduledExecutorService PACKETHANDLER_SCHEDULER = Executors.newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder()
            .namingPattern("PACKETHANDLER_SCHEDULER-%d")
            .daemon(false)
            .priority(Thread.NORM_PRIORITY)
            .build());

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

            final LogicModule[] server_rooms = new LogicModule[Globals.SERVER_ROOMS.size()];
            PacketSender.setLogic(server_rooms);
            PacketHandler.setLogic(server_rooms);
            final GameServer server = new GameServer();
            server.start();

            Globals.log(Main.class, "Server started ", Globals.LOG_TYPE_ERR, false);
            Globals.log(Main.class, "Server started", Globals.LOG_TYPE_DATA, true);
            if (Globals.SERVER_BATCH_PACKETSEND) {
                PacketSender.init();
                PACKETSENDER_SCHEDULER.scheduleAtFixedRate(new PacketSender(), 0, 5, TimeUnit.MICROSECONDS);
            }
            //PACKETHANDLER_SCHEDULER.scheduleAtFixedRate(packetHandler, 0, 10, TimeUnit.MICROSECONDS);
            for (final Map.Entry<Byte, Byte> b : Globals.SERVER_ROOMS.entrySet()) {
                server_rooms[b.getValue()] = new LogicModule(b.getKey());
                LOGIC_SCHEDULER.scheduleAtFixedRate(server_rooms[b.getValue()], 0, 750, TimeUnit.MICROSECONDS);
            }
            Globals.log(Main.class, "Initialized " + server_rooms.length + " rooms", Globals.LOG_TYPE_ERR, false);
            Globals.log(Main.class, "Initialized " + server_rooms.length + " rooms", Globals.LOG_TYPE_DATA, true);

        } catch (final Exception ex) {
            Globals.logError(ex.getLocalizedMessage(), ex, true);
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
