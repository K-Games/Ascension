package blockfighter.client;

import blockfighter.client.entities.ingamenumber.IngameNumber;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.mob.Mob;
import blockfighter.client.entities.notification.Notification;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.client.entities.player.skills.Skill;
import blockfighter.client.maps.GameMap;
import blockfighter.client.net.PacketReceiver;
import blockfighter.client.render.RenderModule;
import blockfighter.client.render.RenderPanel;
import blockfighter.client.screen.Screen;
import blockfighter.client.screen.ScreenSelectChar;
import blockfighter.client.screen.ScreenTitle;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 *
 * @author Ken Kwan
 */
public class Main {

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
            IngameNumber.class,
            Skill.class,
            ItemEquip.class,
            Notification.class
        };

        for (Class<?> cls : classes) {
            try {
                cls.getMethod("init").invoke(null);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                switch (args[i].toLowerCase()) {
                    case "-port":
                        try {
                            int port = Integer.parseInt(args[i + 1]);
                            if (port > 0 && port <= 65535) {
                                System.out.println("Setting server connection port to " + port);
                                Globals.SERVER_PORT = port;
                            } else {
                                System.err.println("-port Specify a valid port between 1 to 65535");
                                System.exit(202);
                            }
                        } catch (Exception e) {
                            System.err.println("-port Specify a valid port between 1 to 65535");
                            System.exit(201);
                        }
                        break;
                    case "-skiptitle":
                        Globals.SKIP_TITLE = true;
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

        final KeyHandler keyHandler = new KeyHandler();
        final MouseHandler mouseHandler = new MouseHandler();
        final FocusHandler focusHandler = new FocusHandler();
        // frame.setUndecorated(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setPreferredSize(new Dimension(Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(panel, null);
        frame.setVisible(true);

        panel.setLayout(null);
        panel.setFocusable(true);
        panel.addKeyListener(keyHandler);
        panel.addMouseMotionListener(mouseHandler);
        panel.addMouseListener(mouseHandler);
        panel.addFocusListener(focusHandler);
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
