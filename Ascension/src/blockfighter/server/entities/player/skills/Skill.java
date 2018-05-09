package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.shared.Globals;
import java.util.HashMap;

public abstract class Skill {

    protected byte level;
    protected long skillCastTime;
    protected LogicModule logic;

    public final <T> T getStaticFieldValue(String fieldName, Class<T> fieldType) {
        try {
            return fieldType.cast(this.getClass().getDeclaredField(fieldName).get(null));
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Globals.logError("Could not find static field: " + fieldName + " in " + this.getClass().getSimpleName(), ex);
        }
        return null;
    }

    public final Double getCustomValue(String customHeader) {
        HashMap customValues = getStaticFieldValue("CUSTOM_VALUES", HashMap.class);
        if (customValues == null) {
            return null;

        }
        return (Double) customValues.get(customHeader);
    }

    public long getMaxCooldown() {
        return getStaticFieldValue("MAX_COOLDOWN", Long.class);
    }

    public final int getReqLevel() {
        return getStaticFieldValue("REQ_LEVEL", Integer.class);
    }

    public final byte getReqWeapon() {
        return getStaticFieldValue("REQ_WEAPON", Byte.class);
    }

    public final byte getSkillCode() {
        return getStaticFieldValue("SKILL_CODE", Byte.class);
    }

    public final String getSkillName() {
        return getStaticFieldValue("SKILL_NAME", String.class);
    }

    public final boolean isPassive() {
        return getStaticFieldValue("IS_PASSIVE", Boolean.class);
    }

    public final boolean cantLevel() {
        return getStaticFieldValue("CANT_LEVEL", Boolean.class);
    }

    public byte castPlayerState() {
        return getStaticFieldValue("PLAYER_STATE", Byte.class);
    }

    public final double getBaseValue() {
        return getStaticFieldValue("BASE_VALUE", Double.class);
    }

    public final double getMultValue() {
        return getStaticFieldValue("MULT_VALUE", Double.class);
    }

    public byte getReqEquipSlot() {
        return getStaticFieldValue("REQ_EQUIP_SLOT", Byte.class);
    }

    public int getSkillDuration() {
        return getStaticFieldValue("SKILL_DURATION", Integer.class);
    }

    public Skill(final LogicModule l) {
        this.logic = l;
    }

    public void reduceCooldown(final int ms) {
        this.skillCastTime -= Globals.msToNs(ms);
    }

    public void setCooldown() {
        this.skillCastTime = this.logic.getTime();
    }

    public long getCooldown() {
        return Globals.nsToMs(this.logic.getTime() - this.skillCastTime);
    }

    public void setLevel(final byte lvl) {
        this.level = lvl;
    }

    public byte getLevel() {
        return this.level;
    }

    public boolean isMaxed() {
        return this.level == 30;
    }

    public boolean canCast(final Player player) {
        return !isPassive() && (getReqWeapon() == -1 || getReqEquipSlot() == -1 || Globals.getEquipType(player.getEquips()[getReqEquipSlot()]) == getReqWeapon())
                && canCast() && player.getStats()[Globals.STAT_LEVEL] >= getReqLevel();
    }

    public boolean canCast() {
        return this.getCooldown() >= getMaxCooldown() || Globals.DEBUG_MODE;
    }

    public void updateSkillUse(Player player) {
    }

    public void updatePlayerAnimState(Player player) {
    }

}
