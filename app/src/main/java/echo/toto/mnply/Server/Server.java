package echo.toto.mnply.Server;

import android.util.Log;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.UUID;

import echo.toto.mnply.Events.Data;
import echo.toto.mnply.Game.Server.ServerGame;

public class Server {
    private static Server server = null;
    private final ServerSocket socket;
    private final int port;
    private final ServerGame game;

    private Server(int port) throws IOException {
        this.port = port;
        socket = new ServerSocket(port);
        initSocket();
        game = new ServerGame();
    }

    private void initSocket() {
        socket.on("logger", (client, data) -> Log.i("Server", "Message received: " + Arrays.toString(data.getArgs())));

        socket.on("pseudo", (client, data) -> {
            client.getPlayer().setName((String) data.getArgs()[0]);
            client.getPlayer().setId((UUID) data.getArgs()[1]);
            socket.emit(new Data("players", socket.getPublicClients()), socket.getClients());
        });

        socket.on("startGame", (client, data) -> {
            System.out.println("Start game received");
            socket.emit(new Data("message", "startGame"), socket.getClients());
        });
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
