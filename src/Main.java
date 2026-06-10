import logic.GameEngine;
import model.GameMode;
import ui.GameUI;
import ui.ModeSelectDialog;

import javax.swing.*;
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameMode mode = ModeSelectDialog.show(null);
            if (mode == null) { System.exit(0); return; }
            GameUI ui = new GameUI(mode);
            new GameEngine(mode, ui).startGame();
        });
    }
}
