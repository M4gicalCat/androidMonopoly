package echo.toto.mnply.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import echo.toto.mnply.Game.Player;
import echo.toto.mnply.Model.Street.*;
import echo.toto.mnply.UI.Popup;

public class Model {
    private static final Map<Integer, Street> streets = new HashMap<>();
    private static final ArrayList<Card> piocheCartesDeCommunaute = new ArrayList<>();
    private static final ArrayList<Card> defausseCartesDeCommunaute = new ArrayList<>();
    private static final ArrayList<Card> piocheCartesChance = new ArrayList<>();
    private static final ArrayList<Card> defausseCartesChance = new ArrayList<>();
    private static int nbStreets;
    private static Random random;

    public static void init(Random random) {
        Model.random = random;
        initDefaultStreets();
        nbStreets = streets.size();
        initCartesCaisseDeCommunaute();
        initCartesChance();
    }

    //todo annoncer aux autres qu'on tire une carte pour ne pas qu'ils tirent la même

    private static void initCartesChance() {
        piocheCartesChance.add(new Card("La Banque vous verse un dividende de € 50", p -> {
            p.updateMoney(50);
            p.endTurn();
        }));

        piocheCartesChance.add(new Card("Votre immeuble et votre prêt rapportent. Vous devez touchez € 150", p -> {
            p.updateMoney(150);
            p.endTurn();
        }));

        piocheCartesChance.add(new Card("Allez à la gare de Lyon. Si vous passez par la case Départ, recevez € 200", p -> {
            int posGareLyon = getPosition("Gare de Lyon");
            if (p.getPosition() > posGareLyon) p.updateMoney(200);
            p.setPosition(posGareLyon);
        }));

        piocheCartesChance.add(new Card("Allez en prison.", p -> p.setPosition(getPosition("Prison"))));

        piocheCartesChance.add(new Card("Rendez-vous à l'Avenue Henri-Martin. Si vous passez par la case Départ recevez € 200", p -> {
            int posAvenueHenriMartin = getPosition("Avenue Henry-Martin");
            if (p.getPosition() > posAvenueHenriMartin) p.updateMoney(200);
            p.setPosition(posAvenueHenriMartin);
        }));

        piocheCartesChance.add(new Card("Vous êtes imposé pour les réparations de voirie à raison de : € 40 par maison et € 115 par hôtel", p -> {
            int nbMaisons = 0;
            int nbHotel = 0;
            for (Street s : p.getStreets()) {
                if (s.getNbHotel() > 0) nbHotel += s.getNbHotel();
                else nbMaisons += s.getNbMaison();
            }
            int total = (nbMaisons * 40 + nbHotel * 115);
            p.updateMoney(-total);
            ParcGratuitStreet.pay(total);
            p.endTurn();
        }));

        piocheCartesChance.add(new Card("Payez pour frais de scolarité", p -> {
            p.updateMoney(-150);
            ParcGratuitStreet.pay(150);
            p.endTurn();
        }));

        piocheCartesChance.add(new Card("Amende pour excès de vitesse : € 15", p -> p.updateMoney(-15)));

        piocheCartesChance.add(new Card("Avancez au Boulevard de la Villette. Si vous passez par la case Départ, recevez € 200", p -> {
            int posBvdVillette = getPosition("Boulevard de la Villette");
            if (p.getPosition() > posBvdVillette) p.updateMoney(200);
            p.setPosition(posBvdVillette);
        }));

        piocheCartesChance.add(new Card("Amende pour ivresse : € 20", p -> {
            p.updateMoney(-20);
            ParcGratuitStreet.pay(20);
            p.endTurn();
        }));

        piocheCartesChance.add(new Card("Avancez jusqu'à la case Départ", p -> p.setPosition(0)));

        piocheCartesChance.add(new Card("Vous gagnez un jeton de libération de prison", Player::addJetonSortiePrison));

        piocheCartesChance.add(new Card("Faites des réparations dans toutes vos maisons. Versez pour chaque maison € 25. Versez pour chaque hôtel € 100", p -> {
            int nbMaisons = 0;
            int nbHotel = 0;
            for (Street s : p.getStreets()) {
                if (s.getNbHotel() > 0) nbHotel += s.getNbHotel();
                else nbMaisons += s.getNbMaison();
            }
            int total = (nbMaisons * 25 + nbHotel * 100);
            p.updateMoney(-total);
            ParcGratuitStreet.pay(total);
            p.endTurn();
        }));

        piocheCartesChance.add(new Card("Reculez de trois cases", p -> {
            int pos = p.getPosition() - 3;
            p.setPosition(pos < 0 ? pos + streets.size() : pos);
        }));

        piocheCartesChance.add(new Card("Vous avez gagné le prix de mots croisés. Recevez € 100", p -> {
            p.updateMoney(100);
            p.endTurn();
        }));

        piocheCartesChance.add(new Card("Rendez vous à la Rue de la Paix", p -> p.setPosition(getPosition("Rue de la Paix"))));

        shuffle(piocheCartesChance, defausseCartesChance);
    }

