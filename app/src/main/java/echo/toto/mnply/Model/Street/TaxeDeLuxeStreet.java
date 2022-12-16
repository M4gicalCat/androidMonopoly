package echo.toto.mnply.Model.Street;

import echo.toto.mnply.Game.Player;

public class TaxeDeLuxeStreet extends Street {
    public TaxeDeLuxeStreet() {
        super("Taxe de Luxe");
    }

    @Override
    public void action(Player player, int[] dices) {
        player.updateMoney(-100);
        ParcGratuitStreet.pay(100);
        player.endTurn();
    }
}
