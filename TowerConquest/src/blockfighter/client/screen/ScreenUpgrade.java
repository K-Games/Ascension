package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.SaveData;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.items.ItemUpgrade;
import blockfighter.client.entities.particles.ParticleMenuUpgrade;
import blockfighter.client.entities.particles.ParticleMenuUpgradeEnd;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author Ken Kwan
 */
public class ScreenUpgrade extends ScreenMenu {

    private SaveData c;
    private boolean destroy = false, destroyConfirm = false;

    private int selectEquip = -1;
    private int selectUpgrade = -1;
    private int dragItem = -1;

    private Rectangle2D.Double[] inventSlots = new Rectangle2D.Double[100],
            equipSlots = new Rectangle2D.Double[Globals.NUM_EQUIP_SLOTS],
            destroyBox = new Rectangle2D.Double[2],
            upgradeBox = new Rectangle2D.Double[2],
            promptBox = new Rectangle2D.Double[2];
    private Rectangle2D.Double combineBox;

    private Point mousePos;

    private int drawItem = -1, drawEquip = -1, drawSelect = -1;

    private byte charFrame = 0;
    private double nextFrameTime = 0, upgradeTime = 0;
    private boolean upgrading = false;
    private int upPart = 0;

    public ScreenUpgrade(LogicModule l) {
        super(l);
        c = l.getSelectedChar();
        for (int i = 0; i < inventSlots.length; i++) {
            inventSlots[i] = new Rectangle2D.Double(255 + (i * 62) - (i / 10 * 620), 30 + i / 10 * 62, 60, 60);
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

        upgradeBox[0] = new Rectangle2D.Double(980, 450, 60, 60);
        upgradeBox[1] = new Rectangle2D.Double(1140, 450, 60, 60);

        for (int i = 0; i < destroyBox.length; i++) {
            destroyBox[i] = new Rectangle2D.Double(520 + i * 185, 655, 180, 30);
        }
        promptBox[0] = new Rectangle2D.Double(401, 400, 214, 112);
        promptBox[1] = new Rectangle2D.Double(665, 400, 214, 112);

        combineBox = new Rectangle2D.Double(1000, 580, 180, 30);
    }

    @Override
    public void update() {
        double now = System.nanoTime(); //Get time now
        if (now - lastUpdateTime >= Globals.LOGIC_UPDATE) {

            nextFrameTime -= Globals.LOGIC_UPDATE;
            if (nextFrameTime <= 0) {
                if (charFrame >= 8) {
                    charFrame = 0;
                } else {
                    charFrame++;
                }
                nextFrameTime = 150000000;
            }

            if (upgradeTime > 0) {
                upgradeTime -= Globals.LOGIC_UPDATE / 1000000;
                if (upgradeTime > 1000) {
                    Random rng = new Random();
                    for (int i = 0; i < 5; i++) {
                        particles.put(upPart + 2, new ParticleMenuUpgrade(logic, upPart + 2, (int) upgradeBox[0].x + 30, (int) upgradeBox[0].y + 30, upPart % 4, rng.nextInt(5) - 16, rng.nextInt(10) - 5));
                        upPart++;
                    }
                }
            }
            if (upgrading && upgradeTime <= 0) {
                Random rng = new Random();
                if (ItemUpgrade.rollUpgrade(c.getUpgrades()[selectUpgrade], c.getEquip()[selectEquip])) {
                    c.getEquip()[selectEquip].addUpgrade(1);
                    for (int i = 0; i < 20; i++) {
                        particles.put(upPart + 2, new ParticleMenuUpgradeEnd(logic, upPart + 2, (int) upgradeBox[1].x + 30, (int) upgradeBox[1].y + 30, 3, rng.nextInt(10) - 5, -5 - rng.nextInt(3)));
                        upPart++;
                    }
                } else {
                    for (int i = 0; i < 20; i++) {
                        particles.put(upPart + 2, new ParticleMenuUpgradeEnd(logic, upPart + 2, (int) upgradeBox[1].x + 30, (int) upgradeBox[1].y + 30, 2, rng.nextInt(10) - 5, -5 - rng.nextInt(3)));
                        upPart++;
                    }
                }
                c.destroyItem(selectUpgrade);
                c.calcStats();
                SaveData.saveData(c.getSaveNum(), c);
                selectUpgrade = -1;
                upgrading = false;
            }
            updateParticles(particles);
            lastUpdateTime = now;
        }
        try {
            Thread.sleep(0, 1);
        } catch (InterruptedException ex) {
            Logger.getLogger(ScreenInventory.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void draw(Graphics2D g) {
        BufferedImage bg = Globals.MENU_BG[2];
        g.drawImage(bg, 0, 0, null);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SMALLRECT];
        g.drawImage(button, (int) destroyBox[0].x, (int) destroyBox[0].y, null);
        g.drawImage(button, (int) destroyBox[1].x, (int) destroyBox[1].y, null);
        g.drawImage(button, (int) combineBox.x, (int) combineBox.y, null);

        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, "Destroy Item", 560, 682, 1);
        drawStringOutline(g, "Destroy All", 750, 682, 1);
        drawStringOutline(g, "Enhance", 1053, 607, 1);
        g.setColor(Color.WHITE);
        g.drawString("Destroy Item", 560, 682);
        g.drawString("Destroy All", 750, 682);
        g.drawString("Enhance", 1053, 607);

        int selTemp1 = selectUpgrade, selTemp2 = selectEquip;
        if (selTemp1 >= 0 && selTemp2 >= 0) {
            g.setFont(Globals.ARIAL_15PT);
            drawStringOutline(g, "Chance of Success: " + df.format(ItemUpgrade.upgradeChance(c.getUpgrades()[selTemp1], c.getEquip()[selTemp2]) * 100) + "%", 1000, 550, 1);
            g.setColor(Color.WHITE);
            g.drawString("Chance of Success: " + df.format(ItemUpgrade.upgradeChance(c.getUpgrades()[selTemp1], c.getEquip()[selTemp2]) * 100) + "%", 1000, 550);
        }

        drawSlots(g);
        drawDestroyConfirm(g);
        if (destroy) {
            button = Globals.MENU_ITEMDELETE[0];
            g.drawImage(button, mousePos.x + 10, mousePos.y + 15, null);
        }
        drawMenuButton(g);
        if (dragItem != -1) {
            c.getUpgrades()[dragItem].draw(g, mousePos.x + 5, mousePos.y + 5);
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

    private void drawSlots(Graphics2D g) {
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SLOT];
        BufferedImage character = Globals.CHAR_SPRITE[Globals.PLAYER_STATE_STAND][charFrame];

        g.drawImage(character, 1050, 100, null);
        int x = 1050 + character.getWidth() / 2, y = 100 + character.getHeight();
        if (c.getEquip()[Globals.ITEM_OFFHAND] != null) {
            c.getEquip()[Globals.ITEM_OFFHAND].drawIngame(g, x, y, Globals.PLAYER_STATE_STAND, charFrame, Globals.RIGHT, true);
        }
        if (c.getEquip()[Globals.ITEM_CHEST] != null) {
            c.getEquip()[Globals.ITEM_CHEST].drawIngame(g, x, y, Globals.PLAYER_STATE_STAND, charFrame, Globals.RIGHT);
        }
        if (c.getEquip()[Globals.ITEM_SHOULDER] != null) {
            c.getEquip()[Globals.ITEM_SHOULDER].drawIngame(g, x, y, Globals.PLAYER_STATE_STAND, charFrame, Globals.RIGHT);
        }

        if (c.getEquip()[Globals.ITEM_PANTS] != null) {
            c.getEquip()[Globals.ITEM_PANTS].drawIngame(g, x, y, Globals.PLAYER_STATE_STAND, charFrame, Globals.RIGHT);
        }
        if (c.getEquip()[Globals.ITEM_SHOE] != null) {
            c.getEquip()[Globals.ITEM_SHOE].drawIngame(g, x, y, Globals.PLAYER_STATE_STAND, charFrame, Globals.RIGHT);
        }
        if (c.getEquip()[Globals.ITEM_WEAPON] != null) {
            c.getEquip()[Globals.ITEM_WEAPON].drawIngame(g, x, y, Globals.PLAYER_STATE_STAND, charFrame, Globals.RIGHT);
        }
        if (c.getEquip()[Globals.ITEM_GLOVE] != null) {
            c.getEquip()[Globals.ITEM_GLOVE].drawIngame(g, x, y, Globals.PLAYER_STATE_STAND, charFrame, Globals.RIGHT);
        }

        //Inventory
        for (int i = 0; i < c.getUpgrades().length; i++) {
            g.drawImage(button, (int) inventSlots[i].x, (int) inventSlots[i].y, null);
            if (c.getUpgrades()[i] != null) {
                c.getUpgrades()[i].draw(g, (int) inventSlots[i].x, (int) inventSlots[i].y);
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

        //upgrades
        for (Rectangle2D.Double box : upgradeBox) {
            g.drawImage(button, (int) box.x, (int) box.y, null);
        }

        if (selectUpgrade > -1) {
            if (c.getUpgrades()[selectUpgrade] != null) {
                c.getUpgrades()[selectUpgrade].draw(g, (int) upgradeBox[0].x, (int) upgradeBox[0].y);
            }
        }
        if (selectEquip > -1) {
            if (c.getEquip()[selectEquip] != null) {
                c.getEquip()[selectEquip].draw(g, (int) upgradeBox[1].x, (int) upgradeBox[1].y);
            }
        }
    }

    private void drawItemInfo(Graphics2D g) {
        if (destroyConfirm) {
            return;
        }
        if (drawItem > -1) {
            drawItemInfo(g, inventSlots[drawItem], c.getUpgrades()[drawItem]);
        } else if (drawEquip > -1) {
            drawItemInfo(g, equipSlots[drawEquip], c.getEquip()[drawEquip]);
        } else if (drawSelect == 0 && selectUpgrade > -1) {
            drawItemInfo(g, upgradeBox[0], c.getUpgrades()[selectUpgrade]);
        } else if (drawSelect == 1 && selectEquip > -1) {
            drawItemInfo(g, upgradeBox[1], c.getEquip()[selectEquip]);
        }
    }

    private void drawItemInfo(Graphics2D g, Rectangle2D.Double box, ItemUpgrade e) {
        if (e == null) {
            return;
        }
        g.setColor(new Color(30, 30, 30, 185));
        int y = (int) box.y;
        int x = (int) box.x;
        int boxHeight = 60, boxWidth = 280;

        if (y + boxHeight > 720) {
            y = 700 - boxHeight;
        }

        if (x + 30 + boxWidth > 1280) {
            x = 1240 - boxWidth;
        }
        g.fillRect(x + 30, y, boxWidth, boxHeight);
        g.setColor(Color.BLACK);
        g.drawRect(x + 30, y, boxWidth, boxHeight);
        g.drawRect(x + 31, y + 1, boxWidth - 2, boxHeight - 2);

        g.setFont(Globals.ARIAL_15PT);
        g.setColor(Color.WHITE);
        g.drawString("Level " + e.getLevel() + " " + e.getItemName(), x + 40, y + 20);
        g.drawString("Use this to enhance any equipment.", x + 40, y + 40);

    }

    private void drawItemInfo(Graphics2D g, Rectangle2D.Double box, ItemEquip e) {
        if (e == null) {
            return;
        }
        g.setColor(new Color(30, 30, 30, 185));
        int y = (int) box.y;
        int x = (int) box.x;
        int boxHeight = 120, boxWidth = 200;

        if (e.getTotalStats()[Globals.STAT_REGEN] > 0) {
            boxHeight += 20;
        }
        if (e.getTotalStats()[Globals.STAT_ARMOR] > 0) {
            boxHeight += 20;
        }
        if (e.getTotalStats()[Globals.STAT_CRITDMG] > 0) {
            boxHeight += 20;
        }
        if (e.getTotalStats()[Globals.STAT_CRITCHANCE] > 0) {
            boxHeight += 20;
        }
        if (y + boxHeight > 720) {
            y = 700 - boxHeight;
        }

        if (x + 30 + boxWidth > 1280) {
            x = 1240 - boxWidth;
        }
        g.fillRect(x + 30, y, boxWidth, boxHeight);
        g.setColor(Color.BLACK);
        g.drawRect(x + 30, y, boxWidth, boxHeight);
        g.drawRect(x + 31, y + 1, boxWidth - 2, boxHeight - 2);

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
        g.drawString("Level: " + (int) e.getTotalStats()[Globals.STAT_LEVEL], x + 40, y + 40);
        g.drawString("Power: " + (int) e.getTotalStats()[Globals.STAT_POWER], x + 40, y + 60);
        g.drawString("Defense: " + (int) e.getTotalStats()[Globals.STAT_DEFENSE], x + 40, y + 80);
        g.drawString("Spirit: " + (int) e.getTotalStats()[Globals.STAT_SPIRIT], x + 40, y + 100);
        int bonusY = 20;
        if (e.getTotalStats()[Globals.STAT_ARMOR] > 0) {
            g.drawString("Armor: " + (int) e.getTotalStats()[Globals.STAT_ARMOR], x + 40, y + 100 + bonusY);
            bonusY += 20;
        }
        if (e.getTotalStats()[Globals.STAT_REGEN] > 0) {
            g.drawString("Regen: " + df.format(e.getTotalStats()[Globals.STAT_REGEN]) + " HP/Sec", x + 40, y + 100 + bonusY);
            bonusY += 20;
        }
        if (e.getTotalStats()[Globals.STAT_CRITDMG] > 0) {
            g.drawString("Critical Damage: " + df.format(e.getTotalStats()[Globals.STAT_CRITDMG] * 100) + "%", x + 40, y + 100 + bonusY);
            bonusY += 20;
        }
        if (e.getTotalStats()[Globals.STAT_CRITCHANCE] > 0) {
            g.drawString("Critical Chance: " + df.format(e.getTotalStats()[Globals.STAT_CRITCHANCE] * 100) + "%", x + 40, y + 100 + bonusY);
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
        int drItem = dragItem;
        dragItem = -1;
        if (destroyConfirm) {
            mouseReleased_destroyConfirm(e);
            return;
        }
        super.mouseReleased(e);
        if (upgrading) {
            return;
        }
        if (SwingUtilities.isLeftMouseButton(e)) {
            for (int i = 0; i < inventSlots.length; i++) {
                if (inventSlots[i].contains(e.getPoint())) {
                    if (!destroy) {
                        if (drItem != -1) {
                            ItemUpgrade temp = c.getUpgrades()[i];
                            c.getUpgrades()[i] = c.getUpgrades()[drItem];
                            c.getUpgrades()[drItem] = temp;
                            return;
                        }
                        //set upgrade item
                        if (c.getUpgrades()[i] != null) {
                            selectUpgrade = i;
                            return;
                        }
                    } else {
                        //Destroy upgrade item
                        if (selectUpgrade == i) {
                            selectUpgrade = -1;
                            //drawSelect = -1;
                        }
                        c.destroyItem(i);
                        return;
                    }
                }
            }

            for (int i = 0; !destroy && i < equipSlots.length; i++) {
                if (equipSlots[i].contains(e.getPoint()) && c.getEquip()[i] != null) {
                    //Set upgrading item
                    selectEquip = i;
                    return;
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

            if (!upgrading && combineBox.contains(e.getPoint())) {
                if (selectUpgrade >= 0 && selectEquip >= 0) {
                    upPart = 0;
                    upgrading = true;
                    upgradeTime = 1500;
                }
            }
        }
    }

    private void mouseReleased_destroyConfirm(MouseEvent e) {
        for (byte i = 0; i < promptBox.length; i++) {
            if (promptBox[i].contains(e.getPoint())) {
                if (i == 0) {
                    selectUpgrade = -1;
                    c.destroyAllUpgrade();
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
        if (destroyConfirm || destroy || upgrading) {
            return;
        }
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (dragItem == -1) {
                for (int i = 0; i < inventSlots.length; i++) {
                    if (inventSlots[i].contains(e.getPoint()) && c.getUpgrades()[i] != null) {
                        dragItem = i;
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
        drawSelect = -1;

        if (upgradeBox[0].contains(e.getPoint()) && selectUpgrade > -1) {
            drawSelect = 0;
            return;
        }

        if (upgradeBox[1].contains(e.getPoint()) && selectEquip > -1) {
            drawSelect = 1;
            return;
        }

        for (int i = 0; i < inventSlots.length; i++) {
            if (inventSlots[i].contains(e.getPoint()) && c.getUpgrades()[i] != null) {
                drawItem = i;
                return;
            }
        }

        for (int i = 0; drawItem < 0 && i < equipSlots.length; i++) {
            if (equipSlots[i].contains(e.getPoint()) && c.getEquip()[i] != null) {
                drawEquip = i;
                return;
            }
        }
    }

    @Override
    public void unload() {
    }

}
