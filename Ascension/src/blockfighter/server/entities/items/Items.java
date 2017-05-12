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

    public static byte getItemType(final int i) {
        if (i >= 100000 && i <= 109999) {
            // Swords
            return Globals.ITEM_SWORD;
        } else if (i >= 110000 && i <= 119999) {
            // Shields
            return Globals.ITEM_SHIELD;
        } else if (i >= 120000 && i <= 129999) {
            // Bows
            return Globals.ITEM_BOW;
        } else if (i >= 130000 && i <= 199999) {
            return Globals.ITEM_ARROW;
        } else if (i >= 200000 && i <= 209999) {
            return Globals.ITEM_HEAD;
        } else if (i >= 300000 && i <= 309999) {
            return Globals.ITEM_CHEST;
        } else if (i >= 400000 && i <= 409999) {
            return Globals.ITEM_PANTS;
        } else if (i >= 500000 && i <= 509999) {
            return Globals.ITEM_SHOULDER;
        } else if (i >= 600000 && i <= 609999) {
            return Globals.ITEM_GLOVE;
        } else if (i >= 700000 && i <= 709999) {
            return Globals.ITEM_SHOE;
        } else if (i >= 800000 && i <= 809999) {
            return Globals.ITEM_BELT;
        } else if (i >= 900000 && i <= 909999) {
            return Globals.ITEM_RING;
        } else if (i >= 1000000 && i <= 1009999) {
            return Globals.ITEM_AMULET;
        }
        return -1;
    }
}
