package logic;

import model.BulletType;
import model.GameMode;
import model.GameState;
import model.Gun;
import model.Item;
import model.Player;
import ui.GameUI;
import ui.ModeSelectDialog;

import javax.swing.*;
import java.util.Map;

public class GameEngine {

    private final GameUI    ui;
    private final GameState state;
    private final AIPlayer  ai;

    private static final int AI_DELAY_MS = 900;

    public GameEngine(GameMode mode, GameUI ui) {
        this.ui = ui;
        this.ai = new AIPlayer();

        Player human  = new Player("PLAYER", false, mode.getStartingCharges());
        Player aiPlayer = new Player("DEALER", true, mode.getStartingCharges());
        Gun    gun    = new Gun(mode.getGunName());

        this.state = new GameState(mode, human, aiPlayer, gun);
    }

    public void startGame() {
        
        ui.setOnShootOpponent(this::humanShootsOpponent);
        ui.setOnShootSelf(this::humanShootsSelf);
        ui.getTablePanel().setOnItemUsed(this::humanUsesItem);

        startRound();
    }

    private void startRound() {
        
        state.getHuman().resetForNewRound(state.getMode().getStartingCharges());
        state.getAI().resetForNewRound(state.getMode().getStartingCharges());

        Map<Integer, BulletType> knownSlots = GunLoader.load(state.getGun(), state.getMode());
        ai.setKnownSlots(knownSlots);

        GunLoader.dealItems(state.getHuman(), state.getMode());
        GunLoader.dealItems(state.getAI(),    state.getMode());

        state.getHuman(); 
        
        forceHumanTurn();

        ui.refresh(state);
        startTurn();
    }

    private void forceHumanTurn() {
        
        while (!state.isHumanTurn()) state.switchTurn();
    }

    private void startTurn() {
        Player current = state.getCurrentTurnPlayer();

        if (current.isSkipNextTurn()) {
            current.setSkipNextTurn(false);
            ui.showResult(current.getName() + " is skipped (handcuffs)!", false);
            pause(800, () -> {
                ui.clearResult();
                state.switchTurn();
                ui.refresh(state);
                startTurn();
            });
            return;
        }

        if (state.getGun().isEmpty()) {
            reloadGun();
            return;
        }

        ui.refresh(state);

        if (!state.isHumanTurn()) {
            runAITurn();
        }
        
    }

    private void reloadGun() {
        Map<Integer, BulletType> knownSlots = GunLoader.load(state.getGun(), state.getMode());
        ai.setKnownSlots(knownSlots);
        ui.showResult("Chamber empty — reloading...", false);
        pause(1000, () -> {
            ui.clearResult();
            ui.refresh(state);
            startTurn();
        });
    }

    public void humanShootsOpponent() {
        if (!state.isHumanTurn()) return;
        resolveShot(state.getHuman(), state.getAI());
    }

    public void humanShootsSelf() {
        if (!state.isHumanTurn()) return;
        resolveShot(state.getHuman(), state.getHuman());
    }

    public void humanUsesItem(Item item) {
        if (!state.isHumanTurn()) return;
        if (!state.getHuman().hasItem(item)) return;

        String msg = ItemEffect.apply(item, state.getHuman(), state);

        ui.showResult(msg, false);
        ui.refresh(state);

        if (state.getGun().isEmpty()) {
            pause(700, this::reloadGun);
        }
        
    }

