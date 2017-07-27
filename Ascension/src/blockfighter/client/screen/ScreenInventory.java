package blockfighter.client.screen;

import blockfighter.client.Core;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

public class ScreenInventory extends ScreenItemManagement {

    private static final int STAT_BOX_X = 935, STAT_BOX_Y = 440;

    private static final String DAMAGE_TEXT = "Damage: ";

    private static byte selectedTab = Globals.EQUIP_WEAPON;
    private static final Rectangle2D.Double[] INVENTORY_SLOTS = new Rectangle2D.Double[100],
            ITEM_TABS = new Rectangle2D.Double[Globals.NUM_EQUIP_TABS];

    private final String[] statString = new String[10];

    private int drawInfoItem = -1, drawInfoEquip = -1;

    private int dragItem = -1, dragEquip = -1;

    static {
        for (int i = 0; i < INVENTORY_SLOTS.length; i++) {
            INVENTORY_SLOTS[i] = new Rectangle2D.Double(270 + (i * 62) - (i / 10 * 620), 30 + i / 10 * 62, 60, 60);
        }

        for (int i = 0; i < ITEM_TABS.length; i++) {
            ITEM_TABS[i] = new Rectangle2D.Double(230, 30 + i * 61, 30, 60);
        }
    }

    @Override
    public void update() {
        super.update();
        final long now = Core.getLogicModule().getTime(); // Get time now
        if (now - this.lastUpdateTime >= Globals.CLIENT_LOGIC_UPDATE) {
            double[] baseStats = this.character.getBaseStats();
            double[] totalStats = this.character.getTotalStats();
            double[] bonusStats = this.character.getBonusStats();
            statString[0] = Globals.getStatName(Globals.STAT_LEVEL) + Globals.COLON_SPACE_TEXT + (int) baseStats[Globals.STAT_LEVEL];
            statString[1] = Globals.getStatName(Globals.STAT_POWER) + Globals.COLON_SPACE_TEXT + (int) baseStats[Globals.STAT_POWER] + " + " + (int) bonusStats[Globals.STAT_POWER];
            statString[2] = Globals.getStatName(Globals.STAT_DEFENSE) + Globals.COLON_SPACE_TEXT + (int) baseStats[Globals.STAT_DEFENSE] + " + " + (int) bonusStats[Globals.STAT_DEFENSE];
            statString[3] = Globals.getStatName(Globals.STAT_SPIRIT) + Globals.COLON_SPACE_TEXT + (int) baseStats[Globals.STAT_SPIRIT] + " + " + (int) bonusStats[Globals.STAT_SPIRIT];
            statString[4] = Globals.getStatName(Globals.STAT_MAXHP) + Globals.COLON_SPACE_TEXT + Globals.NUMBER_FORMAT.format((int) totalStats[Globals.STAT_MAXHP]);
            statString[5] = DAMAGE_TEXT + Globals.NUMBER_FORMAT.format((int) totalStats[Globals.STAT_MINDMG]) + " - " + Globals.NUMBER_FORMAT.format((int) totalStats[Globals.STAT_MAXDMG]);
            statString[6] = Globals.getStatName(Globals.STAT_ARMOUR) + Globals.COLON_SPACE_TEXT + Globals.NUMBER_FORMAT.format((int) baseStats[Globals.STAT_ARMOUR]) + " + " + Globals.NUMBER_FORMAT.format((int) bonusStats[Globals.STAT_ARMOUR]);
            statString[7] = Globals.getStatName(Globals.STAT_REGEN) + Globals.COLON_SPACE_TEXT + Globals.NUMBER_FORMAT.format(baseStats[Globals.STAT_REGEN]) + " + " + Globals.NUMBER_FORMAT.format(bonusStats[Globals.STAT_REGEN]) + " HP/Sec";
            statString[8] = Globals.getStatName(Globals.STAT_CRITCHANCE) + Globals.COLON_SPACE_TEXT + Globals.NUMBER_FORMAT.format(baseStats[Globals.STAT_CRITCHANCE] * 100) + " + "
                    + Globals.NUMBER_FORMAT.format(bonusStats[Globals.STAT_CRITCHANCE] * 100) + "%";
            statString[9] = Globals.getStatName(Globals.STAT_CRITDMG) + Globals.COLON_SPACE_TEXT + Globals.NUMBER_FORMAT.format((1 + baseStats[Globals.STAT_CRITDMG]) * 100) + " + "
                    + Globals.NUMBER_FORMAT.format(bonusStats[Globals.STAT_CRITDMG] * 100) + "%";
            this.lastUpdateTime = now;
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        final BufferedImage bg = Globals.MENU_BG[1];
        g.drawImage(bg, 0, 0, null);

        drawDestroyButtons(g);
        drawStats(g);
        drawEquipSlots(g);
        drawInventory(g);
        drawItemTabs(g);
        if (this.destroyConfirm) {
            drawDestroyConfirm(g);
        }
        drawMenuButton(g);
        if (this.destroy) {
            BufferedImage button = Globals.MENU_ITEMDELETE[0];
            g.drawImage(button, (int) (this.mousePos.x + 10), (int) (this.mousePos.y + 15), null);
        }
        if (this.dragItem != -1) {
            this.character.getInventory(selectedTab)[this.dragItem].drawIcon(g, (int) (this.mousePos.x + 5), (int) (this.mousePos.y + 5), this.overlayColour);
        } else if (this.dragEquip != -1) {
            this.character.getEquip()[this.dragEquip].drawIcon(g, (int) (this.mousePos.x + 5), (int) (this.mousePos.y + 5), this.overlayColour);
        }
        super.draw(g);
        drawItemInfo(g);
    }

    private void drawItemTabs(final Graphics2D g) {
        for (int i = 0; i < ITEM_TABS.length; i++) {
            final BufferedImage button = Globals.MENU_BUTTON[i + 5];
            g.drawImage(button, (int) ITEM_TABS[i].x, (int) ITEM_TABS[i].y, null);
        }
        // Tab pointer
        g.drawImage(Globals.MENU_TABPOINTER[0], 260, (int) ITEM_TABS[selectedTab].y, null);
        g.drawImage(Globals.MENU_TABPOINTER[1], (int) ITEM_TABS[selectedTab].x, (int) ITEM_TABS[selectedTab].y, null);

    }

    private void drawItemInfo(final Graphics2D g) {
        if (this.destroyConfirm) {
            return;
        }
        if (this.drawInfoItem != -1) {
            drawItemInfo(g, INVENTORY_SLOTS[this.drawInfoItem], this.character.getInventory(selectedTab)[this.drawInfoItem]);
        } else if (this.drawInfoEquip != -1) {
            drawItemInfo(g, EQUIP_SLOTS[this.drawInfoEquip], this.character.getEquip()[this.drawInfoEquip]);
        }
    }

    private void drawStats(final Graphics2D g) {
        g.setColor(SKILL_BOX_BG_COLOR);
        g.fillRoundRect(STAT_BOX_X - 10, STAT_BOX_Y - 20, 320, 255, 15, 15);

        for (byte i = 0; i < statString.length; i++) {
            g.setFont(Globals.ARIAL_15PT);
            drawStringOutline(g, statString[i], STAT_BOX_X, STAT_BOX_Y + i * 25, 1);
            g.setColor(Color.WHITE);
            g.drawString(statString[i], STAT_BOX_X, STAT_BOX_Y + i * 25);
        }

    }

    private void drawInventory(final Graphics2D g) {
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SLOT];

        for (int i = 0; i < this.character.getInventory(selectedTab).length; i++) {
            g.drawImage(button, (int) INVENTORY_SLOTS[i].x, (int) INVENTORY_SLOTS[i].y, null);
            if (this.character.getInventory(selectedTab)[i] != null) {
                this.character.getInventory(selectedTab)[i].drawIcon(g, (int) INVENTORY_SLOTS[i].x, (int) INVENTORY_SLOTS[i].y, this.overlayColour);
            }
        }
        if (selectedTab == Globals.EQUIP_WEAPON) {
            button = Globals.MENU_BUTTON[Globals.BUTTON_RIGHTCLICK];
            g.drawImage(button, 280, 657, null);
            g.setFont(Globals.ARIAL_15PT);
            drawStringOutline(g, EQUIP_OFFHAND_TEXT, 310, 680, 1);
            g.setColor(new Color(255, 130, 0));
            g.drawString(EQUIP_OFFHAND_TEXT, 310, 680);
        }
    }

