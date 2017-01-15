package blockfighter.client;

import blockfighter.client.entities.items.Item;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.items.ItemUpgrade;
import blockfighter.client.entities.player.skills.Skill;
import blockfighter.client.entities.player.skills.SkillBowArc;
import blockfighter.client.entities.player.skills.SkillBowFrost;
import blockfighter.client.entities.player.skills.SkillBowPower;
import blockfighter.client.entities.player.skills.SkillBowRapid;
import blockfighter.client.entities.player.skills.SkillBowStorm;
import blockfighter.client.entities.player.skills.SkillBowVolley;
import blockfighter.client.entities.player.skills.SkillPassiveBarrier;
import blockfighter.client.entities.player.skills.SkillPassiveBowMastery;
import blockfighter.client.entities.player.skills.SkillPassiveDualSword;
import blockfighter.client.entities.player.skills.SkillPassiveHarmony;
import blockfighter.client.entities.player.skills.SkillPassiveKeenEye;
import blockfighter.client.entities.player.skills.SkillPassiveResistance;
import blockfighter.client.entities.player.skills.SkillPassiveShadowAttack;
import blockfighter.client.entities.player.skills.SkillPassiveShieldMastery;
import blockfighter.client.entities.player.skills.SkillPassiveStatic;
import blockfighter.client.entities.player.skills.SkillPassiveTough;
import blockfighter.client.entities.player.skills.SkillPassiveVitalHit;
import blockfighter.client.entities.player.skills.SkillPassiveWillpower;
import blockfighter.client.entities.player.skills.SkillShieldCharge;
import blockfighter.client.entities.player.skills.SkillShieldMagnetize;
import blockfighter.client.entities.player.skills.SkillShieldReflect;
import blockfighter.client.entities.player.skills.SkillShieldRoar;
import blockfighter.client.entities.player.skills.SkillSwordCinder;
import blockfighter.client.entities.player.skills.SkillSwordGash;
import blockfighter.client.entities.player.skills.SkillSwordPhantom;
import blockfighter.client.entities.player.skills.SkillSwordSlash;
import blockfighter.client.entities.player.skills.SkillSwordTaunt;
import blockfighter.client.entities.player.skills.SkillSwordVorpal;
import blockfighter.client.entities.player.skills.SkillUtilityDash;
import blockfighter.client.entities.player.skills.SkillUtilityFortify;
import blockfighter.shared.Globals;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.apache.commons.io.FileUtils;

public class SaveData {

    private final double[] baseStats = new double[Globals.NUM_STATS],
            totalStats = new double[Globals.NUM_STATS],
            bonusStats = new double[Globals.NUM_STATS];

    private UUID uniqueID;
    private String name;
    private final byte saveNum;

    private final ItemEquip[][] inventory = new ItemEquip[Globals.NUM_ITEM_TABS][100];
    private final ItemUpgrade[] upgrades = new ItemUpgrade[100];
    private final ItemEquip[] equipment = new ItemEquip[Globals.NUM_EQUIP_SLOTS];

    private final Skill[] hotkeys = new Skill[12];
    private final Skill[] skills = new Skill[Globals.NUM_SKILLS];
    private final int[] keybinds = new int[Globals.NUM_KEYBINDS];

