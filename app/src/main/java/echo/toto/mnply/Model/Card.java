package echo.toto.mnply.Model;

import echo.toto.mnply.Game.Player;

public class Card {
    private final String texte;
    public final CardAction action;

    public Card(String texte, CardAction action) {
        this.texte = texte;
        this.action = action;
    }

    public String getTexte() {
        return texte;
    }

    public void action(Player p) {
        action.run(p);
    }
}
