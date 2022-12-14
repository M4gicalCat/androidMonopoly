package echo.toto.mnply.UI;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import echo.toto.mnply.R;

public class Popup {
    private final String title;
    private final String buttonTrue;
    private final String buttonFalse;
    private final Runnable onClickButtonTrue;
    private final Runnable onClickButtonFalse;
    public Popup(String title, String buttonTrue, String buttonFalse, Runnable onClickButtonTrue, Runnable onClickButtonFalse) {
        this.title = title;
        this.buttonTrue = buttonTrue;
        this.buttonFalse = buttonFalse;
        this.onClickButtonTrue = onClickButtonTrue;
        this.onClickButtonFalse = onClickButtonFalse;
    }

    public void affiche(MonopolyActivity activity) {
        TextView title = activity.findViewById(R.id.popup_title);
        Button buttonTrue = activity.findViewById(R.id.popup_button_true);
        Button buttonFalse = activity.findViewById(R.id.popup_button_false);

        title.setVisibility(this.title.length() > 0 ? View.VISIBLE : View.INVISIBLE);
        buttonTrue.setVisibility(this.buttonTrue.length() > 0 ? View.VISIBLE : View.INVISIBLE);
        buttonFalse.setVisibility(this.buttonFalse.length() > 0 ? View.VISIBLE : View.INVISIBLE);

        title.setText(this.title);
        buttonTrue.setText(this.buttonTrue);
        buttonFalse.setText(this.buttonFalse);
        buttonTrue.setOnClickListener((v) -> {
            onClickButtonTrue.run();
            activity.findViewById(R.id.popup).setVisibility(View.INVISIBLE);
        });
        buttonFalse.setOnClickListener((v) -> {
            onClickButtonFalse.run();
            activity.findViewById(R.id.popup).setVisibility(View.INVISIBLE);
        });
        activity.findViewById(R.id.popup).setVisibility(View.VISIBLE);
    }
}
