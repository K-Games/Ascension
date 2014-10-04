package blockfighter.client;

import blockfighter.client.render.RenderModule;
import blockfighter.client.render.RenderPanel;
import blockfighter.client.net.ConnectionThread;
import java.awt.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
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
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.connect(InetAddress.getByName(Globals.SERVER_ADDRESS), Globals.SERVER_PORT);
            Globals.loadCharSprites();
            JFrame frame = new JFrame("Block Fighter");
            RenderPanel render = new RenderPanel();
            LogicModule logicCore = new LogicModule(socket);
            ConnectionThread responseThread = new ConnectionThread(logicCore, socket);
            RenderModule renderCore = new RenderModule(render, logicCore);
            KeyHandler keyHandler = new KeyHandler(logicCore, socket);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setPreferredSize(new Dimension(Globals.WINDOW_WIDTH, Globals.WINDOW_HEIGHT));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.getContentPane().add(render, BorderLayout.CENTER);
            frame.setVisible(true);

            frame.addKeyListener(keyHandler);

            logicCore.start();
            responseThread.start();
            renderCore.start();
        } catch (SocketException | UnknownHostException | HeadlessException e) {
            e.printStackTrace();
        }
    }
}
