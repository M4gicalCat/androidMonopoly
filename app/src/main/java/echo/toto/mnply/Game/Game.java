package echo.toto.mnply.Game;

import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import echo.toto.mnply.Client.Socket;
import echo.toto.mnply.Events.Data;
import echo.toto.mnply.Model.GameState;
import echo.toto.mnply.Model.Model;
import echo.toto.mnply.Model.Street.BuyableStreet;
import echo.toto.mnply.R;
import echo.toto.mnply.Server.Server;
import echo.toto.mnply.UI.MonopolyActivity;
import echo.toto.mnply.UI.Popup;

public class Game {
    private final MonopolyActivity activity;
    private final ArrayList<Player> players;
    private GameState state;
    private final Socket socket;
    private Player localPlayer;
    public static Game game;

    public Game(MonopolyActivity activity, Socket socket) {
        this.activity = activity;
        players = new ArrayList<>();
        state = GameState.WAITING;
        this.socket = socket;
        localPlayer = null;
        initSocket();
        game = this;
    }

    private void initSocket() {
        socket.on("players", (__, data) -> {
            List<PublicPlayer> publicPlayers = (List<PublicPlayer>) data.getArgs()[0];
            ArrayList<Player> p = new ArrayList<>();
            for (PublicPlayer publicPlayer : publicPlayers) {
                Player player = new Player(publicPlayer.id, publicPlayer.name);
                player.setArgent(publicPlayer.argent);
                player.setPosition(publicPlayer.position, false);
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
            activity.findViewById(R.id.lance_le_de).setVisibility(View.VISIBLE);
        }));

        socket.on("dés", (__, data) -> {
            int[] dices = (int[]) data.getArgs()[1];
            activity.runOnUiThread(() -> {
                activity.findViewById(R.id.lance_le_de).setVisibility(View.GONE);
                activity.findViewById(R.id.dices).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.d1).setBackgroundResource(dices[0] == 1 ? R.drawable.dice_1 : dices[0] == 2 ? R.drawable.dice_2 : dices[0] == 3 ? R.drawable.dice_3 : dices[0] == 4 ? R.drawable.dice_4 : dices[0] == 5 ? R.drawable.dice_5 : R.drawable.dice_6);
                activity.findViewById(R.id.d2).setBackgroundResource(dices[1] == 1 ? R.drawable.dice_1 : dices[1] == 2 ? R.drawable.dice_2 : dices[1] == 3 ? R.drawable.dice_3 : dices[1] == 4 ? R.drawable.dice_4 : dices[1] == 5 ? R.drawable.dice_5 : R.drawable.dice_6);
            });
            Button lancerLeDe = activity.findViewById(R.id.lance_le_de);
            if (lancerLeDe.getVisibility() != View.GONE) return;
            activity.runOnUiThread(() -> lancerLeDe.setVisibility(View.INVISIBLE));
            if (localPlayer == null) return;
            localPlayer.move(dices);
        });

        socket.on("move", (__, data) -> {
            UUID id = (UUID) data.getArgs()[0];
            int position = (int) data.getArgs()[1];
            for (Player player : players) {
                if (player.getId().equals(id)) {
                    if (player.isLocal()) break;
                    player.setPosition(position, false);
                    break;
                }
            }
        });

        socket.on("paye", (__, data) -> {
            UUID idDonate = (UUID) data.getArgs()[0];
            UUID idReceive = (UUID) data.getArgs()[1];
            int amount = (int) data.getArgs()[2];
            Player donate = null, receive = null;
            for (Player player : players) {
                if (player.getId().equals(idDonate)) {
                    donate = player;
                }
                if (player.getId().equals(idReceive)) {
                    receive = player;
                }
            }
            if (donate == null || receive == null) return;
            donate.paye(receive, amount);
        });

        socket.on("mort", (__, data) -> {
            UUID id = (UUID) data.getArgs()[0];
            for (Player player : players) {
                if (player.getId().equals(id)) {
                    activity.runOnUiThread(() -> {
                        activity.affichePopup(new Popup(
                                player.getName() + " n'a plus d'argent. Honte à lui !",
                                "OK",
                                "",
                                () -> {},
                                () -> {}
                        ));
                    });
                    players.remove(player);
                    setPlayers(players);
                    break;
                }
            }
        });

        socket.on("buyStreet", (__, data) -> {
            UUID id = (UUID) data.getArgs()[0];
            int street = (int) data.getArgs()[1];
            for (Player player : players) {
                if (player.getId().equals(id)) {
                    if (player.isLocal()) return;
                    player.addStreet(Model.getStreet(street));
                    player.updateMoney(-((BuyableStreet) Model.getStreet(street)).getPrice());
                    break;
                }
            }
        });

        socket.on("endGame", (__, data) -> {
            UUID id = (UUID) data.getArgs()[0];
            for (Player player : players) {
                if (player.getId().equals(id)) {
                    activity.runOnUiThread(() -> {
                        activity.affichePopup(new Popup(
                                player.getName() + " a gagné la partie !",
                                "OK",
                                "",
                                () -> {
                                    socket.close();
                                    Game.game.getActivity().finish();
                                    Server.close();
                                },
                                () -> {}
                        ));
                    });
                    break;
                }
            }
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
        socket.emit(new Data("jouer", ""));
        activity.runOnUiThread(() -> activity.findViewById(R.id.lance_le_de).setVisibility(android.view.View.GONE));
    }

    public void emit(Data data) {
        socket.emit(data);
    }

    public void setLocalPlayer(Player localPlayer) {
        this.localPlayer = localPlayer;
    }
}
