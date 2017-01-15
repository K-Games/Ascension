package blockfighter.client.screen;

import blockfighter.client.AscensionClient;
import blockfighter.client.net.hub.HubClient;
import blockfighter.shared.Globals;
import blockfighter.shared.ServerInfo;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.apache.commons.io.FileUtils;

public class ScreenServerList extends ScreenMenu {

    private static final String STATUS_LABEL_TEXT = "Status: ";
    private static final String PREV_PAGE_BUTTON_TEXT = "Prev Page";
    private static final String NEXT_PAGE_BUTTON_TEXT = "Next Page";
    private static final String REFRESH_BUTTON_TEXT = "Refresh";
    private static final String CONNECT_BUTTON_TEXT = "Connect";
    private static final String SERVER_LABEL_TEXT = "Server:";
    private static final String CAPACITY_COL_TEXT = "Capacity";
    private static final String REGION_COL_TEXT = "Region";
    private static final String ADDRESS_COL_TEXT = "Address";

    private static final int SERVER_LIST_AREA_Y = 80;
    private static final int SERVER_LIST_AREA_X = 260;
    private static final int SERVER_LIST_AREA_WIDTH = 970;
    private static final int SERVER_LIST_AREA_HEIGHT = 490;
    private static final int SERVER_LIST_ADDRESS_COL_WIDTH = 395;
    private static final int SERVER_LIST_REGION_COL_WIDTH = 395;
    private static final int SERVER_LIST_CAPACITY_COL_WIDTH = 170;
    private static final Color[] CAPACITY_BAR_COLOR = {
        new Color(12, 255, 0),
        new Color(128, 255, 0),
        new Color(255, 255, 0),
        new Color(255, 128, 0),
        new Color(255, 12, 0)
    };

    public static final byte STATUS_CONNECTING = 0,
            STATUS_NORMAL_SHUTDOWN = 1,
            STATUS_FAILEDCONNECT = 2,
            STATUS_NOSKILL = 3,
            STATUS_WRONGVERSION = 4,
            STATUS_FULLROOM = 5,
            STATUS_UIDINROOM = 6,
            STATUS_NO_ROOMS = 7,
            STATUS_NOEQUIP = 8,
            STATUS_NOSKILL_NOEQUIP = 9,
            STATUS_DISCONNECTED = 10,
            STATUS_REFRESHING = 11,
            STATUS_REFRESHING_DONE = 12,
            STATUS_REFRESHING_FAILED = 13;

    private static final JTextField SERVERADDRESS_FIELD = new JTextField();
    private static final Rectangle CONNECT_BOX = new Rectangle(1030, 620, 200, 70);
    private static final Rectangle REFRESH_BOX = new Rectangle(SERVER_LIST_AREA_X, SERVER_LIST_AREA_Y + SERVER_LIST_AREA_HEIGHT, 120, 35);

    private static final Rectangle PREV_PAGE_BOX = new Rectangle(SERVER_LIST_AREA_X + 125, SERVER_LIST_AREA_Y + SERVER_LIST_AREA_HEIGHT, 120, 35);
    private static final Rectangle NEXT_PAGE_BOX = new Rectangle(SERVER_LIST_AREA_X + 125 + 125, SERVER_LIST_AREA_Y + SERVER_LIST_AREA_HEIGHT, 120, 35);

    private String status = "Getting servers...";
    private boolean connecting = false, enabledInput = false;
    private byte statusCode = -1;
    private final ServerList serverList = new ServerList();

    private long lastRefreshTime = 0;

