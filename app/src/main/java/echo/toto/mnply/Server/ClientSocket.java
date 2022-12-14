package echo.toto.mnply.Server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public Data receive() {
        try {
            return (Data) inputStream.readObject();
        } catch (java.io.IOException | ClassNotFoundException e) {
            e.printStackTrace();
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
