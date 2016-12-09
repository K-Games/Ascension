package blockfighter.shared;

import java.io.Serializable;

public class ServerInfo implements Serializable {

    private String address;
    private String region;
    private int capacity;

    public ServerInfo() {
    }

    public ServerInfo(final String address, final String region, final int capactiy) {
        this.address = address;
        this.region = region;
        this.capacity = capactiy;
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

    public void setAddress(final String address) {
        this.address = address;
    }

    public void setRegion(final String r) {
        this.region = r;
    }

    public void setCapacity(final int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return this.address + "[Region=" + this.region + ", Capacity=" + this.capacity + "]";
    }
}
