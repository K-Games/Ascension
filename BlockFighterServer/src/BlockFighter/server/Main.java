/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package blockfighter.server;

import blockfighter.server.net.ConnectionThread;
import blockfighter.server.net.Broadcaster;

/**
 * Start module of server
 * @author Ken
 */
public class Main{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        LogicModule logic = new LogicModule();
        Broadcaster broadcaster = new Broadcaster(logic);
        ConnectionThread server = new ConnectionThread(logic, broadcaster);
        
        logic.setBroadcaster(broadcaster);
        logic.start();
        server.start();
    }

}
