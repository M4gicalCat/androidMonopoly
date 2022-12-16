package echo.toto.mnply.Game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import echo.toto.mnply.Model.Model;
import echo.toto.mnply.Model.Street.Street;

public class PublicPlayer implements Serializable {
    public final UUID id;
    public final String name;
    public final int argent;
    public final int position;
    public final int jetonsSortiePrisons;
    public final int prison;
    public final ArrayList<Integer> streets;

    public PublicPlayer(Player player) {
        this.name = player.getName();
        this.id = player.getId();
        this.argent = player.getArgent();
        this.position = player.getPosition();
        this.jetonsSortiePrisons = player.getJetonsSortiePrisons();
        this.prison = player.getPrison();
        this.streets = new ArrayList<>();
        for (Street s : player.getStreets()) {
            this.streets.add(Model.getPosition(s));
        }
    }
}
