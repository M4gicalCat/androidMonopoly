package echo.toto.mnply.Model.Street;

public class Loyer {
    private final int[] loyersMaisons;
    private final int loyersHotels;

    public Loyer(int[] loyersMaisons, int loyersHotels) {
        this.loyersMaisons = loyersMaisons;
        this.loyersHotels = loyersHotels;
    }

    public int getLoyerMaison(int nbMaisons) {
        return loyersMaisons[nbMaisons];
    }

    public int getLoyerHotel(int nbHotel) {
        return loyersHotels;
    }
}
