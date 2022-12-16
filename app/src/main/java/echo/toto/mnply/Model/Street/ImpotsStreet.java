package echo.toto.mnply.Model.Street;

import echo.toto.mnply.Game.Player;

public class ImpotsStreet extends Street {
    public ImpotsStreet() {
        super("Impôts sur le revenu");
    }

    @Override
    public void action(Player player, int[] dices) {
        player.updateMoney(-200);
        ParcGratuitStreet.pay(200);
        player.endTurn();
    }
}
