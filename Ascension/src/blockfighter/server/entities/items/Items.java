package blockfighter.server.entities.items;

import blockfighter.shared.Globals;

public class Items {

    public static byte getEquipSlot(final byte itemType) {
        switch (itemType) {
            case Globals.ITEM_AMULET:
                return Globals.EQUIP_AMULET;
            case Globals.ITEM_BELT:
                return Globals.EQUIP_BELT;
            case Globals.ITEM_CHEST:
                return Globals.EQUIP_CHEST;
            case Globals.ITEM_GLOVE:
                return Globals.EQUIP_GLOVE;
            case Globals.ITEM_HEAD:
                return Globals.EQUIP_HEAD;
            case Globals.ITEM_PANTS:
                return Globals.EQUIP_PANTS;
            case Globals.ITEM_RING:
                return Globals.EQUIP_RING;
            case Globals.ITEM_SHOE:
                return Globals.EQUIP_SHOE;
            case Globals.ITEM_SHOULDER:
                return Globals.EQUIP_SHOULDER;
            case Globals.ITEM_SWORD:
            case Globals.ITEM_BOW:
                return Globals.EQUIP_WEAPON;
            case Globals.ITEM_SHIELD:
            case Globals.ITEM_ARROW:
                return Globals.EQUIP_OFFHAND;
        }
        return -1;
    }
}
