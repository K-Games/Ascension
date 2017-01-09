package blockfighter.client.screen;

import blockfighter.client.SaveData;
import blockfighter.client.entities.items.ItemEquip;
import static blockfighter.client.screen.Screen.SKILL_BOX_BG_COLOR;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public abstract class ScreenItemManagement extends ScreenMenu {

    private static final int EQUIP_BOX_X = 980, EQUIP_BOX_Y = 40;

    protected boolean destroy = false, destroyConfirm = false;

    protected static final Rectangle2D.Double[] EQUIP_SLOTS = new Rectangle2D.Double[Globals.NUM_EQUIP_SLOTS],
            DESTROY_BOX = new Rectangle2D.Double[2],
            PROMPT_BOX = new Rectangle2D.Double[2];

    protected static final String DESTROY_ALL_TEXT = "Destroy All";
    protected static final String DESTROY_ITEM_TEXT = "Destroy Item";
    protected static final String CANCLEL_TEXT = "Cancel";
    protected static final String CONFIRM_TEXT = "Confirm";
    protected static final String ARE_YOU_SURE_TEXT = "Are you sure?";
    protected static final String EQUIP_OFFHAND_TEXT = "Equip For Offhand";

    private static final String OFFHAND = "Offhand";
    private static final String MAIN_HAND = "Weapon";

    private int nextFrameTime = 0;
    private long lastFrameTime = 0;
    private byte charFrame = 0;
    protected final SaveData character;

    static {
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

    public ScreenItemManagement() {
        this.character = logic.getSelectedChar();
    }

    @Override
    public void update() {
        super.update();
        final long now = logic.getTime(); // Get time now
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
        if (this.destroyConfirm) {
            drawDestroyConfirm(g);
        }
        if (this.destroy) {
            BufferedImage button = Globals.MENU_ITEMDELETE[0];
            g.drawImage(button, (int) (this.mousePos.x + 10), (int) (this.mousePos.y + 15), null);
        }
        super.draw(g);
    }

    protected void drawDestroyConfirm(final Graphics2D g) {
        final BufferedImage window = Globals.MENU_WINDOW[Globals.WINDOW_DESTROYCONFIRM];
        g.drawImage(window, 265, 135, null);

        g.setFont(Globals.ARIAL_30PT);
        drawStringOutline(g, ARE_YOU_SURE_TEXT, 540, 300, 2);
        g.setColor(Color.WHITE);
        g.drawString(ARE_YOU_SURE_TEXT, 540, 300);

        final BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_BIGRECT];
        g.drawImage(button, 401, 400, null);
        drawStringOutline(g, CONFIRM_TEXT, 455, 465, 2);
        g.setColor(Color.WHITE);
        g.drawString(CONFIRM_TEXT, 455, 465);

        g.drawImage(button, 665, 400, null);
        drawStringOutline(g, CANCLEL_TEXT, 725, 465, 2);
        g.setColor(Color.WHITE);
        g.drawString(CANCLEL_TEXT, 725, 465);
    }

    protected void drawEquipSlots(final Graphics2D g) {
        g.setColor(SKILL_BOX_BG_COLOR);
        g.fillRoundRect(EQUIP_BOX_X - 10, EQUIP_BOX_Y - 10, 240, 360, 15, 15);
        final BufferedImage characterSprite = Globals.CHAR_SPRITE[Globals.PLAYER_ANIM_STATE_STAND][this.charFrame];
        final int x = EQUIP_BOX_X + 90 + characterSprite.getWidth() / 2, y = EQUIP_BOX_Y + 160 + characterSprite.getHeight();
        if (this.character.getEquip()[Globals.ITEM_OFFHAND] != null) {
            this.character.getEquip()[Globals.ITEM_OFFHAND].drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_STAND, charFrame, Globals.RIGHT, true);
        }
        g.drawImage(characterSprite, 1070, 170, null);

        if (this.character.getEquip()[Globals.ITEM_CHEST] != null) {
            this.character.getEquip()[Globals.ITEM_CHEST].drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_STAND, charFrame, Globals.RIGHT);
        }
        if (this.character.getEquip()[Globals.ITEM_SHOULDER] != null) {
            this.character.getEquip()[Globals.ITEM_SHOULDER].drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_STAND, charFrame, Globals.RIGHT);
        }

        if (this.character.getEquip()[Globals.ITEM_PANTS] != null) {
            this.character.getEquip()[Globals.ITEM_PANTS].drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_STAND, charFrame, Globals.RIGHT);
        }
        if (this.character.getEquip()[Globals.ITEM_SHOE] != null) {
            this.character.getEquip()[Globals.ITEM_SHOE].drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_STAND, charFrame, Globals.RIGHT);
        }
        if (this.character.getEquip()[Globals.ITEM_WEAPON] != null) {
            this.character.getEquip()[Globals.ITEM_WEAPON].drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_STAND, charFrame, Globals.RIGHT);
        }
        if (this.character.getEquip()[Globals.ITEM_GLOVE] != null) {
            this.character.getEquip()[Globals.ITEM_GLOVE].drawIngame(g, x, y, Globals.PLAYER_ANIM_STATE_STAND, charFrame, Globals.RIGHT);
        }

        final BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SLOT];
        // Equipment
        for (int i = 0; i < EQUIP_SLOTS.length; i++) {
            g.drawImage(button, (int) EQUIP_SLOTS[i].x, (int) EQUIP_SLOTS[i].y, null);
            if (this.character.getEquip()[i] != null) {
                this.character.getEquip()[i].draw(g, (int) EQUIP_SLOTS[i].x, (int) EQUIP_SLOTS[i].y);
            }
            String s = ItemEquip.getItemTypeName((byte) i);
            if (i == Globals.ITEM_WEAPON) {
                s = MAIN_HAND;
            } else if (i == Globals.ITEM_OFFHAND) {
                s = OFFHAND;
            }
            g.setFont(Globals.ARIAL_12PT);
            drawStringOutline(g, s, (int) EQUIP_SLOTS[i].x + 2, (int) EQUIP_SLOTS[i].y + 58, 1);
            g.setColor(Color.WHITE);
            g.drawString(s, (int) EQUIP_SLOTS[i].x + 2, (int) EQUIP_SLOTS[i].y + 58);
        }
    }

    protected void drawDestroyButtons(final Graphics2D g) {
        BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SMALLRECT];
        g.drawImage(button, (int) DESTROY_BOX[0].x, (int) DESTROY_BOX[0].y, null);
        g.drawImage(button, (int) DESTROY_BOX[1].x, (int) DESTROY_BOX[1].y, null);
        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, DESTROY_ITEM_TEXT, (int) (DESTROY_BOX[0].x + button.getWidth() / 2 - g.getFontMetrics().stringWidth(DESTROY_ITEM_TEXT) / 2), (int) (DESTROY_BOX[0].y + 27), 1);
        drawStringOutline(g, DESTROY_ALL_TEXT, (int) (DESTROY_BOX[1].x + button.getWidth() / 2 - g.getFontMetrics().stringWidth(DESTROY_ALL_TEXT) / 2), (int) (DESTROY_BOX[1].y + 27), 1);
        g.setColor(Color.WHITE);
        g.drawString(DESTROY_ITEM_TEXT, (int) (DESTROY_BOX[0].x + button.getWidth() / 2 - g.getFontMetrics().stringWidth(DESTROY_ITEM_TEXT) / 2), (int) (DESTROY_BOX[0].y + 27));
        g.drawString(DESTROY_ALL_TEXT, (int) (DESTROY_BOX[1].x + button.getWidth() / 2 - g.getFontMetrics().stringWidth(DESTROY_ALL_TEXT) / 2), (int) (DESTROY_BOX[1].y + 27));
    }

    protected abstract void mouseReleased_destroyConfirm(final MouseEvent e);
}
