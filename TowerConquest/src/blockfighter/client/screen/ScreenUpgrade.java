package blockfighter.client.screen;

import blockfighter.client.Globals;
import blockfighter.client.SaveData;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.items.ItemUpgrade;
import blockfighter.client.entities.particles.ParticleMenuUpgrade;
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
public class ScreenUpgrade extends ScreenMenu {

    private static final int EQUIP_BOX_X = 980, EQUIP_BOX_Y = 40;
    private static final int UPGRADE_BOX_X = 980, UPGRADE_BOX_Y = 430;

    private final SaveData c;
    private boolean destroy = false, destroyConfirm = false;

    private int selectEquip = -1;
    private int selectUpgrade = -1;
    private int dragItem = -1;

    private long lastFrameTime = 0;

    private final Rectangle2D.Double[] inventSlots = new Rectangle2D.Double[100],
            equipSlots = new Rectangle2D.Double[Globals.NUM_EQUIP_SLOTS],
            destroyBox = new Rectangle2D.Double[2],
            upgradeBox = new Rectangle2D.Double[2],
            promptBox = new Rectangle2D.Double[2];
    private final Rectangle2D.Double combineBox;

    private Point mousePos;

    private int drawItem = -1, drawEquip = -1, drawSelect = -1;

    private byte charFrame = 0;
    private long nextFrameTime = 0;
    private boolean upgrading = false;
    private int upPart = 0;

