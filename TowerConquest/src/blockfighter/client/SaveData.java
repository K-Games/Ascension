package blockfighter.client;

import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.items.ItemUpgrade;
import blockfighter.client.entities.player.skills.Skill;
import blockfighter.client.entities.player.skills.SkillBowArc;
import blockfighter.client.entities.player.skills.SkillBowFrost;
import blockfighter.client.entities.player.skills.SkillBowPower;
import blockfighter.client.entities.player.skills.SkillBowRapid;
import blockfighter.client.entities.player.skills.SkillBowStorm;
import blockfighter.client.entities.player.skills.SkillBowVolley;
import blockfighter.client.entities.player.skills.SkillPassive12;
import blockfighter.client.entities.player.skills.SkillPassiveBarrier;
import blockfighter.client.entities.player.skills.SkillPassiveBowMastery;
import blockfighter.client.entities.player.skills.SkillPassiveDualSword;
import blockfighter.client.entities.player.skills.SkillPassiveKeenEye;
import blockfighter.client.entities.player.skills.SkillPassiveResistance;
import blockfighter.client.entities.player.skills.SkillPassiveRevive;
import blockfighter.client.entities.player.skills.SkillPassiveShadowAttack;
import blockfighter.client.entities.player.skills.SkillPassiveShieldMastery;
import blockfighter.client.entities.player.skills.SkillPassiveTactical;
import blockfighter.client.entities.player.skills.SkillPassiveVitalHit;
import blockfighter.client.entities.player.skills.SkillPassiveWillpower;
import blockfighter.client.entities.player.skills.SkillShieldCharge;
import blockfighter.client.entities.player.skills.SkillShieldDash;
import blockfighter.client.entities.player.skills.SkillShieldFortify;
import blockfighter.client.entities.player.skills.SkillShieldIron;
import blockfighter.client.entities.player.skills.SkillShieldReflect;
import blockfighter.client.entities.player.skills.SkillShieldToss;
import blockfighter.client.entities.player.skills.SkillSwordCinder;
import blockfighter.client.entities.player.skills.SkillSwordDrive;
import blockfighter.client.entities.player.skills.SkillSwordMulti;
import blockfighter.client.entities.player.skills.SkillSwordSlash;
import blockfighter.client.entities.player.skills.SkillSwordTaunt;
import blockfighter.client.entities.player.skills.SkillSwordVorpal;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Ken Kwan
 */
public class SaveData {

    private double[] baseStats = new double[Globals.NUM_STATS],
            totalStats = new double[Globals.NUM_STATS],
            bonusStats = new double[Globals.NUM_STATS];

    private int uniqueID;
    private String name;
    private byte saveNum;

    private ItemEquip[][] inventory = new ItemEquip[Globals.NUM_ITEM_TABS][100];
    private ItemUpgrade[] upgrades = new ItemUpgrade[100];
    private ItemEquip[] equipment = new ItemEquip[Globals.NUM_EQUIP_SLOTS];

    private Skill[] hotkeys = new Skill[12];
    private Skill[] skills = new Skill[Skill.NUM_SKILLS];
    private int[] keybinds = new int[Globals.NUM_KEYBINDS];

