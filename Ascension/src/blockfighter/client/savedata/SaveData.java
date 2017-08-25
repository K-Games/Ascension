package blockfighter.client.savedata;

import blockfighter.client.entities.items.Item;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.items.ItemUpgrade;
import blockfighter.client.entities.player.skills.Skill;
import blockfighter.shared.Globals;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;
import org.apache.commons.io.FileUtils;

public class SaveData {

    private static final int LEGACY_SAVE_DATA_LENGTH = 45485;
    public static final int SAVE_VERSION_0240 = 240;
    public static final int SAVE_VERSION_0232 = 232;
    public static final int SAVE_VERSION_0231 = 231;
    private static final int CURRENT_SAVE_VERSION = SAVE_VERSION_0240;
    private static final HashMap<Integer, Class<? extends SaveDataReader>> SAVE_READERS = new HashMap<>();
    private static final HashMap<Integer, Class<? extends SaveDataWriter>> SAVE_WRITERS = new HashMap<>();

    static {
        SAVE_READERS.put(SAVE_VERSION_0240, blockfighter.client.savedata.ver_0_24_0.SaveDataReaderImpl.class);
        SAVE_READERS.put(SAVE_VERSION_0232, blockfighter.client.savedata.ver_0_23_2.SaveDataReaderImpl.class);
        SAVE_READERS.put(SAVE_VERSION_0231, blockfighter.client.savedata.ver_0_23_1.SaveDataReaderImpl.class);

        SAVE_WRITERS.put(SAVE_VERSION_0240, blockfighter.client.savedata.ver_0_24_0.SaveDataWriterImpl.class);
        SAVE_WRITERS.put(SAVE_VERSION_0232, blockfighter.client.savedata.ver_0_23_2.SaveDataWriterImpl.class);
        SAVE_WRITERS.put(SAVE_VERSION_0231, blockfighter.client.savedata.ver_0_23_1.SaveDataWriterImpl.class);
    }

    private final double[] baseStats = new double[Globals.NUM_STATS],
            totalStats = new double[Globals.NUM_STATS],
            bonusStats = new double[Globals.NUM_STATS];

    private UUID uniqueID;
    private String name;
    private final byte saveNum;

    private final ItemEquip[][] inventory = new ItemEquip[Globals.NUM_EQUIP_TABS][100];
    private final ItemUpgrade[] upgrades = new ItemUpgrade[100];
    private final ItemEquip[] equipment = new ItemEquip[Globals.NUM_EQUIP_SLOTS];

    private final HashMap<Byte, Skill> hotkeys = new HashMap<>(12);
    private final HashMap<Byte, Skill> skills = new HashMap<>(Globals.NUM_SKILLS);
    private final int[] keybinds = new int[Globals.NUM_KEYBINDS];

    public SaveData(final String n, final byte saveNumber) throws InstantiationException, IllegalAccessException {
        this.saveNum = saveNumber;
        this.name = n;
        this.uniqueID = UUID.randomUUID();
        // initalize skill list
        for (Globals.SkillClassMap skill : Globals.SkillClassMap.values()) {
            this.skills.put(skill.getByteCode(), skill.getClientClass().newInstance());
        }
        Arrays.fill(this.keybinds, -1);
    }

