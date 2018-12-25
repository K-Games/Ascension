package blockfighter.shared.data.skill;

import java.util.ArrayList;
import java.util.HashMap;

public class SkillData {

    private Byte SKILLCODE;
    private ArrayList<String> CUSTOM_DATA_HEADERS;
    private HashMap<String, Double> CUSTOM_VALUES;

    private String SKILL_NAME;

    private ArrayList<String> DESCRIPTION;
    private ArrayList<String> LEVEL_DESC;
    private ArrayList<String> MAX_BONUS_DESC;

    private Boolean IS_PASSIVE;
    private Boolean CANT_LEVEL;
    private Byte REQ_WEAPON;
    private Byte REQ_EQUIP_SLOT;
    private Long MAX_COOLDOWN;

    private Double BASE_VALUE, MULT_VALUE;
    private Integer REQ_LEVEL;

    private Byte CAST_PLAYER_STATE;
    private Integer SKILL_DURATION;

    public SkillData() {
    }

    public Byte getSkillCode() {
        return SKILLCODE;
    }

    public ArrayList<String> getCustomDataHeaders() {
        return CUSTOM_DATA_HEADERS;
    }

    public HashMap<String, Double> getCustomValues() {
        return CUSTOM_VALUES;
    }

    public String getSkillName() {
        return SKILL_NAME;
    }

    public ArrayList<String> getDesc() {
        return DESCRIPTION;
    }

    public ArrayList<String> getLevelDesc() {
        return LEVEL_DESC;
    }

    public ArrayList<String> getMaxBonusDesc() {
        return MAX_BONUS_DESC;
    }

    public Boolean isPassive() {
        return IS_PASSIVE;
    }

    public Boolean cantLevel() {
        return CANT_LEVEL;
    }

    public Byte getReqWeapon() {
        return REQ_WEAPON;
    }

    public Long getMaxCooldown() {
        return MAX_COOLDOWN;
    }

    public Double getBaseValue() {
        return BASE_VALUE;
    }

    public Double getMultValue() {
        return MULT_VALUE;
    }

    public Integer getReqLevel() {
        return REQ_LEVEL;
    }

    public Byte getReqEquipSlot() {
        return REQ_EQUIP_SLOT;
    }

    public Byte castPlayerState() {
        return CAST_PLAYER_STATE;
    }

    public Integer getSkillDuration() {
        return SKILL_DURATION;
    }

    public void set(Byte eqSlot, Byte PlayerState, Integer duratio) {
        REQ_EQUIP_SLOT = eqSlot;
        CAST_PLAYER_STATE = PlayerState;
        SKILL_DURATION = duratio;
    }
}
