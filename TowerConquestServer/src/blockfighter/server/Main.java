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
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * Start module of server
 *
 * @author Ken Kwan
 */
public class Main {

    private static ScheduledExecutorService senderSch;
    private static ScheduledExecutorService logicSchThreadPool;

    private static JTextArea dataLog = new JTextArea(),
            errLog = new JTextArea();

    public static void init() {
        LogicModule.init();
        PacketSender.init();
        senderSch = Executors.newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder()
                .namingPattern("PacketSenderScheduler-%d")
                .daemon(true)
                .priority(Thread.NORM_PRIORITY)
                .build());
        logicSchThreadPool = Executors.newScheduledThreadPool(Math.max(Globals.SERVER_ROOMS / 30, 1),
                new BasicThreadFactory.Builder()
                .namingPattern("LogicModuleScheduler-%d")
                .daemon(false)
                .priority(Thread.NORM_PRIORITY)
                .build());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean isGUI = true, getMaxPlayer = false, getMaxRooms = false,
                isDefault = false;

        if (args.length > 0) {
            HashSet<String> arguments = new HashSet<>();
            arguments.addAll(Arrays.asList(args));
            isGUI = !arguments.contains("--nogui");
            getMaxPlayer = arguments.contains("--players");
            getMaxRooms = arguments.contains("--rooms");
            isDefault = arguments.contains("--default");
        }
        Globals.initLogger();
        Globals.setGUILog(dataLog, errLog);
        if (!isDefault) {
            Globals.setServerProp();
        }
        init();
        if (getMaxPlayer) {
            byte value = 0;
            while (value <= 0) {
                Scanner in = new Scanner(System.in);
                System.out.println("Enter maximum number of players allowed (Must be more than 0 and less than " + Byte.MAX_VALUE + "):");

                byte input = 0;
                try {
                    if (in.hasNextByte()) {
                        input = in.nextByte();
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
                if (input > 0 && input <= 127) {
                    value = input;
                }
            }
            Globals.SERVER_MAX_PLAYERS = value;
            System.out.println("Setting " + Globals.SERVER_MAX_PLAYERS + " max players per room.");
        }

        if (getMaxRooms) {
            byte value = 1;
            while (value <= 1) {
                Scanner in = new Scanner(System.in);
                System.out.println("Enter number of rooms(levels) created (Must be less than 100):");

                byte input = 0;
                try {
                    if (in.hasNextByte()) {
                        input = in.nextByte();
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
                if (input > 1 && input <= 100) {
                    value += input;
                }
            }

            Globals.SERVER_ROOMS = value;
            System.out.println("Creating " + Globals.SERVER_ROOMS + " Rooms");
        }

        if (isGUI) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                createAndShowGUI();
            });
        }
        try {
            LogicModule[] server_rooms = new LogicModule[Globals.SERVER_ROOMS];
            PacketSender.setLogic(server_rooms);
            PacketHandler.setLogic(server_rooms);

            PacketSender packetSender = new PacketSender();
            PacketReceiver packetReceiver = new PacketReceiver();

            LogicModule.setPacketSender(packetSender);
            PacketHandler.setPacketSender(packetSender);

            Player.setPacketSender(packetSender);
            Boss.setPacketSender(packetSender);
            Projectile.setPacketSender(packetSender);

            Globals.log("Server started", String.format("%1$td/%1$tm/%1$tY %1$tT", System.currentTimeMillis()), Globals.LOG_TYPE_ERR, false);
            Globals.log("Server started", String.format("%1$td/%1$tm/%1$tY %1$tT", System.currentTimeMillis()), Globals.LOG_TYPE_DATA, true);

            senderSch.scheduleAtFixedRate(packetSender, 0, 500, TimeUnit.MICROSECONDS);
            for (byte i = 0; i < server_rooms.length; i++) {
                server_rooms[i] = new LogicModule(i);
                logicSchThreadPool.scheduleAtFixedRate(server_rooms[i], 0, 1, TimeUnit.MILLISECONDS);
            }
            Globals.log("Initialization", "Initialized " + server_rooms.length + " rooms", Globals.LOG_TYPE_ERR, false);
            Globals.log("Initialization", "Initialized " + server_rooms.length + " rooms", Globals.LOG_TYPE_DATA, true);

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
        frame.getContentPane().setPreferredSize(new Dimension(500, 600));
        JPanel panel = new JPanel();

        panel.setLayout(null);
        JScrollPane dataLogPane = new JScrollPane(dataLog);
        dataLogPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        dataLogPane.setBounds(0, 0, 500, 300);
        dataLog.setEditable(false);
        dataLog.setText("Data Log");

        JScrollPane errLogPane = new JScrollPane(errLog);
        errLogPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        errLogPane.setBounds(0, 300, 500, 300);
        errLog.setEditable(false);
        errLog.setText("Error Log");

        panel.add(dataLogPane);
        panel.add(errLogPane);

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
