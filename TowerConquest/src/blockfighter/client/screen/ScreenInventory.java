package blockfighter.client.screen;

import blockfighter.client.Globals;
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
 * @author Ken Kwan
 */
public class ScreenInventory extends ScreenMenu {

    private final SaveData c;
    private byte selectedTab = Globals.ITEM_WEAPON;
    private boolean destroy = false, destroyConfirm = false;
    private final Rectangle2D.Double[] inventSlots = new Rectangle2D.Double[100],
            equipSlots = new Rectangle2D.Double[Globals.NUM_EQUIP_SLOTS],
            tabs = new Rectangle2D.Double[Globals.NUM_ITEM_TABS],
            destroyBox = new Rectangle2D.Double[2],
            promptBox = new Rectangle2D.Double[2];

    private Point mousePos;

    private int drawInfoItem = -1, drawInfoEquip = -1;
    private byte charFrame = 0;

    private int dragItem = -1, dragEquip = -1;
    private int nextFrameTime = 0;
    private long lastFrameTime = 0;

    public ScreenInventory() {
        this.c = logic.getSelectedChar();
        for (int i = 0; i < this.inventSlots.length; i++) {
            this.inventSlots[i] = new Rectangle2D.Double(270 + (i * 62) - (i / 10 * 620), 30 + i / 10 * 62, 60, 60);
        }

        for (int i = 0; i < this.tabs.length; i++) {
            this.tabs[i] = new Rectangle2D.Double(230, 30 + i * 61, 30, 60);
        }

        this.equipSlots[Globals.ITEM_AMULET] = new Rectangle2D.Double(1140, 40, 60, 60);
        this.equipSlots[Globals.ITEM_BELT] = new Rectangle2D.Double(1140, 250, 60, 60);
        this.equipSlots[Globals.ITEM_OFFHAND] = new Rectangle2D.Double(1140, 180, 60, 60);
        this.equipSlots[Globals.ITEM_CHEST] = new Rectangle2D.Double(1140, 110, 60, 60);
        this.equipSlots[Globals.ITEM_HEAD] = new Rectangle2D.Double(1060, 40, 60, 60);
        this.equipSlots[Globals.ITEM_RING] = new Rectangle2D.Double(980, 40, 60, 60);
        this.equipSlots[Globals.ITEM_SHOULDER] = new Rectangle2D.Double(980, 110, 60, 60);
        this.equipSlots[Globals.ITEM_GLOVE] = new Rectangle2D.Double(980, 250, 60, 60);
        this.equipSlots[Globals.ITEM_WEAPON] = new Rectangle2D.Double(980, 180, 60, 60);
        this.equipSlots[Globals.ITEM_PANTS] = new Rectangle2D.Double(1025, 320, 60, 60);
        this.equipSlots[Globals.ITEM_SHOE] = new Rectangle2D.Double(1095, 320, 60, 60);

        for (int i = 0; i < this.destroyBox.length; i++) {
            this.destroyBox[i] = new Rectangle2D.Double(520 + i * 185, 655, 180, 30);
        }
        this.promptBox[0] = new Rectangle2D.Double(401, 400, 214, 112);
        this.promptBox[1] = new Rectangle2D.Double(665, 400, 214, 112);
    }

    @Override
    public void update() {
        final long now = logic.getTime(); // Get time now
        if (now - this.lastUpdateTime >= Globals.LOGIC_UPDATE) {
            updateParticles(particles);
            this.lastUpdateTime = now;
        }
        if (now - this.lastFrameTime >= this.nextFrameTime) {
            if (this.charFrame >= Globals.CHAR_SPRITE[Globals.PLAYER_ANIM_STATE_STAND].length - 1) {
                this.charFrame = 0;
            } else {
                this.charFrame++;
            }
            this.nextFrameTime = 250000000;
            this.lastFrameTime = now;
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        final BufferedImage bg = Globals.MENU_BG[2];
        g.drawImage(bg, 0, 0, null);

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SMALLRECT];
        g.drawImage(button, (int) this.destroyBox[0].x, (int) this.destroyBox[0].y, null);
        g.drawImage(button, (int) this.destroyBox[1].x, (int) this.destroyBox[1].y, null);
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
        if (this.destroy) {
            button = Globals.MENU_ITEMDELETE[0];
            g.drawImage(button, this.mousePos.x + 10, this.mousePos.y + 15, null);
        }
        if (this.dragItem != -1) {
            this.c.getInventory(this.selectedTab)[this.dragItem].draw(g, this.mousePos.x + 5, this.mousePos.y + 5);
        } else if (this.dragEquip != -1) {
            this.c.getEquip()[this.dragEquip].draw(g, this.mousePos.x + 5, this.mousePos.y + 5);
        }
        super.draw(g);
        drawItemInfo(g);
    }

