package echo.toto.mnply.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import echo.toto.mnply.Client.Socket;
import echo.toto.mnply.Events.Callback;
import echo.toto.mnply.Events.Data;
import echo.toto.mnply.Game.Game;
import echo.toto.mnply.Game.Player;
import echo.toto.mnply.Model.GameState;
import echo.toto.mnply.Model.Model;
import echo.toto.mnply.R;
import echo.toto.mnply.Server.ClientSocket;
import echo.toto.mnply.Utils.AdresseIpEtPort;

public class MonopolyActivity extends AppCompatActivity {

    private Game game;
    private Socket socket;

    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monopoly);
        Intent intent = getIntent();

        String ip = intent.getStringExtra(MainActivity.EXTRA_IP);
        int port = intent.getIntExtra(MainActivity.EXTRA_PORT, 0);
        String pseudo = intent.getStringExtra(MainActivity.EXTRA_PSEUDO);
        boolean isHost = intent.getBooleanExtra(MainActivity.EXTRA_HOST, false);
        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (socket == null) {
            finish();
            return;
        }
        Model.init(new Random());

        TextView affichageCode = findViewById(R.id.affichage_code);
        TextView buttonStartGame = findViewById(R.id.start_game);
        View popupFragment = findViewById(R.id.popup);
        popupFragment.setVisibility(View.INVISIBLE);
        TextView pseudoView = findViewById(R.id.affiche_pseudo);
        pseudoView.setText(pseudo);

        Button lancerLeDe = findViewById(R.id.lance_le_de);
        lancerLeDe.setOnClickListener((view) -> game.jouer());

        if (isHost) {
            buttonStartGame.setVisibility(View.VISIBLE);
            Socket finalSocket = socket;
            buttonStartGame.setOnClickListener((view) -> finalSocket.emit(new Data("startGame")));
        }
        affichageCode.setText(AdresseIpEtPort.translateToCode(ip, port));
        game = new Game(this, socket);
        Player player = new Player(UUID.randomUUID(), pseudo);
        game.setLocalPlayer(player);
        Log.i("Player", player.toString());
        socket.setPlayer(player);
        socket.emit(new Data("pseudo", pseudo, player.getId()));
        socket.on("logger", (__, data) -> Log.i("Client", "(" + data.getEvent() + ") | " + Arrays.toString(data.getArgs())));
        socket.on("message", (client, args) -> System.out.println(Arrays.toString(args.getArgs())));
        socket.on("startGame", (client, args) -> {
            game.setState(GameState.STARTED);
            affichePopup(new Popup("Le jeu commence !", "C'est parti !", "", () -> {}, () -> {}));
            runOnUiThread(() -> buttonStartGame.setVisibility(View.INVISIBLE));
        });

        SensorManager shakeDetector = (SensorManager) game.getActivity().getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(shakeDetector).registerListener(shakeCallback, shakeDetector.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    public void affichePopup(Popup options) {
        runOnUiThread(() -> options.affiche(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.close();
    }

    public void updatePlayers(ArrayList<Player> players) {
        runOnUiThread(() -> {
            TextView affichageJoueurs = findViewById(R.id.affichage_joueurs);
            affichageJoueurs.setText("");
            StringBuilder text = new StringBuilder();
            // add every name of players, join with a comma, without one at the end
            for (int i = 0; i < players.size() - 1; i++) {
                text.append(players.get(i).getName()).append(", ");
            }
            // add the last name of players, with a dot at the end
            text.append(players.get(players.size() - 1).getName()).append(".");
            affichageJoueurs.setText(text.toString());
        });
    }

    private final SensorEventListener shakeCallback = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (findViewById(R.id.lance_le_de).getVisibility() != View.VISIBLE) return;
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;
            if (mAccel > 10) {
                game.jouer();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {}
    };

    public void close(String erreur) {
        runOnUiThread(() -> affichePopup(new Popup("Erreur : " + erreur, "Quitter", "", this::finish, () -> {})));
    }
}