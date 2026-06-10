package ui;

import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;


public class GameUI extends JFrame {

    private final HUDPanel   hudPanel;
    private final TablePanel tablePanel;

    public GameUI(GameMode mode) {
        super("RUSSIAN ROULETTE  ·  " + mode.getDisplayName().toUpperCase());

        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(Theme.BG_DEEP);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(Theme.BG_DEEP);
        root.setBorder(new EmptyBorder(0, 0, 0, 0));
        setContentPane(root);

        GameState dummy = buildDummyState(mode);

        hudPanel   = new HUDPanel(dummy);
        tablePanel = new TablePanel();
        tablePanel.update(dummy);

        root.add(hudPanel,   BorderLayout.NORTH);
        root.add(tablePanel, BorderLayout.CENTER);

        setMinimumSize(new Dimension(760, 560));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }


    public void refresh(GameState state) {
        SwingUtilities.invokeLater(() -> {
            hudPanel.update(state);
            tablePanel.update(state);
            repaint();
        });
    }

    public void showResult(String message, boolean isLive) {
        SwingUtilities.invokeLater(() -> tablePanel.showActionResult(message, isLive));
    }

    public void clearResult() {
        SwingUtilities.invokeLater(tablePanel::clearActionResult);
    }

    public int showEventDialog(String title, String body, String[] options) {
        JPanel panel = new JPanel();
        panel.setBackground(Theme.BG_CARD);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(Theme.FONT_MONO_LARGE);
        titleLabel.setForeground(Theme.AMBER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(12));

        for (String line : body.split("\n")) {
            JLabel l = new JLabel(line.isEmpty() ? " " : line, SwingConstants.CENTER);
            l.setFont(Theme.FONT_MONO_SMALL);
            l.setForeground(Theme.TEXT_SECONDARY);
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(l);
        }

        UIManager.put("OptionPane.background", Theme.BG_CARD);
        UIManager.put("Panel.background",      Theme.BG_CARD);

        return JOptionPane.showOptionDialog(
                this, panel, "",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]
        );
    }

    public void setOnShootOpponent(Runnable r) {
        tablePanel.addShootOpponentListener(e -> r.run());
    }

    public void setOnShootSelf(Runnable r) {
        tablePanel.addShootSelfListener(e -> r.run());
    }

    public TablePanel getTablePanel() { return tablePanel; }



    private GameState buildDummyState(GameMode mode) {
        Player human = new Player("PLAYER", false, mode.getStartingCharges());
        Player ai    = new Player("DEALER", true,  mode.getStartingCharges());

        // Give human a couple of demo items so item boxes render
        human.addItem(Item.BEER);
        human.addItem(Item.MAGNIFYING_GLASS);
        ai.addItem(Item.HANDCUFFS);

        Gun gun = new Gun(mode.getGunName());
        java.util.List<BulletType> bullets = new java.util.ArrayList<>();
        for (int i = 0; i < mode.getLiveBullets(); i++) bullets.add(BulletType.LIVE);
        while (bullets.size() < mode.getTotalBullets()) bullets.add(BulletType.BLANK);
        Collections.shuffle(bullets);
        gun.load(bullets);
        return new GameState(mode, human, ai, gun);
    }

}
