/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package BlockFighter.Server.Entities;

import BlockFighter.Server.Maps.TestMap;
import BlockFighter.Server.Net.Broadcaster;
import BlockFighter.Server.Globals;
import java.net.InetAddress;

/**
 * Player entities on the server.
 * @author Ken
 */
public class Player extends Thread {
    private final byte index;
    private double x, y, ySpeed, xSpeed;
    private boolean[] isMove = new boolean[4];
    private boolean isFalling = false, isJumping = false;
    private boolean updatePos = false, updateFacing = false, updateState = false;
    private byte playerState, facing, frame;
    private double nextFrameTime = 0;
    
    private double stunDuration = 0, kbDuration = 0;
    
    private final InetAddress address;
    private final int port;
    private final Broadcaster broadcaster;
    private final TestMap map;
    
    /**
     * Return this player's current X position.
     * @return The player's X in double
     */
    public double getX() { return x; }

    /**
     * Return this player's current Y position.
     * @return The player's Y in double
     */
    public double getY() { return y; }    

    /**
     * Return this player's array index.
     * This index is the same index in the player array in the logic module. 
     * @return The index of this player in byte
     */
    public byte getIndex() { return index; }

    /**
     * Return this player's current state.
     * Used for updating animation state and player interactions.
     * States are listed in Globals.
     * @return The player's state in byte
     */
    public byte getPlayerState() { return playerState; }

    /**
     * Return this player's IP address.
     * Used for broadcasting to player with UDP.
     * @return The player's IP
     */
    public InetAddress getAddress() { return address; }

    /**
     * Return this player's connected port
     * Used for broadcasting to player with UDP.
     * @return The player's port in int
     */
    public int getPort() { return port; }

    /**
     * Return this player's facing direction.
     * Direction value is found in Globals.
     * @return The player's facing direction in byte
     */
    public byte getFacing() { return facing; }

    /**
     * Return this player's current animation frame.
     * @return The player's current animation frame
     */
    public byte getFrame() { return frame; }

    /**
     * Set this player's movement when server receives packet
     * that key is pressed.
     * @param direction The direction to be set
     * @param move True when pressed, false when released
     */
    public void setMove(int direction, boolean move) { isMove[direction] = move; }

    /**
     * Set the player's x and y position.
     * This does not interpolate. The player is instantly moved
     * to this location.
     * @param x New x location in double
     * @param y New y location in double
     */
    public void setPos(double x, double y) { this.x = x; this.y = y; updatePos = true;}
    
    /**
     * Set change in Y on the next tick.
     * @param speed Distance in double
     */
    public void setYSpeed(double speed) { ySpeed = speed;}

    /**
     * Set change in X on the next tick.
     * @param speed Distance in double
     */
    public void setXSpeed(double speed) { xSpeed = speed;}
    
    /**
     * Create a new player entity in the server.
     * @param index The index of this player in the player array in logic module
     * @param address IP address of player
     * @param port Connected port
     * @param x Spawning x location in double
     * @param y Spawning y location in double
     * @param bc Server Broadcaster used to send bytes to players
     * @param map Server's loaded map
     */
    public Player(byte index, InetAddress address, int port, double x, double y, Broadcaster bc, TestMap map){
        System.out.println(address+":"+port+" Index:"+index);
        broadcaster = bc;
        this.index = index;
        this.address = address;
        this.port = port;
        this.x = x;
        this.y = y;
        this.map = map;
        facing = Globals.RIGHT;
        playerState = Globals.PLAYER_STATE_STAND;
        frame = 0;
    }
    
    @Override
    public void run() {
        update();
    }
    
    /**
     * Updates all logic of this player.
     * Should be called every tick.
     * Specific logic updates are separated into other methods.
     * Specific logic updates should be private.
     */
    public void update(){
        boolean stunned = updateStun(),
                knocked = updateKnockback();
        
        updateFall();
        
        if (!stunned && !knocked) {
            updateWalk();
            updateJump();
        }
        
        updateFrame();
        
        if (updatePos) sendPos();
        if (updateFacing) sendFacing();
        if (updateState) sendState();
        
    }
    
    private boolean updateStun(){
        stunDuration -= Globals.LOGIC_UPDATE;
        return stunDuration > 0;
    }
    
    private boolean updateKnockback(){
        kbDuration -= Globals.LOGIC_UPDATE;
        if (kbDuration > 0) updateX(xSpeed);
        return kbDuration > 0;
    }
    
    /**
     * Set a stun duration for this player
     * Stun duration is in nanoseconds 100000ns = 1ms;
     * @param duration Duration in ns
     */
    public void setStun(double duration) {
        stunDuration = duration;
    }
    
    /**
     * Set a knockback duration for this player
     * Stun duration is in nanoseconds 100000ns = 1ms;
     * @param duration Duration in ns
     * @param xS Change in x per tick over the duration
     * @param yS Change in y per tick over the duration
     */
    public void setKnockback(double duration, double xS, double yS) {
        kbDuration = duration;
        setXSpeed(xS);
        setYSpeed(yS);
    }
    
