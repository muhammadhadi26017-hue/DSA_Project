package model;

public class GameState {
    private final GameMode mode;
    private final Player human;
    private final Player ai;
    private final Gun gun;

    private Player currentTurnPlayer;
    private int currentRound;          // which round we're in (1-based)
    private boolean doubleDamage;      // PILL effect active
    private boolean gameOver;
    private Player winner;             // null until game ends

    // Revealed bullet info (for UI message display)
    private String lastRevealMessage;
    private BulletType lastFiredBullet;

    public GameState(GameMode mode, Player human, Player ai, Gun gun) {
        this.mode = mode;
        this.human = human;
        this.ai = ai;
        this.gun = gun;
        this.currentTurnPlayer = human; // human always goes first
        this.currentRound = 1;
        this.doubleDamage = false;
        this.gameOver = false;
        this.winner = null;
        this.lastRevealMessage = null;
        this.lastFiredBullet = null;
    }

    // --- Turn management ---

    public Player getCurrentTurnPlayer() {
        return currentTurnPlayer;
    }

    public void switchTurn() {
        currentTurnPlayer = (currentTurnPlayer == human) ? ai : human;
    }

    public boolean isHumanTurn() {
        return currentTurnPlayer == human;
    }


    public Player getOpponent(Player player) {
        return (player == human) ? ai : human;
    }

    // --- Round management

    public int getCurrentRound() {
        return currentRound;
    }

    public void advanceRound() {
        currentRound++;
    }


    public boolean checkGameOver() {
        if (human.getRoundsWon() >= mode.getRoundsToWin()) {
            gameOver = true;
            winner = human;
        } else if (ai.getRoundsWon() >= mode.getRoundsToWin()) {
            gameOver = true;
            winner = ai;
        }
        return gameOver;
    }

    // --- Double damage (PILL)

    public boolean isDoubleDamage() {
        return doubleDamage;
    }

    public void setDoubleDamage(boolean doubleDamage) {
        this.doubleDamage = doubleDamage;
    }

    // --- Game over state

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getWinner() {
        return winner;
    }

    // --- Last action info (for UI feedback)

    public String getLastRevealMessage() {
        return lastRevealMessage;
    }

    public void setLastRevealMessage(String msg) {
        this.lastRevealMessage = msg;
    }

    public BulletType getLastFiredBullet() {
        return lastFiredBullet;
    }

    public void setLastFiredBullet(BulletType bullet) {
        this.lastFiredBullet = bullet;
    }

    // --- Accessors -

    public GameMode getMode()  { return mode; }
    public Player getHuman()   { return human; }
    public Player getAI()      { return ai; }
    public Gun getGun()        { return gun; }

    @Override
    public String toString() {
        return String.format(
            "=== Round %d | Mode: %s ===\n  Human: %s\n  AI:    %s\n  Gun:   %s\n  Turn:  %s%s",
            currentRound, mode.getDisplayName(),
            human, ai, gun,
            currentTurnPlayer.getName(),
            doubleDamage ? " [DOUBLE DAMAGE ACTIVE]" : ""
        );
    }
}
