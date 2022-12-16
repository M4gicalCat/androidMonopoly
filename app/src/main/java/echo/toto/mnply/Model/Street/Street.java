package echo.toto.mnply.Model.Street;

import echo.toto.mnply.Game.Player;

public  abstract class Street {
    protected String name;

    public Street(String name) {
        this.name = name;
    }

    public abstract void action(Player player, int[] dices);

    public int getNbHotel() {return 0;}

    public int getNbMaison() {return 0;}

    public String getName() {
        return name;
    }
}
