package echo.toto.mnply.Model.Street;

import echo.toto.mnply.Game.Player;

public abstract class CompagnieDeDistributionStreet extends BuyableStreet {
    public CompagnieDeDistributionStreet(String name) {
        super(name, 150, null);
    }

    @Override
    public void action(Player player) {
        if (owner == null || player.getId().compareTo(owner.getId()) == 0) {
            player.endTurn();
            return;
        }
        player.endTurn();
        //todo valeur des dés ?
    }
}
