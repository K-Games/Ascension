package blockfighter.server.entities.player.skills;

/**
 *
 * @author Ken Kwan
 */
public abstract class Skill {

    protected byte reqWeapon = -1;
    protected byte skillCode;
    protected byte level;
    protected long cooldown;
    protected long maxCooldown;

    public final static byte NUM_SKILLS = 30,
            SWORD_VORPAL = 0x00,
            SWORD_MULTI = 0x01,
            SWORD_CINDER = 0x02,
            SWORD_DRIVE = 0x03,
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

    /**
     * Get this skill code of this skill.
     *
     * @return
     */
    public byte getSkillCode() {
        return this.skillCode;
    }

    /**
     * Reduce the cooldown timer of this skill in milliseconds.
     *
     * @param ms Amount of milliseconds to reduce.
     */
    public void reduceCooldown(final long ms) {
        if (this.cooldown > 0) {
            this.cooldown -= ms;
        } else {
            this.cooldown = 0;
        }
    }

    /**
     * Set the cooldown of this skill to it's maximum.
     */
    public void setCooldown() {
        this.cooldown = this.maxCooldown;
    }

    /**
     * Get the current cooldown time of this skill.
     *
     * @return Cooldown in milliseconds
     */
    public long getCooldown() {
        return this.cooldown;
    }

    /**
     * Get the maximum cooldown of this skill.
     *
     * @return Maximum cooldown in milliseconds.
     */
    public long getMaxCooldown() {
        return this.maxCooldown;
    }

    /**
     * Set the level of this skill.
     *
     * @param lvl Skill level
     */
    public void setLevel(final byte lvl) {
        this.level = lvl;
    }

    /**
     * Get the skill level
     *
     * @return Byte - Skill level
     */
    public byte getLevel() {
        return this.level;
    }

    /**
     * Check if this skill is level 30
     *
     * @return True if skill is level 30
     */
    public boolean isMaxed() {
        return this.level == 30;
    }

    /**
     * Check if this skill can be cast with this weapon type and is off cooldown.
     *
     * @param weaponType Weapon Type(Item Type)
     * @return True if weapon is same as required weapon and cooldown is <= 0
     */
    public boolean canCast(final byte weaponType) {
        return (weaponType == this.reqWeapon || this.reqWeapon == -1) && this.cooldown <= 0;
    }

    public boolean canCast() {
        return this.reqWeapon == -1 && this.cooldown <= 0;
    }
}