    public ScreenUpgrade() {
        this.c = logic.getSelectedChar();
        for (int i = 0; i < this.inventSlots.length; i++) {
            this.inventSlots[i] = new Rectangle2D.Double(255 + (i * 62) - (i / 10 * 620), 30 + i / 10 * 62, 60, 60);
        }

        this.equipSlots[Globals.ITEM_AMULET] = new Rectangle2D.Double(EQUIP_BOX_X + 160, EQUIP_BOX_Y, 60, 60);
        this.equipSlots[Globals.ITEM_BELT] = new Rectangle2D.Double(EQUIP_BOX_X + 160, EQUIP_BOX_Y + 210, 60, 60);
        this.equipSlots[Globals.ITEM_OFFHAND] = new Rectangle2D.Double(EQUIP_BOX_X + 160, EQUIP_BOX_Y + 140, 60, 60);
        this.equipSlots[Globals.ITEM_CHEST] = new Rectangle2D.Double(EQUIP_BOX_X + 160, EQUIP_BOX_Y + 70, 60, 60);
        this.equipSlots[Globals.ITEM_HEAD] = new Rectangle2D.Double(EQUIP_BOX_X + 80, EQUIP_BOX_Y, 60, 60);
        this.equipSlots[Globals.ITEM_RING] = new Rectangle2D.Double(EQUIP_BOX_X, 40, EQUIP_BOX_Y + 20, 60);
        this.equipSlots[Globals.ITEM_SHOULDER] = new Rectangle2D.Double(EQUIP_BOX_X, EQUIP_BOX_Y + 70, 60, 60);
        this.equipSlots[Globals.ITEM_GLOVE] = new Rectangle2D.Double(EQUIP_BOX_X, EQUIP_BOX_Y + 210, 60, 60);
        this.equipSlots[Globals.ITEM_WEAPON] = new Rectangle2D.Double(EQUIP_BOX_X, EQUIP_BOX_Y + 140, 60, 60);
        this.equipSlots[Globals.ITEM_PANTS] = new Rectangle2D.Double(EQUIP_BOX_X + 45, EQUIP_BOX_Y + 280, 60, 60);
        this.equipSlots[Globals.ITEM_SHOE] = new Rectangle2D.Double(EQUIP_BOX_X + 115, EQUIP_BOX_Y + 280, 60, 60);

        this.upgradeBox[0] = new Rectangle2D.Double(UPGRADE_BOX_X, UPGRADE_BOX_Y + 30, 60, 60);
        this.upgradeBox[1] = new Rectangle2D.Double(UPGRADE_BOX_X + 160, UPGRADE_BOX_Y + 30, 60, 60);
        this.combineBox = new Rectangle2D.Double(UPGRADE_BOX_X + 20, UPGRADE_BOX_Y + 130, 180, 30);

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
            if (this.upgrading) {
                if (ItemUpgrade.rollUpgrade(this.c.getUpgrades()[this.selectUpgrade], this.c.getEquip()[this.selectEquip])) {
                    this.c.getEquip()[this.selectEquip].addUpgrade(1);
                    for (int i = 0; i < 20; i++) {
                        particles.put(i + 2, new ParticleMenuUpgrade(this.upPart + 2, (int) this.upgradeBox[1].x + 30,
                                (int) this.upgradeBox[1].y + 30, 3,
                                Globals.rng(10) - 5, -5 - Globals.rng(3)));
                    }
                } else {
                    for (int i = 0; i < 20; i++) {
                        particles.put(i + 2, new ParticleMenuUpgrade(this.upPart + 2, (int) this.upgradeBox[1].x + 30,
                                (int) this.upgradeBox[1].y + 30, 2,
                                Globals.rng(10) - 5, -5 - Globals.rng(3)));
                    }
                }
                this.c.destroyItem(this.selectUpgrade);
                this.c.calcStats();
                SaveData.saveData(this.c.getSaveNum(), this.c);
                this.selectUpgrade = -1;
                this.upgrading = false;
            }

            if (this.selectUpgrade > -1 && this.c.getUpgrades()[this.selectUpgrade] == null) {
                selectUpgrade = -1;
            }

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

        final Graphics2D g2d = g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        drawEquipSlots(g);
        drawUpgradeBox(g);
        drawInventory(g);
        drawDestroyConfirm(g);
        if (this.destroy) {
            BufferedImage button = Globals.MENU_ITEMDELETE[0];
            g.drawImage(button, this.mousePos.x + 10, this.mousePos.y + 15, null);
        }
        drawMenuButton(g);
        if (this.dragItem != -1) {
            this.c.getUpgrades()[this.dragItem].draw(g, this.mousePos.x + 5, this.mousePos.y + 5);
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

    private void drawUpgradeBox(final Graphics2D g) {
        g.setColor(SKILL_BOX_BG_COLOR);
        g.fillRoundRect(UPGRADE_BOX_X - 20, UPGRADE_BOX_Y - 10, 260, 190, 15, 15);
        
        g.setFont(Globals.ARIAL_15PT);
        drawStringOutline(g, "Equipment Upgrade", UPGRADE_BOX_X + 40,  UPGRADE_BOX_Y + 15, 1);
        g.setColor(Color.WHITE);
        g.drawString("Equipment Upgrade", UPGRADE_BOX_X + 40,  UPGRADE_BOX_Y + 15);
        
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SLOT];
        // upgrades
        for (final Rectangle2D.Double box : this.upgradeBox) {
            g.drawImage(button, (int) box.x, (int) box.y, null);
        }

        if (this.selectUpgrade > -1) {
            if (this.c.getUpgrades()[this.selectUpgrade] != null) {
                this.c.getUpgrades()[this.selectUpgrade].draw(g, (int) this.upgradeBox[0].x, (int) this.upgradeBox[0].y);
            }
        }
        if (this.selectEquip > -1) {
            if (this.c.getEquip()[this.selectEquip] != null) {
                this.c.getEquip()[this.selectEquip].draw(g, (int) this.upgradeBox[1].x, (int) this.upgradeBox[1].y);
            }
        }
        button = Globals.MENU_BUTTON[Globals.BUTTON_SMALLRECT];
        g.drawImage(button, (int) this.destroyBox[0].x, (int) this.destroyBox[0].y, null);
        g.drawImage(button, (int) this.destroyBox[1].x, (int) this.destroyBox[1].y, null);
        g.drawImage(button, (int) this.combineBox.x, (int) this.combineBox.y, null);

        final int selTemp1 = this.selectUpgrade, selTemp2 = this.selectEquip;
        if (selTemp1 >= 0 && selTemp2 >= 0) {
            g.setFont(Globals.ARIAL_15PT);
            drawStringOutline(g, "Chance of Success: "
                    + this.df.format(ItemUpgrade.upgradeChance(this.c.getUpgrades()[selTemp1], this.c.getEquip()[selTemp2]) * 100) + "%",
                    UPGRADE_BOX_X + 20, UPGRADE_BOX_Y + 115, 1);
            g.setColor(Color.WHITE);
            g.drawString("Chance of Success: "
                    + this.df.format(ItemUpgrade.upgradeChance(this.c.getUpgrades()[selTemp1], this.c.getEquip()[selTemp2]) * 100) + "%",
                    UPGRADE_BOX_X + 20, UPGRADE_BOX_Y + 115);
        }

        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, "Enhance", (int) this.combineBox.x + 55, (int) this.combineBox.y + 25, 1);
        g.setColor(Color.WHITE);
        g.drawString("Enhance", (int) this.combineBox.x + 55, (int) this.combineBox.y + 25);
    }