    private void runAITurn() {
        new SwingWorker<Void, String>() {

            @Override
            protected Void doInBackground() throws Exception {
                
                Item itemToUse;
                while ((itemToUse = ai.decideItem(state)) != null) {
                    final Item chosen = itemToUse;
                    Thread.sleep(AI_DELAY_MS);

                    final String msg = ItemEffect.apply(chosen, state.getAI(), state);

                    if (chosen == Item.MAGNIFYING_GLASS) {
                        BulletType cur = state.getGun().peekCurrent();
                        if (cur != null)
                            ai.revealSlot(state.getGun().getCurrentIndex(), cur);
                    } else if (chosen == Item.FLIP_PHONE) {
                        int[] peek = state.getGun().peekRandom();
                        if (peek != null)
                            ai.revealSlot(peek[0], BulletType.values()[peek[1]]);
                    }

                    publish(msg);
                    Thread.sleep(600);
                }

                Thread.sleep(AI_DELAY_MS);
                boolean shootSelf = ai.decideShootSelf(state);
                Player target = shootSelf ? state.getAI() : state.getHuman();
                publish("AI targets: " + (shootSelf ? "SELF" : state.getHuman().getName()));
                Thread.sleep(500);

                resolveShot(state.getAI(), target);
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String msg : chunks) {
                    ui.showResult(msg, msg.contains("LIVE"));
                    ui.refresh(state);
                }
            }

            @Override
            protected void done() {
                try { get(); } catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }

    private void resolveShot(Player shooter, Player target) {
        BulletType bullet = state.getGun().fire();
        state.setLastFiredBullet(bullet);
        ai.advanceIndex(state.getGun().getCurrentIndex());

        boolean selfShot = (shooter == target);

        if (bullet == BulletType.LIVE) {
            int damage = state.isDoubleDamage() ? 2 : 1;
            state.setDoubleDamage(false);
            target.removeCharge(damage);

            String msg = selfShot
                ? shooter.getName() + " shot themselves! -" + damage + " charge."
                : shooter.getName() + " shot " + target.getName() + "! -" + damage + " charge.";
            ui.showResult(msg, true);

            pause(1200, () -> {
                ui.clearResult();
                if (!target.isAlive()) {
                    handlePlayerDead(target);
                } else {
                    
                    state.switchTurn();
                    ui.refresh(state);
                    startTurn();
                }
            });

        } else { 
            state.setDoubleDamage(false);
            if (selfShot) {
                
                ui.showResult(shooter.getName() + " — BLANK. Take another turn!", false);
                pause(1000, () -> {
                    ui.clearResult();
                    ui.refresh(state);
                    startTurn(); 
                });
            } else {
                
                ui.showResult("BLANK — no damage.", false);
                pause(900, () -> {
                    ui.clearResult();
                    state.switchTurn();
                    ui.refresh(state);
                    startTurn();
                });
            }
        }
    }

    private void handlePlayerDead(Player loser) {
        Player winner = state.getOpponent(loser);
        winner.addRoundWin();
        ui.refresh(state);

        if (state.checkGameOver()) {
            handleGameOver();
        } else {
            
            String body = winner.getName() + " wins round " + state.getCurrentRound() + "!\n\n"
                        + "Score:  PLAYER " + state.getHuman().getRoundsWon()
                        + "  vs  " + state.getAI().getRoundsWon() + "  DEALER";
            int choice = ui.showEventDialog("ROUND OVER", body, new String[]{"Next Round"});
            state.advanceRound();
            startRound();
        }
    }

    private void handleGameOver() {
        Player winner = state.getWinner();
        boolean humanWon = (winner == state.getHuman());
        String title = humanWon ? "YOU WIN!" : "GAME OVER";
        String body  = winner.getName() + " wins the match!\n\n"
                     + "Final score:  PLAYER " + state.getHuman().getRoundsWon()
                     + "  vs  " + state.getAI().getRoundsWon() + "  DEALER";

        int choice = ui.showEventDialog(title, body, new String[]{"Play Again", "Quit"});
        if (choice == 0) {
            
            state.getHuman().resetForNewRound(state.getMode().getStartingCharges());
            state.getAI().resetForNewRound(state.getMode().getStartingCharges());
            
            restartGame();
        } else {
            System.exit(0);
        }
    }

    private void restartGame() {
        SwingUtilities.invokeLater(() -> {
            ui.dispose();
            model.GameMode mode = ModeSelectDialog.show(null);
            if (mode == null) { System.exit(0); return; }
            GameUI newUI = new GameUI(mode);
            new GameEngine(mode, newUI).startGame();
        });
    }

    private void pause(int delayMs, Runnable action) {
        Timer t = new Timer(delayMs, e -> action.run());
        t.setRepeats(false);
        t.start();
    }

    public GameState getState() { return state; }
}
