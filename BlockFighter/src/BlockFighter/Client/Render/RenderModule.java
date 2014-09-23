/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package BlockFighter.Client.Render;

import BlockFighter.Client.LogicModule;
import BlockFighter.Client.Globals;

/**
 *
 * @author ckwa290
 */
public class RenderModule extends Thread {
    private final RenderPanel panel; 
    private final LogicModule logic;
    private boolean isRunning = false;
    private int FPSCount = 0;
    
    public RenderModule(RenderPanel p, LogicModule l){
        panel = p;
        logic = l;
        isRunning = true;
    }
    
    @Override
    public void run(){

        double lastUpdateTime = System.nanoTime(); //Last time we rendered
        double lastFPSTime = lastUpdateTime; //Last time FPS count reset
        
        while (isRunning) {
            double now = System.nanoTime(); //Get time now
            if (now - lastUpdateTime >= Globals.RENDER_UPDATE) {
                panel.setPlayers(logic.getPlayers());
                panel.setPing(logic.getPing());
                panel.setMyIndex(logic.getMyIndex());
                panel.repaint();
                FPSCount++;
                lastUpdateTime = now;
            }
            
                        
            if(now - lastFPSTime >= 1000000000) {
                panel.setFPSCount(FPSCount);
                FPSCount = 0;
                lastFPSTime = now;
            }
            
            //Yield until rendering again
            while (now - lastUpdateTime < Globals.RENDER_UPDATE && now - lastFPSTime < 1000000000) {
                Thread.yield();
                now = System.nanoTime();
            }

        }
    }
    
    public int getFPS() {return FPSCount;}
    
}