    public SaveData(final String n, final byte saveNumber) {
        this.saveNum = saveNumber;
        this.name = n;
        this.uniqueID = UUID.randomUUID();
        // initalize skill list
        this.skills[Globals.SWORD_CINDER] = new SkillSwordCinder();
        this.skills[Globals.SWORD_GASH] = new SkillSwordGash();
        this.skills[Globals.SWORD_PHANTOM] = new SkillSwordPhantom();
        this.skills[Globals.SWORD_SLASH] = new SkillSwordSlash();
        this.skills[Globals.SWORD_TAUNT] = new SkillSwordTaunt();
        this.skills[Globals.SWORD_VORPAL] = new SkillSwordVorpal();

        this.skills[Globals.BOW_ARC] = new SkillBowArc();
        this.skills[Globals.BOW_FROST] = new SkillBowFrost();
        this.skills[Globals.BOW_POWER] = new SkillBowPower();
        this.skills[Globals.BOW_RAPID] = new SkillBowRapid();
        this.skills[Globals.BOW_STORM] = new SkillBowStorm();
        this.skills[Globals.BOW_VOLLEY] = new SkillBowVolley();

        this.skills[Globals.UTILITY_FORTIFY] = new SkillUtilityFortify();
        this.skills[Globals.SHIELD_ROAR] = new SkillShieldRoar();
        this.skills[Globals.SHIELD_CHARGE] = new SkillShieldCharge();
        this.skills[Globals.SHIELD_REFLECT] = new SkillShieldReflect();
        this.skills[Globals.SHIELD_MAGNETIZE] = new SkillShieldMagnetize();
        this.skills[Globals.UTILITY_DASH] = new SkillUtilityDash();

        this.skills[Globals.PASSIVE_DUALSWORD] = new SkillPassiveDualSword();
        this.skills[Globals.PASSIVE_KEENEYE] = new SkillPassiveKeenEye();
        this.skills[Globals.PASSIVE_VITALHIT] = new SkillPassiveVitalHit();
        this.skills[Globals.PASSIVE_SHIELDMASTERY] = new SkillPassiveShieldMastery();
        this.skills[Globals.PASSIVE_BARRIER] = new SkillPassiveBarrier();
        this.skills[Globals.PASSIVE_RESIST] = new SkillPassiveResistance();
        this.skills[Globals.PASSIVE_BOWMASTERY] = new SkillPassiveBowMastery();
        this.skills[Globals.PASSIVE_WILLPOWER] = new SkillPassiveWillpower();
        this.skills[Globals.PASSIVE_HARMONY] = new SkillPassiveHarmony();
        this.skills[Globals.PASSIVE_TOUGH] = new SkillPassiveTough();
        this.skills[Globals.PASSIVE_SHADOWATTACK] = new SkillPassiveShadowAttack();
        this.skills[Globals.PASSIVE_STATIC] = new SkillPassiveStatic();
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

        for (final int itemCode : Globals.ITEM_CODES) {
            final ItemEquip startEq = new ItemEquip(itemCode, this.baseStats[Globals.STAT_LEVEL], Globals.TEST_MAX_LEVEL);
            addItem(startEq);
        }

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
    }

    public static void saveData(final byte saveNum, final SaveData c) {
        final byte[] data = new byte[Globals.MAX_NAME_LENGTH //Name in UTF-8 Character
                + Long.BYTES * 2 //UUID
                + Integer.BYTES * 5 //Main stats
                + Integer.BYTES * c.equipment.length * 11
                + Integer.BYTES * c.inventory.length * c.inventory[0].length * 11
                + Integer.BYTES * c.upgrades.length * 2
                + Byte.BYTES * c.skills.length
                + Byte.BYTES * c.hotkeys.length
                + Integer.BYTES * 16 //Base Keybinds
                + Integer.BYTES //EXP
                + Integer.BYTES * Globals.NUM_EMOTES];

        byte[] temp = c.name.getBytes(StandardCharsets.UTF_8);

        int pos = 0;
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += Globals.MAX_NAME_LENGTH;

        temp = Globals.longToBytes(c.getUniqueID().getLeastSignificantBits());
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += temp.length;

        temp = Globals.longToBytes(c.getUniqueID().getMostSignificantBits());
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += temp.length;

        final int[] statIDs = {Globals.STAT_LEVEL,
            Globals.STAT_POWER,
            Globals.STAT_DEFENSE,
            Globals.STAT_SPIRIT,
            Globals.STAT_SKILLPOINTS};

        for (final int i : statIDs) {
            temp = Globals.intToBytes((int) c.baseStats[i]);
            System.arraycopy(temp, 0, data, pos, temp.length);
            pos += temp.length;
        }

        pos = saveItems(data, c.equipment, pos);
        for (final ItemEquip[] e : c.inventory) {
            pos = saveItems(data, e, pos);
        }
        pos = saveItems(data, c.upgrades, pos);
        pos = saveSkills(data, c, pos);
        pos = saveHotkeys(data, c, pos);
        pos = saveKeyBind(data, c.getKeyBind(), pos);

        temp = Globals.intToBytes((int) c.baseStats[Globals.STAT_EXP]);
        System.arraycopy(temp, 0, data, pos, temp.length);
        pos += temp.length;

        pos = saveEmoteKeyBind(data, c.getKeyBind(), pos);

        try {
            FileUtils.writeByteArrayToFile(new File(saveNum + ".tcdat"), data);
        } catch (final IOException ex) {
            Globals.logError(ex.toString(), ex);
        }
    }

