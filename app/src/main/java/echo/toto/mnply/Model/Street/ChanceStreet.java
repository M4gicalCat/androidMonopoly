package echo.toto.mnply.Model.Street;

import android.util.Log;

import echo.toto.mnply.Game.Player;
import echo.toto.mnply.Model.Card;
import echo.toto.mnply.Model.Model;

public class ChanceStreet extends Street {
    public ChanceStreet() {
        super("Chance");
    }

    @Override
    public void action(Player player, int[] dices) {
        Card card = Model.tireCarteChance();
        Log.i("Carte", card.getTexte());
        card.action(player);
    }
}
