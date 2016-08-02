package performancetest;

import java.util.UUID;

public class TestSaveData {

    private final double[] baseStats = new double[Globals.NUM_STATS];
    private UUID uniqueID;
    private final String name;

    public TestSaveData(String n) {
        name = n;
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public String getPlayerName() {
        return this.name;
    }

    public void newCharacter(final int lvl) {
        // Set level 1
        this.baseStats[Globals.STAT_LEVEL] = lvl;
        this.baseStats[Globals.STAT_POWER] = 0;
        this.baseStats[Globals.STAT_DEFENSE] = 700;
        this.baseStats[Globals.STAT_SPIRIT] = 0;
        this.baseStats[Globals.STAT_EXP] = 0;
        this.baseStats[Globals.STAT_SKILLPOINTS] = 3 * this.baseStats[Globals.STAT_LEVEL];
        this.uniqueID = UUID.randomUUID();
    }

    public double[] getTotalStats() {
        return this.baseStats;
    }
}
