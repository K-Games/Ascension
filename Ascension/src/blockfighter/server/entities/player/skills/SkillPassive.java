package blockfighter.server.entities.player.skills;

import blockfighter.server.LogicModule;

public abstract class SkillPassive extends Skill {

    public SkillPassive(LogicModule l) {
        super(l);
    }

    @Override
    public byte castPlayerState() {
        return -1;
    }

    @Override
    public byte getReqEquipSlot() {
        return -1;
    }

    @Override
    public int getSkillDuration() {
        return 0;
    }

    @Override
    public Double getCustomValue(String customHeader) {
        return null;
    }
}
