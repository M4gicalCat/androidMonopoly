package echo.toto.mnply.Game;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.UUID;

import echo.toto.mnply.Events.Data;
import echo.toto.mnply.Model.GameState;
import echo.toto.mnply.Model.Model;
import echo.toto.mnply.Model.Street.BuyableStreet;
import echo.toto.mnply.Model.Street.PrisonStreet;
import echo.toto.mnply.Model.Street.Street;
import echo.toto.mnply.R;

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
        String dollars = argent + "$";
        if (game == null) setGame(Game.game);
        game.getActivity().runOnUiThread(() -> ((TextView) game.getActivity().findViewById(R.id.affiche_argent)).setText(dollars));
    }

    public void endTurn() {
        Log.i("Player", "end of " + name + "'s turn");
        game.emit(new Data("endTurn", id));
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
        setPosition(position, true);
    }

    public void setPosition(int position, boolean withAction) {
        this.position = position;
        Game.game.getActivity().runOnUiThread(() -> {
            ((TextView) Game.game.getActivity().findViewById(R.id.affichage_position)).setText(Model.getStreet(position).getName());
        });
        if (Game.game.getState() != GameState.STARTED || !withAction) return;
        Model.getStreet(position).action(this, new int[]{0, 1});
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
        updateMoney(-buyableStreet.getPrice());
        streets.add(buyableStreet);
        buyableStreet.setOwner(this);
        if (game == null) setGame(Game.game);
        game.emit(new Data("buyStreet", Model.getPosition(buyableStreet)));
    }

    public void paye(Player owner, int loyerAPayer) {
        owner.updateMoney(loyerAPayer);
        updateMoney(-loyerAPayer);
        if (game == null) setGame(Game.game);
        game.emit(new Data("paye", id, owner.getId(), loyerAPayer));
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setGame(Game game) {
        this.game = game;
        Log.println(Log.WARN, "Player", "setGame: " + game);
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

    @NonNull
    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", game=" + game +
                ", streets=" + streets +
                ", argent=" + argent +
                ", position=" + position +
                ", jetonsSortiePrisons=" + jetonsSortiePrisons +
                ", prison=" + prison +
                ", isLocal=" + isLocal +
                '}';
    }

    public void addStreet(Street street) {
        streets.add((BuyableStreet) street);
        ((BuyableStreet) street).setOwner(this);
    }

    public void move(int[] dices) {
        int newPosition = (position + dices[0] + dices[1]);
        while (newPosition > Model.getNbStreets()) {
            newPosition -= Model.getNbStreets();
            updateMoney(200);
        }
        setPosition(newPosition);
        if (game == null) setGame(Game.game);
        game.emit(new Data("move", id, newPosition));
        Model.getStreet(newPosition).action(this, dices);
    }
}