    private void updateJump() {
        if (isMove[Globals.UP] && !isFalling && !isJumping){
            isJumping = true;
            setYSpeed(-12.5);
        }
    }
    
    private void updateFall(){
        if (ySpeed != 0) {
            updateY(ySpeed);
            setPlayerState(Globals.PLAYER_STATE_JUMP);
        }
        
        setYSpeed(ySpeed + Globals.GRAVITY);
        if (ySpeed >= Globals.MAX_FALLSPEED) setYSpeed(Globals.MAX_FALLSPEED);
         
        isFalling = map.isFalling(x,y,ySpeed);
        if (!isFalling  && ySpeed > 0) {
            y = map.getValidY(x, y, ySpeed);
            setYSpeed(0);
            isJumping = false;
            setPlayerState(Globals.PLAYER_STATE_STAND);
        }
    }
    
    private void updateWalk() {
        if (isMove[Globals.RIGHT] && !isMove[Globals.LEFT]) {
            setXSpeed(4.5);
            if (facing != Globals.RIGHT) setFacing(Globals.RIGHT);
            if (updateX(xSpeed)) {
                if (ySpeed == 0) setPlayerState(Globals.PLAYER_STATE_WALK);
            } else {
                if (ySpeed == 0) setPlayerState(Globals.PLAYER_STATE_STAND);
            }
        } else if (isMove[Globals.LEFT] && !isMove[Globals.RIGHT]){
            setXSpeed(-4.5);
            if (facing != Globals.LEFT) setFacing(Globals.LEFT);
            if (updateX(xSpeed)){
                if (ySpeed == 0) setPlayerState(Globals.PLAYER_STATE_WALK);
            } else {
                if (ySpeed == 0) setPlayerState(Globals.PLAYER_STATE_STAND);
            }
        }
    }
    
    /**
     * Set player facing direction
     * Direction constants in Globals
     * @param f Direction in byte
     */
    public void setFacing(byte f) {
        facing = f;
        updateFacing = true;
    }
    
    private boolean updateX(double change){
        if (map.isOutOfBounds(x+change, y)) return false;
        x=x+change;
        updatePos = true;
        return true;
    }
    
    private boolean updateY(double change){
        if (map.isOutOfBounds(x, y+change)) return false;
        y=y+change;
        updatePos = true;
        return true;
    }
    
    /**
     * Set player state
     * States constants in Globals
     * @param newState
     */
    public void setPlayerState(byte newState){
        if (playerState != newState) {
            playerState = newState;
            updateState = true;
        }
    }
    
    private void updateFrame() {
        switch (playerState) {
            case Globals.PLAYER_STATE_STAND: 
                if (frame != 0) {
                    frame = 0;
                    updateState = true;
                }
                break;
                    
            case Globals.PLAYER_STATE_WALK:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                if (nextFrameTime <= 0){
                    frame = (byte) ((frame == 0)?1:0);
                    nextFrameTime = 250000000;
                    updateState = true;
                }
                break;
            case Globals.PLAYER_STATE_JUMP: 
                if (frame != 0) {
                    frame = 0;
                    updateState = true;
                }
                break;
        }
    }
    
    /**
     * Send the player's current position to every connected player
     * X and y are casted and sent as int.
     * Uses Server Broadcaster to send to all
     * Byte sent:
     * 0 - Data type
     * 1 - Index
     * 2,3,4,5 - x
     * 6,7,8,9 - y
     */
    public void sendPos(){
        //Buffer header
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_INT + Globals.PACKET_INT];
        //Index
        bytes[0] = Globals.DATA_GET_PLAYER_POS;
        bytes[1] = index;
        //Send Pos
        byte[] posXInt = Globals.intToByte((int)x);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        byte[] posYInt = Globals.intToByte((int)y);
        bytes[6] = posYInt[0];
        bytes[7] = posYInt[1];
        bytes[8] = posYInt[2];
        bytes[9] = posYInt[3];
        broadcaster.sendAll(bytes);
        updatePos = false;
    }

    /**
     * Send the player's current facing direction to every connected player
     * Facing uses direction constants in Globals.
     * Uses Server Broadcaster to send to all
     * Byte sent:
     * 0 - Data type
     * 1 - Index
     * 2 - Facing direction
     */
    public void sendFacing(){
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE];
        bytes[0] = Globals.DATA_SET_PLAYER_FACING;
        bytes[1] = index;
        bytes[2] = facing;
        broadcaster.sendAll(bytes);
        updateFacing = false;
    }

    /**
     * Send the player's current state(for animation) and current
     * frame of animation to every connected player
     * State constants are in Globals.
     * Uses Server Broadcaster to send to all
     * Byte sent:
     * 0 - Data type
     * 1 - Index
     * 2 - Player state
     * 3 - Current frame
     */
    public void sendState(){
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE];
        bytes[0] = Globals.DATA_SET_PLAYER_STATE;
        bytes[1] = index;
        bytes[2] = playerState;
        bytes[3] = frame;
        broadcaster.sendAll(bytes);
        updateState = false;
    }

}