    public SaveData(String n, byte sn) {
        saveNum = sn;
        name = n;
        uniqueID = Globals.rng(Integer.MAX_VALUE);
        //initalize skill list
        skills[Skill.SWORD_CINDER] = new SkillSwordCinder();
        skills[Skill.SWORD_DRIVE] = new SkillSwordDrive();
        skills[Skill.SWORD_MULTI] = new SkillSwordMulti();
        skills[Skill.SWORD_SLASH] = new SkillSwordSlash();
        skills[Skill.SWORD_TAUNT] = new SkillSwordTaunt();
        skills[Skill.SWORD_VORPAL] = new SkillSwordVorpal();

        skills[Skill.BOW_ARC] = new SkillBowArc();
        skills[Skill.BOW_FROST] = new SkillBowFrost();
        skills[Skill.BOW_POWER] = new SkillBowPower();
        skills[Skill.BOW_RAPID] = new SkillBowRapid();
        skills[Skill.BOW_STORM] = new SkillBowStorm();
        skills[Skill.BOW_VOLLEY] = new SkillBowVolley();

        skills[Skill.SHIELD_FORTIFY] = new SkillShieldFortify();
        skills[Skill.SHIELD_IRON] = new SkillShieldIron();
        skills[Skill.SHIELD_CHARGE] = new SkillShieldCharge();
        skills[Skill.SHIELD_REFLECT] = new SkillShieldReflect();
        skills[Skill.SHIELD_TOSS] = new SkillShieldToss();
        skills[Skill.SHIELD_DASH] = new SkillShieldDash();

        skills[Skill.PASSIVE_DUALSWORD] = new SkillPassiveDualSword();
        skills[Skill.PASSIVE_KEENEYE] = new SkillPassiveKeenEye();
        skills[Skill.PASSIVE_VITALHIT] = new SkillPassiveVitalHit();
        skills[Skill.PASSIVE_SHIELDMASTERY] = new SkillPassiveShieldMastery();
        skills[Skill.PASSIVE_BARRIER] = new SkillPassiveBarrier();
        skills[Skill.PASSIVE_RESIST] = new SkillPassiveResistance();
        skills[Skill.PASSIVE_BOWMASTERY] = new SkillPassiveBowMastery();
        skills[Skill.PASSIVE_WILLPOWER] = new SkillPassiveWillpower();
        skills[Skill.PASSIVE_TACTICAL] = new SkillPassiveTactical();
        skills[Skill.PASSIVE_REVIVE] = new SkillPassiveRevive();
        skills[Skill.PASSIVE_SHADOWATTACK] = new SkillPassiveShadowAttack();
        skills[Skill.PASSIVE_12] = new SkillPassive12();
    }

    public void newCharacter() {
        //Set level 1
        baseStats[Globals.STAT_LEVEL] = 1;
        baseStats[Globals.STAT_POWER] = 0;
        baseStats[Globals.STAT_DEFENSE] = 0;
        baseStats[Globals.STAT_SPIRIT] = 0;
        baseStats[Globals.STAT_EXP] = 0;
        baseStats[Globals.STAT_SKILLPOINTS] = 3 * baseStats[Globals.STAT_LEVEL];
        //for (int i = 0; i < upgrades.length; i++) {
        //upgrades[i] = new ItemUpgrade(1, (int) baseStats[Globals.STAT_LEVEL] + 1);
        //}
        //Empty inventory
        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = new ItemEquip[100];
        }

        for (int itemCode : ItemEquip.ITEM_CODES) {
            ItemEquip startEq = new ItemEquip(itemCode, baseStats[Globals.STAT_LEVEL]);
            addItem(startEq);
        }

        keybinds[Globals.KEYBIND_SKILL1] = KeyEvent.VK_Q;
        keybinds[Globals.KEYBIND_SKILL2] = KeyEvent.VK_W;
        keybinds[Globals.KEYBIND_SKILL3] = KeyEvent.VK_E;
        keybinds[Globals.KEYBIND_SKILL4] = KeyEvent.VK_R;
        keybinds[Globals.KEYBIND_SKILL5] = KeyEvent.VK_T;
        keybinds[Globals.KEYBIND_SKILL6] = KeyEvent.VK_Y;
        keybinds[Globals.KEYBIND_SKILL7] = KeyEvent.VK_A;
        keybinds[Globals.KEYBIND_SKILL8] = KeyEvent.VK_S;
        keybinds[Globals.KEYBIND_SKILL9] = KeyEvent.VK_D;
        keybinds[Globals.KEYBIND_SKILL10] = KeyEvent.VK_F;
        keybinds[Globals.KEYBIND_SKILL11] = KeyEvent.VK_G;
        keybinds[Globals.KEYBIND_SKILL12] = KeyEvent.VK_H;

