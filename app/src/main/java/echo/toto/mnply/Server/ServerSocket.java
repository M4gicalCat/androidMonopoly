package echo.toto.mnply.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import echo.toto.mnply.Events.Callback;
import echo.toto.mnply.Events.Data;

public class ServerSocket {
    java.net.ServerSocket socket;
    ArrayList<ClientSocket> clients;
    private final Map<String, Callback> callbacks;

    public ServerSocket(int port) throws java.io.IOException {
        callbacks = new HashMap<>();
        socket = new java.net.ServerSocket(port);
        clients = new ArrayList<>();
        acceptConnections();
    }

    private void acceptConnections() {
        new Thread(() -> {
            while (!socket.isClosed()) {
                try {
                    clients.add(new ClientSocket(socket.accept(), this));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void emit(Data data, List<ClientSocket> clients) {
        for (ClientSocket client : clients) {
            client.emit(data);
        }
    }

    public void receive(Data data, ClientSocket client) {
        Callback callback = callbacks.get("logger");
        if (callback != null) callback.call(client, data);
        Callback cb = callbacks.get(data.getEvent());
        if (cb != null) cb.call(client, data);
    }

    public void on(String event, Callback callback) {
        callbacks.put(event, callback);
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ClientSocket> getClients() {
        return clients;
    }

    public List<Object> getPublicClients() {
        ArrayList<Object> publicClients = new ArrayList<>();
        for (ClientSocket client : clients) {
            publicClients.add(client.getPublic());
        }
        return publicClients;
    }
}
