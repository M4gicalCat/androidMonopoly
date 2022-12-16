package echo.toto.mnply.Model.Street;

import echo.toto.mnply.Game.Player;
import echo.toto.mnply.Model.Model;

public abstract class CompagnieDeDistributionStreet extends BuyableStreet {
    public CompagnieDeDistributionStreet(String name) {
        super(name, 150, null);
    }

    @Override
    public void action(Player player, int[] dices) {
        if (owner == null || player.getId().equals(owner.getId())) {
            super.action(player, dices);
            return;
        }
        Player p1 = ((BuyableStreet) Model.getStreet(Model.getPosition("Compagnie d'électricité"))).getOwner();
        Player p2 = ((BuyableStreet) Model.getStreet(Model.getPosition("Compagnie de distribution d'eau"))).getOwner();
        boolean hasBothCompanies = (p1 != null && p2 != null && p1.getId().equals(p2.getId()));

        int aPayer = (hasBothCompanies ? 10 : 4) * (dices[0] + dices[1]);
        player.paye(p1, aPayer);
        player.endTurn();
    }
}