    private static void shuffle(ArrayList<Card> pioche, ArrayList<Card> defausse) {
        defausse.addAll(pioche);
        pioche.clear();
        addFromDefausse(pioche, defausse);
    }

    private static void initCartesCaisseDeCommunaute() {
        piocheCartesDeCommunaute.add(new Card("Payez à l'Hôpital € 100", (player) -> {
            player.updateMoney(-100);
            ParcGratuitStreet.pay(100);
            player.endTurn();
        }));

        piocheCartesDeCommunaute.add(new Card("Avancez jusqu'à la case Départ", (p) -> p.setPosition(0)));

        piocheCartesDeCommunaute.add(new Card("Vous gagnez un jeton de libération de prison", p -> {
            p.addJetonSortiePrison();
            p.endTurn();
        }));

        piocheCartesDeCommunaute.add(new Card("Erreur de la banque en votre faveur. Recevez € 200.", p -> {
            p.updateMoney(200);
            p.endTurn();
        }));

        piocheCartesDeCommunaute.add(new Card("Payez votre Police d'Assurance s'élevant à € 50", p -> {
            p.updateMoney(-50);
            ParcGratuitStreet.pay(50);
            p.endTurn();
        }));

        piocheCartesDeCommunaute.add(new Card("Retournez à Belleville", p -> {
            int pos = getPosition("Boulevard de Belleville");
            p.setPosition(pos);
        }));

        piocheCartesDeCommunaute.add(new Card("Payez une amende de €10 ou bien tirez une carte chance", p ->
            p.getGame().getActivity().affichePopup(
                new Popup("Payez une amende de €10 ou bien tirez une carte chance",
                    "Payer",
                    "Carte chance",
                    () -> {
                        p.updateMoney(-10);
                        p.endTurn();
                    },
                    () -> tireCarteChance().action(p)
                )
            )
        ));

        piocheCartesDeCommunaute.add(new Card("Allez en prison.", Player::vaEnPrison));

        piocheCartesDeCommunaute.add(new Card("Payez la note du Médecin € 50", p -> {
            p.updateMoney(-50);
            ParcGratuitStreet.pay(50);
            p.endTurn();
        }));

        piocheCartesDeCommunaute.add(new Card("Vous héritez € 100", p -> p.updateMoney(100)));

        piocheCartesDeCommunaute.add(new Card("C'est votre anniversaire : chaque joueur doit vous donner € 10", p -> {
            ArrayList<Player> players = p.getGame().getPlayers();
            int total = 10 * (players.size() + 1);
            for (Player player : players) player.updateMoney(-10);
            p.updateMoney(total);
            //todo faire perdre de l'argent aux autres aussi chez eux
            p.endTurn();
        }));

        piocheCartesDeCommunaute.add(new Card("Recevez votre intérêt sur l'emprunt à 7%. € 25", p -> {
            p.updateMoney(25);
            p.endTurn();
        }));

        piocheCartesDeCommunaute.add(new Card("Recevez votre revenu annuel € 100", p -> {
            p.updateMoney(100);
            p.endTurn();
        }));

        piocheCartesDeCommunaute.add(new Card("Vous avez gagné le deuxième Prix de Beauté. Recevez € 10", p -> {
            p.updateMoney(10);
            p.endTurn();
        }));

        piocheCartesDeCommunaute.add(new Card("La vente de votre stock vous rapporte € 50", p -> {
            p.updateMoney(50);
            p.endTurn();
        }));

        piocheCartesDeCommunaute.add(new Card("Les Contributions vous remboursent la somme de € 20", p -> {
            p.updateMoney(20);
            p.endTurn();
        }));

        shuffle(defausseCartesDeCommunaute, defausseCartesDeCommunaute);
    }

