package echo.toto.mnply.Model.Street;

import echo.toto.mnply.Game.Player;

public class DepartStreet extends Street {
    public DepartStreet() {
        super("Départ");
    }

    @Override
    public void action(Player player, int[] dices) {
        player.updateMoney(200);
        player.endTurn();
    }
}
