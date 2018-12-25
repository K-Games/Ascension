package blockfighter.client.savedata;

import blockfighter.client.entities.items.Item;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.items.ItemUpgrade;
import blockfighter.client.entities.player.skills.PlayerSkillData;
import blockfighter.client.savedata.json.SaveDataReader;
import blockfighter.client.savedata.json.SaveDataWriter;
import blockfighter.shared.Globals;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;

public class SaveData {

    private double[] baseStats = new double[Globals.NUM_STATS];

    private UUID uniqueID;
    private String characterName;

    private HashMap<Integer, HashMap<Integer, ItemEquip>> inventory = new HashMap<>();
    private HashMap<Integer, ItemUpgrade> upgrades = new HashMap<>();
    private HashMap<Byte, ItemEquip> equipment = new HashMap<>(Globals.NUM_EQUIP_SLOTS);

    private HashMap<Byte, Byte> hotkeys = new HashMap<>(12);
    private HashMap<Byte, PlayerSkillData> skills = new HashMap<>(Globals.NUM_SKILLS);
    private HashMap<Byte, Integer> keybinds = new HashMap<>(Globals.NUM_KEYBINDS);

    private transient byte saveNum;
    private final transient double[] totalStats = new double[Globals.NUM_STATS];
    private final transient double[] bonusStats = new double[Globals.NUM_STATS];

    public SaveData() {
    }

    public SaveData(final String characterName) {
        this.characterName = characterName;
        this.uniqueID = UUID.randomUUID();
    }

    public void setSaveNumber(byte saveNumber) {
        this.saveNum = saveNumber;
    }

