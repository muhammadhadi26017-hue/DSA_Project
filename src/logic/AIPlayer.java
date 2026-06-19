package logic;

import model.BulletType;
import model.GameState;
import model.Item;
import model.Player;

import java.util.HashMap;
import java.util.Map;

public class AIPlayer {

    private final Map<Integer, BulletType> knownSlots = new HashMap<>();

    public void setKnownSlots(Map<Integer, BulletType> slots) {
        knownSlots.clear();
        knownSlots.putAll(slots);
    }

    public void advanceIndex(int newIndex) {
        knownSlots.entrySet().removeIf(e -> e.getKey() < newIndex);
    }

    public void revealSlot(int index, BulletType type) {
        knownSlots.put(index, type);
    }

    public Item decideItem(GameState state) {
        Player ai       = state.getAI();
        Player human    = state.getHuman();
        int    curIdx   = state.getGun().getCurrentIndex();
        BulletType curKnown = knownSlots.get(curIdx);
        double blankProb = state.getGun().blankProbability();

        if (ai.hasItem(Item.HANDCUFFS) && human.getCharges() > 1)
            return Item.HANDCUFFS;

        if (ai.hasItem(Item.PILL) && curKnown == BulletType.LIVE)
            return Item.PILL;

        if (ai.hasItem(Item.MAGNIFYING_GLASS) && curKnown == null)
            return Item.MAGNIFYING_GLASS;

        if (ai.hasItem(Item.BEER) && curKnown == BulletType.LIVE && blankProb < 0.3)
            return Item.BEER;

        if (ai.hasItem(Item.CIGARETTE) && ai.getCharges() == 1)
            return Item.CIGARETTE;

        if (ai.hasItem(Item.FLIP_PHONE) && curKnown == null
                && !ai.hasItem(Item.MAGNIFYING_GLASS))
            return Item.FLIP_PHONE;

        return null; 
    }

    public boolean decideShootSelf(GameState state) {
        int    curIdx    = state.getGun().getCurrentIndex();
        BulletType curKnown = knownSlots.get(curIdx);
        double blankProb = state.getGun().blankProbability();

        if (curKnown == BulletType.BLANK) return true;

        if (curKnown == BulletType.LIVE) return false;

        double threshold = switch (state.getMode()) {
            case EASY   -> 0.75;  
            case NORMAL -> 0.60;
            case HARD   -> 0.50;  
        };

        return blankProb >= threshold;
    }
}
