package ui;

import model.GameState;
import model.Item;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;


public class TablePanel extends JPanel {

    private static final int PORT_W = 120;
    private static final int PORT_H = 160;
    private static final int ITEM_SZ = 48;

    // ── Opponent row ──
    private final JPanel oppItemsBox;
    private final JLabel oppPortraitLabel;  // dealer portrait image
    private final JLabel oppNameLabel;
    private final JLabel oppChargesLabel;
    private final JLabel oppWinsLabel;

    // ── Player row ──
    private final JLabel plrChargesLabel;
    private final JLabel plrWinsLabel;
    private final JLabel plrPortraitLabel;  // player portrait image
    private final JLabel plrNameLabel;
    private final JPanel plrItemsBox;

    // ── Centre table ───
    private final JButton shootOpponentBtn;
    private final JButton shootSelfBtn;
    private final JLabel  actionResultLabel;
    private final JLabel  turnBanner;

    private Consumer<Item> onItemUsed;

    public TablePanel() {
        setBackground(Theme.BG_PANEL);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_SUBTLE, 1),
                new EmptyBorder(Theme.PADDING_LG, Theme.PADDING_LG,
                                Theme.PADDING_LG, Theme.PADDING_LG)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.fill    = GridBagConstraints.BOTH;
        c.insets  = new Insets(6, 8, 6, 8);
        c.weighty = 1.0;

        // ── ROW 0: Opponent ──

        // Col 0 — opponent items (with images)
        oppItemsBox = makeScrollableItemBox();
        c.gridx = 0; c.gridy = 0; c.weightx = 0.26;
        add(oppItemsBox, c);

        // Col 1 — dealer portrait + name
        oppPortraitLabel = new JLabel();
        oppPortraitLabel.setHorizontalAlignment(SwingConstants.CENTER);
        oppPortraitLabel.setIcon(ImageLoader.getDealerImage(PORT_W, PORT_H));

        oppNameLabel = new JLabel("DEALER", SwingConstants.CENTER);
        oppNameLabel.setFont(Theme.FONT_MONO_MEDIUM);
        oppNameLabel.setForeground(Theme.RED_HOT);

        JPanel oppPortraitBox = makePortraitBox(oppPortraitLabel, oppNameLabel);
        c.gridx = 1; c.weightx = 0.48;
        add(oppPortraitBox, c);

        // Col 2 — opponent stats
        oppChargesLabel = makeValueLabel("");
        oppWinsLabel    = makeSmallLabel("");
        JPanel oppStatsBox = makeStatsBox(oppChargesLabel, oppWinsLabel, "OPP  STATS");
        c.gridx = 2; c.weightx = 0.26;
        add(oppStatsBox, c);

        // ── ROW 1: Centre felt table ────
        turnBanner        = new JLabel("", SwingConstants.CENTER);
        turnBanner.setFont(Theme.FONT_MONO_SMALL);
        turnBanner.setForeground(Theme.TEXT_SECONDARY);

        shootOpponentBtn  = makeFireButton("▲  SHOOT OPPONENT", Theme.RED_HOT);
        shootSelfBtn      = makeFireButton("▼  SHOOT SELF",     Theme.AMBER);

        actionResultLabel = new JLabel(" ", SwingConstants.CENTER);
        actionResultLabel.setFont(Theme.FONT_MONO_MEDIUM);
        actionResultLabel.setForeground(Theme.AMBER);

        JPanel tableCenter = buildCenterTable();
        c.gridx = 0; c.gridy = 1;
        c.gridwidth = 3;
        c.weightx = 1.0; c.weighty = 2.5;
        add(tableCenter, c);

        // ── ROW 2: Player ───
        c.gridwidth = 1;
        c.weighty   = 1.0;

        // Col 0 — player stats
        plrChargesLabel = makeValueLabel("");
        plrWinsLabel    = makeSmallLabel("");
        JPanel plrStatsBox = makeStatsBox(plrChargesLabel, plrWinsLabel, "YOUR STATS");
        c.gridx = 0; c.gridy = 2; c.weightx = 0.26;
        add(plrStatsBox, c);

        // Col 1 — player portrait + name
        plrPortraitLabel = new JLabel();
        plrPortraitLabel.setHorizontalAlignment(SwingConstants.CENTER);
        plrPortraitLabel.setIcon(ImageLoader.getPlayerImage(PORT_W, PORT_H));

        plrNameLabel = new JLabel("PLAYER", SwingConstants.CENTER);
        plrNameLabel.setFont(Theme.FONT_MONO_MEDIUM);
        plrNameLabel.setForeground(Theme.AMBER);

        JPanel plrPortraitBox = makePortraitBox(plrPortraitLabel, plrNameLabel);
        c.gridx = 1; c.weightx = 0.48;
        add(plrPortraitBox, c);

