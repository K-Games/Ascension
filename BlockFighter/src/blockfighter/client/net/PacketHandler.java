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
            case Globals.DATA_LOGIN: responseLogin(data); break;
            case Globals.DATA_GET_PLAYER_POS: responseGetPlayerPos(data); break;
            case Globals.DATA_PING: responsePing(data); break;
            case Globals.DATA_SET_PLAYER_FACING: responseSetPlayerFacing(data); break;
            case Globals.DATA_SET_PLAYER_STATE: responseSetPlayerState(data); break;
        }
    }
    
    private void responseGetPlayerPos(byte[] data){
        logic.setPlayerPos(data);
    }
    
    private void responseLogin(byte[] data){
        byte index = data[1];
        byte size = data[2];
        logic.setPlayersSize(size);
        logic.setMyIndex(index);
        logic.addPlayer(index);
    }
    
    private void responsePing(byte[] data){
        logic.setPing(data[1]);
    }
    
    private void responseSetPlayerFacing(byte[] data){
        logic.setPlayerFacing(data);
    }
    
    private void responseSetPlayerState(byte[] data){
        logic.setPlayerState(data);
    }

}
