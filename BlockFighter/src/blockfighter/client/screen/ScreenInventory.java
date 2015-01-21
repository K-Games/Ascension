package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.SaveData;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

/**
 *
 * @author Ken
 */
public class ScreenInventory extends ScreenMenu {

    private SaveData c;
    private byte selectedTab = Globals.ITEM_WEAPON;
    private boolean destroy = false, destroyConfirm = false;
    private Rectangle2D.Double[] inventSlots = new Rectangle2D.Double[100],
            equipSlots = new Rectangle2D.Double[Globals.NUM_EQUIP_SLOTS],
            tabs = new Rectangle2D.Double[Globals.NUM_ITEM_TYPES],
            destroyBox = new Rectangle2D.Double[2],
            promptBox = new Rectangle2D.Double[2];

    private Point mousePos;

    private int drawItem = -1, drawEquip = -1;
    private int charFrame = 0;

    private int dragItem = -1, dragEquip = -1;
    private double nextFrameTime = 0;

    public ScreenInventory(LogicModule l) {
        super(l);
        c = l.getSelectedChar();
        for (int i = 0; i < inventSlots.length; i++) {
            inventSlots[i] = new Rectangle2D.Double(270 + (i * 62) - (i / 10 * 620), 30 + i / 10 * 62, 60, 60);
        }

        for (int i = 0; i < tabs.length; i++) {
            tabs[i] = new Rectangle2D.Double(230, 30 + i * 61, 30, 60);
        }

        equipSlots[Globals.ITEM_AMULET] = new Rectangle2D.Double(1140, 40, 60, 60);
        equipSlots[Globals.ITEM_BELT] = new Rectangle2D.Double(1140, 250, 60, 60);
        equipSlots[Globals.ITEM_OFFHAND] = new Rectangle2D.Double(1140, 180, 60, 60);
        equipSlots[Globals.ITEM_CHEST] = new Rectangle2D.Double(1140, 110, 60, 60);
        equipSlots[Globals.ITEM_HEAD] = new Rectangle2D.Double(1060, 40, 60, 60);
        equipSlots[Globals.ITEM_RING] = new Rectangle2D.Double(980, 40, 60, 60);
        equipSlots[Globals.ITEM_SHOULDER] = new Rectangle2D.Double(980, 110, 60, 60);
        equipSlots[Globals.ITEM_GLOVE] = new Rectangle2D.Double(980, 250, 60, 60);
        equipSlots[Globals.ITEM_WEAPON] = new Rectangle2D.Double(980, 180, 60, 60);
        equipSlots[Globals.ITEM_PANTS] = new Rectangle2D.Double(1025, 320, 60, 60);
        equipSlots[Globals.ITEM_SHOE] = new Rectangle2D.Double(1095, 320, 60, 60);

        for (int i = 0; i < destroyBox.length; i++) {
            destroyBox[i] = new Rectangle2D.Double(520 + i * 185, 655, 180, 30);
        }
        promptBox[0] = new Rectangle2D.Double(401, 400, 214, 112);
        promptBox[1] = new Rectangle2D.Double(665, 400, 214, 112);
    }

    @Override
    public void update() {
        double now = System.nanoTime(); //Get time now
        if (now - lastUpdateTime >= Globals.LOGIC_UPDATE) {
            updateParticles(particles);
            nextFrameTime -= Globals.LOGIC_UPDATE;
            if (nextFrameTime <= 0) {
                if (charFrame >= 8) {
                    charFrame = 0;
                } else {
                    charFrame++;
                }
                nextFrameTime = 150000000;
            }
            lastUpdateTime = now;
        }
        while (now - lastUpdateTime < Globals.LOGIC_UPDATE) {
            Thread.yield();
            now = System.nanoTime();
        }

    }

