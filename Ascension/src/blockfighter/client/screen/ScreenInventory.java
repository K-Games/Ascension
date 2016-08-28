package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.SaveData;
import blockfighter.client.entities.items.ItemEquip;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

public class ScreenInventory extends ScreenMenu {

    private static final int EQUIP_BOX_X = 980, EQUIP_BOX_Y = 40;
    private static final int STAT_BOX_X = 935, STAT_BOX_Y = 440;

    private final SaveData c;
    private static byte selectedTab = Globals.ITEM_WEAPON;
    private boolean destroy = false, destroyConfirm = false;
    private static final Rectangle2D.Double[] INVENTORY_SLOTS = new Rectangle2D.Double[100],
            EQUIP_SLOTS = new Rectangle2D.Double[Globals.NUM_EQUIP_SLOTS],
            ITEM_TABS = new Rectangle2D.Double[Globals.NUM_ITEM_TABS],
            DESTROY_BOX = new Rectangle2D.Double[2],
            PROMPT_BOX = new Rectangle2D.Double[2];

    private Point2D.Double mousePos;

    private int drawInfoItem = -1, drawInfoEquip = -1;
    private byte charFrame = 0;

    private int dragItem = -1, dragEquip = -1;
    private int nextFrameTime = 0;
    private long lastFrameTime = 0;

    static {
        for (int i = 0; i < INVENTORY_SLOTS.length; i++) {
            INVENTORY_SLOTS[i] = new Rectangle2D.Double(270 + (i * 62) - (i / 10 * 620), 30 + i / 10 * 62, 60, 60);
        }

        for (int i = 0; i < ITEM_TABS.length; i++) {
            ITEM_TABS[i] = new Rectangle2D.Double(230, 30 + i * 61, 30, 60);
        }

        EQUIP_SLOTS[Globals.ITEM_AMULET] = new Rectangle2D.Double(EQUIP_BOX_X + 160, EQUIP_BOX_Y, 60, 60);
        EQUIP_SLOTS[Globals.ITEM_BELT] = new Rectangle2D.Double(EQUIP_BOX_X + 160, EQUIP_BOX_Y + 210, 60, 60);
        EQUIP_SLOTS[Globals.ITEM_OFFHAND] = new Rectangle2D.Double(EQUIP_BOX_X + 160, EQUIP_BOX_Y + 140, 60, 60);
        EQUIP_SLOTS[Globals.ITEM_CHEST] = new Rectangle2D.Double(EQUIP_BOX_X + 160, EQUIP_BOX_Y + 70, 60, 60);
        EQUIP_SLOTS[Globals.ITEM_HEAD] = new Rectangle2D.Double(EQUIP_BOX_X + 80, EQUIP_BOX_Y, 60, 60);
        EQUIP_SLOTS[Globals.ITEM_RING] = new Rectangle2D.Double(EQUIP_BOX_X, 40, EQUIP_BOX_Y + 20, 60);
        EQUIP_SLOTS[Globals.ITEM_SHOULDER] = new Rectangle2D.Double(EQUIP_BOX_X, EQUIP_BOX_Y + 70, 60, 60);
        EQUIP_SLOTS[Globals.ITEM_GLOVE] = new Rectangle2D.Double(EQUIP_BOX_X, EQUIP_BOX_Y + 210, 60, 60);
        EQUIP_SLOTS[Globals.ITEM_WEAPON] = new Rectangle2D.Double(EQUIP_BOX_X, EQUIP_BOX_Y + 140, 60, 60);
        EQUIP_SLOTS[Globals.ITEM_PANTS] = new Rectangle2D.Double(EQUIP_BOX_X + 45, EQUIP_BOX_Y + 280, 60, 60);
        EQUIP_SLOTS[Globals.ITEM_SHOE] = new Rectangle2D.Double(EQUIP_BOX_X + 115, EQUIP_BOX_Y + 280, 60, 60);

        for (int i = 0; i < DESTROY_BOX.length; i++) {
            DESTROY_BOX[i] = new Rectangle2D.Double(520 + i * 185, 655, 180, 40);
        }
        PROMPT_BOX[0] = new Rectangle2D.Double(401, 400, 214, 112);
        PROMPT_BOX[1] = new Rectangle2D.Double(665, 400, 214, 112);
    }

