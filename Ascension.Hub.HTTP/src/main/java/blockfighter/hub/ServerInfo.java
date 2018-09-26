package blockfighter.hub;

import java.io.Serializable;
import org.json.JSONObject;

public class ServerInfo implements Serializable {

    private final long createdTime = System.currentTimeMillis();

    private String address;
    private String region;
    private int capacity;
    private int tcpPort, udpPort;

    private static final String ADDRESS = "address";
    private static final String REGION = "region";
    private static final String CAPACITY = "capacity";
    private static final String TCP_PORT = "tcpPort";
    private static final String UDP_PORT = "udpPort";

    public ServerInfo() {
    }

    public ServerInfo(final JSONObject serverInfo) {
        if (!serverInfo.has(ADDRESS) || !serverInfo.has(CAPACITY) || !serverInfo.has(TCP_PORT) || !serverInfo.has(UDP_PORT)) {
            throw new NullPointerException();
        }
        address = serverInfo.getString(ADDRESS);
        region = serverInfo.has(REGION) ? serverInfo.getString(REGION) : "";
        capacity = serverInfo.getInt(CAPACITY);
        tcpPort = serverInfo.getInt(TCP_PORT);
        udpPort = serverInfo.getInt(UDP_PORT);
    }

    public ServerInfo(final String address, final String region, final int capacity, final int tcpPort, final int udpPort) {
        this.address = address;
        this.region = region;
        this.capacity = capacity;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
    }

    public String getAddress() {
        return this.address;
    }

    public String getRegion() {
        return this.region;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getTcpPort() {
        return this.tcpPort;
    }

    public int getUdpPort() {
        return this.udpPort;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public void setRegion(final String r) {
        this.region = r;
    }

    public void setCapacity(final int capacity) {
        this.capacity = capacity;
    }

    public void setTcpPort(final int port) {
        this.tcpPort = port;
    }

    public void setUdpPort(final int port) {
        this.udpPort = port;
    }

    @Override
    public String toString() {
        return this.address + " TCP=" + tcpPort + " UDP=" + udpPort + " [Region=\"" + this.region + "\", Capacity=" + this.capacity + "]";
    }

    public long getTimeAlive() {
        return System.currentTimeMillis() - createdTime;
    }
}
