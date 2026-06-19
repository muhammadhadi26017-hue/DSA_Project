package logic;

import model.BulletType;
import model.GameState;
import model.Item;
import model.Player;

public class ItemEffect {

    private ItemEffect() {}

    public static String apply(Item item, Player user, GameState state) {
        user.removeItem(item);

        return switch (item) {

            case BEER -> {
                
                BulletType ejected = state.getGun().ejectCurrent();
                if (ejected == null) yield "Chamber was already empty!";
                yield "Beer can — ejected a " + ejected.name() + " round.";
            }

            case CIGARETTE -> {
                
                user.addCharge(1);
                yield user.getName() + " smoked a cigarette — +1 charge.";
            }

            case VODKA -> {
                
                state.getGun().addRandomBullet();
                yield "Vodka — a random bullet was added to the chamber.";
            }

            case MAGNIFYING_GLASS -> {
                
                BulletType current = state.getGun().peekCurrent();
                if (current == null) yield "Chamber is empty — nothing to reveal.";
                yield "Magnifying glass — current round is " + current.name() + ".";
            }

            case FLIP_PHONE -> {
                
                int[] result = state.getGun().peekRandom();
                if (result == null) yield "Flip phone — not enough rounds left to reveal.";
                BulletType revealed = BulletType.values()[result[1]];
                yield "Flip phone — slot #" + (result[0] + 1) + " is " + revealed.name() + ".";
            }

            case HANDCUFFS -> {
                
                Player opponent = state.getOpponent(user);
                opponent.setSkipNextTurn(true);
                yield "Handcuffs — " + opponent.getName() + "'s next turn is skipped!";
            }

            case PILL -> {
                
                state.setDoubleDamage(true);
                yield "Pill — next shot deals DOUBLE damage!";
            }
        };
    }
}
