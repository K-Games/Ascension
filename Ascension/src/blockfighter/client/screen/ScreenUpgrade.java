package blockfighter.client.screen;

import blockfighter.client.SaveData;
import blockfighter.client.entities.items.ItemEquip;
import blockfighter.client.entities.items.ItemUpgrade;
import blockfighter.client.entities.particles.ParticleMenuUpgrade;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;

public class ScreenUpgrade extends ScreenItemManagement {

    private static final String ENHANCE_TEXT = "Enhance";
    private static final String CHANCE_OF_SUCCESS_TEXT = "Chance of Success: ";
    private static final String EQUIPMENT_UPGRADE_TEXT = "Equipment Upgrade";

    private static final int UPGRADE_BOX_X = 980, UPGRADE_BOX_Y = 430;

    private int selectEquip = -1;
    private int selectUpgrade = -1;
    private int dragItem = -1;

    private long lastFrameTime = 0;

    private static final Rectangle2D.Double[] INVENTORY_SLOTS = new Rectangle2D.Double[100],
            UPGRADE_BOX = new Rectangle2D.Double[2];
    private static final Rectangle2D.Double COMBINE_BOX;

    private int drawItem = -1, drawEquip = -1, drawSelect = -1;

    private byte charFrame = 0;
    private long nextFrameTime = 0;
    private boolean upgrading = false;
    private int upPart = 0;

    static {
        UPGRADE_BOX[0] = new Rectangle2D.Double(UPGRADE_BOX_X, UPGRADE_BOX_Y + 30, 60, 60);
        UPGRADE_BOX[1] = new Rectangle2D.Double(UPGRADE_BOX_X + 160, UPGRADE_BOX_Y + 30, 60, 60);
        COMBINE_BOX = new Rectangle2D.Double(UPGRADE_BOX_X + 20, UPGRADE_BOX_Y + 130, 180, 40);
        for (int i = 0; i < INVENTORY_SLOTS.length; i++) {
            INVENTORY_SLOTS[i] = new Rectangle2D.Double(255 + (i * 62) - (i / 10 * 620), 30 + i / 10 * 62, 60, 60);
        }
    }

