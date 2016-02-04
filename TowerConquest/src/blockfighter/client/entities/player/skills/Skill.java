package blockfighter.client.entities.player.skills;

import blockfighter.client.Globals;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

/**
 *
 * @author Ken Kwan
 */
public abstract class Skill {

    protected byte reqWeapon = -1;
    protected byte skillCode;
    protected byte level;
    protected long cooldown;
    protected long maxCooldown = 1;
    protected BufferedImage icon = Globals.MENU_BUTTON[Globals.BUTTON_SLOT];
    protected DecimalFormat df = new DecimalFormat("###,###,##0.##");
    protected String skillName = "NO_NAME";

    public final static byte NUM_SKILLS = 30,
            SWORD_VORPAL = 0x00,
            SWORD_MULTI = 0x01,
            SWORD_PHANTOM = 0x01,
            SWORD_CINDER = 0x02,
            SWORD_GASH = 0x03,
            SWORD_SLASH = 0x04,
            SWORD_TAUNT = 0x05,
            BOW_ARC = 0x06,
            BOW_POWER = 0x07,
            BOW_RAPID = 0x08,
            BOW_FROST = 0x09,
            BOW_STORM = 0x0A,
            BOW_VOLLEY = 0x0B,
            SHIELD_FORTIFY = 0x0C,
            SHIELD_IRON = 0x0D,
            SHIELD_CHARGE = 0x0E,
            SHIELD_REFLECT = 0x0F,
            SHIELD_TOSS = 0x10,
            SHIELD_DASH = 0x11,
            PASSIVE_DUALSWORD = 0x12,
            PASSIVE_KEENEYE = 0x13,
            PASSIVE_VITALHIT = 0x14,
            PASSIVE_SHIELDMASTERY = 0x15,
            PASSIVE_BARRIER = 0x16,
            PASSIVE_RESIST = 0x17,
            PASSIVE_BOWMASTERY = 0x18,
            PASSIVE_WILLPOWER = 0x19,
            PASSIVE_TACTICAL = 0x1A,
            PASSIVE_REVIVE = 0x1B,
            PASSIVE_SHADOWATTACK = 0x1C,
            PASSIVE_12 = 0x1D;

    public void draw(final Graphics2D g, final int x, final int y) {
        g.drawImage(this.icon, x, y, null);
    }

    public abstract void drawInfo(Graphics2D g, int x, int y);

    public String getSkillName() {
        return this.skillName;
    }

    public byte getSkillCode() {
        return this.skillCode;
    }

    public void resetCooldown() {
        this.cooldown = 0;
    }

    public void reduceCooldown(final long ms) {
        if (this.cooldown > 0) {
            this.cooldown -= ms;
        } else {
            this.cooldown = 0;
        }
    }

    public void setCooldown() {
        this.cooldown = this.maxCooldown;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public void setLevel(final byte lvl) {
        this.level = lvl;
    }

    public byte getLevel() {
        return this.level;
    }

    public boolean addLevel(final byte amount) {
        if (this.level + amount <= 30) {
            this.level += amount;
            return true;
        }
        return false;
    }

    public boolean isMaxed() {
        return this.level == 30;
    }

    public double getMaxCooldown() {
        return this.maxCooldown;
    }
}
