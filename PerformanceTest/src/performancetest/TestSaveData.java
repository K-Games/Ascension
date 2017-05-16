package performancetest;

import blockfighter.shared.Globals;
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
        double remainingSP = lvl * Globals.STAT_PER_LEVEL * 3;
        this.baseStats[Globals.STAT_LEVEL] = lvl;
        this.baseStats[Globals.STAT_POWER] = Globals.rng((int) remainingSP);
        remainingSP -= this.baseStats[Globals.STAT_POWER];
        this.baseStats[Globals.STAT_DEFENSE] = Globals.rng((int) remainingSP);
        remainingSP -= this.baseStats[Globals.STAT_DEFENSE];
        this.baseStats[Globals.STAT_SPIRIT] = remainingSP;
        this.baseStats[Globals.STAT_EXP] = 0;
        this.baseStats[Globals.STAT_SKILLPOINTS] = 3 * this.baseStats[Globals.STAT_LEVEL];
        this.uniqueID = UUID.randomUUID();
    }

    public double[] getTotalStats() {
        return this.baseStats;
    }
}
