package blockfighter.client.screen;

import blockfighter.client.Globals;
import static blockfighter.client.screen.Screen.panel;
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
            STATUS_UNKNOWNHOST = 3;

    private final JTextField SERVERADDRESS_FIELD = new JTextField();
    private final JComboBox SERVER_ROOMS = new JComboBox();
    private Rectangle connect = new Rectangle(650, 230, 200, 70);
    private String status = "Waiting to connect...";
    private boolean connecting = false;

    public ScreenServerList() {
        SERVERADDRESS_FIELD.setBounds(550, 150, 400, 40);
        SERVERADDRESS_FIELD.setFont(Globals.ARIAL_24PT);
        SERVERADDRESS_FIELD.setForeground(Color.WHITE);
        SERVERADDRESS_FIELD.setBackground(Color.BLACK);
        SERVERADDRESS_FIELD.setCaretColor(Color.WHITE);
        SERVERADDRESS_FIELD.setOpaque(true);
        SERVERADDRESS_FIELD.setText(loadServerList());

        try {
            SERVER_ROOMS.addItem("PVP");
            for (int i = 0; i < 100; i++) {
                SERVER_ROOMS.addItem("Level " + (i + 1));
            }
        } catch (Exception e) {

        }
        SERVER_ROOMS.setFont(Globals.ARIAL_24PT);
        SERVER_ROOMS.setForeground(Color.WHITE);
        SERVER_ROOMS.setBackground(Color.BLACK);
        SERVER_ROOMS.setOpaque(true);
        SERVER_ROOMS.setBounds(1000, 150, 150, 40);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                panel.add(SERVERADDRESS_FIELD);
                panel.add(SERVER_ROOMS);
                panel.revalidate();
            }
        });
    }

    public static String loadServerList() {
        try {
            return FileUtils.readLines(new File("server.txt"), StandardCharsets.UTF_8).get(0);
        } catch (Exception ex) {
            return "";
        }
    }

    public static void saveServerList(String address) {
        try {
            FileUtils.writeStringToFile(new File("server.txt"), address, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            System.err.println("saveServerList: Failed to save server name.");
        }
    }

    @Override
    public void draw(Graphics2D g) {
        BufferedImage bg = Globals.MENU_BG[4];
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

        g.drawRect(connect.x, connect.y, connect.width, connect.height);
        g.setColor(new Color(30, 30, 30, 255));
        g.fillRect(connect.x, connect.y, connect.width, connect.height);
        g.setColor(Color.BLACK);
        g.drawRect(connect.x, connect.y, connect.width, connect.height);
        g.drawRect(connect.x + 1, connect.y + 1, connect.width - 2, connect.height - 2);

        g.setFont(Globals.ARIAL_24PT);
        g.setColor(Color.WHITE);
        g.drawString("Connect", connect.x + 55, connect.y + 40);

        g.setFont(Globals.ARIAL_24PT);
        int strWidth = g.getFontMetrics().stringWidth(status);
        drawStringOutline(g, status, 750 - strWidth / 2, 350, 2);
        g.setColor(Color.WHITE);
        g.drawString(status, 750 - strWidth / 2, 350);

        drawMenuButton(g);
        super.draw(g);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (connecting) {
            return;
        }
        super.mouseReleased(e);
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (connect.contains(e.getPoint())) {
                //Connect
                if (SERVERADDRESS_FIELD.getText().trim().length() > 0) {
                    connecting = true;
                    saveServerList(SERVERADDRESS_FIELD.getText().trim());
                    logic.sendLogin(SERVERADDRESS_FIELD.getText().trim(), (byte) SERVER_ROOMS.getSelectedIndex());
                }
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void unload() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                panel.remove(SERVERADDRESS_FIELD);
                panel.remove(SERVER_ROOMS);
                panel.revalidate();
            }
        });
        saveServerList(SERVERADDRESS_FIELD.getText().trim());
    }

    public void setStatus(byte code) {
        switch (code) {
            case STATUS_CONNECTING:
                status = "Connecting...";
                break;
            case STATUS_SOCKETCLOSED:
                connecting = false;
                status = "Could not connect: Socket closed.";
                break;
            case STATUS_FAILEDCONNECT:
                connecting = false;
                status = "Could not connect: Cannot reach server.";
                break;
            case STATUS_UNKNOWNHOST:
                connecting = false;
                status = "Could not connect: Cannot resolve host.";
                break;
            default:
                connecting = false;
                status = "Unkown Status";
        }
    }
}