    private static int saveKeyBind(final byte[] data, final int[] keybind, final int pos) {
        int nextPos = pos;
        for (int i = 0; i < 16; i++) {
            byte[] temp;
            temp = Globals.intToBytes(keybind[i]);
            System.arraycopy(temp, 0, data, nextPos, temp.length);
            nextPos += temp.length;
        }
        return nextPos;
    }

    private static int saveEmoteKeyBind(final byte[] data, final int[] keybind, final int pos) {
        int nextPos = pos;
        for (int i = 16; i < 16 + Globals.NUM_EMOTES; i++) {
            byte[] temp;
            temp = Globals.intToBytes(keybind[i]);
            System.arraycopy(temp, 0, data, nextPos, temp.length);
            nextPos += temp.length;
        }
        return nextPos;
    }

    private static int saveItems(final byte[] data, final ItemUpgrade[] e, final int pos) {
        int nextPos = pos;
        for (final ItemUpgrade item : e) {
            if (item == null) {
                nextPos += 2 * 4;
                continue;
            }
            byte[] temp;

            temp = Globals.intToBytes(item.getItemCode());
            System.arraycopy(temp, 0, data, nextPos, temp.length);
            nextPos += temp.length;

            temp = Globals.intToBytes(item.getLevel());
            System.arraycopy(temp, 0, data, nextPos, temp.length);
            nextPos += temp.length;
        }
        return nextPos;
    }

    private static int saveSkills(final byte[] data, final SaveData c, final int pos) {
        int nextPos = pos;
        for (int i = 0; i < Globals.NUM_SKILLS; i++) {
            data[nextPos] = c.getSkills()[i].getLevel();
            nextPos += 1;
        }
        return nextPos;
    }

    private static int saveHotkeys(final byte[] data, final SaveData c, final int pos) {
        int nextPos = pos;
        for (final Skill hotkey : c.getHotkeys()) {
            if (hotkey == null) {
                data[nextPos] = -1;
            } else {
                data[nextPos] = hotkey.getSkillCode();
            }
            nextPos += 1;
        }
        return nextPos;
    }

    private static int saveItems(final byte[] data, final ItemEquip[] e, final int pos) {
        int nextPos = pos;
        for (final ItemEquip item : e) {
            if (item == null) {
                nextPos += 11 * 4;
                continue;
            }
            byte[] temp;
            temp = Globals.intToBytes(item.getItemCode());
            System.arraycopy(temp, 0, data, nextPos, temp.length);
            nextPos += temp.length;

            final int[] statIDs = {Globals.STAT_LEVEL,
                Globals.STAT_POWER,
                Globals.STAT_DEFENSE,
                Globals.STAT_SPIRIT,
                Globals.STAT_ARMOR,
                Globals.STAT_REGEN,
                Globals.STAT_CRITDMG,
                Globals.STAT_CRITCHANCE};

            for (final int i : statIDs) {
                switch (i) {
                    case Globals.STAT_REGEN:
                        temp = Globals.intToBytes((int) (item.getBaseStats()[i] * 10));
                        break;
                    case Globals.STAT_CRITDMG:
                        temp = Globals.intToBytes((int) (item.getBaseStats()[i] * 10000));
                        break;
                    case Globals.STAT_CRITCHANCE:
                        temp = Globals.intToBytes((int) (item.getBaseStats()[i] * 10000));
                        break;
                    default:
                        temp = Globals.intToBytes((int) item.getBaseStats()[i]);
                }
                System.arraycopy(temp, 0, data, nextPos, temp.length);
                nextPos += temp.length;
            }

            temp = Globals.intToBytes(item.getUpgrades());
            System.arraycopy(temp, 0, data, nextPos, temp.length);
            nextPos += temp.length;

            temp = Globals.intToBytes((int) (item.getBonusMult() * 100));
            System.arraycopy(temp, 0, data, nextPos, temp.length);
            nextPos += temp.length;
        }
        return nextPos;
    }

