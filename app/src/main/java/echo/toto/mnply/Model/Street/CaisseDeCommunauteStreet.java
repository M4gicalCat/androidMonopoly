package echo.toto.mnply.Model.Street;

import echo.toto.mnply.Game.Player;
import echo.toto.mnply.Model.Card;
import echo.toto.mnply.Model.Model;

public class CaisseDeCommunauteStreet extends Street {
    public CaisseDeCommunauteStreet() {
        super("Caisse de communauté");
    }

    @Override
    public void action(Player player) {
        Card card = Model.tireCarteCaisseDeCommunaute();
        card.action(player);
    }
}
