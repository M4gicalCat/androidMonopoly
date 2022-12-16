package echo.toto.mnply.Game.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import echo.toto.mnply.Events.Data;
import echo.toto.mnply.Game.Player;
import echo.toto.mnply.Model.GameState;
import echo.toto.mnply.Server.Server;

public class ServerGame {
    private final List<Player> players;
    private GameState state;
    private int whoseTurn;
    private final Server server;

    public ServerGame(Server server) {
        this.players = new ArrayList<>();
        state = GameState.WAITING;
        whoseTurn = -1;
        this.server = server;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public Player getPlayer(UUID id) {
        for (Player player : players) {
            if (player.getId().equals(id)) return player;
        }
        return null;
    }

    public void setState(GameState state) {
        if (state == this.state) return;
        this.state = state;
        if (state == GameState.STARTED) {
            nextPlayer();
        }
    }

    public void nextPlayer() {
        whoseTurn = whoseTurn == -1 ? 0 : (whoseTurn + 1) % players.size();
        ArrayList<Player> list = new ArrayList<Player>() {{add(players.get(whoseTurn));}};
        server.emit(new Data("yourTurn", players.get(whoseTurn).getId()), list);
    }

    public boolean checkTurn(Player player) {
        if (state != GameState.STARTED) return false;
        return player.getId().equals(players.get(whoseTurn).getId());
    }

    public List<Player> getPlayers() {
        return players;
    }
}
