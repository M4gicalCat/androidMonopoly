package echo.toto.mnply.Game;

import java.util.ArrayList;
import java.util.UUID;

import echo.toto.mnply.Client.Socket;
import echo.toto.mnply.Model.Model;
import echo.toto.mnply.Model.Street.BuyableStreet;
import echo.toto.mnply.Model.Street.PrisonStreet;

public class Player {
    private String name;
    private UUID id;
    private Game game;
    private final ArrayList<BuyableStreet> streets;
    private int argent;
    private int position;
    private int jetonsSortiePrisons;
    private int prison;
    private boolean isLocal;

    public Player(UUID id, String name) {
        this.id = id;
        this.name = name;
        streets = new ArrayList<>();
        argent = 1500;
        jetonsSortiePrisons = 0;
        prison = 0;
    }

    public Player() {
        this(null, null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void updateMoney(int montant) {
        argent += montant;
    }

    public void endTurn() {
        // todo
    }

    public UUID getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public ArrayList<BuyableStreet> getStreets() {
        return streets;
    }

    public int getArgent() {
        return argent;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getJetonsSortiePrisons() {
        return jetonsSortiePrisons;
    }

    public void addJetonSortiePrison() {
        this.jetonsSortiePrisons ++;
    }

    public int getPrison() {
        return prison;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public void vaEnPrison() {
        setPosition(Model.getPosition("Prison"));
        prison = PrisonStreet.NB_TOURS_PRISON;
    }

    public void reducePrison() {
        prison = Math.max(prison - 1, 0);
    }

    public void buyStreet(BuyableStreet buyableStreet) {
        if (argent < buyableStreet.getPrice()) return;
        argent -= buyableStreet.getPrice();
        streets.add(buyableStreet);
        buyableStreet.setOwner(this);
    }

    public void paye(Player owner, int loyerAPayer) {
        // todo
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setPrison(int prison) {
        this.prison = prison;
    }

    public void setJetonsSortiePrisons(int jetonsSortiePrisons) {
        this.jetonsSortiePrisons = jetonsSortiePrisons;
    }

    public void setArgent(int argent) {
        this.argent = argent;
    }
}
