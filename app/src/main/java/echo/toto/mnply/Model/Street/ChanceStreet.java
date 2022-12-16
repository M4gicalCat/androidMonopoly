package echo.toto.mnply.Model.Street;

import echo.toto.mnply.Game.Player;
import echo.toto.mnply.Model.Model;

public class ChanceStreet extends Street {
    public ChanceStreet() {
        super("Chance");
    }

    @Override
    public void action(Player player, int[] dices) {
        Model.tireCarteChance().action(player);
    }
}
