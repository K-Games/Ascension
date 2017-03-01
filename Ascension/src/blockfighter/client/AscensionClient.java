package blockfighter.client;

import blockfighter.client.render.RenderModule;
import blockfighter.client.render.RenderPanel;
import blockfighter.client.screen.Screen;
import blockfighter.client.screen.ScreenSelectChar;
import blockfighter.client.screen.ScreenTitle;
import blockfighter.shared.Globals;
import java.awt.Dimension;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class AscensionClient {

    static {
        Globals.loadClient();
    }

    public static void launch(final String[] args) {
        Core.setup();
        if (args.length > 0) {
            boolean devEnabled = false;
            for (int i = 0; i < args.length; i++) {
                if (args[i].toLowerCase().equals("-pass")) {
                    try {
                        byte[] digest = MessageDigest.getInstance("SHA-256").digest(args[i + 1].getBytes(StandardCharsets.UTF_8));
                        String passphrase = Base64.getEncoder().encodeToString(digest);
                        if (passphrase.equals(Globals.DEV_PASSPHRASE)) {
                            devEnabled = true;
                        } else {
                            System.err.println("-pass Incorrect passphrase");
                        }
                    } catch (Exception e) {
                        System.err.println("-pass Enter the developer passphrase");
                    }
                    break;
                }
            }

            for (int i = 0; i < args.length; i++) {
                switch (args[i].toLowerCase()) {
                    case "-tcpport":
                        try {
                            int port = Integer.parseInt(args[i + 1]);
                            if (port > 0 && port <= 65535) {
                                Globals.log(AscensionClient.class, "Setting server connection TCP port to " + port, Globals.LOG_TYPE_DATA);
                                Globals.SERVER_TCP_PORT = port;
                            } else {
                                System.err.println("-tcpport Specify a valid port between 1 to 65535");
                                System.exit(202);
                            }
                        } catch (Exception e) {
                            System.err.println("-tcpport Specify a valid port between 1 to 65535");
                            System.exit(201);
                        }
                        break;
                    case "-udpport":
                        try {
                            int port = Integer.parseInt(args[i + 1]);
                            if (port > 0 && port <= 65535) {
                                Globals.log(AscensionClient.class, "Setting server connection UDP port to " + port, Globals.LOG_TYPE_DATA);
                                Globals.SERVER_UDP_PORT = port;
                            } else {
                                System.err.println("-udpport Specify a valid port between 1 to 65535");
                                System.exit(203);
                            }
                        } catch (Exception e) {
                            System.err.println("-udpport Specify a valid port between 1 to 65535");
                            System.exit(204);
                        }
                        break;
                    case "-skiptitle":
                        Globals.SKIP_TITLE = true;
                        break;
                    case "-debug":
                        if (devEnabled) {
                            Globals.DEBUG_MODE = true;
                        } else {
                            System.err.println("-debug Dev passphrase required");
                        }
                        break;
                    case "-maxlevel":
                        if (devEnabled) {
                            Globals.TEST_MAX_LEVEL = true;
                        } else {
                            System.err.println("-maxlevel Dev passphrase required");
                        }
                        break;
                    case "-tcpmode":
                        Globals.UDP_MODE = false;
                        Globals.log(AscensionClient.class, "Disabling UDP. Using TCP only mode", Globals.LOG_TYPE_DATA);
                        break;
                    case "-log":
                        Globals.LOGGING = true;
                        Globals.createLogDirectory();
                        break;
                    case "-hubaddress":
                        Globals.CLIENT_HUB_SERVER_ADDRESS = args[i + 1];
                        Globals.log(AscensionClient.class, "Setting Hub Server address to " + Globals.CLIENT_HUB_SERVER_ADDRESS, Globals.LOG_TYPE_DATA);
                        break;
                    case "-hubport":
                        try {
                            int port = Integer.parseInt(args[i + 1]);
                            if (port > 0 && port <= 65535) {
                                Globals.HUB_SERVER_TCP_PORT = port;
                                Globals.log(AscensionClient.class, "Setting Hub Server TCP port to " + Globals.HUB_SERVER_TCP_PORT, Globals.LOG_TYPE_DATA);
                            } else {
                                System.err.println("-hubport Specify a valid port between 1 to 65535");
                                System.exit(7);
                            }
                        } catch (Exception e) {
                            System.err.println("-hubport Specify a valid port between 1 to 65535");
                            System.exit(8);
                        }
                        break;
                }
            }
        }
        javax.swing.SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {

        final JFrame frame = new JFrame(Globals.WINDOW_TITLE);
        final RenderPanel panel = new RenderPanel();

        final RenderModule render = new RenderModule(panel);

        Screen.setRenderPanel(panel);

        // frame.setUndecorated(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (Globals.WINDOW_SCALE_ENABLED) {
            frame.getContentPane().setPreferredSize(new Dimension((int) (Globals.WINDOW_WIDTH * Globals.WINDOW_SCALE), (int) (Globals.WINDOW_HEIGHT * Globals.WINDOW_SCALE)));
        } else {
            frame.getContentPane().setPreferredSize(new Dimension(Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT));
        }
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(panel, null);
        frame.setVisible(true);

        panel.setLayout(null);
        panel.setFocusable(true);
        panel.addKeyListener(Core.KEY_HANDLER);
        panel.addMouseMotionListener(Core.MOUSE_HANDLER);
        panel.addMouseListener(Core.MOUSE_HANDLER);
        panel.addFocusListener(Core.FOCUS_HANDLER);
        panel.requestFocus();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Core.getLogicModule().disconnect();
                Core.getSoundModule().shutdown();
            }
        });
        final ScheduledExecutorService service = Executors.newScheduledThreadPool(2, new BasicThreadFactory.Builder()
                .namingPattern("Client-Runner-%d")
                .daemon(false)
                .priority(Thread.NORM_PRIORITY)
                .build());
        Core.getLogicModule().setScreen((!Globals.SKIP_TITLE) ? new ScreenTitle() : new ScreenSelectChar());
        service.scheduleAtFixedRate(Core.getLogicModule(), 0, 1, TimeUnit.MILLISECONDS);
        service.scheduleAtFixedRate(render, 0, Globals.RENDER_UPDATE, TimeUnit.MICROSECONDS);
    }
}
