package echo.toto.mnply.Model.Street;


import echo.toto.mnply.Game.Player;
import echo.toto.mnply.UI.Popup;

public class BuyableStreet extends Street {
    protected int price;
    protected Player owner;
    protected Loyer loyer;
    protected int nbMaisons;
    protected int nbHotel;
    protected boolean isGare;

    public BuyableStreet(String name, int price, Loyer loyer) {
        super(name);
        this.price = price;
        this.loyer = loyer;
        nbMaisons = 0;
        nbHotel = 0;
        isGare = false;
    }

    @Override
    public void action(Player player, int[] dices) {
        if (owner != null && player.getId().compareTo(owner.getId()) == 0) {
            player.endTurn();
            return;
        }

        if (owner != null) {
            int loyerAPayer = nbHotel > 0 ? loyer.getLoyerHotel(nbHotel) : loyer.getLoyerMaison(nbMaisons);
            player.paye(owner, loyerAPayer);
            player.endTurn();
            return;
        }

        if (player.getArgent() < price) {
            player.endTurn();
            return;
        }

        // propose d'acheter
        player.getGame().getActivity().runOnUiThread(() -> player.getGame().getActivity().affichePopup(new Popup(
                "Voulez vous acheter " + name + " pour " + price + " € ?",
                "Acheter",
                "Passer",
                () -> {
                    player.buyStreet(this);
                    player.endTurn();
                },
                player::endTurn
        )));
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getPrice() {
        return price;
    }

    public int getNbHotel() {return nbHotel;}

    public int getNbMaison() {return nbMaisons;}

    public Player getOwner() {
        return owner;
    }
}