    private void drawItemInfo(final Graphics2D g, final Rectangle2D.Double box, final ItemEquip e) {
        if (e == null) {
            return;
        }
        e.drawInfo(g, box);
    }

    @Override
    public void keyTyped(final KeyEvent e) {

    }

    @Override
    public void keyPressed(final KeyEvent e) {

    }

    @Override
    public void keyReleased(final KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.destroy = false;
            this.destroyConfirm = false;
        }
    }

    @Override
    public void mouseClicked(final MouseEvent e) {

    }

    @Override
    public void mousePressed(final MouseEvent e) {

    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        Point2D.Double scaled;
        if (Globals.WINDOW_SCALE_ENABLED) {
            scaled = new Point2D.Double(e.getX() / Globals.WINDOW_SCALE, e.getY() / Globals.WINDOW_SCALE);
        } else {
            scaled = new Point2D.Double(e.getX(), e.getY());
        }
        final int drItem = this.dragItem, drEq = this.dragEquip;
        this.dragItem = -1;
        this.dragEquip = -1;
        if (this.destroyConfirm) {
            mouseReleased_destroyConfirm(e);
            return;
        }

        super.mouseReleased(e);
        if (SwingUtilities.isLeftMouseButton(e)) {
            for (byte i = 0; i < ITEM_TABS.length; i++) {
                if (drItem == -1 && drEq == -1 && ITEM_TABS[i].contains(scaled)) {
                    selectedTab = i;
                    this.destroy = false;
                    return;
                }
            }

            for (int i = 0; i < INVENTORY_SLOTS.length; i++) {
                if (INVENTORY_SLOTS[i].contains(scaled)) {
                    if (!this.destroy) {
                        if (drItem != -1) {
                            final ItemEquip temp = this.character.getInventory(selectedTab)[i];
                            this.character.getInventory(selectedTab)[i] = this.character.getInventory(selectedTab)[drItem];
                            this.character.getInventory(selectedTab)[drItem] = temp;
                            return;
                        }
                        if (drEq != -1) {
                            final ItemEquip equipped = this.character.getEquip()[drEq];
                            this.character.equipItem(equipped.getEquipTab(), drEq, i);
                            return;
                        }
                        if (this.character.getInventory(selectedTab)[i] != null) {
                            final ItemEquip toEquip = this.character.getInventory(selectedTab)[i];
                            this.character.equipItem(toEquip.getEquipTab(), toEquip.getEquipSlot(), i);
                            return;
                        }
                    } else {
                        this.character.destroyItem(selectedTab, i);
                    }
                    return;
                }
            }

            for (byte i = 0; !this.destroy && i < EQUIP_SLOTS.length; i++) {
                if (EQUIP_SLOTS[i].contains(scaled)) {
                    if (drItem != -1) {
                        if (selectedTab == i || (selectedTab == Globals.EQUIP_WEAPON && i == Globals.EQUIP_OFFHAND)) {
                            final ItemEquip toEquip = this.character.getInventory(selectedTab)[drItem];
                            if (!(toEquip.getEquipSlot() == Globals.EQUIP_OFFHAND && i == Globals.EQUIP_WEAPON)
                                    && !(toEquip.getItemType() == Globals.ITEM_BOW && i == Globals.EQUIP_OFFHAND)) {
                                this.character.equipItem(toEquip.getEquipTab(), i, drItem);
                                return;
                            }
                        }
                    }
                    if (drItem == -1 && drEq == -1 && this.character.getEquip()[i] != null) {
                        final ItemEquip equip = this.character.getEquip()[i];
                        this.character.unequipItem(equip.getEquipTab(), i);
                        return;
                    }
                }
            }

            for (int i = 0; i < DESTROY_BOX.length; i++) {
                if (DESTROY_BOX[i].contains(scaled)) {
                    switch (i) {
                        case 0:
                            this.destroy = !this.destroy;
                            break;
                        case 1:
                            this.destroy = false;
                            this.destroyConfirm = true;
                            break;
                    }
                    return;
                }
            }
        }

        if (SwingUtilities.isRightMouseButton(e)) {
            if (selectedTab == Globals.EQUIP_WEAPON) {
                for (int i = 0; i < INVENTORY_SLOTS.length; i++) {
                    if (INVENTORY_SLOTS[i].contains(scaled) && this.character.getInventory(selectedTab)[i] != null) {
                        if (!this.destroy) {
                            final ItemEquip equip = this.character.getInventory(selectedTab)[i];
                            if (equip.getItemType() != Globals.ITEM_BOW) {
                                this.character.equipItem(equip.getEquipTab(), Globals.EQUIP_OFFHAND, i);
                            }
                        }
                        return;
                    }
                }
            }
        }
    }

    @Override
    protected void mouseReleased_destroyConfirm(final MouseEvent e) {
        Point2D.Double scaled;
        if (Globals.WINDOW_SCALE_ENABLED) {
            scaled = new Point2D.Double(e.getX() / Globals.WINDOW_SCALE, e.getY() / Globals.WINDOW_SCALE);
        } else {
            scaled = new Point2D.Double(e.getX(), e.getY());
        }
        for (byte i = 0; i < PROMPT_BOX.length; i++) {
            if (PROMPT_BOX[i].contains(scaled)) {
                if (i == 0) {
                    this.character.destroyAll(selectedTab);
                }
                this.destroyConfirm = false;
            }
        }
    }

    @Override
    public void mouseEntered(final MouseEvent e) {

    }

    @Override
    public void mouseExited(final MouseEvent e) {

    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        Point2D.Double scaled;
        if (Globals.WINDOW_SCALE_ENABLED) {
            scaled = new Point2D.Double(e.getX() / Globals.WINDOW_SCALE, e.getY() / Globals.WINDOW_SCALE);
        } else {
            scaled = new Point2D.Double(e.getX(), e.getY());
        }
        mouseMoved(e);
        if (this.destroyConfirm || this.destroy) {
            return;
        }
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (this.dragItem == -1 && this.dragEquip == -1) {
                for (int i = 0; i < INVENTORY_SLOTS.length; i++) {
                    if (INVENTORY_SLOTS[i].contains(scaled) && this.character.getInventory(selectedTab)[i] != null) {
                        this.dragItem = i;
                        return;
                    }
                }

                for (byte i = 0; i < EQUIP_SLOTS.length; i++) {
                    if (EQUIP_SLOTS[i].contains(scaled) && this.character.getEquip()[i] != null) {
                        this.dragEquip = i;
                        if (i == Globals.EQUIP_OFFHAND) {
                            selectedTab = Globals.EQUIP_WEAPON;
                        } else {
                            selectedTab = i;
                        }
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        Point2D.Double scaled;
        if (Globals.WINDOW_SCALE_ENABLED) {
            scaled = new Point2D.Double(e.getX() / Globals.WINDOW_SCALE, e.getY() / Globals.WINDOW_SCALE);
        } else {
            scaled = new Point2D.Double(e.getX(), e.getY());
        }
        this.mousePos = scaled;
        this.drawInfoItem = -1;
        this.drawInfoEquip = -1;
        for (int i = 0; i < INVENTORY_SLOTS.length; i++) {
            if (INVENTORY_SLOTS[i].contains(scaled) && this.character.getInventory(selectedTab)[i] != null) {
                this.drawInfoItem = i;
                return;
            }
        }

        for (byte i = 0; i < EQUIP_SLOTS.length; i++) {
            if (EQUIP_SLOTS[i].contains(scaled) && this.character.getEquip()[i] != null) {
                this.drawInfoEquip = i;
                return;
            }
        }
    }

    @Override
    public void unload() {
    }

}
