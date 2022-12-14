package echo.toto.mnply.Model.Street;

import echo.toto.mnply.Game.Player;

public class ParcGratuitStreet extends Street {
    private static int total;
    public ParcGratuitStreet() {
        super("Parc gratuit");
        total = 0;
    }

    @Override
    public void action(Player player) {
        player.updateMoney(total);
        total = 0;
        player.endTurn();
    }

    public static void pay(int money) {
        total += money;
    }
}