    @Override
    public void update() {
        super.update();
        final long now = logic.getTime(); // Get time now
        if (now - this.lastUpdateTime >= Globals.CLIENT_LOGIC_UPDATE) {
            if (this.upgrading) {
                if (ItemUpgrade.rollUpgrade(this.character.getUpgrades()[this.selectUpgrade], this.character.getEquip()[this.selectEquip])) {
                    this.character.getEquip()[this.selectEquip].addUpgrade(1);
                    for (int i = 0; i < 20; i++) {
                        particles.put(i + 2, new ParticleMenuUpgrade(this.upPart + 2, (int) UPGRADE_BOX[1].x + 30,
                                (int) UPGRADE_BOX[1].y + 30, 3,
                                Globals.rng(10) - 5, -5 - Globals.rng(3)));
                    }
                } else {
                    for (int i = 0; i < 20; i++) {
                        particles.put(i + 2, new ParticleMenuUpgrade(this.upPart + 2, (int) UPGRADE_BOX[1].x + 30,
                                (int) UPGRADE_BOX[1].y + 30, 2,
                                Globals.rng(10) - 5, -5 - Globals.rng(3)));
                    }
                }
                this.character.destroyItem(this.selectUpgrade);
                this.character.calcStats();
                SaveData.saveData(this.character.getSaveNum(), this.character);
                this.selectUpgrade = -1;
                this.upgrading = false;
            }

            if (this.selectUpgrade > -1 && this.character.getUpgrades()[this.selectUpgrade] == null) {
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

        drawEquipSlots(g);
        drawUpgradeBox(g);
        drawInventory(g);
        drawDestroyButtons(g);
        if (this.destroyConfirm) {
            drawDestroyConfirm(g);
        }
        if (this.destroy) {
            BufferedImage button = Globals.MENU_ITEMDELETE[0];
            g.drawImage(button, (int) (this.mousePos.x + 10), (int) (this.mousePos.y + 15), null);
        }
        drawMenuButton(g);
        if (this.dragItem != -1) {
            this.character.getUpgrades()[this.dragItem].draw(g, (int) (this.mousePos.x + 5), (int) (this.mousePos.y + 5));
        }
        super.draw(g);
        drawItemInfo(g);
    }

    private void drawUpgradeBox(final Graphics2D g) {
        g.setColor(SKILL_BOX_BG_COLOR);
        g.fillRoundRect(UPGRADE_BOX_X - 20, UPGRADE_BOX_Y - 10, 260, 190, 15, 15);

        g.setFont(Globals.ARIAL_15PT);
        drawStringOutline(g, EQUIPMENT_UPGRADE_TEXT, UPGRADE_BOX_X + 40, UPGRADE_BOX_Y + 15, 1);
        g.setColor(Color.WHITE);
        g.drawString(EQUIPMENT_UPGRADE_TEXT, UPGRADE_BOX_X + 40, UPGRADE_BOX_Y + 15);

        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SLOT];
        // upgrades
        for (final Rectangle2D.Double box : UPGRADE_BOX) {
            g.drawImage(button, (int) box.x, (int) box.y, null);
        }

        if (this.selectUpgrade > -1) {
            if (this.character.getUpgrades()[this.selectUpgrade] != null) {
                this.character.getUpgrades()[this.selectUpgrade].draw(g, (int) UPGRADE_BOX[0].x, (int) UPGRADE_BOX[0].y);
            }
        }
        if (this.selectEquip > -1) {
            if (this.character.getEquip()[this.selectEquip] != null) {
                this.character.getEquip()[this.selectEquip].draw(g, (int) UPGRADE_BOX[1].x, (int) UPGRADE_BOX[1].y);
            }
        }
        button = Globals.MENU_BUTTON[Globals.BUTTON_SMALLRECT];
        g.drawImage(button, (int) DESTROY_BOX[0].x, (int) DESTROY_BOX[0].y, null);
        g.drawImage(button, (int) DESTROY_BOX[1].x, (int) DESTROY_BOX[1].y, null);
        g.drawImage(button, (int) COMBINE_BOX.x, (int) COMBINE_BOX.y, null);

        final int selTemp1 = this.selectUpgrade, selTemp2 = this.selectEquip;
        if (selTemp1 >= 0 && selTemp2 >= 0) {
            g.setFont(Globals.ARIAL_15PT);
            drawStringOutline(g, CHANCE_OF_SUCCESS_TEXT
                    + Globals.NUMBER_FORMAT.format(ItemUpgrade.upgradeChance(this.character.getUpgrades()[selTemp1], this.character.getEquip()[selTemp2]) * 100) + "%",
                    UPGRADE_BOX_X + 20, UPGRADE_BOX_Y + 115, 1);
            g.setColor(Color.WHITE);
            g.drawString(CHANCE_OF_SUCCESS_TEXT
                    + Globals.NUMBER_FORMAT.format(ItemUpgrade.upgradeChance(this.character.getUpgrades()[selTemp1], this.character.getEquip()[selTemp2]) * 100) + "%",
                    UPGRADE_BOX_X + 20, UPGRADE_BOX_Y + 115);
        }

        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, ENHANCE_TEXT, (int) COMBINE_BOX.x + 55, (int) COMBINE_BOX.y + 25, 1);
        g.setColor(Color.WHITE);
        g.drawString(ENHANCE_TEXT, (int) COMBINE_BOX.x + 55, (int) COMBINE_BOX.y + 25);
    }