    static {
        SERVERADDRESS_FIELD.addFocusListener(AscensionClient.FOCUS_HANDLER);

        if (Globals.WINDOW_SCALE_ENABLED) {
            SERVERADDRESS_FIELD.setBounds((int) (330 * Globals.WINDOW_SCALE), (int) (640 * Globals.WINDOW_SCALE), (int) (670 * Globals.WINDOW_SCALE), (int) (40 * Globals.WINDOW_SCALE));
            SERVERADDRESS_FIELD.setFont(new Font(Globals.ARIAL_24PT.getFontName(), Globals.ARIAL_24PT.getStyle(), (int) (Globals.ARIAL_24PT.getSize() * Globals.WINDOW_SCALE)));
        } else {
            SERVERADDRESS_FIELD.setBounds(330, 640, 670, 40);
            SERVERADDRESS_FIELD.setFont(Globals.ARIAL_24PT);
        }
        SERVERADDRESS_FIELD.setForeground(Color.WHITE);
        SERVERADDRESS_FIELD.setBackground(Color.BLACK);
        SERVERADDRESS_FIELD.setCaretColor(Color.WHITE);
        SERVERADDRESS_FIELD.setOpaque(true);
        SERVERADDRESS_FIELD.setText(loadServerList());
    }

    public ScreenServerList() {
        this(false);
        AscensionClient.SHARED_THREADPOOL.execute(new HubClient());
    }

    public ScreenServerList(final boolean fadeIn) {
        super(fadeIn);
        javax.swing.SwingUtilities.invokeLater(() -> {
            panel.add(SERVERADDRESS_FIELD);
            panel.revalidate();
        });
        this.enabledInput = !fadeIn;
    }

    @Override
    public void update() {
        super.update();
        if (this.fadeIn && this.finishedFadeIn && !this.enabledInput) {
            enableFields();
            this.enabledInput = true;
        }
        if (HubClient.getServerInfo() != null) {
            serverList.setServerInfo(HubClient.getServerInfo());
        }
    }

    public static String loadServerList() {
        try {
            return FileUtils.readLines(new File("server.txt"), StandardCharsets.UTF_8).get(0);
        } catch (final Exception ex) {
            return "";
        }
    }

