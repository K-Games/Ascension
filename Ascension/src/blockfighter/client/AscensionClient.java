package blockfighter.client;

import blockfighter.client.entities.emotes.Emote;
import blockfighter.client.entities.ingamenumber.IngameNumber;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.mob.Mob;
import blockfighter.client.entities.notification.Notification;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.client.entities.player.skills.Skill;
import blockfighter.client.maps.GameMap;
import blockfighter.client.net.PacketHandler;
import blockfighter.client.net.PacketReceiver;
import blockfighter.client.render.RenderModule;
import blockfighter.client.render.RenderPanel;
import blockfighter.client.screen.Screen;
import blockfighter.client.screen.ScreenSelectChar;
import blockfighter.client.screen.ScreenTitle;
import blockfighter.shared.Globals;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class AscensionClient {

    public static final KeyHandler KEY_HANDLER = new KeyHandler();
    public static final MouseHandler MOUSE_HANDLER = new MouseHandler();
    public static final FocusHandler FOCUS_HANDLER = new FocusHandler();
    private static final SoundModule SOUND_MODULE = new SoundModule();

    private static final LogicModule LOGIC_MODULE = new LogicModule(SOUND_MODULE);
    private static final ExecutorService SHARED_THREADPOOL = Executors.newFixedThreadPool(4,
            new BasicThreadFactory.Builder()
            .namingPattern("SHARED_THREADPOOL-%d")
            .daemon(true)
            .priority(Thread.NORM_PRIORITY)
            .build());

    static {
        SHARED_THREADPOOL.execute(SOUND_MODULE);

        Globals.loadClient();
        Class<?>[] classes = {
            Particle.class,
            Screen.class,
            RenderModule.class,
            FocusHandler.class,
            KeyHandler.class,
            MouseHandler.class,
            Player.class,
            Mob.class,
            PacketReceiver.class,
            PacketHandler.class,
            IngameNumber.class,
            Skill.class,
            ItemEquip.class,
            Notification.class,
            Emote.class
        };

        for (Class<?> cls : classes) {
            try {
                cls.getMethod("init").invoke(null);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(AscensionClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void launch(final String[] args) {
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
                                Globals.log(AscensionClient.class, "Setting server connection TCP port to " + port, Globals.LOG_TYPE_DATA, true);
                                Globals.SERVER_TCP_PORT = port;
                            } else {
                                System.err.println("-port Specify a valid port between 1 to 65535");
                                System.exit(202);
                            }
                        } catch (Exception e) {
                            System.err.println("-port Specify a valid port between 1 to 65535");
                            System.exit(201);
                        }
                        break;
                    case "-udpport":
                        try {
                            int port = Integer.parseInt(args[i + 1]);
                            if (port > 0 && port <= 65535) {
                                Globals.log(AscensionClient.class, "Setting server connection UDP port to " + port, Globals.LOG_TYPE_DATA, true);
                                Globals.SERVER_UDP_PORT = port;
                            } else {
                                System.err.println("-port Specify a valid port between 1 to 65535");
                                System.exit(203);
                            }
                        } catch (Exception e) {
                            System.err.println("-port Specify a valid port between 1 to 65535");
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
                        break;
                    case "-log":
                        Globals.LOGGING = true;
                        Globals.createLogDirectory();
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

        Screen.setThreadPool(SHARED_THREADPOOL);
        GameMap.setThreadPool(SHARED_THREADPOOL);

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
        panel.addKeyListener(KEY_HANDLER);
        panel.addMouseMotionListener(MOUSE_HANDLER);
        panel.addMouseListener(MOUSE_HANDLER);
        panel.addFocusListener(FOCUS_HANDLER);
        panel.requestFocus();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOGIC_MODULE.disconnect();
                SOUND_MODULE.shutdown();
            }
        });
        final ScheduledExecutorService service = Executors.newScheduledThreadPool(2, new BasicThreadFactory.Builder()
                .namingPattern("RunScheduler-%d")
                .daemon(true)
                .priority(Thread.NORM_PRIORITY)
                .build());
        LOGIC_MODULE.setScreen((!Globals.SKIP_TITLE) ? new ScreenTitle() : new ScreenSelectChar());
        service.scheduleAtFixedRate(LOGIC_MODULE, 0, 1, TimeUnit.MILLISECONDS);
        service.scheduleAtFixedRate(render, 0, Globals.RENDER_UPDATE, TimeUnit.MICROSECONDS);
    }

    public static LogicModule getLogicModule() {
        return LOGIC_MODULE;
    }

}