        keybinds[Globals.KEYBIND_LEFT] = KeyEvent.VK_LEFT;
        keybinds[Globals.KEYBIND_RIGHT] = KeyEvent.VK_RIGHT;
        keybinds[Globals.KEYBIND_JUMP] = KeyEvent.VK_SPACE;
        keybinds[Globals.KEYBIND_DOWN] = KeyEvent.VK_DOWN;
    }

    public static void saveData(byte saveNum, SaveData c) {
        byte[] data = new byte[46413];
        byte[] temp = c.name.getBytes(StandardCharsets.UTF_8);

        int pos = 0;
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += Globals.MAX_NAME_LENGTH;

        temp = Globals.intToByte(c.uniqueID);
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += temp.length;

        int[] statIDs = {Globals.STAT_LEVEL,
            Globals.STAT_POWER,
            Globals.STAT_DEFENSE,
            Globals.STAT_SPIRIT,
            Globals.STAT_SKILLPOINTS};

        for (int i : statIDs) {
            temp = Globals.intToByte((int) c.baseStats[i]);
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
        }

        pos = saveItems(data, c.equipment, pos);
        for (ItemEquip[] e : c.inventory) {
            pos = saveItems(data, e, pos);
        }
        pos = saveItems(data, c.upgrades, pos);
        pos = saveSkills(data, c, pos);
        pos = saveHotkeys(data, c, pos);
        pos = saveKeyBind(data, c.getKeyBind(), pos);

        temp = Globals.intToByte((int) c.baseStats[Globals.STAT_EXP]);
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += temp.length;

        try {
            FileUtils.writeByteArrayToFile(new File(saveNum + ".tcdat"), data);
        } catch (IOException ex) {
            Logger.getLogger(SaveData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static int saveKeyBind(byte[] data, int[] keybind, int pos) {
        for (int i = 0; i < keybind.length; i++) {
            byte[] temp;
            temp = Globals.intToByte(keybind[i]);
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
        }
        return pos;
    }

    private static int saveItems(byte[] data, ItemUpgrade[] e, int pos) {
        for (ItemUpgrade item : e) {
            if (item == null) {
                pos += 2 * 4;
                continue;
            }
            byte[] temp;

            temp = Globals.intToByte(item.getItemCode());
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;

            temp = Globals.intToByte((int) item.getLevel());
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
        }
        return pos;
    }

    private static int saveSkills(byte[] data, SaveData c, int pos) {
        for (int i = 0; i < Skill.NUM_SKILLS; i++) {
            data[pos] = c.getSkills()[i].getLevel();
            pos += 1;
        }
        return pos;
    }

    private static int saveHotkeys(byte[] data, SaveData c, int pos) {
        for (Skill hotkey : c.getHotkeys()) {
            if (hotkey == null) {
                data[pos] = -1;
            } else {
                data[pos] = hotkey.getSkillCode();
            }
            pos += 1;
        }
        return pos;
    }

    private static int saveItems(byte[] data, ItemEquip[] e, int pos) {
        for (ItemEquip item : e) {
            if (item == null) {
                pos += 11 * 4;
                continue;
            }
            byte[] temp;
            temp = Globals.intToByte(item.getItemCode());
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;

            int[] statIDs = {Globals.STAT_LEVEL,
                Globals.STAT_POWER,
                Globals.STAT_DEFENSE,
                Globals.STAT_SPIRIT,
                Globals.STAT_ARMOR,
                Globals.STAT_REGEN,
                Globals.STAT_CRITDMG,
                Globals.STAT_CRITCHANCE};

            for (int i : statIDs) {
                switch (i) {
                    case Globals.STAT_REGEN:
                        temp = Globals.intToByte((int) (item.getBaseStats()[i] * 10));
                        break;
                    case Globals.STAT_CRITDMG:
                        temp = Globals.intToByte((int) (item.getBaseStats()[i] * 10000));
                        break;
                    case Globals.STAT_CRITCHANCE:
                        temp = Globals.intToByte((int) (item.getBaseStats()[i] * 10000));
                        break;
                    default:
                        temp = Globals.intToByte((int) item.getBaseStats()[i]);
                }
                System.arraycopy(temp, 0, data, pos, temp.length);
                pos += temp.length;
            }

            temp = Globals.intToByte(item.getUpgrades());
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;

            temp = Globals.intToByte((int) (item.getBonusMult() * 100));
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
        }
        return pos;
    }

    public static SaveData readData(byte saveNum) {
        SaveData c = new SaveData("", saveNum);
        byte[] data, temp = new byte[Globals.MAX_NAME_LENGTH];

        try {
            data = FileUtils.readFileToByteArray(new File(saveNum + ".tcdat"));
        } catch (IOException ex) {
            return null;
        }

        int pos = 0;
        System.arraycopy(data, pos, temp, 0, temp.length);
        c.name = new String(temp, StandardCharsets.UTF_8).trim();
        pos += Globals.MAX_NAME_LENGTH;

        temp = new byte[4];
        System.arraycopy(data, pos, temp, 0, temp.length);
        c.uniqueID = Globals.bytesToInt(temp);
        pos += temp.length;

        int[] statIDs = {Globals.STAT_LEVEL,
            Globals.STAT_POWER,
            Globals.STAT_DEFENSE,
            Globals.STAT_SPIRIT,
            Globals.STAT_SKILLPOINTS};

        for (int i : statIDs) {
            System.arraycopy(data, pos, temp, 0, temp.length);
            c.baseStats[i] = Globals.bytesToInt(temp);
            pos += temp.length;
        }

        pos = readItems(data, c.equipment, pos);
        for (ItemEquip[] e : c.inventory) {
            pos = readItems(data, e, pos);
        }
        pos = readItems(data, c.upgrades, pos);
        pos = readSkills(data, c, pos);
        pos = readHotkeys(data, c, pos);
        pos = readKeyBind(data, c.getKeyBind(), pos);

        System.arraycopy(data, pos, temp, 0, temp.length);
        c.baseStats[Globals.STAT_EXP] = Globals.bytesToInt(temp);
        pos += temp.length;

        c.calcStats();
        return c;
    }

    private static int readKeyBind(byte[] data, int[] keybind, int pos) {
        for (int i = 0; i < keybind.length; i++) {
            byte[] temp = new byte[4];
            System.arraycopy(data, pos, temp, 0, temp.length);
            keybind[i] = Globals.bytesToInt(temp);
            pos += temp.length;
        }
        return pos;
    }

    private static int readSkills(byte[] data, SaveData c, int pos) {
        for (int i = 0; i < Skill.NUM_SKILLS; i++) {
            c.getSkills()[i].setLevel(data[pos]);
            pos += 1;
        }
        return pos;
    }

    private static int readHotkeys(byte[] data, SaveData c, int pos) {
        Skill[] e = c.getHotkeys();
        for (int i = 0; i < e.length; i++) {
            byte skillCode = data[pos];
            if (skillCode != -1) {
                e[i] = c.getSkills()[skillCode];
            }
            pos += 1;
        }
        return pos;
    }

    private static int readItems(byte[] data, ItemUpgrade[] e, int pos) {
        for (int i = 0; i < e.length; i++) {
            byte[] temp = new byte[4];
            int itemCode;
            int level;

            System.arraycopy(data, pos, temp, 0, temp.length);
            itemCode = Globals.bytesToInt(temp);
            pos += temp.length;

            System.arraycopy(data, pos, temp, 0, temp.length);
            level = Globals.bytesToInt(temp);
            pos += temp.length;

            if (!ItemUpgrade.isValidItem(itemCode)) {
                e[i] = null;
            } else {
                e[i] = new ItemUpgrade(itemCode, level);
            }
        }
        return pos;
    }

    private static int readItems(byte[] data, ItemEquip[] e, int pos) {
        for (int i = 0; i < e.length; i++) {
            double[] bs = new double[Globals.NUM_STATS];
            byte[] temp = new byte[4];
            int itemCode;
            int upgrades;
            double bMult;

            System.arraycopy(data, pos, temp, 0, temp.length);
            itemCode = Globals.bytesToInt(temp);
            pos += temp.length;
            int[] statIDs = {Globals.STAT_LEVEL,
                Globals.STAT_POWER,
                Globals.STAT_DEFENSE,
                Globals.STAT_SPIRIT,
                Globals.STAT_ARMOR,
                Globals.STAT_REGEN,
                Globals.STAT_CRITDMG,
                Globals.STAT_CRITCHANCE};

            for (int s : statIDs) {
                System.arraycopy(data, pos, temp, 0, temp.length);
                switch (s) {
                    case Globals.STAT_REGEN:
                        bs[s] = Globals.bytesToInt(temp) / 10D;
                        break;
                    case Globals.STAT_CRITDMG:
                        bs[s] = Globals.bytesToInt(temp) / 10000D;
                        break;
                    case Globals.STAT_CRITCHANCE:
                        bs[s] = Globals.bytesToInt(temp) / 10000D;
                        break;
                    default:
                        bs[s] = Globals.bytesToInt(temp);
                }
                pos += temp.length;
            }

            System.arraycopy(data, pos, temp, 0, temp.length);
            upgrades = Globals.bytesToInt(temp);
            pos += temp.length;

            System.arraycopy(data, pos, temp, 0, temp.length);
            bMult = Globals.bytesToInt(temp) / 100D;
            pos += temp.length;

            if (!ItemEquip.isValidItem(itemCode)) {
                e[i] = null;
            } else {
                e[i] = new ItemEquip(bs, upgrades, bMult, itemCode);
            }

        }
        return pos;
    }

    public Skill[] getHotkeys() {
        return hotkeys;
    }

    public Skill[] getSkills() {
        return skills;
    }

    public String getPlayerName() {
        return name;
    }

    public double[] getBaseStats() {
        return baseStats;
    }

    public double[] getTotalStats() {
        return totalStats;
    }

    public int getUniqueID() {
        return uniqueID;
    }

    public void addDrops(int lvl) {
        for (int i = 1; i <= 3; i++) {
            if (Globals.rng(100) < 30 * i) {
                addItem(new ItemUpgrade(1, lvl + Globals.rng(6)));
            }
        }
        for (int itemCode : ItemEquip.ITEM_CODES) {
            if (Globals.rng(100) < 50) {
                ItemEquip e = new ItemEquip(itemCode, lvl, Globals.rng(100) < 20);
                addItem(e);
            }
        }
    }

    public void addExp(double amount) {
        baseStats[Globals.STAT_EXP] += amount;
        if (baseStats[Globals.STAT_LEVEL] >= 100) {
            if (baseStats[Globals.STAT_EXP] >= Globals.calcEXPtoNxtLvl(baseStats[Globals.STAT_LEVEL])) {
                baseStats[Globals.STAT_EXP] = Globals.calcEXPtoNxtLvl(baseStats[Globals.STAT_LEVEL]);
            }
            return;
        }
        while (baseStats[Globals.STAT_EXP] >= Globals.calcEXPtoNxtLvl(baseStats[Globals.STAT_LEVEL])) {
            levelUp();
        }
    }

    public void levelUp() {
        baseStats[Globals.STAT_EXP] -= Globals.calcEXPtoNxtLvl(baseStats[Globals.STAT_LEVEL]);
        baseStats[Globals.STAT_LEVEL] += 1;
        calcStats();
        saveData(saveNum, this);
    }

    public void calcStats() {

        if (baseStats[Globals.STAT_LEVEL] > 100) {
            baseStats[Globals.STAT_LEVEL] = 100;
        }

        for (int i = 0; i < bonusStats.length; i++) {
            bonusStats[i] = 0;
            for (ItemEquip e : equipment) {
                if (i != Globals.STAT_LEVEL && e != null) {
                    bonusStats[i] += e.getTotalStats()[i];
                }
            }
        }

        baseStats[Globals.STAT_POINTS] = baseStats[Globals.STAT_LEVEL] * Globals.STAT_PER_LEVEL
                - (baseStats[Globals.STAT_POWER]
                + baseStats[Globals.STAT_DEFENSE]
                + baseStats[Globals.STAT_SPIRIT]);

        if (baseStats[Globals.STAT_POWER]
                + baseStats[Globals.STAT_DEFENSE]
                + baseStats[Globals.STAT_SPIRIT] > baseStats[Globals.STAT_LEVEL] * Globals.STAT_PER_LEVEL) {
            baseStats[Globals.STAT_POINTS] = baseStats[Globals.STAT_LEVEL] * Globals.STAT_PER_LEVEL;
            baseStats[Globals.STAT_POWER] = 0;
            baseStats[Globals.STAT_DEFENSE] = 0;
            baseStats[Globals.STAT_SPIRIT] = 0;
        }

        int totalSP = 0;
        for (Skill s : skills) {
            totalSP += s.getLevel();
        }
        baseStats[Globals.STAT_SKILLPOINTS] = Globals.SP_PER_LEVEL * baseStats[Globals.STAT_LEVEL] - totalSP;

        if (totalSP > Globals.SP_PER_LEVEL * baseStats[Globals.STAT_LEVEL]) {
            for (Skill s : skills) {
                s.setLevel((byte) 0);
            }
            baseStats[Globals.STAT_SKILLPOINTS] = Globals.SP_PER_LEVEL * baseStats[Globals.STAT_LEVEL];
        }

        System.arraycopy(baseStats, 0, totalStats, 0, baseStats.length);

        totalStats[Globals.STAT_POWER] = (int) (baseStats[Globals.STAT_POWER] + bonusStats[Globals.STAT_POWER]);
        totalStats[Globals.STAT_DEFENSE] = (int) (baseStats[Globals.STAT_DEFENSE] + bonusStats[Globals.STAT_DEFENSE]);
        totalStats[Globals.STAT_SPIRIT] = (int) (baseStats[Globals.STAT_SPIRIT] + bonusStats[Globals.STAT_SPIRIT]);

        totalStats[Globals.STAT_MAXHP] = Globals.calcMaxHP(totalStats[Globals.STAT_DEFENSE]);
        totalStats[Globals.STAT_MINHP] = baseStats[Globals.STAT_MAXHP];

        totalStats[Globals.STAT_MINDMG] = Globals.calcMinDmg(totalStats[Globals.STAT_POWER]);
        totalStats[Globals.STAT_MAXDMG] = Globals.calcMaxDmg(totalStats[Globals.STAT_POWER]);

        baseStats[Globals.STAT_ARMOR] = Globals.calcArmor(totalStats[Globals.STAT_DEFENSE]);
        baseStats[Globals.STAT_REGEN] = Globals.calcRegen(totalStats[Globals.STAT_SPIRIT]);
        baseStats[Globals.STAT_CRITCHANCE] = Globals.calcCritChance(totalStats[Globals.STAT_SPIRIT]);
        baseStats[Globals.STAT_CRITDMG] = Globals.calcCritDmg(totalStats[Globals.STAT_SPIRIT]);

        totalStats[Globals.STAT_ARMOR] = baseStats[Globals.STAT_ARMOR] + bonusStats[Globals.STAT_ARMOR];
        totalStats[Globals.STAT_REGEN] = baseStats[Globals.STAT_REGEN] + bonusStats[Globals.STAT_REGEN];
        totalStats[Globals.STAT_CRITCHANCE] = baseStats[Globals.STAT_CRITCHANCE] + bonusStats[Globals.STAT_CRITCHANCE];
        totalStats[Globals.STAT_CRITDMG] = baseStats[Globals.STAT_CRITDMG] + bonusStats[Globals.STAT_CRITDMG];
        totalStats[Globals.STAT_DAMAGEREDUCT] = 1 - Globals.calcReduction(totalStats[Globals.STAT_ARMOR]);
    }

    public ItemEquip[] getInventory(byte type) {
        return inventory[type];
    }

    public ItemEquip[] getEquip() {
        return equipment;
    }

    public ItemUpgrade[] getUpgrades() {
        return upgrades;
    }

    public int[] getKeyBind() {
        return keybinds;
    }

    public double[] getBonusStats() {
        return bonusStats;
    }

    public byte getSaveNum() {
        return saveNum;
    }

    public void resetStat() {
        baseStats[Globals.STAT_POWER] = 0;
        baseStats[Globals.STAT_DEFENSE] = 0;
        baseStats[Globals.STAT_SPIRIT] = 0;
        calcStats();
        saveData(saveNum, this);
    }

    public void resetSkill() {
        for (Skill skill : skills) {
            skill.setLevel((byte) 0);
        }
        calcStats();
        saveData(saveNum, this);
    }

    public void addSkill(byte skillCode) {
        if (baseStats[Globals.STAT_SKILLPOINTS] <= 0 || skills[skillCode].getLevel() >= 30) {
            return;
        }
        baseStats[Globals.STAT_SKILLPOINTS]--;
        skills[skillCode].addLevel((byte) 1);

        saveData(saveNum, this);
    }

    public void addStat(byte stat, int amount) {
        if (baseStats[Globals.STAT_POINTS] < amount) {
            return;
        }
        baseStats[Globals.STAT_POINTS] -= amount;
        baseStats[stat] += amount;

        calcStats();
        saveData(saveNum, this);
    }

    public void unequipItem(byte slot) {
        byte itemType = slot;
        if (slot == Globals.ITEM_OFFHAND) {
            itemType = Globals.ITEM_WEAPON;
        }

        for (int i = 0; i < inventory[itemType].length; i++) {
            if (inventory[itemType][i] == null) {
                inventory[itemType][i] = equipment[slot];
                equipment[slot] = null;
                break;
            }
        }
        calcStats();
        saveData(saveNum, this);
    }

    public void equipItem(int slot, int inventorySlot) {
        int itemType = slot;
        if (slot == Globals.ITEM_OFFHAND) {
            itemType = Globals.ITEM_WEAPON;
        }

        ItemEquip temp = inventory[itemType][inventorySlot];
        if (temp != null) {
            if (temp.getBaseStats()[Globals.STAT_LEVEL] > baseStats[Globals.STAT_LEVEL]) {
                return;
            }
            switch (ItemEquip.getItemType(temp.getItemCode())) {
                case Globals.ITEM_SHIELD:
                    slot = Globals.ITEM_OFFHAND;
                    break;
                case Globals.ITEM_BOW:
                    slot = Globals.ITEM_WEAPON;
                    break;
                case Globals.ITEM_QUIVER:
                    slot = Globals.ITEM_OFFHAND;
                    break;
            }
        }
        inventory[itemType][inventorySlot] = equipment[slot];
        equipment[slot] = temp;
        calcStats();
        saveData(saveNum, this);
    }

    public void destroyItem(int type, int slot) {
        inventory[type][slot] = null;
        saveData(saveNum, this);
    }

    public void destroyItem(int slot) {
        upgrades[slot] = null;
        saveData(saveNum, this);
    }

    public void destroyAll(int type) {
        for (int i = 0; i < inventory[type].length; i++) {
            inventory[type][i] = null;
        }
        saveData(saveNum, this);
    }

    public void destroyAllUpgrade() {
        for (int i = 0; i < upgrades.length; i++) {
            upgrades[i] = null;
        }
        saveData(saveNum, this);
    }

    public void addItem(ItemEquip e) {
        int tab = ItemEquip.getItemType(e.getItemCode());
        if (tab == Globals.ITEM_SHIELD || tab == Globals.ITEM_QUIVER || tab == Globals.ITEM_BOW) {
            tab = Globals.ITEM_WEAPON;
        }
        for (int i = 0; i < inventory[tab].length; i++) {
            if (inventory[tab][i] == null) {
                inventory[tab][i] = e;
                break;
            }
        }
        saveData(saveNum, this);
    }

    public void addItem(ItemUpgrade e) {
        for (int i = 0; i < upgrades.length; i++) {
            if (upgrades[i] == null) {
                upgrades[i] = e;
                break;
            }
        }
        saveData(saveNum, this);
    }

    public void setKeyBind(int k, int keycode) {
        keybinds[k] = keycode;
        for (int i = 0; i < keybinds.length; i++) {
            if (i != k && keybinds[i] == keycode) {
                keybinds[i] = -1;
            }
        }
    }
}
