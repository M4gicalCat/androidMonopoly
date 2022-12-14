package echo.toto.mnply.Model.Street;

import echo.toto.mnply.Game.Player;
import echo.toto.mnply.Model.Card;
import echo.toto.mnply.Model.Model;

public class CaisseDeCommunauteStreet extends Street {
    public CaisseDeCommunauteStreet() {
        super("Caisse de communaut√©");
    }

    @Override
    public void action(Player player, int[] dices) {
        Card card = Model.tireCarteCaisseDeCommunaute();
        card.action(player);
    }
}
