package ui;

import java.awt.*;

public final class Theme {
    private Theme() {}

    public static final Color BG_DEEP    = new Color(0x0D0D0D);
    public static final Color BG_PANEL   = new Color(0x141414);
    public static final Color BG_CARD    = new Color(0x1C1C1C);
    public static final Color BG_TABLE   = new Color(0x0F1F0F); // felt green
    public static final Color BG_HOVER   = new Color(0x252525);

    public static final Color AMBER       = new Color(0xE8A020);
    public static final Color AMBER_DIM   = new Color(0x7A5510);
    public static final Color RED_HOT     = new Color(0xC0392B);
    public static final Color GREEN_DIM   = new Color(0x2E6B3E);
    public static final Color GREEN_FELT  = new Color(0x1A3D1A); // table felt
    public static final Color GREEN_BRIGHT= new Color(0x4CAF73);
    public static final Color TABLE_BORDER= new Color(0x3B6B3B);

    public static final Color TEXT_PRIMARY   = new Color(0xE8DCC8);
    public static final Color TEXT_SECONDARY = new Color(0x857A65);
    public static final Color TEXT_DISABLED  = new Color(0x3D3830);

    public static final Color BORDER_SUBTLE = new Color(0x2A2A2A);
    public static final Color BORDER_ACCENT = AMBER_DIM;

    public static final Font FONT_MONO_LARGE  = new Font("Courier New", Font.BOLD, 20);
    public static final Font FONT_MONO_MEDIUM = new Font("Courier New", Font.BOLD, 13);
    public static final Font FONT_MONO_SMALL  = new Font("Courier New", Font.PLAIN, 11);
    public static final Font FONT_DISPLAY     = new Font("Courier New", Font.BOLD, 28);
    public static final Font FONT_LABEL       = new Font("Courier New", Font.PLAIN, 11);

    public static final int PADDING_LG = 18;
    public static final int PADDING_MD = 10;
    public static final int PADDING_SM = 5;
    public static final int CORNER_RADIUS = 5;
}