    public static SaveData readData(final byte saveNum) {
        final SaveData c = new SaveData("", saveNum);
        byte[] data, temp = new byte[Globals.MAX_NAME_LENGTH];

        try {
            data = FileUtils.readFileToByteArray(new File(saveNum + ".tcdat"));
        } catch (final IOException ex) {
            return null;
        }

        int pos = 0;
        System.arraycopy(data, pos, temp, 0, temp.length);
        c.name = new String(temp, StandardCharsets.UTF_8).trim();
        pos += Globals.MAX_NAME_LENGTH;

        long leastSigBits, mostSigBits;
        temp = new byte[8];
        System.arraycopy(data, pos, temp, 0, temp.length);
        leastSigBits = Globals.bytesToLong(temp);
        pos += temp.length;

        System.arraycopy(data, pos, temp, 0, temp.length);
        mostSigBits = Globals.bytesToLong(temp);
        pos += temp.length;
        c.uniqueID = new UUID(mostSigBits, leastSigBits);

        final int[] statIDs = {Globals.STAT_LEVEL,
            Globals.STAT_POWER,
            Globals.STAT_DEFENSE,
            Globals.STAT_SPIRIT,
            Globals.STAT_SKILLPOINTS};

        temp = new byte[4];
        for (final int i : statIDs) {
            System.arraycopy(data, pos, temp, 0, temp.length);
            c.baseStats[i] = Globals.bytesToInt(temp);
            pos += temp.length;
        }

        pos = readItems(data, c.equipment, pos);
        for (final ItemEquip[] e : c.inventory) {
            pos = readItems(data, e, pos);
        }
        pos = readItems(data, c.upgrades, pos);
        pos = readSkills(data, c, pos);
        pos = readHotkeys(data, c, pos);
        pos = readKeyBind(data, c.getKeyBind(), pos);

        System.arraycopy(data, pos, temp, 0, temp.length);
        c.baseStats[Globals.STAT_EXP] = Globals.bytesToInt(temp);
        pos += temp.length;

        pos = readEmoteKeyBind(data, c.getKeyBind(), pos);

        c.calcStats();
        return c;
    }

    private static int readKeyBind(final byte[] data, final int[] keybind, final int pos) {
        int nextPos = pos;
        for (int i = 0; i < 16; i++) {
            final byte[] temp = new byte[4];
            System.arraycopy(data, nextPos, temp, 0, temp.length);
            keybind[i] = Globals.bytesToInt(temp);
            nextPos += temp.length;
        }
        return nextPos;
    }

    private static int readEmoteKeyBind(final byte[] data, final int[] keybind, final int pos) {
        int nextPos = pos;
        for (int i = 16; i < 16 + Globals.NUM_EMOTES; i++) {
            final byte[] temp = new byte[4];
            try {
                System.arraycopy(data, nextPos, temp, 0, temp.length);
                if (Globals.bytesToInt(temp) == 0) {
                    keybind[i] = -1;
                } else {
                    keybind[i] = Globals.bytesToInt(temp);
                }
            } catch (Exception e) {
                keybind[i] = -1;
            }
            nextPos += temp.length;
        }
        return nextPos;
    }

    private static int readSkills(final byte[] data, final SaveData c, final int pos) {
        int nextPos = pos;
        for (int i = 0; i < Globals.NUM_SKILLS; i++) {
            c.getSkills()[i].setLevel(data[nextPos]);
            nextPos += 1;
        }
        return nextPos;
    }

    private static int readHotkeys(final byte[] data, final SaveData c, final int pos) {
        int nextPos = pos;
        final Skill[] e = c.getHotkeys();
        for (int i = 0; i < e.length; i++) {
            final byte skillCode = data[nextPos];
            if (skillCode != -1) {
                e[i] = c.getSkills()[skillCode];
            }
            nextPos += 1;
        }
        return nextPos;
    }

    private static int readItems(final byte[] data, final ItemUpgrade[] e, final int pos) {
        int nextPos = pos;
        for (int i = 0; i < e.length; i++) {
            final byte[] temp = new byte[4];
            int itemCode;
            int level;

            System.arraycopy(data, nextPos, temp, 0, temp.length);
            itemCode = Globals.bytesToInt(temp);
            nextPos += temp.length;

            System.arraycopy(data, nextPos, temp, 0, temp.length);
            level = Globals.bytesToInt(temp);
            nextPos += temp.length;

            if (!ItemUpgrade.isValidItem(itemCode)) {
                e[i] = null;
            } else {
                e[i] = new ItemUpgrade(itemCode, level);
            }
        }
        return nextPos;
    }

