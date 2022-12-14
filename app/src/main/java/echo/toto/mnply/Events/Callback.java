package echo.toto.mnply.Events;

import echo.toto.mnply.Server.ClientSocket;

public interface Callback {
    void call(ClientSocket client, Data args);
}