    private static void initDefaultStreets() {
        streets.put(0, new DepartStreet());
        streets.put(1, new BuyableStreet("Boulevard de Belleville", 60, new Loyer(new int[]{2, 10, 30, 90, 160}, 250)));
        streets.put(2, new CaisseDeCommunauteStreet());
        streets.put(3, new BuyableStreet("Rue Lecourbe", 60, new Loyer(new int[]{4, 20, 60, 180, 320}, 450)));
        streets.put(4, new ImpotsStreet());
        streets.put(5, new GareBuyableStreet("Gare Montparnasse"));
        streets.put(6, new BuyableStreet("Rue de Vaugirard", 100, new Loyer(new int[]{6, 30, 90, 270, 400}, 550)));
        streets.put(7, new ChanceStreet());
        streets.put(8, new BuyableStreet("Rue de Courcelles", 100, new Loyer(new int[]{6, 30, 90, 270, 400}, 550)));
        streets.put(9, new BuyableStreet("Avenue de la République", 100, new Loyer(new int[]{8, 40, 100, 300, 450, 600}, 600)));
        streets.put(10, new PrisonStreet());
        streets.put(11, new BuyableStreet("Boulevard de la Villette", 140, new Loyer(new int[]{10, 50, 150, 450, 625}, 750)));
        streets.put(12, new CompagnieElectriciteStreet());
        streets.put(13, new BuyableStreet("Avenue de Neuilly", 140, new Loyer(new int[]{10, 50, 150, 450, 625}, 750)));
        streets.put(14, new BuyableStreet("Rue de Paradis", 160, new Loyer(new int[]{12, 60, 180, 500, 700}, 900)));
        streets.put(15, new GareBuyableStreet("Gare de Lyon"));
        streets.put(16, new BuyableStreet("Avenue Mozart", 180, new Loyer(new int[]{14, 70, 200, 550, 750}, 950)));
        streets.put(17, new CaisseDeCommunauteStreet());
        streets.put(18, new BuyableStreet("Boulevard Saint-Michel", 180, new Loyer(new int[]{14, 70, 200, 550, 750}, 950)));
        streets.put(19, new BuyableStreet("Place Pigalle", 200, new Loyer(new int[]{16, 80, 220, 600, 800}, 1000)));
        streets.put(20, new ParcGratuitStreet());
        streets.put(21, new BuyableStreet("Avenue Matignon", 220, new Loyer(new int[]{18, 90, 250, 700, 875}, 1050)));
        streets.put(22, new ChanceStreet());
        streets.put(23, new BuyableStreet("Boulevard Malesherbes", 220, new Loyer(new int[]{18, 90, 250, 700, 875}, 1050)));
        streets.put(24, new BuyableStreet("Avenue Henry-Martin", 240, new Loyer(new int[]{20, 100, 300, 750, 925}, 1100)));
        streets.put(25, new GareBuyableStreet("Gare du Nord"));
        streets.put(26, new BuyableStreet("Faubourg Saint-Honoré", 260, new Loyer(new int[]{22, 110, 330, 800, 975}, 1150)));
        streets.put(27, new BuyableStreet("Blace De La Bourse", 260, new Loyer(new int[]{22, 110, 330, 800, 975}, 1150)));
        streets.put(28, new CompagnieEauStreet());
        streets.put(29, new BuyableStreet("Rue de la Fayette", 280, new Loyer(new int[]{24, 120, 360, 850, 1025}, 1200)));
        streets.put(30, new AllezEnPrisonStreet());
        streets.put(31, new BuyableStreet("Avenue de Breteuil", 300, new Loyer(new int[]{26, 130, 390, 900, 1100}, 1275)));
        streets.put(32, new BuyableStreet("Avenue Foch", 300, new Loyer(new int[]{26, 130, 390, 900, 1100}, 1275)));
        streets.put(33, new CaisseDeCommunauteStreet());
        streets.put(34, new BuyableStreet("Boulevard des Capucines", 320, new Loyer(new int[]{28, 150, 450, 1000, 1200}, 1400)));
        streets.put(35, new GareBuyableStreet("Gare Saint-Lazare"));
        streets.put(36, new ChanceStreet());
        streets.put(37, new BuyableStreet("Avenue des Champs-Élysées", 350, new Loyer(new int[]{35, 175, 500, 1100, 1300}, 1500)));
        streets.put(38, new TaxeDeLuxeStreet());
        streets.put(39, new BuyableStreet("Rue de la Paix",400, new Loyer(new int[]{50, 200, 600, 1400, 1700}, 2000)));
    }

    public static Street getStreet(int position) {
        return streets.get(position % streets.size());
    }

    public static int getPosition(String nom) {
        for (int i = 0; i < streets.size(); i++) {
            if (Objects.equals(Objects.requireNonNull(streets.get(i)).getName(), nom)) return i;
        }
        return -1;
    }

    public static int getPosition(Street s) {
        for (int i = 0; i < streets.size(); i++) {
            if (streets.get(i) == s) return i;
        }
        return -1;
    }

    public static Card tireCarteCaisseDeCommunaute() {
        Card c = piocheCartesDeCommunaute.remove(0);
        defausseCartesDeCommunaute.add(c);
        // remplis la pioche si elle est vide
        if (piocheCartesDeCommunaute.size() == 0) {
            addFromDefausse(piocheCartesDeCommunaute, defausseCartesDeCommunaute);
        }
        return c;
    }

    private static void addFromDefausse(ArrayList<Card> pioche, ArrayList<Card> defausse) {
        while (defausse.size() > 0) {
            pioche.add(defausse.remove(random.nextInt(defausse.size())));
        }
    }

    public static Card tireCarteChance() {
        Card c = piocheCartesChance.remove(0);
        defausseCartesChance.add(c);
        // remplis la pioche si elle est vide
        if (piocheCartesChance.size() == 0) {
            addFromDefausse(piocheCartesChance, defausseCartesChance);
        }
        return c;
    }

    public static int getNbStreets() {
        return nbStreets;
    }
}