    private static int readItems(final byte[] data, final ItemEquip[] e, final int pos) {
        int nextPos = pos;
        for (int i = 0; i < e.length; i++) {
            final double[] bs = new double[Globals.NUM_STATS];
            final byte[] temp = new byte[4];
            int itemCode;
            int upgrades;
            double bMult;

            System.arraycopy(data, nextPos, temp, 0, temp.length);
            itemCode = Globals.bytesToInt(temp);
            nextPos += temp.length;
            final int[] statIDs = {Globals.STAT_LEVEL,
                Globals.STAT_POWER,
                Globals.STAT_DEFENSE,
                Globals.STAT_SPIRIT,
                Globals.STAT_ARMOR,
                Globals.STAT_REGEN,
                Globals.STAT_CRITDMG,
                Globals.STAT_CRITCHANCE};

            for (final int s : statIDs) {
                System.arraycopy(data, nextPos, temp, 0, temp.length);
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
                nextPos += temp.length;
            }

            System.arraycopy(data, nextPos, temp, 0, temp.length);
            upgrades = Globals.bytesToInt(temp);
            nextPos += temp.length;

            System.arraycopy(data, nextPos, temp, 0, temp.length);
            bMult = Globals.bytesToInt(temp) / 100D;
            nextPos += temp.length;

            if (!ItemEquip.isValidItem(itemCode)) {
                e[i] = null;
            } else {
                e[i] = new ItemEquip(bs, upgrades, bMult, itemCode);
            }

        }
        return nextPos;
    }

    public Skill[] getHotkeys() {
        return this.hotkeys;
    }

