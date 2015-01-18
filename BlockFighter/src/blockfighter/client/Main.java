package blockfighter.client;

import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.render.RenderModule;
import blockfighter.client.render.RenderPanel;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author ckwa290
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
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
        ItemEquip.loadItemNames();
        
        JFrame frame = new JFrame("Tower Conquest");
        RenderPanel panel = new RenderPanel();

        LogicModule logic = new LogicModule();
        RenderModule render = new RenderModule(panel, logic, frame);

        KeyHandler keyHandler = new KeyHandler(logic);
        MouseHandler mouseHandler = new MouseHandler(logic);
        //frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setPreferredSize(new Dimension(Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setVisible(true);

        panel.setFocusable(true);

        panel.addKeyListener(keyHandler);
        panel.addMouseMotionListener(mouseHandler);
        panel.addMouseListener(mouseHandler);
        panel.requestFocus();

        logic.start();
        render.start();

    }
}