    private void drawDestroyConfirm(final Graphics2D g) {
        if (this.destroyConfirm) {
            final BufferedImage window = Globals.MENU_WINDOW[Globals.WINDOW_DESTROYCONFIRM];
            g.drawImage(window, 265, 135, null);

            g.setFont(Globals.ARIAL_30PT);
            drawStringOutline(g, "Are you sure?", 540, 300, 2);
            g.setColor(Color.WHITE);
            g.drawString("Are you sure?", 540, 300);

            final BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_BIGRECT];
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

    private void drawItemTabs(final Graphics2D g) {
        for (int i = 0; i < this.tabs.length; i++) {
            final BufferedImage button = Globals.MENU_BUTTON[i + 5];
            g.drawImage(button, (int) this.tabs[i].x, (int) this.tabs[i].y, null);
        }
        // Tab pointer
        g.drawImage(Globals.MENU_TABPOINTER[0], 260, (int) this.tabs[this.selectedTab].y, null);
        g.drawImage(Globals.MENU_TABPOINTER[1], (int) this.tabs[this.selectedTab].x, (int) this.tabs[this.selectedTab].y, null);

    }

    private void drawItemInfo(final Graphics2D g) {
        if (this.destroyConfirm) {
            return;
        }
        if (this.drawInfoItem != -1) {
            drawItemInfo(g, this.inventSlots[this.drawInfoItem], this.c.getInventory(this.selectedTab)[this.drawInfoItem]);
        } else if (this.drawInfoEquip != -1) {
            drawItemInfo(g, this.equipSlots[this.drawInfoEquip], this.c.getEquip()[this.drawInfoEquip]);
        }
    }

    private void drawStats(final Graphics2D g) {
        g.setFont(Globals.ARIAL_15PT);
        final int statY = 440, statX = 935;
        final double[] bs = this.c.getBaseStats(), bonus = this.c.getBonusStats(), total = this.c.getTotalStats();
        drawStringOutline(g, "Level: " + (int) bs[Globals.STAT_LEVEL], statX, statY, 1);
        drawStringOutline(g, "Power: " + (int) bs[Globals.STAT_POWER] + " + " + (int) bonus[Globals.STAT_POWER], statX, statY + 25, 1);
        drawStringOutline(g, "Defense: " + (int) bs[Globals.STAT_DEFENSE] + " + " + (int) bonus[Globals.STAT_DEFENSE], statX, statY + 50,
                1);
        drawStringOutline(g, "Spirit: " + (int) bs[Globals.STAT_SPIRIT] + " + " + (int) bonus[Globals.STAT_SPIRIT], statX, statY + 75, 1);

        drawStringOutline(g, "HP: " + (int) total[Globals.STAT_MAXHP], statX, statY + 105, 1);
        drawStringOutline(g, "Damage: " + (int) total[Globals.STAT_MINDMG] + " - " + (int) total[Globals.STAT_MAXDMG], statX, statY + 130,
                1);
        drawStringOutline(g, "Armor: " + (int) bs[Globals.STAT_ARMOR] + " + " + (int) bonus[Globals.STAT_ARMOR], statX, statY + 155, 1);
        drawStringOutline(g,
                "Regen: " + this.df.format(bs[Globals.STAT_REGEN]) + " + " + this.df.format(bonus[Globals.STAT_REGEN]) + " HP/Sec",
                statX, statY + 180, 1);
        drawStringOutline(g, "Critical Hit Chance: " + this.df.format(bs[Globals.STAT_CRITCHANCE] * 100) + " + "
                + this.df.format(bonus[Globals.STAT_CRITCHANCE] * 100) + "%", statX, statY + 205, 1);
        drawStringOutline(g, "Critical Hit Damage: " + this.df.format((1 + bs[Globals.STAT_CRITDMG]) * 100) + " + "
                + this.df.format(bonus[Globals.STAT_CRITDMG] * 100) + "%", statX, statY + 230, 1);

        g.setColor(Color.WHITE);
        g.drawString("Level: " + (int) bs[Globals.STAT_LEVEL], statX, statY);
        g.drawString("Power: " + (int) bs[Globals.STAT_POWER] + " + " + (int) bonus[Globals.STAT_POWER], statX, statY + 25);
        g.drawString("Defense: " + (int) bs[Globals.STAT_DEFENSE] + " + " + (int) bonus[Globals.STAT_DEFENSE], statX, statY + 50);
        g.drawString("Spirit: " + (int) bs[Globals.STAT_SPIRIT] + " + " + (int) bonus[Globals.STAT_SPIRIT], statX, statY + 75);

        g.drawString("HP: " + (int) total[Globals.STAT_MAXHP], statX, statY + 105);
        g.drawString("Damage: " + (int) total[Globals.STAT_MINDMG] + " - " + (int) total[Globals.STAT_MAXDMG], statX, statY + 130);
        g.drawString("Armor: " + (int) bs[Globals.STAT_ARMOR] + " + " + (int) bonus[Globals.STAT_ARMOR], statX, statY + 155);
        g.drawString("Regen: " + this.df.format(bs[Globals.STAT_REGEN]) + " + " + this.df.format(bonus[Globals.STAT_REGEN]) + " HP/Sec",
                statX,
                statY + 180);
        g.drawString("Critical Hit Chance: " + this.df.format(bs[Globals.STAT_CRITCHANCE] * 100) + " + "
                + this.df.format(bonus[Globals.STAT_CRITCHANCE] * 100) + "%", statX, statY + 205);
        g.drawString("Critical Hit Damage: " + this.df.format((1 + bs[Globals.STAT_CRITDMG]) * 100) + " + "
                + this.df.format(bonus[Globals.STAT_CRITDMG] * 100) + "%", statX, statY + 230);

    }

    private void drawSlots(final Graphics2D g) {
        final BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SLOT];
        final BufferedImage character = Globals.CHAR_SPRITE[Globals.PLAYER_ANIM_STATE_STAND][this.charFrame];

        final int x = 1070 + character.getWidth() / 2, y = 200 + character.getHeight();
        if (this.c.getEquip()[Globals.ITEM_OFFHAND] != null) {
            this.c.getEquip()[Globals.ITEM_OFFHAND].drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_STAND, this.charFrame, Globals.RIGHT, true);
        }
        g.drawImage(character, 1070, 170, null);

