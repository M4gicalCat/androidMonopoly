package echo.toto.mnply.Client;

import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import echo.toto.mnply.Events.Callback;
import echo.toto.mnply.Events.Data;
import echo.toto.mnply.Game.Game;
import echo.toto.mnply.Game.Player;
import echo.toto.mnply.UI.Popup;

public class Socket {
    private java.net.Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Map<String, Callback> callbacks;
    private Player player;

    public Socket(String ip, int port) throws java.io.IOException {
        Socket that = this;
        Thread t = new Thread(() -> {
            try {
                that.socket = new java.net.Socket(ip, port);
                that.callbacks = new HashMap<>();
                that.outputStream = new ObjectOutputStream(socket.getOutputStream());
                that.inputStream = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        acceptMessages();
    }

    public void on(String event, Callback callback) {
        callbacks.put(event, callback);
    }

    public void emit(Data message) {
        Log.i("Client", "Emitting (" + message + ")");
        new Thread(() -> {
            try {
                outputStream.writeObject(message);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void acceptMessages() {
        (new Thread(() -> {
            while (!socket.isClosed()) {
                Data message = receive();
                if (message == null) {
                    continue;
                }
                Callback logger = callbacks.get("logger");
                if (logger != null) logger.call(null, message);
                Callback callback = callbacks.get(message.getEvent());
                if (callback != null) callback.call(null, message);
            }
            getPlayer().getGame().getActivity().runOnUiThread(() -> getPlayer().getGame().getActivity().affichePopup(new Popup("Connection lost", "Exit", "", () -> getPlayer().getGame().getActivity().finish(), () -> {})));
        })).start();
    }

    public Data receive() {
        try {
            return (Data) inputStream.readObject();
        } catch (EOFException exception) {
            try {
                inputStream.close();
                inputStream = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (getPlayer().getGame() == null) getPlayer().setGame(Game.game);
            getPlayer().getGame().getActivity().runOnUiThread(() -> getPlayer().getGame().getActivity().affichePopup(new Popup("Connection lost", "Exit", "", () -> getPlayer().getGame().getActivity().finish(), () -> {})));
        }
        return null;
    }

    public void disconnect() throws java.io.IOException {
        socket.close();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
