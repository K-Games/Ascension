package blockfighter.client.screen;

import blockfighter.client.AscensionClient;
import blockfighter.shared.Globals;
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
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.apache.commons.io.FileUtils;

public class ScreenServerList extends ScreenMenu {

    public static final byte STATUS_CONNECTING = 0,
            STATUS_NORMAL_SHUTDOWN = 1,
            STATUS_FAILEDCONNECT = 2,
            STATUS_NOSKILL = 3,
            STATUS_WRONGVERSION = 4,
            STATUS_FULLROOM = 5,
            STATUS_UIDINROOM = 6,
            STATUS_OUTSIDELEVEL = 7,
            STATUS_NOEQUIP = 8,
            STATUS_NOSKILL_NOEQUIP = 9,
            STATUS_DISCONNECTED = 10;

    private static final JTextField SERVERADDRESS_FIELD = new JTextField();
    private static final JComboBox<String> SERVER_ROOMS;
    private static final Rectangle CONNECT_BOX = new Rectangle(650, 230, 200, 70);
    private String status = "Waiting to login...";
    private boolean connecting = false, enabledInput = false;
    private byte statusCode = -1;

    static {
        String[] listItems = new String[10];

        for (int i = 0; i < listItems.length; i++) {
            listItems[i] = "Lvl " + (i * 10 + 1) + "-" + ((i + 1) * 10);
        }
        SERVER_ROOMS = new JComboBox<>(listItems);
        SERVERADDRESS_FIELD.addFocusListener(AscensionClient.FOCUS_HANDLER);
        SERVER_ROOMS.addFocusListener(AscensionClient.FOCUS_HANDLER);

        if (Globals.WINDOW_SCALE_ENABLED) {
            SERVERADDRESS_FIELD.setBounds((int) (550 * Globals.WINDOW_SCALE), (int) (150 * Globals.WINDOW_SCALE), (int) (400 * Globals.WINDOW_SCALE), (int) (40 * Globals.WINDOW_SCALE));
            SERVERADDRESS_FIELD.setFont(new Font(Globals.ARIAL_24PT.getFontName(), Globals.ARIAL_24PT.getStyle(), (int) (Globals.ARIAL_24PT.getSize() * Globals.WINDOW_SCALE)));
        } else {
            SERVERADDRESS_FIELD.setBounds(550, 150, 400, 40);
            SERVERADDRESS_FIELD.setFont(Globals.ARIAL_24PT);
        }
        SERVERADDRESS_FIELD.setForeground(Color.WHITE);
        SERVERADDRESS_FIELD.setBackground(Color.BLACK);
        SERVERADDRESS_FIELD.setCaretColor(Color.WHITE);
        SERVERADDRESS_FIELD.setOpaque(true);
        SERVERADDRESS_FIELD.setText(loadServerList());
        if (Globals.WINDOW_SCALE_ENABLED) {
            SERVER_ROOMS.setBounds((int) (1000 * Globals.WINDOW_SCALE), (int) (150 * Globals.WINDOW_SCALE), (int) (150 * Globals.WINDOW_SCALE), (int) (40 * Globals.WINDOW_SCALE));
            SERVER_ROOMS.setFont(new Font(Globals.ARIAL_24PT.getFontName(), Globals.ARIAL_24PT.getStyle(), (int) (Globals.ARIAL_24PT.getSize() * Globals.WINDOW_SCALE)));
        } else {
            SERVER_ROOMS.setBounds(1000, 150, 150, 40);
            SERVER_ROOMS.setFont(Globals.ARIAL_24PT);
        }
        SERVER_ROOMS.setForeground(Color.WHITE);
        SERVER_ROOMS.setBackground(Color.BLACK);
        SERVER_ROOMS.setOpaque(true);

    }

    public ScreenServerList() {
        this(false);
    }

    public ScreenServerList(final boolean fadeIn) {
        super(fadeIn);
        javax.swing.SwingUtilities.invokeLater(() -> {
            panel.add(SERVERADDRESS_FIELD);
            panel.add(SERVER_ROOMS);
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
            System.err.println("saveServerList: Failed to save server name.");
        }
    }

    @Override
    public void draw(final Graphics2D g) {
        final BufferedImage bg = Globals.MENU_BG[1];
        g.drawImage(bg, 0, 0, null);

        String title = "Login to Server";
        g.setFont(Globals.ARIAL_30PT);
        final int titleX = 750 - g.getFontMetrics().stringWidth(title) / 2, titleY = 90;

        drawStringOutline(g, title, titleX, titleY, 2);
        g.setColor(Color.WHITE);
        g.drawString(title, titleX, titleY);

        String label = "Server:";
        g.setFont(Globals.ARIAL_24PT);
        final int labelX = 550 - g.getFontMetrics().stringWidth(label) - 5, labelY = 177;
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
        String buttonLabel = "Login";
        g.drawString(buttonLabel, CONNECT_BOX.x + CONNECT_BOX.width / 2 - g.getFontMetrics().stringWidth(buttonLabel) / 2, CONNECT_BOX.y + CONNECT_BOX.height / 2 + g.getFontMetrics().getHeight() / 3);

        g.setFont(Globals.ARIAL_24PT);
        final int strWidth = g.getFontMetrics().stringWidth(this.status);
        drawStringOutline(g, this.status, 750 - strWidth / 2, 350, 2);
        g.setColor(Color.WHITE);
        g.drawString(this.status, 750 - strWidth / 2, 350);

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
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (CONNECT_BOX.contains(scaled)) {
                // Connect
                if (SERVERADDRESS_FIELD.getText().trim().length() > 0) {
                    connecting = true;
                    SERVER_ROOMS.setEnabled(false);
                    SERVERADDRESS_FIELD.setEnabled(false);
                    saveServerList(SERVERADDRESS_FIELD.getText().trim());
                    logic.connect(SERVERADDRESS_FIELD.getText().trim(), (byte) SERVER_ROOMS.getSelectedIndex());
                }
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
            panel.remove(SERVER_ROOMS);
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
            case STATUS_OUTSIDELEVEL:
                enableFields();
                this.status = "Could not connect: This character does not meet the level requirements.";
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
            default:
                enableFields();
                this.status = "Could not connect: Unknown Status";
        }
    }

    public void enableFields() {
        connecting = false;
        SERVERADDRESS_FIELD.setVisible(true);
        SERVERADDRESS_FIELD.setEnabled(true);
        SERVER_ROOMS.setEnabled(true);
    }
}
