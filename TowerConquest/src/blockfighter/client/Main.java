package blockfighter.client;

import blockfighter.client.entities.boss.Boss;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.entities.player.Player;
import blockfighter.client.maps.GameMap;
import blockfighter.client.net.PacketHandler;
import blockfighter.client.net.PacketReceiver;
import blockfighter.client.render.RenderModule;
import blockfighter.client.render.RenderPanel;
import blockfighter.client.screen.Screen;
import java.awt.Dimension;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
            .namingPattern("SharedThreads-%d")
            .daemon(true)
            .priority(Thread.NORM_PRIORITY)
            .build());

    static {
        SHARED_THREADPOOL.execute(SOUND_MODULE);
    }

    /**
     * @param args the command line arguments
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.IllegalAccessException
     */
    public static void main(final String[] args) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        if (args.length >= 1) {
            Globals.SERVER_ADDRESS = args[0];
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
        Particle.setLogic(LOGIC_MODULE);
        Screen.setLogic(LOGIC_MODULE);
        RenderModule.setLogic(LOGIC_MODULE);
        FocusHandler.setLogic(LOGIC_MODULE);
        KeyHandler.setLogic(LOGIC_MODULE);
        MouseHandler.setLogic(LOGIC_MODULE);
        Player.setLogic(LOGIC_MODULE);
        Boss.setLogic(LOGIC_MODULE);
        PacketHandler.setLogic(LOGIC_MODULE);
        PacketReceiver.setLogic(LOGIC_MODULE);

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
        service.scheduleAtFixedRate(LOGIC_MODULE, 0, 5, TimeUnit.MILLISECONDS);
        service.scheduleAtFixedRate(render, 0, Globals.RENDER_UPDATE, TimeUnit.MICROSECONDS);
    }
}
