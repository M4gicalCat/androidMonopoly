package echo.toto.mnply.Game.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import echo.toto.mnply.Game.Player;
import echo.toto.mnply.Model.GameState;

public class ServerGame {
    private final List<Player> players;
    private GameState state;

    public ServerGame() {
        this.players = new ArrayList<>();
        state = GameState.WAITING;
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
        this.state = state;
    }
}
