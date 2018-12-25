package performancetest;

import blockfighter.client.entities.player.skills.PlayerSkillData;
import blockfighter.shared.Globals;
import java.util.HashMap;
import java.util.UUID;

public class TestSaveData {

    private final double[] baseStats = new double[Globals.NUM_STATS];
    private UUID uniqueID;
    private final String name;
    private final int aiType = Globals.rng(2);

    private final HashMap<Byte, PlayerSkillData> skills = new HashMap<>(Globals.NUM_SKILLS);

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

        this.baseStats[Globals.STAT_LEVEL] = lvl;
        double remainingSP = 0;
        for (int level = 1; level <= this.baseStats[Globals.STAT_LEVEL]; level++) {
            if (level <= 40) {
                remainingSP += 3;
            } else if (level <= 70) {
                remainingSP += 4;
            } else if (level <= 90) {
                remainingSP += 6;
            } else if (level <= 100) {
                remainingSP += 12;
            }
        }
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

    public HashMap<Byte, PlayerSkillData> getSkills() {
        return this.skills;
    }

    public int getAI() {
        return this.aiType;
    }
}
