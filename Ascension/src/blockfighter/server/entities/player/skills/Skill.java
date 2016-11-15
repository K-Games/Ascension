package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;
import blockfighter.server.entities.items.Items;
import blockfighter.server.entities.player.Player;
import blockfighter.shared.Globals;

public abstract class Skill {

    protected byte level;
    protected long skillCastTime;
    protected LogicModule logic;

    public abstract byte getSkillCode();

    public abstract boolean isPassive();

    public abstract long getMaxCooldown();

    public abstract byte getReqWeapon();

    public abstract byte getReqEquipSlot();

    public abstract byte castPlayerState();

    public abstract int getSkillDuration();

    public abstract double getBaseValue();

    public abstract double getMultValue();

    public abstract Double getCustomValue(String customHeader);

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
        return !isPassive() && (getReqWeapon() == -1 || getReqEquipSlot() == -1 || Items.getItemType(player.getEquips()[getReqEquipSlot()]) == getReqWeapon())
                && canCast();
    }

    public boolean canCast() {
        return this.getCooldown() >= getMaxCooldown() || Globals.DEBUG_MODE;
    }

    public void updateSkillUse(Player player) {

    }
}