    public void newCharacter(final boolean testMax) {
        // Set level 1
        this.baseStats[Globals.STAT_LEVEL] = (testMax) ? 100 : 1;
        this.baseStats[Globals.STAT_POWER] = 0;
        this.baseStats[Globals.STAT_DEFENSE] = 0;
        this.baseStats[Globals.STAT_SPIRIT] = 0;
        this.baseStats[Globals.STAT_EXP] = 0;
        this.baseStats[Globals.STAT_SKILLPOINTS] = 3 * this.baseStats[Globals.STAT_LEVEL];

        // Empty inventory
        for (int i = 0; i < this.inventory.length; i++) {
            this.inventory[i] = new ItemEquip[100];
        }

        for (int i = 0; i < 5; i++) {
            addItem(new ItemUpgrade(ItemUpgrade.ITEM_TOME, (int) this.baseStats[Globals.STAT_LEVEL]));
        }

        Globals.ITEM_CODES.stream().map((itemCode) -> new ItemEquip(itemCode, this.baseStats[Globals.STAT_LEVEL], Globals.TEST_MAX_LEVEL)).forEachOrdered((startEq) -> {
            addItem(startEq);
        });

        this.keybinds[Globals.KEYBIND_SKILL1] = KeyEvent.VK_Q;
        this.keybinds[Globals.KEYBIND_SKILL2] = KeyEvent.VK_W;
        this.keybinds[Globals.KEYBIND_SKILL3] = KeyEvent.VK_E;
        this.keybinds[Globals.KEYBIND_SKILL4] = KeyEvent.VK_R;
        this.keybinds[Globals.KEYBIND_SKILL5] = KeyEvent.VK_T;
        this.keybinds[Globals.KEYBIND_SKILL6] = KeyEvent.VK_Y;
        this.keybinds[Globals.KEYBIND_SKILL7] = KeyEvent.VK_A;
        this.keybinds[Globals.KEYBIND_SKILL8] = KeyEvent.VK_S;
        this.keybinds[Globals.KEYBIND_SKILL9] = KeyEvent.VK_D;
        this.keybinds[Globals.KEYBIND_SKILL10] = KeyEvent.VK_F;
        this.keybinds[Globals.KEYBIND_SKILL11] = KeyEvent.VK_G;
        this.keybinds[Globals.KEYBIND_SKILL12] = KeyEvent.VK_H;

        this.keybinds[Globals.KEYBIND_LEFT] = KeyEvent.VK_LEFT;
        this.keybinds[Globals.KEYBIND_RIGHT] = KeyEvent.VK_RIGHT;
        this.keybinds[Globals.KEYBIND_JUMP] = KeyEvent.VK_SPACE;
        this.keybinds[Globals.KEYBIND_DOWN] = KeyEvent.VK_DOWN;
        this.keybinds[Globals.KEYBIND_EMOTE1] = KeyEvent.VK_1;
        this.keybinds[Globals.KEYBIND_EMOTE2] = KeyEvent.VK_2;
        this.keybinds[Globals.KEYBIND_EMOTE3] = KeyEvent.VK_3;
        this.keybinds[Globals.KEYBIND_EMOTE4] = KeyEvent.VK_4;
        this.keybinds[Globals.KEYBIND_EMOTE5] = KeyEvent.VK_5;
        this.keybinds[Globals.KEYBIND_EMOTE6] = KeyEvent.VK_6;
        this.keybinds[Globals.KEYBIND_EMOTE7] = KeyEvent.VK_7;
        this.keybinds[Globals.KEYBIND_EMOTE8] = KeyEvent.VK_8;
        this.keybinds[Globals.KEYBIND_EMOTE9] = KeyEvent.VK_9;
        this.keybinds[Globals.KEYBIND_EMOTE10] = KeyEvent.VK_0;

        this.keybinds[Globals.KEYBIND_SCOREBOARD] = KeyEvent.VK_TAB;
    }

    public static void writeSaveData(final byte saveNum, final SaveData c) {
        SaveDataWriter writer;
        try {
            Globals.log(SaveData.class, "Grabbing Save Data Writer " + SAVE_READERS.get(CURRENT_SAVE_VERSION).getName(), Globals.LOG_TYPE_DATA);
            writer = SAVE_WRITERS.get(CURRENT_SAVE_VERSION).newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Globals.logError("Failed to grab Save Data Writer " + SAVE_READERS.get(CURRENT_SAVE_VERSION).getName(), ex);
            return;
        }

        try {
            Globals.log(SaveData.class, "Writing Save Data with " + writer.getClass().getName(), Globals.LOG_TYPE_DATA);
            FileUtils.writeByteArrayToFile(new File(Globals.SAVE_FILE_DIRECTORY, saveNum + ".tcdat"), writer.writeSaveData(c));
        } catch (final IOException ex) {
            Globals.logError(ex.toString(), ex);
        }
    }

