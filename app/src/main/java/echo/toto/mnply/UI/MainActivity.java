package echo.toto.mnply.UI;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import echo.toto.mnply.R;
import echo.toto.mnply.Server.Server;
import echo.toto.mnply.Utils.AdresseIpEtPort;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_IP = "echo.toto.mnply.ui.IP";
    public static final String EXTRA_PORT = "echo.toto.mnply.ui.PORT";
    public static final String EXTRA_PSEUDO = "echo.toto.mnply.ui.PSEUDO";
    public static final String EXTRA_HOST = "echo.toto.mnply.ui.HOST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int[] ids = {R.id.code_input, R.id.pseudo_input};
            for (int id : ids) {
                findViewById(id).setOnFocusChangeListener((v, hasFocus) -> {
                    if (!hasFocus) {
                        hideKeyboard(v);
                    }
                });
            }
        }

        View joinGame = findViewById(R.id.button_join_game);
        joinGame.setOnClickListener((v) -> {
            String pseudo = ((EditText) findViewById(R.id.pseudo_input)).getText().toString();
            if (pseudo.length() == 0) {
                Toast.makeText(this, "Veuillez entrer un pseudo", Toast.LENGTH_SHORT).show();
                return;
            }
            String code = ((EditText) findViewById(R.id.code_input)).getText().toString();
            Pattern pattern = Pattern.compile("^[a-pA-P]{8}[0-9]{4,}$");
            if (!pattern.matcher(code).matches()) {
                Toast.makeText(getApplicationContext(), "Le code n'est pas valide", Toast.LENGTH_LONG).show();
                return;
            }

            String ip = AdresseIpEtPort.getIp(code);
            int port = AdresseIpEtPort.getPort(code);

            startGame(ip, port, pseudo, false);
        });

        View createServer = findViewById(R.id.button_create_game);
        createServer.setOnClickListener(v -> {
            try {
                Server server = Server.create(1024);
                String pseudo = ((EditText) findViewById(R.id.pseudo_input)).getText().toString();
                if (pseudo.length() == 0) {
                    Toast.makeText(this, "Veuillez entrer un pseudo", Toast.LENGTH_SHORT).show();
                    return;
                }
                // start the game
                startGame(Server.getIpAddress(), server.getPort(), pseudo, true);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("ERROR", "ERROR WHILE STARTING THE GAME :" + e.getMessage());
                Server.close();
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void startGame(String ip, int port, String pseudo, boolean isHost) {
        Intent intent = new Intent(this, MonopolyActivity.class);
        intent.putExtra(EXTRA_IP, ip);
        intent.putExtra(EXTRA_PORT, port);
        intent.putExtra(EXTRA_PSEUDO, pseudo);
        intent.putExtra(EXTRA_HOST, isHost);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Server.close();
    }
}