    private void drawInventory(final Graphics2D g) {
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SLOT];
        // Inventory
        for (int i = 0; i < this.c.getUpgrades().length; i++) {
            g.drawImage(button, (int) this.inventSlots[i].x, (int) this.inventSlots[i].y, null);
            if (this.c.getUpgrades()[i] != null) {
                this.c.getUpgrades()[i].draw(g, (int) this.inventSlots[i].x, (int) this.inventSlots[i].y);
            }
        }
        button = Globals.MENU_BUTTON[Globals.BUTTON_SMALLRECT];
        g.drawImage(button, (int) this.destroyBox[0].x, (int) this.destroyBox[0].y, null);
        g.drawImage(button, (int) this.destroyBox[1].x, (int) this.destroyBox[1].y, null);

        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, "Destroy Item", 560, 682, 1);
        drawStringOutline(g, "Destroy All", 750, 682, 1);
        g.setColor(Color.WHITE);
        g.drawString("Destroy Item", 560, 682);
        g.drawString("Destroy All", 750, 682);

    }

    private void drawItemInfo(final Graphics2D g) {
        if (this.destroyConfirm) {
            return;
        }
        if (this.drawItem > -1) {
            drawItemInfo(g, this.inventSlots[this.drawItem], this.c.getUpgrades()[this.drawItem]);
        } else if (this.drawEquip > -1) {
            drawItemInfo(g, this.equipSlots[this.drawEquip], this.c.getEquip()[this.drawEquip]);
        } else if (this.drawSelect == 0 && this.selectUpgrade > -1) {
            drawItemInfo(g, this.upgradeBox[0], this.c.getUpgrades()[this.selectUpgrade]);
        } else if (this.drawSelect == 1 && this.selectEquip > -1) {
            drawItemInfo(g, this.upgradeBox[1], this.c.getEquip()[this.selectEquip]);
        }
    }

    private void drawItemInfo(final Graphics2D g, final Rectangle2D.Double box, final ItemUpgrade e) {
        if (e == null) {
            return;
        }
        e.drawInfo(g, box);
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
        final int drItem = this.dragItem;
        this.dragItem = -1;
        if (this.destroyConfirm) {
            mouseReleased_destroyConfirm(e);
            return;
        }
        super.mouseReleased(e);
        if (this.upgrading) {
            return;
        }
        if (SwingUtilities.isLeftMouseButton(e)) {
            for (int i = 0; i < this.inventSlots.length; i++) {
                if (this.inventSlots[i].contains(e.getPoint())) {
                    if (!this.destroy) {
                        if (drItem != -1) {
                            if (drItem == selectUpgrade) {
                                selectUpgrade = i;
                            } else if (i == selectUpgrade) {
                                selectUpgrade = drItem;
                            }
                            final ItemUpgrade temp = this.c.getUpgrades()[i];
                            this.c.getUpgrades()[i] = this.c.getUpgrades()[drItem];
                            this.c.getUpgrades()[drItem] = temp;
                            return;
                        }
                        // set upgrade item
                        if (this.c.getUpgrades()[i] != null) {
                            this.selectUpgrade = i;
                            return;
                        }
                    } else {
                        // Destroy upgrade item
                        if (this.selectUpgrade == i) {
                            this.selectUpgrade = -1;
                            // drawSelect = -1;
                        }
                        this.c.destroyItem(i);
                        return;
                    }
                }
            }
            if (this.upgradeBox[0].contains(e.getPoint())) {
                if (!this.destroy) {
                    if (drItem != -1) {
                        this.selectUpgrade = drItem;
                        return;
                    }
                }
            }

            for (int i = 0; !this.destroy && i < this.equipSlots.length; i++) {
                if (this.equipSlots[i].contains(e.getPoint()) && this.c.getEquip()[i] != null) {
                    // Set upgrading item
                    this.selectEquip = i;
                    return;
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

            if (!this.upgrading && this.combineBox.contains(e.getPoint())) {
                if (this.selectUpgrade >= 0 && this.selectEquip >= 0) {
                    this.upPart = 0;
                    this.upgrading = true;
                }
            }
        }
    }

    private void mouseReleased_destroyConfirm(final MouseEvent e) {
        for (byte i = 0; i < this.promptBox.length; i++) {
            if (this.promptBox[i].contains(e.getPoint())) {
                if (i == 0) {
                    this.selectUpgrade = -1;
                    this.c.destroyAllUpgrade();
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
        if (this.destroyConfirm || this.destroy || this.upgrading) {
            return;
        }
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (this.dragItem == -1) {
                for (int i = 0; i < this.inventSlots.length; i++) {
                    if (this.inventSlots[i].contains(e.getPoint()) && this.c.getUpgrades()[i] != null) {
                        this.dragItem = i;
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        this.mousePos = e.getPoint();
        this.drawItem = -1;
        this.drawEquip = -1;
        this.drawSelect = -1;

        if (this.upgradeBox[0].contains(e.getPoint()) && this.selectUpgrade > -1) {
            this.drawSelect = 0;
            return;
        }

        if (this.upgradeBox[1].contains(e.getPoint()) && this.selectEquip > -1) {
            this.drawSelect = 1;
            return;
        }

        for (int i = 0; i < this.inventSlots.length; i++) {
            if (this.inventSlots[i].contains(e.getPoint()) && this.c.getUpgrades()[i] != null) {
                this.drawItem = i;
                return;
            }
        }

        for (int i = 0; this.drawItem < 0 && i < this.equipSlots.length; i++) {
            if (this.equipSlots[i].contains(e.getPoint()) && this.c.getEquip()[i] != null) {
                this.drawEquip = i;
                return;
            }
        }
    }

    @Override
    public void unload() {
    }

}