    @Override
    public void draw(Graphics2D g) {
        BufferedImage bg = Globals.MENU_BG[2];
        g.drawImage(bg, 0, 0, null);

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SMALLRECT];
        g.drawImage(button, (int) destroyBox[0].x, (int) destroyBox[0].y, null);
        g.drawImage(button, (int) destroyBox[1].x, (int) destroyBox[1].y, null);
        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, "Destroy Item", 560, 682, 1);
        drawStringOutline(g, "Destroy All", 750, 682, 1);
        g.setColor(Color.WHITE);
        g.drawString("Destroy Item", 560, 682);
        g.drawString("Destroy All", 750, 682);

        drawStats(g);
        drawSlots(g);
        drawItemTabs(g);
        drawDestroyConfirm(g);
        drawMenuButton(g);
        if (destroy) {
            button = Globals.MENU_ITEMDELETE[0];
            g.drawImage(button, mousePos.x + 10, mousePos.y + 15, null);
        }
        if (dragItem != -1) {
            c.getInventory(selectedTab)[dragItem].draw(g, mousePos.x + 5, mousePos.y + 5);
        }
        if (dragEquip != -1) {
            c.getEquip()[dragEquip].draw(g, mousePos.x + 5, mousePos.y + 5);
        }
        super.draw(g);
        drawItemInfo(g);
    }

    private void drawDestroyConfirm(Graphics2D g) {
        if (destroyConfirm) {
            BufferedImage window = Globals.MENU_WINDOW[Globals.WINDOW_DESTROYCONFIRM];
            g.drawImage(window, 265, 135, null);

            g.setFont(Globals.ARIAL_30PT);
            drawStringOutline(g, "Are you sure?", 540, 300, 2);
            g.setColor(Color.WHITE);
            g.drawString("Are you sure?", 540, 300);

            BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_BIGRECT];
            g.drawImage(button, 401, 400, null);
            drawStringOutline(g, "Confirm", 455, 465, 2);
            g.setColor(Color.WHITE);
            g.drawString("Confirm", 455, 465);

            g.drawImage(button, 665, 400, null);
            drawStringOutline(g, "Cancel", 725, 465, 2);
            g.setColor(Color.WHITE);
            g.drawString("Cancel", 725, 465);
        }
    }

    private void drawItemTabs(Graphics2D g) {
        for (int i = 0; i < tabs.length; i++) {
            BufferedImage button = Globals.MENU_BUTTON[i + 5];
            g.drawImage(button, (int) tabs[i].x, (int) tabs[i].y, null);
        }
        //Tab pointer
        g.drawImage(Globals.MENU_TABPOINTER[0], 260, (int) tabs[selectedTab].y, null);

    }

    private void drawItemInfo(Graphics2D g) {
        if (destroyConfirm) {
            return;
        }
        if (drawItem != -1) {
            drawItemInfo(g, inventSlots[drawItem], c.getInventory(selectedTab)[drawItem]);
        } else if (drawEquip != -1) {
            drawItemInfo(g, equipSlots[drawEquip], c.getEquip()[drawEquip]);
        }
    }

    private void drawStats(Graphics2D g) {
        g.setFont(Globals.ARIAL_15PT);
        int statY = 440, statX = 935;
        double[] bs = c.getBaseStats(), bonus = c.getBonusStats(), total = c.getStats();
        drawStringOutline(g, "Level: " + (int) bs[Globals.STAT_LEVEL], statX, statY, 1);
        drawStringOutline(g, "Power: " + (int) bs[Globals.STAT_POWER] + " + " + (int) bonus[Globals.STAT_POWER], statX, statY + 25, 1);
        drawStringOutline(g, "Defense: " + (int) bs[Globals.STAT_DEFENSE] + " + " + (int) bonus[Globals.STAT_DEFENSE], statX, statY + 50, 1);
        drawStringOutline(g, "Spirit: " + (int) bs[Globals.STAT_SPIRIT] + " + " + (int) bonus[Globals.STAT_SPIRIT], statX, statY + 75, 1);

        drawStringOutline(g, "HP: " + (int) total[Globals.STAT_MAXHP], statX, statY + 105, 1);
        drawStringOutline(g, "Damage: " + (int) total[Globals.STAT_MINDMG] + " - " + (int) total[Globals.STAT_MAXDMG], statX, statY + 130, 1);
        drawStringOutline(g, "Armor: " + (int) bs[Globals.STAT_ARMOR] + " + " + (int) bonus[Globals.STAT_ARMOR], statX, statY + 155, 1);
        drawStringOutline(g, "Regen: " + df.format(bs[Globals.STAT_REGEN]) + " + " + df.format(bonus[Globals.STAT_REGEN]) + " HP/Sec", statX, statY + 180, 1);
        drawStringOutline(g, "Critical Hit Chance: " + df.format(bs[Globals.STAT_CRITCHANCE] * 100) + " + " + df.format(bonus[Globals.STAT_CRITCHANCE] * 100) + "%", statX, statY + 205, 1);
        drawStringOutline(g, "Critical Hit Damage: " + df.format(bs[Globals.STAT_CRITDMG] * 100) + " + " + df.format(bonus[Globals.STAT_CRITDMG] * 100) + "%", statX, statY + 230, 1);

        g.setColor(Color.WHITE);
        g.drawString("Level: " + (int) bs[Globals.STAT_LEVEL], statX, statY);
        g.drawString("Power: " + (int) bs[Globals.STAT_POWER] + " + " + (int) bonus[Globals.STAT_POWER], statX, statY + 25);
        g.drawString("Defense: " + (int) bs[Globals.STAT_DEFENSE] + " + " + (int) bonus[Globals.STAT_DEFENSE], statX, statY + 50);
        g.drawString("Spirit: " + (int) bs[Globals.STAT_SPIRIT] + " + " + (int) bonus[Globals.STAT_SPIRIT], statX, statY + 75);

        g.drawString("HP: " + (int) total[Globals.STAT_MAXHP], statX, statY + 105);
        g.drawString("Damage: " + (int) total[Globals.STAT_MINDMG] + " - " + (int) total[Globals.STAT_MAXDMG], statX, statY + 130);
        g.drawString("Armor: " + (int) bs[Globals.STAT_ARMOR] + " + " + (int) bonus[Globals.STAT_ARMOR], statX, statY + 155);
        g.drawString("Regen: " + df.format(bs[Globals.STAT_REGEN]) + " + " + df.format(bonus[Globals.STAT_REGEN]) + " HP/Sec", statX, statY + 180);
        g.drawString("Critical Hit Chance: " + df.format(bs[Globals.STAT_CRITCHANCE] * 100) + " + " + df.format(bonus[Globals.STAT_CRITCHANCE] * 100) + "%", statX, statY + 205);
        g.drawString("Critical Hit Damage: " + df.format(bs[Globals.STAT_CRITDMG] * 100) + " + " + df.format(bonus[Globals.STAT_CRITDMG] * 100) + "%", statX, statY + 230);

    }

    private void drawSlots(Graphics2D g) {
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_ITEMSLOT];
        BufferedImage character = Globals.CHAR_SPRITE[Globals.PLAYER_STATE_STAND][charFrame];

        //Inventory
        for (int i = 0; i < c.getInventory(selectedTab).length; i++) {
            g.drawImage(button, (int) inventSlots[i].x, (int) inventSlots[i].y, null);
            if (c.getInventory(selectedTab)[i] != null) {
                c.getInventory(selectedTab)[i].draw(g, (int) inventSlots[i].x, (int) inventSlots[i].y);
            }
            if (selectedTab == Globals.ITEM_WEAPON) {
                g.setFont(Globals.ARIAL_15PT);
                drawStringOutline(g, "Right Click to equip as Offhand", 280, 682, 1);
                g.setColor(Color.WHITE);
                g.drawString("Right Click to equip as Offhand", 280, 682);
            }
        }

        //Equipment
        for (int i = 0; i < equipSlots.length; i++) {
            g.drawImage(button, (int) equipSlots[i].x, (int) equipSlots[i].y, null);
            if (c.getEquip()[i] != null) {
                c.getEquip()[i].draw(g, (int) equipSlots[i].x, (int) equipSlots[i].y);
            }
            String s = "";
            switch (i) {
                case Globals.ITEM_AMULET:
                    s = "Amulet";
                    break;

                case Globals.ITEM_BELT:
                    s = "Belt";
                    break;

                case Globals.ITEM_CHEST:
                    s = "Chest";
                    break;

                case Globals.ITEM_GLOVE:
                    s = "Glove";
                    break;

                case Globals.ITEM_HEAD:
                    s = "Head";
                    break;

                case Globals.ITEM_OFFHAND:
                    s = "Offhand";
                    break;

                case Globals.ITEM_PANTS:
                    s = "Pants";
                    break;

                case Globals.ITEM_RING:
                    s = "Ring";
                    break;

                case Globals.ITEM_SHOE:
                    s = "Shoe";
                    break;

                case Globals.ITEM_SHOULDER:
                    s = "Shoulder";
                    break;

                case Globals.ITEM_WEAPON:
                    s = "Weapon";
                    break;
            }
            g.setFont(Globals.ARIAL_12PT);
            drawStringOutline(g, s, (int) equipSlots[i].x + 2, (int) equipSlots[i].y + 58, 1);
            g.setColor(Color.WHITE);
            g.drawString(s, (int) equipSlots[i].x + 2, (int) equipSlots[i].y + 58);
        }

        g.drawImage(character, 1050, 100, null);
    }

    private void drawItemInfo(Graphics2D g, Rectangle2D.Double box, ItemEquip e) {
        if (e == null) {
            return;
        }
        g.setColor(new Color(30, 30, 30, 185));
        int y = (int) box.y;
        int x = (int) box.x;
        int boxHeight = 120;

        if (e.getStats()[Globals.STAT_REGEN] > 0) {
            boxHeight += 20;
        }
        if (e.getStats()[Globals.STAT_ARMOR] > 0) {
            boxHeight += 20;
        }
        if (e.getStats()[Globals.STAT_CRITDMG] > 0) {
            boxHeight += 20;
        }
        if (e.getStats()[Globals.STAT_CRITCHANCE] > 0) {
            boxHeight += 20;
        }
        if (y + boxHeight > 720) {
            y = 700 - boxHeight;
        }

        if (x + 30 + 200 > 1280) {
            x = 1040;
        }
        g.fillRect(x + 30, y, 200, boxHeight);
        g.setColor(Color.BLACK);
        g.drawRect(x + 30, y, 200, boxHeight);
        g.drawRect(x + 31, y + 1, 198, boxHeight - 2);

        g.setFont(Globals.ARIAL_15PT);

        String tier = "";
        switch (e.getTier()) {
            case ItemEquip.TIER_COMMON:
                g.setColor(Color.WHITE);
                tier = "Common ";
                break;
            case ItemEquip.TIER_UNCOMMON:
                g.setColor(new Color(180, 0, 255));
                tier = "Uncommon ";
                break;
            case ItemEquip.TIER_RARE:
                g.setColor(new Color(255, 225, 0));
                tier = "Rare ";
                break;
            case ItemEquip.TIER_RUNIC:
                g.setColor(new Color(255, 130, 0));
                tier = "Runic ";
                break;
            case ItemEquip.TIER_LEGENDARY:
                g.setColor(new Color(205, 15, 0));
                tier = "Legendary ";
                break;
            case ItemEquip.TIER_ARCHAIC:
                g.setColor(new Color(0, 220, 0));
                tier = "Archaic ";
                break;
            case ItemEquip.TIER_DIVINE:
                g.setColor(new Color(0, 255, 160));
                tier = "Divine ";
                break;
        }

        if (e.getUpgrades() > 0) {
            g.drawString(tier + e.getItemName() + " +" + e.getUpgrades(), x + 40, y + 20);
        } else {
            g.drawString(tier + e.getItemName(), x + 40, y + 20);
        }
        g.setColor(Color.WHITE);
        g.drawString("Level: " + (int) e.getStats()[Globals.STAT_LEVEL], x + 40, y + 40);
        g.drawString("Power: " + (int) e.getStats()[Globals.STAT_POWER], x + 40, y + 60);
        g.drawString("Defense: " + (int) e.getStats()[Globals.STAT_DEFENSE], x + 40, y + 80);
        g.drawString("Spirit: " + (int) e.getStats()[Globals.STAT_SPIRIT], x + 40, y + 100);
        int bonusY = 20;
        if (e.getStats()[Globals.STAT_ARMOR] > 0) {
            g.drawString("Armor: " + (int) e.getStats()[Globals.STAT_ARMOR], x + 40, y + 100 + bonusY);
            bonusY += 20;
        }
        if (e.getStats()[Globals.STAT_REGEN] > 0) {
            g.drawString("Regen: " + df.format(e.getStats()[Globals.STAT_REGEN]) + " HP/Sec", x + 40, y + 100 + bonusY);
            bonusY += 20;
        }
        if (e.getStats()[Globals.STAT_CRITDMG] > 0) {
            g.drawString("Critical Damage: " + df.format(e.getStats()[Globals.STAT_CRITDMG] * 100) + "%", x + 40, y + 100 + bonusY);
            bonusY += 20;
        }
        if (e.getStats()[Globals.STAT_CRITCHANCE] > 0) {
            g.drawString("Critical Chance: " + df.format(e.getStats()[Globals.STAT_CRITCHANCE] * 100) + "%", x + 40, y + 100 + bonusY);
            bonusY += 20;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            destroy = false;
            destroyConfirm = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int drItem = dragItem, drEq = dragEquip;
        dragItem = -1;
        dragEquip = -1;
        if (destroyConfirm) {
            mouseReleased_destroyConfirm(e);
            return;
        }

        super.mouseReleased(e);
        if (SwingUtilities.isLeftMouseButton(e)) {
            for (byte i = 0; i < tabs.length; i++) {
                if (tabs[i].contains(e.getPoint())) {
                    selectedTab = i;
                    destroy = false;
                    return;
                }
            }

            for (int i = 0; i < inventSlots.length; i++) {
                if (inventSlots[i].contains(e.getPoint())) {
                    if (!destroy) {
                        if (drItem != -1) {
                            ItemEquip temp = c.getInventory(selectedTab)[i];
                            c.getInventory(selectedTab)[i] = c.getInventory(selectedTab)[drItem];
                            c.getInventory(selectedTab)[drItem] = temp;
                            return;
                        }
                        if (drEq != -1) {
                            c.equipItem(drEq, i);
                            return;
                        }
                        if (c.getInventory(selectedTab)[i] != null) {
                            c.equipItem(selectedTab, i);
                            return;
                        }
                    } else {
                        c.destroyItem(selectedTab, i);
                    }
                    return;
                }
            }

            for (int i = 0; !destroy && i < equipSlots.length; i++) {
                if (equipSlots[i].contains(e.getPoint())) {
                    if (drItem != -1) {
                        if (selectedTab == i || (selectedTab == Globals.ITEM_WEAPON && i == Globals.ITEM_OFFHAND)) {
                            c.equipItem(i, drItem);
                            return;
                        }
                    }
                    if (drEq == -1 && c.getEquip()[i] != null) {
                        c.unequipItem(i);
                        return;
                    }
                }
            }

            for (int i = 0; i < destroyBox.length; i++) {
                if (destroyBox[i].contains(e.getPoint())) {
                    switch (i) {
                        case 0:
                            destroy = !destroy;
                            break;
                        case 1:
                            destroy = false;
                            destroyConfirm = true;
                            break;
                    }
                    return;
                }
            }
        }

        if (SwingUtilities.isRightMouseButton(e)) {
            if (selectedTab == Globals.ITEM_WEAPON) {
                for (int i = 0; i < inventSlots.length; i++) {
                    if (inventSlots[i].contains(e.getPoint()) && c.getInventory(selectedTab)[i] != null) {
                        if (!destroy) {
                            c.equipItem(Globals.ITEM_OFFHAND, i);
                        }
                        return;
                    }
                }
            }
        }
    }

    private void mouseReleased_destroyConfirm(MouseEvent e) {
        for (byte i = 0; i < promptBox.length; i++) {
            if (promptBox[i].contains(e.getPoint())) {
                if (i == 0) {
                    c.destroyAll(selectedTab);
                }
                destroyConfirm = false;
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
        if (destroyConfirm || destroy) {
            return;
        }
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (dragItem == -1 && dragEquip == -1) {
                for (int i = 0; i < inventSlots.length; i++) {
                    if (inventSlots[i].contains(e.getPoint()) && c.getInventory(selectedTab)[i] != null) {
                        dragItem = i;
                        return;
                    }
                }

                for (byte i = 0; i < equipSlots.length; i++) {
                    if (equipSlots[i].contains(e.getPoint()) && c.getEquip()[i] != null) {
                        dragEquip = i;
                        if (i == Globals.ITEM_OFFHAND) {
                            selectedTab = Globals.ITEM_WEAPON;
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
    public void mouseMoved(MouseEvent e) {
        mousePos = e.getPoint();
        drawItem = -1;
        drawEquip = -1;
        for (int i = 0; i < inventSlots.length; i++) {
            if (inventSlots[i].contains(e.getPoint()) && c.getInventory(selectedTab)[i] != null) {
                drawItem = i;
                return;
            }
        }

        for (byte i = 0; i < equipSlots.length; i++) {
            if (equipSlots[i].contains(e.getPoint()) && c.getEquip()[i] != null) {
                drawEquip = i;
                return;
            }
        }
    }

}
