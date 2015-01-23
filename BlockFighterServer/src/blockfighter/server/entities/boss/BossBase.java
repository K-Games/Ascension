package blockfighter.server.entities.boss;

import blockfighter.server.maps.GameMap;
import blockfighter.server.net.PacketSender;
import blockfighter.server.Globals;
import blockfighter.server.LogicModule;
import blockfighter.server.entities.buff.Buff;
import blockfighter.server.entities.buff.BuffKnockback;
import blockfighter.server.entities.buff.BuffStun;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Player entities on the server.
 *
 * @author Ken Kwan
 */
public abstract class BossBase extends Thread implements Boss {

    private final byte key;
    private final LogicModule logic;
    private double x, y, ySpeed, xSpeed;

    private boolean updatePos = false, updateFacing = false, updateState = false;
    private byte bossState, facing, frame;
    private double nextFrameTime = 0;
    private Rectangle2D.Double hitbox;

    private ArrayList<Buff> buffs = new ArrayList<>();
    private boolean isStun = false, isKnockback = false;

    private final PacketSender packetSender;
    private final GameMap map;

    /**
     * Create a new player entity in the server.
     *
     * @param key The key of this player in the player array in logic module
     * @param x Spawning x location in double
     * @param y Spawning y location in double
     * @param bc Reference to Server PacketSender
     * @param map Reference to server's loaded map
     * @param l Reference to Logic module
     */
    public BossBase(PacketSender bc, LogicModule l, byte key, GameMap map, double x, double y) {
        packetSender = bc;
        logic = l;
        this.key = key;
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Double(x - 30, y - 96, 60, 96);
        this.map = map;
        facing = Globals.RIGHT;
        bossState = Globals.PLAYER_STATE_STAND;
        frame = 0;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public byte getKey() {
        return key;
    }

    @Override
    public byte getBossState() {
        return bossState;
    }

    @Override
    public byte getFacing() {
        return facing;
    }

    @Override
    public byte getFrame() {
        return frame;
    }

    @Override
    public void setPos(double x, double y) {
        this.x = x;
        this.y = y;
        updatePos = true;
    }

    @Override
    public void setYSpeed(double speed) {
        ySpeed = speed;
    }

    @Override
    public void setXSpeed(double speed) {
        xSpeed = speed;
    }

    @Override
    public void run() {
        update();
    }

    @Override
    public void update() {
        updateBuffs();

        hitbox.x = x - 30;
        hitbox.y = y - 96;

        updateFrame();

        if (updatePos) {
            sendPos();
        }
        if (updateFacing) {
            sendFacing();
        }
        if (updateState) {
            sendState();
        }

    }

    private void updateBuffs() {
        isStun = false;
        isKnockback = false;
        ArrayList<Buff> remove = new ArrayList<>();
        for (Buff b : buffs) {
            b.update();
            if (b instanceof BuffStun) {
                isStun = true;
            } else if (b instanceof BuffKnockback) {
                isKnockback = true;
            }
            if (b.isExpired()) {
                remove.add(b);
            }
        }

        for (Buff b : remove) {
            buffs.remove(b);
        }
    }

    @Override
    public boolean intersectHitbox(Rectangle2D.Double box) {
        return hitbox.intersects(box);
    }

    @Override
    public synchronized boolean isStunned() {
        return isStun;
    }

    @Override
    public synchronized boolean isKnockback() {
        return isKnockback;
    }

    @Override
    public void addBuff(Buff b) {
        buffs.add(b);
    }

    @Override
    public void setFacing(byte f) {
        facing = f;
        updateFacing = true;
    }

    @Override
    public void setBossState(byte newState) {
        if (bossState != newState) {
            bossState = newState;
            updateState = true;
        }
    }

    private void updateFrame() {
        switch (bossState) {
            case Globals.PLAYER_STATE_STAND:
                if (frame != 0) {
                    frame = 0;
                    updateState = true;
                }
                break;

            case Globals.PLAYER_STATE_WALK:
                nextFrameTime -= Globals.LOGIC_UPDATE;
                if (nextFrameTime <= 0) {
                    frame = (byte) ((frame == 0) ? 1 : 0);
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

    @Override
    public void sendPos() {
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_INT + Globals.PACKET_INT];
        bytes[0] = Globals.DATA_PLAYER_SET_POS;
        bytes[1] = key;
        byte[] posXInt = Globals.intToByte((int) x);
        bytes[2] = posXInt[0];
        bytes[3] = posXInt[1];
        bytes[4] = posXInt[2];
        bytes[5] = posXInt[3];
        byte[] posYInt = Globals.intToByte((int) y);
        bytes[6] = posYInt[0];
        bytes[7] = posYInt[1];
        bytes[8] = posYInt[2];
        bytes[9] = posYInt[3];
        packetSender.sendAll(bytes, logic.getRoom());
        updatePos = false;
    }

    @Override
    public void sendFacing() {
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE];
        bytes[0] = Globals.DATA_PLAYER_SET_FACING;
        bytes[1] = key;
        bytes[2] = facing;
        packetSender.sendAll(bytes, logic.getRoom());
        updateFacing = false;
    }

    @Override
    public void sendState() {
        byte[] bytes = new byte[Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE + Globals.PACKET_BYTE];
        bytes[0] = Globals.DATA_PLAYER_SET_STATE;
        bytes[1] = key;
        bytes[2] = bossState;
        bytes[3] = frame;
        packetSender.sendAll(bytes, logic.getRoom());
        updateState = false;
    }

}
