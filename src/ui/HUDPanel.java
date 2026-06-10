package ui;

import model.GameMode;
import model.GameState;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class HUDPanel extends JPanel {

    private static final int GUN_IMG_W = 220;
    private static final int GUN_IMG_H = 80;

    private final JLabel roundLabel;
    private final JLabel gunImageLabel;   // shows gun JPG
    private final JLabel gunNameLabel;
    private final JLabel chamberLabel;
    private final JLabel probLabel;

    private GameMode lastMode = null;

    public HUDPanel(GameState state) {
        setBackground(Theme.BG_PANEL);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_SUBTLE),
                new EmptyBorder(Theme.PADDING_MD, Theme.PADDING_LG,
                                Theme.PADDING_MD, Theme.PADDING_LG)
        ));
        setLayout(new BorderLayout(0, 0));


        roundLabel = new JLabel("ROUND  1", SwingConstants.CENTER);
        roundLabel.setFont(Theme.FONT_MONO_MEDIUM);
        roundLabel.setForeground(Theme.AMBER);
        roundLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_ACCENT, 1),
                new EmptyBorder(6, 18, 6, 18)
        ));
        roundLabel.setOpaque(true);
        roundLabel.setBackground(Theme.BG_CARD);

        JPanel leftWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftWrap.setOpaque(false);
        leftWrap.add(roundLabel);
        add(leftWrap, BorderLayout.WEST);


        gunImageLabel = new JLabel();
        gunImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gunImageLabel.setVerticalAlignment(SwingConstants.CENTER);

        gunNameLabel = new JLabel("", SwingConstants.CENTER);
        gunNameLabel.setFont(Theme.FONT_MONO_MEDIUM);
        gunNameLabel.setForeground(Theme.TEXT_PRIMARY);

        chamberLabel = new JLabel("", SwingConstants.CENTER);
        chamberLabel.setFont(new Font("Courier New", Font.BOLD, 13));
        chamberLabel.setForeground(Theme.TEXT_SECONDARY);

        probLabel = new JLabel("", SwingConstants.CENTER);
        probLabel.setFont(Theme.FONT_MONO_SMALL);
        probLabel.setForeground(Theme.TEXT_DISABLED);


        JPanel gunTextCol = new JPanel();
        gunTextCol.setOpaque(false);
        gunTextCol.setLayout(new BoxLayout(gunTextCol, BoxLayout.Y_AXIS));
        gunNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        chamberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        probLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gunTextCol.add(Box.createVerticalGlue());
        gunTextCol.add(gunNameLabel);
        gunTextCol.add(Box.createVerticalStrut(3));
        gunTextCol.add(chamberLabel);
        gunTextCol.add(Box.createVerticalStrut(2));
        gunTextCol.add(probLabel);
        gunTextCol.add(Box.createVerticalGlue());

        JPanel gunBox = new JPanel(new BorderLayout(10, 0));
        gunBox.setBackground(Theme.BG_CARD);
        gunBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_SUBTLE, 1),
                new EmptyBorder(6, 12, 6, 14)
        ));
        gunBox.add(gunImageLabel, BorderLayout.WEST);
        gunBox.add(gunTextCol,    BorderLayout.CENTER);

        JPanel rightWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightWrap.setOpaque(false);
        rightWrap.add(gunBox);
        add(rightWrap, BorderLayout.EAST);

        update(state);
    }

    public void update(GameState state) {
        roundLabel.setText("ROUND  " + state.getCurrentRound()
                           + " / " + state.getMode().getRoundsToWin());


        if (state.getMode() != lastMode) {
            lastMode = state.getMode();
            ImageIcon gunIcon = ImageLoader.getGunImage(state.getMode(), GUN_IMG_W, GUN_IMG_H);
            gunImageLabel.setIcon(gunIcon);
            gunImageLabel.setPreferredSize(new Dimension(GUN_IMG_W, GUN_IMG_H));
        }

        gunNameLabel.setText(state.getMode().getGunName().toUpperCase());

        int total = state.getGun().getTotalCount();
        int spent = state.getGun().getCurrentIndex();
        StringBuilder sb = new StringBuilder("<html><center>");
        for (int i = 0; i < total; i++) {
            if (i > 0 && i % 8 == 0) sb.append("<br>");
            sb.append(i < spent ? "─" : "■");
            if (i < total - 1 && (i + 1) % 8 != 0) sb.append(" ");
        }
        sb.append("</center></html>");
        chamberLabel.setText(sb.toString());

        int lp = (int) Math.round(state.getGun().liveProbability()  * 100);
        int bp = (int) Math.round(state.getGun().blankProbability() * 100);
        probLabel.setText("LIVE " + lp + "%  ·  BLANK " + bp + "%");
    }
}
