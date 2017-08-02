package blockfighter.client.screen.window;

import blockfighter.client.Core;
import blockfighter.client.entities.player.Player;
import blockfighter.client.screen.Screen;
import blockfighter.client.screen.ScreenIngame;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Arrays;

public class WindowScoreboard extends Window {

    private static final Color SCOREBOARD_BG_COLOR = new Color(0, 0, 0, 190);

    private static final int SCOREBOARD_SPACING = 5;
    private static final int SCOREBOARD_Y = 120;
    private static final int SCOREBOARD_ROW_HEIGHT = 30;

    private static final int SCOREBOARD_PLAYER_NAME_X = 220;
    private static final int SCOREBOARD_PLAYER_NAME_COL_WIDTH = 400;

    private static final int SCOREBOARD_PLAYER_LEVEL_X = SCOREBOARD_PLAYER_NAME_X + SCOREBOARD_PLAYER_NAME_COL_WIDTH + SCOREBOARD_SPACING;
    private static final int SCOREBOARD_PLAYER_LEVEL_COL_WIDTH = 150;

    private static final int SCOREBOARD_PLAYER_SCORE_X = SCOREBOARD_PLAYER_LEVEL_X + SCOREBOARD_PLAYER_LEVEL_COL_WIDTH + SCOREBOARD_SPACING;
    private static final int SCOREBOARD_PLAYER_SCORE_COL_WIDTH = 150;

    private static final int SCOREBOARD_PLAYER_PING_X = SCOREBOARD_PLAYER_SCORE_X + SCOREBOARD_PLAYER_SCORE_COL_WIDTH + SCOREBOARD_SPACING;
    private static final int SCOREBOARD_PLAYER_PING_COL_WIDTH = 100;

    private static final String SCOREBOARD_PLAYER_NAME_TEXT = "Player Name";
    private static final String SCOREBOARD_PLAYER_LEVEL_TEXT = "Level";
    private static final String SCOREBOARD_PLAYER_SCORE_TEXT = "Score";
    private static final String SCOREBOARD_PLAYER_PING_TEXT = "Ping";

    private Player[] scoreboardList;

    public WindowScoreboard(Screen parent) {
        super(parent);
    }

    @Override
    public void draw(Graphics2D g) {
        final AffineTransform resetForm = g.getTransform();
        Composite reset = g.getComposite();
        try {
            drawScoreboard(g);
        } finally {
            g.setComposite(reset);
            g.setTransform(resetForm);
        }
    }

    private void drawScoreboard(final Graphics2D g) {
        g.setColor(SCOREBOARD_BG_COLOR);
        g.fillRoundRect(SCOREBOARD_PLAYER_NAME_X, SCOREBOARD_Y, SCOREBOARD_PLAYER_NAME_COL_WIDTH, SCOREBOARD_ROW_HEIGHT, 10, 10);
        g.fillRoundRect(SCOREBOARD_PLAYER_LEVEL_X, SCOREBOARD_Y, SCOREBOARD_PLAYER_LEVEL_COL_WIDTH, SCOREBOARD_ROW_HEIGHT, 10, 10);
        g.fillRoundRect(SCOREBOARD_PLAYER_SCORE_X, SCOREBOARD_Y, SCOREBOARD_PLAYER_SCORE_COL_WIDTH, SCOREBOARD_ROW_HEIGHT, 10, 10);
        g.fillRoundRect(SCOREBOARD_PLAYER_PING_X, SCOREBOARD_Y, SCOREBOARD_PLAYER_PING_COL_WIDTH, SCOREBOARD_ROW_HEIGHT, 10, 10);

        g.setColor(Color.WHITE);
        g.drawString(SCOREBOARD_PLAYER_NAME_TEXT, SCOREBOARD_PLAYER_NAME_X + 10, SCOREBOARD_Y + 20);
        g.drawString(SCOREBOARD_PLAYER_LEVEL_TEXT, SCOREBOARD_PLAYER_LEVEL_X + 10, SCOREBOARD_Y + 20);
        g.drawString(SCOREBOARD_PLAYER_SCORE_TEXT, SCOREBOARD_PLAYER_SCORE_X + 10, SCOREBOARD_Y + 20);
        g.drawString(SCOREBOARD_PLAYER_PING_TEXT, SCOREBOARD_PLAYER_PING_X + 10, SCOREBOARD_Y + 20);

        g.setFont(Globals.ARIAL_15PT);

        boolean drawMyScore = true;

        for (int i = 0; i < 10 && i < this.scoreboardList.length; i++) {
            Player player = this.scoreboardList[i];
            if (player == null) {
                continue;
            }
            drawScore(g, player, i);
            if (player.getKey() == Core.getLogicModule().getMyPlayerKey()) {
                drawMyScore = false;
            }
        }
        if (drawMyScore) {
            drawScore(g, ((ScreenIngame) this.parentScreen).getPlayers().get(Core.getLogicModule().getMyPlayerKey()), 10);
        }
    }

