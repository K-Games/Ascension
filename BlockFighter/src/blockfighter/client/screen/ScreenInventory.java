package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.LogicModule;
import blockfighter.client.SaveData;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

/**
 *
 * @author Ken
 */
public class ScreenInventory extends ScreenMenu {

    private SaveData c;
    private byte selectedTab = Globals.ITEM_WEAPON;
    private Rectangle2D.Double[] inventSlots = new Rectangle2D.Double[100];
    private Rectangle2D.Double[] equipSlots = new Rectangle2D.Double[Globals.NUM_EQUIP_SLOTS];
    private Rectangle2D.Double[] tabs = new Rectangle2D.Double[Globals.NUM_ITEM_TYPES];

    private int drawItem = -1, drawEquip = -1;
    DecimalFormat df = new DecimalFormat("0.00");
    private int charFrame = 0;
    private double nextFrameTime = 0;

    public ScreenInventory(LogicModule l) {
        super(l);
        c = l.getSelectedChar();
        for (int i = 0; i < inventSlots.length; i++) {
            inventSlots[i] = new Rectangle2D.Double(270 + (i * 62) - (i / 10 * 620), 50 + i / 10 * 62, 60, 60);
        }

        for (int i = 0; i < tabs.length; i++) {
            tabs[i] = new Rectangle2D.Double(230, 50 + i * 61, 30, 60);
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
    }

    @Override
    public void update() {
        super.update();
        nextFrameTime -= Globals.LOGIC_UPDATE;
        if (nextFrameTime <= 0) {
            if (charFrame >= 8) {
                charFrame = 0;
            } else {
                charFrame++;
            }
            nextFrameTime = 150000000;
        }
    }

    @Override
    public void draw(Graphics g) {
        BufferedImage bg = Globals.MENU_BG[2];
        g.drawImage(bg, 0, 0, null);

        super.draw(g);
        drawMenuButton(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        
        drawStats(g);
        drawSlots(g);
        
        for (int i = 0; i < tabs.length; i++) {
            BufferedImage button = Globals.MENU_BUTTON[i + 5];
            g.drawImage(button, (int) tabs[i].x, (int) tabs[i].y, null);//Weapon
        }
        g.drawImage(Globals.MENU_TABPOINTER[0], 260,(int)tabs[selectedTab].y,null);
        
        if (drawItem > -1) {
            drawItemInfo(g, inventSlots[drawItem], c.getInventory(selectedTab)[drawItem]);
        }
        if (drawEquip > -1) {
            drawItemInfo(g, equipSlots[drawEquip], c.getEquip()[drawEquip]);
        }
    }

    private void drawStats(Graphics g) {
        g.setFont(Globals.ARIAL_15PT);
        int statX = 440, statY = 935;
        double[] bs = c.getBaseStats(), bonus = c.getBonusStats(), total = c.getStats();
        drawStringOutline(g, "Level: " + (int) bs[Globals.STAT_LEVEL], statY, statX, 1);
        drawStringOutline(g, "Power: " + (int) bs[Globals.STAT_POWER] + " + " + (int) bonus[Globals.STAT_POWER], statY, statX + 25, 1);
        drawStringOutline(g, "Defense: " + (int) bs[Globals.STAT_DEFENSE] + " + " + (int) bonus[Globals.STAT_DEFENSE], statY, statX + 50, 1);
        drawStringOutline(g, "Spirit: " + (int) bs[Globals.STAT_SPIRIT] + " + " + (int) bonus[Globals.STAT_SPIRIT], statY, statX + 75, 1);

        drawStringOutline(g, "HP: " + (int) total[Globals.STAT_MAXHP], statY, statX + 105, 1);
        drawStringOutline(g, "Damage: " + (int) total[Globals.STAT_MINDMG] + " - " + (int) total[Globals.STAT_MAXDMG], statY, statX + 130, 1);
        drawStringOutline(g, "Armor: " + (int) bs[Globals.STAT_ARMOR] + " + " + (int) bonus[Globals.STAT_ARMOR], statY, statX + 155, 1);
        drawStringOutline(g, "Regen: " + df.format(bs[Globals.STAT_REGEN]) + " + " + df.format((int) bonus[Globals.STAT_REGEN]) + " HP/Sec", statY, statX + 180, 1);
        drawStringOutline(g, "Critical Hit Chance: " + df.format(bs[Globals.STAT_CRITCHANCE] * 100) + " + " + df.format(bonus[Globals.STAT_CRITCHANCE] * 100) + "%", statY, statX + 205, 1);
        drawStringOutline(g, "Critical Hit Damage: " + df.format(bs[Globals.STAT_CRITDMG] * 100) + " + " + df.format(bonus[Globals.STAT_CRITDMG] * 100) + "%", statY, statX + 230, 1);

        g.setColor(Color.WHITE);
        g.drawString("Level: " + (int) bs[Globals.STAT_LEVEL], statY, statX);
        g.drawString("Power: " + (int) bs[Globals.STAT_POWER] + " + " + (int) bonus[Globals.STAT_POWER], statY, statX + 25);
        g.drawString("Defense: " + (int) bs[Globals.STAT_DEFENSE] + " + " + (int) bonus[Globals.STAT_DEFENSE], statY, statX + 50);
        g.drawString("Spirit: " + (int) bs[Globals.STAT_SPIRIT] + " + " + (int) bonus[Globals.STAT_SPIRIT], statY, statX + 75);

        g.drawString("HP: " + (int) total[Globals.STAT_MAXHP], statY, statX + 105);
        g.drawString("Damage: " + (int) total[Globals.STAT_MINDMG] + " - " + (int) total[Globals.STAT_MAXDMG], statY, statX + 130);
        g.drawString("Armor: " + (int) bs[Globals.STAT_ARMOR] + " + " + (int) bonus[Globals.STAT_ARMOR], statY, statX + 155);
        g.drawString("Regen: " + df.format(bs[Globals.STAT_REGEN]) + " + " + df.format((int) bonus[Globals.STAT_REGEN]) + " HP/Sec", statY, statX + 180);
        g.drawString("Critical Hit Chance: " + df.format(bs[Globals.STAT_CRITCHANCE] * 100) + " + " + df.format(bonus[Globals.STAT_CRITCHANCE] * 100) + "%", statY, statX + 205);
        g.drawString("Critical Hit Damage: " + df.format(bs[Globals.STAT_CRITDMG] * 100) + " + " + df.format(bonus[Globals.STAT_CRITDMG] * 100) + "%", statY, statX + 230);

    }

    private void drawSlots(Graphics g) {
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_ITEMSLOT];
        BufferedImage character = Globals.CHAR_SPRITE[Globals.PLAYER_STATE_STAND][charFrame];

        //Inventory
        for (int i = 0; i < c.getInventory(selectedTab).length; i++) {
            g.drawImage(button, (int) inventSlots[i].x, (int) inventSlots[i].y, null);
            if (c.getInventory(selectedTab)[i] != null) {
                c.getInventory(selectedTab)[i].draw(g, (int) inventSlots[i].x, (int) inventSlots[i].y);
            }
        }

        //Equipment
        for (int i = 0; i < equipSlots.length; i++) {
            g.drawImage(button, (int) equipSlots[i].x, (int) equipSlots[i].y, null);
            c.getEquip()[i].draw(g, (int) equipSlots[i].x, (int) equipSlots[i].y);
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

    private void drawItemInfo(Graphics g, Rectangle2D.Double box, ItemEquip e) {
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
        g.setColor(Color.WHITE);
        switch (e.getTier()) {
            case ItemEquip.TIER_COMMON:
                g.drawString("Common " + e.getItemName(), x + 40, y + 20);
                break;
            case ItemEquip.TIER_UNCOMMON:
                g.drawString("Uncommon " + e.getItemName(), x + 40, y + 20);
                break;
            case ItemEquip.TIER_RARE:
                g.drawString("Rare " + e.getItemName(), x + 40, y + 20);
                break;
            case ItemEquip.TIER_RUNIC:
                g.drawString("Runic " + e.getItemName(), x + 40, y + 20);
                break;
            case ItemEquip.TIER_LEGENDARY:
                g.drawString("Legendary " + e.getItemName(), x + 40, y + 20);
                break;
            case ItemEquip.TIER_ARCHAIC:
                g.drawString("Archaic " + e.getItemName(), x + 40, y + 20);
                break;
        }
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

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        for (byte i = 0; i < tabs.length; i++) {
            if (tabs[i].contains(e.getPoint())) {
                selectedTab = i;
                break;
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

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        drawItem = -1;
        drawEquip = -1;
        for (int i = 0; i < inventSlots.length; i++) {
            if (inventSlots[i].contains(e.getPoint()) && c.getInventory(selectedTab)[i] != null) {
                drawItem = i;
            }
        }

        for (int i = 0; drawItem < 0 && i < equipSlots.length; i++) {
            if (equipSlots[i].contains(e.getPoint()) && c.getEquip()[i] != null) {
                drawEquip = i;
            }
        }
    }

}
