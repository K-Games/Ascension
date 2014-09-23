/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package BlockFighter.Client;

import BlockFighter.Client.Render.RenderModule;
import BlockFighter.Client.Render.RenderPanel;
import blockfighter.Client.Net.ConnectionThread;
import java.awt.*;
import java.net.DatagramSocket;
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
            Globals.loadCharSprites();
            JFrame frame = new JFrame("Block Fighter");
            RenderPanel render = new RenderPanel();
            LogicModule logicCore = new LogicModule(socket);
            ConnectionThread responseThread = new ConnectionThread(logicCore,socket);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