    private void drawInventory(final Graphics2D g) {
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SLOT];
        // Inventory
        for (int i = 0; i < this.character.getUpgrades().length; i++) {
            g.drawImage(button, (int) INVENTORY_SLOTS[i].x, (int) INVENTORY_SLOTS[i].y, null);
            if (this.character.getUpgrades()[i] != null) {
                this.character.getUpgrades()[i].draw(g, (int) INVENTORY_SLOTS[i].x, (int) INVENTORY_SLOTS[i].y);
            }
        }
    }

    private void drawItemInfo(final Graphics2D g) {
        if (this.destroyConfirm) {
            return;
        }
        if (this.drawItem > -1) {
            drawItemInfo(g, INVENTORY_SLOTS[this.drawItem], this.character.getUpgrades()[this.drawItem]);
        } else if (this.drawEquip > -1) {
            drawItemInfo(g, EQUIP_SLOTS[this.drawEquip], this.character.getEquip()[this.drawEquip]);
        } else if (this.drawSelect == 0 && this.selectUpgrade > -1) {
            drawItemInfo(g, UPGRADE_BOX[0], this.character.getUpgrades()[this.selectUpgrade]);
        } else if (this.drawSelect == 1 && this.selectEquip > -1) {
            drawItemInfo(g, UPGRADE_BOX[1], this.character.getEquip()[this.selectEquip]);
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
        Point2D.Double scaled;
        if (Globals.WINDOW_SCALE_ENABLED) {
            scaled = new Point2D.Double(e.getX() / Globals.WINDOW_SCALE, e.getY() / Globals.WINDOW_SCALE);
        } else {
            scaled = new Point2D.Double(e.getX(), e.getY());
        }
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
            for (int i = 0; i < INVENTORY_SLOTS.length; i++) {
                if (INVENTORY_SLOTS[i].contains(scaled)) {
                    if (!this.destroy) {
                        if (drItem != -1) {
                            if (drItem == selectUpgrade) {
                                selectUpgrade = i;
                            } else if (i == selectUpgrade) {
                                selectUpgrade = drItem;
                            }
                            final ItemUpgrade temp = this.character.getUpgrades()[i];
                            this.character.getUpgrades()[i] = this.character.getUpgrades()[drItem];
                            this.character.getUpgrades()[drItem] = temp;
                            return;
                        }
                        // set upgrade item
                        if (this.character.getUpgrades()[i] != null) {
                            this.selectUpgrade = i;
                            return;
                        }
                    } else {
                        // Destroy upgrade item
                        if (this.selectUpgrade == i) {
                            this.selectUpgrade = -1;
                            // drawSelect = -1;
                        }
                        this.character.destroyItem(i);
                        return;
                    }
                }
            }
            if (UPGRADE_BOX[0].contains(scaled)) {
                if (!this.destroy) {
                    if (drItem != -1) {
                        this.selectUpgrade = drItem;
                        return;
                    }
                }
            }

            for (int i = 0; !this.destroy && i < EQUIP_SLOTS.length; i++) {
                if (EQUIP_SLOTS[i].contains(scaled) && this.character.getEquip()[i] != null) {
                    // Set upgrading item
                    this.selectEquip = i;
                    return;
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

            if (!this.upgrading && COMBINE_BOX.contains(scaled)) {
                if (this.selectUpgrade >= 0 && this.selectEquip >= 0) {
                    this.upPart = 0;
                    this.upgrading = true;
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
                    this.selectUpgrade = -1;
                    this.character.destroyAllUpgrade();
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
        if (this.destroyConfirm || this.destroy || this.upgrading) {
            return;
        }
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (this.dragItem == -1) {
                for (int i = 0; i < INVENTORY_SLOTS.length; i++) {
                    if (INVENTORY_SLOTS[i].contains(scaled) && this.character.getUpgrades()[i] != null) {
                        this.dragItem = i;
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
        this.drawItem = -1;
        this.drawEquip = -1;
        this.drawSelect = -1;

        if (UPGRADE_BOX[0].contains(scaled) && this.selectUpgrade > -1) {
            this.drawSelect = 0;
            return;
        }

        if (UPGRADE_BOX[1].contains(scaled) && this.selectEquip > -1) {
            this.drawSelect = 1;
            return;
        }

        for (int i = 0; i < INVENTORY_SLOTS.length; i++) {
            if (INVENTORY_SLOTS[i].contains(scaled) && this.character.getUpgrades()[i] != null) {
                this.drawItem = i;
                return;
            }
        }

        for (int i = 0; this.drawItem < 0 && i < EQUIP_SLOTS.length; i++) {
            if (EQUIP_SLOTS[i].contains(scaled) && this.character.getEquip()[i] != null) {
                this.drawEquip = i;
                return;
            }
        }
    }

    @Override
    public void unload() {
    }

}