    public void createNewCharacterLoadout(final boolean testMax) {
        // Set level 1
        this.baseStats[Globals.STAT_LEVEL] = (testMax) ? 100 : 1;
        this.baseStats[Globals.STAT_POWER] = 0;
        this.baseStats[Globals.STAT_DEFENSE] = 0;
        this.baseStats[Globals.STAT_SPIRIT] = 0;
        this.baseStats[Globals.STAT_EXP] = 0;
        this.baseStats[Globals.STAT_SKILLPOINTS] = 3 * this.baseStats[Globals.STAT_LEVEL];

        // initalize skill list
        for (Globals.SkillClassMap skill : Globals.SkillClassMap.values()) {
            PlayerSkillData newSkillData = new PlayerSkillData();
            newSkillData.setSkillCode(skill.getByteCode());
            newSkillData.setLevel((byte) 0);

            this.skills.put(skill.getByteCode(), newSkillData);
        }

        // Empty inventory
        for (int i = 0; i < Globals.NUM_EQUIP_TABS; i++) {
            this.inventory.put(i, new HashMap<>());
        }

        if (testMax) {
            for (int i = 0; i < 5; i++) {
                addItem(new ItemUpgrade(ItemUpgrade.ITEM_TOME, (int) this.baseStats[Globals.STAT_LEVEL]));
            }
            Globals.ITEM_EQUIP_CODES.stream().map((itemCode) -> new ItemEquip(itemCode, this.baseStats[Globals.STAT_LEVEL], Globals.TEST_MAX_LEVEL)).forEachOrdered((startEq) -> {
                addItem(startEq);
            });
        } else {
            // Insanity Blade
            addItem(new ItemEquip(100000, this.baseStats[Globals.STAT_LEVEL], false));
            // Poor Man's Shield
            addItem(new ItemEquip(110000, this.baseStats[Globals.STAT_LEVEL], false));
            // Eaglethorn
            addItem(new ItemEquip(120000, this.baseStats[Globals.STAT_LEVEL], false));
            // Silver Gems
            addItem(new ItemEquip(130000, this.baseStats[Globals.STAT_LEVEL], false));
        }

        this.keybinds.put(Globals.KEYBIND_SKILL1, KeyEvent.VK_Q);
        this.keybinds.put(Globals.KEYBIND_SKILL2, KeyEvent.VK_W);
        this.keybinds.put(Globals.KEYBIND_SKILL3, KeyEvent.VK_E);
        this.keybinds.put(Globals.KEYBIND_SKILL4, KeyEvent.VK_R);
        this.keybinds.put(Globals.KEYBIND_SKILL5, KeyEvent.VK_T);
        this.keybinds.put(Globals.KEYBIND_SKILL6, KeyEvent.VK_Y);
        this.keybinds.put(Globals.KEYBIND_SKILL7, KeyEvent.VK_A);
        this.keybinds.put(Globals.KEYBIND_SKILL8, KeyEvent.VK_S);
        this.keybinds.put(Globals.KEYBIND_SKILL9, KeyEvent.VK_D);
        this.keybinds.put(Globals.KEYBIND_SKILL10, KeyEvent.VK_F);
        this.keybinds.put(Globals.KEYBIND_SKILL11, KeyEvent.VK_G);
        this.keybinds.put(Globals.KEYBIND_SKILL12, KeyEvent.VK_H);

        this.keybinds.put(Globals.KEYBIND_LEFT, KeyEvent.VK_LEFT);
        this.keybinds.put(Globals.KEYBIND_RIGHT, KeyEvent.VK_RIGHT);
        this.keybinds.put(Globals.KEYBIND_JUMP, KeyEvent.VK_SPACE);
        this.keybinds.put(Globals.KEYBIND_DOWN, KeyEvent.VK_DOWN);
        this.keybinds.put(Globals.KEYBIND_EMOTE1, KeyEvent.VK_1);
        this.keybinds.put(Globals.KEYBIND_EMOTE2, KeyEvent.VK_2);
        this.keybinds.put(Globals.KEYBIND_EMOTE3, KeyEvent.VK_3);
        this.keybinds.put(Globals.KEYBIND_EMOTE4, KeyEvent.VK_4);
        this.keybinds.put(Globals.KEYBIND_EMOTE5, KeyEvent.VK_5);
        this.keybinds.put(Globals.KEYBIND_EMOTE6, KeyEvent.VK_6);
        this.keybinds.put(Globals.KEYBIND_EMOTE7, KeyEvent.VK_7);
        this.keybinds.put(Globals.KEYBIND_EMOTE8, KeyEvent.VK_8);
        this.keybinds.put(Globals.KEYBIND_EMOTE9, KeyEvent.VK_9);
        this.keybinds.put(Globals.KEYBIND_EMOTE10, KeyEvent.VK_0);

        this.keybinds.put(Globals.KEYBIND_SCOREBOARD, KeyEvent.VK_TAB);
        completeSaveDataLoad();
    }

    public static void writeSaveData(final byte saveNum, final SaveData c) {
        try {
            Globals.log(SaveData.class, "Writing Save Data with " + SaveDataWriter.class.getName(), Globals.LOG_TYPE_DATA);
            FileUtils.writeByteArrayToFile(new File(Globals.SAVE_FILE_DIRECTORY, saveNum + ".tcdat"), SaveDataWriter.writeSaveData(c));
        } catch (final IOException ex) {
            Globals.logError(ex.toString(), ex);
        }
    }

