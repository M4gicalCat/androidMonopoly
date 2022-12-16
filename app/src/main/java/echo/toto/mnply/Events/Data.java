package echo.toto.mnply.Events;

import java.io.Serializable;
import java.util.Arrays;

public class Data implements Serializable {
    private final String event;
    private final Object[] args;

    public Data(String event, Object... args) {
        this.event = event;
        this.args = args;
    }

    public String getEvent() {
        return event;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return "Data{" +
                "event='" + event + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