        if (this.c.getEquip()[Globals.ITEM_CHEST] != null) {
            this.c.getEquip()[Globals.ITEM_CHEST].drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_STAND, this.charFrame, Globals.RIGHT);
        }
        if (this.c.getEquip()[Globals.ITEM_SHOULDER] != null) {
            this.c.getEquip()[Globals.ITEM_SHOULDER].drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_STAND, this.charFrame, Globals.RIGHT);
        }

        if (this.c.getEquip()[Globals.ITEM_PANTS] != null) {
            this.c.getEquip()[Globals.ITEM_PANTS].drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_STAND, this.charFrame, Globals.RIGHT);
        }
        if (this.c.getEquip()[Globals.ITEM_SHOE] != null) {
            this.c.getEquip()[Globals.ITEM_SHOE].drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_STAND, this.charFrame, Globals.RIGHT);
        }
        if (this.c.getEquip()[Globals.ITEM_WEAPON] != null) {
            this.c.getEquip()[Globals.ITEM_WEAPON].drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_STAND, this.charFrame, Globals.RIGHT);
        }
        if (this.c.getEquip()[Globals.ITEM_GLOVE] != null) {
            this.c.getEquip()[Globals.ITEM_GLOVE].drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_STAND, this.charFrame, Globals.RIGHT);
        }

        // Inventory
        for (int i = 0; i < this.c.getInventory(this.selectedTab).length; i++) {
            g.drawImage(button, (int) this.inventSlots[i].x, (int) this.inventSlots[i].y, null);
            if (this.c.getInventory(this.selectedTab)[i] != null) {
                this.c.getInventory(this.selectedTab)[i].draw(g, (int) this.inventSlots[i].x, (int) this.inventSlots[i].y);
            }
            if (this.selectedTab == Globals.ITEM_WEAPON) {
                g.setFont(Globals.ARIAL_15PT);
                drawStringOutline(g, "Right Click to equip as Offhand", 280, 682, 1);
                g.setColor(new Color(255, 130, 0));
                g.drawString("Right Click to equip as Offhand", 280, 682);
            }
        }

        // Equipment
        for (int i = 0; i < this.equipSlots.length; i++) {
            g.drawImage(button, (int) this.equipSlots[i].x, (int) this.equipSlots[i].y, null);
            if (this.c.getEquip()[i] != null) {
                this.c.getEquip()[i].draw(g, (int) this.equipSlots[i].x, (int) this.equipSlots[i].y);
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
            drawStringOutline(g, s, (int) this.equipSlots[i].x + 2, (int) this.equipSlots[i].y + 58, 1);
            g.setColor(Color.WHITE);
            g.drawString(s, (int) this.equipSlots[i].x + 2, (int) this.equipSlots[i].y + 58);
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
        final int drItem = this.dragItem, drEq = this.dragEquip;
        this.dragItem = -1;
        this.dragEquip = -1;
        if (this.destroyConfirm) {
            mouseReleased_destroyConfirm(e);
            return;
        }

        super.mouseReleased(e);
        if (SwingUtilities.isLeftMouseButton(e)) {
            for (byte i = 0; i < this.tabs.length; i++) {
                if (drItem == -1 && drEq == -1 && this.tabs[i].contains(e.getPoint())) {
                    this.selectedTab = i;
                    this.destroy = false;
                    return;
                }
            }

            for (int i = 0; i < this.inventSlots.length; i++) {
                if (this.inventSlots[i].contains(e.getPoint())) {
                    if (!this.destroy) {
                        if (drItem != -1) {
                            final ItemEquip temp = this.c.getInventory(this.selectedTab)[i];
                            this.c.getInventory(this.selectedTab)[i] = this.c.getInventory(this.selectedTab)[drItem];
                            this.c.getInventory(this.selectedTab)[drItem] = temp;
                            return;
                        }
                        if (drEq != -1) {
                            this.c.equipItem(drEq, i);
                            return;
                        }
                        if (this.c.getInventory(this.selectedTab)[i] != null) {
                            this.c.equipItem(this.selectedTab, i);
                            return;
                        }
                    } else {
                        this.c.destroyItem(this.selectedTab, i);
                    }
                    return;
                }
            }

            for (byte i = 0; !this.destroy && i < this.equipSlots.length; i++) {
                if (this.equipSlots[i].contains(e.getPoint())) {
                    if (drItem != -1) {
                        if (this.selectedTab == i || (this.selectedTab == Globals.ITEM_WEAPON && i == Globals.ITEM_OFFHAND)) {
                            this.c.equipItem(i, drItem);
                            return;
                        }
                    }
                    if (drItem == -1 && drEq == -1 && this.c.getEquip()[i] != null) {
                        this.c.unequipItem(i);
                        return;
                    }
                }
            }

            for (int i = 0; i < this.destroyBox.length; i++) {
                if (this.destroyBox[i].contains(e.getPoint())) {
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
            if (this.selectedTab == Globals.ITEM_WEAPON) {
                for (int i = 0; i < this.inventSlots.length; i++) {
                    if (this.inventSlots[i].contains(e.getPoint()) && this.c.getInventory(this.selectedTab)[i] != null) {
                        if (!this.destroy) {
                            this.c.equipItem(Globals.ITEM_OFFHAND, i);
                        }
                        return;
                    }
                }
            }
        }
    }

    private void mouseReleased_destroyConfirm(final MouseEvent e) {
        for (byte i = 0; i < this.promptBox.length; i++) {
            if (this.promptBox[i].contains(e.getPoint())) {
                if (i == 0) {
                    this.c.destroyAll(this.selectedTab);
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
        mouseMoved(e);
        if (this.destroyConfirm || this.destroy) {
            return;
        }
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (this.dragItem == -1 && this.dragEquip == -1) {
                for (int i = 0; i < this.inventSlots.length; i++) {
                    if (this.inventSlots[i].contains(e.getPoint()) && this.c.getInventory(this.selectedTab)[i] != null) {
                        this.dragItem = i;
                        return;
                    }
                }

                for (byte i = 0; i < this.equipSlots.length; i++) {
                    if (this.equipSlots[i].contains(e.getPoint()) && this.c.getEquip()[i] != null) {
                        this.dragEquip = i;
                        if (i == Globals.ITEM_OFFHAND) {
                            this.selectedTab = Globals.ITEM_WEAPON;
                        } else {
                            this.selectedTab = i;
                        }
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        this.mousePos = e.getPoint();
        this.drawInfoItem = -1;
        this.drawInfoEquip = -1;
        for (int i = 0; i < this.inventSlots.length; i++) {
            if (this.inventSlots[i].contains(e.getPoint()) && this.c.getInventory(this.selectedTab)[i] != null) {
                this.drawInfoItem = i;
                return;
            }
        }

        for (byte i = 0; i < this.equipSlots.length; i++) {
            if (this.equipSlots[i].contains(e.getPoint()) && this.c.getEquip()[i] != null) {
                this.drawInfoEquip = i;
                return;
            }
        }
    }

    @Override
    public void unload() {
    }

}