    public static SaveData readSaveData(final byte saveNum) throws InstantiationException, IllegalAccessException, IOException {
        try {
            SaveData saveData = SaveDataReader.readSaveData(FileUtils.readFileToByteArray(new File(Globals.SAVE_FILE_DIRECTORY, saveNum + ".tcdat")));
            saveData.setSaveNum(saveNum);
            saveData.completeSaveDataLoad();
            return saveData;
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<Byte, Byte> getHotkeys() {
        return this.hotkeys;
    }

    public HashMap<Byte, PlayerSkillData> getSkills() {
        return this.skills;
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
            ((ItemEquip) dropItemCode).updateStats();
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

        for (byte i = 0; i < this.bonusStats.length; i++) {
            this.bonusStats[i] = 0;
            for (final ItemEquip e : this.equipment.values()) {
                if (i != Globals.STAT_LEVEL && e != null && e.getTotalStats().get(i) != null) {
                    this.bonusStats[i] += e.getTotalStats().get(i);
                }
            }
        }

        double totalStatPoints = 0;
        for (int level = 1; level <= this.baseStats[Globals.STAT_LEVEL]; level++) {
            if (level <= 40) {
                totalStatPoints += 3;
            } else if (level <= 70) {
                totalStatPoints += 4;
            } else if (level <= 90) {
                totalStatPoints += 6;
            } else if (level <= 100) {
                totalStatPoints += 12;
            }
        }

        this.baseStats[Globals.STAT_POINTS] = totalStatPoints
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
        for (final PlayerSkillData s : this.skills.values()) {
            totalSP += s.getLevel();
        }
        this.baseStats[Globals.STAT_SKILLPOINTS] = Globals.SP_PER_LEVEL * this.baseStats[Globals.STAT_LEVEL] - totalSP;

        if (totalSP > Globals.SP_PER_LEVEL * this.baseStats[Globals.STAT_LEVEL]) {
            this.skills.values().forEach((s) -> {
                s.setLevel((byte) 0);
            });
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

    public HashMap<Integer, HashMap<Integer, ItemEquip>> getInventory() {
        return this.inventory;
    }

    public HashMap<Integer, ItemEquip> getInventory(final int type) {
        return this.inventory.get(type);
    }

    public HashMap<Byte, ItemEquip> getEquip() {
        return this.equipment;
    }

    public HashMap<Integer, ItemUpgrade> getUpgrades() {
        return this.upgrades;
    }

    public HashMap<Byte, Integer> getKeyBind() {
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
        for (final PlayerSkillData skill : this.skills.values()) {
            skill.setLevel((byte) 0);
        }
        calcStats();
        writeSaveData(this.saveNum, this);
    }

    public void addSkill(final byte skillCode, final boolean isMax) {
        if (this.skills.get(skillCode).getSkillData().cantLevel() || this.baseStats[Globals.STAT_SKILLPOINTS] <= 0 || this.skills.get(skillCode).getLevel() >= 30) {
            return;
        }
        if (!isMax) {
            if (this.skills.get(skillCode).addLevel((byte) 1)) {
                this.baseStats[Globals.STAT_SKILLPOINTS]--;
            }
        } else {
            double skillPointsRequired = 30 - this.skills.get(skillCode).getLevel();
            double amount = skillPointsRequired;
            if (this.baseStats[Globals.STAT_SKILLPOINTS] < skillPointsRequired) {
                amount = this.baseStats[Globals.STAT_SKILLPOINTS];
            }
            if (this.skills.get(skillCode).addLevel((byte) amount)) {
                this.baseStats[Globals.STAT_SKILLPOINTS] -= amount;
            }
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

    public void unequipItem(final int equipTab, final byte equipSlot) {
        for (int i = 0; i < Globals.MAX_INVENTORY_SLOTS; i++) {
            if (this.inventory.get(equipTab).get(i) == null) {
                this.inventory.get(equipTab).put(i, this.equipment.get(equipSlot));
                this.equipment.remove(equipSlot);
                break;
            }
        }
        calcStats();
        writeSaveData(this.saveNum, this);
    }

    public void equipItem(final int equipTab, byte equipSlot, final int inventorySlot) {
        final ItemEquip itemToEquip = this.inventory.get(equipTab).get(inventorySlot);
        if (itemToEquip != null) {
            if (itemToEquip.getBaseStats().get(Globals.STAT_LEVEL) > this.baseStats[Globals.STAT_LEVEL]) {
                return;
            }
        }
        this.inventory.get(equipTab).put(inventorySlot, this.equipment.get(equipSlot));
        this.equipment.put(equipSlot, itemToEquip);
        calcStats();
        writeSaveData(this.saveNum, this);
    }

    public void destroyItem(final int type, final int slot) {
        this.inventory.get(type).remove(slot);
        writeSaveData(this.saveNum, this);
    }

    public void destroyItem(final int slot) {
        this.upgrades.remove(slot);
        writeSaveData(this.saveNum, this);
    }

    public void destroyAll(final int type) {
        this.inventory.get(type).clear();
        writeSaveData(this.saveNum, this);
    }

    public void destroyAllUpgrade() {
        this.upgrades.clear();
        writeSaveData(this.saveNum, this);
    }

    public void addItem(final ItemEquip e) {
        int equipTab = e.getEquipType();
        if (equipTab == Globals.ITEM_SHIELD || equipTab == Globals.ITEM_ARROW || equipTab == Globals.ITEM_BOW) {
            equipTab = Globals.EQUIP_WEAPON;
        }

        int freeSlot = SaveData.getFirstOpenInventorySlot(inventory.get(equipTab));
        if (freeSlot > -1) {
            this.inventory.get(equipTab).put(freeSlot, e);
        }

        writeSaveData(this.saveNum, this);
    }

    public void addItem(final ItemUpgrade e) {
        int freeSlot = SaveData.getFirstOpenInventorySlot(this.upgrades);
        if (freeSlot > -1) {
            this.upgrades.put(freeSlot, e);
        }
    }

    public void setKeyBind(final byte actionKey, final int keycode) {
        this.keybinds.put(actionKey, keycode);
        ArrayList<Byte> keysToRemove = new ArrayList<>();
        this.keybinds.keySet().forEach(key -> {
            if (key != actionKey && this.keybinds.get(key) == keycode) {
                keysToRemove.add(key);
            }
        });

        keysToRemove.forEach(key -> {
            this.keybinds.remove(key);
        });
    }

    public void setUniqueID(final UUID id) {
        this.uniqueID = id;
    }

    public void sortUpgradeItems() {
        List<Entry<Integer, ItemUpgrade>> list = new ArrayList<>(this.upgrades.entrySet());
        list = list.stream().filter(entry -> entry.getValue() != null).collect(Collectors.toList());
        list.sort(Entry.comparingByValue(Comparator.reverseOrder()));

        int slot = 0;
        this.upgrades.clear();
        for (Entry<Integer, ItemUpgrade> upgrade : list) {
            this.upgrades.put(slot, upgrade.getValue());
            slot++;
        }

        writeSaveData(this.saveNum, this);
    }

    public void validate() {
        for (byte i = 0; i < Globals.NUM_HOTKEYS; i++) {
            if (getHotkeys().get(i) != null && getTotalStats()[Globals.STAT_LEVEL] < getSkills().get(getHotkeys().get(i)).getSkillData().getReqLevel()) {
                getHotkeys().remove(i);
            }
        }
    }

    public void completeSaveDataLoad() {
        this.inventory.forEach((itemTab, equips) -> {
            for (ItemEquip equip : equips.values()) {
                if (equip != null) {
                    equip.updateStats();
                }
            }
        });

        for (ItemEquip equip : this.equipment.values()) {
            if (equip != null) {
                equip.updateStats();
            }
        }

        this.skills.forEach((skillCode, skill) -> {
            PlayerSkillData playerSkillData = new PlayerSkillData();
            playerSkillData.setSkillCode(skillCode);
            playerSkillData.setLevel(skill.getLevel());

            this.skills.put(skillCode, playerSkillData);
        });

        calcStats();
    }

    public void setSaveNum(byte saveNum) {
        this.saveNum = saveNum;
    }

    public String getCharacterName() {
        return characterName;
    }

    private static int getFirstOpenInventorySlot(HashMap<Integer, ? extends Object> inventoryMap) {
        for (int i = 0; i < Globals.MAX_INVENTORY_SLOTS; i++) {
            if (inventoryMap.get(i) == null) {
                return i;
            }
        }
        return -1;
    }
}
