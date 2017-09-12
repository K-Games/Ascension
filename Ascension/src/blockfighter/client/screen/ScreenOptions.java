package blockfighter.client.screen;

import blockfighter.client.Core;
import blockfighter.shared.Globals;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ScreenOptions extends ScreenMenu {

    private static final String SAVE_TEXT = "Save Settings";
    private static final String RESTART_APPLY_TEXT = "Restart required to apply new window scale.";

    private static final HashMap<Globals.ClientOptions, Object> ORIGINAL_VALUES = new HashMap<>(Globals.ClientOptions.values().length);

    private static final HashMap<Globals.ClientOptions, Rectangle2D.Double> OPTION_BOX = new HashMap<>(Globals.ClientOptions.values().length);
    private static final Rectangle2D.Double SAVE_BUTTON = new Rectangle2D.Double(280, 630, 180, 40);
    private static final Rectangle2D.Double VOL_DOWN, VOL_UP;
    private static final Rectangle2D.Double WINDOW_SCALE_DOWN, WINDOW_SCALE_UP;

    static {
        int y = 0;
        for (Globals.ClientOptions option : Globals.ClientOptions.values()) {
            ORIGINAL_VALUES.put(option, option.getValue());
            OPTION_BOX.put(option, new Rectangle2D.Double(450, 45 + (y * 60), 150, 40));
            y = (45 + (y * 50) >= 500) ? 0 : y + 1;
        }

        VOL_DOWN = new Rectangle2D.Double(OPTION_BOX.get(Globals.ClientOptions.VOLUME_LEVEL).x, OPTION_BOX.get(Globals.ClientOptions.VOLUME_LEVEL).y, 40, 40);
        VOL_UP = new Rectangle2D.Double(OPTION_BOX.get(Globals.ClientOptions.VOLUME_LEVEL).getMaxX() - 40, OPTION_BOX.get(Globals.ClientOptions.VOLUME_LEVEL).y, 40, 40);

        WINDOW_SCALE_DOWN = new Rectangle2D.Double(OPTION_BOX.get(Globals.ClientOptions.WINDOW_SCALE).x, OPTION_BOX.get(Globals.ClientOptions.WINDOW_SCALE).y, 40, 40);
        WINDOW_SCALE_UP = new Rectangle2D.Double(OPTION_BOX.get(Globals.ClientOptions.WINDOW_SCALE).getMaxX() - 40, OPTION_BOX.get(Globals.ClientOptions.WINDOW_SCALE).y, 40, 40);
    }

    private void saveOptions() {
        for (Globals.ClientOptions option : Globals.ClientOptions.values()) {
            ORIGINAL_VALUES.put(option, option.getValue());
        }
        try {
            final Properties prop = new Properties();
            for (Globals.ClientOptions option : Globals.ClientOptions.values()) {
                prop.setProperty(option.getKey(), option.getValue().toString());
            }
            prop.store(new FileOutputStream(Globals.SETTINGS_FILE), null);
        } catch (IOException ex) {
            Globals.logError(ex.toString(), ex);
        }
    }

    private void setSetting(final Globals.ClientOptions option) {
        switch (option) {
            case DAMAGE_FORMAT:
                int value = (Integer) option.getValue() + 1;
                value = (value > 2) ? 0 : value;
                option.setValue(String.valueOf(value));
                break;
            case SOUND_ENABLE:
                option.setValue(String.valueOf(!(Boolean) option.getValue()));
                Core.getSoundModule().updateVolume();
                break;
        }
    }

    private void setSetting(final Globals.ClientOptions option, double delta) {
        switch (option) {
            case WINDOW_SCALE:
                double scale = (Double) option.getValue() + delta;
                scale = (scale >= 1.5) ? 1.5 : scale;
                scale = (scale <= 0.5) ? 0.5 : scale;
                option.setValue(String.valueOf(scale));
                break;
            case VOLUME_LEVEL:
                int volume = (Integer) option.getValue() + (int) delta;
                volume = (volume >= 100) ? 100 : volume;
                volume = (volume <= 0) ? 0 : volume;
                option.setValue(String.valueOf(volume));
                Core.getSoundModule().updateVolume();
                break;
        }
    }

    private String getSettingValue(final Globals.ClientOptions option) {
        switch (option) {
            case WINDOW_SCALE:
                return Globals.NUMBER_FORMAT.format(option.getValue());
            case DAMAGE_FORMAT:
                switch ((Integer) option.getValue()) {
                    case 0:
                        return "Off";
                    case 1:
                        return "Single Numbers";
                    case 2:
                        return "Stacked Numbers";
                    default:
                        return "Off";
                }
            case SOUND_ENABLE:
                return ((Boolean) option.getValue()) ? "On" : "Off";
            case VOLUME_LEVEL:
                return ((Integer) option.getValue()).toString();
        }
        return "";
    }

    @Override
    public void draw(final Graphics2D g) {
        final BufferedImage bg = Globals.MENU_BG[1];
        g.drawImage(bg, 0, 0, null);

        for (Map.Entry<Globals.ClientOptions, Rectangle2D.Double> box : OPTION_BOX.entrySet()) {
            g.setFont(Globals.ARIAL_18PT);
            int strWidth = g.getFontMetrics().stringWidth(box.getKey().getDesc() + Globals.COLON_SPACE_TEXT);
            drawStringOutline(g, box.getKey().getDesc() + Globals.COLON_SPACE_TEXT, (int) (box.getValue().x - strWidth - 5), (int) (box.getValue().y + 25), 1);
            g.setColor(Color.WHITE);
            g.drawString(box.getKey().getDesc() + Globals.COLON_SPACE_TEXT, (int) (box.getValue().x - strWidth - 5), (int) (box.getValue().y + 25));

            g.setColor(Color.BLACK);
            g.fill(box.getValue());
            g.setColor(Color.WHITE);
            g.draw(box.getValue());

            g.draw(VOL_DOWN);
            g.draw(VOL_UP);
            g.draw(WINDOW_SCALE_DOWN);
            g.draw(WINDOW_SCALE_UP);

            String text = "-";
            strWidth = g.getFontMetrics().stringWidth(text);
            g.setColor(Color.WHITE);
            g.drawString(text, (int) (VOL_DOWN.getCenterX() - strWidth / 2), (int) (VOL_DOWN.y + 25));
            g.drawString(text, (int) (WINDOW_SCALE_DOWN.getCenterX() - strWidth / 2), (int) (WINDOW_SCALE_DOWN.y + 25));

            text = "+";
            strWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text, (int) (VOL_UP.getCenterX() - strWidth / 2), (int) (VOL_UP.y + 25));
            g.drawString(text, (int) (WINDOW_SCALE_UP.getCenterX() - strWidth / 2), (int) (WINDOW_SCALE_UP.y + 25));

            text = getSettingValue(box.getKey());
            strWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text, (int) (box.getValue().getCenterX() - strWidth / 2), (int) (box.getValue().y + 25));

        }

        final BufferedImage button = Globals.MENU_BUTTON[Globals.BUTTON_SMALLRECT];
        g.drawImage(button, (int) SAVE_BUTTON.x, (int) SAVE_BUTTON.y, null);
        final int width = g.getFontMetrics().stringWidth(SAVE_TEXT);
        drawStringOutline(g, SAVE_TEXT, (int) (SAVE_BUTTON.x + 90 - width / 2), (int) (SAVE_BUTTON.y + 25), 1);
        g.setColor(Color.WHITE);
        g.drawString(SAVE_TEXT, (int) (SAVE_BUTTON.x + 90 - width / 2), (int) (SAVE_BUTTON.y + 25));

        g.setFont(Globals.ARIAL_18PT);
        drawStringOutline(g, RESTART_APPLY_TEXT, (int) (OPTION_BOX.get(Globals.ClientOptions.WINDOW_SCALE).getMaxX() + 10), (int) (OPTION_BOX.get(Globals.ClientOptions.WINDOW_SCALE).y + 25), 1);
        g.setColor(Color.WHITE);
        g.drawString(RESTART_APPLY_TEXT, (int) (OPTION_BOX.get(Globals.ClientOptions.WINDOW_SCALE).getMaxX() + 10), (int) (OPTION_BOX.get(Globals.ClientOptions.WINDOW_SCALE).y + 25));

        drawMenuButton(g);
        super.draw(g);
    }

    @Override
    public void keyPressed(final KeyEvent e) {

    }

    @Override
    public void keyReleased(final KeyEvent e) {

    }

    @Override
    public void keyTyped(final KeyEvent e) {

    }

    @Override
    public void mouseClicked(final MouseEvent e) {

    }

    @Override
    public void mouseDragged(final MouseEvent e) {

    }

    @Override
    public void mouseEntered(final MouseEvent e) {

    }

    @Override
    public void mouseExited(final MouseEvent e) {

    }

    @Override
    public void mouseMoved(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        Point2D.Double scaled;
        if (Globals.WINDOW_SCALE_ENABLED) {
            scaled = new Point2D.Double(e.getX() / (Double) Globals.WINDOW_SCALE, e.getY() / (Double) Globals.WINDOW_SCALE);
        } else {
            scaled = new Point2D.Double(e.getX(), e.getY());
        }
        super.mouseReleased(e);

        for (Map.Entry<Globals.ClientOptions, Rectangle2D.Double> box : OPTION_BOX.entrySet()) {
            if (box.getValue().contains(scaled)) {
                setSetting(box.getKey());
            }
        }
        if (SAVE_BUTTON.contains(scaled)) {
            saveOptions();
        } else if (VOL_DOWN.contains(scaled)) {
            setSetting(Globals.ClientOptions.VOLUME_LEVEL, -1);
        } else if (VOL_UP.contains(scaled)) {
            setSetting(Globals.ClientOptions.VOLUME_LEVEL, 1);
        } else if (WINDOW_SCALE_DOWN.contains(scaled)) {
            setSetting(Globals.ClientOptions.WINDOW_SCALE, -0.1);
        } else if (WINDOW_SCALE_UP.contains(scaled)) {
            setSetting(Globals.ClientOptions.WINDOW_SCALE, 0.1);
        }
    }

    @Override
    public void unload() {
        for (Globals.ClientOptions option : Globals.ClientOptions.values()) {
            option.setValue(ORIGINAL_VALUES.get(option).toString());
        }
        Core.getSoundModule().updateVolume();
    }
}
