package model;

public enum Item {
    BEER("Beer Can", "Ejects the current chambered round"),
    CIGARETTE("Cigarette", "Gain an extra charge"),
    VODKA("Vodka", "Adds another bullet to the gun"),
    MAGNIFYING_GLASS("Magnifying Glass", "Reveals the nature of the current round"),
    FLIP_PHONE("Flip Phone", "Reveals the nature of a random round"),
    HANDCUFFS("Handcuffs", "Forcefully skips the opponent's next turn"),
    PILL("Pill", "The current bullet will cause double damage");

    private final String displayName;
    private final String description;

    Item(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