        // Col 2 — player items (with images)
        plrItemsBox = makeScrollableItemBox();
        c.gridx = 2; c.weightx = 0.26;
        add(plrItemsBox, c);
    }

    // ── Public API ──

    public void update(GameState state) {
        // Names
        oppNameLabel.setText(state.getAI().getName().toUpperCase());
        plrNameLabel.setText(state.getHuman().getName().toUpperCase());

        // Charges
        oppChargesLabel.setText(chargeBar(state.getAI().getCharges()));
        plrChargesLabel.setText(chargeBar(state.getHuman().getCharges()));

        // Wins
        int rMax = state.getMode().getRoundsToWin();
        oppWinsLabel.setText("WINS  " + state.getAI().getRoundsWon()    + " / " + rMax);
        plrWinsLabel.setText("WINS  " + state.getHuman().getRoundsWon() + " / " + rMax);

        // Active player highlighting
        boolean humanTurn = state.isHumanTurn();
        plrNameLabel.setForeground(humanTurn  ? Theme.AMBER    : Theme.TEXT_SECONDARY);
        oppNameLabel.setForeground(!humanTurn ? Theme.RED_HOT  : Theme.TEXT_SECONDARY);
        plrChargesLabel.setForeground(humanTurn  ? Theme.TEXT_PRIMARY : Theme.TEXT_SECONDARY);
        oppChargesLabel.setForeground(!humanTurn ? Theme.TEXT_PRIMARY : Theme.TEXT_SECONDARY);

        // Turn banner
        String who = humanTurn ? state.getHuman().getName() : state.getAI().getName();
        turnBanner.setText("[ " + who.toUpperCase() + "'S TURN ]");
        turnBanner.setForeground(humanTurn ? Theme.AMBER : Theme.RED_HOT);

        // Fire button state
        boolean canFire = humanTurn && !state.getGun().isEmpty();
        shootOpponentBtn.setEnabled(canFire);
        shootSelfBtn.setEnabled(canFire);

        // Rebuild item boxes with images
        rebuildItemBox(plrItemsBox, state, true,  humanTurn);
        rebuildItemBox(oppItemsBox, state, false, false);
    }

    public void showActionResult(String msg, boolean isLive) {
        actionResultLabel.setText(msg);
        actionResultLabel.setForeground(isLive ? Theme.RED_HOT : Theme.GREEN_BRIGHT);
    }

    public void clearActionResult() {
        actionResultLabel.setText(" ");
        actionResultLabel.setForeground(Theme.AMBER);
    }

    public void setOnItemUsed(Consumer<Item> cb) { this.onItemUsed = cb; }

    public void addShootOpponentListener(java.awt.event.ActionListener l) {
        shootOpponentBtn.addActionListener(l);
    }
    public void addShootSelfListener(java.awt.event.ActionListener l) {
        shootSelfBtn.addActionListener(l);
    }

    // ── Private builders ──

    private JPanel buildCenterTable() {
        JPanel felt = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Theme.GREEN_FELT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(Theme.TABLE_BORDER);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        felt.setOpaque(false);
        felt.setLayout(new GridBagLayout());
        felt.setBorder(new EmptyBorder(14, 24, 14, 24));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 0, 4, 0);

        c.gridy = 0; felt.add(turnBanner, c);

        c.gridy = 1;
        JSeparator s1 = new JSeparator(); s1.setForeground(Theme.GREEN_DIM);
        felt.add(s1, c);

        c.gridy = 2; felt.add(shootOpponentBtn, c);
        c.gridy = 3; felt.add(actionResultLabel, c);
        c.gridy = 4; felt.add(shootSelfBtn, c);

        c.gridy = 5;
        JSeparator s2 = new JSeparator(); s2.setForeground(Theme.GREEN_DIM);
        felt.add(s2, c);

        return felt;
    }


    private JPanel makePortraitBox(JLabel imgLabel, JLabel nameLabel) {
        JPanel p = new JPanel();
        p.setBackground(Theme.BG_CARD);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_SUBTLE, 1),
                new EmptyBorder(10, 10, 8, 10)
        ));
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(Box.createVerticalGlue());
        p.add(imgLabel);
        p.add(Box.createVerticalStrut(8));
        p.add(nameLabel);
        p.add(Box.createVerticalGlue());
        return p;
    }

    /** Stats box: header label, charge bar, wins. */
    private JPanel makeStatsBox(JLabel chargesLbl, JLabel winsLbl, String header) {
        JPanel p = new JPanel();
        p.setBackground(Theme.BG_CARD);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_SUBTLE, 1),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel hdr = new JLabel(header, SwingConstants.CENTER);
        hdr.setFont(Theme.FONT_LABEL);
        hdr.setForeground(Theme.TEXT_DISABLED);
        hdr.setAlignmentX(Component.CENTER_ALIGNMENT);

        chargesLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        winsLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(Box.createVerticalGlue());
        p.add(hdr);
        p.add(Box.createVerticalStrut(6));
        p.add(chargesLbl);
        p.add(Box.createVerticalStrut(4));
        p.add(winsLbl);
        p.add(Box.createVerticalGlue());
        return p;
    }


    private JPanel makeScrollableItemBox() {
        JPanel p = new JPanel();
        p.setBackground(Theme.BG_CARD);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_SUBTLE, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));
        return p;
    }

    private void rebuildItemBox(JPanel box, GameState state, boolean isPlayer, boolean active) {
        box.removeAll();

        var items = isPlayer ? state.getHuman().getItems() : state.getAI().getItems();
        String header = isPlayer ? "YOUR ITEMS" : "OPP ITEMS";

        JLabel hdr = new JLabel(header, SwingConstants.CENTER);
        hdr.setFont(Theme.FONT_LABEL);
        hdr.setForeground(Theme.TEXT_DISABLED);
        hdr.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(hdr);
        box.add(Box.createVerticalStrut(6));

        if (items.isEmpty()) {
            JLabel empty = new JLabel("—", SwingConstants.CENTER);
            empty.setFont(Theme.FONT_MONO_SMALL);
            empty.setForeground(Theme.TEXT_DISABLED);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            box.add(Box.createVerticalGlue());
            box.add(empty);
            box.add(Box.createVerticalGlue());
        } else {
            box.add(Box.createVerticalGlue());
            for (Item item : items) {
                if (isPlayer && active) {
                    box.add(makeItemButton(item));
                } else {
                    box.add(makeItemDisplay(item));
                }
                box.add(Box.createVerticalStrut(4));
            }
            box.add(Box.createVerticalGlue());
        }

        box.revalidate();
        box.repaint();
    }


    private JPanel makeItemButton(Item item) {
        ImageIcon icon = ImageLoader.getItemImage(item, ITEM_SZ);

        JButton imgBtn = new JButton(icon) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed()  ? Theme.AMBER_DIM :
                           getModel().isRollover() ? Theme.BG_HOVER   : Theme.BG_PANEL;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                if (getModel().isRollover()) {
                    g2.setColor(Theme.AMBER_DIM);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 6, 6);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        imgBtn.setContentAreaFilled(false);
        imgBtn.setBorderPainted(false);
        imgBtn.setFocusPainted(false);
        imgBtn.setPreferredSize(new Dimension(ITEM_SZ + 4, ITEM_SZ + 4));
        imgBtn.setMaximumSize(new Dimension(ITEM_SZ + 4, ITEM_SZ + 4));
        imgBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imgBtn.setToolTipText("<html><b>" + item.getDisplayName()
                              + "</b><br>" + item.getDescription() + "</html>");
        imgBtn.addActionListener(e -> { if (onItemUsed != null) onItemUsed.accept(item); });

        JLabel nameLbl = new JLabel(item.getDisplayName(), SwingConstants.CENTER);
        nameLbl.setFont(new Font("Courier New", Font.PLAIN, 9));
        nameLbl.setForeground(Theme.AMBER);
        nameLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));

        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        imgBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        cell.add(imgBtn);
        cell.add(Box.createVerticalStrut(2));
        cell.add(nameLbl);
        cell.setMaximumSize(new Dimension(Integer.MAX_VALUE, ITEM_SZ + 22));

        return cell;
    }


    private JPanel makeItemDisplay(Item item) {
        ImageIcon icon = ImageLoader.getItemImage(item, ITEM_SZ);

        // Desaturate / dim the icon
        ImageIcon dimIcon = dimIcon(icon, ITEM_SZ);

        JLabel imgLbl = new JLabel(dimIcon);
        imgLbl.setToolTipText(item.getDisplayName());

        JLabel nameLbl = new JLabel(item.getDisplayName(), SwingConstants.CENTER);
        nameLbl.setFont(new Font("Courier New", Font.PLAIN, 9));
        nameLbl.setForeground(Theme.TEXT_DISABLED);
        nameLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));

        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        imgLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        cell.add(imgLbl);
        cell.add(Box.createVerticalStrut(2));
        cell.add(nameLbl);
        cell.setMaximumSize(new Dimension(Integer.MAX_VALUE, ITEM_SZ + 22));

        return cell;
    }


    private ImageIcon dimIcon(ImageIcon src, int size) {
        java.awt.image.BufferedImage out =
                new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src.getImage(), 0, 0, size, size, null);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.55f));
        g.setColor(Theme.BG_PANEL);
        g.fillRect(0, 0, size, size);
        g.dispose();
        return new ImageIcon(out);
    }

    // ── Fire button ─

    private JButton makeFireButton(String text, Color accent) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = !isEnabled()            ? new Color(0x111811) :
                           getModel().isPressed()  ? accent.darker()     :
                           getModel().isRollover() ? new Color(0x1E2E1E) : new Color(0x162016);
                g2.setColor(bg);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),6,6);
                g2.setColor(isEnabled() ? accent : Theme.TEXT_DISABLED);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,6,6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(Theme.FONT_MONO_MEDIUM);
        btn.setForeground(accent);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setPreferredSize(new Dimension(340, 44));
        return btn;
    }

    // ── Label helpers ──

    private JLabel makeValueLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(Theme.FONT_MONO_LARGE);
        l.setForeground(Theme.TEXT_PRIMARY);
        return l;
    }

    private JLabel makeSmallLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(Theme.FONT_LABEL);
        l.setForeground(Theme.TEXT_SECONDARY);
        return l;
    }

    private String chargeBar(int n) {
        if (n <= 0) return "DEAD";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append("▮");
        return sb.toString();
    }
}
