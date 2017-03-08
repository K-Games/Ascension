package blockfighter.client.screen.window;

import blockfighter.client.Core;
import blockfighter.client.entities.items.Item;
import blockfighter.client.net.PacketSender;
import blockfighter.client.screen.Screen;
import blockfighter.shared.Globals;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WindowMatchResult extends Window {

    private static final String SECONDS_TEXT = " seconds.";
    private static final String RETURNING_TO_MENU_IN_TEXT = "Returning to menu in ";
    private static final String MATCH_COMPLETION_REWARDS_TEXT = "Match Completion Rewards:";
    private static final String TOTAL_EXP_GAINED_TEXT = "Total EXP Gained: ";
    private static final String LEAVE_GAME_TEXT = "Leave Game";

    private static final Color EXP_COLOUR = new Color(255, 200, 0);
    private static final Color REWARDS_COLOUR = new Color(0, 170, 255);
    private static final Color BG_COLOUR = new Color(0, 0, 0, 130);
    private static final Color LEAVE_BUTTON_COLOUR = new Color(70, 70, 70, 255);
    private static final Color FIRST_PLACE = new Color(212, 175, 55);
    private static final Color SECOND_PLACE = new Color(188, 198, 204);
    private static final Color THIRD_PLACE = new Color(193, 115, 73);

    private final ConcurrentLinkedQueue<Item> ITEMS_GAINED = new ConcurrentLinkedQueue<>();
    private double expGained = 0;
    private long countdownStartTime = 0;
    private int secondsLeftBeforeMenu = 0;
    private float transparency = 0f;
    private double translateY, deltaY;

    private Globals.VictoryStatus victoryStatus = Globals.VictoryStatus.LAST;
    private static final Rectangle2D.Double RESULT_BOARD = new Rectangle2D.Double(350, 250, 580, 200);
    private static final Polygon VICTORY_BG = new Polygon(
            new int[]{(int) RESULT_BOARD.getMinX() + 50, (int) RESULT_BOARD.getMaxX() - 50, (int) RESULT_BOARD.getMaxX() - 20, (int) RESULT_BOARD.getMinX() + 20},
            new int[]{(int) RESULT_BOARD.getMinY() - 50, (int) RESULT_BOARD.getMinY() - 50, (int) RESULT_BOARD.getMinY(), (int) RESULT_BOARD.getMinY()},
            4);
    private static final Rectangle2D.Double RETURN_BUTTON = new Rectangle2D.Double(RESULT_BOARD.getCenterX() - 100, RESULT_BOARD.getMaxY() - 65, 200, 50);

    public WindowMatchResult(Screen parent) {
        super(parent);
    }

    public void startCountdown() {
        this.countdownStartTime = Core.getLogicModule().getTime();
        this.translateY = -200;
        this.deltaY = 5;
    }

    public void setVictoryStatus(Globals.VictoryStatus status) {
        this.victoryStatus = status;
    }

    public void addItemGained(final Item item) {
        this.ITEMS_GAINED.add(item);
    }

    public void addExpGained(final int exp) {
        this.expGained += exp;
    }

    @Override
    public void draw(Graphics2D g) {
        final AffineTransform resetForm = g.getTransform();
        g.translate(0, this.translateY);
        Composite reset = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.transparency));
        try {
            g.setColor(BG_COLOUR);
            g.fill(RESULT_BOARD);
            g.fill(VICTORY_BG);
            g.setColor(Color.BLACK);
            g.draw(RESULT_BOARD);
            g.draw(VICTORY_BG);

            g.setColor(LEAVE_BUTTON_COLOUR);
            g.fill(RETURN_BUTTON);
            g.setColor(Color.BLACK);
            g.draw(RETURN_BUTTON);

            switch (victoryStatus) {
                case FIRST:
                    g.setColor(FIRST_PLACE);
                    break;
                case SECOND:
                    g.setColor(SECOND_PLACE);
                    break;
                case THIRD:
                    g.setColor(THIRD_PLACE);
                    break;
                default:
                    g.setColor(Color.WHITE);
                    break;
            }
            g.setFont(Globals.MULAN_24PT.deriveFont(35f));
            g.drawString(victoryStatus.toString(), (int) (VICTORY_BG.getBounds2D().getCenterX() - g.getFontMetrics().stringWidth(victoryStatus.toString()) / 2), (int) (VICTORY_BG.getBounds2D().getMinY() + 37));

            g.setFont(Globals.ARIAL_18PTBOLD);
            g.setColor(REWARDS_COLOUR);
            g.drawString(MATCH_COMPLETION_REWARDS_TEXT, (int) (RESULT_BOARD.getMinX() + 15), (int) (RESULT_BOARD.getMinY() + 30));

            g.drawString(victoryStatus.getBonusesText(), (int) (RESULT_BOARD.getCenterX() - g.getFontMetrics().stringWidth(victoryStatus.getBonusesText()) / 2), (int) (RESULT_BOARD.getMinY() + 55));

            g.setColor(EXP_COLOUR);
            g.drawString(TOTAL_EXP_GAINED_TEXT + Globals.NUMBER_FORMAT.format(this.expGained), (int) (RESULT_BOARD.getMinX() + 15), (int) (RESULT_BOARD.getMinY() + 90));

            g.setFont(Globals.ARIAL_24PTBOLD);
            g.setColor(Color.WHITE);
            String returnText = RETURNING_TO_MENU_IN_TEXT + this.secondsLeftBeforeMenu + SECONDS_TEXT;
            g.drawString(returnText, (int) (RESULT_BOARD.getCenterX() - g.getFontMetrics().stringWidth(returnText) / 2), (int) (RETURN_BUTTON.getMinY() - 15));

            g.setFont(Globals.ARIAL_24PT);
            g.drawString(LEAVE_GAME_TEXT, (int) (RETURN_BUTTON.getCenterX() - g.getFontMetrics().stringWidth(LEAVE_GAME_TEXT) / 2), (int) (RETURN_BUTTON.getMaxY() - 15));
        } finally {
            g.setComposite(reset);
            g.setTransform(resetForm);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point2D.Double scaled;
        if (Globals.WINDOW_SCALE_ENABLED) {
            scaled = new Point2D.Double(e.getX() / Globals.WINDOW_SCALE, e.getY() / Globals.WINDOW_SCALE);
        } else {
            scaled = new Point2D.Double(e.getX(), e.getY());
        }
        if (RETURN_BUTTON.contains(scaled)) {
            PacketSender.sendDisconnect(Core.getLogicModule().getSelectedRoom(), Core.getLogicModule().getMyPlayerKey());
        }
    }

    @Override
    public void update() {
        this.secondsLeftBeforeMenu = 10 - (int) (Globals.nsToMs(Core.getLogicModule().getTime() - this.countdownStartTime) / 1000D);
        this.transparency = Globals.nsToMs(Core.getLogicModule().getTime() - this.countdownStartTime) / 1000f;
        this.transparency = (this.transparency > 1f) ? 1f : this.transparency;
        this.transparency = (this.transparency < 0f) ? 0f : this.transparency;

        this.translateY += this.deltaY;
        this.deltaY += 3;

        if (this.translateY >= 0 && this.deltaY > 0) {
            if (this.deltaY > 9) {
                this.deltaY *= -0.5;
            } else {
                this.deltaY = 0;
                this.translateY = 0;
            }
        }
    }
}
