package model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private final boolean isAI;
    private int charges;
    private int roundsWon;
    private boolean skipNextTurn;
    private boolean hasExchangedItem;
    private final List<Item> items;

    public Player(String name, boolean isAI, int startingCharges) {
        this.name = name;
        this.isAI = isAI;
        this.charges = startingCharges;
        this.roundsWon = 0;
        this.skipNextTurn = false;
        this.hasExchangedItem = false;
        this.items = new ArrayList<>();
    }

    // --- Charges (HP) ---

    public int getCharges() {
        return charges;
    }

    public void setCharges(int charges) {
        this.charges = Math.max(0, charges);
    }

    public void removeCharge(int amount) {
        this.charges = Math.max(0, this.charges - amount);
    }

    public void addCharge(int amount) {
        this.charges += amount;
    }

    public boolean isAlive() {
        return charges > 0;
    }

    // --- Items ---

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public boolean hasItem(Item item) {
        return items.contains(item);
    }

    public void clearItems() {
        items.clear();
    }

    // --- Turn state ---

    public boolean isSkipNextTurn() {
        return skipNextTurn;
    }

    public void setSkipNextTurn(boolean skip) {
        this.skipNextTurn = skip;
    }

    public boolean hasExchangedItem() {
        return hasExchangedItem;
    }

    public void setHasExchangedItem(boolean exchanged) {
        this.hasExchangedItem = exchanged;
    }

    // --- Rounds won ---

    public int getRoundsWon() {
        return roundsWon;
    }

    public void addRoundWin() {
        this.roundsWon++;
    }

    // --- Identity ---

    public String getName() {
        return name;
    }

    public boolean isAI() {
        return isAI;
    }


    public void resetForNewRound(int startingCharges) {
        this.charges = startingCharges;
        this.skipNextTurn = false;
        this.hasExchangedItem = false;
        this.items.clear();
    }

    @Override
    public String toString() {
        return String.format("%s [HP:%d | Items:%d | Wins:%d]",
                name, charges, items.size(), roundsWon);
    }
}