    public static void saveServerList(final String address) {
        try {
            FileUtils.writeStringToFile(new File("server.txt"), address, StandardCharsets.UTF_8);
        } catch (final Exception ex) {
            Globals.logError(ex.toString(), ex);
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        final BufferedImage bg = Globals.MENU_BG[1];
        g.drawImage(bg, 0, 0, null);

        g.setColor(SKILL_BOX_BG_COLOR);
        g.fillRoundRect(SERVER_LIST_AREA_X, SERVER_LIST_AREA_Y - 35, SERVER_LIST_ADDRESS_COL_WIDTH, 30, 10, 10);
        g.fillRoundRect(SERVER_LIST_AREA_X + SERVER_LIST_ADDRESS_COL_WIDTH + 5, SERVER_LIST_AREA_Y - 35, SERVER_LIST_REGION_COL_WIDTH, 30, 10, 10);
        g.fillRoundRect(SERVER_LIST_AREA_X + SERVER_LIST_ADDRESS_COL_WIDTH + 10 + SERVER_LIST_REGION_COL_WIDTH, SERVER_LIST_AREA_Y - 35, SERVER_LIST_CAPACITY_COL_WIDTH, 30, 10, 10);
        g.fillRoundRect(SERVER_LIST_AREA_X, SERVER_LIST_AREA_Y, SERVER_LIST_AREA_WIDTH, SERVER_LIST_AREA_HEIGHT, 15, 15);

        String label = ADDRESS_COL_TEXT;
        g.setFont(Globals.ARIAL_15PT);
        drawStringOutline(g, label, SERVER_LIST_AREA_X + 15, SERVER_LIST_AREA_Y - 15, 2);
        g.setColor(Color.WHITE);
        g.drawString(label, SERVER_LIST_AREA_X + 15, SERVER_LIST_AREA_Y - 15);

        label = REGION_COL_TEXT;
        g.setFont(Globals.ARIAL_15PT);
        drawStringOutline(g, label, SERVER_LIST_AREA_X + SERVER_LIST_ADDRESS_COL_WIDTH + 20, SERVER_LIST_AREA_Y - 15, 2);
        g.setColor(Color.WHITE);
        g.drawString(label, SERVER_LIST_AREA_X + SERVER_LIST_ADDRESS_COL_WIDTH + 20, SERVER_LIST_AREA_Y - 15);

        label = CAPACITY_COL_TEXT;
        g.setFont(Globals.ARIAL_15PT);
        drawStringOutline(g, label, SERVER_LIST_AREA_X + SERVER_LIST_ADDRESS_COL_WIDTH + SERVER_LIST_REGION_COL_WIDTH + 30, SERVER_LIST_AREA_Y - 15, 2);
        g.setColor(Color.WHITE);
        g.drawString(label, SERVER_LIST_AREA_X + SERVER_LIST_ADDRESS_COL_WIDTH + SERVER_LIST_REGION_COL_WIDTH + 30, SERVER_LIST_AREA_Y - 15);

        label = SERVER_LABEL_TEXT;
        g.setFont(Globals.ARIAL_24PT);
        final int labelX = (int) (330 - g.getFontMetrics().stringWidth(label) - 5), labelY = (int) (640 + 27);
        g.setColor(Color.BLACK);
        drawStringOutline(g, label, labelX, labelY, 2);
        g.setColor(Color.WHITE);
        g.drawString(label, labelX, labelY);

        g.drawRect(CONNECT_BOX.x, CONNECT_BOX.y, CONNECT_BOX.width, CONNECT_BOX.height);
        g.setColor(new Color(30, 30, 30, 255));
        g.fillRect(CONNECT_BOX.x, CONNECT_BOX.y, CONNECT_BOX.width, CONNECT_BOX.height);
        g.setColor(Color.BLACK);
        g.drawRect(CONNECT_BOX.x, CONNECT_BOX.y, CONNECT_BOX.width, CONNECT_BOX.height);
        g.drawRect(CONNECT_BOX.x + 1, CONNECT_BOX.y + 1, CONNECT_BOX.width - 2, CONNECT_BOX.height - 2);

        g.setFont(Globals.ARIAL_24PT);
        g.setColor(Color.WHITE);
        String buttonLabel = CONNECT_BUTTON_TEXT;
        g.drawString(buttonLabel, CONNECT_BOX.x + CONNECT_BOX.width / 2 - g.getFontMetrics().stringWidth(buttonLabel) / 2, CONNECT_BOX.y + CONNECT_BOX.height / 2 + g.getFontMetrics().getHeight() / 3);

        g.drawRect(REFRESH_BOX.x, REFRESH_BOX.y, REFRESH_BOX.width, REFRESH_BOX.height);
        g.setColor(new Color(30, 30, 30, 255));
        g.fillRect(REFRESH_BOX.x, REFRESH_BOX.y, REFRESH_BOX.width, REFRESH_BOX.height);
        g.setColor(Color.BLACK);
        g.drawRect(REFRESH_BOX.x, REFRESH_BOX.y, REFRESH_BOX.width, REFRESH_BOX.height);
        g.drawRect(REFRESH_BOX.x + 1, REFRESH_BOX.y + 1, REFRESH_BOX.width - 2, REFRESH_BOX.height - 2);

        g.setFont(Globals.ARIAL_15PT);
        g.setColor(Color.WHITE);
        buttonLabel = REFRESH_BUTTON_TEXT;
        g.drawString(buttonLabel, REFRESH_BOX.x + REFRESH_BOX.width / 2 - g.getFontMetrics().stringWidth(buttonLabel) / 2, REFRESH_BOX.y + REFRESH_BOX.height / 2 + g.getFontMetrics().getHeight() / 3);

        g.drawRect(NEXT_PAGE_BOX.x, NEXT_PAGE_BOX.y, NEXT_PAGE_BOX.width, NEXT_PAGE_BOX.height);
        g.setColor(new Color(30, 30, 30, 255));
        g.fillRect(NEXT_PAGE_BOX.x, NEXT_PAGE_BOX.y, NEXT_PAGE_BOX.width, NEXT_PAGE_BOX.height);
        g.setColor(Color.BLACK);
        g.drawRect(NEXT_PAGE_BOX.x, NEXT_PAGE_BOX.y, NEXT_PAGE_BOX.width, NEXT_PAGE_BOX.height);
        g.drawRect(NEXT_PAGE_BOX.x + 1, NEXT_PAGE_BOX.y + 1, NEXT_PAGE_BOX.width - 2, NEXT_PAGE_BOX.height - 2);

        g.setFont(Globals.ARIAL_15PT);
        g.setColor(Color.WHITE);
        buttonLabel = NEXT_PAGE_BUTTON_TEXT;
        g.drawString(buttonLabel, NEXT_PAGE_BOX.x + NEXT_PAGE_BOX.width / 2 - g.getFontMetrics().stringWidth(buttonLabel) / 2, NEXT_PAGE_BOX.y + NEXT_PAGE_BOX.height / 2 + g.getFontMetrics().getHeight() / 3);

        g.drawRect(PREV_PAGE_BOX.x, PREV_PAGE_BOX.y, PREV_PAGE_BOX.width, PREV_PAGE_BOX.height);
        g.setColor(new Color(30, 30, 30, 255));
        g.fillRect(PREV_PAGE_BOX.x, PREV_PAGE_BOX.y, PREV_PAGE_BOX.width, PREV_PAGE_BOX.height);
        g.setColor(Color.BLACK);
        g.drawRect(PREV_PAGE_BOX.x, PREV_PAGE_BOX.y, PREV_PAGE_BOX.width, PREV_PAGE_BOX.height);
        g.drawRect(PREV_PAGE_BOX.x + 1, PREV_PAGE_BOX.y + 1, PREV_PAGE_BOX.width - 2, PREV_PAGE_BOX.height - 2);

        g.setFont(Globals.ARIAL_15PT);
        g.setColor(Color.WHITE);
        buttonLabel = PREV_PAGE_BUTTON_TEXT;
        g.drawString(buttonLabel, PREV_PAGE_BOX.x + PREV_PAGE_BOX.width / 2 - g.getFontMetrics().stringWidth(buttonLabel) / 2, PREV_PAGE_BOX.y + PREV_PAGE_BOX.height / 2 + g.getFontMetrics().getHeight() / 3);

        g.setFont(Globals.ARIAL_15PT);
        drawStringOutline(g, STATUS_LABEL_TEXT + this.status, 650, SERVER_LIST_AREA_Y + SERVER_LIST_AREA_HEIGHT + 25, 2);
        g.setColor(Color.WHITE);
        g.drawString(STATUS_LABEL_TEXT + this.status, 650, SERVER_LIST_AREA_Y + SERVER_LIST_AREA_HEIGHT + 25);

        if (serverList != null) {
            serverList.draw(g);
        }

        drawMenuButton(g);
        super.draw(g);
    }

    @Override
    public void keyTyped(final KeyEvent e) {

    }

    @Override
    public void keyPressed(final KeyEvent e) {

    }

    @Override
    public void keyReleased(final KeyEvent e) {

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
        if (connecting || !enabledInput) {
            return;
        }
        super.mouseReleased(e);
        if (this.serverList != null) {
            this.serverList.mouseReleased(e, scaled);
        }
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (CONNECT_BOX.contains(scaled)) {
                // Connect
                if (SERVERADDRESS_FIELD.getText().trim().length() > 0) {
                    connecting = true;
                    SERVERADDRESS_FIELD.setEnabled(false);
                    saveServerList(SERVERADDRESS_FIELD.getText().trim());
                    logic.connect(SERVERADDRESS_FIELD.getText().trim());
                }
            }
            if (REFRESH_BOX.contains(scaled) && logic.getTime() - this.lastRefreshTime >= Globals.msToNs(2000)) {
                setStatus(STATUS_REFRESHING);
                this.serverList.setServerInfo(null);
                AscensionClient.SHARED_THREADPOOL.execute(new HubClient());
                this.lastRefreshTime = logic.getTime();
            }

            if (PREV_PAGE_BOX.contains(scaled)) {
                this.serverList.prevPage();
            }
            if (NEXT_PAGE_BOX.contains(scaled)) {
                this.serverList.nextPage();
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

    }

    @Override
    public void mouseMoved(final MouseEvent e) {

    }

    @Override
    public void unload() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            panel.remove(SERVERADDRESS_FIELD);
            panel.revalidate();
        });
        saveServerList(SERVERADDRESS_FIELD.getText().trim());
    }

    public void setStatus(final byte code) {
        if ((code == STATUS_FAILEDCONNECT || code == STATUS_DISCONNECTED) && this.statusCode != 0) {
            return;
        }
        this.statusCode = code;
        switch (code) {
            case STATUS_CONNECTING:
                this.status = "Connecting...";
                break;
            case STATUS_NORMAL_SHUTDOWN:
                enableFields();
                this.status = "Could not connect: Connection closed.";
                break;
            case STATUS_DISCONNECTED:
                enableFields();
                this.status = "Could not connect: Disconnected by server.";
                break;
            case STATUS_FAILEDCONNECT:
                enableFields();
                this.status = "Could not connect: Cannot reach server.";
                break;
            case STATUS_WRONGVERSION:
                enableFields();
                this.status = "Could not connect: Server is a different version.";
                break;
            case STATUS_FULLROOM:
                enableFields();
                this.status = "Could not connect: Room is full.";
                break;
            case STATUS_UIDINROOM:
                enableFields();
                this.status = "Could not connect: This character is already in the room.";
                break;
            case STATUS_NO_ROOMS:
                enableFields();
                this.status = "Could not connect: No rooms available for your level.";
                break;
            case STATUS_NOSKILL:
                enableFields();
                this.status = "Character is not ready: You don't have any Skills assigned!";
                break;
            case STATUS_NOEQUIP:
                enableFields();
                this.status = "Character is not ready: You haven't equipped a Weapon!";
                break;
            case STATUS_NOSKILL_NOEQUIP:
                enableFields();
                this.status = "Character is not ready: You haven't equipped any Skills or Weapons!";
                break;
            case STATUS_REFRESHING:
                this.status = "Refresing server list...";
                break;
            case STATUS_REFRESHING_DONE:
                this.status = "Done refreshing server list.";
                break;
            case STATUS_REFRESHING_FAILED:
                this.status = "Failed to get server list.";
                break;
            default:
                enableFields();
                this.status = "Could not connect: Unknown Status";
        }
    }

    public void enableFields() {
        logic.stopCharacterLoginAttemptTimeout();
        connecting = false;
        SERVERADDRESS_FIELD.setVisible(true);
        SERVERADDRESS_FIELD.setEnabled(true);
    }

    private class ServerList {

        private static final int SERVERS_PER_PAGE = 14;
        private int page = 0, selectedIndex = -1;
        private ServerInfo[] serverInfo;
        private Rectangle[] serverListBox;

        public void draw(final Graphics2D g) {
            if (serverInfo == null || serverListBox == null) {
                return;
            }
            for (int i = page * SERVERS_PER_PAGE; i < page * SERVERS_PER_PAGE + SERVERS_PER_PAGE; i++) {
                if (i < serverInfo.length) {
                    if (serverInfo[i] != null) {
                        g.setColor(Color.BLACK);
                        g.fillRoundRect(serverListBox[i].x, serverListBox[i].y, SERVER_LIST_ADDRESS_COL_WIDTH, serverListBox[i].height, 10, 10);
                        g.fillRoundRect(serverListBox[i].x + SERVER_LIST_ADDRESS_COL_WIDTH + 5, serverListBox[i].y, SERVER_LIST_REGION_COL_WIDTH, serverListBox[i].height, 10, 10);
                        g.fillRoundRect(serverListBox[i].x + SERVER_LIST_ADDRESS_COL_WIDTH + SERVER_LIST_REGION_COL_WIDTH + 10, serverListBox[i].y, SERVER_LIST_CAPACITY_COL_WIDTH, serverListBox[i].height, 10, 10);

                        if (selectedIndex == i) {
                            g.setColor(Color.WHITE);
                            g.drawRoundRect(serverListBox[i].x, serverListBox[i].y, SERVER_LIST_ADDRESS_COL_WIDTH, serverListBox[i].height, 10, 10);
                            g.drawRoundRect(serverListBox[i].x + SERVER_LIST_ADDRESS_COL_WIDTH + 5, serverListBox[i].y, SERVER_LIST_REGION_COL_WIDTH, serverListBox[i].height, 10, 10);
                            g.drawRoundRect(serverListBox[i].x + SERVER_LIST_ADDRESS_COL_WIDTH + SERVER_LIST_REGION_COL_WIDTH + 10, serverListBox[i].y, SERVER_LIST_CAPACITY_COL_WIDTH, serverListBox[i].height, 10, 10);

                        }
                        g.setColor(Color.WHITE);
                        g.setFont(Globals.ARIAL_15PT);
                        g.drawString(serverInfo[i].getAddress(), serverListBox[i].x + 15, serverListBox[i].y + 20);
                        g.drawString(serverInfo[i].getRegion(), serverListBox[i].x + SERVER_LIST_ADDRESS_COL_WIDTH + 20, serverListBox[i].y + 20);
                        for (int capacity = 0; capacity < Math.round(serverInfo[i].getCapacity() / 10f); capacity++) {
                            g.setColor(CAPACITY_BAR_COLOR[capacity / 2]);
                            g.fillRect(capacity * 13 + serverListBox[i].x + SERVER_LIST_ADDRESS_COL_WIDTH + SERVER_LIST_REGION_COL_WIDTH + 30, serverListBox[i].y + 10, 8, 10);
                        }
                        for (int capBack = 0; capBack < 10; capBack++) {
                            g.setColor(Color.WHITE);
                            g.drawRect(capBack * 13 + serverListBox[i].x + SERVER_LIST_ADDRESS_COL_WIDTH + SERVER_LIST_REGION_COL_WIDTH + 30, serverListBox[i].y + 10, 8, 10);
                        }

                    }
                }
            }
        }

        public void setServerInfo(final ServerInfo[] list) {
            if (list == null) {
                this.serverInfo = null;
                this.serverListBox = null;
                this.page = 0;
                this.selectedIndex = -1;
                return;
            }

            if (this.serverInfo != list) {
                setStatus(STATUS_REFRESHING_DONE);
                this.serverInfo = list;
                this.serverListBox = new Rectangle[this.serverInfo.length];
                this.page = 0;
                this.selectedIndex = -1;
                for (int i = 0; i < this.serverListBox.length; i++) {
                    this.serverListBox[i] = new Rectangle(SERVER_LIST_AREA_X, SERVER_LIST_AREA_Y + i % SERVERS_PER_PAGE * 35, SERVER_LIST_AREA_WIDTH, 30);
                }
            }
        }

        public void mouseReleased(final MouseEvent e, final Point2D.Double scaled) {
            if (serverListBox != null) {
                for (int i = page * SERVERS_PER_PAGE; i < page * SERVERS_PER_PAGE + SERVERS_PER_PAGE; i++) {
                    if (i < serverInfo.length && this.serverListBox[i].contains(scaled)) {
                        this.selectedIndex = i;
                        SERVERADDRESS_FIELD.setText(getSelectedServer().getAddress());
                        break;
                    }
                }
            }
        }

        private ServerInfo getSelectedServer() {
            return this.serverInfo[this.selectedIndex];
        }

        public void nextPage() {
            int nextPage = page + 1;
            if (nextPage * SERVERS_PER_PAGE < serverInfo.length) {
                this.page++;
                if (this.page < 0) {
                    this.page = 0;
                }
            }
        }

        public void prevPage() {
            int nextPage = page - 1;
            if (nextPage * SERVERS_PER_PAGE >= 0) {
                this.page--;
            }
        }
    }
}
