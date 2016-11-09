package blockfighter.server;

import blockfighter.server.net.GameServer;
import blockfighter.server.net.PacketHandler;
import blockfighter.server.net.PacketSender;
import blockfighter.shared.Globals;
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

public class AscensionServer {

    private final ScheduledExecutorService PACKETSENDER_SCHEDULER = Executors.newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder()
            .namingPattern("PACKETSENDER_SCHEDULER-%d")
            .daemon(true)
            .priority(Thread.NORM_PRIORITY)
            .build());

    private final ScheduledExecutorService LOGIC_SCHEDULER = Executors.newScheduledThreadPool(Math.max(Globals.SERVER_ROOMNUM_TO_ROOMINDEX.size() / 30, 1),
            new BasicThreadFactory.Builder()
            .namingPattern("LOGIC_SCHEDULER-%d")
            .daemon(false)
            .priority(Thread.NORM_PRIORITY)
            .build());

    private JTextArea DATA_LOG, ERROR_LOG;
    private LogicModule[] SERVER_ROOMS;
    private GameServer SERVER;

    static {
        Globals.loadServer();
    }

    public void shutdown() {
        System.out.println("Shutting down server...");
        SERVER.shutdown();
        PACKETSENDER_SCHEDULER.shutdown();
        LOGIC_SCHEDULER.shutdown();
        SERVER_ROOMS = null;
        System.gc();
        System.out.println("Server shut down.");
    }

    public void launch(final String[] args) {
        System.out.println("Ascension Server " + Globals.GAME_RELEASE_VERSION);

        boolean isGUI = false, isDefault = false;

        if (args.length > 0) {
            final HashSet<String> arguments = new HashSet<>();
            arguments.addAll(Arrays.asList(args));
            isGUI = arguments.contains("--gui");
            isDefault = arguments.contains("--default");
        }

        if (!isDefault) {
            Globals.setServerProp();
        }

        if (isGUI) {
            DATA_LOG = new JTextArea();
            ERROR_LOG = new JTextArea();
            Globals.setGUILog(DATA_LOG, ERROR_LOG);
            javax.swing.SwingUtilities.invokeLater(() -> {
                createAndShowGUI();
            });
        }
        try {
            SERVER_ROOMS = new LogicModule[Globals.SERVER_ROOMNUM_TO_ROOMINDEX.size()];
            SERVER = new GameServer();
            PacketSender.setLogic(SERVER_ROOMS);
            PacketHandler.setLogic(SERVER_ROOMS);

            SERVER.start();

            Globals.log(AscensionServer.class, "Server started ", Globals.LOG_TYPE_ERR, false);
            Globals.log(AscensionServer.class, "Server started", Globals.LOG_TYPE_DATA, true);
            if (Globals.SERVER_BATCH_PACKETSEND) {
                PacketSender.init();
                PACKETSENDER_SCHEDULER.scheduleAtFixedRate(new PacketSender(), 0, 100, TimeUnit.MICROSECONDS);
            }
            for (final Map.Entry<Byte, Byte> b : Globals.SERVER_ROOMNUM_TO_ROOMINDEX.entrySet()) {
                SERVER_ROOMS[b.getValue()] = new LogicModule(b.getKey(), b.getValue());
                LOGIC_SCHEDULER.scheduleAtFixedRate(SERVER_ROOMS[b.getValue()], 0, 750, TimeUnit.MICROSECONDS);
            }
            Globals.log(AscensionServer.class, "Initialized " + SERVER_ROOMS.length + " rooms", Globals.LOG_TYPE_ERR, false);
            Globals.log(AscensionServer.class, "Initialized " + SERVER_ROOMS.length + " rooms", Globals.LOG_TYPE_DATA, true);

        } catch (final Exception ex) {
            Globals.logError(ex.getStackTrace()[0].toString(), ex, true);
        }
    }

    private void createAndShowGUI() {
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
        DATA_LOG.setLineWrap(true);
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