    private void drawScore(final Graphics2D g, final Player player, final int row) {
        int rowY = SCOREBOARD_Y + SCOREBOARD_ROW_HEIGHT + 10 + row * (SCOREBOARD_ROW_HEIGHT + SCOREBOARD_SPACING);

        if (!player.getPlayerName().isEmpty()) {
            g.setColor(SCOREBOARD_BG_COLOR);
            g.fillRoundRect(SCOREBOARD_PLAYER_NAME_X, rowY, SCOREBOARD_PLAYER_NAME_COL_WIDTH, SCOREBOARD_ROW_HEIGHT, 10, 10);
            g.fillRoundRect(SCOREBOARD_PLAYER_LEVEL_X, rowY, SCOREBOARD_PLAYER_LEVEL_COL_WIDTH, SCOREBOARD_ROW_HEIGHT, 10, 10);
            g.fillRoundRect(SCOREBOARD_PLAYER_SCORE_X, rowY, SCOREBOARD_PLAYER_SCORE_COL_WIDTH, SCOREBOARD_ROW_HEIGHT, 10, 10);
            g.fillRoundRect(SCOREBOARD_PLAYER_PING_X, rowY, SCOREBOARD_PLAYER_PING_COL_WIDTH, SCOREBOARD_ROW_HEIGHT, 10, 10);

            g.setColor(player.getPlayerColor());
            g.fillRoundRect(SCOREBOARD_PLAYER_NAME_X + 1, rowY, 5, SCOREBOARD_ROW_HEIGHT, 10, 10);

            g.setColor(Color.WHITE);
            if (player.getKey() == Core.getLogicModule().getMyPlayerKey()) {
                g.drawRoundRect(SCOREBOARD_PLAYER_NAME_X, rowY, SCOREBOARD_PLAYER_NAME_COL_WIDTH, SCOREBOARD_ROW_HEIGHT, 10, 10);
                g.drawRoundRect(SCOREBOARD_PLAYER_LEVEL_X, rowY, SCOREBOARD_PLAYER_LEVEL_COL_WIDTH, SCOREBOARD_ROW_HEIGHT, 10, 10);
                g.drawRoundRect(SCOREBOARD_PLAYER_SCORE_X, rowY, SCOREBOARD_PLAYER_SCORE_COL_WIDTH, SCOREBOARD_ROW_HEIGHT, 10, 10);
                g.drawRoundRect(SCOREBOARD_PLAYER_PING_X, rowY, SCOREBOARD_PLAYER_PING_COL_WIDTH, SCOREBOARD_ROW_HEIGHT, 10, 10);
            }
            g.drawString(player.getPlayerName(), SCOREBOARD_PLAYER_NAME_X + 10, rowY + 20);
            if (player.getStat(Globals.STAT_LEVEL) > 0) {
                g.drawString(Integer.toString((int) player.getStat(Globals.STAT_LEVEL)), SCOREBOARD_PLAYER_LEVEL_X + 10, rowY + 20);
            }
            if (player.getScore() > -1) {
                g.drawString(Integer.toString(player.getScore()), SCOREBOARD_PLAYER_SCORE_X + 10, rowY + 20);
            }
            g.drawString(Integer.toString(player.getPing()), SCOREBOARD_PLAYER_PING_X + 10, rowY + 20);
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
    }

    @Override
    public void update() {
        if (this.parentScreen instanceof ScreenIngame) {
            this.scoreboardList = ((ScreenIngame) this.parentScreen).getPlayers().values().toArray(new Player[0]);
            Arrays.sort(this.scoreboardList, (Player a, Player b) -> b.getScore() - a.getScore());
        }
    }
}
