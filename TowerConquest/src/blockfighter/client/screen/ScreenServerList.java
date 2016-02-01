package blockfighter.client.screen;

import blockfighter.client.FocusHandler;
import blockfighter.client.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Ken Kwan
 */
public class ScreenServerList extends ScreenMenu {

    public static final byte STATUS_CONNECTING = 0,
            STATUS_SOCKETCLOSED = 1,
            STATUS_FAILEDCONNECT = 2,
            STATUS_UNKNOWNHOST = 3,
            STATUS_WRONGVERSION = 4;

    private final JTextField SERVERADDRESS_FIELD = new JTextField();
    private final JComboBox<String> SERVER_ROOMS = new JComboBox<>();
    private final Rectangle connect = new Rectangle(650, 230, 200, 70);
    private String status = "Waiting to connect...";
    private boolean connecting = false;

    public ScreenServerList() {
        final FocusHandler focusHandler = new FocusHandler();
        this.SERVERADDRESS_FIELD.addFocusListener(focusHandler);
        this.SERVER_ROOMS.addFocusListener(focusHandler);

        this.SERVERADDRESS_FIELD.setBounds(550, 150, 400, 40);
        this.SERVERADDRESS_FIELD.setFont(Globals.ARIAL_24PT);
        this.SERVERADDRESS_FIELD.setForeground(Color.WHITE);
        this.SERVERADDRESS_FIELD.setBackground(Color.BLACK);
        this.SERVERADDRESS_FIELD.setCaretColor(Color.WHITE);
        this.SERVERADDRESS_FIELD.setOpaque(true);
        this.SERVERADDRESS_FIELD.setText(loadServerList());

        try {
            this.SERVER_ROOMS.addItem("Arena");
            for (int i = 0; i < 100; i++) {
                this.SERVER_ROOMS.addItem("Level " + (i + 1));
            }
        } catch (final Exception e) {

        }
        this.SERVER_ROOMS.setFont(Globals.ARIAL_24PT);
        this.SERVER_ROOMS.setForeground(Color.WHITE);
        this.SERVER_ROOMS.setBackground(Color.BLACK);
        this.SERVER_ROOMS.setOpaque(true);
        this.SERVER_ROOMS.setBounds(1000, 150, 150, 40);

        javax.swing.SwingUtilities.invokeLater(() -> {
            panel.add(this.SERVERADDRESS_FIELD);
            panel.add(this.SERVER_ROOMS);
            panel.revalidate();
        });
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
        final BufferedImage bg = Globals.MENU_BG[4];
        g.drawImage(bg, 0, 0, null);

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        g.setFont(Globals.ARIAL_30PT);
        drawStringOutline(g, "Connect To Server", 600, 90, 2);
        g.setColor(Color.WHITE);
        g.drawString("Connect To Server", 600, 90);

        g.setFont(Globals.ARIAL_24PT);
        drawStringOutline(g, "Host: ", 490, 177, 2);
        g.setColor(Color.WHITE);
        g.drawString("Host: ", 490, 177);

        g.drawRect(this.connect.x, this.connect.y, this.connect.width, this.connect.height);
        g.setColor(new Color(30, 30, 30, 255));
        g.fillRect(this.connect.x, this.connect.y, this.connect.width, this.connect.height);
        g.setColor(Color.BLACK);
        g.drawRect(this.connect.x, this.connect.y, this.connect.width, this.connect.height);
        g.drawRect(this.connect.x + 1, this.connect.y + 1, this.connect.width - 2, this.connect.height - 2);

        g.setFont(Globals.ARIAL_24PT);
        g.setColor(Color.WHITE);
        g.drawString("Connect", this.connect.x + 55, this.connect.y + 40);

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
        if (this.connecting) {
            return;
        }
        super.mouseReleased(e);
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (this.connect.contains(e.getPoint())) {
                // Connect
                if (this.SERVERADDRESS_FIELD.getText().trim().length() > 0) {
                    this.connecting = true;
                    saveServerList(this.SERVERADDRESS_FIELD.getText().trim());
                    logic.sendLogin(this.SERVERADDRESS_FIELD.getText().trim(), (byte) this.SERVER_ROOMS.getSelectedIndex());
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
            panel.remove(this.SERVERADDRESS_FIELD);
            panel.remove(this.SERVER_ROOMS);
            panel.revalidate();
        });
        saveServerList(this.SERVERADDRESS_FIELD.getText().trim());
    }

    public void setStatus(final byte code) {
        switch (code) {
            case STATUS_CONNECTING:
                this.status = "Connecting...";
                break;
            case STATUS_SOCKETCLOSED:
                this.connecting = false;
                this.status = "Could not connect: Socket closed.";
                break;
            case STATUS_FAILEDCONNECT:
                this.connecting = false;
                this.status = "Could not connect: Cannot reach server.";
                break;
            case STATUS_UNKNOWNHOST:
                this.connecting = false;
                this.status = "Could not connect: Cannot resolve host.";
                break;
            case STATUS_WRONGVERSION:
                this.connecting = false;
                this.status = "Could not connect: Server is a different version.";
                break;
            default:
                this.connecting = false;
                this.status = "Unkown Status";
        }
    }
}
