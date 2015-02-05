package blockfighter.client;

import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.items.ItemUpgrade;
import blockfighter.client.render.RenderModule;
import blockfighter.client.render.RenderPanel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

/**
 *
 * @author Ken Kwan
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length >= 1) {
            Globals.SERVER_ADDRESS = args[0];
        }
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

        final LogicModule logic = new LogicModule();
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
            }
        });
        logic.start();
        render.start();

    }
}
