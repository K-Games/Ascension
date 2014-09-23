/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package BlockFighter.Client;

import blockfighter.Client.Net.PacketSender;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.DatagramSocket;

/**
 *
 * @author Ken
 */
public class KeyHandler implements KeyListener{
    private PacketSender requests = null;
    DatagramSocket socket = null;
    LogicModule logic = null;

    public KeyHandler(LogicModule logic, DatagramSocket socket){
        this.logic = logic;
        this.socket = socket;
        requests = new PacketSender(socket);    
    }
        
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP: logic.setKeyDown(Globals.UP, true); break;
            case KeyEvent.VK_DOWN: logic.setKeyDown(Globals.DOWN, true); break;
            case KeyEvent.VK_LEFT: logic.setKeyDown(Globals.LEFT, true); break;
            case KeyEvent.VK_RIGHT: logic.setKeyDown(Globals.RIGHT, true); break;
        }
       
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP: logic.setKeyDown(Globals.UP, false); break;
            case KeyEvent.VK_DOWN: logic.setKeyDown(Globals.DOWN, false); break;
            case KeyEvent.VK_LEFT: logic.setKeyDown(Globals.LEFT, false); break;
            case KeyEvent.VK_RIGHT: logic.setKeyDown(Globals.RIGHT, false); break;
        }
    }
}
