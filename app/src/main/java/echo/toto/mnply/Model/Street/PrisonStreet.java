package echo.toto.mnply.Model.Street;


import echo.toto.mnply.Game.Player;

public class PrisonStreet extends Street {

    public static final int NB_TOURS_PRISON = 3;

    public PrisonStreet() {
        super("Prison");
    }

    @Override
    public void action(Player player, int[] dices) {
        if (dices[0] == dices[1]) {
            player.setPrison(0);
        }
        if (player.getPrison() == 0) {
            player.endTurn();
            return;
        }

        player.reducePrison();
        player.endTurn();
    }
}
