package echo.toto.mnply.Model.Street;

import echo.toto.mnply.Game.Player;

public class AllezEnPrisonStreet extends Street {
    public AllezEnPrisonStreet() {
        super("Allez en prison");
    }

    @Override
    public void action(Player player, int[] dices) {
        player.vaEnPrison();
    }
}
