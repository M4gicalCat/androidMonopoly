package echo.toto.mnply.Server;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

import echo.toto.mnply.Events.Data;
import echo.toto.mnply.Game.Player;
import echo.toto.mnply.Game.PublicPlayer;

public class ClientSocket {
    private final Socket socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
    private final ServerSocket serverSocket;
    private final Player player;

    public ClientSocket(java.net.Socket socket, ServerSocket serverSocket) throws java.io.IOException {
        this.socket = socket;
        this.serverSocket = serverSocket;
        inputStream = new ObjectInputStream(socket.getInputStream());
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        acceptMessages();
        player = new Player();
    }

    public void emit(Data data) {
        try {
            outputStream.writeObject(data);
            outputStream.flush();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public Data receive() {
        try {
            return (Data) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            serverSocket.removeClient(this);
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Log.e("Server", "Client disconnected");
        }
        return null;
    }

    private void acceptMessages() {
        (new Thread(() -> {
            while (!socket.isClosed()) {
                Data data = receive();
                if (data == null) continue;
                serverSocket.receive(data, this);
            }
            Log.d("ClientSocket", "Socket closed");
        })).start();
    }

    public void close() {
        try {
            socket.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public Object getPublic() {
        return new PublicPlayer(player);
    }

    public Player getPlayer() {
        return player;
    }
}
