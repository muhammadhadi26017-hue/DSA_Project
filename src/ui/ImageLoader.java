package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader {

    private static final String ASSETS = "assets";

    private static final Map<String, ImageIcon> CACHE = new HashMap<>();

    private ImageLoader() {}

    public static ImageIcon getGunImage(model.GameMode mode, int width, int height) {
        String file = switch (mode) {
            case EASY   -> "glock.jpg";
            case NORMAL -> "swiss_revolver.jpg";
            case HARD   -> "mossberg.jpg";
        };
        return load(ASSETS + "/guns/" + file, width, height, "[ " + mode.getGunName() + " ]");
    }

    public static ImageIcon getPlayerImage(int width, int height) {
        return load(ASSETS + "/players/player.jpg", width, height, "[ PLAYER ]");
    }

    public static ImageIcon getDealerImage(int width, int height) {
        return load(ASSETS + "/players/dealer.jpg", width, height, "[ DEALER ]");
    }

    public static ImageIcon getItemImage(model.Item item, int size) {
        String file = switch (item) {
            case BEER             -> "beer.jpg";
            case CIGARETTE        -> "cigarette.jpg";
            case VODKA            -> "vodka.jpg";
            case MAGNIFYING_GLASS -> "magnifying_glass.jpg";
            case FLIP_PHONE       -> "flip_phone.jpg";
            case HANDCUFFS        -> "handcuffs.jpg";
            case PILL             -> "pill.jpg";
        };
        return load(ASSETS + "/items/" + file, size, size, item.getDisplayName());
    }

    public static ImageIcon load(String path, int w, int h, String placeholderText) {
        String key = path + "@" + w + "x" + h;
        if (CACHE.containsKey(key)) return CACHE.get(key);

        File f = new File(path);
        if (f.exists()) {
            try {
                BufferedImage raw = ImageIO.read(f);
                if (raw != null) {
                    Image scaled = raw.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                    ImageIcon icon = new ImageIcon(scaled);
                    CACHE.put(key, icon);
                    return icon;
                }
            } catch (Exception ignored) {}
        }

        ImageIcon placeholder = makePlaceholder(w, h, placeholderText);
        CACHE.put(key, placeholder);
        return placeholder;
    }

    public static void clearCache() {
        CACHE.clear();
    }

    private static ImageIcon makePlaceholder(int w, int h, String label) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Theme.BG_CARD);
        g.fillRoundRect(0, 0, w, h, 8, 8);

        g.setColor(Theme.BORDER_SUBTLE);
        float[] dash = {4f, 4f};
        g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0f));
        g.drawRoundRect(1, 1, w - 2, h - 2, 8, 8);

        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(0x2A2A2A));
        g.drawLine(w / 2, 4, w / 2, h - 4);
        g.drawLine(4, h / 2, w - 4, h / 2);

        g.setFont(new Font("Courier New", Font.PLAIN, Math.max(9, Math.min(12, w / 12))));
        g.setColor(Theme.TEXT_DISABLED);
        FontMetrics fm = g.getFontMetrics();

        String display = label;
        while (fm.stringWidth(display) > w - 8 && display.length() > 4)
            display = display.substring(0, display.length() - 1);

        int tx = (w - fm.stringWidth(display)) / 2;
        int ty = h / 2 + fm.getAscent() / 2 - 1;
        g.drawString(display, tx, ty);

        g.dispose();
        return new ImageIcon(img);
    }
}
