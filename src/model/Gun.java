package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Gun {
    private List<BulletType> chamber;
    private int currentIndex;
    private final String modelName;
    private final Random random;

    public Gun(String modelName) {
        this.modelName = modelName;
        this.chamber = new ArrayList<>();
        this.currentIndex = 0;
        this.random = new Random();
    }

    public void load(List<BulletType> bullets) {
        this.chamber = new ArrayList<>(bullets);
        this.currentIndex = 0;
    }


    public BulletType fire() {
        if (isEmpty()) return null;
        BulletType result = chamber.get(currentIndex);
        currentIndex++;
        return result;
    }


    public BulletType ejectCurrent() {
        if (isEmpty()) return null;
        BulletType ejected = chamber.get(currentIndex);
        currentIndex++;
        return ejected;
    }


    public BulletType peekCurrent() {
        if (isEmpty()) return null;
        return chamber.get(currentIndex);
    }

    public int[] peekRandom() {
        int remaining = chamber.size() - currentIndex;
        if (remaining < 2) return null;

        List<Integer> candidates = new ArrayList<>();
        for (int i = currentIndex + 1; i < chamber.size(); i++) {
            candidates.add(i);
        }
        int chosen = candidates.get(random.nextInt(candidates.size()));
        BulletType type = chamber.get(chosen);

        return new int[]{ chosen, type.ordinal() };
    }


    public void addRandomBullet() {
        BulletType newBullet = random.nextBoolean() ? BulletType.LIVE : BulletType.BLANK;

        int insertAt = currentIndex + random.nextInt(chamber.size() - currentIndex + 1);
        chamber.add(insertAt, newBullet);
    }

    // --- Probability helpers

    public double blankProbability() {
        int remaining = chamber.size() - currentIndex;
        if (remaining == 0) return 0.0;
        long blanks = chamber.subList(currentIndex, chamber.size())
                             .stream()
                             .filter(b -> b == BulletType.BLANK)
                             .count();
        return (double) blanks / remaining;
    }


    public double liveProbability() {
        return 1.0 - blankProbability();
    }


    public boolean isEmpty() {
        return currentIndex >= chamber.size();
    }

    public int getRemainingCount() {
        return chamber.size() - currentIndex;
    }

    public int getTotalCount() {
        return chamber.size();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public BulletType getBulletAt(int index) {
        if (index < 0 || index >= chamber.size()) return null;
        return chamber.get(index);
    }

    public String getModelName() {
        return modelName;
    }

    public String getChamberDisplay() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chamber.size(); i++) {
            if (i < currentIndex) {
                sb.append("X"); // spent
            } else {
                sb.append("?"); // unknown
            }
            if (i < chamber.size() - 1) sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("%s [%d/%d remaining | Live%.0f%% | Blank%.0f%%]",
                modelName,
                getRemainingCount(), getTotalCount(),
                liveProbability() * 100, blankProbability() * 100);
    }
}
