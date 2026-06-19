package ui;

import model.GameMode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModeSelectDialog extends JDialog {

    private GameMode selectedMode = null;

    public ModeSelectDialog(Frame parent) {
        super(parent, "Russian Roulette", true); 
        setUndecorated(false);
        getContentPane().setBackground(Theme.BG_DEEP);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel root = new JPanel();
        root.setBackground(Theme.BG_DEEP);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(40, 60, 40, 60));

        JLabel title = new JLabel("RUSSIAN  ROULETTE", SwingConstants.CENTER);
        title.setFont(new Font("Courier New", Font.BOLD, 28));
        title.setForeground(Theme.AMBER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(title);

        root.add(Box.createVerticalStrut(6));

        JLabel subtitle = new JLabel("SELECT  DIFFICULTY", SwingConstants.CENTER);
        subtitle.setFont(Theme.FONT_MONO_SMALL);
        subtitle.setForeground(Theme.TEXT_DISABLED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(subtitle);

        root.add(Box.createVerticalStrut(36));

        for (GameMode mode : GameMode.values()) {
            JPanel card = buildModeCard(mode);
            card.setAlignmentX(Component.CENTER_ALIGNMENT);
            root.add(card);
            root.add(Box.createVerticalStrut(12));
        }

        add(root, BorderLayout.CENTER);

        pack();
        setMinimumSize(new Dimension(420, 0));
        setLocationRelativeTo(parent);
    }

    private JPanel buildModeCard(GameMode mode) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Theme.BORDER_SUBTLE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(16, 0));
        card.setBorder(new EmptyBorder(14, 18, 14, 18));
        card.setPreferredSize(new Dimension(360, 80));
        card.setMaximumSize(new Dimension(360, 80));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel leftBlock = new JPanel();
        leftBlock.setOpaque(false);
        leftBlock.setLayout(new BoxLayout(leftBlock, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(mode.getDisplayName().toUpperCase());
        nameLabel.setFont(Theme.FONT_MONO_MEDIUM);
        nameLabel.setForeground(modeColor(mode));
        leftBlock.add(nameLabel);

        JLabel gunLabel = new JLabel(mode.getGunName());
        gunLabel.setFont(Theme.FONT_MONO_SMALL);
        gunLabel.setForeground(Theme.TEXT_SECONDARY);
        leftBlock.add(gunLabel);

        card.add(leftBlock, BorderLayout.WEST);

        JPanel rightBlock = new JPanel();
        rightBlock.setOpaque(false);
        rightBlock.setLayout(new BoxLayout(rightBlock, BoxLayout.Y_AXIS));
        rightBlock.setAlignmentX(Component.RIGHT_ALIGNMENT);

        String statsLine1 = mode.getTotalBullets() + " bullets  ·  " + mode.getStartingCharges() + " HP";
        String statsLine2 = mode.getItemsPerDeal() + " items  ·  " + mode.getRoundsToWin() + " rounds";

        JLabel s1 = new JLabel(statsLine1, SwingConstants.RIGHT);
        s1.setFont(Theme.FONT_LABEL);
        s1.setForeground(Theme.TEXT_SECONDARY);

        JLabel s2 = new JLabel(statsLine2, SwingConstants.RIGHT);
        s2.setFont(Theme.FONT_LABEL);
        s2.setForeground(Theme.TEXT_DISABLED);

        rightBlock.add(s1);
        rightBlock.add(Box.createVerticalStrut(3));
        rightBlock.add(s2);
        card.add(rightBlock, BorderLayout.EAST);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(modeColor(mode), 1),
                        new EmptyBorder(13, 17, 13, 17)
                ));
                nameLabel.setForeground(Theme.TEXT_PRIMARY);
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(new EmptyBorder(14, 18, 14, 18));
                nameLabel.setForeground(modeColor(mode));
                card.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                selectedMode = mode;
                dispose();
            }
        });

        return card;
    }

    private Color modeColor(GameMode mode) {
        return switch (mode) {
            case EASY   -> Theme.GREEN_BRIGHT;
            case NORMAL -> Theme.AMBER;
            case HARD   -> Theme.RED_HOT;
        };
    }

    public static GameMode show(Frame parent) {
        ModeSelectDialog dialog = new ModeSelectDialog(parent);
        dialog.setVisible(true);
        return dialog.selectedMode;
    }
}
