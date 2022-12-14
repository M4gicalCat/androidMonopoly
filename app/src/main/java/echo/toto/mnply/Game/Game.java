package echo.toto.mnply.Game;

import java.util.ArrayList;
import java.util.List;

import echo.toto.mnply.Client.Socket;
import echo.toto.mnply.Events.Data;
import echo.toto.mnply.Model.GameState;
import echo.toto.mnply.UI.MonopolyActivity;

public class Game {
    private final MonopolyActivity activity;
    private final ArrayList<Player> players;
    private GameState state;
    private final Socket socket;

    public Game(MonopolyActivity activity, Socket socket) {
        this.activity = activity;
        players = new ArrayList<>();
        state = GameState.WAITING;
        this.socket = socket;
        initSocket();
    }

    private void initSocket() {
        socket.on("players", (__, data) -> {
            List<PublicPlayer> publicPlayers = (List<PublicPlayer>) data.getArgs()[0];
            ArrayList<Player> p = new ArrayList<>();
            for (PublicPlayer publicPlayer : publicPlayers) {
                Player player = new Player(publicPlayer.id, publicPlayer.name);
                player.setArgent(publicPlayer.argent);
                player.setPosition(publicPlayer.position);
                player.setJetonsSortiePrisons(publicPlayer.jetonsSortiePrisons);
                player.setPrison(publicPlayer.prison);
                p.add(player);
            }
            setPlayers(p);
        });
    }

    public void addPlayer(Player player) {
        if (players.contains(player))return;
        players.add(player);
        player.setGame(this);
        activity.updatePlayers(players);
    }

    public void setPlayers(List<Player> list) {
        players.clear();
        players.addAll(list);
        activity.updatePlayers(players);
    }

    public MonopolyActivity getActivity() {
        return activity;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public Socket getSocket() {
        return socket;
    }
}
