package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.player.Player;
import blockfighter.shared.Globals;
import blockfighter.shared.data.skill.SkillData;
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

    public final Double getCustomValue(int customHeaderIndex) {
        try {
            return getCustomValue(getSkillData().getCustomDataHeaders().get(customHeaderIndex));
        } catch (Exception e) {
            Globals.logError("Error while getting custom value " + customHeaderIndex, e);
            return null;
        }
    }

    public final Double getCustomValue(String customHeader) {
        HashMap<String, Double> customValues = getSkillData().getCustomValues();
        if (customValues == null) {
            return null;
        }
        return customValues.get(customHeader);
    }

    public final Byte getSkillCode() {
        return getStaticFieldValue("SKILL_CODE", Byte.class);
    }

    public final SkillData getSkillData() {
        return Globals.SkillClassMap.get(getSkillCode()).getSkillData();
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
        return !getSkillData().isPassive()
                && isOffCooldown() && player.getStats()[Globals.STAT_LEVEL] >= getSkillData().getReqLevel()
                && ((getSkillData().getReqWeapon() != null && getSkillData().getReqEquipSlot() != null
                && Globals.getEquipType(player.getEquips()[getSkillData().getReqEquipSlot()]) == getSkillData().getReqWeapon()) || getSkillData().getReqWeapon() == null);
    }

    public boolean isOffCooldown() {
        return this.getCooldown() >= getMaxCooldown() || Globals.DEBUG_MODE;
    }

    public long getMaxCooldown() {
        return getSkillData().getMaxCooldown();
    }

    public void updateSkillUse(Player player) {
    }

    public void updatePlayerAnimState(Player player) {
    }

}
