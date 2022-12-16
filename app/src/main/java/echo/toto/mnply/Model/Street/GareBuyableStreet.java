package echo.toto.mnply.Model.Street;

import echo.toto.mnply.Game.Player;

public class GareBuyableStreet extends BuyableStreet {
    public GareBuyableStreet(String name) {
        super(name, 200, null);
        isGare = true;
    }

    @Override
    public void action(Player player, int[] dices) {
        if (owner == null || player.getId().compareTo(owner.getId()) == 0) {
            super.action(player, dices);
            return;
        }
        int aPayer = 0;
        /* 25 50 100 200 */

        for (Street s : owner.getStreets()) {
            if (s instanceof GareBuyableStreet) aPayer++;
        }
        switch (aPayer) {
            case 1:
                player.updateMoney(25);
                break;
            case 2:
                player.updateMoney(50);
                break;
            case 3:
                player.updateMoney(100);
                break;
            case 4:
                player.updateMoney(200);
                break;
        }
        player.endTurn();
    }
}