    public static SaveData readSaveData(final byte saveNum) throws InstantiationException, IllegalAccessException {
        final SaveData c = new SaveData("", saveNum);
        final int legacyLength = LEGACY_SAVE_DATA_LENGTH;
        byte[] data;
        try {
            data = FileUtils.readFileToByteArray(new File(Globals.SAVE_FILE_DIRECTORY, saveNum + ".tcdat"));
            Globals.log(SaveData.class, "Loading Save Data " + saveNum + "...", Globals.LOG_TYPE_DATA);
        } catch (final IOException ex) {
            return null;
        }

        int saveVersion = SAVE_VERSION_0231;
        if (data.length != legacyLength) {
            byte[] temp = new byte[Integer.BYTES];
            System.arraycopy(data, 0, temp, 0, temp.length);
            saveVersion = Globals.bytesToInt(temp);
        }

        SaveDataReader reader;
        try {
            Globals.log(SaveData.class, "Grabbing Save Data Reader " + SAVE_READERS.get(saveVersion).getName(), Globals.LOG_TYPE_DATA);
            reader = SAVE_READERS.get(saveVersion).newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Globals.logError("Failed to grab Save Data Reader " + SAVE_READERS.get(CURRENT_SAVE_VERSION).getName(), ex);
            return null;
        }

        Globals.log(SaveData.class, "Reading Save Data with " + reader.getClass().getName(), Globals.LOG_TYPE_DATA);
        return reader.readSaveData(c, data);
    }

    public HashMap<Byte, Skill> getHotkeys() {
        return this.hotkeys;
    }

    public HashMap<Byte, Skill> getSkills() {
        return this.skills;
    }

    public String getPlayerName() {
        return this.name;
    }

    public double[] getBaseStats() {
        return this.baseStats;
    }