    public Skill[] getSkills() {
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
        saveData(this.saveNum, this);
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
        for (final Skill s : this.skills) {
            totalSP += s.getLevel();
        }
        this.baseStats[Globals.STAT_SKILLPOINTS] = Globals.SP_PER_LEVEL * this.baseStats[Globals.STAT_LEVEL] - totalSP;

        if (totalSP > Globals.SP_PER_LEVEL * this.baseStats[Globals.STAT_LEVEL]) {
            for (final Skill s : this.skills) {
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

        this.baseStats[Globals.STAT_ARMOR] = Globals.calcArmor(this.totalStats[Globals.STAT_DEFENSE]);
        this.baseStats[Globals.STAT_REGEN] = Globals.calcRegen(this.totalStats[Globals.STAT_SPIRIT]);
        this.baseStats[Globals.STAT_CRITCHANCE] = Globals.calcCritChance(this.totalStats[Globals.STAT_SPIRIT]);
        this.baseStats[Globals.STAT_CRITDMG] = Globals.calcCritDmg(this.totalStats[Globals.STAT_SPIRIT]);

        this.totalStats[Globals.STAT_ARMOR] = this.baseStats[Globals.STAT_ARMOR] + this.bonusStats[Globals.STAT_ARMOR];
        this.totalStats[Globals.STAT_REGEN] = this.baseStats[Globals.STAT_REGEN] + this.bonusStats[Globals.STAT_REGEN];
        this.totalStats[Globals.STAT_CRITCHANCE] = this.baseStats[Globals.STAT_CRITCHANCE] + this.bonusStats[Globals.STAT_CRITCHANCE];
        this.totalStats[Globals.STAT_CRITDMG] = this.baseStats[Globals.STAT_CRITDMG] + this.bonusStats[Globals.STAT_CRITDMG];
        this.totalStats[Globals.STAT_DAMAGEREDUCT] = Globals.calcReduction(this.totalStats[Globals.STAT_ARMOR]);
        this.baseStats[Globals.STAT_MAXEXP] = Globals.calcEXPtoNxtLvl(this.baseStats[Globals.STAT_LEVEL]);
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
        saveData(this.saveNum, this);
    }

    public void resetSkill() {
        for (final Skill skill : this.skills) {
            skill.setLevel((byte) 0);
        }
        calcStats();
        saveData(this.saveNum, this);
    }

    public void addSkill(final byte skillCode, final boolean isMax) {
        if (this.baseStats[Globals.STAT_SKILLPOINTS] <= 0 || this.skills[skillCode].getLevel() >= 30) {
            return;
        }
        if (!isMax) {
            this.baseStats[Globals.STAT_SKILLPOINTS]--;
            this.skills[skillCode].addLevel((byte) 1);
        } else {
            double skillPointsRequired = 30 - this.skills[skillCode].getLevel();
            double amount = skillPointsRequired;
            if (this.baseStats[Globals.STAT_SKILLPOINTS] < skillPointsRequired) {
                amount = this.baseStats[Globals.STAT_SKILLPOINTS];
            }
            this.baseStats[Globals.STAT_SKILLPOINTS] -= amount;
            this.skills[skillCode].addLevel((byte) amount);
        }
        saveData(this.saveNum, this);
    }

    public void addStat(final byte stat, final int amount) {
        if (this.baseStats[Globals.STAT_POINTS] < amount) {
            return;
        }
        this.baseStats[Globals.STAT_POINTS] -= amount;
        this.baseStats[stat] += amount;

        calcStats();
        saveData(this.saveNum, this);
    }

    public void unequipItem(final byte slot) {
        byte itemType = slot;
        if (slot == Globals.ITEM_OFFHAND) {
            itemType = Globals.ITEM_WEAPON;
        }

        for (int i = 0; i < this.inventory[itemType].length; i++) {
            if (this.inventory[itemType][i] == null) {
                this.inventory[itemType][i] = this.equipment[slot];
                this.equipment[slot] = null;
                break;
            }
        }
        calcStats();
        saveData(this.saveNum, this);
    }

    public void equipItem(final int slot, final int inventorySlot) {
        int itemType = slot, equipSlot = slot;
        if (equipSlot == Globals.ITEM_OFFHAND) {
            itemType = Globals.ITEM_WEAPON;
        }

        final ItemEquip temp = this.inventory[itemType][inventorySlot];
        if (temp != null) {
            if (temp.getBaseStats()[Globals.STAT_LEVEL] > this.baseStats[Globals.STAT_LEVEL]) {
                return;
            }
            switch (ItemEquip.getItemType(temp.getItemCode())) {
                case Globals.ITEM_SHIELD:
                    equipSlot = Globals.ITEM_OFFHAND;
                    break;
                case Globals.ITEM_BOW:
                    equipSlot = Globals.ITEM_WEAPON;
                    break;
                case Globals.ITEM_ARROW:
                    equipSlot = Globals.ITEM_OFFHAND;
                    break;
            }
        }
        this.inventory[itemType][inventorySlot] = this.equipment[equipSlot];
        this.equipment[equipSlot] = temp;
        calcStats();
        saveData(this.saveNum, this);
    }

    public void destroyItem(final int type, final int slot) {
        this.inventory[type][slot] = null;
        saveData(this.saveNum, this);
    }

    public void destroyItem(final int slot) {
        this.upgrades[slot] = null;
        saveData(this.saveNum, this);
    }

    public void destroyAll(final int type) {
        for (int i = 0; i < this.inventory[type].length; i++) {
            this.inventory[type][i] = null;
        }
        saveData(this.saveNum, this);
    }

    public void destroyAllUpgrade() {
        for (int i = 0; i < this.upgrades.length; i++) {
            this.upgrades[i] = null;
        }
        saveData(this.saveNum, this);
    }

    public void addItem(final ItemEquip e) {
        int tab = ItemEquip.getItemType(e.getItemCode());
        if (tab == Globals.ITEM_SHIELD || tab == Globals.ITEM_ARROW || tab == Globals.ITEM_BOW) {
            tab = Globals.ITEM_WEAPON;
        }
        for (int i = 0; i < this.inventory[tab].length; i++) {
            if (this.inventory[tab][i] == null) {
                this.inventory[tab][i] = e;
                break;
            }
        }
        saveData(this.saveNum, this);
    }

    public void addItem(final ItemUpgrade e) {
        for (int i = 0; i < this.upgrades.length; i++) {
            if (this.upgrades[i] == null) {
                this.upgrades[i] = e;
                break;
            }
        }
        saveData(this.saveNum, this);
    }

    public void setKeyBind(final int k, final int keycode) {
        this.keybinds[k] = keycode;
        for (int i = 0; i < this.keybinds.length; i++) {
            if (i != k && this.keybinds[i] == keycode) {
                this.keybinds[i] = -1;
            }
        }
    }
}
