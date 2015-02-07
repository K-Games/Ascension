package blockfighter.client;

import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.items.ItemUpgrade;
import blockfighter.client.render.RenderModule;
import blockfighter.client.render.RenderPanel;
import java.awt.*;
import java.lang.reflect.Field;
import javax.swing.*;

/**
 *
 * @author Ken Kwan
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.lang.NoSuchFieldException
     * @throws java.lang.IllegalAccessException
     */
    public static void main(String[] args) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        if (args.length >= 1) {
            Globals.SERVER_ADDRESS = args[0];
        }
        System.setProperty("java.library.path", "native/windows");

        //set sys_paths to null so that java.library.path will be reevalueted next time it is needed
        final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
        sysPathsField.setAccessible(true);
        sysPathsField.set(null, null);
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        Globals.loadGFX();
        ItemEquip.loadItemDetails();
        ItemEquip.loadItemDrawOrigin();
        ItemUpgrade.loadUpgradeItems();
        JFrame frame = new JFrame(Globals.WINDOW_TITLE);
        RenderPanel panel = new RenderPanel();
        final SoundModule sounds = new SoundModule();
        final LogicModule logic = new LogicModule(sounds);
        RenderModule render = new RenderModule(panel, logic, frame);

        KeyHandler keyHandler = new KeyHandler(logic);
        MouseHandler mouseHandler = new MouseHandler(logic);
        //frame.setUndecorated(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setPreferredSize(new Dimension(Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(panel, null);
        frame.setVisible(true);

        panel.setFocusable(true);
        panel.addKeyListener(keyHandler);
        panel.addMouseMotionListener(mouseHandler);
        panel.addMouseListener(mouseHandler);
        panel.requestFocus();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logic.disconnect();
                sounds.shutdown();
            }
        });
        logic.start();
        render.start();

    }
}
