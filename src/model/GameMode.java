package model;

public enum GameMode {
    EASY(
        "Easy",
        "Glock",
        4,          
        2,          
        3,          
        3,          
        2,          
        true,       
        0           
    ),
    NORMAL(
        "Normal",
        "1872 Swiss Revolver",
        6,
        3,
        5,
        4,
        2,
        false,
        1           
    ),
    HARD(
        "Hard",
        "Mossberg 12 Gauge",
        11,
        6,
        7,
        6,
        3,
        true,
        3           
    );

    private final String displayName;
    private final String gunName;
    private final int totalBullets;
    private final int liveBullets;
    private final int itemsPerDeal;
    private final int startingCharges;
    private final int roundsToWin;
    private final boolean canExchangeItem;
    private final int aiKnownPositions;

    GameMode(String displayName, String gunName, int totalBullets, int liveBullets,
             int itemsPerDeal, int startingCharges, int roundsToWin,
             boolean canExchangeItem, int aiKnownPositions) {
        this.displayName = displayName;
        this.gunName = gunName;
        this.totalBullets = totalBullets;
        this.liveBullets = liveBullets;
        this.itemsPerDeal = itemsPerDeal;
        this.startingCharges = startingCharges;
        this.roundsToWin = roundsToWin;
        this.canExchangeItem = canExchangeItem;
        this.aiKnownPositions = aiKnownPositions;
    }

    public String getDisplayName()    { return displayName; }
    public String getGunName()        { return gunName; }
    public int getTotalBullets()      { return totalBullets; }
    public int getLiveBullets()       { return liveBullets; }
    public int getItemsPerDeal()      { return itemsPerDeal; }
    public int getStartingCharges()   { return startingCharges; }
    public int getRoundsToWin()       { return roundsToWin; }
    public boolean canExchangeItem()  { return canExchangeItem; }
    public int getAiKnownPositions()  { return aiKnownPositions; }

    @Override
    public String toString() {
        return displayName;
    }
}
