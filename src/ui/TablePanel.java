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

    private final JPanel oppItemsBox;
    private final JLabel oppPortraitLabel;
    private final JLabel oppNameLabel;
    private final JLabel oppChargesLabel;
    private final JLabel oppWinsLabel;

    private final JLabel plrChargesLabel;
    private final JLabel plrWinsLabel;
    private final JLabel plrPortraitLabel;
    private final JLabel plrNameLabel;
    private final JPanel plrItemsBox;

    private final JButton shootOpponentBtn;
    private final JButton shootSelfBtn;
    private final JLabel actionResultLabel;
    private final JLabel turnBanner;

    private Consumer<Item> onItemUsed;

    public TablePanel() {
        setBackground(Theme.BG_PANEL);
        setLayout(new BorderLayout(0, 6));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_SUBTLE, 1),
                new EmptyBorder(Theme.PADDING_LG, Theme.PADDING_LG,
                        Theme.PADDING_LG, Theme.PADDING_LG)
        ));

        oppItemsBox = makeItemBox();
        plrItemsBox = makeItemBox();

        oppPortraitLabel = new JLabel();
        oppPortraitLabel.setHorizontalAlignment(SwingConstants.CENTER);
        oppPortraitLabel.setIcon(ImageLoader.getDealerImage(PORT_W, PORT_H));

        oppNameLabel = new JLabel("DEALER", SwingConstants.CENTER);
        oppNameLabel.setFont(Theme.FONT_MONO_MEDIUM);
        oppNameLabel.setForeground(Theme.RED_HOT);

        oppChargesLabel = makeValueLabel("");
        oppWinsLabel = makeSmallLabel("");

        plrChargesLabel = makeValueLabel("");
        plrWinsLabel = makeSmallLabel("");

        plrPortraitLabel = new JLabel();
        plrPortraitLabel.setHorizontalAlignment(SwingConstants.CENTER);
        plrPortraitLabel.setIcon(ImageLoader.getPlayerImage(PORT_W, PORT_H));

        plrNameLabel = new JLabel("PLAYER", SwingConstants.CENTER);
        plrNameLabel.setFont(Theme.FONT_MONO_MEDIUM);
        plrNameLabel.setForeground(Theme.AMBER);

        turnBanner = new JLabel("", SwingConstants.CENTER);
        turnBanner.setFont(Theme.FONT_MONO_SMALL);
        turnBanner.setForeground(Theme.TEXT_SECONDARY);

        shootOpponentBtn = makeFireButton("▲ SHOOT OPPONENT", Theme.RED_HOT);
        shootSelfBtn = makeFireButton("▼ SHOOT SELF", Theme.AMBER);

        actionResultLabel = new JLabel(" ", SwingConstants.CENTER);
        actionResultLabel.setFont(Theme.FONT_MONO_MEDIUM);
        actionResultLabel.setForeground(Theme.AMBER);

        JPanel oppRow = buildTopRow();
        JPanel plrRow = buildBottomRow();
        JPanel center = buildCenterTable();

        add(oppRow, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(plrRow, BorderLayout.SOUTH);
    }

    private JPanel buildTopRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 8, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, PORT_H + 40));

        row.add(oppItemsBox);
        row.add(makePortraitBox(oppPortraitLabel, oppNameLabel));
        row.add(makeStatsBox(oppChargesLabel, oppWinsLabel, "OPP STATS"));

        return row;
    }


    private JPanel buildBottomRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 8, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, PORT_H + 40));

        row.add(makeStatsBox(plrChargesLabel, plrWinsLabel, "YOUR STATS"));
        row.add(makePortraitBox(plrPortraitLabel, plrNameLabel));
        row.add(plrItemsBox);

        return row;
    }

    private JPanel buildCenterTable() {
        JPanel center = new JPanel(new BorderLayout(15, 0));
        center.setOpaque(false);
        JPanel felt = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Theme.GREEN_FELT);
                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        14,
                        14
                );
                g2.setColor(Theme.TABLE_BORDER);
                g2.setStroke(new BasicStroke(1.5f));

                g2.drawRoundRect(
                        0,
                        0,
                        getWidth() - 1,
                        getHeight() - 1,
                        14,
                        14
                );

                g2.dispose();
            }
        };
        felt.setOpaque(false);
        felt.setLayout(new BoxLayout(felt, BoxLayout.Y_AXIS));
        felt.setBorder(new EmptyBorder(20, 20, 20, 20));
        turnBanner.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionResultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        felt.add(Box.createVerticalGlue());
        felt.add(turnBanner);
        felt.add(Box.createVerticalStrut(12));
        felt.add(actionResultLabel);
        felt.add(Box.createVerticalGlue());
        JPanel left = new JPanel(new GridBagLayout());
        left.setOpaque(false);
        left.add(shootOpponentBtn);
        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        right.add(shootSelfBtn);
        center.add(left, BorderLayout.WEST);
        center.add(felt, BorderLayout.CENTER);
        center.add(right, BorderLayout.EAST);
        return center;
    }

    public void update(GameState state) {

        oppNameLabel.setText(state.getAI().getName().toUpperCase());
        plrNameLabel.setText(state.getHuman().getName().toUpperCase());

        boolean humanTurn = state.isHumanTurn();

        String who = humanTurn ? state.getHuman().getName() : state.getAI().getName();
        turnBanner.setText("[ " + who.toUpperCase() + " TURN ]");

        boolean canFire = humanTurn && !state.getGun().isEmpty();
        shootOpponentBtn.setEnabled(canFire);
        shootSelfBtn.setEnabled(canFire);

        rebuildItemBox(plrItemsBox, state, true, humanTurn);
        rebuildItemBox(oppItemsBox, state, false, false);
        oppChargesLabel.setText(chargeBar(state.getAI().getCharges()));
        plrChargesLabel.setText(chargeBar(state.getHuman().getCharges()));

        int rMax = state.getMode().getRoundsToWin();

        oppWinsLabel.setText(
                "WINS " + state.getAI().getRoundsWon() + " / " + rMax
        );

        plrWinsLabel.setText(
                "WINS " + state.getHuman().getRoundsWon() + " / " + rMax
        );
    }

    private JPanel makeItemBox() {
        JPanel p = new JPanel(new GridLayout(0, 2, 6, 6));
        p.setBackground(Theme.BG_CARD);
        p.setBorder(new EmptyBorder(8, 8, 8, 8));
        return p;
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

    public void showActionResult(String msg, boolean isLive) {
        actionResultLabel.setText(msg);
        actionResultLabel.setForeground(
                isLive ? Theme.RED_HOT : Theme.GREEN_BRIGHT
        );
    }

    public void clearActionResult() {
        actionResultLabel.setText(" ");
        actionResultLabel.setForeground(Theme.AMBER);
    }

    public void setOnItemUsed(Consumer<Item> cb) {
        this.onItemUsed = cb;
    }

    public void addShootOpponentListener(java.awt.event.ActionListener l) {
        shootOpponentBtn.addActionListener(l);
    }

    public void addShootSelfListener(java.awt.event.ActionListener l) {
        shootSelfBtn.addActionListener(l);
    }

    private String chargeBar(int n) {
        if (n <= 0) {
            return "DEAD";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < n; i++) {
            sb.append("▮");
        }

        return sb.toString();
    }

    private void rebuildItemBox(JPanel box, GameState state, boolean isPlayer, boolean active) {
        box.removeAll();

        var items = isPlayer ? state.getHuman().getItems() : state.getAI().getItems();

        JLabel hdr = new JLabel(isPlayer ? "YOUR ITEMS" : "OPP ITEMS", SwingConstants.CENTER);
        hdr.setForeground(Theme.TEXT_DISABLED);
        box.add(hdr);

        if (items.isEmpty()) {
            box.add(new JLabel("—", SwingConstants.CENTER));
        } else {
            for (Item item : items) {
                if (isPlayer && active) box.add(makeItemButton(item));
                else box.add(makeItemDisplay(item));
            }
        }

        box.revalidate();
        box.repaint();
    }
    private JPanel makeItemButton(Item item) {
        ImageIcon icon = ImageLoader.getItemImage(item, ITEM_SZ);

        JButton imgBtn = new JButton(icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                Color bg =
                        getModel().isPressed() ? Theme.AMBER_DIM :
                                getModel().isRollover() ? Theme.BG_HOVER :
                                        Theme.BG_PANEL;

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

                if (getModel().isRollover()) {
                    g2.setColor(Theme.AMBER_DIM);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(
                            0,
                            0,
                            getWidth() - 1,
                            getHeight() - 1,
                            6,
                            6
                    );
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };

        imgBtn.setContentAreaFilled(false);
        imgBtn.setBorderPainted(false);
        imgBtn.setFocusPainted(false);

        imgBtn.setPreferredSize(
                new Dimension(ITEM_SZ + 4, ITEM_SZ + 4)
        );

        imgBtn.setMaximumSize(
                new Dimension(ITEM_SZ + 4, ITEM_SZ + 4)
        );

        imgBtn.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        imgBtn.setToolTipText(
                "<html><b>" +
                        item.getDisplayName() +
                        "</b><br>" +
                        item.getDescription() +
                        "</html>"
        );

        imgBtn.addActionListener(e -> {
            if (onItemUsed != null) {
                onItemUsed.accept(item);
            }
        });

        JLabel nameLbl = new JLabel(
                item.getDisplayName(),
                SwingConstants.CENTER
        );

        nameLbl.setFont(
                new Font("Courier New", Font.PLAIN, 9)
        );

        nameLbl.setForeground(Theme.AMBER);

        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));

        imgBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        cell.add(imgBtn);
        cell.add(Box.createVerticalStrut(2));
        cell.add(nameLbl);

        return cell;
    }
    private ImageIcon dimIcon(ImageIcon src, int size) {
        java.awt.image.BufferedImage out =
                new java.awt.image.BufferedImage(
                        size,
                        size,
                        java.awt.image.BufferedImage.TYPE_INT_ARGB
                );
        Graphics2D g = out.createGraphics();
        g.drawImage(
                src.getImage(),
                0,
                0,
                size,
                size,
                null
        );
        g.setComposite(
                AlphaComposite.getInstance(
                        AlphaComposite.SRC_ATOP,
                        0.55f
                )
        );
        g.setColor(Theme.BG_PANEL);
        g.fillRect(0, 0, size, size);
        g.dispose();
        return new ImageIcon(out);
    }

    private JPanel makeItemDisplay(Item item) {
        ImageIcon icon = ImageLoader.getItemImage(item, ITEM_SZ);

        JLabel imgLbl = new JLabel(dimIcon(icon, ITEM_SZ));

        JLabel nameLbl = new JLabel(
                item.getDisplayName(),
                SwingConstants.CENTER
        );

        nameLbl.setFont(
                new Font("Courier New", Font.PLAIN, 9)
        );
        nameLbl.setForeground(Theme.TEXT_DISABLED);
        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        imgLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        cell.add(imgLbl);
        cell.add(Box.createVerticalStrut(2));
        cell.add(nameLbl);

        return cell;
    }

    private JPanel wrap(JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(c);
        return p;
    }

    private JButton makeFireButton(String text, Color accent) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                Color bg =
                        !isEnabled()
                                ? new Color(0x111811)
                                : getModel().isPressed()
                                ? accent.darker()
                                : getModel().isRollover()
                                ? new Color(0x1E2E1E)
                                : new Color(0x162016);
                g2.setColor(bg);

                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        6,
                        6
                );
                g2.setColor(
                        isEnabled()
                                ? accent
                                : Theme.TEXT_DISABLED
                );

                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(
                        0,
                        0,
                        getWidth() - 1,
                        getHeight() - 1,
                        6,
                        6
                );

                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(Theme.FONT_MONO_MEDIUM);
        btn.setForeground(accent);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );
        btn.setPreferredSize(
                new Dimension(180, 52)
        );
        btn.setMinimumSize(
                new Dimension(180, 52)
        );
        btn.setMaximumSize(
                new Dimension(180, 52)
        );
        return btn;
    }

    private JLabel makeValueLabel(String text) {
        return new JLabel(text, SwingConstants.CENTER);
    }

    private JLabel makeSmallLabel(String text) {
        return new JLabel(text, SwingConstants.CENTER);
    }
}