    public ScreenInventory() {
        this.c = logic.getSelectedChar();
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
        final BufferedImage bg = Globals.MENU_BG[1];
        g.drawImage(bg, 0, 0, null);

        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SMALLRECT];
        g.drawImage(button, (int) DESTROY_BOX[0].x, (int) DESTROY_BOX[0].y, null);
        g.drawImage(button, (int) DESTROY_BOX[1].x, (int) DESTROY_BOX[1].y, null);
        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, "Destroy Item", 560, 682, 1);
        drawStringOutline(g, "Destroy All", 750, 682, 1);
        g.setColor(Color.WHITE);
        g.drawString("Destroy Item", 560, 682);
        g.drawString("Destroy All", 750, 682);

        drawStats(g);
        drawEquipSlots(g);
        drawInventory(g);
        drawItemTabs(g);
        drawDestroyConfirm(g);
        drawMenuButton(g);
        if (this.destroy) {
            button = Globals.MENU_ITEMDELETE[0];
            g.drawImage(button, (int) (this.mousePos.x + 10), (int) (this.mousePos.y + 15), null);
        }
        if (this.dragItem != -1) {
            this.c.getInventory(selectedTab)[this.dragItem].draw(g, (int) (this.mousePos.x + 5), (int) (this.mousePos.y + 5));
        } else if (this.dragEquip != -1) {
            this.c.getEquip()[this.dragEquip].draw(g, (int) (this.mousePos.x + 5), (int) (this.mousePos.y + 5));
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
            drawItemInfo(g, INVENTORY_SLOTS[this.drawInfoItem], this.c.getInventory(selectedTab)[this.drawInfoItem]);
        } else if (this.drawInfoEquip != -1) {
            drawItemInfo(g, EQUIP_SLOTS[this.drawInfoEquip], this.c.getEquip()[this.drawInfoEquip]);
        }
    }

    private void drawStats(final Graphics2D g) {
        g.setColor(SKILL_BOX_BG_COLOR);
        g.fillRoundRect(STAT_BOX_X - 10, STAT_BOX_Y - 20, 320, 255, 15, 15);
        double[] baseStats = this.c.getBaseStats();
        double[] totalStats = this.c.getTotalStats();
        double[] bonusStats = this.c.getBonusStats();
        String[] statString = {
            "Level: " + (int) baseStats[Globals.STAT_LEVEL],
            "Power: " + (int) baseStats[Globals.STAT_POWER] + " + " + (int) bonusStats[Globals.STAT_POWER],
            "Defense: " + (int) baseStats[Globals.STAT_DEFENSE] + " + " + (int) bonusStats[Globals.STAT_DEFENSE],
            "Spirit: " + (int) baseStats[Globals.STAT_SPIRIT] + " + " + (int) bonusStats[Globals.STAT_SPIRIT],
            "HP: " + (int) totalStats[Globals.STAT_MAXHP],
            "Damage: " + (int) totalStats[Globals.STAT_MINDMG] + " - " + (int) totalStats[Globals.STAT_MAXDMG],
            "Armor: " + (int) baseStats[Globals.STAT_ARMOR] + " + " + (int) bonusStats[Globals.STAT_ARMOR],
            "Regen: " + this.df.format(baseStats[Globals.STAT_REGEN]) + " + " + this.df.format(bonusStats[Globals.STAT_REGEN]) + " HP/Sec",
            "Critical Hit Chance: " + this.df.format(baseStats[Globals.STAT_CRITCHANCE] * 100) + " + "
            + this.df.format(bonusStats[Globals.STAT_CRITCHANCE] * 100) + "%",
            "Critical Hit Damage: " + this.df.format((1 + baseStats[Globals.STAT_CRITDMG]) * 100) + " + "
            + this.df.format(bonusStats[Globals.STAT_CRITDMG] * 100) + "%"};

        for (byte i = 0; i < statString.length; i++) {
            g.setFont(Globals.ARIAL_15PT);
            drawStringOutline(g, statString[i], STAT_BOX_X, STAT_BOX_Y + i * 25, 1);
            g.setColor(Color.WHITE);
            g.drawString(statString[i], STAT_BOX_X, STAT_BOX_Y + i * 25);
        }

    }

    private void drawEquipSlots(final Graphics2D g) {
        g.setColor(SKILL_BOX_BG_COLOR);
        g.fillRoundRect(EQUIP_BOX_X - 10, EQUIP_BOX_Y - 10, 240, 360, 15, 15);
        final BufferedImage character = Globals.CHAR_SPRITE[Globals.PLAYER_ANIM_STATE_STAND][this.charFrame];
        final int x = EQUIP_BOX_X + 90 + character.getWidth() / 2, y = EQUIP_BOX_Y + 160 + character.getHeight();
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

        final BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SLOT];
        // Equipment
        for (int i = 0; i < EQUIP_SLOTS.length; i++) {
            g.drawImage(button, (int) EQUIP_SLOTS[i].x, (int) EQUIP_SLOTS[i].y, null);
            if (this.c.getEquip()[i] != null) {
                this.c.getEquip()[i].draw(g, (int) EQUIP_SLOTS[i].x, (int) EQUIP_SLOTS[i].y);
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
            drawStringOutline(g, s, (int) EQUIP_SLOTS[i].x + 2, (int) EQUIP_SLOTS[i].y + 58, 1);
            g.setColor(Color.WHITE);
            g.drawString(s, (int) EQUIP_SLOTS[i].x + 2, (int) EQUIP_SLOTS[i].y + 58);
        }
    }

    private void drawInventory(final Graphics2D g) {
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SLOT];

        for (int i = 0; i < this.c.getInventory(selectedTab).length; i++) {
            g.drawImage(button, (int) INVENTORY_SLOTS[i].x, (int) INVENTORY_SLOTS[i].y, null);
            if (this.c.getInventory(selectedTab)[i] != null) {
                this.c.getInventory(selectedTab)[i].draw(g, (int) INVENTORY_SLOTS[i].x, (int) INVENTORY_SLOTS[i].y);
            }
        }
        if (selectedTab == Globals.ITEM_WEAPON) {
            button = Globals.MENU_BUTTON[Globals.BUTTON_RIGHTCLICK];
            g.drawImage(button, 280, 657, null);
            g.setFont(Globals.ARIAL_15PT);
            drawStringOutline(g, "Equip For Offhand", 310, 680, 1);
            g.setColor(new Color(255, 130, 0));
            g.drawString("Equip For Offhand", 310, 680);
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
                            final ItemEquip temp = this.c.getInventory(selectedTab)[i];
                            this.c.getInventory(selectedTab)[i] = this.c.getInventory(selectedTab)[drItem];
                            this.c.getInventory(selectedTab)[drItem] = temp;
                            return;
                        }
                        if (drEq != -1) {
                            this.c.equipItem(drEq, i);
                            return;
                        }
                        if (this.c.getInventory(selectedTab)[i] != null) {
                            this.c.equipItem(selectedTab, i);
                            return;
                        }
                    } else {
                        this.c.destroyItem(selectedTab, i);
                    }
                    return;
                }
            }

            for (byte i = 0; !this.destroy && i < EQUIP_SLOTS.length; i++) {
                if (EQUIP_SLOTS[i].contains(scaled)) {
                    if (drItem != -1) {
                        if (selectedTab == i || (selectedTab == Globals.ITEM_WEAPON && i == Globals.ITEM_OFFHAND)) {
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
            if (selectedTab == Globals.ITEM_WEAPON) {
                for (int i = 0; i < INVENTORY_SLOTS.length; i++) {
                    if (INVENTORY_SLOTS[i].contains(scaled) && this.c.getInventory(selectedTab)[i] != null) {
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
        Point2D.Double scaled;
        if (Globals.WINDOW_SCALE_ENABLED) {
            scaled = new Point2D.Double(e.getX() / Globals.WINDOW_SCALE, e.getY() / Globals.WINDOW_SCALE);
        } else {
            scaled = new Point2D.Double(e.getX(), e.getY());
        }
        for (byte i = 0; i < PROMPT_BOX.length; i++) {
            if (PROMPT_BOX[i].contains(scaled)) {
                if (i == 0) {
                    this.c.destroyAll(selectedTab);
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
                    if (INVENTORY_SLOTS[i].contains(scaled) && this.c.getInventory(selectedTab)[i] != null) {
                        this.dragItem = i;
                        return;
                    }
                }

                for (byte i = 0; i < EQUIP_SLOTS.length; i++) {
                    if (EQUIP_SLOTS[i].contains(scaled) && this.c.getEquip()[i] != null) {
                        this.dragEquip = i;
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
            if (INVENTORY_SLOTS[i].contains(scaled) && this.c.getInventory(selectedTab)[i] != null) {
                this.drawInfoItem = i;
                return;
            }
        }

        for (byte i = 0; i < EQUIP_SLOTS.length; i++) {
            if (EQUIP_SLOTS[i].contains(scaled) && this.c.getEquip()[i] != null) {
                this.drawInfoEquip = i;
                return;
            }
        }
    }

    @Override
    public void unload() {
    }

}
