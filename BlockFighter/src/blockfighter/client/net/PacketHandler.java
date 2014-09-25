/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package blockfighter.client.net;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import java.net.DatagramPacket;

/**
 *
 * @author Ken
 */
public class PacketHandler extends Thread {
    private DatagramPacket r = null;
    private final LogicModule logic;

    public PacketHandler(DatagramPacket response, LogicModule logic) {
        r = response;
        this.logic = logic;
    }
    
    @Override
    public void run(){
        byte[] data = r.getData();
        byte dataType = data[0];
        switch (dataType) {
            case Globals.DATA_LOGIN: receiveLogin(data); break;
            case Globals.DATA_GET_PLAYER_POS: receiveGetPlayerPos(data); break;
            case Globals.DATA_PING: receiveGetPing(data); break;
            case Globals.DATA_SET_PLAYER_FACING: receiveSetPlayerFacing(data); break;
            case Globals.DATA_SET_PLAYER_STATE: receiveSetPlayerState(data); break;
            case Globals.DATA_PARTICLE_EFFECT: receiveParticleEffect(data); break;
        }
    }
    
    private void receiveParticleEffect(byte[] data){
        logic.queueParticleEffect(data);
    }
    
    private void receiveGetPlayerPos(byte[] data){
        logic.queueSetPlayerPos(data);
    }
    
    private void receiveLogin(byte[] data){
        byte index = data[1];
        byte size = data[2];
        logic.setPlayersSize(size);
        logic.setMyIndex(index);
        logic.queueAddPlayer(index);
    }
    
    private void receiveGetPing(byte[] data){
        logic.setPing(data[1]);
    }
    
    private void receiveSetPlayerFacing(byte[] data){
        logic.queueSetPlayerFacing(data);
    }
    
    private void receiveSetPlayerState(byte[] data){
        logic.queueSetPlayerState(data);
    }

}
