package echo.toto.mnply.Server;

import android.util.Log;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import echo.toto.mnply.Events.Data;
import echo.toto.mnply.Game.Player;
import echo.toto.mnply.Game.Server.ServerGame;
import echo.toto.mnply.Model.GameState;
import echo.toto.mnply.Model.Model;
import echo.toto.mnply.Model.Street.BuyableStreet;

public class Server {
    private static Server server = null;
    private final ServerSocket socket;
    private final int port;
    private final ServerGame game;

    private Server(int port) throws IOException {
        this.port = port;
        socket = new ServerSocket(port);
        initSocket();
        game = new ServerGame(this);
    }

    private void initSocket() {
        socket.on("logger", (client, data) -> Log.i("Server", "Message received: (" + data.getEvent() + ") | " + Arrays.toString(data.getArgs())));

        socket.on("pseudo", (client, data) -> {
            client.getPlayer().setName((String) data.getArgs()[0]);
            client.getPlayer().setId((UUID) data.getArgs()[1]);
            game.addPlayer(client.getPlayer());
            socket.emit(new Data("players", socket.getPublicClients()), socket.getClients());
        });

        socket.on("startGame", (client, data) -> {
            socket.emit(new Data("startGame"), socket.getClients());
            game.setState(GameState.STARTED);
        });

        socket.on("jouer", (client, data) -> {
            if (!game.checkTurn(client.getPlayer())) return;
            int[] des = new int[2];
            for (int i = 0; i < 2; i++) {
                des[i] = (int) Math.ceil(Math.random() * 5);
            }
            socket.emit(new Data("dés", client.getPlayer().getId(), (Object) des), socket.getClients());
        });

        socket.on("endTurn", ((client, args) -> {
            if (!game.checkTurn(client.getPlayer())) return;
            game.nextPlayer();
        }));

        socket.on("move", (client, data) -> {
            if (!game.checkTurn(client.getPlayer())) return;
            socket.emit(new Data("move", data.getArgs()[0], data.getArgs()[1]), socket.getClients());
        });

        socket.on("paye", (client, data) -> {
            int argent = (int) data.getArgs()[2];
            client.getPlayer().updateMoney(-argent);
            socket.emit(new Data("paye", data.getArgs()[0], data.getArgs()[1], data.getArgs()[2]), socket.getClients());
            if (client.getPlayer().getArgent() <= 0) {
                socket.emit(new Data("mort", client.getPlayer().getId()), socket.getClients());
                socket.removeClient(client);
                if (game.getPlayers().size() <= 1) {
                    socket.emit(new Data("endGame", game.getPlayers().get(0).getId()), socket.getClients());
                }
            }
        });

        socket.on("buyStreet", (client, data) -> {
            if (!game.checkTurn(client.getPlayer())) return;
            int position = (int) data.getArgs()[0];
            client.getPlayer().updateMoney(-((BuyableStreet) Model.getStreet(position)).getPrice());
            if (client.getPlayer().getArgent() <= 0) {
                socket.emit(new Data("mort", client.getPlayer().getId()), socket.getClients());
                socket.removeClient(client);
            }
            socket.emit(new Data("buyStreet", client.getPlayer().getId(), position), socket.getClients());
        });
    }

    public void emit(Data data, List<Player> players) {
        ArrayList<ClientSocket> to = new ArrayList<>();
        for (Player p : players) {
            ClientSocket client = null;
            for (ClientSocket c : socket.clients) {
                if (c.getPlayer() == p || c.getPlayer().getId().equals(p.getId())) {
                    client = c;
                    break;
                }
            }
            if (client == null) continue;
            to.add(client);
        }
        socket.emit(data, to);
    }

    public static Server create(int port) throws IOException {
        if (server != null) {
            close();
        }
        server = new Server(port);
        return server;
    }

    public static void close() {
        for (ClientSocket client : server.socket.getClients()) {
            client.close();
        }
        if (server.socket != null)
            server.socket.close();
        server = null;
    }

    public int getPort() {
        return port;
    }

    public static String getIpAddress() throws Exception {
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface intf = en.nextElement();
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                InetAddress inetAddress = enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                    return inetAddress.getHostAddress();
                }
            }
        }
        throw new Exception("Impossible de trouver l'adresse IP");
    }
}
