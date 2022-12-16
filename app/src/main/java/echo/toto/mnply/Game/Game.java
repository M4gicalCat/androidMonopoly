package echo.toto.mnply.Game;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import echo.toto.mnply.Client.Socket;
import echo.toto.mnply.Events.Data;
import echo.toto.mnply.Model.GameState;
import echo.toto.mnply.Model.Model;
import echo.toto.mnply.R;
import echo.toto.mnply.UI.MonopolyActivity;
import echo.toto.mnply.UI.Popup;

public class Game {
    private final MonopolyActivity activity;
    private final ArrayList<Player> players;
    private GameState state;
    private final Socket socket;
    private Player localPlayer;

    public Game(MonopolyActivity activity, Socket socket) {
        this.activity = activity;
        players = new ArrayList<>();
        state = GameState.WAITING;
        this.socket = socket;
        localPlayer = null;
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
                player.setGame(this);
                for (int s : publicPlayer.streets) {
                    player.addStreet(Model.getStreet(s));
                }
                p.add(player);
            }
            Player me = null;
            for (Player player : players) {
                if (player.getId() == null || player.getName() == null) {
                    socket.emit(new Data("pseudo", player.getName(), player.getId()));
                    me = player;
                    break;
                }
                if (player.getId().equals(socket.getPlayer().getId())) {
                    me = player;
                    break;
                }
            }
            if (me != null) {
                me.setLocal(true);
                localPlayer = me;
            }
            setPlayers(p);
        });

        socket.on("yourTurn", (__, ___) -> activity.runOnUiThread(() -> {
            activity.affichePopup(new Popup(
                    "À votre tour de jouer",
                    "OK",
                    "",
                    () -> {},
                    () -> {}
            ));
            activity.findViewById(R.id.lance_le_de).setVisibility(android.view.View.VISIBLE);
        }));

        socket.on("dés", (__, data) -> {
            int[] dices = (int[]) data.getArgs()[1];
            activity.runOnUiThread(() -> {
                activity.findViewById(R.id.lance_le_de).setVisibility(android.view.View.GONE);
                activity.findViewById(R.id.d1).setVisibility(android.view.View.VISIBLE);
                activity.findViewById(R.id.d2).setVisibility(android.view.View.VISIBLE);
                activity.findViewById(R.id.d1).setBackgroundResource(dices[0] == 1 ? R.drawable.dice_1 : dices[0] == 2 ? R.drawable.dice_2 : dices[0] == 3 ? R.drawable.dice_3 : dices[0] == 4 ? R.drawable.dice_4 : dices[0] == 5 ? R.drawable.dice_5 : R.drawable.dice_6);
                activity.findViewById(R.id.d2).setBackgroundResource(dices[1] == 1 ? R.drawable.dice_1 : dices[1] == 2 ? R.drawable.dice_2 : dices[1] == 3 ? R.drawable.dice_3 : dices[1] == 4 ? R.drawable.dice_4 : dices[1] == 5 ? R.drawable.dice_5 : R.drawable.dice_6);
            });
            Button lancerLeDe = activity.findViewById(R.id.lance_le_de);
            if (lancerLeDe.getVisibility() != View.GONE) return;
            activity.runOnUiThread(() -> lancerLeDe.setVisibility(View.INVISIBLE));
            if (localPlayer == null) return;
            localPlayer.move(dices);
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

    public void jouer() {
        socket.emit(new Data("jouer"));
        activity.runOnUiThread(() -> activity.findViewById(R.id.lance_le_de).setVisibility(android.view.View.GONE));
    }

    public void emit(Data data) {
        socket.emit(data);
    }

    public void setLocalPlayer(Player localPlayer) {
        this.localPlayer = localPlayer;
    }
}
