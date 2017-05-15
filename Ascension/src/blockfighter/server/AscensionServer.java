package blockfighter.server;

import blockfighter.server.net.GameServer;
import blockfighter.server.net.PacketSender;
import blockfighter.server.net.hub.HubClient;
import blockfighter.shared.Globals;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class AscensionServer {

    private static JTextArea DATA_LOG, ERROR_LOG;
    private static ConcurrentHashMap<Byte, LogicModule> SERVER_ROOMS;
    private static GameServer SERVER;

    static {
        Globals.createLogDirectory();
        Globals.loadServer();
        Globals.loadServerConfig();

        SERVER_ROOMS = new ConcurrentHashMap<>(Globals.SERVER_MAX_ROOMS);
    }

    public void shutdown() {
        Globals.log(AscensionServer.class, "Shutting down server...", Globals.LOG_TYPE_DATA);
        SERVER.shutdown();
        SERVER_ROOMS = null;
        System.gc();
        Globals.log(AscensionServer.class, "Server shut down", Globals.LOG_TYPE_DATA);
    }

    public void launch(final String[] args) {
        System.out.println(Globals.WINDOW_TITLE + " Server");
        Globals.LOGGING = true;

        boolean isGUI = false;

        if (args.length > 0) {
            final HashSet<String> arguments = new HashSet<>();
            arguments.addAll(Arrays.asList(args));
            isGUI = arguments.contains("-gui");
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
            SERVER = new GameServer();
            SERVER.start();
            Globals.log(AscensionServer.class, "Server started ", Globals.LOG_TYPE_ERR);
            Globals.log(AscensionServer.class, "Server started", Globals.LOG_TYPE_DATA);

            Core.SHARED_SCHEDULED_THREADPOOL.scheduleAtFixedRate(new PacketSender(), 0, 16000, TimeUnit.MICROSECONDS);

            Core.SHARED_SCHEDULED_THREADPOOL.scheduleAtFixedRate(() -> {
                PacketSender.clearDisconnectedConnectionBatch();
            }, 10, 10, TimeUnit.SECONDS);

            if (Globals.SERVER_HUB_CONNECT) {
                Core.SHARED_SCHEDULED_THREADPOOL.scheduleAtFixedRate(new HubClient(), 0, 10, TimeUnit.SECONDS);
            }
        } catch (final Exception ex) {
            Globals.logError(ex.toString(), ex);
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

    public static ConcurrentHashMap<Byte, LogicModule> getServerRooms() {
        return SERVER_ROOMS;
    }

    public static LogicModule addRoom(final int level) {
        Byte nextRoomIndex = getNextRoomIndex();
        if (nextRoomIndex != null) {

            byte minLevel = (byte) (level - Globals.SERVER_ROOM_LEVEL_DIFF);
            byte maxLevel = (byte) (level + Globals.SERVER_ROOM_LEVEL_DIFF);
            LogicModule newRoom = new LogicModule(nextRoomIndex, minLevel, maxLevel);
            SERVER_ROOMS.put(nextRoomIndex, newRoom);
            newRoom.setFuture(Core.SHARED_SCHEDULED_THREADPOOL.scheduleAtFixedRate(newRoom, 0, 750, TimeUnit.MICROSECONDS));
            Globals.log(AscensionServer.class, "New room instance - Room: " + newRoom.getRoomData().getRoomIndex() + " Level Range: " + minLevel + "-" + maxLevel, Globals.LOG_TYPE_DATA);
            return newRoom;
        }
        return null;
    }

    public static void removeRoom(final byte roomIndex) {
        SERVER_ROOMS.remove(roomIndex);
    }

    public static boolean canCreateRoom() {
        return SERVER_ROOMS.size() < Globals.SERVER_MAX_ROOMS;
    }

    private static Byte getNextRoomIndex() {
        if (SERVER_ROOMS.size() >= Globals.SERVER_MAX_ROOMS) {
            return null;
        }
        byte index = 0;
        while (SERVER_ROOMS.containsKey(index)) {
            index++;
        }
        return index;
    }

    public static LogicModule getOpenRoom(final int level) {
        Iterator<Map.Entry<Byte, LogicModule>> iter = SERVER_ROOMS.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Byte, LogicModule> room = iter.next();
            if (room.getValue().getMatchTimeRemaining() > Globals.SERVER_MATCH_TIME_REMAINING_THRESHOLD && !room.getValue().getRoomData().isFull() && room.getValue().getRoomData().isInLevelRange(level)) {
                return room.getValue();
            }
        }
        return null;
    }

    public static int getServerCapacityStatus() {
        int totalPlayers = 0;
        Iterator<Map.Entry<Byte, LogicModule>> iter = SERVER_ROOMS.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Byte, LogicModule> room = iter.next();
            totalPlayers += room.getValue().getRoomData().getPlayers().size();
        }
        return Math.round(100f * totalPlayers / (Globals.SERVER_MAX_ROOMS * Globals.SERVER_MAX_ROOM_PLAYERS));
    }
}