    public double[] getTotalStats() {
        return this.totalStats;
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public void addDrops(final int lvl, final Item dropItemCode) {
        if (dropItemCode instanceof ItemEquip) {
            addItem((ItemEquip) dropItemCode);
        }
        if (dropItemCode instanceof ItemUpgrade) {
            addItem((ItemUpgrade) dropItemCode);
        }
    }

    public void addExp(final double amount) {
        this.baseStats[Globals.STAT_EXP] += amount;
        if (this.baseStats[Globals.STAT_LEVEL] >= 100) {
            if (this.baseStats[Globals.STAT_EXP] >= this.baseStats[Globals.STAT_MAXEXP]) {
                this.baseStats[Globals.STAT_EXP] = this.baseStats[Globals.STAT_MAXEXP];
            }
            return;
        }
        while (this.baseStats[Globals.STAT_EXP] >= this.baseStats[Globals.STAT_MAXEXP]) {
            levelUp();
        }
    }

    public void levelUp() {
        this.baseStats[Globals.STAT_EXP] -= this.baseStats[Globals.STAT_MAXEXP];
        this.baseStats[Globals.STAT_LEVEL] += 1;
        calcStats();
        writeSaveData(this.saveNum, this);
    }

    public void calcStats() {

        if (this.baseStats[Globals.STAT_LEVEL] > 100) {
            this.baseStats[Globals.STAT_LEVEL] = 100;
        }

        for (int i = 0; i < this.bonusStats.length; i++) {
            this.bonusStats[i] = 0;
            for (final ItemEquip e : this.equipment) {
                if (i != Globals.STAT_LEVEL && e != null) {
                    this.bonusStats[i] += e.getTotalStats()[i];
                }
            }
        }

        this.baseStats[Globals.STAT_POINTS] = this.baseStats[Globals.STAT_LEVEL] * Globals.STAT_PER_LEVEL
                - (this.baseStats[Globals.STAT_POWER]
                + this.baseStats[Globals.STAT_DEFENSE]
                + this.baseStats[Globals.STAT_SPIRIT]);

        if (this.baseStats[Globals.STAT_POWER]
                + this.baseStats[Globals.STAT_DEFENSE]
                + this.baseStats[Globals.STAT_SPIRIT] > this.baseStats[Globals.STAT_LEVEL] * Globals.STAT_PER_LEVEL) {
            this.baseStats[Globals.STAT_POINTS] = this.baseStats[Globals.STAT_LEVEL] * Globals.STAT_PER_LEVEL;
            this.baseStats[Globals.STAT_POWER] = 0;
            this.baseStats[Globals.STAT_DEFENSE] = 0;
            this.baseStats[Globals.STAT_SPIRIT] = 0;
        }

        int totalSP = 0;
        for (final Skill s : this.skills.values()) {
            totalSP += s.getLevel();
        }
        this.baseStats[Globals.STAT_SKILLPOINTS] = Globals.SP_PER_LEVEL * this.baseStats[Globals.STAT_LEVEL] - totalSP;

        if (totalSP > Globals.SP_PER_LEVEL * this.baseStats[Globals.STAT_LEVEL]) {
            for (final Skill s : this.skills.values()) {
                s.setLevel((byte) 0);
            }
            this.baseStats[Globals.STAT_SKILLPOINTS] = Globals.SP_PER_LEVEL * this.baseStats[Globals.STAT_LEVEL];
        }

        System.arraycopy(this.baseStats, 0, this.totalStats, 0, this.baseStats.length);

        this.totalStats[Globals.STAT_POWER] = (int) (this.baseStats[Globals.STAT_POWER] + this.bonusStats[Globals.STAT_POWER]);
        this.totalStats[Globals.STAT_DEFENSE] = (int) (this.baseStats[Globals.STAT_DEFENSE] + this.bonusStats[Globals.STAT_DEFENSE]);
        this.totalStats[Globals.STAT_SPIRIT] = (int) (this.baseStats[Globals.STAT_SPIRIT] + this.bonusStats[Globals.STAT_SPIRIT]);

        this.totalStats[Globals.STAT_MAXHP] = Globals.calcMaxHP(this.totalStats[Globals.STAT_DEFENSE]);
        this.totalStats[Globals.STAT_MINHP] = this.baseStats[Globals.STAT_MAXHP];

        this.totalStats[Globals.STAT_MINDMG] = Globals.calcMinDmg(this.totalStats[Globals.STAT_POWER]);
        this.totalStats[Globals.STAT_MAXDMG] = Globals.calcMaxDmg(this.totalStats[Globals.STAT_POWER]);

        this.baseStats[Globals.STAT_ARMOUR] = Globals.calcArmour(this.totalStats[Globals.STAT_DEFENSE]);
        this.baseStats[Globals.STAT_REGEN] = Globals.calcRegen(this.totalStats[Globals.STAT_SPIRIT]);
        this.baseStats[Globals.STAT_CRITCHANCE] = Globals.calcCritChance(this.totalStats[Globals.STAT_SPIRIT]);
        this.baseStats[Globals.STAT_CRITDMG] = Globals.calcCritDmg(this.totalStats[Globals.STAT_SPIRIT]);

        this.totalStats[Globals.STAT_ARMOUR] = this.baseStats[Globals.STAT_ARMOUR] + this.bonusStats[Globals.STAT_ARMOUR];
        this.totalStats[Globals.STAT_REGEN] = this.baseStats[Globals.STAT_REGEN] + this.bonusStats[Globals.STAT_REGEN];
        this.totalStats[Globals.STAT_CRITCHANCE] = this.baseStats[Globals.STAT_CRITCHANCE] + this.bonusStats[Globals.STAT_CRITCHANCE];
        this.totalStats[Globals.STAT_CRITDMG] = this.baseStats[Globals.STAT_CRITDMG] + this.bonusStats[Globals.STAT_CRITDMG];
        this.totalStats[Globals.STAT_DAMAGEREDUCT] = Globals.calcReduction(this.totalStats[Globals.STAT_ARMOUR]);
        this.baseStats[Globals.STAT_MAXEXP] = Globals.calcEXPtoNxtLvl(this.baseStats[Globals.STAT_LEVEL]);
    }

    public ItemEquip[][] getInventory() {
        return this.inventory;
    }

    public ItemEquip[] getInventory(final byte type) {
        return this.inventory[type];
    }

    public ItemEquip[] getEquip() {
        return this.equipment;
    }

    public ItemUpgrade[] getUpgrades() {
        return this.upgrades;
    }

    public int[] getKeyBind() {
        return this.keybinds;
    }

    public double[] getBonusStats() {
        return this.bonusStats;
    }

    public byte getSaveNum() {
        return this.saveNum;
    }

    public void resetStat() {
        this.baseStats[Globals.STAT_POWER] = 0;
        this.baseStats[Globals.STAT_DEFENSE] = 0;
        this.baseStats[Globals.STAT_SPIRIT] = 0;
        calcStats();
        writeSaveData(this.saveNum, this);
    }

    public void resetSkill() {
        for (final Skill skill : this.skills.values()) {
            skill.setLevel((byte) 0);
        }
        calcStats();
        writeSaveData(this.saveNum, this);
    }

    public void addSkill(final byte skillCode, final boolean isMax) {
        if (this.baseStats[Globals.STAT_SKILLPOINTS] <= 0 || this.skills.get(skillCode).getLevel() >= 30) {
            return;
        }
        if (!isMax) {
            this.baseStats[Globals.STAT_SKILLPOINTS]--;
            this.skills.get(skillCode).addLevel((byte) 1);
        } else {
            double skillPointsRequired = 30 - this.skills.get(skillCode).getLevel();
            double amount = skillPointsRequired;
            if (this.baseStats[Globals.STAT_SKILLPOINTS] < skillPointsRequired) {
                amount = this.baseStats[Globals.STAT_SKILLPOINTS];
            }
            this.baseStats[Globals.STAT_SKILLPOINTS] -= amount;
            this.skills.get(skillCode).addLevel((byte) amount);
        }
        writeSaveData(this.saveNum, this);
    }

    public void addStat(final byte stat, final int amount) {
        if (this.baseStats[Globals.STAT_POINTS] < amount) {
            return;
        }
        this.baseStats[Globals.STAT_POINTS] -= amount;
        this.baseStats[stat] += amount;

        calcStats();
        writeSaveData(this.saveNum, this);
    }

    public void unequipItem(final byte equipTab, final byte equipSlot) {
        for (int i = 0; i < this.inventory[equipTab].length; i++) {
            if (this.inventory[equipTab][i] == null) {
                this.inventory[equipTab][i] = this.equipment[equipSlot];
                this.equipment[equipSlot] = null;
                break;
            }
        }
        calcStats();
        writeSaveData(this.saveNum, this);
    }

    public void equipItem(final int equipTab, int equipSlot, final int inventorySlot) {
        final ItemEquip itemToEquip = this.inventory[equipTab][inventorySlot];
        if (itemToEquip != null) {
            if (itemToEquip.getBaseStats()[Globals.STAT_LEVEL] > this.baseStats[Globals.STAT_LEVEL]) {
                return;
            }
        }
        this.inventory[equipTab][inventorySlot] = this.equipment[equipSlot];
        this.equipment[equipSlot] = itemToEquip;
        calcStats();
        writeSaveData(this.saveNum, this);
    }

    public void destroyItem(final int type, final int slot) {
        this.inventory[type][slot] = null;
        writeSaveData(this.saveNum, this);
    }

    public void destroyItem(final int slot) {
        this.upgrades[slot] = null;
        writeSaveData(this.saveNum, this);
    }

    public void destroyAll(final int type) {
        for (int i = 0; i < this.inventory[type].length; i++) {
            this.inventory[type][i] = null;
        }
        writeSaveData(this.saveNum, this);
    }

    public void destroyAllUpgrade() {
        for (int i = 0; i < this.upgrades.length; i++) {
            this.upgrades[i] = null;
        }
        writeSaveData(this.saveNum, this);
    }

    public void addItem(final ItemEquip e) {
        int equipTab = e.getItemType();
        if (equipTab == Globals.ITEM_SHIELD || equipTab == Globals.ITEM_ARROW || equipTab == Globals.ITEM_BOW) {
            equipTab = Globals.EQUIP_WEAPON;
        }
        for (int i = 0; i < this.inventory[equipTab].length; i++) {
            if (this.inventory[equipTab][i] == null) {
                this.inventory[equipTab][i] = e;
                break;
            }
        }
        writeSaveData(this.saveNum, this);
    }

    public void addItem(final ItemUpgrade e) {
        for (int i = 0; i < this.upgrades.length; i++) {
            if (this.upgrades[i] == null) {
                this.upgrades[i] = e;
                break;
            }
        }
        writeSaveData(this.saveNum, this);
    }

    public void setKeyBind(final int k, final int keycode) {
        this.keybinds[k] = keycode;
        for (int i = 0; i < this.keybinds.length; i++) {
            if (i != k && this.keybinds[i] == keycode) {
                this.keybinds[i] = -1;
            }
        }
        writeSaveData(this.saveNum, this);
    }

    public void setPlayerName(final String name) {
        this.name = name;
    }

    public void setUniqueID(final UUID id) {
        this.uniqueID = id;
    }

    public void sortUpgradeItems() {
        Arrays.sort(this.upgrades, Comparator.nullsLast(Comparator.reverseOrder()));
        writeSaveData(this.saveNum, this);
    }

    public void validate() {
        for (byte i = 0; i < getHotkeys().size(); i++) {
            if (getHotkeys().get(i) != null && getTotalStats()[Globals.STAT_LEVEL] < getHotkeys().get(i).getReqLevel()) {
                getHotkeys().remove(i);
            }
        }
    }
}
