package logic;

import model.BulletType;
import model.GameMode;
import model.Gun;
import model.Item;
import model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GunLoader {

    private GunLoader() {}


    public static Map<Integer, BulletType> load(Gun gun, GameMode mode) {
        Random rng = new Random();

        List<BulletType> bullets = new ArrayList<>();
        for (int i = 0; i < mode.getLiveBullets(); i++)
            bullets.add(BulletType.LIVE);
        while (bullets.size() < mode.getTotalBullets())
            bullets.add(BulletType.BLANK);
        Collections.shuffle(bullets, rng);

        gun.load(bullets);

        Map<Integer, BulletType> knownSlots = new HashMap<>();
        int knownCount = mode.getAiKnownPositions();
        if (knownCount == 0) return knownSlots;


        List<Integer> liveIndices = new ArrayList<>();
        for (int i = 0; i < bullets.size(); i++)
            if (bullets.get(i) == BulletType.LIVE) liveIndices.add(i);

        if (!liveIndices.isEmpty()) {
            int pick = liveIndices.get(rng.nextInt(liveIndices.size()));
            knownSlots.put(pick, BulletType.LIVE);
        }

        if (knownCount >= 3) {
            List<Integer> blankIndices = new ArrayList<>();
            for (int i = 0; i < bullets.size(); i++)
                if (bullets.get(i) == BulletType.BLANK && !knownSlots.containsKey(i))
                    blankIndices.add(i);
            Collections.shuffle(blankIndices, rng);
            int n = Math.min(2, blankIndices.size());
            for (int i = 0; i < n; i++)
                knownSlots.put(blankIndices.get(i), BulletType.BLANK);
        }

        return knownSlots;
    }


    public static void dealItems(Player player, GameMode mode) {
        List<Item> pool = new ArrayList<>(List.of(Item.values()));
        Collections.shuffle(pool);
        int count = Math.min(mode.getItemsPerDeal(), pool.size());
        for (int i = 0; i < count; i++)
            player.addItem(pool.get(i));
    }
}
