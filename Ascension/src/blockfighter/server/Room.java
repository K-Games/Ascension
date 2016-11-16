package blockfighter.server;

import blockfighter.server.entities.mob.Mob;
import blockfighter.server.entities.player.Player;
import blockfighter.server.entities.proj.Projectile;
import blockfighter.server.maps.GameMap;
import blockfighter.server.maps.GameMapArena;
import blockfighter.shared.Globals;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Room {

    private byte roomNumber = -1;
    private byte roomIndex = -1;
    private ConcurrentHashMap<Integer, ConcurrentHashMap<Byte, Player>> playerBuckets;

    private ConcurrentHashMap<Byte, Player> players = new ConcurrentHashMap<>(Globals.SERVER_MAX_PLAYERS, 0.9f,
            Math.max(Globals.SERVER_MAX_PLAYERS / 5, 3));
    private ConcurrentHashMap<Integer, Mob> mobs = new ConcurrentHashMap<>(1, 0.9f, 1);
    private ConcurrentHashMap<Integer, Projectile> projectiles = new ConcurrentHashMap<>(500, 0.75f, 3);

    private GameMap map;
    private int projMaxKeys = 500;
    private int mobMaxKeys = 255;
    private int minLevel = 0, maxLevel = 0;

    private ConcurrentLinkedQueue<Byte> playerKeys = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Integer> projKeys = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Integer> mobKeys = new ConcurrentLinkedQueue<>();

    public Room(final byte roomNumber, final byte roomIndex) {
        this.roomNumber = roomNumber;
        this.roomIndex = roomIndex;
        reset();
    }

    public boolean isFull() {
        return this.playerKeys.isEmpty();
    }

    public boolean isInLevelRange(int level) {
        return level >= this.minLevel && level <= this.maxLevel;
    }

    public byte getRoomIndex() {
        return this.roomIndex;
    }

    private void resetKeys() {
        this.projKeys.clear();
        this.projMaxKeys = 500;
        for (int i = 0; i < this.projMaxKeys; i++) {
            this.projKeys.add(i);
        }
        this.mobKeys.clear();
        this.mobMaxKeys = 255;
        for (int i = 0; i < this.mobMaxKeys; i++) {
            this.mobKeys.add(i);
        }

        this.playerKeys.clear();
        for (byte i = 0; i < Globals.SERVER_MAX_PLAYERS; i++) {
            this.playerKeys.add(i);
        }
    }

    private void resetPlayerBuckets() {
        double numRows = this.map.getMapHeight() / Globals.SERVER_LOGIC_BUCKET_CELLSIZE;
        double numCols = this.map.getMapWidth() / Globals.SERVER_LOGIC_BUCKET_CELLSIZE;
        int numBuckets = (int) Math.ceil(numRows * numCols);
        this.playerBuckets = new ConcurrentHashMap<>(numBuckets);
        Integer[] bucketIDs = getBucketIDsForRect(this.map.getBoundaryRectangle());
        for (int bucketID : bucketIDs) {
            this.playerBuckets.put(bucketID, new ConcurrentHashMap<>(10));
        }
    }

    public final void reset() {
        this.players.clear();
        this.mobs.clear();
        this.projectiles.clear();

        this.setMap(new GameMapArena());
        this.setMinLevel(this.roomNumber * 10 + 1);
        this.setMaxLevel((this.roomNumber + 1) * 10);

        resetKeys();
        resetPlayerBuckets();
        //this.map.spawnMapMobs(this);
    }

    public void clearPlayerBuckets() {
        for (final Map.Entry<Integer, ConcurrentHashMap<Byte, Player>> playerBucket : this.playerBuckets.entrySet()) {
            playerBucket.getValue().clear();
        }
    }

    public void putPlayerIntoBuckets(Player player) {
        Integer[] bucketIDs = getBucketIDsForRect(player.getHitbox());
        for (int bucketID : bucketIDs) {
            if (this.playerBuckets.containsKey(bucketID)) {
                this.playerBuckets.get(bucketID).put(player.getKey(), player);
            }
        }
    }

    public HashMap<Byte, Player> getPlayersNearRect(Rectangle2D.Double rect) {
        HashMap<Byte, Player> nearbyPlayerBuckets = new HashMap<>(this.players.size());
        Integer[] bucketIDs = getBucketIDsForRect(rect);
        for (int bucketID : bucketIDs) {
            if (this.playerBuckets.containsKey(bucketID)) {
                for (final Map.Entry<Byte, Player> player : this.playerBuckets.get(bucketID).entrySet()) {
                    if (!nearbyPlayerBuckets.containsKey(player.getKey())) {
                        nearbyPlayerBuckets.put(player.getKey(), player.getValue());
                    }
                }
            }
        }
        return nearbyPlayerBuckets;
    }

    public HashMap<Byte, Player> getPlayersNearProj(Projectile proj) {
        return getPlayersNearRect(proj.getHitbox()[0]);
    }

    private Integer[] getBucketIDsForRect(Rectangle2D.Double rect) {
        int maxOccupiedCol = 1 + (int) Math.ceil(rect.width / Globals.SERVER_LOGIC_BUCKET_CELLSIZE);
        int maxOccupiedRow = 1 + (int) Math.ceil(rect.height / Globals.SERVER_LOGIC_BUCKET_CELLSIZE);
        ArrayList<Integer> containingBuckets = new ArrayList<>(maxOccupiedRow * maxOccupiedCol);

        double numCol = this.map.getMapWidth() / Globals.SERVER_LOGIC_BUCKET_CELLSIZE;

        double[] rectPointsX = new double[maxOccupiedCol];
        rectPointsX[rectPointsX.length - 1] = rect.getMaxX();
        for (int i = 0; i < rectPointsX.length - 1; i++) {
            rectPointsX[i] = rect.getMinX() + i * Globals.SERVER_LOGIC_BUCKET_CELLSIZE;
            if (rectPointsX[i] < this.map.getBoundary()[Globals.MAP_LEFT]) {
                rectPointsX[i] = this.map.getBoundary()[Globals.MAP_LEFT];
            } else if (rectPointsX[i] > this.map.getBoundary()[Globals.MAP_RIGHT]) {
                rectPointsX[i] = this.map.getBoundary()[Globals.MAP_RIGHT];
            }
        }

        double[] rectPointsY = new double[maxOccupiedRow];
        rectPointsY[rectPointsY.length - 1] = rect.getMaxY();
        for (int i = 0; i < rectPointsY.length - 1; i++) {
            rectPointsY[i] = rect.getMinY() + i * Globals.SERVER_LOGIC_BUCKET_CELLSIZE;
            if (rectPointsY[i] < this.map.getBoundary()[Globals.MAP_TOP]) {
                rectPointsY[i] = this.map.getBoundary()[Globals.MAP_TOP];
            } else if (rectPointsY[i] > this.map.getBoundary()[Globals.MAP_BOTTOM]) {
                rectPointsY[i] = this.map.getBoundary()[Globals.MAP_BOTTOM];
            }
        }

        for (int i = 0; i < rectPointsY.length; i++) {
            for (int j = 0; j < rectPointsX.length; j++) {
                double row = Math.floor(rectPointsY[i] / Globals.SERVER_LOGIC_BUCKET_CELLSIZE) * numCol;
                double col = Math.floor(rectPointsX[j] / Globals.SERVER_LOGIC_BUCKET_CELLSIZE);
                int id = (int) (row + col);
                if (!containingBuckets.contains(id)) {
                    containingBuckets.add(id);
                }
            }
        }
        return containingBuckets.toArray(new Integer[containingBuckets.size()]);
    }

    public ConcurrentHashMap<Byte, Player> getPlayers() {
        return this.players;
    }

    public ConcurrentHashMap<Integer, Mob> getMobs() {
        return this.mobs;
    }

    public ConcurrentHashMap<Integer, Projectile> getProj() {
        return this.projectiles;
    }

    public GameMap getMap() {
        return this.map;
    }

    public byte getRoomNumber() {
        return this.roomNumber;
    }

    public byte getNextPlayerKey() {
        if (this.playerKeys.isEmpty()) {
            return -1;
        }
        return this.playerKeys.poll();
    }

    public int getNextMobKey() {
        if (this.mobKeys.isEmpty()) {
            return -1;
        }
        return this.mobKeys.poll();
    }

    public int getNextProjKey() {
        Integer nextKey = this.projKeys.poll();
        while (nextKey == null) {
            this.projKeys.add(this.projMaxKeys);
            this.projMaxKeys++;
            nextKey = this.projKeys.poll();
        }
        return nextKey;
    }

    public void returnProjKey(final int key) {
        this.projKeys.add(key);
    }

    public void returnMobKey(final int key) {
        this.mobKeys.add(key);
    }

    public void returnPlayerKey(final byte key) {
        this.playerKeys.add(key);
        Globals.log(Room.class, "Room: " + this.roomNumber + " Returned player key: " + key + " Keys Remaining: " + this.playerKeys.size(), Globals.LOG_TYPE_DATA, true);
    }

    public boolean containsPlayerID(final UUID id) {
        for (final Map.Entry<Byte, Player> player : this.players.entrySet()) {
            if (player.getValue().getUniqueID().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public byte getPlayerKey(final UUID id) {
        for (final Map.Entry<Byte, Player> player : this.players.entrySet()) {
            if (player.getValue().getUniqueID().equals(id)) {
                return player.getKey();
            }
        }
        return -1;
    }

    public void addPlayer(final Player newPlayer) {
        final byte key = newPlayer.getKey();
        this.players.put(key, newPlayer);
    }

    public ArrayList<Player> getPlayersInRange(final Player player, final double radius) {
        Rectangle2D.Double rect = new Rectangle2D.Double(player.getX() - radius, player.getY() - radius, radius * 2, radius * 2);
        ArrayList<Player> playersInRange = new ArrayList<>(Globals.SERVER_MAX_PLAYERS);
        for (final Map.Entry<Byte, Player> pEntry : getPlayersNearRect(rect).entrySet()) {
            final Player p = pEntry.getValue();
            if (p != player && !p.isDead() && !p.isInvulnerable()) {
                double distance = Math.sqrt(Math.pow((player.getX() - p.getX()), 2) + Math.pow((player.getY() - p.getY()), 2));
                if (distance <= radius) {
                    playersInRange.add(p);
                }
            }
        }
        return playersInRange;
    }

    public ArrayList<Mob> getMobsInRange(final Player player, final double radius) {
        ArrayList<Mob> mobInRange = new ArrayList<>(getMobs().size());
        for (final Map.Entry<Integer, Mob> bEntry : getMobs().entrySet()) {
            final Mob b = bEntry.getValue();
            double distance = Math.sqrt(Math.pow((player.getX() - b.getX()), 2) + Math.pow((player.getY() - b.getY()), 2));
            if (distance <= 100) {
                mobInRange.add(b);
            }
        }
        return mobInRange;
    }

    public void setPlayerKeys(ConcurrentLinkedQueue<Byte> playerKeys) {
        this.playerKeys = playerKeys;
    }

    public void setMobKeys(ConcurrentLinkedQueue<Integer> mobKeys) {
        this.mobKeys = mobKeys;
    }

    public void setProjKeys(ConcurrentLinkedQueue<Integer> projKeys) {
        this.projKeys = projKeys;
        this.projMaxKeys = this.projKeys.size();
    }

    public void setPlayers(ConcurrentHashMap<Byte, Player> players) {
        this.players = players;
    }

    public void setMobs(ConcurrentHashMap<Integer, Mob> mobs) {
        this.mobs = mobs;
    }

    public void setProjectiles(ConcurrentHashMap<Integer, Projectile> projectiles) {
        this.projectiles = projectiles;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public boolean isComplete() {
        return false;
    }
}
